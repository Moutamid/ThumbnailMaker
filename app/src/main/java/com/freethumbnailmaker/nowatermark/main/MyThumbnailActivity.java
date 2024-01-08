package com.freethumbnailmaker.nowatermark.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.activity.BaseActivity;
import com.freethumbnailmaker.nowatermark.adapter.MyThumbnailAdapter;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.utility.ImageUtils;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyThumbnailActivity extends BaseActivity {
    private static final String TAG = "MyThumbnailActivity";
    public static File[] listFile;
    public Context context;
    public int count = 0;
    public MyThumbnailAdapter imageAdapter;
    public RecyclerView imagegrid;

    public RelativeLayout rel_text;
    public int screenWidth;
    public int spostion;


    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(1);
        setContentView(R.layout.activity_saved_history);
        setMyFontBold(findViewById(16908290));
        AppPreference appPreference = new AppPreference(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = displayMetrics.widthPixels - ImageUtils.dpToPx(this, 10.0f);
        TextView no_image = findViewById(R.id.no_image);
        this.rel_text = findViewById(R.id.rel_text);
        TextView txtTitle = findViewById(R.id.txtTitle);
        ImageView btn_back = findViewById(R.id.btn_back);
        txtTitle.setTypeface(setBoldFont());
        no_image.setTypeface(setBoldFont());
        btn_back.setOnClickListener(view -> MyThumbnailActivity.this.onBackPressed());
        this.imagegrid = findViewById(R.id.gridView);
        this.imagegrid.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.imagegrid.setHasFixedSize(true);
        requestStoragePermission();
    }

    private void requestStoragePermission() {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    MyThumbnailActivity.this.getImageAndView();
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    MyThumbnailActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(MyThumbnailActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            MyThumbnailActivity.this.openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }


    public void getImageAndView() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.plzwait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> {
            try {
                MyThumbnailActivity.this.getFromSdcard();
                MyThumbnailActivity.this.context = MyThumbnailActivity.this;
                if (MyThumbnailActivity.listFile != null) {
                    MyThumbnailActivity.this.imageAdapter = new MyThumbnailAdapter(MyThumbnailActivity.this.getApplicationContext(), MyThumbnailActivity.listFile, MyThumbnailActivity.this.screenWidth);
                    MyThumbnailActivity.this.imageAdapter.setItemClickCallback((OnClickCallback<ArrayList<String>, Integer, String, Context>) (arrayList, num, str, context) -> {
                        if (str.equals("0")) {
                            MyThumbnailActivity.this.showOptionsDialog(num);
                            return;
                        }
                        MyThumbnailActivity.this.spostion = num;
                        Intent intent = new Intent(MyThumbnailActivity.this, ShareImageActivity.class);
                        intent.putExtra("uri", MyThumbnailActivity.listFile[num].getAbsolutePath());
                        intent.putExtra("way", "Gallery");
                        MyThumbnailActivity.this.startActivity(intent);
                        return;
                    });
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                Log.e(MyThumbnailActivity.TAG, "run: " + e);
            }
            progressDialog.dismiss();
        }).start();
        progressDialog.setOnDismissListener(dialogInterface -> {
            MyThumbnailActivity.this.imagegrid.setAdapter(MyThumbnailActivity.this.imageAdapter);
            if (MyThumbnailActivity.this.count == 0) {
                MyThumbnailActivity.this.rel_text.setVisibility(View.VISIBLE);
            } else {
                MyThumbnailActivity.this.rel_text.setVisibility(View.GONE);
            }
        });
    }


    public void showOptionsDialog(final int i) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.setCancelable(false);
        Button button = dialog.findViewById(R.id.btnDelete);
        Button button2 = dialog.findViewById(R.id.btnCancel);
        ((TextView) dialog.findViewById(R.id.txtTitle)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.txtDescription)).setTypeface(setNormalFont());
        button.setTypeface(setBoldFont());
        button2.setTypeface(setBoldFont());
        button.setOnClickListener(view -> {
            if (MyThumbnailActivity.this.deleteFile(Uri.parse(MyThumbnailActivity.listFile[i].getAbsolutePath()))) {
                MyThumbnailActivity.listFile = null;
                MyThumbnailActivity.this.getImageAndView();
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }


    public boolean deleteFile(Uri uri) {
        boolean z = false;
        try {
            File file = new File(uri.getPath());
            z = file.delete();
            if (file.exists()) {
                try {
                    z = file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    z = getApplicationContext().deleteFile(file.getName());
                }
            }
            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file)));
        } catch (Exception e) {
            Log.e(TAG, "deleteFile: ");
        }
        return z;
    }

    public void getFromSdcard() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Thumbnail Design");
        if (file.isDirectory()) {
            listFile = file.listFiles();
            File[] fileArr = listFile;
            this.count = fileArr.length;
            Arrays.sort(fileArr, (file1, file2) -> {
                long lastModified;
                long lastModified2;
                lastModified = file2.lastModified();
                lastModified2 = file1.lastModified();
                return (Long.compare(lastModified, lastModified2));
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
