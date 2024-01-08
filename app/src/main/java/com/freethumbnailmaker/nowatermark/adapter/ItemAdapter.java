package com.freethumbnailmaker.nowatermark.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.util.Pair;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.text.AutofitTextRel;
import com.freethumbnailmaker.nowatermark.utility.ImageUtils;
import com.freethumbnailmaker.nowatermark.view.AutoResizeTextView;
import com.freethumbnailmaker.nowatermark.view.StickerView;
import com.woxthebox.draglistview.DragItemAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemAdapter extends DragItemAdapter<Pair<Long, View>, ItemAdapter.ViewHolder> {
    Activity activity;

    public boolean mDragOnLongPress;

    public int mGrabHandleId;
    private final int mLayoutId;

    public ItemAdapter(Activity activity2, ArrayList<Pair<Long, View>> arrayList, int i, int i2, boolean z) {
        this.mLayoutId = i;
        this.mGrabHandleId = i2;
        this.activity = activity2;
        this.mDragOnLongPress = z;
        setItemList(arrayList);
    }

    @NotNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.mLayoutId, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NotNull final ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        final View view =  this.mItemList.get(i).second;
        try {
            if (view instanceof StickerView) {
                View childAt = ((StickerView) view).getChildAt(1);
                Bitmap createBitmap = Bitmap.createBitmap(childAt.getWidth(), childAt.getHeight(), Bitmap.Config.ARGB_8888);
                childAt.draw(new Canvas(createBitmap));
                float[] fArr = new float[9];
                ((ImageView) childAt).getImageMatrix().getValues(fArr);
                float f = fArr[0];
                float f2 = fArr[4];
                Drawable drawable = ((ImageView) childAt).getDrawable();
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                int round = Math.round(((float) intrinsicWidth) * f);
                int round2 = Math.round(((float) intrinsicHeight) * f2);
                viewHolder.mImage.setImageBitmap(Bitmap.createBitmap(createBitmap, (createBitmap.getWidth() - round) / 2, (createBitmap.getHeight() - round2) / 2, round, round2));
                viewHolder.mImage.setRotationY(childAt.getRotationY());
                viewHolder.mImage.setTag(this.mItemList.get(i));
                viewHolder.mImage.setAlpha(1.0f);
                viewHolder.textView.setText(" ");
            }
            if (view instanceof AutofitTextRel) {
                viewHolder.textView.setText(((AutoResizeTextView) ((AutofitTextRel) view).getChildAt(2)).getText());
                viewHolder.textView.setTypeface(((AutoResizeTextView) ((AutofitTextRel) view).getChildAt(2)).getTypeface());
                viewHolder.textView.setTextColor(((AutoResizeTextView) ((AutofitTextRel) view).getChildAt(2)).getTextColors());
                viewHolder.textView.setGravity(17);
                viewHolder.textView.setMinTextSize(10.0f);
                if (((AutofitTextRel) view).getTextInfo().getBG_COLOR() != 0) {
                    Bitmap createBitmap2 = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
                    new Canvas(createBitmap2).drawColor(((AutofitTextRel) view).getTextInfo().getBG_COLOR());
                    viewHolder.mImage.setImageBitmap(createBitmap2);
                    viewHolder.mImage.setAlpha(((float) ((AutofitTextRel) view).getTextInfo().getBG_ALPHA()) / 255.0f);
                } else if (((AutofitTextRel) view).getTextInfo().getBG_DRAWABLE().equals("0")) {
                    viewHolder.mImage.setAlpha(1.0f);
                } else {
                    viewHolder.mImage.setImageBitmap(ImageUtils.getTiledBitmap(this.activity, this.activity.getResources().getIdentifier(((AutofitTextRel) view).getTextInfo().getBG_DRAWABLE(), "drawable", this.activity.getPackageName()), 150, 150));
                    viewHolder.mImage.setAlpha(((float) ((AutofitTextRel) view).getTextInfo().getBG_ALPHA()) / 255.0f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (view instanceof StickerView) {
            if (((StickerView) view).isMultiTouchEnabled) {
                viewHolder.img_lock.setImageResource(R.drawable.ic_unlock);
            } else {
                viewHolder.img_lock.setImageResource(R.drawable.ic_lock);
            }
        }
        if (view instanceof AutofitTextRel) {
            if (((AutofitTextRel) view).isMultiTouchEnabled) {
                viewHolder.img_lock.setImageResource(R.drawable.ic_unlock);
            } else {
                viewHolder.img_lock.setImageResource(R.drawable.ic_lock);
            }
        }
        viewHolder.img_lock.setOnClickListener(view1 -> {
            Log.d("qq","img_lock onclick");
            if (view1 instanceof StickerView) {
                if (((StickerView) view1).isMultiTouchEnabled) {
                    ((StickerView) view1).isMultiTouchEnabled = ((StickerView) view1).setDefaultTouchListener(false);
                    viewHolder.img_lock.setImageResource(R.drawable.ic_lock);
                } else {
                    ((StickerView) view1).isMultiTouchEnabled = ((StickerView) view1).setDefaultTouchListener(true);
                    viewHolder.img_lock.setImageResource(R.drawable.ic_unlock);
                }
            }
            if (view1 instanceof AutofitTextRel) {
                if (((AutofitTextRel) view1).isMultiTouchEnabled) {
                    ((AutofitTextRel) view1).isMultiTouchEnabled = ((AutofitTextRel) view1).setDefaultTouchListener(false);
                    viewHolder.img_lock.setImageResource(R.drawable.ic_lock);
                    return;
                }
                ((AutofitTextRel) view1).isMultiTouchEnabled = ((AutofitTextRel) view1).setDefaultTouchListener(true);
                viewHolder.img_lock.setImageResource(R.drawable.ic_unlock);
            }
        });
    }

    public long getUniqueItemId(int i) {
        return this.mItemList.get(i).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        ImageView img_lock;
        ImageView mImage;
        TextView mText;
        AutoResizeTextView textView;
        @Override
        public void onItemClicked(View view) {
        }
        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }

        ViewHolder(View view) {
            super(view, ItemAdapter.this.mGrabHandleId, ItemAdapter.this.mDragOnLongPress);
            this.mText = view.findViewById(R.id.text);
            this.mImage = view.findViewById(R.id.image1);
            this.img_lock = view.findViewById(R.id.img_lock);
            this.textView = view.findViewById(R.id.auto_fit_edit_text);
        }
    }
}
