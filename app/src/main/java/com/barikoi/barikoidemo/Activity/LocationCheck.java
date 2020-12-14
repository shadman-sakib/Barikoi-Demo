package com.barikoi.barikoidemo.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.barikoi.barikoidemo.Model.Api;
import com.barikoi.barikoidemo.Model.RequestQueueSingleton;
import com.barikoi.barikoidemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class LocationCheck extends AppCompatActivity {

    private TextView cell_id, cell_lac, tv_response, tv_lat, tv_lon;
    private Integer mcc, mnc, cellid, cellLac;
    private RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_check);

        cell_id = findViewById(R.id.tv_cellId);
        cell_lac = findViewById(R.id.tv_cellLac);
        tv_lat = findViewById(R.id.tv_Lat);
        tv_lon = findViewById(R.id.tv_Lon);
        tv_response = findViewById(R.id.response);

        queue = RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue();

        TelephonyManager m_manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String networkOperator = m_manager.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
            Log.d("Locationcheck", "Sim mcc: " + mcc);
            Log.d("Locationcheck", "Sim mnc: " + mnc);
        }

        Log.d("Locationcheck", "Sim Iso: " + m_manager.getSimCountryIso());
        Log.d("Locationcheck", "Sim Network: " + m_manager.getNetworkOperator());


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        GsmCellLocation loc = (GsmCellLocation) m_manager.getCellLocation();
        if (loc != null)
        {
            if (loc.getCid() == -1) {
                Toast.makeText(getApplicationContext(), "Cell id unknown",Toast.LENGTH_SHORT).show();
            } else {
                cell_id.setText(String.valueOf(loc.getCid()));
                cellid = loc.getCid();
            }
            if (loc.getLac() == -1) {
                Toast.makeText(getApplicationContext(), "Cell LAC unknown",Toast.LENGTH_SHORT).show();
            } else {
                cell_lac.setText(String.valueOf(loc.getLac()));
                cellLac = loc.getLac();
            }
        }

        getLocation(mcc, mnc, cellid, cellLac);
    }

    private void getLocation(Integer mcc, Integer mnc, Integer cellid, Integer cellLac) {

        String url = "http://www.opencellid.org/";
        String params = "cell/get?key=feb120a641d77a&mcc="+mcc+"&mnc="+mnc+"&cellid="+cellid+"&lac="+cellLac+"&fmt=txt";
        StringRequest request = new StringRequest(Request.Method.GET, url + params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            // convert to a JSONObject
                            JSONObject jsonObject = xmlToJson.toJson();
                            // convert to a Json String
                            String jsonString = xmlToJson.toString();


                            JSONObject data = new JSONObject(jsonString);
                            JSONObject rsp = data.getJSONObject("rsp");
                            JSONObject cellinfo = rsp.getJSONObject("cell");
                            //JSONArray locArray = data.getJSONArray("rsp");

                            String latitude = cellinfo.getString("lat");
                            String longitude = cellinfo.getString("lon");
                            tv_lat.setText(latitude);
                            tv_lon.setText(longitude);

                            getRevAddress(latitude, longitude);

                            Log.d("Locationcheck", "lat: " +latitude+ " lon: " +longitude);

                        }catch (Exception e){
                            Log.d("Locationcheck", "error: " + e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Locationcheck", "Volley Error: " +error.getMessage());
            }
        }){

        };

        Log.d("Locationcheck", "response: " + request.getUrl());
        queue.add(request);


    }

    private void getRevAddress(String latitude, String longitude) {

        StringRequest request = new StringRequest(Request.Method.GET,
                Api.INSTANCE.getrevgeourl() + "?latitude=" + latitude + "&longitude=" + longitude,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject place = new JSONArray(response).getJSONObject(0);
                            String address = place.getString("Address");
                            tv_response.setText(address);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

        };
        queue.add(request);
    }

}
