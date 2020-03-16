package com.barikoi.barikoidemo.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.*
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import barikoi.barikoilocation.PlaceModels.GeoCodePlace
import barikoi.barikoilocation.SearchAutoComplete.SearchAutocompleteFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.bottomsheet_placeview.*
import kotlinx.android.synthetic.main.content_main_demo.*
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import barikoi.barikoilocation.BarikoiAPI
import barikoi.barikoilocation.JsonUtils
import barikoi.barikoilocation.NearbyPlace.NearbyPlaceAPI
import barikoi.barikoilocation.NearbyPlace.NearbyPlaceListener
import barikoi.barikoilocation.PlaceModels.NearbyPlace
import barikoi.barikoilocation.PlaceModels.ReverseGeoPlace
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPI
import barikoi.barikoilocation.ReverseGeo.ReverseGeoAPIListener
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.barikoi.barikoi.Models.RequestQueueSingleton
import com.barikoi.barikoidemo.Adapter.PlaceAddressAdapter
import com.barikoi.barikoidemo.Adapter.TypeListAdapter
import com.barikoi.barikoidemo.Fragment.MorePlaceTypeFragment
import com.barikoi.barikoidemo.Model.Api
import com.barikoi.barikoidemo.Model.Place
import com.barikoi.barikoidemo.Model.Type
import com.barikoi.barikoidemo.Task.JsonUtilsTask
import com.barikoi.barikoidemo.Adapter.PlaceListAdapter
import com.barikoi.barikoidemo.R
import com.infideap.drawerbehavior.AdvanceDrawerLayout
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
import com.shreyaspatil.material.navigationview.MaterialNavigationView
import com.xiaoyi.action.Platform.initialize
import kotlinx.android.synthetic.main.bottomsheet_addresslist.*
import kotlinx.android.synthetic.main.bottomsheet_nearbylist.*
import kotlinx.android.synthetic.main.bottomsheet_placeview.textview_address
import kotlinx.android.synthetic.main.bottomsheet_placeview.textview_area
import kotlinx.android.synthetic.main.bottomsheet_rupantor.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap


class MainDemoActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {


    private var currentLat: Double? = null
    private var currentLng: Double? = null
    private var currentlocation: Location?=null
    private val placemarkermap: HashMap<String,Marker>?=HashMap()
    private var iconFactory: IconFactory?=null
    private var permissionsManager: PermissionsManager?=null
    private var map: MapboxMap?=null
    private var mBottomSheetBehaviorplaceview: BottomSheetBehavior<View>?=null
    private var mBottomSheetBehaviorRupantor: BottomSheetBehavior<LinearLayout>?=null
    private var mBottomSheetBehaviorNearby: BottomSheetBehavior<LinearLayout>?=null
    private var mBottomSheetBehaviorAddress: BottomSheetBehavior<View>?=null
    var types= arrayOf("Bank","Education","Food","Fuel","Government","Healthcare","Hotel","Shop","Utility")
    private var nearbyadapter: PlaceListAdapter?=null
    //private lateinit var appBarConfiguration: AppBarConfiguration

    private val requestCode = 555
    val GHURBOKOI = 23
    var gridLayoutManager: GridLayoutManager ?=null
    var typeadapter: TypeListAdapter ?= null
    private var nearbySerchType: NearbySerchType?= null
    private var latitude = 0.0
    private var longitude = 0.0

    private val token = ""

    private val TAG = "MainActivityDemo"

    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_demo)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: AdvanceDrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        drawerLayout.useCustomBehavior(Gravity.START)
        drawerLayout.setViewScale(Gravity.START, 0.9f)
        drawerLayout.setViewElevation(Gravity.START, 20f)
        drawerLayout.setRadius(Gravity.START, 25F)

        val navView: MaterialNavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId){
                R.id.nav_search -> initSearchautocomplete()
                R.id.nav_reversegeo -> initReversegeo()
                R.id.nav_rupantor -> initRupantor()
                R.id.nav_nearby -> initNearby()
                R.id.nav_addresses -> initAddress()
                R.id.nav_checkout ->{val intent = Intent(this@MainDemoActivity, CheckOutActivity::class.java)
                    Log.d(TAG, "Current Location: " + currentlocation)
                    intent.putExtra("location",currentlocation.toString())
                    intent.putExtra("lat",currentLat)
                    intent.putExtra("lng",currentLng)
                    startActivity(intent)}
            }
            drawerLayout.closeDrawer(GravityCompat.START)

            true
        }


        /*resourse name checking by id
//        val id = 2131361916
//        val name = getResources().getResourceEntryName(id)
//
//        Log.d(TAG, "Res Name: " +name)

        */

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        initViews()
        initSearchautocomplete()
        //checkOptimization()



    }

//    @SuppressLint("MissingPermission")
//    override fun NearbySearchType(t: Type, lat: Double, lon: Double) {
//        try {
//            val prev = supportFragmentManager.findFragmentByTag(MorePlaceTypeFragment.TAG)
//            if (prev != null) {
//                val df = prev as DialogFragment?
//                df!!.dismiss()
//            }
//
//            BottomSheetDown()
//            ClearMap()
//            val searchNearbyFragment = SearchNearbyFragment()
//            val bundle = Bundle()
//            bundle.putSerializable("Type", t)
//            if (originLocation == null && locationEngine!!.lastLocation != null) {
//
//                originLocation = locationEngine!!.lastLocation
//                bundle.putDouble("latitude", originLocation!!.latitude)
//                bundle.putDouble("longitude", originLocation!!.longitude)
//            } else if (originLocation != null) {
//                bundle.putDouble("latitude", originLocation!!.latitude)
//                bundle.putDouble("longitude", originLocation!!.longitude)
//            } else
//                return
//
//            searchNearbyFragment.arguments = bundle
//            loadFragment(searchNearbyFragment, "SearchNearby")
//            BottomSheetBehavior(R.id.bottom_sheet_place_search_nearby)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

    @SuppressLint( "InvalidWakeLockTag", "WakelockTimeout")
    private fun checkOptimization() {
       var tag = "com.barikoi.barikoidemo:LOCK"

        Log.d(TAG, "Huawei: " +Build.VERSION.SDK_INT)
        Log.d(TAG, "Huawei: " +Build.MANUFACTURER)

        if (Build.VERSION.SDK_INT >= 21 && Build.MANUFACTURER == "HUAWEI") {
            tag = "LocationManagerService"
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        Log.d(TAG, "Huawei: " +tag)

//        val wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
//            1, tag)
//        Log.d(TAG, "WakeLog: " +wakeLock.toString())

//        wakeLock.acquire()
//        Log.d(TAG, "WakeLog: " +wakeLock.acquire().toString())


//        val wakeLock: PowerManager.WakeLock =
//            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//                newWakeLock(1, tag).apply {
//                    acquire()
//                }
//            }
//
//        Log.d(TAG, "WakeLog: " +wakeLock.toString())


    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        Log.d(TAG,"map ready")
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

                    currentlocation=result?.lastLocation

                    currentLat = result?.lastLocation!!.latitude
                    currentLng = result.lastLocation!!.longitude
                    map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result?.lastLocation!!.latitude,result.lastLocation!!.longitude),17.0))

                    //Log.d(TAG,"Map: " +map.toString())
                }

                override fun onFailure(exception: Exception) {

                }

            })
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
            Log.d(TAG,"location permission not granted")
            permissionsManager = PermissionsManager(this)

            permissionsManager?.requestLocationPermissions(this)

        }

    }

   

    private fun initViews(){

        mBottomSheetBehaviorplaceview = BottomSheetBehavior.from(bottomsheet_placeview)
        mBottomSheetBehaviorAddress = BottomSheetBehavior.from(bottomsheet_addresslist)
        mBottomSheetBehaviorNearby = BottomSheetBehavior.from(bottomsheet_nearby)
        mBottomSheetBehaviorRupantor = BottomSheetBehavior.from(bottomsheet_rupantor)
        mBottomSheetBehaviorNearby?.peekHeight=500
        mBottomSheetBehaviorRupantor?.peekHeight=200
        mBottomSheetBehaviorplaceview?.peekHeight = 200
        mBottomSheetBehaviorAddress?.peekHeight = 200




        nearbyadapter= PlaceListAdapter(ArrayList<NearbyPlace>(), object : PlaceListAdapter.OnPlaceItemSelectListener {
                override fun onPlaceItemSelected(mItem: NearbyPlace?, position: Int) {
                    placemarkermap?.get(mItem!!.code)?.showInfoWindow(map!!, mapView)
                }
            })
        nearbylistview.layoutManager=LinearLayoutManager(this)
        nearbylistview.adapter=nearbyadapter

        val DRAWABLE_RIGHT = 2
        search_rupantor.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP ->
                        if(event.getRawX() >= search_rupantor.getRight() - search_rupantor.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                            rupantor(searchtext = search_rupantor.text.toString())
                        }

                }

                return v?.onTouchEvent(event) ?: true
            }
        })

//        rupantor_button.setOnClickListener { v ->
//            val searchtext= rupantor_searchtext.text
//            rupantor(searchtext.toString())
//        }

        iconFactory = IconFactory.getInstance(this@MainDemoActivity)
        clearmode()
        loadnearbytypes()

    }

    fun hideKeyboard(mActivity : Activity) {


        val view = mActivity.currentFocus
        if (view != null) {
            val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
        }
    }

    private fun initAddress(){

        clearmode()
        map_pointer.visibility= VISIBLE
        btn_addressList.visibility= VISIBLE
        map?.addOnCameraIdleListener (maprevgeolistener2)

        btn_addressList.setOnClickListener { v ->
            mBottomSheetBehaviorAddress!!.state =
                BottomSheetBehavior.STATE_EXPANDED

            initsuggestionList(
                map?.cameraPosition!!.target.latitude,
                map?.cameraPosition!!.target.longitude
            )
            //nearbyAddressview.visibility = VISIBLE

        }

        layoutHeader.setOnClickListener { v ->
            mBottomSheetBehaviorAddress?.isHideable = true
            mBottomSheetBehaviorAddress!!.state =
                BottomSheetBehavior.STATE_HIDDEN
        }

    }
    val maprevgeolistener2= object: MapboxMap.OnCameraIdleListener {
        override fun onCameraIdle() {
            Log.d(TAG, "onCameraIdle")
            progress.visibility= VISIBLE
            ReverseGeoAPI.builder(this@MainDemoActivity)
                .setLatLng(map?.cameraPosition!!.target.latitude,map?.cameraPosition!!.target.longitude)
                .build()
                .getAddress(object: ReverseGeoAPIListener {
                    override fun onFailure(message: String?) {
                        progress.visibility=GONE
                        Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                    }

                    override fun reversedAddress(place: ReverseGeoPlace?) {
                        Log.d(TAG, "reversedAddress")
                        progress.visibility=GONE
//                        tv_address.text=place?.address
//                        tv_area.text=place?.area

                        //nearbyAddressview.visibility = GONE



                    }

                })
        }


    }

    private fun initNearby() {
        clearmode()

        val typeS = java.util.ArrayList<Type>()
        typeS.add(Type("Food", "", getString(R.string.food),
            R.drawable.food
        ))
        typeS.add(Type("Religious Place", "", getString(R.string.religious),
            R.drawable.religion
        ))

        //typeS.add(Type("More", "", getString(R.string.more), R.drawable.more))
        typeS.add(Type("", "Hospital", getString(R.string.hospital),
            R.drawable.hospital
        ))
        typeS.add(Type("", "Pharmacy", getString(R.string.pharmacy),
            R.drawable.pharmacy
        ))
        typeS.add(Type("Bank", "", getString(R.string.bank),
            R.drawable.bank
        ))
        typeS.add(Type("Education", "", getString(R.string.education),
            R.drawable.education
        ))
        typeS.add(Type("", "Police Station", getString(R.string.policestation),
            R.drawable.policestation
        ))
        typeS.add(Type("Hotel", "", getString(R.string.hotel),
            R.drawable.hotel
        ))
        typeS.add(Type("Public Toilet", "", getString(R.string.toilet),
            R.drawable.toilet
        ))
        typeS.add(Type("fuel", "", getString(R.string.fuel),
            R.drawable.gas
        ))
        typeS.add(Type("", "BKash", getString(R.string.bkash),
            R.drawable.bkash
        ))
        typeS.add(Type("", "UCash", getString(R.string.ucash),
            R.drawable.ucash2
        ))
        typeS.add(Type("", "SureCash", getString(R.string.surecash),
            R.drawable.surecash
        ))
        typeS.add(Type("", "Parking", getString(R.string.parking),
            R.drawable.parking
        ))
        typeS.add(Type("", "General Store", getString(R.string.generalStore),
            R.drawable.commercial
        ))
        typeS.add(Type("", "Market", getString(R.string.market),
            R.drawable.commercial
        ))
//        typeS.add(Type("Attractions", "", getString(R.string.attractions),
//            R.drawable.landmark
//        ))

//        layoutHeaderNearby.setOnClickListener { v ->
//            mBottomSheetBehaviorNearby!!.state =
//                BottomSheetBehavior.STATE_HIDDEN
//        }


        val typeadapter = TypeListAdapter(typeS,
            R.layout.type_list_item_nearby_places,
            object : TypeListAdapter.OnTypeItemSelectListener {
                override fun onTypeSelected(t: Type) {
                    Log.d(TAG, "TypeS List: ")
                    if (t.name.equals("Attractions")) {
                        val intent = Intent(applicationContext, GhurboKoiActivity::class.java)
                            startActivityForResult(intent, GHURBOKOI)
                    } else if (t.name.equals("More")) {
                        /*Intent intent=new Intent(getContext(),MorePlaceTypeActivity.class);
                    startActivity(intent);*/
                        MoreTypeView(true)
                    } else {

                        nearbySerchType?.NearbySearchType(t, latitude, longitude)
                        getnearby(t.name)
                    }
                }
            })
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        typeList.layoutManager = linearLayoutManager
        typeList.adapter = typeadapter


        mBottomSheetBehaviorNearby!!.state=BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetBehaviorNearby!!.isHideable=false
        //getnearby(nearbytypespineer.selectedItem.toString())

//        nearbytypespineer.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>,
//                                        view: View, position: Int, id: Long){
//                getnearby(types[position])
//            }
//        })
    }
    private fun getnearby(type : String){
        nearbylistview.visibility = VISIBLE

        map?.locationComponent!!.locationEngine?.getLastLocation(object :LocationEngineCallback<LocationEngineResult>{
            override fun onSuccess(result: LocationEngineResult?) {
                NearbyPlaceAPI.builder(this@MainDemoActivity)
                    .setDistance(2.0)
                    .setLimit(5)
                    .setType(type)
                    .setLatLng(result?.lastLocation!!.latitude,result.lastLocation!!.longitude)
                    .build()
                    .generateNearbyPlaceListByType(object: NearbyPlaceListener{
                        override fun onPlaceListReceived(places: java.util.ArrayList<NearbyPlace>?) {
                            plotmarkers(places!!,result.lastLocation!!.latitude,result.lastLocation!!.longitude)
                            nearbyadapter?.setplaces(places)
                        }
                        override fun onFailure(message: String?) {
                            Toast.makeText(applicationContext,"could not get nearby places",Toast.LENGTH_SHORT).show()
                        }

                    })
                mBottomSheetBehaviorNearby!!.state=BottomSheetBehavior.STATE_COLLAPSED
                //mBottomSheetBehaviorNearby!!.isHideable=false
            }

            override fun onFailure(exception: Exception) {
                Toast.makeText(applicationContext,"could not get location",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initRupantor() {
        clearmode()
        mBottomSheetBehaviorRupantor!!.state=BottomSheetBehavior.STATE_EXPANDED
        mBottomSheetBehaviorRupantor!!.isHideable=false

        search_rupantor.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                rupantor(searchtext = search_rupantor.text.toString())
            }
            true
        }

    }

    private fun initReversegeo() {
        clearmode()
        map_pointer.visibility= VISIBLE
        mBottomSheetBehaviorplaceview!!.state=BottomSheetBehavior.STATE_EXPANDED
        mBottomSheetBehaviorplaceview!!.isHideable = false
        map?.addOnCameraIdleListener (maprevgeolistener)

//        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                // Do something for new state
//
//                // this part hides the button immediately and waits bottom sheet
//                // to collapse to show
//                if (BottomSheetBehavior.STATE_DRAGGING == newState) {
//                    fab.animate().scaleX(0f).scaleY(0f).setDuration(300).start()
//                } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
//                    fab.animate().scaleX(1f).scaleY(1f).setDuration(300).start()
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                // Do something for slide offset
//
//            }
//        }
        //mBottomSheetBehaviorplaceview!!.setBottomSheetCallback(bottomSheetCallback)
    }
    val maprevgeolistener= object: MapboxMap.OnCameraIdleListener {
        override fun onCameraIdle() {
            Log.d(TAG, "onCameraIdle")
            progress.visibility= VISIBLE
            ReverseGeoAPI.builder(this@MainDemoActivity)
                .setLatLng(map?.cameraPosition!!.target.latitude,map?.cameraPosition!!.target.longitude)
                .build()
                .getAddress(object: ReverseGeoAPIListener {
                    override fun onFailure(message: String?) {
                        progress.visibility=GONE
                        Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                    }

                    override fun reversedAddress(place: ReverseGeoPlace?) {
                        Log.d(TAG, "reversedAddress")
                        progress.visibility=GONE
                        textview_address.text=place?.address
                        textview_area.text=place?.area
//                        btn_send.setOnClickListener {
//                            val intent = Intent(this@MainDemoActivity, CheckOutActivity::class.java)
//                            intent.putExtra("address", place.toString())
//                            setResult(RESULT_OK,intent)
//                            startActivityForResult(intent, requestCode)
//                            finish()
//                        }

                    }

                })
        }
    }

    private fun initsuggestionList(lat: Double, lon: Double) {


//        mBottomSheetBehaviorplaceview?.setState(BottomSheetBehavior.STATE_EXPANDED)
//        mBottomSheetBehaviorplaceview?.setHideable(false)
        val request = object : StringRequest(Method.GET,
            Api.addplacesugg + "?longitude=" + lon + "&latitude=" + lat,
            Response.Listener { response ->
                try {
                    progress.visibility=GONE
                    val placearray = JSONArray(response.toString())
                    val newplaces = JsonUtilsTask.getPlaces(placearray)

                    Log.d(TAG, "SuggestionList: "+newplaces)

//                    val iconFactory = IconFactory.getInstance(this@MainDemoActivity)
//                    val icon = iconFactory.fromResource(R.drawable.mapmarkerforshowplaces)
//                    for (p in newplaces) {
//                        val point = LatLng(
//                            java.lang.Double.parseDouble(p.getLat()),
//                            java.lang.Double.parseDouble(p.getLon())
//                        )
//                        map?.addMarker(
//                            com.mapbox.mapboxsdk.annotations.MarkerOptions().position(
//                                point
//                            ).icon(icon)
//                        )
//                    }
                    if (newplaces.size > 0) {
                        nearbyAddressview.visibility = VISIBLE

                        val placeArrayAdapter = PlaceAddressAdapter(newplaces,
                            object : PlaceAddressAdapter.OnPlaceItemSelectListener{
                                override fun onPlaceItemSelected(mItem: Place?, position: Int) {

                                    ///OnClick
                                }
                            })

                        val linearLayoutManager = LinearLayoutManager(applicationContext)
                        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                        nearbyAddressview.layoutManager = linearLayoutManager
                        nearbyAddressview.adapter = placeArrayAdapter

                        Log.d(TAG, "SuggestionList: "+nearbyAddressview)

//                        mBottomSheetBehaviorAddress!!.state=BottomSheetBehavior.STATE_COLLAPSED
//                        mBottomSheetBehaviorAddress!!.isHideable=false

                    } else {
                        //Toast.makeText(AddPlaceActivity.this,lat+" "+lon, Toast.LENGTH_LONG).show();

                        mBottomSheetBehaviorAddress?.setHideable(true)
                        mBottomSheetBehaviorAddress?.setState(BottomSheetBehavior.STATE_HIDDEN)
                        //mBottomSheetBehaviorAddress?.setState(BottomSheetBehavior.STATE_EXPANDED)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()

                }
            },
            Response.ErrorListener {
                progress.visibility=GONE
                //Toast.makeText(getActivity(),"Network error",Toast.LENGTH_SHORT).show();
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                if (token != "") {
                    params["Authorization"] = "bearer $token"
                }
                return params
            }
        }
        val queue = Volley.newRequestQueue(this@MainDemoActivity)
        queue.add(request)
        //progress.visibility= VISIBLE
//        findViewById<View>(R.id.buttonskip).setOnClickListener {
//            clearInputData()
//            suggestSheetBehavior.setHideable(true)
//            suggestSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//        }
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
                JsonUtilsTask.logResponse(error)

                Toast.makeText(this@MainDemoActivity, "Not found", Toast.LENGTH_SHORT).show()
            }){}
        RequestQueueSingleton.getInstance(this).requestQueue!!.add(typesreq)*/

//        val adapter = ArrayAdapter(this,
//            android.R.layout.simple_spinner_item,types )
//        //nearbytypespineer.adapter = adapter

    }

    private fun rupantor(searchtext: String){
        progress.visibility= VISIBLE
        resultpane.visibility= INVISIBLE

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        val rupantorrequest= object : StringRequest(
            Request.Method.POST,
            "https://barikoi.xyz/v1/api/search/"+BarikoiAPI.getAccessToken()+"/rupantor/geocode",
            { response: String ->
                progress.visibility=GONE
                try {

                    //hideKeyboard()
                    hideKeyboard(this@MainDemoActivity)

                    val data = JSONObject(response)
                    val placearray = data.getJSONObject("geocoded_address")

                    val place = JsonUtils.getGeoCodePlace(placearray)

                    fixed_addresstext.text=data.getString("fixed_address")
                    rupantor_addresstext.text=place.address
                    rupantor_type.text=place.type
                    rupantor_scoreText.text=data.getString("confidence_score_percentage")
                    rupantor_status.text=data.getString("address_status")
                    resultpane.visibility= VISIBLE
                    plotmarker(place)
                } catch (e: JSONException) {
                    progress.visibility=GONE
                    try {
                        val data = JSONObject(response)
                        Toast.makeText(applicationContext, data.getString("Message"), Toast.LENGTH_SHORT).show()
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }

                }
            },
            { error ->
                progress.visibility=GONE
                JsonUtils.logResponse(error)

                Toast.makeText(applicationContext, "Not found", Toast.LENGTH_SHORT).show()
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
        mBottomSheetBehaviorAddress!!.isHideable=true
        mBottomSheetBehaviorAddress!!.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehaviorNearby!!.isHideable=true
        mBottomSheetBehaviorNearby!!.state = BottomSheetBehavior.STATE_HIDDEN
        mBottomSheetBehaviorRupantor!!.isHideable=true
        mBottomSheetBehaviorRupantor!!.state = BottomSheetBehavior.STATE_HIDDEN
        autocompletepane.visibility=GONE
        map_pointer.visibility=GONE
        btn_addressList.visibility= GONE
        placemarkermap?.clear()
        map?.clear()
        map?.removeOnCameraIdleListener(maprevgeolistener)
        map?.removeOnCameraIdleListener(maprevgeolistener2)
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
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

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
    interface NearbySerchType {
        fun NearbySearchType(t: Type, lat: Double, lon: Double)
        //fun MoreTypeView(clicked: Boolean)

    }

    fun MoreTypeView(clicked: Boolean) {
        if (clicked) {
            val dialog = MorePlaceTypeFragment()
            val ft = supportFragmentManager.beginTransaction()
            dialog.show(ft, MorePlaceTypeFragment.TAG)
        }
    }
   /* override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
}
