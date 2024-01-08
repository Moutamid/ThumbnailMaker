package com.freethumbnailmaker.nowatermark.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.utility.GlideApp;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class MyThumbnailAdapter extends RecyclerView.Adapter<MyThumbnailAdapter.ViewHolder> {
    Context context;
    File[] listFile;

    public OnClickCallback<ArrayList<String>, Integer, String, Context> mSingleCallback;
    int screenWidth;

    @Override
    public long getItemId(int i) {
        return i;
    }

    public MyThumbnailAdapter(Context context2, File[] fileArr, int i) {
        this.context = context2;
        this.listFile = fileArr;
        this.screenWidth = i;
    }

    @NotNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_itemthumb, viewGroup, false));
        viewGroup.setId(i);
        viewGroup.setFocusable(false);
        viewGroup.setFocusableInTouchMode(false);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.imageview.setId(i);
        viewHolder.imgDeletePoster.setVisibility(View.VISIBLE);
        GlideApp.with(this.context).load(this.listFile[i]).fitCenter().apply(((new RequestOptions().dontAnimate()).placeholder(R.drawable.no_image)).error(R.drawable.no_image)).into(viewHolder.imageview);
        viewHolder.imgDeletePoster.setOnClickListener(view -> MyThumbnailAdapter.this.mSingleCallback.onClickCallBack(null, i, "0", MyThumbnailAdapter.this.context));
        viewHolder.imageview.setOnClickListener(view -> MyThumbnailAdapter.this.mSingleCallback.onClickCallBack(null, i, "1", MyThumbnailAdapter.this.context));
    }

    public int getItemCount() {
        return this.listFile.length;
    }

    public void setItemClickCallback(OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        ImageView imgDeletePoster;

        public ViewHolder(View view) {
            super(view);
            this.imageview = view.findViewById(R.id.image);
            this.imgDeletePoster = view.findViewById(R.id.imgDeletePoster);
        }
    }
}
