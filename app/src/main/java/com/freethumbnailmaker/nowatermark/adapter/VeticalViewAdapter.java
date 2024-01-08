package com.freethumbnailmaker.nowatermark.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;
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
import java.util.List;

public class VeticalViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GravitySnapHelper.SnapListener {

    Activity context;
    int flagForActivity;

    public OnClickCallback<ArrayList<String>, ArrayList<BackgroundImage>, String, Activity> mSingleCallback;

    public ArrayList<Object> mSnaps;

    public void onSnap(int i) {
    }

    public VeticalViewAdapter(Activity activity, ArrayList<Object> arrayList, int i) {
        this.mSnaps = arrayList;
        this.context = activity;
        this.flagForActivity = i;
    }

    @Override
    public int getItemViewType(int i) {
        if (this.mSnaps.get(i) == null) {
            return 0;
        }
        return this.mSnaps.get(i).equals("Ads") ? 2 : 1;
    }

    @NotNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_snap, viewGroup, false));
        }
        if (i != 2) {
            return new LoadingHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_view, viewGroup, false));
        }
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_snap, viewGroup, false));

    }

    public void addData(List<Object> list) {
        notifyDataSetChanged();
    }

    public void addLoadingView() {
        new Handler().post(() -> {
            VeticalViewAdapter.this.mSnaps.add(null);
            VeticalViewAdapter veticalViewAdapter = VeticalViewAdapter.this;
            veticalViewAdapter.notifyItemInserted(veticalViewAdapter.mSnaps.size() - 1);
        });
    }

    public void removeLoadingView() {
        ArrayList<Object> arrayList = this.mSnaps;
        arrayList.remove(arrayList.size() - 1);
        notifyItemRemoved(this.mSnaps.size());
    }

    public void setItemClickCallback(OnClickCallback onClickCallback) {
        this.mSingleCallback = onClickCallback;
    }

    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = getItemViewType(i);
        if (itemViewType == 1) {
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
            ArrayList<BackgroundImage> arrayList = new ArrayList<>();
            if (snap2.getPosterThumbFulls().size() >= 5) {
                for (int i2 = 0; i2 < 5; i2++) {
                    arrayList.add(snap2.getPosterThumbFulls().get(i2));
                }
            } else {
                arrayList = snap2.getPosterThumbFulls();
            }
            ArrayList<BackgroundImage> arrayList2 = arrayList;
            if (this.flagForActivity == 1) {
                viewHolder2.recyclerView.setAdapter(new Adapters(this.context, snap2.getGravity() == 8388611 || snap2.getGravity() == 8388613 || snap2.getGravity() == 1, snap2.getGravity() == 17, arrayList2, this.flagForActivity));
            } else {
                Adapters adapters = new Adapters(this.context, snap2.getGravity() == 8388611 || snap2.getGravity() == 8388613 || snap2.getGravity() == 1, snap2.getGravity() == 17, arrayList2, this.flagForActivity);
                viewHolder2.recyclerView.setAdapter(adapters);
                adapters.setItemClickCallback((OnClickCallback<ArrayList<String>, Integer, String, Activity>) (arrayList1, num, str, activity) -> VeticalViewAdapter.this.mSingleCallback.onClickCallBack(null, snap2.getPosterThumbFulls(), str, (FragmentActivity) VeticalViewAdapter.this.context));
            }
            viewHolder2.seeMoreTextView.setOnClickListener(view -> {
                if (VeticalViewAdapter.this.flagForActivity == 1) {
                    ((BackgrounImageActivity) VeticalViewAdapter.this.context).itemClickSeeMoreAdapter(snap2.getPosterThumbFulls());
                } else {
                    VeticalViewAdapter.this.mSingleCallback.onClickCallBack(null, snap2.getPosterThumbFulls(), "", VeticalViewAdapter.this.context);
                }
            });
        }
    }

    public int getItemCount() {
        return this.mSnaps.size();
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

    public class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View view) {
            super(view);
        }
    }


}
