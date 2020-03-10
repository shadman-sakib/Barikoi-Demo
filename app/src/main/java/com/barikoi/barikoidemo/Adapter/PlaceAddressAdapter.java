package com.barikoi.barikoidemo.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.R;

import java.util.ArrayList;

public class PlaceAddressAdapter extends RecyclerView.Adapter<PlaceAddressAdapter.ViewHolder> implements Filterable {

    private ArrayList<Place> places;
    ///private Context context;
    private OnPlaceItemSelectListener opsl;
    private ArrayList<Place> placeListFiltered;


    public PlaceAddressAdapter(ArrayList<Place> places, OnPlaceItemSelectListener opsl){
        this.places=places;
        this.placeListFiltered=places;
        this.opsl=opsl;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestionlistitem, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = placeListFiltered.get(position);

        holder.placeView.setText(holder.mItem.getAddress()+", "+holder.mItem.getArea()+", "+holder.mItem.getCity());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opsl.onPlaceItemSelected(holder.mItem,position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return placeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if (charString.isEmpty()) {
                    placeListFiltered = places;
                } else {
                    ArrayList<Place> filteredList = new ArrayList<Place>();
                    for (Place row : places) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getAddress().toLowerCase().contains(charString.toLowerCase()) || row.getSubType().contains(charString) || row.getCode().contains(charString)) {
                            filteredList.add(row);
                        }
                    }

                    placeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = placeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                placeListFiltered = (ArrayList<Place>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final View mView;

        public final TextView placeView;


        public Place mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            placeView = mView.findViewById(R.id.textViewAddress);

        }


        @Override
        public String toString() {
            return super.toString() + " '" + placeView.getText() + "'";
        }


    }

    public interface OnPlaceItemSelectListener{

        void onPlaceItemSelected(Place mItem, int position);

    }

}