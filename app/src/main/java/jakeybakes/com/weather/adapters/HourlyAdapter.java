package jakeybakes.com.weather.adapters;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.databinding.HourlyListItemBinding;
import jakeybakes.com.weather.ui.HourlyForecastActivity;
import jakeybakes.com.weather.weather.Hour;

public    class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder>   {

    public int selectedPos = RecyclerView.NO_POSITION;
    private List<Hour> hours;
    private Context context;
    private HourlyForecastActivity caller;

    public HourlyAdapter(List<Hour> hours, Context context, HourlyForecastActivity caller) {
        this.hours = hours;
        this.context = context;
        this.caller = caller;
    }

    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // called when Adapter is created and is used to initialise the viewholder

        // create binding
        HourlyListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.hourly_list_item,
                        parent,
                        false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // called for each viewholder to bind it to the adapter
        Hour hour = hours.get(position);
        holder.itemView.setSelected(selectedPos == position);
        holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.forecast_listview_state));
        holder.hourlyListItemBinding.setHour(hour);
    }

    @Override
    public int getItemCount() {
        // returns the size of the collection of the items to display
        return hours.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Binding variables
        public HourlyListItemBinding hourlyListItemBinding;

        // Constructor to do view lookups for each subview
        public ViewHolder(HourlyListItemBinding hourlyLayoutBinding){
            // pass binding to super and call get root to get the outermost view
            super(hourlyLayoutBinding.getRoot());
            // Handle item click and set the selection
            itemView.setOnClickListener((v)->{
                // Redraw the old selection and the new
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
                caller.fromHourlyToHourDetails(v);
            });
            //link the bindings together
            hourlyListItemBinding = hourlyLayoutBinding;
        }
    }
}
