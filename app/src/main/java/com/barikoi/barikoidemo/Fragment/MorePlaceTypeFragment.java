package com.barikoi.barikoidemo.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.barikoi.barikoidemo.Adapter.PlaceTypeAdapter;
import com.barikoi.barikoidemo.Activity.MainDemoActivity;
import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.Model.Type;
import com.barikoi.barikoidemo.R;

import java.util.ArrayList;

public class MorePlaceTypeFragment extends DialogFragment {
    public static String TAG = "MorePlaceTypeDialog";
    private ArrayList<Place> items;
    private PlaceTypeAdapter adapter;
    private RecyclerView typelist;
    Type type;
   MainDemoActivity.NearbySerchType nearbySerchType;

    Context context;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.more_place_type_dialog, container, false);
        ArrayList<Type> types = new ArrayList<>();

//        toolbar = view.findViewById(R.id.toolBar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(MorePlaceTypeFragment.TAG);
//                        if (prev != null) {
//                            DialogFragment df = (DialogFragment) prev;
//                            df.dismiss();
//                        }
//                    }
//                });
//        }

        types.add(new Type("","Hospital",getString(R.string.hospital),R.drawable.hospital));
        types.add(new Type("","Pharmacy",getString(R.string.pharmacy),R.drawable.pharmacy));
        types.add(new Type("Bank","",getString(R.string.bank),R.drawable.bank));
        types.add(new Type("Education","",getString(R.string.education),R.drawable.education));
        types.add(new Type("","Police Station",getString(R.string.policestation),R.drawable.policestation));
        types.add(new Type("Hotel","",getString(R.string.hotel),R.drawable.hotel));
        types.add(new Type("Public Toilet","",getString(R.string.toilet),R.drawable.toilet));
        types.add(new Type("fuel","",getString(R.string.fuel),R.drawable.gas));
        types.add(new Type("","BKash",getString(R.string.bkash),R.drawable.ic_fallback));
        types.add(new Type("","UCash",getString(R.string.ucash),R.drawable.ic_fallback));
        types.add(new Type("","SureCash",getString(R.string.surecash),R.drawable.ic_fallback));
        types.add(new Type("","Parking",getString(R.string.parking),R.drawable.parking));
        types.add(new Type("","General Store",getString(R.string.generalStore),R.drawable.ic_fallback));
        types.add(new Type("","Market",getString(R.string.market),R.drawable.ic_fallback));

        typelist = view.findViewById(R.id.typelist1);

        PlaceTypeAdapter typeadapter = new PlaceTypeAdapter(types,R.layout.moreplacetypelist, new PlaceTypeAdapter.OnPlaceItemSelectListener() {

            /**
             * @param type is the type the user search for nearby area
             *             On Item click sends a type to SearchNearby activity
             */
            @Override
            public void onPlaceItemSelected(Type type) {
                nearbySerchType.NearbySearchType(type,0,0);
            }

        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        typelist.setLayoutManager(linearLayoutManager);
        typelist.setAdapter(typeadapter);
        return view;

    }
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.context=context;
//        nearbySerchType=(MainDemoActivity.NearbySerchType)context;
//    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public interface NearbySerchType{
        void NearbySearchType(Type t, double lat, double lon);
    }
}