package com.freethumbnailmaker.nowatermark;

import android.os.StrictMode;
import android.text.TextUtils;


import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.ads.MobileAds;
import com.freethumbnailmaker.nowatermark.ads.AdmobAds;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MyApplication extends MultiDexApplication {
    private static String MAIN_URL = "http://threemartians.in/thumbnailmaker/";
    private static final String TAG = "MyApplication";
    private static MyApplication mInstance;
    private RequestQueue mRequestQueue;


    public static synchronized MyApplication getInstance() {
        MyApplication myApplication;
        synchronized (MyApplication.class) {
            synchronized (MyApplication.class) {
                myApplication = mInstance;
            }
        }
        return myApplication;
    }

    public RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return this.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String str) {
        if (TextUtils.isEmpty(str)) {
            str = TAG;
        }
        request.setTag(str);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object obj) {
        RequestQueue requestQueue = this.mRequestQueue;
        if (requestQueue != null) {
            requestQueue.cancelAll(obj);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInstance = this;
        Constants.BASE_URL_POSTER = MAIN_URL + "API/V1/";
        Constants.BASE_URL_STICKER = MAIN_URL;
        Constants.BASE_URL_BG = MAIN_URL;
        Constants.BASE_URL = MAIN_URL + "Resources/Poster.php";
        Constants.fURL = MAIN_URL + "Resources/Font/";
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        loadAds();
    }

    private void loadAds() {
        MobileAds.initialize(this);
        AdmobAds.initFullAds(this);


    }
}
