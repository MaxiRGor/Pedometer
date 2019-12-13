package ed.doron.pedometer.ui;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ed.doron.pedometer.R;
import ed.doron.pedometer.data.PedometerViewModel;
import ed.doron.pedometer.models.DayResult;

public class DiagramFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private PedometerViewModel pedometerViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LineChart lineChart;

    public static DiagramFragment newInstance() {
        return new DiagramFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diagram_fragment, container, false);
        pedometerViewModel = ViewModelProviders.of(this).get(PedometerViewModel.class);
        setupSwipeRefreshListener(view);
        setupLineChart(view);
        return view;
    }

    private void setupSwipeRefreshListener(View view) {
        swipeRefreshLayout = view.findViewById(R.id.diagram_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setupLineChart(View view) {
        lineChart = view.findViewById(R.id.diagram_line_chart);
        List<Entry> entries = getEntries();
        LineDataSet dataSet = new LineDataSet(entries, "label1");
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    @Override
    public void onRefresh() {
        //adapter.updateStatistics((ArrayList<DayResult>) pedometerViewModel.getAllResults());
        swipeRefreshLayout.setRefreshing(false);
    }

    private List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        ArrayList<DayResult> results = (ArrayList<DayResult>) pedometerViewModel.getAllResults();
        int size = results.size();
        int i = (size < 8) ? size : 8;

        //int lastDay = Integer.valueOf(dateFormat.format(new Date(results.get(results.size() - 1).getTime())));


        for (; i > 0; i--) {
            entries.add(new Entry(i, results.get(size - i).getStepCount()));
        }
/*        for (DayResult result :(ArrayList<DayResult>) pedometerViewModel.getAllResults()) {
            entries.add(new Entry(Integer.valueOf(dateFormat.format(new Date(result.getTime()))), result.getStepCount()));
        }*/
        return entries;
    }
}
