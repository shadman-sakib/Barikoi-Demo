package com.barikoi.barikoidemo.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;


/**
 * This works in the GhurboKOI Activity to show lists of Places
 * Created by Sakib on 12/3/2017.
 */

public class GhurboAdapter extends RecyclerView.Adapter<GhurboAdapter.ViewHolder> {

    private final ArrayList<Place> itemsArrayList;
    private OnPlaceItemSelectListener ssl;
    String code;
    float distance;
    private LayoutInflater inflater;
    public GhurboAdapter(ArrayList<Place> itemsArrayList, OnPlaceItemSelectListener ssl) {
        this.itemsArrayList = itemsArrayList;
        this.ssl=ssl;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ghurbokoi_list_item, parent, false);
        //view.setLayoutParams(new RelativeLayout.LayoutParams(parent.getWidth()/2,parent.getWidth()/2));

        return new ViewHolder(view);
    }
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = itemsArrayList.get(position);
        // 3. Get the two text view from the rowView

        //holder.codeView.setText(holder.mItem.getCode());
        holder.placeView.setText(holder.mItem.getAddress().split(",")[0]);
        String area=holder.mItem.getArea();
        String subtype=holder.mItem.getSubType();
        String image= holder.mItem.getImglink();
        if(!image.equals("")) {
            holder.loading.setVisibility(View.VISIBLE);
            Glide.with(holder.mView.getContext())
                    .load(image)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {


                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.loading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.loading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.image);
        }else {
            Glide.with(holder.mView.getContext())
                    .load(R.drawable.barikoi_logo)
                    .fitCenter()
                    .into(holder.image);
        }
        holder.areatag.setText(area);
        holder.subtypeTag.setText(subtype);
        if(holder.areatag.getText().equals("") || area.equals("null")){
            holder.areatag.setVisibility(View.GONE);
        }
        if(holder.subtypeTag.getText().equals("")||subtype.equals("null")){
            holder.subtypeTag.setVisibility(View.GONE);
        }
        holder.share.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this awesome place at Barikoi!");
                sendIntent.putExtra(Intent.EXTRA_TEXT,"http://search.barikoi.com/#/"+holder.mItem.getCode());
                sendIntent.setType("text/plain");
                holder.mView.getContext().startActivity(sendIntent);
            }
        });
        holder.share.setFocusable(false);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ssl!=null){
                    ssl.onPlaceItemSelect(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView /*codeView,*/placeView,areatag,subtypeTag;
        public final ImageButton share;
        public final ImageView image;
        public final ProgressBar loading;
        public Place mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //codeView = (TextView) view.findViewById(R.id.textViewCode);
            placeView = (TextView) view.findViewById(R.id.textView_placename);
            areatag= (TextView) view.findViewById(R.id.textViewAreaTag);
            subtypeTag= (TextView) view .findViewById(R.id.textViewSubtypeTag);
            loading=(ProgressBar) view.findViewById(R.id.loading);
            share= (ImageButton)view.findViewById(R.id.sharelistButton);
            image=(ImageView)view.findViewById(R.id.place_image);
            share.setFocusable(false);

        }

        @Override
        public String toString() {
            return super.toString() ;
        }
    }

    public interface OnPlaceItemSelectListener {
        // TODO: Update argument type and name
        void onPlaceItemSelect(Place place);
    }
}
