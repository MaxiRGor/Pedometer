package ed.doron.pedometer.services;

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
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;

import ed.doron.pedometer.CalculateDayResultsScheduledTask;
import ed.doron.pedometer.R;
import ed.doron.pedometer.data.Preferences;
import ed.doron.pedometer.interfaces.OnNewDayStartedListener;
import ed.doron.pedometer.interfaces.StepListener;
import ed.doron.pedometer.models.AppDatabase;
import ed.doron.pedometer.models.DayResult;
import ed.doron.pedometer.sensor.StepDetector;

public class StepCounterService extends Service implements SensorEventListener, StepListener, OnNewDayStartedListener {

    // Notifications
    private static final int NOTIFY_ID = 42;
    private static String CHANNEL_ID = "Pedometer step channel";

    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;

    private StepDetector stepDetector;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    public MutableLiveData<Integer> stepCount;

    private IBinder binder;

    // private PeriodicWorkRequest updater;


    @Override
    public void onCreate() {
        Log.d("myLogs", "service started");
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        binder = new LocalBinder();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer;
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        this.stepDetector = new StepDetector();
        this.stepDetector.registerListener(this);

        this.stepCount = new MutableLiveData<>(Preferences.getStepCount(StepCounterService.this));


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

        startForeground(NOTIFY_ID, notification);

        setScheduledTask();

    }

    private void setScheduledTask() {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        // Set Execution to 00:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 0);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        //in final product replace with 1 day (1000 * 60 * 60 * 24)
        long repeatTime = 1000 * 10;   //now == 10 seconds

        //in final product replace uncomment
        //long initDelay = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();
        long initDelay = repeatTime - 1;

        Timer time = new Timer();
        CalculateDayResultsScheduledTask st = new CalculateDayResultsScheduledTask(stepCount, StepCounterService.this);
        time.schedule(st, initDelay, repeatTime);
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
    public void step(long count) {
        stepCount.setValue(stepCount.getValue() + 1);
        Log.d("tag", TEXT_NUM_STEPS + this.stepCount);
        updateNotification();

    }

    @Override
    public void onDestroy() {
        Log.d("tag", "onDestroy");
        Preferences.setStepCount(StepCounterService.this, this.stepCount.getValue());
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("tag", "onTaskRemoved");
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
    public void startNewDay() {
        Log.d("myLogs", "new day started )))");
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
        Log.d("myLogs", "result == " + synced);
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


/*        Data data = new Data.Builder().putInt("count", this.stepCount.getValue()).build();
        stepCount.observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                data = new Data.Builder().putInt("count",integer).build();
            }
        });*//*


        //This will take care of the first execution
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        // Set Execution around 05:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 13);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();


        if (stepCount.getValue() != null) {
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(DatabaseInfoUpdater.class)
                    .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                    .setInputData(new Data.Builder().putInt(getString(R.string.steps), stepCount.getValue()).build()).build();
            WorkManager.getInstance().enqueue(oneTimeWorkRequest);
        } else Log.d("myLogs", "value = null");*/