package com.freethumbnailmaker.nowatermark.newAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.asm.Opcodes;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.main.ThumbCatActivity;
import com.freethumbnailmaker.nowatermark.model.ThumbnailThumbFull;
import com.freethumbnailmaker.nowatermark.utility.GlideImageLoaderPoster;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import cz.msebera.android.httpclient.HttpStatus;

public class ThumbnailCategoryWithListAdapter extends RecyclerView.Adapter<ThumbnailCategoryWithListAdapter.ViewHolder> {
    private final AppPreference appPreference;
    int cat_id;
    Context context;
    private final boolean mHorizontal;
    private final boolean mPager;
    String ratio;

    public ArrayList<ThumbnailThumbFull> thumbnailThumbFulls;

    public ThumbnailCategoryWithListAdapter(Context context2, int i, boolean z, boolean z2, ArrayList<ThumbnailThumbFull> arrayList, String str) {
        this.mHorizontal = z;
        this.thumbnailThumbFulls = arrayList;
        this.mPager = z2;
        this.context = context2;
        this.cat_id = i;
        this.ratio = str;
        this.appPreference = new AppPreference(context2);
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

    public void onBindViewHolder(@NotNull final ViewHolder viewHolder, final int i) {
        if (i <= 2 || this.appPreference.getInt(Constants.isRated, 0) != 0) {
            viewHolder.ivLock.setVisibility(View.GONE);
        } else {
            viewHolder.ivLock.setVisibility(View.GONE);
        }
        if (this.ratio.equalsIgnoreCase("27:10")) {
            viewHolder.imageView.getLayoutParams().width = 621;
            viewHolder.imageView.getLayoutParams().height = 230;
        } else if (this.ratio.equalsIgnoreCase("16:9") || this.ratio.equalsIgnoreCase("32:18") || this.ratio.equalsIgnoreCase("135:76") || this.ratio.equalsIgnoreCase("3:1")) {
            viewHolder.imageView.getLayoutParams().width = 533;
            viewHolder.imageView.getLayoutParams().height = HttpStatus.SC_MULTIPLE_CHOICES;
        } else if (this.ratio.equalsIgnoreCase("56:17")) {
            viewHolder.imageView.getLayoutParams().width = 533;
            viewHolder.imageView.getLayoutParams().height = Opcodes.IF_ACMPEQ;
        } else if (this.ratio.equalsIgnoreCase("2:3")) {
            viewHolder.imageView.getLayoutParams().width = 200;
            viewHolder.imageView.getLayoutParams().height = HttpStatus.SC_MULTIPLE_CHOICES;
        }
        new GlideImageLoaderPoster(viewHolder.imageView, viewHolder.mProgressBar).load(this.thumbnailThumbFulls.get(i).getPost_thumb(), new RequestOptions().centerCrop().priority(Priority.HIGH));
        viewHolder.imageView.setOnClickListener(view -> {
            ThumbCatActivity thumbCatActivity = (ThumbCatActivity) ThumbnailCategoryWithListAdapter.this.context;
            if (viewHolder.ivLock.getVisibility() == View.GONE) {
                thumbCatActivity.openPosterActivity(ThumbnailCategoryWithListAdapter.this.thumbnailThumbFulls.get(i).getPost_id(), ThumbnailCategoryWithListAdapter.this.cat_id);
            }
        });
    }
    @Override
    public int getItemViewType(int i) {
        return super.getItemViewType(i);
    }

    public int getItemCount() {
        return this.thumbnailThumbFulls.size();
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
            Log.d("backgroundImages", "==" + ThumbnailCategoryWithListAdapter.this.thumbnailThumbFulls.get(getAdapterPosition()).getPost_id());
        }
    }
}
