package com.freethumbnailmaker.nowatermark.activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.model.ServerData;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;
import com.freethumbnailmaker.nowatermark.utils.Configure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public AppPreference appPreference;
    public Typeface typefaceBold;
    public Typeface typefaceNormal;


    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.typefaceNormal = Typeface.createFromAsset(getAssets(), "font/Montserrat-Medium.ttf");
        this.typefaceBold = Typeface.createFromAsset(getAssets(), "font/Montserrat-SemiBold.ttf");
        this.appPreference = new AppPreference(this);
    }

    public Typeface setBoldFont() {
        return this.typefaceBold;
    }

    public Typeface setNormalFont() {
        return this.typefaceNormal;
    }

    public void setMyFontNormal(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                setMyFontNormal((ViewGroup) childAt);
            } else if (childAt instanceof TextView) {
                ((TextView) childAt).setTypeface(this.typefaceNormal);
            } else if (childAt instanceof Button) {
                ((Button) childAt).setTypeface(this.typefaceNormal);
            } else if (childAt instanceof EditText) {
                ((EditText) childAt).setTypeface(this.typefaceNormal);
            }
        }
    }

    public void setMyFontBold(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                setMyFontNormal((ViewGroup) childAt);
            } else if (childAt instanceof TextView) {
                ((TextView) childAt).setTypeface(this.typefaceBold);
            } else if (childAt instanceof Button) {
                ((Button) childAt).setTypeface(this.typefaceBold);
            } else if (childAt instanceof EditText) {
                ((EditText) childAt).setTypeface(this.typefaceBold);
            }
        }
    }

    public void toGooglePlay() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() >= 1) {
            startActivity(intent);
        }
    }


    public void toShare() {
        try {
            File file = new File(getExternalCacheDir() + "/shareimg.png");
            if (!file.exists()) {
                Bitmap decodeResource = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                decodeResource.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.putExtra("android.intent.extra.TEXT", getResources().getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void copyFile(String str, String str2) {
        try {
            InputStream open = getAssets().open("font/" + str);
            String str3 = str2 + "/" + str;
            if (new File(str3).exists()) {
                Log.e(TAG, "copyAssets: font exist   " + str3);
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(str3);
            Log.e(TAG, "copyFile: " + str);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    open.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void getSticker() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(20000);
        asyncHttpClient.post(Constants.BASE_URL, null, new getAllSticker());
    }

    public void DownoloadSticker(String str, String str2, String str3) {
        AndroidNetworking.download(str, str2, str3).build().startDownload(new DownloadListener() {
            public void onDownloadComplete() {
                Log.e(BaseActivity.TAG, "onDownloadComplete: ");
            }

            public void onError(ANError aNError) {
                Log.e(BaseActivity.TAG, "onError: ");
            }
        });
    }


    public class copyFontBG extends AsyncTask<String, Void, String> {
        @Override
        public void onPostExecute(String str) {
        }

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onProgressUpdate(Void... voidArr) {
        }


        public copyFontBG() {
        }


        public String doInBackground(String... strArr) {
            try {
                File file = new File(Configure.GetFileDir(BaseActivity.this.getApplicationContext()).getPath() + File.separator + "font");
                try {
                    String[] list = BaseActivity.this.getAssets().list("font");
                    if (!file.exists() && !file.mkdir()) {
                        Log.e(BaseActivity.TAG, "No create external directory: " + file);
                    }
                    for (String str : list) {
                        BaseActivity.this.copyFile(str, file.getPath());
                    }
                    return "Executed";
                } catch (IOException e) {
                    Log.e(BaseActivity.TAG, "I/O Exception", e);
                    return "Executed";
                }
            } catch (NullPointerException e2) {
                e2.printStackTrace();
                return "Executed";
            }
        }
    }


    public class copyServerFontBG extends AsyncTask<String, Void, String> {
        @Override
        public void onPostExecute(String str) {
        }

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onProgressUpdate(Void... voidArr) {
        }

        public copyServerFontBG() {
        }


        public String doInBackground(String... strArr) {
            File file = new File(Configure.GetFileDir(BaseActivity.this.getApplicationContext()).getPath() + File.separator + "font/");
            if (!file.exists()) {
                file.mkdirs();
            }
            if (MainActivity.allStickerArrayList == null) {
                return "Executed";
            }
            try {
                if (MainActivity.allStickerArrayList.get(0).getFonts() == null) {
                    return "Executed";
                }
                for (int i = 0; i < MainActivity.allStickerArrayList.get(0).getFonts().size(); i++) {
                    String str = Constants.fURL + MainActivity.allStickerArrayList.get(0).getFonts().get(i);
                    Log.e("url", "====" + str);
                    String str2 = MainActivity.allStickerArrayList.get(0).getFonts().get(i);
                    File file2 = new File(file.getPath() + "/" + MainActivity.allStickerArrayList.get(0).getFonts().get(i));
                    if (file2.exists()) {
                        Log.e(BaseActivity.TAG, "doInBackground: font exist " + file2.getPath());
                    } else {
                        BaseActivity.this.DownoloadSticker(str, file.getPath(), str2);
                    }
                }
                return "Executed";
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return "Executed";
            }
        }
    }

    public void makeStickerDir() {
        this.appPreference = new AppPreference(this);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/.Poster Design Stickers/sticker");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/.Poster Design Stickers/sticker/bg");
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File file3 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/.Poster Design Stickers/sticker/font");
        if (!file3.exists()) {
            file3.mkdirs();
        }
        for (int i = 0; i < 29; i++) {
            File file4 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/.Poster Design Stickers/sticker/cat" + i);
            if (!file4.exists()) {
                file4.mkdirs();
            }
        }
        for (int i2 = 0; i2 < 11; i2++) {
            File file5 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/.Poster Design Stickers/sticker/art" + i2);
            if (!file5.exists()) {
                file5.mkdirs();
            }
        }
        this.appPreference.putString(Constants.sdcardPath, file.getPath());
        Log.e(TAG, "onCreate: " + Constants.sdcardPath);
    }


    public class getAllSticker extends AsyncHttpResponseHandler {
        public getAllSticker() {
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onFinish() {
            Log.e(BaseActivity.TAG, "onFinish: ");
            super.onFinish();
        }

        public void onSuccess(int i, Header[] headerArr, byte[] bArr) {
            try {
                String str = new String(bArr);
                BaseActivity.this.appPreference.putString(Constants.jsonData, str);
                MainActivity.allStickerArrayList = new ArrayList<>();
                MainActivity.allStickerArrayList.add(new Gson().fromJson(str, ServerData.class));
                Log.e(BaseActivity.TAG, "onSuccess: ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onFailure(int i, Header[] headerArr, byte[] bArr, Throwable th) {
            Log.e(BaseActivity.TAG, "onFailure: " + th);
        }
    }
}
