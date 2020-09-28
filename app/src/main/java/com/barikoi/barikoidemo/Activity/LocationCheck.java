package com.barikoi.barikoidemo.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.barikoi.barikoidemo.R;

public class LocationCheck extends AppCompatActivity {

    private TextView cell_id, cell_lac;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_check);

        cell_id = findViewById(R.id.tv_cellId);
        cell_lac = findViewById(R.id.tv_cellLac);

        TelephonyManager m_manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

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
            }
            if (loc.getLac() == -1) {
                Toast.makeText(getApplicationContext(), "Cell LAC unknown",Toast.LENGTH_SHORT).show();
            } else {
                cell_lac.setText(String.valueOf(loc.getLac()));
            }
        }

//        // Acquire a reference to the system Location Manager
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//
//// Define a listener that responds to location updates
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
////                makeUseOfNewLocation(location);
//                Log.d("Locationcheck", "location: " + location);
//                Log.d("Locationcheck", "location lat: " + location.getLatitude());
//                Log.d("Locationcheck", "location lon: " + location.getLongitude());
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            public void onProviderEnabled(String provider) {
//            }
//
//            public void onProviderDisabled(String provider) {
//            }
//        };
//
//// Register the listener with the Location Manager to receive location updates
//        if (locationManager != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    Activity#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for Activity#requestPermissions for more details.
//                    return;
//                }
//            }
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//        }
    }
}
