package com.freethumbnailmaker.nowatermark.newAdapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.freethumbnailmaker.nowatermark.R;

import com.freethumbnailmaker.nowatermark.main.ThumbCatActivity;
import com.freethumbnailmaker.nowatermark.model.Snap1;
import com.freethumbnailmaker.nowatermark.model.ThumbnailThumbFull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailSnapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GravitySnapHelper.SnapListener {
    Activity context;
    private boolean isLoaderVisible = false;
    private final ArrayList<Object> mSnaps;

    public ThumbnailSnapAdapter(Activity activity, ArrayList<Object> arrayList) {
        this.mSnaps = arrayList;
        this.context = activity;
    }

    @Override
    public int getItemViewType(int i) {
        if (this.isLoaderVisible) {
            if (i == this.mSnaps.size() - 1) {
                return 0;
            }
            if (this.mSnaps.get(i) instanceof String) {
                return 2;
            }
            return 1;
        } else if (this.mSnaps.get(i) instanceof String) {
            return 2;
        } else {
            return 1;
        }
    }

    @NotNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_snap, viewGroup, false));
        }
        if (i != 2) {
            return new LoadingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_view, viewGroup, false));
        }
        return new AdHolder((RelativeLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_ads_frame, viewGroup, false));
    }

    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = getItemViewType(i);
        if (itemViewType == 1) {
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            final Snap1 snap1 = (Snap1) this.mSnaps.get(i);
            if (snap1.getPosterThumbFulls().size() == 0) {
                viewHolder2.llItem.setVisibility(View.GONE);
            } else {
                viewHolder2.llItem.setVisibility(View.VISIBLE);
            }
            viewHolder2.snapTextView.setText(snap1.getText().toUpperCase());
            viewHolder2.recyclerView.setOnFlingListener(null);
            if (snap1.getGravity() == 8388611 || snap1.getGravity() == 8388613) {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
                new GravitySnapHelper(snap1.getGravity(), false, this).attachToRecyclerView(viewHolder2.recyclerView);
            } else if (snap1.getGravity() == 1 || snap1.getGravity() == 16) {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext(), snap1.getGravity() == 1 ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL, false));
                new LinearSnapHelper().attachToRecyclerView(viewHolder2.recyclerView);
            } else if (snap1.getGravity() == 17) {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
                new GravityPagerSnapHelper(GravityCompat.START).attachToRecyclerView(viewHolder2.recyclerView);
            } else {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext()));
                new GravitySnapHelper(snap1.getGravity()).attachToRecyclerView(viewHolder2.recyclerView);
            }
            ArrayList<ThumbnailThumbFull> arrayList = new ArrayList<>();
            if (snap1.getPosterThumbFulls().size() >= 5) {
                for (int i2 = 0; i2 < 5; i2++) {
                    arrayList.add(snap1.getPosterThumbFulls().get(i2));
                }
            } else {
                arrayList = snap1.getPosterThumbFulls();
            }
            viewHolder2.recyclerView.setAdapter(new ThumbnailCategoryWithListAdapter(this.context, snap1.getCat_id(), snap1.getGravity() == 8388611, snap1.getGravity() == 17, arrayList, snap1.getRatio()));
            viewHolder2.seeMoreTextView.setOnClickListener(view -> ((ThumbCatActivity) ThumbnailSnapAdapter.this.context).itemClickSeeMoreAdapter(snap1.getPosterThumbFulls(), snap1.getCat_id(), snap1.getText(), snap1.getRatio()));
        }
    }

    public void addData(List<Object> list) {
        this.mSnaps.addAll(list);
        notifyItemChanged(list.size(), false);
    }

    public void addLoadingView() {
        this.isLoaderVisible = true;
        this.mSnaps.add(new ThumbnailThumbFull());
        notifyItemInserted(this.mSnaps.size() - 1);
    }

    public void removeLoadingView() {
        this.isLoaderVisible = false;
        int size = this.mSnaps.size() - 1;
        if (getItem(size) != null) {
            this.mSnaps.remove(size);
            notifyItemRemoved(size);
        }
    }


    public Object getItem(int i) {
        return this.mSnaps.get(i);
    }

    public int getItemCount() {
        ArrayList<Object> arrayList = this.mSnaps;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public void onSnap(int i) {
        Log.d("Snapped: ", i + "");
    }

    private static class AdHolder extends RecyclerView.ViewHolder {
        RelativeLayout nativeAdLayout;

        AdHolder(RelativeLayout relativeLayout) {
            super(relativeLayout);
            this.nativeAdLayout = relativeLayout;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llItem;
        public RecyclerView recyclerView;
        public TextView seeMoreTextView;
        public TextView snapTextView;

        ViewHolder(View view) {
            super(view);
            this.snapTextView = view.findViewById(R.id.snapTextView);
            this.seeMoreTextView = view.findViewById(R.id.seeMoreTextView);
            this.recyclerView = view.findViewById(R.id.recyclerView);
            this.llItem = view.findViewById(R.id.ll_item);
        }
    }

    public static class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View view) {
            super(view);
        }
    }
}
