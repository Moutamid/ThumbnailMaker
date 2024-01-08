package com.freethumbnailmaker.nowatermark.newAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import com.freethumbnailmaker.nowatermark.R;

import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.main.ThumbCatActivity;
import com.freethumbnailmaker.nowatermark.model.ThumbnailThumbFull;

import com.freethumbnailmaker.nowatermark.utility.GlideImageLoader;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SeeMoreThumbnailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final AppPreference appPreference;
    int catID;

    public Activity context;
    private boolean isLoaderVisible = false;

    private final ArrayList<Object> posterDatas;
    String ratio;

    @Override
    public long getItemId(int i) {
        return i;
    }

    public SeeMoreThumbnailListAdapter(int i, ArrayList<Object> arrayList, String str, Activity activity) {
        this.posterDatas = arrayList;
        this.context = activity;
        this.catID = i;
        this.ratio = str;
        this.appPreference = new AppPreference(activity);

    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_more_poster, viewGroup, false));
        }
        if (i != 2) {
            return new LoadingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_view, viewGroup, false));
        }
        return new AdHolder((RelativeLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_ads_frame, viewGroup, false));
    }

    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = getItemViewType(i);
        if (itemViewType == 1) {
            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
            final ThumbnailThumbFull thumbnailThumbFull = (ThumbnailThumbFull) this.posterDatas.get(i);
            if (this.ratio.equals("1:1")) {
                myViewHolder.ivImage.getLayoutParams().width = -1;
                myViewHolder.ivImage.getLayoutParams().height = 800;
                myViewHolder.ivImage.setAdjustViewBounds(true);
            }

            new GlideImageLoader(myViewHolder.ivImage, myViewHolder.mProgressBar)
                    .load(thumbnailThumbFull.getPost_thumb(), new RequestOptions().centerCrop().priority(Priority.HIGH));
            if (i <= 2 || this.appPreference.getInt(Constants.isRated, 0) != 0) {
                myViewHolder.ivLock.setVisibility(View.GONE);
            } else {
                myViewHolder.ivLock.setVisibility(View.GONE);
            }
            myViewHolder.cardView.setOnClickListener(view -> ((ThumbCatActivity) SeeMoreThumbnailListAdapter.this.context).openPosterActivity(thumbnailThumbFull.getPost_id(), SeeMoreThumbnailListAdapter.this.catID));
        }
    }

    public void addData(List<Object> list) {
        this.posterDatas.addAll(list);
        notifyItemChanged(list.size(), false);
    }

    public void addLoadingView() {
        this.isLoaderVisible = true;
        this.posterDatas.add(new ThumbnailThumbFull());
        notifyItemInserted(this.posterDatas.size() - 1);
    }

    public void removeLoadingView() {
        this.isLoaderVisible = false;
        int size = this.posterDatas.size() - 1;
        if (getItem(size) != null) {
            this.posterDatas.remove(size);
            notifyItemRemoved(size);
        }
    }


    public Object getItem(int i) {
        return this.posterDatas.get(i);
    }

    public int getItemCount() {
        ArrayList<Object> arrayList = this.posterDatas;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int i) {
        if (this.isLoaderVisible) {
            if (i == this.posterDatas.size() - 1) {
                return 0;
            }
            if (this.posterDatas.get(i) instanceof String) {
                return 2;
            }
            return 1;
        } else if (this.posterDatas.get(i) instanceof String) {
            return 2;
        } else {
            return 1;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivImage;
        ImageView ivLock;
        ImageView ivRateUs;
        ProgressBar mProgressBar;

        public MyViewHolder(View view) {
            super(view);
            this.ivImage = view.findViewById(R.id.iv_image);
            this.ivRateUs = view.findViewById(R.id.iv_rate_us);
            this.ivLock = view.findViewById(R.id.iv_lock);
            this.cardView = view.findViewById(R.id.cv_image);
            this.mProgressBar = view.findViewById(R.id.progressBar1);
        }
    }

    private static class AdHolder extends RecyclerView.ViewHolder {
        RelativeLayout nativeAdLayout;

        AdHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);
            this.nativeAdLayout = relativeLayout;
        }
    }

    public static class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View view) {
            super(view);
        }
    }
}
