package eduard.doron.pedometer.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@SuppressWarnings("unused")
@Entity
public class DayResult {

    @PrimaryKey
    private long time;
    private int stepLength;
    private int stepLimit;
    private int stepCount;
    private boolean synced;


    public DayResult(long time, int stepLength, int stepLimit, int stepCount, boolean synced) {
        this.time = time;
        this.stepLength = stepLength;
        this.stepLimit = stepLimit;
        this.stepCount = stepCount;
        this.synced = synced;
    }

    @Ignore
    public DayResult() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStepLength() {
        return stepLength;
    }

    public void setStepLength(int stepLength) {
        this.stepLength = stepLength;
    }

    public int getStepLimit() {
        return stepLimit;
    }

    public void setStepLimit(int stepLimit) {
        this.stepLimit = stepLimit;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }


}
