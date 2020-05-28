package com.barikoi.barikoidemo.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import barikoi.barikoilocation.PlaceModels.GeoCodePlace;
import barikoi.barikoilocation.PlaceModels.ReverseGeoPlace;
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPI;
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPIListener;
import barikoi.barikoilocation.SearchAutoComplete.SearchAutoCompleteActivity;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

public class CheckOutActivity extends AppCompatActivity {

    EditText etInputAddress, etArea, etCity, etZipCode;
    Spinner countryList;
    String text;
    Button confirm;
    ImageView imageView;
    String getCurrentAddress;
    Double getLat, getLng;
    ReverseGeoAPIListener reverseGeoAPIListener;
    ProgressBar progressBar;


    private static final int requestCode = 555;
    private static final String TAG = "Checkout";

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        etInputAddress = findViewById(R.id.input_address);
        etArea = findViewById(R.id.input_Area);
        etCity = findViewById(R.id.input_City);
        etZipCode = findViewById(R.id.input_ZipCode);
        confirm = findViewById(R.id.btn_confirm);
        progressBar = findViewById(R.id.progress);

        getCurrentAddress = String.valueOf(getIntent().getSerializableExtra("location"));
        getLat = getIntent().getDoubleExtra("lat", 1);
        getLng = getIntent().getDoubleExtra("lng", 1);
        Log.d(TAG, "getLat: " + getLat + " getLng: " + getLng);


        //countryList = findViewById(R.id.countryList);

        //countryList.setSelected(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etInputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Intent intent = new Intent(CheckOutActivity.this, SearchAutoCompleteActivity.class);

                    etInputAddress.setText("");
                    etArea.setText("");
                    etCity.setText("");
                    etZipCode.setText("");
                    Intent intent = new Intent(CheckOutActivity.this, SearchPlaceActivity.class);
                    intent.putExtra("key", "checkoutactivity");
                    intent.putExtra("lat",getLat);
                    intent.putExtra("lng",getLng);
                    startActivityForResult(intent, requestCode);

                }
            }
        });

        etInputAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    final int DRAWABLE_RIGHT = 2;
                    if (etInputAddress.getCompoundDrawables()[DRAWABLE_RIGHT]!=null)
                    if(event.getRawX() >= etInputAddress.getRight() - etInputAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        // your action for drawable click event

                        etInputAddress.setText("");
                        etArea.setText("");
                        etCity.setText("");
                        etZipCode.setText("");

                        progressBar.setVisibility(View.VISIBLE);

                        ReverseGeoAPI.builder(CheckOutActivity.this)
                                .setLatLng(getLat, getLng)
                                .build()
                                .getAddress(reverseGeoAPIListener = new ReverseGeoAPIListener() {
                                    @Override
                                    public void reversedAddress(ReverseGeoPlace place) {
                                        progressBar.setVisibility(View.GONE);
                                        etInputAddress.setText(place.toString());
                                        etArea.setText(place.getArea());
                                        etCity.setText(place.getCity());
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_LONG).show();
                                    }
                                });


                        return true;
                    }
                }
                return false;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(CheckOutActivity.this, "Successfully Checkout", Toast.LENGTH_SHORT).show();
            }
        });




    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "requestCode: "+requestCode);
        if (requestCode == this.requestCode) {
            if(resultCode == Activity.RESULT_OK){

                String places = data.getStringExtra("suggestions");
                Log.d(TAG, "selected place: " +places);

                if (places != null && !places.equals("null")) {
                    etInputAddress.setText(places);
                }else{
                    Place place = (Place) data.getSerializableExtra("result");
                    Log.d(TAG, "selected place: " +place.toString());
                    etInputAddress.setText(place.toString());
                    etArea.setText(place.getArea());
                    etCity.setText(place.getCity());
                    Log.d(TAG, "Zip Code: " + place.getPostalcode());
                    if (!place.getPostalcode().equals("null")) {
                        etZipCode.setText(place.getPostalcode());
                    } else {
                        etZipCode.setText(" ");
                    }

                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                if(data!=null){
                    String error=data.getStringExtra("error");
                    Log.d(TAG,"Error: " +error);
                }
                else{

                }
                //Write your code if there's no result
            }
        }
    }
}
