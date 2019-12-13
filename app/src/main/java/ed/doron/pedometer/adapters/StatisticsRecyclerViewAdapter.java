package ed.doron.pedometer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.progressview.ProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ed.doron.pedometer.R;
import ed.doron.pedometer.models.DayResult;

public class StatisticsRecyclerViewAdapter extends RecyclerView.Adapter<StatisticsRecyclerViewAdapter.DayResultViewHolder> {

    private Context context;
    private ArrayList<DayResult> dayResults;
    private SimpleDateFormat simpleDateFormat;

    public StatisticsRecyclerViewAdapter(Context context, ArrayList<DayResult> dayResults) {
        this.simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        this.dayResults = dayResults;
        this.context = context;
    }

    @NonNull
    @Override
    public DayResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_result_item, parent, false);
        return new DayResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayResultViewHolder holder, int position) {
        holder.bind(dayResults.get(position));
    }

    @Override
    public int getItemCount() {
        return dayResults.size();
    }

    public void updateStatistics(ArrayList<DayResult> dayResults) {
        this.dayResults = dayResults;
        notifyDataSetChanged();
    }


    class DayResultViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        ProgressView progressView;
        TextView infoTextView;

        DayResultViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.day_result_date_text_view);
            progressView = itemView.findViewById(R.id.day_result_progress_view);
            infoTextView = itemView.findViewById(R.id.day_result_info_text_view);
        }

        void bind(final DayResult dayResult) {
            int distance = dayResult.getStepCount() * dayResult.getStepLength() / 100;
            dateTextView.setText(simpleDateFormat.format(new Date(dayResult.getTime())));
            progressView.setMax(dayResult.getStepLimit());
            progressView.setProgress(dayResult.getStepCount());
            progressView.progressAnimate();
            infoTextView.setText(String.format(context.getString(R.string.day_result_value)
                    , dayResult.getStepCount()
                    , dayResult.getStepLimit()
                    , distance));
        }

    }
}