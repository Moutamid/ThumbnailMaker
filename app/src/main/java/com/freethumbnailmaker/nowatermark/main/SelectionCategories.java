package com.freethumbnailmaker.nowatermark.main;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.activity.BaseActivity;
import com.freethumbnailmaker.nowatermark.adapter.MyFramesAdapter;
import com.freethumbnailmaker.nowatermark.utility.GridSpacingItemDecoration;

public class SelectionCategories extends BaseActivity {
    ImageView mIvBack;
    MyFramesAdapter myFramesAdapter;
    RecyclerView recyclerView;
    TextView text;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView( R.layout.selection_thumbnail);
        this.mIvBack =  findViewById(R.id.iv_back);
        this.text = findViewById(R.id.text);
        this.text.setTypeface(setBoldFont());
        this.recyclerView =  findViewById(R.id.gridView);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 10, true));
        this.myFramesAdapter = new MyFramesAdapter(this, Constants.Thumbnail_Selection, getResources().getDimensionPixelSize(R.dimen.image_size), getResources().getDimensionPixelSize(R.dimen.image_padding));
        this.recyclerView.setAdapter(this.myFramesAdapter);
        this.mIvBack.setOnClickListener(view -> SelectionCategories.this.onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
