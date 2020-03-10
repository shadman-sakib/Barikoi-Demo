package com.barikoi.barikoidemo.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

import barikoi.barikoilocation.GeoCode.GeoCodeAPI
import barikoi.barikoilocation.GeoCode.PlaceGeoCodeListener
import barikoi.barikoilocation.NearbyPlace.NearbyPlaceAPI
import barikoi.barikoilocation.NearbyPlace.NearbyPlaceListener
import barikoi.barikoilocation.PlaceModels.GeoCodePlace
import barikoi.barikoilocation.PlaceModels.NearbyPlace
import barikoi.barikoilocation.PlaceModels.ReverseGeoPlace
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPI
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPIListener
import barikoi.barikoilocation.SearchAutoComplete.SearchAutocompleteFragment
import com.barikoi.barikoidemo.R


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var searchAutocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as SearchAutocompleteFragment?
        searchAutocompleteFragment!!.setPlaceSelectionListener(object :
            SearchAutocompleteFragment.PlaceSelectionListener {
            override fun onPlaceSelected(place: GeoCodePlace?) {
                Toast.makeText(this@MainActivity, "" + place!!.address,Toast.LENGTH_LONG).show()
            }
            override fun onFailure(error: String) {

            }
        })
        reversegeo.setOnClickListener {
            ReverseGeoAPI.builder(applicationContext)
                .setLatLng(
                    java.lang.Double.parseDouble(lat.getText().toString()),
                    java.lang.Double.parseDouble(lon.getText().toString())
                )
                .build()
                .getAddress(object : ReverseGeoAPIListener {
                    override fun reversedAddress(place: ReverseGeoPlace?) {
                        Toast.makeText(
                            this@MainActivity,
                            "" + place!!.getAddress(),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ReverseGeoPlace", "" + place.getAddress())
                    }

                    override fun onFailure(message: String) {

                    }
                })
        }
        nearby.setOnClickListener {
            NearbyPlaceAPI.builder(applicationContext)
                .setDistance(.5)
                .setLimit(10)
                .setLatLng(
                    java.lang.Double.parseDouble(lat.getText().toString()),
                    java.lang.Double.parseDouble(lon.getText().toString())
                )
                .build()
                .generateNearbyPlaceList(object : NearbyPlaceListener {
                    override fun onPlaceListReceived(places: ArrayList<NearbyPlace>?) {
                        Toast.makeText(
                            this@MainActivity,
                            "" + places!![0].getAddress(),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Nearby", "" + places!!.size)
                    }
                    override fun onFailure(message: String) {

                    }
                })
        }
        geoCode.setOnClickListener {
            GeoCodeAPI.builder(applicationContext)
                .idOrCode(geoId.getText().toString())
                .build()
                .generateList(object : PlaceGeoCodeListener {
                    override fun onGeoCodePlace(place: GeoCodePlace?) {
                        Toast.makeText(
                            this@MainActivity,
                            "" + place!!.getAddress(),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("onGeoCodePlace", "" + place.getAddress())
                    }

                    override fun onFailure(message: String) {
                        Toast.makeText(this@MainActivity, "" + message, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }

    }
}
