package jakeybakes.com.weather.adapters;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jakeybakes.com.weather.R;
import jakeybakes.com.weather.databinding.PreferredLocationsListItemBinding;
import jakeybakes.com.weather.location.PrefLocation;
import jakeybakes.com.weather.ui.LocationActivity;
import jakeybakes.com.weather.ui.LocationsLibraryActivity;
import jakeybakes.com.weather.ui.MainActivity;

public class PrefLocationAdapter extends RecyclerView.Adapter<PrefLocationAdapter.ViewHolder> {
    public int selectedPos = RecyclerView.NO_POSITION;
    private List<PrefLocation> myLocations;
    private Context context;
    private LocationsLibraryActivity caller;

    public PrefLocationAdapter(List<PrefLocation> myLocations, Context context, LocationsLibraryActivity caller) {
        this.myLocations = myLocations;
        this.context = context;
        this.caller = caller;
    }

    @Override
    public PrefLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        PreferredLocationsListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.preferred_locations_list_item,
                        parent,
                        false);

        return new PrefLocationAdapter.ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(PrefLocationAdapter.ViewHolder holder, int position) {
        // called for each viewholder to bind it to the adapter
        PrefLocation pLoc = myLocations.get(position);
        holder.itemView.setSelected(selectedPos == position);
        holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.myloc_listview_state));
        holder.binding.setPrefLocation(pLoc);
    }

    @Override
    public int getItemCount() {
        // returns the size of the collection of the items to display
        return myLocations.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // Binding variables
        public PreferredLocationsListItemBinding binding;

        // Constructor to do view lookups for each subview
        public ViewHolder(PreferredLocationsListItemBinding layoutBinding) {
            // pass binding to super and call get root to get the outermost view
            super(layoutBinding.getRoot());
            // Handle item click and set the selection
            itemView.setOnClickListener((v)->{
                    // Redraw the old selection and the new
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                    caller.preferredLocationOnClick(v, selectedPos);
            });
            //link the bindings together
            binding = layoutBinding;
        }



    }

}
