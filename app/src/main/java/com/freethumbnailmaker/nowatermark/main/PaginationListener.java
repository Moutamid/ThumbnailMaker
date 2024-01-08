package com.freethumbnailmaker.nowatermark.main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    @NonNull
    private final LinearLayoutManager layoutManager;

    public abstract boolean isLastPage();

    public abstract boolean isLoading();


    public abstract void loadMoreItems();

    public PaginationListener(@NonNull LinearLayoutManager linearLayoutManager) {
        this.layoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
        super.onScrolled(recyclerView, i, i2);
        int childCount = this.layoutManager.getChildCount();
        int itemCount = this.layoutManager.getItemCount();
        int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage() && childCount + findFirstVisibleItemPosition >= itemCount && findFirstVisibleItemPosition >= 0 && itemCount >= 10) {
            loadMoreItems();
        }
    }
}
