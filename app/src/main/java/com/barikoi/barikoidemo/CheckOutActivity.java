package com.barikoi.barikoidemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import barikoi.barikoilocation.PlaceModels.GeoCodePlace;
import barikoi.barikoilocation.PlaceModels.ReverseGeoPlace;
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPI;
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPIListener;
import barikoi.barikoilocation.SearchAutoComplete.SearchAutoCompleteActivity;
import barikoi.barikoilocation.SearchAutoComplete.SearchAutocompleteFragment;

public class CheckOutActivity extends AppCompatActivity {

    EditText inputAddress, area, city, zipCode;
    Spinner countryList;
    Button confirm;
    ImageView imageView;
    String getCurrentAddress;
    Double getLat, getLng;
    ReverseGeoAPIListener reverseGeoAPIListener;
    ProgressBar progressBar;
    private static final int requestCode=555;
    private static final String TAG = "Checkout";

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        inputAddress = findViewById(R.id.input_address);
        area = findViewById(R.id.input_Area);
        city = findViewById(R.id.input_City);
        zipCode = findViewById(R.id.input_ZipCode);
        confirm = findViewById(R.id.btn_confirm);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progress);

        //countryList = findViewById(R.id.countryList);

        //countryList.setSelected(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        inputAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getAddress = null;
                Intent intent=new Intent(CheckOutActivity.this, SearchAutoCompleteActivity.class);
                startActivityForResult(intent,requestCode);

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(CheckOutActivity.this, "Successfully Checkout", Toast.LENGTH_LONG).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(CheckOutActivity.this, MainDemoActivity.class);
//                intent.putExtra("key", "fromCheckout");
//                startActivity(intent);

                //getAddress = (Location) getIntent().getSerializableExtra("location");
                progressBar.setVisibility(View.VISIBLE);
                getCurrentAddress = String.valueOf(getIntent().getSerializableExtra("location"));
                getLat = getIntent().getDoubleExtra("lat", 1);
                getLng = getIntent().getDoubleExtra("lng", 1);
                Log.d(TAG, "getLat: " +getLat+ " getLng: " +getLng);
                ReverseGeoAPI.builder(CheckOutActivity.this)
                        .setLatLng(getLat, getLng)
                        .build()
                        .getAddress(reverseGeoAPIListener = new ReverseGeoAPIListener() {
                            @Override
                            public void reversedAddress(ReverseGeoPlace place) {
                                progressBar.setVisibility(View.GONE);
                                inputAddress.setText(place.toString());
                                area.setText(place.getArea());
                                city.setText(place.getCity());
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(CheckOutActivity.this,message,Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "requestCode: "+requestCode);
        if (requestCode == this.requestCode) {
            if(resultCode == Activity.RESULT_OK){

                GeoCodePlace place = (GeoCodePlace) data.getSerializableExtra("place_selected");
                Log.d(TAG, "selected place: " +place.toString());
                inputAddress.setText(place.toString());
                area.setText(place.getArea());
                city.setText(place.getCity());
                zipCode.setText(place.getPostalcode());

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
