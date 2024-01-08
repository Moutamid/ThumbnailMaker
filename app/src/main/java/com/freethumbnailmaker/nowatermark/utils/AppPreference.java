package com.freethumbnailmaker.nowatermark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;



import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class AppPreference {
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";
    private SharedPreferences preferences;

    public AppPreference(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isExternalStorageWritable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static boolean isExternalStorageReadable() {
        String externalStorageState = Environment.getExternalStorageState();
        return "mounted".equals(externalStorageState) || "mounted_ro".equals(externalStorageState);
    }

    public SharedPreferences getPrefernce() {
        SharedPreferences sharedPreferences = this.preferences;
        if (sharedPreferences != null) {
            return sharedPreferences;
        }
        return null;
    }

    public Bitmap getImage(String str) {
        try {
            return BitmapFactory.decodeFile(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSavedImagePath() {
        return this.lastImagePath;
    }




    private String setupFullPath(String str) {
        File file = new File(Environment.getExternalStorageDirectory(), this.DEFAULT_APP_IMAGEDATA_DIRECTORY);
        if (!isExternalStorageReadable() || !isExternalStorageWritable() || file.exists() || file.mkdirs()) {
            return file.getPath() + '/' + str;
        }
        Log.e("ERROR", "Failed to setup folder");
        return "";
    }



    public int getInt(String str, int i) {
        return this.preferences.getInt(str, i);
    }

    public ArrayList<Integer> getListInt(String str) {
        ArrayList arrayList = new ArrayList(Arrays.asList(TextUtils.split(this.preferences.getString(str, ""), "‚‗‚")));
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(Integer.valueOf(Integer.parseInt((String) it.next())));
        }
        return arrayList2;
    }

    public long getLong(String str, long j) {
        return this.preferences.getLong(str, j);
    }

    public float getFloat(String str, float f) {
        return this.preferences.getFloat(str, f);
    }

    public double getDouble(String str, double d) {
        try {
            return Double.parseDouble(getString(str));
        } catch (NumberFormatException e) {
            return d;
        }
    }

    public ArrayList<Double> getListDouble(String str) {
        ArrayList arrayList = new ArrayList(Arrays.asList(TextUtils.split(this.preferences.getString(str, ""), "â€šâ€—â€š")));
        ArrayList<Double> arrayList2 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(Double.valueOf(Double.parseDouble((String) it.next())));
        }
        return arrayList2;
    }

    public String getString(String str) {
        return this.preferences.getString(str, "");
    }

    public ArrayList<String> getListString(String str) {
        return new ArrayList<>(Arrays.asList(TextUtils.split(this.preferences.getString(str, ""), "‚‗‚")));
    }

    public boolean getBoolean(String str, boolean z) {
        return this.preferences.getBoolean(str, z);
    }

    public ArrayList<Boolean> getListBoolean(String str) {
        ArrayList<String> listString = getListString(str);
        ArrayList<Boolean> arrayList = new ArrayList<>();
        Iterator<String> it = listString.iterator();
        while (it.hasNext()) {
            if (it.next().equals("true")) {
                arrayList.add(true);
            } else {
                arrayList.add(false);
            }
        }
        return arrayList;
    }

    public void putInt(String str, int i) {
        this.preferences.edit().putInt(str, i).apply();
    }

    public void putListInt(String str, ArrayList<Integer> arrayList) {
        this.preferences.edit().putString(str, TextUtils.join("‚‗‚", (Integer[]) arrayList.toArray(new Integer[arrayList.size()]))).apply();
    }

    public void putLong(String str, long j) {
        this.preferences.edit().putLong(str, j).apply();
    }

    public void putFloat(String str, float f) {
        this.preferences.edit().putFloat(str, f).apply();
    }

    public void putDouble(String str, double d) {
        putString(str, String.valueOf(d));
    }

    public void putListDouble(String str, ArrayList<Double> arrayList) {
        this.preferences.edit().putString(str, TextUtils.join("â€šâ€—â€š", (Double[]) arrayList.toArray(new Double[arrayList.size()]))).apply();
    }

    public void putString(String str, String str2) {
        this.preferences.edit().putString(str, str2).apply();
    }

    public void putListString(String str, ArrayList<String> arrayList) {
        this.preferences.edit().putString(str, TextUtils.join("‚‗‚", (String[]) arrayList.toArray(new String[arrayList.size()]))).apply();
    }

    public void putListBoolean(String str, ArrayList<Boolean> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        Iterator<Boolean> it = arrayList.iterator();
        while (it.hasNext()) {
            if (it.next().booleanValue()) {
                arrayList2.add("true");
            } else {
                arrayList2.add("false");
            }
        }
        putListString(str, arrayList2);
    }

    public void remove(String str) {
        this.preferences.edit().remove(str).apply();
    }

    public boolean deleteImage(String str) {
        return new File(str).delete();
    }

    public void clear() {
        this.preferences.edit().clear().apply();
    }

    public Map<String, ?> getAll() {
        return this.preferences.getAll();
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        this.preferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }
}
