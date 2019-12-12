package ed.doron.pedometer;

public class UserSetting {

    public UserSetting(String uid, boolean isDayMode, int stepCount, int stepLength, int stepLimit) {
        this.uid = uid;
        this.isDayMode = isDayMode;
        this.stepCount = stepCount;
        this.stepLength = stepLength;
        this.stepLimit = stepLimit;
    }

    public UserSetting() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isDayMode() {
        return isDayMode;
    }

    public void setDayMode(boolean dayMode) {
        isDayMode = dayMode;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
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

    private String uid;
    private boolean isDayMode;
    private int stepCount;
    private int stepLength;
    private int stepLimit;

}
