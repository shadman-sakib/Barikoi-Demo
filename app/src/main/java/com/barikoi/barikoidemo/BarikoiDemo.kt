package com.barikoi.barikoidemo

import android.app.Application
import barikoi.barikoilocation.BarikoiAPI
import com.mapbox.mapboxsdk.Mapbox

class BarikoiDemo: Application() {
    override fun onCreate() {
        super.onCreate()
        BarikoiAPI.getINSTANCE(this,"MTI6SFpDRkoyN0NFOA==")
        Mapbox.getInstance(this,"pk.eyJ1IjoidGF1ZmlxdXIiLCJhIjoiY2psYnY3YTgwNGppdTNscXA0ZDFlMDgyYSJ9.353rlwW3mCw7N2KFSfObSQ")
    }
}