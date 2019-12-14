package ed.doron.pedometer.ui;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import ed.doron.pedometer.MainActivity;
import ed.doron.pedometer.data.PedometerViewModel;
import ed.doron.pedometer.R;

public class ProgressFragment extends Fragment {

    public static ProgressFragment newInstance() {
        return new ProgressFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        PedometerViewModel pedometerViewModel = ViewModelProviders.of(requireActivity()).get(PedometerViewModel.class);

        View view = inflater.inflate(R.layout.progress_fragment, container, false);
        final CircularProgressBar circularProgressBar = view.findViewById(R.id.circularProgressBar);
        final TextView progressStepCountTextView = view.findViewById(R.id.progress_step_count_text_view);
        final TextView progressStepLimitTextView = view.findViewById(R.id.progress_step_limit_text_view);
        final TextView progressDistanceTextView = view.findViewById(R.id.progress_distance_text_view);
        pedometerViewModel.getStepLimit().observe(this, integer -> {
            circularProgressBar.setProgressMax(integer);
            progressStepLimitTextView.setText(String.valueOf(integer));
        });
        pedometerViewModel.getStepCount().observe(this, integer -> {
            circularProgressBar.setProgress(integer);
            progressStepCountTextView.setText(String.valueOf(integer));
            progressDistanceTextView.setText(String.format(getString(R.string.progress_distance_info)
                    , pedometerViewModel.getStepLength().getValue()
                    , getDistance(integer, pedometerViewModel.getStepLength().getValue())));
        });

        return view;
    }

    private int getDistance(int steps, int length) {
        return steps * length / 100;
    }


}
