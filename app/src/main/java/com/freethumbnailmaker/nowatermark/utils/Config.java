package com.freethumbnailmaker.nowatermark.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class Config {
    public static String BannerAdunitID = " ";
    public static String InterstitalAdunitID = " ";
    public static String StartappID = " ";
    public static int bannerAdStatus = 0;
    public static String created = " ";
    public static int interstitalAdStatus;
    public static SharedPreferences sharedPreferences;

    public static void SaveInt(String str, int i, Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(str, i);
        edit.commit();
    }

    public static int loadIntForFlow(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getInt("flow", 1);
    }

    public static int loadIntForAd(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getInt("app_open", 0);
    }

    public static void SaveBool(String str, Boolean bool, Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(str, bool.booleanValue());
        edit.commit();
    }

    public static boolean loadBoolPurchase(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean("in_app_purchase", false);
    }

    public static boolean loadBoolTempVideoAd(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean("video_ad", false);
    }

    public static boolean loadBoolPosterVideoAd(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean("video_ad_poster", false);
    }

    public static String loadString(Activity activity, String str) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getString(str, "");
    }

    public static String loadString(Context context, String str) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(str, "");
    }

    public static boolean loadBoolRate(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean("rate_us", false);
    }

    public static boolean loadBoolRateLater(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean("flag_rate_later", false);
    }

    public static int LoadInt(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getInt("count", 0);
    }

    public static int LoadIntPo(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getInt("isAccepted", 0);
    }

    public static void SaveString(String str, String str2, Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(str, str2);
        edit.commit();
    }

    public static void SaveString(String str, String str2, Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(str, str2);
        edit.commit();
    }

    public static String loadDateString(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getString("date", "date");
    }

    public static String LoadFireBaseString(Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        return sharedPreferences.getString("token", "abc");
    }

    public static boolean isNetworkAvailableContex(Context context) {
        NetworkInfo[] allNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(connectivityManager == null || (allNetworkInfo = connectivityManager.getAllNetworkInfo()) == null)) {
            for (NetworkInfo state : allNetworkInfo) {
                if (state.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
