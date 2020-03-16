package com.barikoi.barikoidemo.Adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is to populate the nearby place search result in a list in SearchNearbyPlace Activity
 * Created by Sakib on 2/9/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    private ArrayList<Place> places;
    private String searchtext;
    ///private Context context;
    private OnPlaceItemSelectListener opsl;
    //private ArrayList<Place> placeListFiltered;

    public SearchAdapter(ArrayList<Place> places, OnPlaceItemSelectListener opsl){
        this.places=places;
        searchtext="";
        //this.placeListFiltered=places;
        this.opsl=opsl;
    }

    public void setSearchtext(String searchtext){
        this.searchtext=searchtext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_placelistitem_layout, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = places.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opsl.onPlaceItemSelected( holder.mItem,position);
            }
        });
        setAdvancedDetailsHighlight(holder.placeView,holder.mItem.getAddress(),searchtext);
        //holder.placeView.setText(holder.mItem.getAddress());
        setAdvancedDetailsHighlight(holder.areatag,holder.mItem.getArea(),searchtext);
    }

    public int findposition(Place p){
        int position=0;
        for (int i=0; i<getItemCount();i++){
            if(places.get(i).getCode().equals(p.getCode())){
                position=i;
            }
        }
        return  position;
    }
    public int findposition(String s){
        int position=0;
        for (int i=0; i<getItemCount();i++){
            if(places.get(i).getCode().equals(s)){
                position=i;
            }
        }

        return  position;
    }

    public int findqueryposition(String query){
        int position=0;
        for (int i=0; i<getItemCount();i++){
            if(places.get(i).getAddress().matches(query)){
                position=i;
            }
        }
        return  position;
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                ArrayList<Place> filteredList = new ArrayList<Place>();
                if (charString.isEmpty()) {
                    filteredList = places;
                } else {

                    for (Place row : places) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getAddress().toLowerCase().contains(charString.toLowerCase()) || row.getSubType().contains(charString) || row.getCode().contains(charString)) {
                            filteredList.add(row);
                        }
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                filterResults.count= filteredList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //placeListFiltered = (ArrayList<Place>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void removeAt(int position) {
        places.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, places.size());
    }

    public ArrayList<Place> getList() {
        return places;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        //public final TextView codeView;
        public final TextView placeView;
        public final TextView areatag;
        public final ImageView typeView;
       /* public final TextView subtypeTag, distance;
        public final ImageButton shareButton;*/
        public Place mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //codeView =  mView.findViewById(R.id.textViewCode);
            placeView =  mView.findViewById(R.id.textView_placename);
            areatag=  mView.findViewById(R.id.textViewAreaTag);
            typeView=mView.findViewById(R.id.imageView);
            /*subtypeTag=  mView .findViewById(R.id.textViewSubtypeTag);
            distance= mView.findViewById(R.id.textView_distance);
            typeView=  mView.findViewById(R.id.imageView14);
            shareButton = mView.findViewById(R.id.sharelistButton);*/
        }
    }

    public interface OnPlaceItemSelectListener{

        void onPlaceItemSelected(Place mItem, int position);

        default void onSharePlaceClicked(String uCode){}
    }
    public static void setAdvancedDetailsHighlight(TextView textView, String fullText, String searchText) {
        searchText = searchText.replace("'", "");

        // highlight search text
        if (null != searchText && !searchText.isEmpty()) {
            String[]searchTextcomps=searchText.split("\\s|,|\\.|-");
            SpannableStringBuilder wordSpan = new SpannableStringBuilder(fullText);
            for(String searchtext: searchTextcomps) {

                Pattern p = Pattern.compile(searchtext, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(fullText);
                while (m.find()) {

                    int wordStart = m.start();
                    int wordEnd = m.end();

                    // Now highlight based on the word boundaries
                    ColorStateList redColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLACK});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, redColor, null);

                    wordSpan.setSpan(highlightSpan, wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //wordSpan.setSpan(new BackgroundColorSpan(0xFFFCFF48), wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    wordSpan.setSpan(new RelativeSizeSpan(1.1f), wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }
            }

            textView.setText(wordSpan, TextView.BufferType.SPANNABLE);

        } else {
            textView.setText(fullText);
        }
    }
}
