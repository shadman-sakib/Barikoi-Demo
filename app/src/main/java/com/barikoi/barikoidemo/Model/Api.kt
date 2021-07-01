package com.barikoi.barikoidemo.Model

/**
 * This Class contains all the api keys needed to hit the server
 * Created by Sakib on 7/16/2017.
 */

object Api {
    var isauth:Boolean=false
    val url_base = "https://admin.barikoi.xyz:8090/"
    val loginurl = url_base + "auth/login"
    val usercheckurl = url_base + "auth/user"
    val logouturl = url_base + "auth/invalidate"
    val url_usercheck = url_base + "auth/user"
    val url_single_image_Upload = url_base + "image"
    val url_single_image_delete = url_base + "image/delete"
    val url_ghurbokoi = url_base + "ghurbokoi"
    val url_get_place_details = url_base + "place/"
    val url_handymama_service = "http://handymama.co/hm_partner_api/service"
    val url_handymama_auth = "http://handymama.co/hm_partner_api/auth"
    val url_handymama_newlead = "http://handymama.co/hm_partner_api/new_lead"
    val USER_ID = "user_id"
    val PASSWORD = "password"
    val TOKEN = "token"
    val LANG = "language"
    val EMAIL = "email"
    val NAME = "name"
    val POINTS = "points"
    val SPENT_POINTS = "redeemed_points"
    val PHONE = "phone"
    val ISREFFERED = "isReferred"
    val REFCODE = "ref_code"
    val HOME_ID = "home_pid"
    val WORK_ID = "work_pid"
    val rewardlistUrl = url_base + "rewards"
    val rewardReqUrl = url_base + "reward"
    val deliverykoireqdelivery = url_base + "order"
    val url_deliveryorderlist = url_base + "order/user"
    val url_deliveryprice = url_base + "delivery/price"
    val url_deliverycancel = url_base + "order/Cancelled/"

    val url_google_place_autocomplete = "https://maps.googleapis.com/maps/api/place/autocomplete/json"
    val url_google_place_details = "https://maps.googleapis.com/maps/api/place/details/json"

    val url_search_new = url_base + "search/autocomplete/app"
    val url_search_web = url_base + "search/autocomplete/web"
    val url_search_elastic = "http://elastic.barikoi.com/bkoi/autocomplete/filter"
    val url_search_tnt = url_base + "tnt/search/test"
    val url_geocode_new = url_base + "search/geocode/"
    val url_geocode_new_nonauth = url_base +"search/geocode/web/"

    val url_geo_search = url_base + "place/"
    val url_destinatination_distance = url_base + "get/distance/"
    //searchactivity
    val getPlaceUrl = url_base + "place/get/"
    val url_couldntfind = url_base + "search/not/found"
    //addplacefragmen
    val insertUrl = url_base + "test/auth/place/newplace"
    val addplacesugg = url_base + "reverse/for/addition"
    //leaderoard
    val leaderurl = url_base + "weekly/leaderboard"
    val leaderurlmonthly = url_base + "monthly/leaderboard"
    val leaderurlall = url_base + "all/leaderboard"

    //changepassactivity
    val changePassUrl = url_base + "auth/UpdatePass"

    //PlaceViewActivity
    val getViewUrl = url_base + "place/get/app/"
    val deleteurl = url_base + "auth/place/delete/"
    val saveUrl = url_base + "auth/save/place"
    val nearbycodeurl = url_base + "public/place/"
    val savedDeleteUrl = url_base + "auth/saved/place/delete/"

    //refferalActivity
    val redeemurl = url_base + "auth/redeem/referrals"

    //signupActivity
    val signupurl = url_base + "auth/register"
    //placepublicFragment
    val url_nearbyauth = url_base + "public/find/nearby/auth/"
    val getplaceurl = url_base + "place/"
    //placesavedFragment
    val getSavepdPlacesUrl = url_base + "auth/savedplacebyuid"
    //viewplacefragment
    val url_byuserid = url_base + "auth/placeby/userid/"

    //viewdialog
    val resetpassUrl = url_base + "auth/password/reset"

    //barikoidirections
    val directeUrl = url_base + "landmarks"

    val customcodeUrl = url_base + "test/auth/place/newplacecustom"
    val typefetchUrl = url_base + "place/get/type/"
    val subtypefetchUrl = url_base + "place/get/sub/type/"
    val updateUrl = url_base + "auth/place/update/"

    val url_Ride_request = url_base + "ride/request/ride"
    val url_Ride_list = url_base + "ride/get/requested/rides/by/user/"

    val amarbikelistUrl = "https://amarbike.tk/api/app/rides/get"
    val amarBikeBookUrl = "https://amarbike.tk/api/app/ride/create/"

    var bikerenturl = url_base + "rent"
    var bikerentrwqurl = url_base + "bikerental/docs"
    val categorynearbyurl = url_base + "public/find/nearby/by/catagory"
    var bikelistrental = url_base + "bike"
    var rentlistreantal = url_base + "rent/by/user"
    var rentstatusCangeUrl = url_base + "rent/change/status/"

    val url_base_local = "http://192.168.31.11:8082/api/"
    val url_getroad = url_base_local + "road/"

    //AddHomeWorkActivity
    val url_add_home = url_base + "save/user/home/"
    val url_add_work = url_base + "save/user/work/"

    //SearchURL
    val url_share_code = "https://barikoi.com/search/"

    val url_revgeo_auth= url_base+"reverse"
    val url_revgeo_noauth= url_base+"reverse/without/auth"
    val url_rupantor_search= "https://barikoi.xyz/v1/api/search/MTI6SFpDRkoyN0NFOA==/rupantor/geocode"
    fun setAuth(isauth: Boolean ){
        this.isauth=isauth
    }
    fun getSearchUrl(): String{
        /*return if(isauth) url_search_new else url_search_web*/
        return url_search_elastic
    }
    fun getrevgeourl():String {
        return if(isauth) url_revgeo_auth else url_revgeo_noauth
    }
    fun getGeoUrl(): String{
        return if (isauth) url_geocode_new else url_geocode_new_nonauth
    }


}