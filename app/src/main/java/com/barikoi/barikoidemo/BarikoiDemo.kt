package com.barikoi.barikoidemo

import android.app.Application
import barikoi.barikoilocation.BarikoiAPI
import com.mapbox.mapboxsdk.Mapbox

class BarikoiDemo: Application() {

    override fun onCreate() {
        super.onCreate()
        BarikoiAPI.getINSTANCE(this,"MTI6SFpDRkoyN0NFOA==")
        //Mapbox.getInstance(this,getString(R.string.access_token))
        //Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

    }
}