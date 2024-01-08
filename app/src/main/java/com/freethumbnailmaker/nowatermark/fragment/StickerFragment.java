package com.freethumbnailmaker.nowatermark.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.MyApplication;

import com.freethumbnailmaker.nowatermark.adapter.VerticalStickerAdapter;
import com.freethumbnailmaker.nowatermark.interfaces.GetSnapListenerData;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import com.freethumbnailmaker.nowatermark.model.MainBG;
import com.freethumbnailmaker.nowatermark.model.Snap2;
import com.freethumbnailmaker.nowatermark.model.ThumbBG;

import com.qintong.library.InsLoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StickerFragment extends Fragment {
    private static final String TAG = "StickerFragment";

    public InsLoadingView loading_view;
    GetSnapListenerData onGetSnap;
    RecyclerView recyclerView;
    RelativeLayout rlAd;
    public VerticalStickerAdapter snapAdapter;
    ArrayList<Object> snapData = new ArrayList<>();
    public ArrayList<MainBG> thumbnail_bg;
    public int[] viewTypes;

    public static StickerFragment newInstance() {
        return new StickerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.mainart_fragment, viewGroup, false);
        this.recyclerView = inflate.findViewById(R.id.overlay_artwork);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.recyclerView.setHasFixedSize(true);
        this.onGetSnap = (GetSnapListenerData) getActivity();
        this.rlAd = inflate.findViewById(R.id.rl_ad);

        this.loading_view = inflate.findViewById(R.id.loading_view);
        MyApplication.getInstance().addToRequestQueue(new StringRequest(1, Constants.BASE_URL_POSTER + "poster/stickerT", str -> {
            try {
                StickerFragment.this.thumbnail_bg = (new Gson().fromJson(str, ThumbBG.class)).getThumbnail_bg();
                for (int i = 0; i < StickerFragment.this.thumbnail_bg.size(); i++) {
                    if ((StickerFragment.this.thumbnail_bg.get(i)).getCategory_list().size() != 0) {
                        StickerFragment.this.snapData.add(new Snap2(1, ((MainBG) StickerFragment.this.thumbnail_bg.get(i)).getCategory_name(), ((MainBG) StickerFragment.this.thumbnail_bg.get(i)).getCategory_list(), ((MainBG) StickerFragment.this.thumbnail_bg.get(i)).getCategory_id(), ""));
                    }
                }
                StickerFragment.this.loading_view.setVisibility(View.GONE);
                StickerFragment.this.viewTypes = new int[StickerFragment.this.snapData.size()];
                for (int i2 = 0; i2 < StickerFragment.this.snapData.size(); i2++) {
                    StickerFragment.this.viewTypes[i2] = 0;
                    if (StickerFragment.this.snapData != null) {
                        StickerFragment.this.snapAdapter = new VerticalStickerAdapter(StickerFragment.this.getActivity(), StickerFragment.this.snapData, StickerFragment.this.viewTypes, 0);
                        StickerFragment.this.recyclerView.setAdapter(StickerFragment.this.snapAdapter);
                    }
                }
                StickerFragment.this.snapAdapter.setItemClickCallback((OnClickCallback<ArrayList<String>, ArrayList<BackgroundImage>, String, Activity>) (arrayList, arrayList2, str1, activity) -> {
                    if (str1.equals("")) {
                        StickerFragment.this.onGetSnap.onSnapFilter(arrayList2, 0);
                    } else {
                        StickerFragment.this.onGetSnap.onSnapFilter(0, 34, str1);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, volleyError -> Log.e(StickerFragment.TAG, "Error: " + volleyError.getMessage())) {
            @Override
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        });
        return inflate;
    }


}
