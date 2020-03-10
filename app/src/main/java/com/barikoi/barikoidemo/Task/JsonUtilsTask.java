package com.barikoi.barikoidemo.Task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.barikoi.barikoidemo.Activity.MainDemoActivity;
import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.Model.Road;
import com.barikoi.barikoidemo.R;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * This class is used to handle all the JSON Data
 * Created by Sakib on 1/3/2018.
 */

public final class JsonUtilsTask {
    public static ArrayList<Place> getPlaces(JSONArray placearray){
        ArrayList<Place> newplaces=new ArrayList<Place>();
        try {
            for (int i = 0; i < placearray.length(); i++) {
                JSONObject report = placearray.getJSONObject(i);
                String lon =report.has("longitude")? report.getString("longitude"):"";
                String lat =report.has("latitude")? report.getString("latitude"):"";
                String address = report.getString("Address");
                String code = report.getString("uCode");
                String area=report.has("area")? report.getString("area"):"";
                String city=report.has("city")? report.getString("city"):"";
                String postal=report.has("postCode")? report.getString("postCode"):"";
                String pType=report.has("pType")? report.getString("pType"):"";
                String subType=report.has("subType")? report.getString("subType"):"";
                String route=report.has("route_description")? report.getString("route_description"):"";
                String ward=report.has("ward")? report.getString("ward"):"";
                String zone=report.has("zone")? report.getString("zone"):"";
                String phoneNumber=report.has("contact_person_phone")? report.getString("contact_person_phone"):"";
                JSONArray images=report.has("images")?  report.getJSONArray("images"): new JSONArray();

                Place newplace = new Place(address, lon, lat, code, city, area, postal, pType, subType,phoneNumber);

                if(ward.length()>0 && !ward.equals("null")){
                    newplace.setWard(ward);
                }
                if(zone.length()>0 && !zone.equals("null")){
                    newplace.setZone(zone);
                }

                if (images.length() > 0) {
                    JSONObject image = images.getJSONObject(0);
                    newplace.setImglink(image.getString("imageLink"));
                }
                if(route.length()>0 && !route.equals("null")){
                    newplace.setRoute(route);
                }
                if(report.has("distance_in_meters")){
                    String distancestring=report.getString("distance_in_meters");
                    newplace.setDistance(Float.parseFloat(distancestring));
                }
                if(phoneNumber.length()>4){
                    newplace.setPhoneNumber(phoneNumber);
                }
                newplaces.add(newplace);
            }
        }catch (Exception e){
            Log.d("JsonUtilsTask",e.getLocalizedMessage());
        }
        return newplaces;
    }

    /**
     * Gets a place from the server
     * @param jsonObject takes a json object and structures the data and return a place
     * @return
     */
    public static Place getPlace(JSONObject jsonObject){
        try{
            String lon = jsonObject.has("longitude")?jsonObject.getString("longitude"):"";
            String lat = jsonObject.has("latitude")?jsonObject.getString("latitude"):"";
            String address = jsonObject.getString("Address");
            String code = jsonObject.getString("uCode");
            String area=jsonObject.has("area")? jsonObject.getString("area"):"";
            String city=jsonObject.has("city")? jsonObject.getString("city"):"";
            String postal=jsonObject.has("postCode")? jsonObject.getString("postCode"):"";
            String pType=jsonObject.has("pType")? jsonObject.getString("pType"):"";
            String subType=jsonObject.has("subType")? jsonObject.getString("subType"):"";
            String route=jsonObject.has("route_description")? jsonObject.getString("route_description"):"";
            String ward=jsonObject.has("ward")? jsonObject.getString("ward"):"";
            String zone=jsonObject.has("zone")? jsonObject.getString("zone"):"";
            String phoneNumber=jsonObject.has("contact_person_phone")? jsonObject.getString("contact_person_phone"):"";
            JSONArray images=jsonObject.has("images")?  jsonObject.getJSONArray("images"): new JSONArray();

            Place newplace = new Place(address, lon, lat, code, city, area, postal, pType, subType,phoneNumber);

            if(ward.length()>0 && !ward.equals("null")){
                newplace.setWard(ward);
            }
            if(zone.length()>0 && !zone.equals("null")){
                newplace.setZone(zone);
            }

            if (images.length() > 0) {
                JSONObject image = images.getJSONObject(0);
                newplace.setImglink(image.getString("imageLink"));
            }
            if(route.length()>0 && !route.equals("null")){
                newplace.setRoute(route);
            }
            if(jsonObject.has("distance_in_meters")){
                try {
                    String distancestring = jsonObject.getString("distance_in_meters");
                    newplace.setDistance(Float.parseFloat(distancestring));
                }catch(NumberFormatException e){

                }
            }
            if(phoneNumber.length()>4){
                newplace.setPhoneNumber(phoneNumber);
            }
            return newplace;

        }catch (JSONException e){
            Log.d("JsonUtilsTask",e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Handles response from the server
     * @param error
     * @param context
     */
    public static void handleResponse(VolleyError error, Context context){
        if(error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError){
            Toast.makeText(context,context.getString(R.string.couldnot_connect), Toast.LENGTH_SHORT).show();
        }
        else if(error instanceof AuthFailureError){
            Intent i=new Intent(context, MainDemoActivity.class);
            context.startActivity(i);
            ((Activity)context).finish();
        }
        else if(error instanceof ServerError || error instanceof ParseError){
            Toast.makeText(context,context.getString(R.string.prob_change_server), Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Handles response from the server and returns error message
     * @param error
     * @return String
     */
    public static String handleResponse(VolleyError error){
        if(error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError){
            return "Could not connect";
        }
        else if(error instanceof AuthFailureError){
            return "Could not Authenticate user";
        }
        else if(error instanceof ServerError || error instanceof ParseError){
            return "Problem or change in server, please wait and try again";

        }
        else{
            return "Unknown Error Occurred";
        }
    }


    public static void logResponse(VolleyError error){
        String body = "";
        //get status code here

        //get response body and parse with appropriate encoding
        if(error.networkResponse!=null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
                Log.d("error response ",body);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
    public static ArrayList<Road> getRoad(String response){
        ArrayList<Road> roads=new ArrayList<>();
        try{
            JSONArray roadArray=new JSONArray(response);
            for(int i=0;i<roadArray.length();i++){
                JSONObject road=roadArray.getJSONObject(i);
                String id=road.getString("id");
                String name_num=road.getString("road_name_number");
                String area_id=road.getString("area_id");
                String subarea_id=road.getString("subarea_id");
                String condition= road.getString("road_condition");
                int num_lanes= Integer.parseInt(road.getString("number_of_lanes"));
                String geojsonstring=road.getString("ST_AsGeoJSON(road_geometry)");
                ArrayList<LatLng> cordinates=new ArrayList<>();
                JSONObject geojson=new JSONObject(geojsonstring);
                JSONArray coordinates=geojson.getJSONArray("coordinates");

                for (int j= 0; j<coordinates.length();j++){
                    JSONArray coordinate=coordinates.getJSONArray(j);
                    Double lat= Double.parseDouble(coordinate.getString(1));
                    Double lon= Double.parseDouble(coordinate.getString(0));
                    cordinates.add(new LatLng(lat,lon));
                }
                roads.add(new Road(id,name_num,area_id,subarea_id,condition,num_lanes,cordinates));

            }
        }catch (JSONException e){
            Log.e("jsonparseerror",e.getMessage());
        }
        return roads;
    }
    public static PolylineOptions getPoly(Road road){
        ArrayList<LatLng> pointlist=road.getCoordinates();

        PolylineOptions polygonOptions = new PolylineOptions();

        for (LatLng point : pointlist) {
            polygonOptions.add(point);
        }
        if(road.getRoadCondition().contains("Good"))
            polygonOptions.color(Color.parseColor("#55AA55"));
        else if(road.getRoadCondition().contains("Bad"))
            polygonOptions.color(Color.parseColor("#D4A76A"));
        else polygonOptions.color(Color.parseColor("#D46A6A"));
        polygonOptions.width(road.getNum_of_lanes()*2);

        return polygonOptions;
    }
//    public static int mapMarker(Place place){
//        int markerid= R.drawable.others;
//        if(place.getType().toLowerCase().contains("food")) markerid= R.drawable.food_marker;
//        if(place.getType().toLowerCase().contains("religious place")) markerid=  R.drawable.religious_place;
//        if(place.getType().toLowerCase().contains("bank")) markerid=  R.drawable.bank_marker;
//        if(place.getType().toLowerCase().contains("bank")) markerid=  R.drawable.bank_marker;
//        if(place.getType().toLowerCase().contains("education")) markerid= R.drawable.education_marker;
//        if(place.getType().toLowerCase().contains("hotel")) markerid= R.drawable.hotel_marker;
//        if(place.getType().toLowerCase().contains("fuel")) markerid= R.drawable.fuel;
//        if(place.getSubType().toLowerCase().contains("public toilet")) markerid= R.drawable.others;
//        if(place.getSubType().toLowerCase().contains("hospital")) markerid= R.drawable.healthcare;
//        if(place.getSubType().toLowerCase().contains("pharmacy")) markerid= R.drawable.healthcare;
//        if(place.getSubType().toLowerCase().contains("police station")) markerid= R.drawable.government;
//        if(place.getSubType().toLowerCase().contains("bkash")) markerid= R.drawable.shop;
//        if(place.getSubType().toLowerCase().contains("ucash")) markerid= R.drawable.shop;
//        if(place.getSubType().toLowerCase().contains("sureCash")) markerid= R.drawable.shop;
//        if(place.getSubType().toLowerCase().contains("general store")) markerid= R.drawable.shop;
//        if(place.getSubType().toLowerCase().contains("market")) markerid= R.drawable.shop;
//        if(place.getSubType().toLowerCase().contains("parking")) markerid= R.drawable.parkinggarage;
//
//        return markerid;
//    }
}
