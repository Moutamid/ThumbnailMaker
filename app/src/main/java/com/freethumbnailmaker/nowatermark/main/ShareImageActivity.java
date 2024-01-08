package com.freethumbnailmaker.nowatermark.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.freethumbnailmaker.nowatermark.R;

import com.freethumbnailmaker.nowatermark.utils.AppPreference;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class ShareImageActivity extends AppCompatActivity implements View.OnClickListener, RatingDialogListener {
    private static final String GOOGLE_PLAY_CONSTANT = "http://play.google.com/store/apps/details?id=";
    private static final String MARKET_CONSTANT = "market://details?id=";
    private static final String TAG = "ShareImageActivity";

    public AppPreference appPreference;

    public ImageView imageView;

    private final BroadcastReceiver myBroadcast_update = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!intent.getStringExtra("Billing").equals("BillingRemoveAds") || !ShareImageActivity.this.appPreference.getBoolean("isAdsDisabled", false)) {
                Log.e("Billing", "" + intent.getStringExtra("Billing"));
            } else if (intent.getStringExtra("Billing").equals("BillingRemoveAds")) {
                ShareImageActivity.this.removeWaterMark.setVisibility(View.GONE);
                ShareImageActivity.this.saveBitmap();
            }
        }
    };

    public Uri phototUri = null;
    public File pictureFile;
    public RelativeLayout removeWaterMark;
    public Typeface typefaceBold;
    public Typeface typefaceNormal;

    public void onNegativeButtonClicked() {
    }

    public void onNeutralButtonClicked() {
    }

    public Typeface setBoldFont() {
        return this.typefaceBold;
    }

    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView(R.layout.activity_share_image);
        this.typefaceBold = Typeface.createFromAsset(getAssets(), "font/Montserrat-SemiBold.ttf");
        this.typefaceNormal = Typeface.createFromAsset(getAssets(), "font/Montserrat-Medium.ttf");
        this.appPreference = new AppPreference(this);
        findView();
        if (this.appPreference.getInt(Constants.isRated, 0) == 0) {
            newLibRateDialog();
        }
        initUI();
        registerReceiver(this.myBroadcast_update, new IntentFilter("BillingUpdate"));

    }

    public Typeface setNormalFont() {
        return this.typefaceNormal;
    }

    private void findView() {
        TextView txt_remove = findViewById(R.id.txt_remove);
        this.removeWaterMark = findViewById(R.id.btn_remowatermark);
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView txtToolbar = findViewById(R.id.txt_toolbar);
        ImageView btnShareMore = findViewById(R.id.btnShareMore);
        ImageView btnMoreApp = findViewById(R.id.btnMoreApp);
        ImageView btnShareFacebook = findViewById(R.id.btnShareFacebook);
        ImageView btnShareIntagram = findViewById(R.id.btnShareIntagram);
        ImageView btnShareWhatsapp = findViewById(R.id.btnShareWhatsapp);
        ImageView btnShareGooglePlus = findViewById(R.id.btnShareGooglePlus);
        ImageView btnSharewMessanger = findViewById(R.id.btnSharewMessanger);
        ImageView btnShareTwitter = findViewById(R.id.btnShareTwitter);
        ImageView btnShareHike = findViewById(R.id.btnShareHike);
        ImageView btnShareMoreImage = findViewById(R.id.btnShareMoreImage);
        btnBack.setOnClickListener(this);
        btnShareMore.setOnClickListener(this);
        btnMoreApp.setOnClickListener(this);
        btnShareFacebook.setOnClickListener(this);
        btnShareIntagram.setOnClickListener(this);
        btnShareWhatsapp.setOnClickListener(this);
        btnShareGooglePlus.setOnClickListener(this);
        btnSharewMessanger.setOnClickListener(this);
        btnShareTwitter.setOnClickListener(this);
        btnShareHike.setOnClickListener(this);
        btnShareMoreImage.setOnClickListener(this);
        this.removeWaterMark.setOnClickListener(this);
        txt_remove.setTypeface(setBoldFont());
        txtToolbar.setTypeface(setBoldFont());
    }

    public void initUI() {
        this.imageView = findViewById(R.id.image);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String oldpath = extras.getString("uri");
            if (oldpath.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.picUpImg), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                this.phototUri = Uri.parse(oldpath);
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.picUpImg), Toast.LENGTH_SHORT).show();
            finish();
        }
        try {
            this.pictureFile = new File(this.phototUri.getPath());
            this.imageView.setImageBitmap(BitmapFactory.decodeFile(this.pictureFile.getAbsolutePath(), new BitmapFactory.Options()));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.imageView.setImageURI(this.phototUri);
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (this.appPreference.getBoolean("removeWatermark", false)) {
            this.removeWaterMark.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);

    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnMoreApp) {
            moreApp("https://play.google.com/store/apps/details?id=" + getPackageName());
        } else if (id == R.id.btn_back) {
            finish();
        } else if (id != R.id.btn_remowatermark) {
            switch (id) {
                case R.id.btnShareFacebook:
                    shareToFacebook(this.pictureFile.getPath());
                    return;
                case R.id.btnShareGooglePlus:
                    sendToGooglePlus(this.pictureFile.getPath());
                    return;
                case R.id.btnShareHike:
                    shareToHike(this.pictureFile.getPath());
                    return;
                case R.id.btnShareIntagram:
                    shareToInstagram(this.pictureFile.getPath());
                    return;
                case R.id.btnShareMore:
                    toShare();
                    return;
                case R.id.btnShareMoreImage:
                    shareImage(this.pictureFile.getPath());
                    return;
                case R.id.btnShareTwitter:
                    shareToTwitter(this.pictureFile.getPath());
                    return;
                case R.id.btnShareWhatsapp:
                    sendToWhatsaApp(this.pictureFile.getPath());
                    return;
                case R.id.btnSharewMessanger:
                    shareToMessanger(this.pictureFile.getPath());
                    return;
                default:
            }
        } else {
            showInAppDailog();
        }
    }

    public void moreApp(String str) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/search?q=pub:" + getResources().getString(R.string.app_name))));
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


    public void newLibRateDialog() {
        new AppRatingDialog.Builder().setPositiveButtonText("Submit").setNegativeButtonText("Cancel").setNeutralButtonText("Later").setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!")).setDefaultRating(2).setTitle("Rate this application").setDescription("Please select some stars and give your feedback").setCommentInputEnabled(false).setStarColor(R.color.yellow).setNoteDescriptionTextColor(R.color.text_color).setTitleTextColor(R.color.text_color).setDescriptionTextColor(R.color.text_color).setHint("Please write your comment here ...").setHintTextColor(R.color.hintTextColor).setWindowAnimation(R.style.MyDialogFadeAnimation).setCancelable(false).setCanceledOnTouchOutside(false).create(this).show();
    }

    private void showInAppDailog() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setContentView(R.layout.in_app);
        TextView textView = dialog.findViewById(R.id.txtDescription);
        Button button = dialog.findViewById(R.id.btnBuyRate);
        Button button2 = dialog.findViewById(R.id.btnNo);
        ((TextView) dialog.findViewById(R.id.txtTitle)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.txtDes)).setTypeface(setBoldFont());
        textView.setTypeface(setNormalFont());
        textView.setText("Purchase premium for just " + this.appPreference.getString("currencycode") + this.appPreference.getString("price"));
        button.setTypeface(setBoldFont());
        button2.setTypeface(setBoldFont());
        button.setOnClickListener(view -> dialog.dismiss());
        button2.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(this.myBroadcast_update);
    }

    public void sendToWhatsaApp(String str) {
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.whatsapp") != null) {
            try {
                Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str));
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                intent.setPackage("com.whatsapp");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendToGooglePlus(String str) {
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.google.android.apps.plus") != null) {
            try {
                ShareCompat.IntentBuilder type = ShareCompat.IntentBuilder.from(this).setType("image/jpeg");
                int REQUEST_FOR_GOOGLE_PLUS = 0;
                startActivityForResult(type.setStream(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str))).getIntent().setPackage("com.google.android.apps.plus"), REQUEST_FOR_GOOGLE_PLUS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Google Plus not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareToHike(String str) {
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.bsb.hike") != null) {
            try {
                Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str));
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                intent.setPackage("com.bsb.hike");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Hike not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToTwitter(String str) {
        try {
            if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.twitter.android") != null) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str)));
                    intent.setType("image/*");
                    for (ResolveInfo next : getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)) {
                        if (next.activityInfo.name.contains("twitter")) {
                            intent.setClassName(next.activityInfo.packageName, next.activityInfo.name);
                            startActivity(intent);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Twitter not installed", Toast.LENGTH_SHORT).show();
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "You don't seem to have twitter installed on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("WrongConstant")
    public void shareToFacebook(String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setPackage("com.facebook.katana");
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.facebook.katana") != null) {
            try {
                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str)));
                intent.setType("image/*");
                intent.addFlags(1);
                startActivity(Intent.createChooser(intent, "Share Gif."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Facebook not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("WrongConstant")
    public void shareToMessanger(String str) {
        if (getPackageManager().getLaunchIntentForPackage("com.facebook.orca") != null) {
            try {
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{str}, null, (str1, uri) -> {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/gif");
                    intent.setPackage("com.facebook.orca");
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    intent.addFlags(524288);
                    ShareImageActivity.this.startActivity(Intent.createChooser(intent, "Test"));
                });
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "You don't seem to have twitter installed on this device", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Facebook Messanger not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareToInstagram(String str) {
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.instagram.android") != null) {
            try {
                Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str));
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/*");
                new File(str);
                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                intent.setPackage("com.instagram.android");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Instagram not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("WrongConstant")
    public void shareImage(String str) {
        try {
            Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str));
            Intent intent = new Intent("android.intent.action.SEND");
            intent.addFlags(524288);
            intent.setType("image/*");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            startActivity(Intent.createChooser(intent, "Share Image Using"));
        } catch (Exception e) {
            Log.e(TAG, "shareImage: " + e);
        }
    }


    public void saveBitmap() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.plzwait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> {
            try {
                ShareImageActivity.this.pictureFile = new File(ShareImageActivity.this.phototUri.getPath());
                try {
                    if (!ShareImageActivity.this.pictureFile.exists()) {
                        ShareImageActivity.this.pictureFile.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(ShareImageActivity.this.pictureFile);
                    ThumbnailActivity.withoutWatermark.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    MediaScannerConnection.scanFile(ShareImageActivity.this, new String[]{ShareImageActivity.this.pictureFile.getAbsolutePath()}, null, (str, uri) -> {
                        Log.i("ExternalStorage", "Scanned " + str + ":");
                        String sb = "-> uri=" +
                                uri;
                        Log.i("ExternalStorage", sb);
                    });
                    ShareImageActivity shareImageActivity = ShareImageActivity.this;
                    ShareImageActivity shareImageActivity2 = ShareImageActivity.this;
                    shareImageActivity.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", FileProvider.getUriForFile(shareImageActivity2, ShareImageActivity.this.getApplicationContext().getPackageName() + ".provider", ShareImageActivity.this.pictureFile)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }).start();
        progressDialog.setOnDismissListener(dialogInterface -> {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            ShareImageActivity.this.imageView.setImageBitmap(BitmapFactory.decodeFile(ShareImageActivity.this.pictureFile.getAbsolutePath(), options));
        });
    }

    private void rateApp() {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(MARKET_CONSTANT + getApplicationContext().getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(GOOGLE_PLAY_CONSTANT + getApplicationContext().getPackageName())));
        }
        this.appPreference.putInt(Constants.isRated, 1);
    }

    public void onPositiveButtonClicked(int i, @NonNull String str) {
        if (i > 3) {
            rateApp();
        }
    }
}
