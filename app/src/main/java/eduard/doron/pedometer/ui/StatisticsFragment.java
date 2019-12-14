package eduard.doron.pedometer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import eduard.doron.pedometer.MainActivity;
import eduard.doron.pedometer.R;
import eduard.doron.pedometer.adapters.StatisticsRecyclerViewAdapter;
import eduard.doron.pedometer.data.PedometerViewModel;
import eduard.doron.pedometer.interfaces.OnEmptyDataListener;
import eduard.doron.pedometer.models.DayResult;


public class StatisticsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private PedometerViewModel pedometerViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StatisticsRecyclerViewAdapter adapter;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_fragment, container, false);
        pedometerViewModel = ViewModelProviders.of(this).get(PedometerViewModel.class);
        setupSwipeRefreshListener(view);
        setupRecyclerView(view);
        return view;
    }

    private void setupSwipeRefreshListener(View view) {
        swipeRefreshLayout = view.findViewById(R.id.statistics_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.statistics_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<DayResult> results = (ArrayList<DayResult>) pedometerViewModel.getAllResults();
        if (results.size() == 0) showSnackBar();
        adapter = new StatisticsRecyclerViewAdapter(this.getContext(), results);
        recyclerView.setAdapter(adapter);
    }

    private void showSnackBar() {
        OnEmptyDataListener listener = (MainActivity) this.getContext();
        if (listener != null) {
            listener.showSnackBar();
        }
    }

    @Override
    public void onRefresh() {
        ArrayList<DayResult> results = (ArrayList<DayResult>) pedometerViewModel.getAllResults();
        if (results.size() == 0) showSnackBar();
        adapter.updateStatistics(results);
        swipeRefreshLayout.setRefreshing(false);
    }


}
