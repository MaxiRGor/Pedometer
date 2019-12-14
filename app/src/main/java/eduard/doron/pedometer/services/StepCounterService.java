package eduard.doron.pedometer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;

import eduard.doron.pedometer.R;
import eduard.doron.pedometer.ResetCounterScheduledTask;
import eduard.doron.pedometer.data.Preferences;
import eduard.doron.pedometer.interfaces.OnResetCounterListenerListener;
import eduard.doron.pedometer.interfaces.StepListener;
import eduard.doron.pedometer.models.AppDatabase;
import eduard.doron.pedometer.models.DayResult;
import eduard.doron.pedometer.sensor.StepDetector;

public class StepCounterService extends Service implements SensorEventListener, StepListener, OnResetCounterListenerListener {

    private static final int NOTIFY_ID = 42;
    private static String CHANNEL_ID = "Pedometer step channel";

    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;

    private StepDetector stepDetector;

    public MutableLiveData<Integer> stepCount;

    private IBinder binder;


    @Override
    public void onCreate() {
        super.onCreate();

        this.stepCount = new MutableLiveData<>(Preferences.getStepCount(StepCounterService.this));
        binder = new LocalBinder();
        setupNotification();
        setupStepDetector();
        setupScheduledTask();
    }

    private void setupNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        builder = new NotificationCompat.Builder(StepCounterService.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_step)
                .setContentTitle(getString(R.string.step_count))
                .setContentText(String.format(getString(R.string.current_steps), this.stepCount.getValue()))
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification;

        notification = builder.build();
        notificationManager = NotificationManagerCompat.from(StepCounterService.this);
        notificationManager.notify(NOTIFY_ID, notification);

        // needed to sdk26+, without notification service would stop
        startForeground(NOTIFY_ID, notification);
    }

    private void setupStepDetector() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer;
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        this.stepDetector = new StepDetector();
        this.stepDetector.registerListener(this);
    }

    private void setupScheduledTask() {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        // Set Execution time
        dueDate.set(Calendar.HOUR_OF_DAY, 23);
        dueDate.set(Calendar.MINUTE, 59);
        dueDate.set(Calendar.SECOND, 30);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long repeatTime = 1000 * 60 * 60 * 24;

        long initialDelay = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        Timer time = new Timer();
        ResetCounterScheduledTask resetCounterScheduledTask = new ResetCounterScheduledTask(StepCounterService.this);
        time.schedule(resetCounterScheduledTask, initialDelay, repeatTime);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccelerometer(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void updateNotification() {
        builder.setContentText(String.format(getString(R.string.current_steps), this.stepCount.getValue()));
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    @Override
    public void step() {
        stepCount.setValue(stepCount.getValue() + 1);
        updateNotification();
    }

    @Override
    public void onDestroy() {
        Preferences.setStepCount(StepCounterService.this, this.stepCount.getValue());
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Preferences.setStepCount(StepCounterService.this, this.stepCount.getValue());
        super.onTaskRemoved(rootIntent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void reset() {
        int currentStepValue = stepCount.getValue();
        this.stepCount.postValue(0);
        addDayResultToFirestore(currentStepValue);
    }

    private void addDayResultToFirestore(int steps) {
        long time = new Date().getTime();
        int length = Preferences.getStepLength(this);
        int limit = Preferences.getStepLimit(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null && isNetworkAvailable()) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(this.getString(R.string.firestore_field_user_uid), FirebaseAuth.getInstance().getCurrentUser().getUid());
            data.put(this.getString(R.string.firestore_field_time), time);
            data.put(this.getString(R.string.firestore_field_step_count), steps);
            data.put(this.getString(R.string.firestore_field_step_length), length);
            data.put(this.getString(R.string.firestore_field_step_limit), limit);

            FirebaseFirestore.getInstance()
                    .collection(this.getString(R.string.firestore_collection_user_results))
                    .document()
                    .set(data)
                    .addOnCompleteListener(task -> addDayResultToLocalDatabase(steps, time, length, limit, task.isSuccessful()));

        } else addDayResultToLocalDatabase(steps, time, length, limit, false);
    }

    private void addDayResultToLocalDatabase(int steps, long time, int length, int limit, boolean synced) {
        AppDatabase.getDatabase(this).getDayResultDao()
                .insertResult(new DayResult(time, length, limit, steps, synced));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                } else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        }
        return false;
    }

    public class LocalBinder extends Binder {
        public StepCounterService getService() {
            // Return this instance of StepCounter so clients can call public methods
            return StepCounterService.this;
        }
    }
}
