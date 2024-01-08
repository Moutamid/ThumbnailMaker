package com.freethumbnailmaker.nowatermark.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.main.BackgrounImageActivity;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import com.freethumbnailmaker.nowatermark.utility.GlideImageLoader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpStatus;

public class Adapters extends RecyclerView.Adapter<Adapters.ViewHolder> {

    public ArrayList<BackgroundImage> backgroundImages;
    Context context;
    int flagForActivity;
    private final boolean mHorizontal;
    private final boolean mPager;

    public OnClickCallback<ArrayList<String>, Integer, String, Activity> mSingleCallback;
    SharedPreferences preferences;

    public Adapters(Context context2, boolean z, boolean z2, ArrayList<BackgroundImage> arrayList, int i) {
        this.mHorizontal = z;
        this.backgroundImages = arrayList;
        this.mPager = z2;
        this.context = context2;
        this.flagForActivity = i;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context2);
    }

    @NotNull
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (this.mPager) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_pager, viewGroup, false));
        }
        if (this.mHorizontal) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter, viewGroup, false));
        }
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_vertical, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {

        final String str = Constants.BASE_URL_BG + "/background/" + this.backgroundImages.get(i).getImage_url();
        new GlideImageLoader(viewHolder.imageView, viewHolder.mProgressBar).load(Constants.BASE_URL_BG + "/background/" + this.backgroundImages.get(i).getThumb_url(), new RequestOptions().centerCrop().override(HttpStatus.SC_MULTIPLE_CHOICES, HttpStatus.SC_MULTIPLE_CHOICES).placeholder(R.drawable.no_image).error(R.drawable.no_image).priority(Priority.HIGH));
        if (i <= 11 || this.preferences.getBoolean("isBFDPurchased", false)) {
            viewHolder.ivLock.setVisibility(View.GONE);
        } else {
            viewHolder.ivLock.setVisibility(View.GONE);
        }
        viewHolder.imageView.setOnClickListener(view -> {
            if (Adapters.this.flagForActivity == 1) {
                ((BackgrounImageActivity) Adapters.this.context).ongetPositions(str);
            } else {
                Adapters.this.mSingleCallback.onClickCallBack(null, i, str, (FragmentActivity) Adapters.this.context);
            }
        });
    }

    public void setItemClickCallback(OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }

    @Override
    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public int getItemCount() {
        return this.backgroundImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        ImageView ivLock;
        public ProgressBar mProgressBar;
        public TextView nameTextView;
        public TextView ratingTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imageView = view.findViewById(R.id.imageView);
            this.ivLock = view.findViewById(R.id.iv_lock);
            this.nameTextView = view.findViewById(R.id.nameTextView);
            this.ratingTextView = view.findViewById(R.id.ratingTextView);
            this.mProgressBar = view.findViewById(R.id.progressBar1);
        }

        public void onClick(View view) {
            Log.d("backgroundImages", Adapters.this.backgroundImages.get(getAdapterPosition()).getImage_url());
        }
    }
}
