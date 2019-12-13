package ed.doron.pedometer.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//@SuppressWarnings("unused")
@Entity
public class DayResult {


    @Ignore
    int id;
    @Ignore
    String documentId;
    @PrimaryKey
    long date;
    int stepSize;
    int stepLimit;
    int stepCount;
    @Ignore
    String uid;

    public DayResult(long date, int stepLength, int stepLimit, int stepCount) {
        this.date = date;
        this.stepSize = stepLength;
        this.stepLimit = stepLimit;
        this.stepCount = stepCount;
    }

    public DayResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStepSize() {
        return stepSize;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
