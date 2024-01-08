package com.freethumbnailmaker.nowatermark.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.view.GravityCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.main.BackgrounImageActivity;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;

import com.freethumbnailmaker.nowatermark.model.Snap2;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class VerticalStickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GravitySnapHelper.SnapListener {

    Activity context;
    int flagForActivity;

    public OnClickCallback<ArrayList<String>, ArrayList<BackgroundImage>, String, Activity> mSingleCallback;
    private final ArrayList<Object> mSnaps;

    private final int[] viewTypes;

    public VerticalStickerAdapter(Activity activity, ArrayList<Object> arrayList, int[] iArr, int i) {
        this.mSnaps = arrayList;
        this.context = activity;
        this.viewTypes = iArr;
        this.flagForActivity = i;
    }


    @Override
    public int getItemViewType(int i) {
        return this.viewTypes[i];
    }

    @NotNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_snap, viewGroup, false));

    }

    public void setItemClickCallback(OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }

    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) != 3) {
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            final Snap2 snap2 = (Snap2) this.mSnaps.get(i);
            viewHolder2.snapTextView.setText(snap2.getText().toUpperCase());
            viewHolder2.recyclerView.setOnFlingListener(null);
            if (snap2.getGravity() == 8388611 || snap2.getGravity() == 8388613) {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
                new GravitySnapHelper(snap2.getGravity(), false, this).attachToRecyclerView(viewHolder2.recyclerView);
            } else if (snap2.getGravity() == 1 || snap2.getGravity() == 16) {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext(), snap2.getGravity() == 1 ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL, false));
                new LinearSnapHelper().attachToRecyclerView(viewHolder2.recyclerView);
            } else if (snap2.getGravity() == 17) {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
                new GravityPagerSnapHelper(GravityCompat.START).attachToRecyclerView(viewHolder2.recyclerView);
            } else {
                viewHolder2.recyclerView.setLayoutManager(new LinearLayoutManager(viewHolder2.recyclerView.getContext()));
                new GravitySnapHelper(snap2.getGravity()).attachToRecyclerView(viewHolder2.recyclerView);
            }
            if (((Snap2) this.mSnaps.get(i)).getPosterThumbFulls().size() > 3) {
                viewHolder2.seeMoreTextView.setVisibility(View.VISIBLE);
            } else {
                viewHolder2.seeMoreTextView.setVisibility(View.GONE);
            }
            if (this.flagForActivity == 1) {
                ArrayList<BackgroundImage> arrayList = new ArrayList<>();
                if (snap2.getPosterThumbFulls().size() >= 5) {
                    for (int i3 = 0; i3 < 5; i3++) {
                        arrayList.add(snap2.getPosterThumbFulls().get(i3));
                    }
                } else {
                    arrayList = snap2.getPosterThumbFulls();
                }
                viewHolder2.recyclerView.setAdapter(new AdapterStiker(this.context, snap2.getGravity() == 8388611 || snap2.getGravity() == 8388613 || snap2.getGravity() == 1, snap2.getGravity() == 17, arrayList, this.flagForActivity));
            } else {
                ArrayList<BackgroundImage> arrayList2 = new ArrayList<>();
                if (snap2.getPosterThumbFulls().size() >= 5) {
                    for (int i4 = 0; i4 < 5; i4++) {
                        arrayList2.add(snap2.getPosterThumbFulls().get(i4));
                    }
                } else {
                    arrayList2 = snap2.getPosterThumbFulls();
                }
                AdapterStiker adapterStiker = new AdapterStiker(this.context, snap2.getGravity() == 8388611 || snap2.getGravity() == 8388613 || snap2.getGravity() == 1, snap2.getGravity() == 17, arrayList2, this.flagForActivity);
                viewHolder2.recyclerView.setAdapter(adapterStiker);
                adapterStiker.setItemClickCallback((OnClickCallback<ArrayList<String>, Integer, String, Activity>) (arrayList, num, str, activity) -> VerticalStickerAdapter.this.mSingleCallback.onClickCallBack(null, snap2.getPosterThumbFulls(), str, VerticalStickerAdapter.this.context));
            }
            viewHolder2.seeMoreTextView.setOnClickListener(view -> {
                if (VerticalStickerAdapter.this.flagForActivity == 1) {
                    ((BackgrounImageActivity) VerticalStickerAdapter.this.context).itemClickSeeMoreAdapter(snap2.getPosterThumbFulls());
                } else {
                    VerticalStickerAdapter.this.mSingleCallback.onClickCallBack(null, snap2.getPosterThumbFulls(), "", VerticalStickerAdapter.this.context);
                }
            });
        } else if (this.mSnaps.get(i) == null) {
        }
    }

    public int getItemCount() {
        return this.mSnaps.size();
    }

    public void onSnap(int i) {
        Log.d("Snapped: ", i + "");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;
        public TextView seeMoreTextView;
        public TextView snapTextView;

        public ViewHolder(View view) {
            super(view);
            this.snapTextView = view.findViewById(R.id.snapTextView);
            this.seeMoreTextView = view.findViewById(R.id.seeMoreTextView);
            this.recyclerView = view.findViewById(R.id.recyclerView);
        }
    }


}
