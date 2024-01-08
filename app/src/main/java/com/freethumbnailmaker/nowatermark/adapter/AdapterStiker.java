package com.freethumbnailmaker.nowatermark.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.bumptech.glide.request.RequestOptions;

import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import com.freethumbnailmaker.nowatermark.network.ConnectivityReceiver;
import com.freethumbnailmaker.nowatermark.utility.GlideApp;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class AdapterStiker extends RecyclerView.Adapter<AdapterStiker.ViewHolder> {

    public AppPreference appPreference;

    public ArrayList<BackgroundImage> backgroundImages;
    Context context;
    int flagForActivity;

    public boolean isDownloadProgress = true;
    private final boolean mHorizontal;
    private final boolean mPager;

    public OnClickCallback<ArrayList<String>, Integer, String, Activity> mSingleCallback;
    SharedPreferences preferences;

    public AdapterStiker(Context context2, boolean z, boolean z2, ArrayList<BackgroundImage> arrayList, int i) {
        this.mHorizontal = z;
        this.backgroundImages = arrayList;
        this.mPager = z2;
        this.context = context2;
        this.flagForActivity = i;
        this.appPreference = new AppPreference(this.context);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context2);
    }

    @NotNull
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (this.mPager) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_pager, viewGroup, false));
        }
        if (this.mHorizontal) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapters, viewGroup, false));
        }
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_vertical, viewGroup, false));
    }

    public static String getFileNameFromUrl(String str) {
        return str.substring(str.lastIndexOf(47) + 1).split("\\?")[0].split("#")[0];
    }

    public void onBindViewHolder(@NotNull final ViewHolder viewHolder, final int i) {
        final BackgroundImage backgroundImage = this.backgroundImages.get(i);
        Log.e("catname", "==" + backgroundImage.getCategory_name());
        String str = Constants.BASE_URL_BG + "background/Sticker/" + backgroundImage.getImage_url();
        String[] split = Uri.parse(str).getPath().split("/");
        final String str2 = split[split.length - 2];
        Log.e("url", "==" + str2);
        Log.e("url", "==" + str);
        File file = new File(this.appPreference.getString(Constants.sdcardPath) + "/cat/" + str2 + "/" + getFileNameFromUrl(str));
        if (file.exists()) {
            viewHolder.imgDownload.setVisibility(View.GONE);
            viewHolder.mProgressBar.setVisibility(View.GONE);
            GlideApp.with(this.context).load(file.getPath()).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().fitCenter().placeholder(R.drawable.no_image).error(R.drawable.no_image)).into(viewHolder.imageView);
        } else {
            viewHolder.imgDownload.setVisibility(View.VISIBLE);
            GlideApp.with(this.context).load(str).thumbnail(0.1f).apply(new RequestOptions().dontAnimate().fitCenter().placeholder(R.drawable.no_image).error(R.drawable.no_image)).into(viewHolder.imageView);
        }
        viewHolder.imgDownload.setOnClickListener(view -> {
            if (!ConnectivityReceiver.isConnected()) {
                Toast.makeText(AdapterStiker.this.context, "No Internet Connection!!!", Toast.LENGTH_SHORT).show();
            } else if (AdapterStiker.this.isDownloadProgress) {
               AdapterStiker.this.isDownloadProgress = false;
                viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                String str1 = Constants.BASE_URL_BG + "background/Sticker/" + backgroundImage.getImage_url();
                File file1 = new File(AdapterStiker.this.appPreference.getString(Constants.sdcardPath) + "/cat/" + str2 + "/");
                String fileNameFromUrl = AdapterStiker.getFileNameFromUrl(str1);
                viewHolder.imgDownload.setVisibility(View.GONE);
                AdapterStiker.this.DownoloadSticker(str1, file1.getPath(), fileNameFromUrl);
            } else {
                Toast.makeText(AdapterStiker.this.context, "Please wait..", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.imageView.setOnClickListener(view -> {
            String str12 = Constants.BASE_URL_BG + "background/Sticker/" + backgroundImage.getImage_url();
            File file12 = new File(AdapterStiker.this.appPreference.getString(Constants.sdcardPath) + "/cat/" + str2 + "/" + AdapterStiker.getFileNameFromUrl(str12));
            if (file12.exists()) {
                AdapterStiker.this.mSingleCallback.onClickCallBack(null, i, file12.getPath(), (FragmentActivity) AdapterStiker.this.context);
            }
        });
    }

    public void DownoloadSticker(String str, String str2, String str3) {
        AndroidNetworking.download(str, str2, str3).build().startDownload(new DownloadListener() {
            public void onDownloadComplete() {
                AdapterStiker.this.isDownloadProgress = true;
                AdapterStiker.this.notifyDataSetChanged();
            }

            public void onError(ANError aNError) {
                AdapterStiker.this.isDownloadProgress = true;
                AdapterStiker.this.notifyDataSetChanged();
                Toast.makeText(AdapterStiker.this.context, "Network Error", Toast.LENGTH_SHORT).show();
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
        RelativeLayout imgDownload;
        ImageView ivLock;
        public ProgressBar mProgressBar;
        public TextView nameTextView;
        public TextView ratingTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.imgDownload = view.findViewById(R.id.imgDownload);
            this.imageView = view.findViewById(R.id.imageView);
            this.ivLock = view.findViewById(R.id.iv_lock);
            this.nameTextView = view.findViewById(R.id.nameTextView);
            this.ratingTextView = view.findViewById(R.id.ratingTextView);
            this.mProgressBar = view.findViewById(R.id.progressBar1);
        }

        public void onClick(View view) {
            Log.d("backgroundImages", AdapterStiker.this.backgroundImages.get(getAdapterPosition()).getImage_url());
        }
    }
}
