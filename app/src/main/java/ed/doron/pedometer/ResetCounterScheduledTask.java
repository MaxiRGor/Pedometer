package ed.doron.pedometer;

import android.content.Context;

import java.util.TimerTask;

import ed.doron.pedometer.interfaces.OnResetCounterListenerListener;
import ed.doron.pedometer.services.StepCounterService;

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
