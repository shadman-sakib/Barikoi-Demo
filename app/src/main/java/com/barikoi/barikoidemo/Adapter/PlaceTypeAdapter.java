package com.barikoi.barikoidemo.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.barikoi.barikoidemo.Model.Type;
import com.barikoi.barikoidemo.R;

import java.util.ArrayList;


/**
 * We use this to populate a list for MorePlacetype Activity
 * The rest of the types are populated through this
 */
public class PlaceTypeAdapter extends RecyclerView.Adapter<PlaceTypeAdapter.ViewHolder> implements Filterable {

    private ArrayList<Type> places;
    ///private Context context;
    private OnPlaceItemSelectListener opsl;
    int layoutResource;

    public PlaceTypeAdapter(ArrayList<Type> places, int layoutResource, OnPlaceItemSelectListener opsl){
        this.places=places;
        this.layoutResource=layoutResource;
        this.opsl=opsl;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutResource, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PlaceTypeAdapter.ViewHolder holder, int position) {
        holder.textView.setText(places.get(position).toString());
        holder.imageView.setImageResource(places.get(position).getImg());
        Log.d("PlaceTypeAdapter",places.get(position).toString());
        holder.type=places.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opsl.onPlaceItemSelected(holder.type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public final View mView;
        public final TextView textView;
        public final ImageView imageView;
        public Type type;
        public ViewHolder(View view) {
            super(view);
            textView=view.findViewById(R.id.textViewPlaceType);
            imageView=view.findViewById(R.id.imageViewType);
            mView = view;

        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public interface OnPlaceItemSelectListener{

        void onPlaceItemSelected(Type type);

    }

}
