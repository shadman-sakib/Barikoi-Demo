package com.barikoi.barikoidemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import barikoi.barikoilocation.PlaceModels.NearbyPlace
import java.util.ArrayList


class PlaceListAdapter(
    private val places: ArrayList<NearbyPlace>, ///private Context context;
    private val opsl: OnPlaceItemSelectListener
)//this.placeListFiltered=places;
    : RecyclerView.Adapter<PlaceListAdapter.ViewHolder> /*implements Filterable*/() {
    //private ArrayList<Place> placeListFiltered;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.placelistitem, parent, false)
        return ViewHolder(view)

    }
    fun setplaces(places: ArrayList<NearbyPlace>){
        this.places.clear()
        this.places.addAll(places)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = places[position]

        holder.placeView.setText(holder.mItem!!.getAddress().split(",")[0])
        holder.areatag.setText(holder.mItem!!.getArea())
        //holder.codeView.setText(holder.mItem!!.getCode())
        holder.subtypeTag.setText(holder.mItem!!.getSubType())
        var distance = holder.mItem!!.distance_within_meters

        if (distance > 0) {
            holder.distanceview.text = distance.toString() + " meter"
        } else {
            holder.distanceview.visibility = View.GONE
        }
        holder.mView.setOnClickListener { opsl.onPlaceItemSelected(holder.mItem, position) }
    }

    override fun getItemCount(): Int {
        return places.size
    }


    /*@Override
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
    }*/

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        //val codeView: TextView
        val placeView: TextView
        val areatag: TextView
        val subtypeTag: TextView
        val distanceview: TextView

        var mItem: NearbyPlace? = null

        init {
            //codeView = mView.findViewById(R.id.textViewCode)
            placeView = mView.findViewById(R.id.textView_placename)
            areatag = mView.findViewById(R.id.textViewAreaTag)
            subtypeTag = mView.findViewById(R.id.textViewSubtypeTag)
            distanceview = mView.findViewById(R.id.textView_distance)

        }


        override fun toString(): String {
            return super.toString() + " '" + placeView.text + "'"
        }


    }

    interface OnPlaceItemSelectListener {

        fun onPlaceItemSelected(mItem: NearbyPlace?, position: Int)

    }

}
