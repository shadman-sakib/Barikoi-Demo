package com.barikoi.barikoidemo

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import barikoi.barikoilocation.PlaceModels.GeoCodePlace
import barikoi.barikoilocation.SearchAutoComplete.SearchAutocompleteFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.bottomsheet_placeview.*
import kotlinx.android.synthetic.main.content_main_demo.*
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import barikoi.barikoilocation.BarikoiAPI
import barikoi.barikoilocation.JsonUtils
import barikoi.barikoilocation.NearbyPlace.NearbyPlaceAPI
import barikoi.barikoilocation.NearbyPlace.NearbyPlaceListener
import barikoi.barikoilocation.PlaceModels.NearbyPlace
import barikoi.barikoilocation.PlaceModels.ReverseGeoPlace
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPI
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPIListener
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.barikoi.barikoi.Models.RequestQueueSingleton
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.bottomsheet_nearbylist.*
import kotlinx.android.synthetic.main.bottomsheet_rupantor.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap


class MainDemoActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {


    private val placemarkermap: HashMap<String,Marker>?=HashMap()
    private var iconFactory: IconFactory?=null
    private var permissionsManager: PermissionsManager?=null
    private var map: MapboxMap?=null
    private var mBottomSheetBehaviorplaceview: BottomSheetBehavior<View>?=null
    private var mBottomSheetBehaviorRupantor: BottomSheetBehavior<LinearLayout>?=null
    private var mBottomSheetBehaviorNearby: BottomSheetBehavior<LinearLayout>?=null
    var types= arrayOf("Bank","Education","Food","Fuel","Government","Healthcare","Hotel","Shop","Utility")
    private var nearbyadapter: PlaceListAdapter?=null
    //private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_demo)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId){
                R.id.nav_search-> initSearchautocomplete()
                R.id.nav_reversegeo-> initReversegeo()
                R.id.nav_rupantor -> initRupantor()
                R.id.nav_nearby -> initNearby()
                R.id.nav_checkout -> 
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        initViews()
        initSearchautocomplete()
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        Log.d("mainactivitydemo","map ready")
        map = mapboxMap

        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent()

        }

    }

    /**
     * This function checks for location permission, if granted initializes location plugin
     */
    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent() {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            val locationComponent = map?.locationComponent

            // Activate with a built LocationComponentActivationOptions object
            locationComponent?.activateLocationComponent(LocationComponentActivationOptions.builder(this, map!!.style!!).build())

            // Enable to make component visible
            locationComponent?.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent?.cameraMode = CameraMode.TRACKING

            // Set the component's render mode
            locationComponent?.renderMode = RenderMode.COMPASS
            locationComponent?.locationEngine?.getLastLocation(object : LocationEngineCallback<LocationEngineResult>{
                override fun onSuccess(result: LocationEngineResult?) {
                    map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result?.lastLocation!!.latitude,result.lastLocation!!.longitude),17.0))

                }

                override fun onFailure(exception: Exception) {

                }

            })
            fab.visibility= VISIBLE
            fab.setOnClickListener {
                locationComponent?.locationEngine?.getLastLocation(object : LocationEngineCallback<LocationEngineResult>{
                    override fun onSuccess(result: LocationEngineResult?) {
                        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result?.lastLocation!!.latitude,result.lastLocation!!.longitude),17.0))

                    }

                    override fun onFailure(exception: Exception) {

                    }

                })
            }

        } else {
            Log.d("mainactivitydemo","location permission not granted")
            permissionsManager = PermissionsManager(this)

            permissionsManager?.requestLocationPermissions(this)

        }

    }

   

    private fun initViews(){
        mBottomSheetBehaviorplaceview = BottomSheetBehavior.from(bottomsheet_placeview)
        mBottomSheetBehaviorNearby = BottomSheetBehavior.from(bottomsheet_nearby)
        mBottomSheetBehaviorRupantor = BottomSheetBehavior.from(bottomsheet_rupantor)
        mBottomSheetBehaviorNearby?.peekHeight=500
        mBottomSheetBehaviorRupantor?.peekHeight=200
        nearbyadapter=PlaceListAdapter(ArrayList<NearbyPlace>(), object : PlaceListAdapter.OnPlaceItemSelectListener{
            override fun onPlaceItemSelected(mItem: NearbyPlace?, position: Int) {
                placemarkermap?.get(mItem!!.code)?.showInfoWindow(map!!,mapView)
            }
        })
        nearbylistview.layoutManager=LinearLayoutManager(this)
        nearbylistview.adapter=nearbyadapter

        rupantor_button.setOnClickListener { v ->
            val searchtext= rupantor_searchtext.text
            rupantor(searchtext.toString())
        }
        iconFactory = IconFactory.getInstance(this@MainDemoActivity)
        clearmode()
        loadnearbytypes()

    }
    private fun initNearby() {
        clearmode()
        mBottomSheetBehaviorNearby!!.state=BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetBehaviorNearby!!.isHideable=false
        getnearby(nearbytypespineer.selectedItem.toString())

        nearbytypespineer.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long){
                getnearby(types[position])
            }
        })
    }
    private fun getnearby(type : String){
        map?.locationComponent!!.locationEngine?.getLastLocation(object :LocationEngineCallback<LocationEngineResult>{
            override fun onSuccess(result: LocationEngineResult?) {
                NearbyPlaceAPI.builder(this@MainDemoActivity)
                    .setDistance(2.0)
                    .setLimit(10)
                    .setType(type)
                    .setLatLng(result?.lastLocation!!.latitude,result.lastLocation!!.longitude)
                    .build()
                    .generateNearbyPlaceListByType(object: NearbyPlaceListener{
                        override fun onPlaceListReceived(places: java.util.ArrayList<NearbyPlace>?) {
                            plotmarkers(places!!,result.lastLocation!!.latitude,result.lastLocation!!.longitude)
                            nearbyadapter?.setplaces(places)
                        }
                        override fun onFailure(message: String?) {
                            Toast.makeText(this@MainDemoActivity,"could not get nearby places",Toast.LENGTH_SHORT).show()
                        }

                    })
            }

            override fun onFailure(exception: Exception) {
                Toast.makeText(this@MainDemoActivity,"could not get location",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initRupantor() {
        clearmode()
        mBottomSheetBehaviorRupantor!!.state=BottomSheetBehavior.STATE_EXPANDED
        mBottomSheetBehaviorRupantor!!.isHideable=false
    }

    private fun initReversegeo() {
        clearmode()
        map_pointer.visibility= VISIBLE
        mBottomSheetBehaviorplaceview!!.state=BottomSheetBehavior.STATE_EXPANDED
        map?.addOnCameraIdleListener (maprevgeolistener)
    }
    val maprevgeolistener= object: MapboxMap.OnCameraIdleListener {
        override fun onCameraIdle() {
            progress.visibility= VISIBLE
            ReverseGeoAPI.builder(this@MainDemoActivity)
                .setLatLng(map?.cameraPosition!!.target.latitude,map?.cameraPosition!!.target.longitude)
                .build()
                .getAddress(object: ReverseGeoAPIListener {
                    override fun onFailure(message: String?) {
                        progress.visibility=GONE
                        Toast.makeText(this@MainDemoActivity,message,Toast.LENGTH_LONG).show()
                    }

                    override fun reversedAddress(place: ReverseGeoPlace?) {
                        progress.visibility=GONE
                        textview_address.text=place?.address
                        textview_area.text=place?.area
                    }

                })
        }
    }
    private fun loadnearbytypes(){
        /*val typesreq= object: StringRequest(
            Method.GET,
            "https://admin.barikoi.xyz/v1/place/get/type",
            {response: String ->
                try {
                    val typearray= JSONArray(response)

                    for (i in 0..typearray.length()){
                    }
                }catch (e: JSONException) {
                    try {
                        val data = JSONObject(response)
                        Toast.makeText(this, data.getString("Message"), Toast.LENGTH_SHORT).show()
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }

                }

            },{ error ->
                JsonUtils.logResponse(error)

                Toast.makeText(this@MainDemoActivity, "Not found", Toast.LENGTH_SHORT).show()
            }){}
        RequestQueueSingleton.getInstance(this).requestQueue!!.add(typesreq)*/

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,types )
        nearbytypespineer.adapter = adapter

    }

    private fun rupantor(searchtext: String){
        progress.visibility= VISIBLE
        resultpane.visibility= INVISIBLE
        val rupantorrequest= object : StringRequest(
            Request.Method.POST,
            "https://barikoi.xyz/v1/api/search/"+BarikoiAPI.getAccessToken()+"/rupantor/geocode",
            { response: String ->
                progress.visibility=GONE
                try {
                    val data = JSONObject(response)
                    val placearray = data.getJSONObject("geocoded_address")

                    val place = JsonUtils.getGeoCodePlace(placearray)

                    fixed_addresstext.text=data.getString("fixed_address")
                    rupantor_addresstext.text=place.address
                    rupantor_type.text=place.type
                    rupantor_ucodetext.text=place.code
                    rupantor_status.text=data.getString("address_status")
                    resultpane.visibility= VISIBLE
                    plotmarker(place)
                } catch (e: JSONException) {
                    progress.visibility=GONE
                    try {
                        val data = JSONObject(response)
                        Toast.makeText(this, data.getString("Message"), Toast.LENGTH_SHORT).show()
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }

                }
            },
            { error ->
                progress.visibility=GONE
                JsonUtils.logResponse(error)

                Toast.makeText(this@MainDemoActivity, "Not found", Toast.LENGTH_SHORT).show()
            }) {


            public override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["q"] = searchtext
                return params
            }
        }
        rupantorrequest.tag = "rupantor"
        RequestQueueSingleton.getInstance(this).requestQueue!!.add(rupantorrequest)
    }

    private fun plotmarker(p: GeoCodePlace){
        placemarkermap?.clear()
        map?.clear()
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p.latitude.toDouble(), p.longitude.toDouble()),17.0))
        val m = map!!.addMarker(
            MarkerOptions().position(
                LatLng(
                    p.latitude.toDouble(),
                    p.longitude.toDouble()
                )
            ).title( p.getAddress())
        )
        placemarkermap?.put(p.code, m)
    }
    private fun plotmarkers(places: List<NearbyPlace>, latitude: Double , longitude: Double ){
        placemarkermap?.clear()
        map?.clear()
        val latlngboundsbuilder= LatLngBounds.Builder()
        for( p in places){
            val m = map!!.addMarker(
                MarkerOptions().position(
                    LatLng(
                        p.latitude.toDouble(),
                        p.longitude.toDouble()
                    )
                ).title( p.getAddress())
            )
            latlngboundsbuilder.include(LatLng(p.latitude.toDouble(),p.longitude.toDouble()))
            placemarkermap?.put(p.code, m)
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude),16.0))
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngboundsbuilder.build(),30))
    }
    private fun clearmode() {
        mBottomSheetBehaviorplaceview!!.isHideable=true
        mBottomSheetBehaviorplaceview!!.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehaviorNearby!!.isHideable=true
        mBottomSheetBehaviorNearby!!.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehaviorRupantor!!.isHideable=true
        mBottomSheetBehaviorRupantor!!.state = BottomSheetBehavior.STATE_HIDDEN
        autocompletepane.visibility=GONE
        map_pointer.visibility=GONE
        placemarkermap?.clear()
        map?.clear()
        map?.removeOnCameraIdleListener(maprevgeolistener)
    }

    private fun initSearchautocomplete(){
        clearmode()
        autocompletepane.visibility= VISIBLE
        Log.d("mainactivitydemo","autocomplete initiated")
        val searchAutocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.barikoiSearchAutocompleteFragment) as SearchAutocompleteFragment?
        searchAutocompleteFragment!!.setPlaceSelectionListener(object :
            SearchAutocompleteFragment.PlaceSelectionListener {
            override fun onPlaceSelected(place: GeoCodePlace?) {
                mBottomSheetBehaviorplaceview!!.isHideable = false
                mBottomSheetBehaviorplaceview!!.state = BottomSheetBehavior.STATE_EXPANDED
                plotmarker(place!!)
                textview_address.text=place.address
                textview_area.text=place.area

            }
            override fun onFailure(error: String) {

            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_demo, menu)
        return true
    }
    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {

        mapView!!.onStop()
        super.onStop()
    }

    override fun onDestroy() {

        mapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onPermissionResult(granted: Boolean) {
        enableLocationComponent()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {


        } else {
            enableLocationComponent()
        }

    }
   /* override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
}
