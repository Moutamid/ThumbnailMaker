package com.freethumbnailmaker.nowatermark.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.adapter.StickerAdapter;
import com.freethumbnailmaker.nowatermark.interfaces.GetSnapListener;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;

import com.freethumbnailmaker.nowatermark.listener.RecyclerViewLoadMoreScroll;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import com.freethumbnailmaker.nowatermark.utility.GridSpacingItemDecoration;
import com.freethumbnailmaker.nowatermark.utility.YourDataProvider;
import com.stepstone.apprating.CKt;

import java.util.ArrayList;

public class StickerFragmentMore extends Fragment {

    ArrayList<BackgroundImage> category_list;
    GetSnapListener onGetSnap;
    RecyclerView recyclerView;
    RelativeLayout rlAd;
    float screenHeight;
    float screenWidth;

    public RecyclerViewLoadMoreScroll scrollListener;
    StickerAdapter stickerAdapter;
    YourDataProvider yourDataProvider;

    public static StickerFragmentMore newInstance(ArrayList<BackgroundImage> arrayList) {
        StickerFragmentMore stickerFragmentMore = new StickerFragmentMore();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(CKt.DIALOG_DATA, arrayList);
        stickerFragmentMore.setArguments(bundle);
        return stickerFragmentMore;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.main_fragment, viewGroup, false);
        this.recyclerView = inflate.findViewById(R.id.overlay_artwork);
        this.onGetSnap = (GetSnapListener) getActivity();
        this.rlAd = inflate.findViewById(R.id.rl_ad);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = (float) displayMetrics.widthPixels;
        this.screenHeight = (float) displayMetrics.heightPixels;

        this.category_list = getArguments().getParcelableArrayList(CKt.DIALOG_DATA);
        setCategory();
        return inflate;
    }

    private void setCategory() {
        this.yourDataProvider = new YourDataProvider();
        this.yourDataProvider.setStickerList(this.category_list);
        this.stickerAdapter = new StickerAdapter(getActivity(), this.yourDataProvider.getLoadMoreStickerItems(), getResources().getDimensionPixelSize(R.dimen.logo_image_size), getResources().getDimensionPixelSize(R.dimen.image_padding));
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 40, true));
        this.recyclerView.setAdapter(this.stickerAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                int itemViewType = StickerFragmentMore.this.stickerAdapter.getItemViewType(i);
                if (itemViewType != 0) {
                    return itemViewType != 1 ? -1 : 3;
                }
                return 1;
            }
        });
        this.scrollListener = new RecyclerViewLoadMoreScroll(mLayoutManager);
        this.scrollListener.setOnLoadMoreListener(() -> StickerFragmentMore.this.LoadMoreData());
        this.recyclerView.addOnScrollListener(this.scrollListener);
        this.stickerAdapter.setItemClickCallback((OnClickCallback<ArrayList<String>, Integer, String, Activity>) (arrayList, num, str, activity) -> StickerFragmentMore.this.onGetSnap.onSnapFilter(num.intValue(), 34, str));
    }


    public void LoadMoreData() {
        this.stickerAdapter.addLoadingView();
        new Handler().postDelayed(() -> {
            StickerFragmentMore.this.stickerAdapter.removeLoadingView();
            StickerFragmentMore.this.stickerAdapter.addData(StickerFragmentMore.this.yourDataProvider.getLoadMoreStickerItemsS());
            StickerFragmentMore.this.stickerAdapter.notifyDataSetChanged();
            StickerFragmentMore.this.scrollListener.setLoaded();
        }, 3000);
    }


}
