package com.barikoi.barikoidemo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barikoi.barikoidemo.Model.Place;
import com.barikoi.barikoidemo.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Telemetry;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerOptions;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import barikoi.barikoilocation.PlaceModels.ReverseGeoPlace;
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPI;
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPIListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback,
        PermissionsListener, LocationEngineListener {

    EditText etInputSource, etInputDestination;
    ImageView imageBack;
    String text;
    String getCurrentAddress;
    Double getLat, getLng, sourceLat, sourceLon, destLat, destLon, currentLat, currentLon;
    ReverseGeoAPIListener reverseGeoAPIListener;
    BottomSheetBehavior mBottomSheetNavigation;
    View nestedScrollView;
    Button buttonStartNavigation;
    TextView tvdistance, tvduration;
    TextInputLayout textInputSource, textInputDest;
    FloatingActionButton fab;
    FirebaseAnalytics firebaseAnalytics;
    private MapView mapView;
    private MapboxMap mMap;
    private LocationManager locationManager;
    private PermissionsManager permissionsManager;
    private LocationEngineProvider locationEngineProvider;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationPlugin;
    private Point originPoint, destinationPoint;
    private Location originLocation;
    private Marker originMarker, destinationMarker;
    private NavigationRoute.Builder navroute;
    private NavigationMapRoute navigationMapRoute;
    private DirectionsRoute currentRoute;
    private static final int requestCode = 555;
    private static final String TAG = "Navigation";

    @SuppressLint({"ClickableViewAccessibility", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Mapbox.getInstance(NavigationActivity.this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_navigation);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Telemetry.disableOnUserRequest();
        mapView = findViewById(R.id.mapview2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setStyleUrl(getString(R.string.map_view_styleUrl));


        nestedScrollView = findViewById(R.id.bottomsheet_navigation);
        tvdistance = findViewById(R.id.tvdistance);
        tvduration = findViewById(R.id.tvduration);
        buttonStartNavigation = findViewById(R.id.buttonStartNavigation);
        //fab = findViewById(R.id.fab);
        textInputSource = findViewById(R.id.textInputSource);
        textInputDest = findViewById(R.id.textInputDest);

        InitBottomSheetNavigation();
//        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);

        getCurrentAddress = String.valueOf(getIntent().getSerializableExtra("location"));
        getLat = getIntent().getDoubleExtra("lat", 1);
        getLng = getIntent().getDoubleExtra("lng", 1);
        Log.d(TAG, "getLat: " + getLat + " getLng: " + getLng);

        etInputSource = findViewById(R.id.input_source);
        etInputDestination = findViewById(R.id.input_destination);
        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearMap();
                onBackPressed();

            }
        });
        //init();


        etInputSource.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(NavigationActivity.this, SearchPlaceActivity.class);
                    intent.putExtra("key", "navigationActivity");
                    //intent.putExtra("input", "source");
                    intent.putExtra("lat", getLat);
                    intent.putExtra("lng", getLng);
                    intent.putExtra("SOURCE", 1);
                    startActivityForResult(intent, requestCode);
                    return true;
                }
                return false;
            }
        });

        etInputDestination.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    Intent intent = new Intent(NavigationActivity.this, SearchPlaceActivity.class);
                    intent.putExtra("key", "navigationActivity");
                    //intent.putExtra("input", "destination");
                    intent.putExtra("lat",getLat);
                    intent.putExtra("lng", getLng);
                    intent.putExtra("DESTINATION", 1);
                    startActivityForResult(intent, requestCode);

                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        mMap = mapboxMap;
        //mMap.setStyle(Style.MAPBOX_STREETS);

       //mMap.setStyleUrl(getString(R.string.map_view_styleUrl));
        enableLocation();

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (locationEngine != null) {
//                    @SuppressLint("MissingPermission")
//                    Location lastLocation = locationEngine.getLastLocation();
//                    if (lastLocation != null) {
//                        originLocation = lastLocation;
//                        setCameraPosition(new LatLng(originLocation.getLatitude(), originLocation.getLongitude()), 17.0);
//                    } else {
//                        locationEngine.requestLocationUpdates();
//                    }
//                } else {
//                    enableLocation();
//                }
//            }
//        });
//        mMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style2) {
//                style = style2;
//                enableLocationComponent();
//
//            }
//        });
//        mMap.setStyle(new Style.Builder().fromUrl(getString(R.string.map_view_styleUrl)), new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style2) {
//
//                style = style2;
//                enableLocationComponent();
//            }
//        });
        //mMap.setStyle(new Style.Builder().fromUrl(getString(R.string.map_view_styleUrl)));
        //enableLocationComponent();

    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        if (locationPlugin == null) {
            locationPlugin = new LocationLayerPlugin(mapView, mMap, locationEngine, LocationLayerOptions.builder(this).maxZoom(25.0).build());
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        }
        // currentLocation.displayLocation();
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 17.0);
            locationEngine.removeLocationUpdates();
        } else {
            locationEngine.addLocationEngineListener(NavigationActivity.this);
        }

    }

    private void setCameraPosition(LatLng location, Double zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom));
    }

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            //Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

//    @SuppressWarnings( {"MissingPermission"})
//    private void enableLocationComponent() {
//
//        // Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//
//            // Get an instance of the component
//            LocationComponent locationComponent = mMap.getLocationComponent();
//
//            // Activate with a built LocationComponentActivationOptions object
//            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build());
//
//            // Enable to make component visible
//            locationComponent.setLocationComponentEnabled(true);
//
//            // Set the component's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//
//            // Set the component's render mode
//            locationComponent.setRenderMode(RenderMode.COMPASS);
//            locationComponent.getLocationEngine().getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
//                @Override
//                public void onSuccess(LocationEngineResult result) {
//
//                    currentLat = result.getLastLocation().getLatitude();
//                    currentLon = result.getLastLocation().getLongitude();
//
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLon),17.0));
//
//                }
//
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//
//                }
//            });
//
////            fab.setOnClickListener {
////                locationComponent?.locationEngine?.getLastLocation(object : LocationEngineCallback<LocationEngineResult>{
////                    override fun onSuccess(result: LocationEngineResult?) {
////                        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result?.lastLocation!!.latitude,result.lastLocation!!.longitude),17.0))
////
////                    }
////
////                    override fun onFailure(exception: Exception) {
////
////                    }
////
////                })
////            }
//
//        } else {
//
//            permissionsManager = new PermissionsManager(this);
//
//            permissionsManager.requestLocationPermissions(this);
//
//        }
//    }

    public void init() {


        ReverseGeoAPI.builder(NavigationActivity.this)
                .setLatLng(getLat, getLng)
                .build()
                .getAddress(reverseGeoAPIListener = new ReverseGeoAPIListener() {
                    @Override
                    public void reversedAddress(ReverseGeoPlace place) {
                        etInputSource.setText(place.toString());
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(NavigationActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void InitBottomSheetNavigation() {

        mBottomSheetNavigation = BottomSheetBehavior.from(nestedScrollView);
        mBottomSheetNavigation.isHideable();
        mBottomSheetNavigation.setState(BottomSheetBehavior.STATE_HIDDEN);
        //BottomSheetDown();
    }

    public void BottomSheetDown() {
      if (mBottomSheetNavigation.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetNavigation.isHideable();
            mBottomSheetNavigation.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void BottomSheetBehavior(int bottmSheetId) {

        switch (bottmSheetId){
            case R.id.bottomsheet_navigation:
                mBottomSheetNavigation.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheetNavigation.setHideable(false);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "requestCode: "+requestCode);
        if (requestCode == this.requestCode) {
            if(resultCode == Activity.RESULT_OK){

                Place place = (Place) data.getSerializableExtra("result");
                Log.d(TAG, "place: " +place);

                Log.d(TAG, "return source: " +data.hasExtra("RSOURCE"));
                Log.d(TAG, "return destination: " +data.hasExtra("RDESTINATION"));

                if (data.hasExtra("RSOURCE")){
                    etInputSource.setText(place.toString());
                    if(place.getLat() != null && place.getLon() != null) {
                        sourceLat = Double.valueOf(place.getLat());
                        sourceLon = Double.valueOf(place.getLon());

                        originPoint = Point.fromLngLat(sourceLon, sourceLat);

                        originMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(sourceLat, sourceLon)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sourceLat, sourceLon), 16.0));
                    }
                    ClearMap();
                }
                else if(data.hasExtra("RDESTINATION")){
                    etInputDestination.setText(place.toString());
                    if(place.getLat() != null && place.getLon() != null) {
                        destLat = Double.valueOf(place.getLat());
                        destLon = Double.valueOf(place.getLon());
                        destinationPoint = Point.fromLngLat(destLon, destLat);
                        destinationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(destLat, destLon)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(destLat, destLon), 16.0));
                    }
                    ClearMap();

                    //getRoute(originPoint, destinationPoint);
                    //BottomSheetBehavior(R.id.bottomsheet_navigation);
                }

                if (validateSource() && validateDest()) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(sourceLat, sourceLon)));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(destLat, destLon)));
                    getRoute(originPoint, destinationPoint);

                }else {
                    //return;
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

    private boolean validateSource() {
        String sourceInput = textInputSource.getEditText().getText().toString().trim();
        if (sourceInput.isEmpty()) {
            textInputSource.setError("Input Source Location");
            return false;
        } else {
            textInputSource.setError(null);
            return true;
        }
    }


    private boolean validateDest() {
        String destInput = textInputDest.getEditText().getText().toString().trim();
        if (destInput.isEmpty()) {
            textInputDest.setError("Input Destination Location");
            return false;
        } else {
            textInputDest.setError(null);
            return true;
        }
    }

    private void ClearMap() {
        if (mMap != null)
            mMap.clear();
        if (destinationMarker != null) {
            mMap.removeMarker(destinationMarker);
        }
        if (originMarker != null) {
            mMap.removeMarker(originMarker);
        }
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        }
    }


    private void getRoute(Point origin, Point destination) {

        Log.d(TAG, "origin: " +origin.toString() + " Dest: " +destination.toString());

//        navroute = NavigationRoute.builder(this)
//                .accessToken(getString(R.string.access_token))
//                .origin(origin)
//                .destination(destination);

        navroute = NavigationRoute.builder(this)
                .accessToken("pk." + getString(R.string.gh_key))
                .baseUrl(getString(R.string.base_url))
                .user("gh")
                .origin(origin)
                .destination(destination)
                .alternatives(true);

        navroute.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                try {

                Log.d(TAG, "Response raw: " + response.raw().toString());
                Log.d(TAG, "Response body: " + response.body());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");

                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG, "No routes found");

                }
                    DirectionsRoute currentRoute = response.body().routes().get(0);
                    double distance = currentRoute.distance()/ 1000;
                    DecimalFormat df = new DecimalFormat("#.##");
                    int duration;
                    if (currentRoute.duration()/ 60 > 10) {
                        duration = (int) (currentRoute.duration()/ 60 * 1.5);
                    } else
                    duration = (int) (currentRoute.duration()/ 60 * 1.2);
                    df.setRoundingMode(RoundingMode.CEILING);
                    tvdistance.setText(getString(R.string.distance) + " " + df.format(distance).toString() + " " + getString(R.string.km));
                    tvduration.setText(getString(R.string.time) + " " + duration + " " + getString(R.string.mins));
                    // Draw the route on the map
                    if (navigationMapRoute != null) {
                        navigationMapRoute.removeRoute();
                    } else {
                        navigationMapRoute = new NavigationMapRoute(null, mapView, mMap, R.style.NavigationMapRoute);
                    }
                    if (mBottomSheetNavigation.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        List<Point> routepoints = currentRoute.routeOptions().coordinates();
                        LatLngBounds nearbybound = new LatLngBounds.Builder()
                                .include(new LatLng(origin.latitude(), origin.longitude()))
                                .include(new LatLng(destination.latitude(), destination.longitude()))
                                .build();

                        for (Point p : routepoints) {
                            nearbybound.include(new LatLng(p.latitude(), p.longitude()));
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(nearbybound, 20, mBottomSheetNavigation.getPeekHeight(), 20, mBottomSheetNavigation.getPeekHeight()));
                        navigationMapRoute.addRoute(currentRoute);
                        mMap.animateCamera(CameraUpdateFactory.zoomOut());

//                        buttonStartNavigation.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                try {
//                                    Boolean simulateRoute = false;
//                                    NavigationLauncherOptions options = NavigationLauncherOptions.builder()
//                                            .directionsRoute(currentRoute)
//                                            .shouldSimulateRoute(simulateRoute)
//                                            .build();
//                                    // Call this method with Context from within an Activity
//                                    NavigationLauncher.startNavigation(NavigationActivity.this, options);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
                    }

                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {
            Log.d("perssion check", "mapbox location got permission");
            enableLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {

        locationEngine.requestLocationUpdates();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        Log.d("OnLoc", "Onlocchanged");
        if (location != null) {
            originLocation = location;
            setCameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 17.0);
            if (mMap != null) /*IntentDataCheck()*/
                locationEngine.removeLocationEngineListener(this);
        } else {
            locationEngine.requestLocationUpdates();
        }

    }

}
