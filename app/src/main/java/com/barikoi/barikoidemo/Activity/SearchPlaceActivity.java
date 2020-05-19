package com.barikoi.barikoidemo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.barikoi.barikoidemo.Adapter.SearchAdapter;
import com.barikoi.barikoidemo.Model.Api;
import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.Model.RecyclerViewEmptySupport;
import com.barikoi.barikoidemo.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import com.barikoi.barikoidemo.Model.RequestQueueSingleton;
import com.barikoi.barikoidemo.Task.JsonUtilsTask;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import barikoi.barikoilocation.JsonUtils;

public class SearchPlaceActivity extends AppCompatActivity implements SearchAdapter.OnPlaceItemSelectListener {

    private static final int MESSAGE_TEXT_CHANGED = 0;
    private static final long AUTOCOMPLETE_DELAY = 100;
    private ArrayList<Place> items;
    private RequestQueue queue;
    private RecyclerViewEmptySupport listView;
    //private RecyclerView recentSearchlistView;

    //private ProgressBar loading;
    private String token;

    public static final int REQUEST_SEARCH_CODE=69;
    private SearchAdapter placeAdapter;
    //private SearchAdapter recentPlaceAdapter;
    LinearLayout linearLayout;
    TextView tvRecentSearch;
    EditText editText;
    ProgressBar progressBar;
    ImageView imageBack, imageClose;
    FirebaseAnalytics firebaseAnalytics;
    //FirebaseAnalytics mFirebaseaAnalytics;
    private TextView addplace,couldntfind;
    private LinearLayout searchdeeplayout;
    private TextView searchdeep;
    private String suggestText, params;
    private TextView textV;
    private String Lat, Lon, fromInput, key;
    private CheckBox locationChecked;
    //PlaceTask placeTask;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        init();
        // handledearch(getIntent());
        //mFirebaseaAnalytics=FirebaseAnalytics.getInstance(this);
        //GetSavedPlace();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        imageBack = findViewById(R.id.imageBack);
        imageClose = findViewById(R.id.imgClose);
        progressBar=findViewById(R.id.progressBarSearchPlace);
        progressBar.setVisibility(View.GONE);
        locationChecked = findViewById(R.id.locationChecked);




        //Place place=GetSavedPlace();
//        if(!place.getAddress().equals("")) {
//            tvRecentSearch.setText(place.getAddress());
//            tvRecentSearch.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ReturnPlace(place);
//                }
//            });
//        }
//
//        else {
//            linearLayout.setVisibility(View.GONE);
//        }
        Lat = String.valueOf(getIntent().getDoubleExtra("lat", 1));
        Lon = String.valueOf(getIntent().getDoubleExtra("lng", 1));
        key = getIntent().getStringExtra("key");
        //fromInput = getIntent().getStringExtra("input");

        Log.d("Search", "LatLng: " +Lat+ ", " +Lon);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                items.clear();
                placeAdapter.notifyDataSetChanged();
            }
        });

//        editText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_LEFT = 0;
//                if(event.getAction() == MotionEvent.ACTION_UP && progressBar.getVisibility()!=View.VISIBLE) {
//                    if(editText.getCompoundDrawables()[DRAWABLE_RIGHT]!=null)
//                        if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                            editText.setText("");
//                            items.clear();
//                            placeAdapter.notifyDataSetChanged();
//                            return true;
//                        }
//                }
//
//                if(event.getRawX() <= (editText.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                    onBackPressed();
//                    return true;
//                }
//                return false;
//            }
//        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                listView.emptyshow(false);
                //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,0,0);
                if(charSequence.length()>=2){
                    //linearLayout.setVisibility(View.GONE);
                    queue.cancelAll("search");
                    mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
                    //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,0,0);
                    imageBack.setVisibility(View.VISIBLE);
                    imageClose.setVisibility(View.GONE);
                    final Message msg = Message.obtain(mHandler, MESSAGE_TEXT_CHANGED, charSequence.toString());
                    mHandler.sendMessageDelayed(msg, AUTOCOMPLETE_DELAY);
                    //
                }
                else {
                    //linearLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,R.drawable.ic_close,0);
                    imageBack.setVisibility(View.VISIBLE);
                    imageClose.setVisibility(View.VISIBLE);
                    dropExtraActions();
                    mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
                    queue.cancelAll("search");
                    items.clear();
                    placeAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                suggestText = editable.toString();
                textV = findViewById(R.id.tvSuggestions);

                String key = getIntent().getStringExtra("key");
                //Log.d("Search", key);
                if (key.equals("mainactivity") && !key.equals("null")){
                    textV.setVisibility(View.GONE);
                }
                else if (key.equals("navigationActivity") && !key.equals("null")){
                    textV.setVisibility(View.GONE);
                }
                else {
                    textV.setVisibility(View.VISIBLE);
                    textV.setText(suggestText);
                    textV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //ReturnPlace(Place);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("suggestions",textV.getText().toString().trim());
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();

                        }
                    });
                }


            }
        });

        couldntfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showdialog();
            }
        });
        searchdeep.setOnClickListener(v ->{
            dropExtraActions();
            rupantor(editText.getText().toString());
        });
       /* Intent intent = getIntent();
        if (intent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false)) {
            Bundle parameters = intent.getExtras();
            String idString = parameters.getString("id");
            Toast.makeText(this, ""+idString, Toast.LENGTH_SHORT).show();
            // Do something with idString
            generatelist(idString);
        }*/

//       Intent intent = getIntent();
//       if(intent.getExtras()!=null){
//           String action = intent.getAction();
//           Uri data = intent.getData();
//           String code=data.toString();
//           //String[] codes = code.split("\\s*/\\s*");
//           //Toast.makeText(this,""+codes[codes.length-1],Toast.LENGTH_LONG).show();
//           //generatelist(codes[codes.length-1]);
//           editText.setText(codes[codes.length-1]);
//       }

    }

    /**
     * Initializes the views needed in this activity
     */



    private void init(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        queue= RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = prefs.getString(Api.INSTANCE.getTOKEN(), "");
        items=new ArrayList<Place>();
        //loading= findViewById(R.id.loading);
        listView= findViewById(R.id.searchedplacelist);
        View emptylist=findViewById(R.id.empty);
        View nonetList=findViewById(R.id.nonetcontainer);
        listView.setNonetview(nonetList);
        listView.setEmptyView(emptylist);
        // recentSearchlistView= findViewById(R.id.recentsearchedplacelist);
        placeAdapter=new SearchAdapter(items, this );
        //recentPlaceAdapter=new SearchAdapter(items, this );
        listView.setAdapter(placeAdapter);
        //recentSearchlistView.setAdapter(recentPlaceAdapter);
        linearLayout=findViewById(R.id.linearLayoutSuggestion);
        tvRecentSearch=findViewById(R.id.tvSuggestions);
        editText=findViewById(R.id.editText1);
        searchdeeplayout=findViewById(R.id.searchmore);
        searchdeep=findViewById(R.id.buttonsearchdeep);
        couldntfind=findViewById(R.id.textViewcouldntfind);

        dropExtraActions();
        editText.requestFocus();
    }
/*
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handledearch(intent);
    }*/

   /* public void handledearch( Intent intent)
    {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            mtToolbar.setTitle(query);

            SearchRecentSuggestions searchRecentSuggestions=new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY,MySuggestionProvider.MODE);

            searchRecentSuggestions.saveRecentQuery(query,null);
        }
    }*/
    /**
     * @param nameOrCode is the place searching for in the app
     *  requests the server to get list of search results
     */
    public void generatelist(final String nameOrCode) {

//        locationChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });

        if(locationChecked.isChecked()){
            params = "?search="+nameOrCode+"&latitude="+Lat+"&longitude="+Lon;
        }else {
            params = "?search="+nameOrCode;
        }

        //loading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,0,0);
        imageBack.setVisibility(View.VISIBLE);
        imageClose.setVisibility(View.GONE);
        queue.cancelAll("search");
        items.clear();
        if (nameOrCode.length() > 0) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Api.INSTANCE.getSearchUrl()+ params,
                    (String response) -> {
                        //loading.setVisibility(View.GONE);
                        //Log.d("Search", "response: " +response);

                        progressBar.setVisibility(View.GONE);
                        //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,R.drawable.ic_close,0);
                        imageClose.setVisibility(View.VISIBLE);
                        try {
                            JSONObject data = new JSONObject(response);
                            Log.d("search result",data.toString());
                            JSONArray placearray = data.getJSONArray("places");
                            ArrayList<Place> newplaces = JsonUtilsTask.getPlaces(placearray);
                            Bundle b = new Bundle();
                            b.putString("query", nameOrCode);
                            b.putInt("result_numbers", placearray.length());
                            //mFirebaseaAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, b);

                            if (newplaces.size()== 0) {
                                listView.emptyshow(true);

                                //Toast.makeText(this,"google", Toast.LENGTH_SHORT).show();
                                //getPlaceFromAutocomplete(nameOrCode);
                            } else {
                                placeAdapter.setSearchtext(nameOrCode);

                                Log.d("searchresult type", newplaces.get(0).getType());
                                items.addAll(newplaces);
                                placeAdapter.notifyDataSetChanged();

                            }
                            showSearchmore();
                        } catch (JSONException e) {
                            try{
                                JSONObject data = new JSONObject(response);
                                listView.emptyshow(true);
                                showSearchmore();
                                //Toast.makeText(SearchPlaceActivity.this,data.getJSONObject("places").getString("Message"), Toast.LENGTH_SHORT).show();
                            }
                            catch (JSONException ex){
                                Toast.makeText(SearchPlaceActivity.this,"problem formatting data", Toast.LENGTH_SHORT).show();
                                ex.printStackTrace();
                            }

                        }
                    },
                    error -> {
                        //loading.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,R.drawable.ic_close,0);
                        imageClose.setVisibility(View.VISIBLE);
                        JsonUtils.logResponse(error);
                        listView.nonetshow(true);
                        //JsonUtils.handleResponse(error, SearchPlaceActivity.this);
                        Log.d("search params",nameOrCode);

                    }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (!token.equals(""))
                        params.put("Authorization", "bearer " + token);
                    return params;
                }

                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("search", nameOrCode);
                    return params;
                }
            };
            request.setTag("search");
            //loading.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            imageClose.setVisibility(View.GONE);
            dropExtraActions();
            queue.add(request);
        }
    }
    private void rupantor(final String nameOrCode) {
        Log.d("Rupantor",nameOrCode);
        //loading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        //editText.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        imageClose.setVisibility(View.GONE);
        imageBack.setVisibility(View.GONE);
        queue.cancelAll("search");
        items.clear();
        if (nameOrCode.length() > 0) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Api.INSTANCE.getUrl_rupantor_search(),
                    (String response) -> {
                        //loading.setVisibility(View.GONE);

                        progressBar.setVisibility(View.GONE);
                        //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,R.drawable.ic_close,0);
                        imageClose.setVisibility(View.VISIBLE);
                        imageBack.setVisibility(View.VISIBLE);
                        try {
                            JSONObject data = new JSONObject(response).getJSONObject("geocoded_address");
                            Log.d("search result",data.toString());
                            Place newplace = JsonUtilsTask.getPlace(data);
                            Bundle b = new Bundle();
                            b.putString("query", nameOrCode);
                            //mFirebaseaAnalytics.logEvent("Rupantor_search", b);

                            if (newplace==null) {
                                listView.emptyshow(true);
                                //Toast.makeText(this,"google", Toast.LENGTH_SHORT).show();
                                //getPlaceFromAutocomplete(nameOrCode);
                            } else {
                                placeAdapter.setSearchtext(nameOrCode);

                                Log.d("searchresult type", newplace.getType());
                                items.add(newplace);
                                placeAdapter.notifyDataSetChanged();

                            }
                            showCouldFind();
                        } catch (JSONException e) {
                            //Crashlytics.logException(e.getCause());
                            listView.emptyshow(true);
                            showCouldFind();

                        }
                    },
                    error -> {
                        //loading.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back,0,R.drawable.ic_close,0);
                        imageClose.setVisibility(View.VISIBLE);
                        imageBack.setVisibility(View.VISIBLE);
                        JsonUtils.logResponse(error);
                        listView.nonetshow(true);
                        dropExtraActions();
                        //JsonUtils.handleResponse(error, SearchPlaceActivity.this);
                        Log.d("search params",nameOrCode);
                        /*Toast toast= Toast.makeText(getApplicationContext(),
                                "Not Found", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 250);
                        toast.show();*/

                    }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (!token.equals(""))
                        params.put("Authorization", "bearer " + token);
                    return params;
                }

                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("q", nameOrCode);
                    return params;
                }
            };
            request.setTag("search");
            //loading.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            imageClose.setVisibility(View.GONE);
            dropExtraActions();
            queue.add(request);
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_TEXT_CHANGED) {
                String enteredText = (String)msg
                        .obj;
                items.clear();
                placeAdapter.notifyDataSetChanged();
                queue.cancelAll("search");
                generatelist(enteredText);
                editText.requestFocus();
            }
        }
    };

    /**
     * This method first save the last searched place by the user
     * then redirects the user to show info about the place in the main activity
     * @param mItem this is the last place the user searched for
     * @param position
     */
    @Override
    public void onPlaceItemSelected(Place mItem, int position) {
        //SavePlace(mItem);
        getGeoCodePlace(mItem.getCode());
    }
//    public void SavePlace(Place mItem){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        prefs.edit().putString("RecentSearchedAddress",mItem.getAddress()).apply();
//        prefs.edit().putString("RecentSearchedLat",mItem.getLat()).apply();
//        prefs.edit().putString("RecentSearchedLon",mItem.getLon()).apply();
//        prefs.edit().putString("RecentSearchedCode",mItem.getCode()).apply();
//        prefs.edit().putString("RecentSearchedCity",mItem.getCity()).apply();
//        prefs.edit().putString("RecentSearchedArea",mItem.getArea()).apply();
//        prefs.edit().putString("RecentSearchedPostalCode",mItem.getPostalcode()).apply();
//        prefs.edit().putString("RecentSearchedType",mItem.getType()).apply();
//        prefs.edit().putString("RecentSearchedSubType",mItem.getSubType()).apply();
//        prefs.edit().putString("RecentSearchedDistance",String.valueOf(mItem.getDistance())).apply();
//        prefs.edit().putString("RecentSearchedIgmLink",mItem.getImglink()).apply();
//        prefs.edit().putString("RecentSearchedRoute",mItem.getRoute()).apply();
//        prefs.edit().putString("RecentSearchedWard",mItem.getWard()).apply();
//        prefs.edit().putString("RecentSearchedZone",mItem.getZone()).apply();
//        prefs.edit().putString("RecentSearchedPhone",mItem.getPhoneNumber()).apply();
//    }
//    public Place GetSavedPlace(){
//        Place place=new Place();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        place.setAddress(prefs.getString("RecentSearchedAddress",""));
//        place.setLat(prefs.getString("RecentSearchedLat",""));
//        place.setLon(prefs.getString("RecentSearchedLon",""));
//        place.setCode(prefs.getString("RecentSearchedCode",""));
//        place.setCity(prefs.getString("RecentSearchedCity",""));
//        place.setArea(prefs.getString("RecentSearchedArea",""));
//        place.setPostalcode(prefs.getString("RecentSearchedPostalCode",""));
//        place.setType(prefs.getString("RecentSearchedType",""));
//        place.setSubType(prefs.getString("RecentSearchedSubType",""));
//        place.setDistance(0.0f);
//        place.setImglink(prefs.getString("RecentSearchedIgmLink",""));
//        place.setRoute(prefs.getString("RecentSearchedRoute",""));
//        place.setWard(prefs.getString("RecentSearchedWard",""));
//        place.setZone(prefs.getString("RecentSearchedZone",""));
//        place.setPhoneNumber(prefs.getString("RecentSearchedPhone",""));
//
//        return place;
//    }
    public void getGeoCodePlace(String nameOrCode){
        queue.cancelAll("search");
        progressBar.setVisibility(View.VISIBLE);
        if (nameOrCode.length() > 0) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    Api.INSTANCE.getGeoUrl() +nameOrCode,
                    (String response) -> {
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            JSONObject data =jsonArray.getJSONObject(0);
                            Place newplace = JsonUtilsTask.getPlace(data);
                            progressBar.setVisibility(View.GONE);
                            //SavePlace(newplace);
                            ReturnPlace(newplace);

                        } catch (JSONException e) {
                            try{
                                JSONObject data = new JSONObject(response);
                                //Toast.makeText(SearchPlaceActivity.this,data.getJSONObject("places").getString("Message"), Toast.LENGTH_SHORT).show();
                            }
                            catch (JSONException ex){
                                Toast.makeText(SearchPlaceActivity.this,"problem formatting data", Toast.LENGTH_SHORT).show();
                                ex.printStackTrace();
                            }

                        }
                    },
                    error -> {
                        JsonUtils.logResponse(error);
                    }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if (!token.equals(""))
                        params.put("Authorization", "bearer " + token);
                    return params;
                }
            };
            request.setTag("search");
            queue.add(request);
        }
    }
    public void ReturnPlace(Place mItem){
        Intent returnIntent= new Intent();

        Log.d("Navigation", "search: " +key);
        Log.d("Navigation", "source: " +getIntent().hasExtra("SOURCE"));
        Log.d("Navigation", "dest: " +getIntent().hasExtra("DESTINATION"));

        if(getCallingActivity()!=null){

            if(getIntent().hasExtra("SOURCE")){
                Log.d("Navigation", "source");
                returnIntent = new Intent();
                returnIntent.putExtra("result",mItem);
                returnIntent.putExtra("RSOURCE", 1);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            else if(getIntent().hasExtra("DESTINATION")){
                Log.d("Navigation", "destination");
                returnIntent = new Intent();
                returnIntent.putExtra("result",mItem);
                returnIntent.putExtra("RDESTINATION", 1);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
            else {
                returnIntent = new Intent();
                returnIntent.putExtra("result",mItem);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }
        else{
            returnIntent = new Intent(this,CheckOutActivity.class);
            returnIntent.putExtra("result",mItem);
            //setResult(Activity.RESULT_OK,returnIntent);
            startActivity(returnIntent);
            finish();
        }
    }

    private void showdialog(){
        new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog)
                .setMessage("We are sorry we could not provide you with the address you are looking for with \""+editText.getText().toString()+"\". Please help us by letting us know what what we are missing.")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendmissing(editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    private void sendmissing(String missing){
        StringRequest request= new StringRequest(Request.Method.POST, Api.INSTANCE.getUrl_couldntfind(),
                response -> {
                    Toast.makeText(this, "Thank you for helping us!", Toast.LENGTH_SHORT).show();
                },error -> {


        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("search",missing);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if (!token.equals(""))
                    params.put("Authorization", "bearer " + token);
                return params;
            }
        };
        queue.add(request);

    }

    private void dropExtraActions(){
        searchdeeplayout.setVisibility(View.GONE);
        couldntfind.setVisibility(View.GONE);
    }

    private void showSearchmore(){
        searchdeeplayout.setVisibility(View.VISIBLE);
        couldntfind.setVisibility(View.GONE);
    }
    private void showCouldFind(){
        searchdeeplayout.setVisibility(View.GONE);
        couldntfind.setVisibility(View.VISIBLE);
    }
}



