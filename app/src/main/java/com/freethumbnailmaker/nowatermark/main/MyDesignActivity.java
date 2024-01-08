package com.freethumbnailmaker.nowatermark.main;


import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.freethumbnailmaker.nowatermark.activity.BaseActivity;
import com.freethumbnailmaker.nowatermark.activity.MainActivity;
import com.freethumbnailmaker.nowatermark.adapter.DesignTemplateAdapter;
import com.freethumbnailmaker.nowatermark.create.DatabaseHandler;
import com.freethumbnailmaker.nowatermark.create.TemplateInfo;
import com.freethumbnailmaker.nowatermark.model.ServerData;
import com.freethumbnailmaker.nowatermark.utility.ImageUtils;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;
import com.freethumbnailmaker.nowatermark.utils.Config;
import java.util.ArrayList;
import java.util.List;

public class MyDesignActivity extends BaseActivity {
    public AppPreference appPreference;
    String catName = "MY_TEMP";
    public DesignTemplateAdapter designTemplateAdapter;
    public GridView gridView;
    int heightItemGrid = 50;
    LordDataOperationAsync loadDataAsync = null;
    ProgressBar progress_bar;
    public int spoisiton;
    public ArrayList<TemplateInfo> templateList = new ArrayList<>();
    TextView txt_dialog;
    int widthItemGrid = 50;

    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(1);
        setContentView( R.layout.activity_my_design);
        Config.SaveInt("flow", 1, this);
        this.appPreference = new AppPreference(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.widthItemGrid = ((int) ((float) (displayMetrics.widthPixels - ImageUtils.dpToPx(this, 10.0f)))) / 2;
        this.heightItemGrid = ((int) ((float) (displayMetrics.heightPixels - ImageUtils.dpToPx(this, 10.0f)))) / 2;
        TextView txtTitle = findViewById(R.id.txtTitle);
        txtTitle.setTypeface(setBoldFont());
        ImageView imagBack =  findViewById(R.id.btn_back);
        imagBack.setOnClickListener(view -> MyDesignActivity.this.onBackPressed());
        this.gridView =  findViewById(R.id.gridview);
        this.progress_bar =  findViewById(R.id.progress_bar);
        this.progress_bar.setVisibility(View.GONE);
        this.txt_dialog = findViewById(R.id.txt_dialog);

        requestStoragePermission();
        this.gridView.setOnItemClickListener((adapterView, view, i, j) -> {
           MyDesignActivity.this.spoisiton = i;
            Intent intent = new Intent(MyDesignActivity.this, ThumbnailActivity.class);
            intent.putExtra("position", MyDesignActivity.this.spoisiton);
            intent.putExtra("loadUserFrame", false);
            intent.putExtra("Temp_Type", MyDesignActivity.this.catName);
            MyDesignActivity.this.startActivity(intent);
        });
    }

    private void requestStoragePermission() {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    MyDesignActivity.this.makeStickerDir();
                    String string = MyDesignActivity.this.appPreference.getString(Constants.jsonData);
                    if (string != null && !string.equals("")) {
                        MainActivity.allStickerArrayList = new ArrayList<>();
                        MainActivity.allStickerArrayList.add(new Gson().fromJson(string, ServerData.class));
                    }
                    new BaseActivity.copyServerFontBG().execute();
                    new BaseActivity.copyFontBG().execute("");
                    MyDesignActivity myDesignActivity = MyDesignActivity.this;
                    myDesignActivity.loadDataAsync = new LordDataOperationAsync();
                    MyDesignActivity.this.loadDataAsync.execute("");
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    MyDesignActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(MyDesignActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle( "Need Permissions");
        builder.setMessage( "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton( "GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            MyDesignActivity.this.openSettings();
        });
        builder.setNegativeButton( "Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(),  null));
        startActivityForResult(intent, 101);
    }

    public class LordDataOperationAsync extends AsyncTask<String, Void, String> {
        public LordDataOperationAsync() {
        }

        @Override
        public void onPreExecute() {
            MyDesignActivity.this.progress_bar.setVisibility(View.VISIBLE);
        }


        public String doInBackground(String... strArr) {
            try {
                MyDesignActivity.this.templateList.clear();
                DatabaseHandler dbHandler = DatabaseHandler.getDbHandler(MyDesignActivity.this);
                if (MyDesignActivity.this.catName.equals("MY_TEMP")) {
                  MyDesignActivity.this.templateList = dbHandler.getTemplateListDes("USER");
                }
                dbHandler.close();
                return "yes";
            } catch (NullPointerException e) {
                return "yes";
            }
        }

        @Override
        public void onPostExecute(String str) {
            try {
                MyDesignActivity.this.progress_bar.setVisibility(View.GONE);
                if (MyDesignActivity.this.templateList.size() != 0) {
                   MyDesignActivity.this.designTemplateAdapter = new DesignTemplateAdapter(MyDesignActivity.this, MyDesignActivity.this.templateList, MyDesignActivity.this.catName, MyDesignActivity.this.widthItemGrid);
                    MyDesignActivity.this.gridView.setAdapter(MyDesignActivity.this.designTemplateAdapter);
                }
                if (MyDesignActivity.this.catName.equals("MY_TEMP")) {
                    if (MyDesignActivity.this.templateList.size() == 0) {
                        MyDesignActivity.this.txt_dialog.setText(MyDesignActivity.this.getResources().getString(R.string.msg_NoDesigns));
                    } else if (MyDesignActivity.this.templateList.size() <= 4) {
                        MyDesignActivity.this.txt_dialog.setText(MyDesignActivity.this.getResources().getString(R.string.ins_DesignOptionsInstruction));
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
