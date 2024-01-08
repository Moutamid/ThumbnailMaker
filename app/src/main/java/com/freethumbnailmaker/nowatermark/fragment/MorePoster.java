package com.freethumbnailmaker.nowatermark.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.main.PaginationListener;
import com.freethumbnailmaker.nowatermark.model.ThumbnailThumbFull;
import com.freethumbnailmaker.nowatermark.newAdapter.SeeMoreThumbnailListAdapter;

import com.qintong.library.InsLoadingView;
import com.stepstone.apprating.CKt;

import java.util.ArrayList;

public class MorePoster extends Fragment {
    String catName;
    int cat_id;
    public int currentPage = 1;
    public boolean isLastPage = false;
    public boolean isLoading = false;
    int itemCount = 0;
    public InsLoadingView loading_view;
    LinearLayoutManager mLayoutManager;
    ArrayList<ThumbnailThumbFull> posterThumbFulls;
    String ratio;
    RecyclerView recyclerView;
    SeeMoreThumbnailListAdapter seeMorePosterListAdapter;
    ArrayList<Object> snapDataThumb = new ArrayList<>();
    int totalPage = 0;

    static int getCurrentPage(MorePoster morePoster) {
        int i = morePoster.currentPage;
        morePoster.currentPage = i + 1;
        return i;
    }

    public static MorePoster newInstance(ArrayList<ThumbnailThumbFull> arrayList, int i, String str, String str2) {
        MorePoster morePoster = new MorePoster();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(CKt.DIALOG_DATA, arrayList);
        bundle.putInt("cat_id", i);
        bundle.putString("cateName", str);
        bundle.putString("ratio", str2);
        morePoster.setArguments(bundle);
        return morePoster;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.main_poster_fragment, viewGroup, false);

        this.loading_view = inflate.findViewById(R.id.loading_view);
        this.recyclerView = inflate.findViewById(R.id.rv_list);
        this.posterThumbFulls = getArguments().getParcelableArrayList(CKt.DIALOG_DATA);
        this.cat_id = getArguments().getInt("cat_id");
        this.catName = getArguments().getString("cateName");
        this.ratio = getArguments().getString("ratio");
        this.snapDataThumb = new ArrayList<>();
        this.snapDataThumb.addAll(this.posterThumbFulls);
        this.mLayoutManager = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(this.mLayoutManager);
        this.recyclerView.setHasFixedSize(true);
        loadData();
        return inflate;
    }

    private void loadData() {
        RecyclerView recyclerView2;
        if (this.snapDataThumb != null && (recyclerView2 = this.recyclerView) != null) {
            if (this.seeMorePosterListAdapter == null) {
                recyclerView2.addOnScrollListener(new PaginationListener(this.mLayoutManager) {

                    public void loadMoreItems() {
                        MorePoster.this.isLoading = true;
                        MorePoster.getCurrentPage(MorePoster.this);
                        MorePoster.this.getPagingData();
                    }

                    public boolean isLastPage() {
                        return MorePoster.this.isLastPage;
                    }

                    public boolean isLoading() {
                        return MorePoster.this.isLoading;
                    }
                });
            }
            this.seeMorePosterListAdapter = new SeeMoreThumbnailListAdapter(this.cat_id, new ArrayList(), this.ratio, getActivity());
            this.recyclerView.setAdapter(this.seeMorePosterListAdapter);
            getPagingData();
        }
    }


    public void getPagingData() {
        final ArrayList arrayList = new ArrayList();
        double size = this.snapDataThumb.size();
        Double.isNaN(size);
        this.totalPage = Round(size / 10.0d);
        new Handler().postDelayed(() -> {
            for (int i = 0; i < 10; i++) {
                if (MorePoster.this.itemCount < MorePoster.this.snapDataThumb.size()) {
                    arrayList.add(MorePoster.this.snapDataThumb.get(MorePoster.this.itemCount));
                    MorePoster.this.itemCount++;
                }
            }
            if (MorePoster.this.currentPage != 1) {
                MorePoster.this.seeMorePosterListAdapter.removeLoadingView();
            }
            MorePoster.this.seeMorePosterListAdapter.addData(arrayList);
            MorePoster.this.loading_view.setVisibility(View.GONE);
            if (MorePoster.this.currentPage < MorePoster.this.totalPage) {
                MorePoster.this.seeMorePosterListAdapter.addLoadingView();
            } else {
                MorePoster.this.isLastPage = true;
            }
            MorePoster.this.isLoading = false;
        }, 1500);
    }

    public int Round(double d) {
        return Math.abs(d - Math.floor(d)) > 0.1d ? ((int) d) + 1 : (int) d;
    }
}
