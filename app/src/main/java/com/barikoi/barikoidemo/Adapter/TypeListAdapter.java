package com.barikoi.barikoidemo.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.barikoi.barikoidemo.Model.Type;
import com.barikoi.barikoidemo.R;

import java.util.ArrayList;

/**
 * Is created to populate the list item in Explore Nearby Fragment
 * Created by Sakib on 2/3/2018.
 */

public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.ViewHolder> {


    private ArrayList<Type> itemsArrayList;
    private TypeListAdapter.OnTypeItemSelectListener ssl;
    private int itemresourceId;
    private LayoutInflater inflater;
    private static String TAG = "TypeListAdapter";

    public TypeListAdapter(ArrayList<Type> itemsArrayList, int itemresourceId, TypeListAdapter.OnTypeItemSelectListener ssl) {
        this.itemsArrayList = itemsArrayList;
        this.ssl=ssl;
        this.itemresourceId=itemresourceId;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(itemresourceId, parent, false);
        //view.setLayoutParams(new RelativeLayout.LayoutParams(parent.getWidth()/2,parent.getWidth()/2));

        return new TypeListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem=itemsArrayList.get(position);
        holder.typeView.setImageResource(holder.mItem.getImg());
        holder.textView.setText(holder.mItem.getDisplayname());

        Log.d(TAG, "TypeS List: " +itemsArrayList.get(position));
        Log.d(TAG, "TypeS List: " +holder.mItem.getDisplayname());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssl.onTypeSelected(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView typeView;
        public final TextView textView;
        public Type mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            typeView =  view.findViewById(R.id.typeView);
            textView=view.findViewById(R.id.textView39);
        }

        @Override
        public String toString() {
            return super.toString() ;
        }
    }

    public interface OnTypeItemSelectListener {

        void onTypeSelected(Type t);
    }
}
