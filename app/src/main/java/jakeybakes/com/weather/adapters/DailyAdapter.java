package jakeybakes.com.weather.adapters;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.databinding.DailyListItemBinding;
import jakeybakes.com.weather.ui.DailyForecastActivity;
import jakeybakes.com.weather.weather.Day;

/**
 * Created by repoo on 05/03/2019.
 */

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder>   {

    public int selectedPos = RecyclerView.NO_POSITION;
    private List<Day> days;
    private Context context;
    private DailyForecastActivity caller;

    public DailyAdapter(List<Day> days, Context context, DailyForecastActivity caller) {
        this.days = days;
        this.context = context;
        this.caller = caller;
    }

    @Override
    public DailyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // called when Adapter is created and is used to initialise the viewholder

        // create binding
        DailyListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.daily_list_item,
                        parent,
                        false);

        return new DailyAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DailyAdapter.ViewHolder holder, int position) {
        // called for each viewholder to bind it to the adapter
        Day day = days.get(position);
        holder.itemView.setSelected(selectedPos == position);
        holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.forecast_listview_state));
        holder.dailyListItemBinding.setDay(day);
    }

    @Override
    public int getItemCount() {
        // returns the size of the collection of the items to display
        return days.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        // Binding variables
        public DailyListItemBinding dailyListItemBinding;

        // Constructor to do view lookups for each subview
        public ViewHolder(DailyListItemBinding dailyLayoutBinding){
            // pass binding to super and call get root to get the outermost view
            super(dailyLayoutBinding.getRoot());
            // Handle item click and set the selection
            itemView.setOnClickListener((v)->{
                // Redraw the old selection and the new
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
                caller.dailyMoreDetailsOnClick(v);
            });
            //link the bindings together
            dailyListItemBinding = dailyLayoutBinding;
        }
    }
}
