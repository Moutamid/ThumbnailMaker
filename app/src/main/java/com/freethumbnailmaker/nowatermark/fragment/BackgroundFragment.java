package com.freethumbnailmaker.nowatermark.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.MyApplication;
import com.freethumbnailmaker.nowatermark.adapter.VeticalViewAdapter;
import com.freethumbnailmaker.nowatermark.interfaces.GetSnapListenerData;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.listener.RecyclerViewLoadMoreScroll;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import com.freethumbnailmaker.nowatermark.model.MainBG;
import com.freethumbnailmaker.nowatermark.model.Snap2;
import com.freethumbnailmaker.nowatermark.model.ThumbBG;
import com.freethumbnailmaker.nowatermark.utility.YourDataProvider;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;
import com.qintong.library.InsLoadingView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BackgroundFragment extends Fragment {
    private static final String TAG = "BackgroundFragment";

    public AppPreference appPreference;

    private InsLoadingView loading_view;
    LinearLayoutManager mLinearLayoutManager;
    GetSnapListenerData onGetSnap;
    RecyclerView recyclerView;
    RelativeLayout rlAd;
    float screenHeight;
    float screenWidth;

    public RecyclerViewLoadMoreScroll scrollListener;
    ArrayList<Object> snapData = new ArrayList<>();

    public ArrayList<MainBG> thumbnail_bg;

    public VeticalViewAdapter veticalViewAdapter;
    YourDataProvider yourDataProvider;

    public static BackgroundFragment newInstance() {
        return new BackgroundFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.main_fragment, viewGroup, false);
        this.onGetSnap = (GetSnapListenerData) getActivity();
        this.recyclerView = inflate.findViewById(R.id.overlay_artwork);
        this.mLinearLayoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(this.mLinearLayoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.rlAd = inflate.findViewById(R.id.rl_ad);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = (float) displayMetrics.widthPixels;
        this.screenHeight = (float) displayMetrics.heightPixels;
        this.appPreference = new AppPreference(getActivity());
        this.loading_view = inflate.findViewById(R.id.loading_view);
        MyApplication.getInstance().addToRequestQueue(new StringRequest(1, Constants.BASE_URL_POSTER + "poster/background", str -> {
            try {
                BackgroundFragment.this.thumbnail_bg = (new Gson().fromJson(str, ThumbBG.class)).getThumbnail_bg();
                for (int i = 0; i < BackgroundFragment.this.thumbnail_bg.size(); i++) {
                    if ((BackgroundFragment.this.thumbnail_bg.get(i)).getCategory_list().size() != 0) {
                        BackgroundFragment.this.snapData.add(new Snap2(1, (BackgroundFragment.this.thumbnail_bg.get(i)).getCategory_name(), (BackgroundFragment.this.thumbnail_bg.get(i)).getCategory_list(), (BackgroundFragment.this.thumbnail_bg.get(i)).getCategory_id(), ""));
                    }
                }
                if (!BackgroundFragment.this.appPreference.getBoolean("isAdsDisabled", false)) {
                    BackgroundFragment.this.loadNativeAds();
                } else if (BackgroundFragment.this.snapData != null) {
                    BackgroundFragment.this.veticalViewAdapter = new VeticalViewAdapter(BackgroundFragment.this.getActivity(), BackgroundFragment.this.snapData, 0);
                    BackgroundFragment.this.recyclerView.setAdapter(BackgroundFragment.this.veticalViewAdapter);
                }
                BackgroundFragment.this.yourDataProvider = new YourDataProvider();
                BackgroundFragment.this.yourDataProvider.setPosterList(BackgroundFragment.this.snapData);
                if (BackgroundFragment.this.snapData != null) {
                    BackgroundFragment.this.veticalViewAdapter = new VeticalViewAdapter(BackgroundFragment.this.getActivity(), BackgroundFragment.this.yourDataProvider.getLoadMorePosterItems(), 0);
                    BackgroundFragment.this.recyclerView.setAdapter(BackgroundFragment.this.veticalViewAdapter);
                    BackgroundFragment.this.scrollListener = new RecyclerViewLoadMoreScroll(BackgroundFragment.this.mLinearLayoutManager);
                    BackgroundFragment.this.scrollListener.setOnLoadMoreListener(() -> BackgroundFragment.this.LoadMoreData());
                    BackgroundFragment.this.recyclerView.addOnScrollListener(BackgroundFragment.this.scrollListener);
                }
                BackgroundFragment.this.recyclerView.setAdapter(BackgroundFragment.this.veticalViewAdapter);
                BackgroundFragment.this.veticalViewAdapter.setItemClickCallback((OnClickCallback<ArrayList<String>, ArrayList<BackgroundImage>, String, Activity>) (arrayList, arrayList2, str1, activity) -> {
                    if (str1.equals("")) {
                        BackgroundFragment.this.onGetSnap.onSnapFilter(arrayList2, 1);
                        return;
                    }
                    final ProgressDialog progressDialog = new ProgressDialog(BackgroundFragment.this.getContext());
                    progressDialog.setMessage(BackgroundFragment.this.getResources().getString(R.string.plzwait));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final File cacheFolder = BackgroundFragment.this.getCacheFolder(BackgroundFragment.this.getContext());
                    MyApplication.getInstance().addToRequestQueue(new ImageRequest(str1, (Response.Listener<Bitmap>) bitmap -> {
                        try {
                            progressDialog.dismiss();
                            try {
                                File file = new File(cacheFolder, "localFileName.png");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                try {
                                    BackgroundFragment.this.onGetSnap.onSnapFilter(0, 104, file.getAbsolutePath());
                                } catch (Exception e) {
                                    try {
                                        e.printStackTrace();
                                    } catch (NullPointerException e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        } catch (Exception e5) {
                            e5.printStackTrace();
                        }
                    }, 0, 0, (Bitmap.Config) null, volleyError -> progressDialog.dismiss()));
                });
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }, volleyError -> Log.e(BackgroundFragment.TAG, "Error: " + volleyError.getMessage())) {
            @Override
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        });
        return inflate;
    }


    public void LoadMoreData() {
        this.veticalViewAdapter.addLoadingView();
        new Handler().postDelayed(() -> {
            BackgroundFragment.this.veticalViewAdapter.removeLoadingView();
            BackgroundFragment.this.veticalViewAdapter.addData(BackgroundFragment.this.yourDataProvider.getLoadMorePosterItemsS());
            BackgroundFragment.this.veticalViewAdapter.notifyDataSetChanged();
            BackgroundFragment.this.scrollListener.setLoaded();
        }, 3000);
    }


    public void loadNativeAds() {
        insertAdsInMenuItems();
    }

    private void insertAdsInMenuItems() {
        this.loading_view.setVisibility(View.GONE);
    }

    public File getCacheFolder(Context context) {
        File file;
        if (Environment.getExternalStorageState().equals("mounted")) {
            file = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        } else {
            file = null;
        }
        return !file.isDirectory() ? context.getCacheDir() : file;
    }
}
