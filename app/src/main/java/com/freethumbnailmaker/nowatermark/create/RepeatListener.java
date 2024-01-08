package com.freethumbnailmaker.nowatermark.create;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RepeatListener implements View.OnTouchListener {

    public final View.OnClickListener clickListener;

    public View downView;
    private ImageView guideline;

    public Handler handler = new Handler();
    private Runnable handlerRunnable = new Runnable() {
        public void run() {
            RepeatListener.this.handler.postDelayed(this, (long) RepeatListener.this.normalInterval);
            RepeatListener.this.clickListener.onClick(RepeatListener.this.downView);
        }
    };
    private int initialInterval;

    public final int normalInterval;

    public RepeatListener(int i, int i2, ImageView imageView, View.OnClickListener onClickListener) {
        if (onClickListener == null) {
            throw new IllegalArgumentException("null runnable");
        } else if (i < 0 || i2 < 0) {
            throw new IllegalArgumentException("negative interval");
        } else {
            this.initialInterval = i;
            this.normalInterval = i2;
            this.clickListener = onClickListener;
            this.guideline = imageView;
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action == 1) {
                this.guideline.setVisibility(View.GONE);
            } else if (action != 3) {
                return false;
            }
            this.handler.removeCallbacks(this.handlerRunnable);
            this.downView.setPressed(false);
            this.downView = null;
            return true;
        }

        if (this.guideline.getVisibility() == View.GONE) {
            this.guideline.setVisibility(View.VISIBLE);
        }
        this.handler.removeCallbacks(this.handlerRunnable);
        this.handler.postDelayed(this.handlerRunnable, (long) this.initialInterval);
        this.downView = view;
        this.downView.setPressed(true);
        this.clickListener.onClick(view);
        return true;
    }
}
