package com.freethumbnailmaker.nowatermark.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.main.ThumbCatActivity;

public class MyFramesAdapter extends RecyclerView.Adapter<MyFramesAdapter.ViewHolder> {
    private final int cellLimit = 0;

    Context context;
    int[] thumbnail;

    public MyFramesAdapter(Context context2, int[] iArr, int i, int i2) {
        this.context = context2;
        this.thumbnail = iArr;

    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_views, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        RequestOptions requestOptions = new RequestOptions();
        (((requestOptions.dontAnimate()).diskCacheStrategy(DiskCacheStrategy.NONE)).skipMemoryCache(true)).autoClone();
        Glide.with(this.context).load(this.thumbnail[i]).thumbnail(0.1f).apply(requestOptions).into(viewHolder.mThumbnail);
        viewHolder.mThumbnail.setOnClickListener(view -> {
            Intent intent = new Intent(MyFramesAdapter.this.context, ThumbCatActivity.class);

            if (i == 0) {
                intent.putExtra("ratio", "27:10");
                intent.putExtra("sizeposition", "851:315");
                MyFramesAdapter.this.context.startActivity(intent);
            } else if (i == 1) {
                intent.putExtra("ratio", "16:9");
                intent.putExtra("sizeposition", "1280:720");
                MyFramesAdapter.this.context.startActivity(intent);
            } else if (i == 2) {
                intent.putExtra("ratio", "32:18");
                intent.putExtra("sizeposition", "2560:1440");
                MyFramesAdapter.this.context.startActivity(intent);
            } else if (i == 3) {
                intent.putExtra("ratio", "1:1");
                intent.putExtra("sizeposition", "1024:1024");
                MyFramesAdapter.this.context.startActivity(intent);
            } else if (i == 4) {
                intent.putExtra("ratio", "135:76");
                intent.putExtra("sizeposition", "1080:608");
                MyFramesAdapter.this.context.startActivity(intent);
            } else if (i == 5) {
                intent.putExtra("ratio", "3:1");
                intent.putExtra("sizeposition", "1500:500");
                MyFramesAdapter.this.context.startActivity(intent);
            } else if (i == 6) {
                intent.putExtra("ratio", "56:17");
                intent.putExtra("sizeposition", "1440:425");
                MyFramesAdapter.this.context.startActivity(intent);
            } else if (i == 7) {
                intent.putExtra("ratio", "2:3");
                intent.putExtra("sizeposition", "512:800");
                MyFramesAdapter.this.context.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        int length = this.thumbnail.length;
        int i = this.cellLimit;
        return i > 0 ? Math.min(length, i) : length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mThumbnail = (this.itemView.findViewById(R.id.img));

        public ViewHolder(View view) {
            super(view);
        }
    }
}
