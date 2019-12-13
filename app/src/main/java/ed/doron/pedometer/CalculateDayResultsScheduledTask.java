package ed.doron.pedometer;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.TimerTask;

import ed.doron.pedometer.data.Preferences;
import ed.doron.pedometer.interfaces.OnNewDayStartedListener;
import ed.doron.pedometer.models.AppDatabase;
import ed.doron.pedometer.services.StepCounterService;

public class CalculateDayResultsScheduledTask extends TimerTask {

    private Context context;
    private MutableLiveData<Integer> steps;
    private OnNewDayStartedListener onNewDayStartedListener;

    public CalculateDayResultsScheduledTask(MutableLiveData<Integer> steps, Context context) {
        this.context = context;
        this.steps = steps;
        this.onNewDayStartedListener = (StepCounterService) context;
    }

    @Override
    public void run() {
        Log.d("myLogs", "run " + steps.getValue());
        // Log.d("myLogs" )
        //TODO calculate day result and synchronize with database
        Log.d("myLogs", "day mode " + Preferences.getDayMode(context));


        onNewDayStartedListener.reset();
    }



}
