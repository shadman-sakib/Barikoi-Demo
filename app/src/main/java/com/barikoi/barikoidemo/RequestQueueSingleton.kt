package com.barikoi.barikoi.Models

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * This class handles all the requests from the server
 * Created by Sakib on 03-Jan-17.
 */

class RequestQueueSingleton private constructor(context: Context) {
    private var mRequestQueue: RequestQueue? = null
    val imageLoader: ImageLoader

    // getApplicationContext() is key, it keeps you from leaking the
    // Activity or BroadcastReceiver if someone passes one in.
    val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx.applicationContext)
            }
            return mRequestQueue
        }

    init {
        mCtx = context
        mRequestQueue = requestQueue

        imageLoader = ImageLoader(mRequestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)

                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue!!.add(req)
    }

    companion object {
        private var mInstance: RequestQueueSingleton? = null
        private lateinit var mCtx: Context

        @Synchronized
        fun getInstance(context: Context): RequestQueueSingleton {
            if (mInstance == null) {
                mInstance = RequestQueueSingleton(context)
            }
            return mInstance as RequestQueueSingleton
        }
    }
}