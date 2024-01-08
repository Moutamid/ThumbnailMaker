package com.freethumbnailmaker.nowatermark.create;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.main.ThumbnailActivity;



public class BlurOperationAsync extends AsyncTask<String, Void, String> {
    ImageView background_blur;
    Bitmap btmp;
    Activity context;

    private ProgressDialog pd;


    public String doInBackground(String... strArr) {
        return "yes";
    }

    public BlurOperationAsync(ThumbnailActivity thumbnailActivity, Bitmap bitmap, ImageView imageView) {
        this.context = thumbnailActivity;
        this.btmp = bitmap;
        this.background_blur = imageView;
    }

    @Override
    public void onPreExecute() {
        this.pd = new ProgressDialog(this.context);
        this.pd.setMessage(this.context.getResources().getString(R.string.plzwait));
        this.pd.setCancelable(false);
        this.pd.show();
    }

    @Override
    public void onPostExecute(String str) {
        this.pd.dismiss();
    }


}
