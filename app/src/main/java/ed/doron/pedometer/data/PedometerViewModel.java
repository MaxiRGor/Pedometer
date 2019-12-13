package ed.doron.pedometer.data;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ed.doron.pedometer.models.AppDatabase;
import ed.doron.pedometer.models.DayResult;

public class PedometerViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    private MutableLiveData<Integer> stepCount;
    private MutableLiveData<Integer> stepLimit;
    private MutableLiveData<Integer> stepLength;

    public PedometerViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(application);
    }


    public LiveData<Integer> getStepCount() {
        if (stepCount == null)
            stepCount = new MutableLiveData<>(1);
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        if (this.stepCount == null)
            this.stepCount = new MutableLiveData<>(stepCount);
        else
            this.stepCount.setValue(stepCount);
    }


    public LiveData<Integer> getStepLimit() {
        if (stepLimit == null)
            stepLimit = new MutableLiveData<>(10);
        return stepLimit;
    }

    public void setStepLimit(int stepLimit) {
        if (this.stepLimit == null)
            this.stepLimit = new MutableLiveData<>(stepLimit);
        else
            this.stepLimit.setValue(stepLimit);
    }

    public LiveData<Integer> getStepLength() {
        if (stepLength == null)
            stepLength = new MutableLiveData<>(10);
        return stepLength;
    }

    public void setStepLength(int stepLength) {
        if (this.stepLength == null)
            this.stepLength = new MutableLiveData<>(stepLength);
        else
            this.stepLength.setValue(stepLength);
    }


    public List<DayResult> getAllResults() {
        return appDatabase.getDayResultDao().getAllResults();
    }

}
