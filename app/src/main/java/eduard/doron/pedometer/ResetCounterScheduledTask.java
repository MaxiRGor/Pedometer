package eduard.doron.pedometer;

import android.content.Context;

import java.util.TimerTask;

import eduard.doron.pedometer.interfaces.OnResetCounterListenerListener;
import eduard.doron.pedometer.services.StepCounterService;

public class ResetCounterScheduledTask extends TimerTask {

    private OnResetCounterListenerListener onResetCounterListener;

    public ResetCounterScheduledTask(Context context) {
        this.onResetCounterListener = (StepCounterService) context;
    }

    @Override
    public void run() {
        onResetCounterListener.reset();
    }
}
