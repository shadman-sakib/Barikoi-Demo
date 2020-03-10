package com.barikoi.barikoidemo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.barikoi.barikoidemo.Model.RequestQueueSingleton;
import com.barikoi.barikoidemo.Adapter.GhurboAdapter;
import com.barikoi.barikoidemo.Model.Api;
import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.R;
import com.barikoi.barikoidemo.Task.JsonUtilsTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GhurboKoiActivity extends AppCompatActivity implements GhurboAdapter.OnPlaceItemSelectListener{

        private ArrayList<Place> items;
        private GhurboAdapter adapter;
        private RequestQueue queue;
        private SharedPreferences prefs;
        String token;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private RecyclerView listView;
        private ProgressBar loading;
        public final static int GHURBOKOI=23;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ghurbo_koi);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            items = new ArrayList<>();
            adapter = new GhurboAdapter( items,this);
            queue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            token = prefs.getString(Api.INSTANCE.getTOKEN(),"");

            loading =(ProgressBar)findViewById(R.id.progressBarPublic);
            mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.ghurbolsitholder);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    generatelist();
                }
            });

            listView = findViewById(R.id.listghurbo);
            generatelist();

        }
        @Override
        public boolean onSupportNavigateUp(){
            finish();
            return true;
        }

        /**
         * Generates the List from server and populates activity
         */
        public void generatelist(){
            StringRequest request = new StringRequest(Request.Method.GET,
                    Api.INSTANCE.getUrl_ghurbokoi(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray placearray = new JSONArray(response);
                                items.clear();

                                items.addAll(JsonUtilsTask.getPlaces(placearray));
                                mSwipeRefreshLayout.setRefreshing(false);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                            // 3. setListAdapter
                            listView.setAdapter(adapter);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getActivity(),"Network error",Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }) {
            /*protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("longitude", String.valueOf(lon));
                parameters.put("latitude", String.valueOf(lat));

                return parameters;
            }*/

            };
            queue.add(request);
        }


        @Override
        public void onPlaceItemSelect(Place place) {
            loading.setVisibility(View.VISIBLE);
            queue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();
            Place clickedplace= place;
            StringRequest request=new StringRequest(Request.Method.GET,Api.INSTANCE.getUrl_get_place_details()+clickedplace.getCode(),
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response){
                            loading.setVisibility(View.GONE);
                            try {
                                JSONObject place= new JSONObject(response.toString());
                                Place newplace= JsonUtilsTask.getPlace(place);
                                loading.setVisibility(View.GONE);
                                if(getCallingActivity()==null) {
                                    Intent i = new Intent(GhurboKoiActivity.this, MainDemoActivity.class);
                                    i.putExtra("place_details",newplace);
                                    startActivity(i);
                                }else{
                                    Intent i=new Intent();
                                    i.putExtra("result",newplace);
                                    setResult(Activity.RESULT_OK,i);
                                    finish();
                                }

                            } catch (JSONException e) {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(GhurboKoiActivity.this, "problem or change in server, please wait and try again.", Toast.LENGTH_LONG).show();
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(GhurboKoiActivity.this,"Could not get result", Toast.LENGTH_LONG).show();
                        }
                    }
            ){
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    if(!token.equals(""))
                        params.put("Authorization", "bearer "+token);


                    return params;
                }

            };
            queue.add(request);

        }

    }

