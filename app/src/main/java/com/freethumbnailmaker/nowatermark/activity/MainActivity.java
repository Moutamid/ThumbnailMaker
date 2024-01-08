package com.freethumbnailmaker.nowatermark.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.freethumbnailmaker.nowatermark.R;

import com.freethumbnailmaker.nowatermark.ads.AdmobAds;
import com.freethumbnailmaker.nowatermark.main.BackgrounImageActivity;
import com.freethumbnailmaker.nowatermark.main.Constants;
import com.freethumbnailmaker.nowatermark.main.MyDesignActivity;
import com.freethumbnailmaker.nowatermark.main.MyThumbnailActivity;
import com.freethumbnailmaker.nowatermark.main.SelectionCategories;
import com.freethumbnailmaker.nowatermark.model.ServerData;
import com.freethumbnailmaker.nowatermark.network.ConnectivityReceiver;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static ArrayList<ServerData> allStickerArrayList;
    public static float ratio;
    public static int width;
    ImageView amin_drawable;
    public AppPreference appPreference;
    boolean isAppInstalled = false;

    public boolean lay_photos = false;
    public boolean lay_poster = false;
    public boolean lay_templates = false;
    LinearLayout linearLayout;

    public SharedPreferences prefs;

    public void createShortCut() {
    }


    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView(R.layout.main_activity);
        AdmobAds.loadBanner(this);
        AdmobAds.loadNativeAds(this, null);


        this.appPreference = new AppPreference(this);
        this.prefs = this.appPreference.getPrefernce();
        LinearLayout tutorial = findViewById(R.id.tutorial);
        this.linearLayout = findViewById(R.id.linearLayout);
        tutorial.setOnClickListener(view -> MainActivity.this.info());
        this.amin_drawable = findViewById(R.id.images);
        if (Build.VERSION.SDK_INT < 23 || (checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED)) {
            new BaseActivity.copyFontBG().execute("");
            makeStickerDir();
        }
        if (ConnectivityReceiver.isConnected()) {
            getSticker();
        } else {
            String string = this.appPreference.getString(Constants.jsonData);
            if (string != null && !string.equals("")) {
                allStickerArrayList = new ArrayList<>();
                allStickerArrayList.add(new Gson().fromJson(string, ServerData.class));
            }
        }
        setMyFontNormal(findViewById(16908290));
        findViews();

        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.isAppInstalled = appPreferences.getBoolean("isAppInstalled", false);
        if (ConnectivityReceiver.isConnected()) {
            getSticker();
            requestStoragePermission();
        } else {
            networkError();
        }
        if (!this.isAppInstalled) {
            createShortCut();
            SharedPreferences.Editor edit = appPreferences.edit();
            edit.putBoolean("isAppInstalled", true);
            edit.apply();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void requestStoragePermission() {

        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    MainActivity.this.makeStickerDir();
                    String string = MainActivity.this.appPreference.getString(Constants.jsonData);
                    if (string != null && !string.equals("")) {
                        MainActivity.allStickerArrayList = new ArrayList<>();
                        MainActivity.allStickerArrayList.add(new Gson().fromJson(string, ServerData.class));
                    } else if (ConnectivityReceiver.isConnected()) {
                        MainActivity.this.getSticker();
                    }
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    MainActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(MainActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            MainActivity.this.openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }

    private void findViews() {

        RelativeLayout layPoster = findViewById(R.id.lay_poster);
        RelativeLayout layTemplate = findViewById(R.id.lay_template);
        RelativeLayout layPhotos = findViewById(R.id.lay_photos);
        LinearLayout btnLayoutMore = findViewById(R.id.btnLayoutMore);
        TextView txtMoreapp = findViewById(R.id.txtMoreapp);
        LinearLayout btnLayoutRate = findViewById(R.id.btnLayoutRate);
        TextView txtRateApp = findViewById(R.id.txtRateApp);
        LinearLayout btnLayoutShare = findViewById(R.id.btnLayoutShare);
        TextView txtShareApp = findViewById(R.id.txtShareApp);
        layPoster.setOnClickListener(this);
        layTemplate.setOnClickListener(this);
        layPhotos.setOnClickListener(this);
        btnLayoutMore.setOnClickListener(this);
        btnLayoutRate.setOnClickListener(this);
        btnLayoutShare.setOnClickListener(this);
        txtMoreapp.setTypeface(setBoldFont());
        txtRateApp.setTypeface(setBoldFont());
        txtShareApp.setTypeface(setBoldFont());
    }

    public void onClick(View view) {
        requestStoragePermissionOnclick(view.getId());
    }

    private void requestStoragePermissionOnclick(final int i) {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    MainActivity.this.makeStickerDir();
                    if (ConnectivityReceiver.isConnected()) {
                        MainActivity.this.getSticker();
                        switch (i) {
                            case R.id.btnLayoutMore:
                                MainActivity.this.lay_poster = false;
                                MainActivity.this.lay_templates = false;
                                MainActivity.this.lay_photos = true;
                                startActivity(new Intent(MainActivity.this, MyDesignActivity.class));
                                return;

                            case R.id.btnLayoutRate:
                                MainActivity.this.toGooglePlay();
                                return;
                            case R.id.btnLayoutShare:
                                MainActivity.this.toShare();
                                return;
                            case R.id.lay_photos:

                                startActivity(new Intent(MainActivity.this, MyThumbnailActivity.class));
                                return;

                            case R.id.lay_poster:
                                MainActivity.this.lay_poster = true;
                                MainActivity.this.lay_templates = false;
                                MainActivity.this.lay_photos = false;

                                startActivity(new Intent(MainActivity.this, BackgrounImageActivity.class));
                                AdmobAds.showFullAds(null);
                                return;

                            case R.id.lay_template:
                                MainActivity.this.lay_poster = false;
                                MainActivity.this.lay_templates = true;
                                MainActivity.this.lay_photos = false;
                                if (ConnectivityReceiver.isConnected()) {
                                    startActivity(new Intent(MainActivity.this, SelectionCategories.class));
                                    AdmobAds.showFullAds(null);
                                    return;
                                } else {
                                    MainActivity.this.networkError();
                                    return;
                                }
                            default:
                        }
                    } else {
                        MainActivity.this.networkError();
                    }
                } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    MainActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(MainActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    public void info() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setContentView(R.layout.info);
        dialog.setCancelable(false);
        TextView textView = dialog.findViewById(R.id.batter);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(Html.fromHtml("for more info you can visit search : \"Add thumbnail to video\""));
        Button button = dialog.findViewById(R.id.ok);
        setMyFontBold(dialog.findViewById(R.id.main));
        ((TextView) dialog.findViewById(R.id.txtTitle)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.permission_des)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.camera_prtext)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.storage_prtext)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.permission_des1)).setTypeface(setBoldFont());
        textView.setTypeface(setBoldFont());
        button.setTypeface(setBoldFont());
        button.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    public void networkError() {
        new SweetAlertDialog(this, 3).setTitleText("No Internet connected?").setContentText("you can't access online templates without internet go through offline mode...").setCancelText("NO").setConfirmText("Go Offline Createion").setConfirmClickListener(sweetAlertDialog -> {
            sweetAlertDialog.dismiss();
            MainActivity mainActivity = MainActivity.this;
            mainActivity.startActivity(new Intent(mainActivity, BackgrounImageActivity.class));
        }).setCancelClickListener(SweetAlertDialog::dismissWithAnimation).show();
    }


}
