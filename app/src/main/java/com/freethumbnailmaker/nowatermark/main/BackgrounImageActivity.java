package com.freethumbnailmaker.nowatermark.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import com.google.android.gms.common.Scopes;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.MyApplication;
import com.freethumbnailmaker.nowatermark.activity.BaseActivity;
import com.freethumbnailmaker.nowatermark.adapter.VeticalViewAdapter;
import com.freethumbnailmaker.nowatermark.fragment.BackgroundFragment1;
import com.freethumbnailmaker.nowatermark.interfaces.GetSelectSize;
import com.freethumbnailmaker.nowatermark.interfaces.GetSnapListener;
import com.freethumbnailmaker.nowatermark.interfaces.SizeSelection;
import com.freethumbnailmaker.nowatermark.listener.RecyclerViewLoadMoreScroll;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import com.freethumbnailmaker.nowatermark.model.MainBG;
import com.freethumbnailmaker.nowatermark.model.Snap2;
import com.freethumbnailmaker.nowatermark.model.ThumbBG;
import com.freethumbnailmaker.nowatermark.utility.YourDataProvider;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;
import com.qintong.library.InsLoadingView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class BackgrounImageActivity extends BaseActivity implements View.OnClickListener, GetSnapListener, GetSelectSize {
    private static final int SELECT_PICTURE_FROM_CAMERA = 9062;
    private static final int SELECT_PICTURE_FROM_GALLERY = 9072;
    private static final String TAG = "BackgrounImageActivity";
    private Animation animSlideDown;
    public Animation animSlideUp;
    public AppPreference appPreference;
    private final int bColor = Color.parseColor("#4149b6");
    public ProgressDialog dialogIs;
    public File file;
    RelativeLayout fragmen_container;
    private boolean isSizeSelected = false;
    public InsLoadingView loading_view;
    private final List<WeakReference<Fragment>> mFragments = new ArrayList();

    LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    UCrop.Options options;
    public String ratio = "1:1";
    public Uri resultUri;
    float screenHeight;
    float screenWidth;

    public RecyclerViewLoadMoreScroll scrollListener;
    ArrayList<Object> snapData = new ArrayList<>();
    private TextView textview_rat;
    ArrayList<MainBG> thumbnail_bg = new ArrayList<>();

    public VeticalViewAdapter veticalViewAdapter;

    YourDataProvider yourDataProvider;

    @Override
    public void onCreate(Bundle bundle) {
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView(R.layout.activity_select_image);
        this.options = new UCrop.Options();
        this.options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        this.options.setToolbarColor(getResources().getColor(R.color.color_bg));
        this.options.setActiveWidgetColor(getResources().getColor(R.color.color_add_btn));
        this.loading_view = findViewById(R.id.loading_view);

        findViews();
        this.mRecyclerView = findViewById(R.id.background_recyclerview);
        this.mLinearLayoutManager = new LinearLayoutManager(this);
        this.mRecyclerView.setLayoutManager(this.mLinearLayoutManager);
        this.mRecyclerView.setHasFixedSize(true);
        this.appPreference = new AppPreference(this);

        this.textview_rat = findViewById(R.id.textview_rat);
        this.textview_rat.setOnClickListener(this);
        this.textview_rat.setTypeface(setBoldFont());
        TextView textView = this.textview_rat;
        textView.setText(getResources().getString(R.string.ratio) + ": (1:1)");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = (float) displayMetrics.widthPixels;
        this.screenHeight = (float) displayMetrics.heightPixels;
        this.animSlideUp = Constants.getAnimUp(this);
        this.animSlideDown = Constants.getAnimDown(this);
        this.fragmen_container = findViewById(R.id.fragmen_container);
        this.fragmen_container.setOnTouchListener((view, motionEvent) -> true);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        SizeSelection sizeSelection = (SizeSelection) supportFragmentManager.findFragmentByTag("size_frgm");
        if (sizeSelection != null) {
            beginTransaction.remove(sizeSelection);
        }
        SizeSelection sizeSelection2 = new SizeSelection();
        this.mFragments.add(new WeakReference(sizeSelection2));
        beginTransaction.add(R.id.fragmen_container, sizeSelection2, "size_frgm");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.fragmen_container.post(() -> {
            BackgrounImageActivity.this.fragmen_container.startAnimation(BackgrounImageActivity.this.animSlideUp);
            BackgrounImageActivity.this.fragmen_container.setVisibility(View.VISIBLE);
        });
        getBgImages();
    }

    private void getBgImages() {
        MyApplication.getInstance().addToRequestQueue(new StringRequest(1, Constants.BASE_URL_POSTER + "poster/background", str -> {
            try {
                BackgrounImageActivity.this.loading_view.setVisibility(View.GONE);
                BackgrounImageActivity.this.thumbnail_bg = new Gson().fromJson(str, ThumbBG.class).getThumbnail_bg();
                BackgrounImageActivity.this.setupAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, volleyError -> Log.e(BackgrounImageActivity.TAG, "Error: " + volleyError.getMessage())) {
            @Override
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        });
    }


    public void setupAdapter() {
        for (int i = 0; i < this.thumbnail_bg.size(); i++) {
            if (this.thumbnail_bg.get(i).getCategory_list().size() != 0) {
                this.snapData.add(new Snap2(1, this.thumbnail_bg.get(i).getCategory_name(), this.thumbnail_bg.get(i).getCategory_list(), this.thumbnail_bg.get(i).getCategory_id(), this.ratio));
            }
        }
        this.loading_view.setVisibility(View.GONE);
        this.yourDataProvider = new YourDataProvider();
        this.yourDataProvider.setPosterList(this.snapData);
        if (this.snapData != null) {
            this.veticalViewAdapter = new VeticalViewAdapter(this, this.yourDataProvider.getLoadMorePosterItems(), 1);
            this.mRecyclerView.setAdapter(this.veticalViewAdapter);
            this.scrollListener = new RecyclerViewLoadMoreScroll(this.mLinearLayoutManager);
            this.scrollListener.setOnLoadMoreListener(BackgrounImageActivity.this::LoadMoreData);
            this.mRecyclerView.addOnScrollListener(this.scrollListener);
        }
    }


    public void LoadMoreData() {
        this.veticalViewAdapter.addLoadingView();
        new Handler().postDelayed(() -> {
            BackgrounImageActivity.this.veticalViewAdapter.removeLoadingView();
            BackgrounImageActivity.this.veticalViewAdapter.addData(BackgrounImageActivity.this.yourDataProvider.getLoadMorePosterItemsS());
            BackgrounImageActivity.this.veticalViewAdapter.notifyDataSetChanged();
            BackgrounImageActivity.this.scrollListener.setLoaded();
        }, 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        freeMemory();
    }

    public void freeMemory() {
        try {
            new Thread(() -> {
                try {
                    Glide.get(BackgrounImageActivity.this).clearDiskCache();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            Glide.get(this).clearMemory();
        } catch (OutOfMemoryError | Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == SELECT_PICTURE_FROM_GALLERY) {
            try {
                Uri fromFile = Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                String[] split = Constants.selectedRatio.split(":");
                UCrop.of(intent.getData(), fromFile).withOptions(this.options).withAspectRatio((float) Integer.parseInt(split[0]), (float) Integer.parseInt(split[1])).start(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (i2 == -1 && i == SELECT_PICTURE_FROM_CAMERA) {
            try {
                Uri fromFile2 = Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                String[] split2 = Constants.selectedRatio.split(":");
                int parseInt = Integer.parseInt(split2[0]);
                int parseInt2 = Integer.parseInt(split2[1]);
                UCrop.of(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", this.file), fromFile2).withOptions(this.options).withAspectRatio((float) parseInt, (float) parseInt2).start(this);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (i2 == -1 && i == 69) {
            handleCropResult(intent);
        } else if (i2 == 96) {
            UCrop.getError(intent);
        }
    }

    private void handleCropResult(@NonNull Intent intent) {
        this.resultUri = UCrop.getOutput(intent);
        try {
            showInterstialAd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int gcd(int i, int i2) {
        return i2 == 0 ? i : gcd(i2, i % i2);
    }



    public void showInterstialAd() {

        Intent intent = new Intent(this, ThumbnailActivity.class);
        intent.putExtra("ratio", this.ratio);
        intent.putExtra("loadUserFrame", true);
        intent.putExtra(Scopes.PROFILE, this.resultUri.toString());
        intent.putExtra("position", "0");
        intent.putExtra("hex", "");
        startActivity(intent);
        return;
    }




    private void findViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView txtTitle = findViewById(R.id.txtTitle);
        ImageView btnColorPicker = findViewById(R.id.btnColorPicker);
        ImageView btnGalleryPicker = findViewById(R.id.btnGalleryPicker);
        ImageView btnTakePicture = findViewById(R.id.btnTakePicture);
        txtTitle.setText("Background");
        btnColorPicker.setVisibility(View.VISIBLE);
        txtTitle.setTypeface(setBoldFont());
        btnBack.setOnClickListener(this);
        btnColorPicker.setOnClickListener(this);
        btnGalleryPicker.setOnClickListener(this);
        btnTakePicture.setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {

        if (this.fragmen_container.getVisibility() != View.VISIBLE) {
            super.onBackPressed();
        } else if (this.isSizeSelected) {
            this.fragmen_container.startAnimation(this.animSlideDown);
            this.fragmen_container.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnColorPicker:
                colorPickerDialog();
                return;
            case R.id.btnGalleryPicker:
                requestStorageGalleryPermission();
                return;
            case R.id.btnTakePicture:
                requestStoragePermission();
                return;
            case R.id.btn_back:
                onBackPressed();
                return;
            case R.id.textview_rat:
                this.fragmen_container.startAnimation(this.animSlideUp);
                this.fragmen_container.setVisibility(View.VISIBLE);
                return;
            default:
        }
    }

    private void requestStoragePermission() {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    BackgrounImageActivity.this.makeStickerDir();
                    String string = BackgrounImageActivity.this.appPreference.getString(Constants.jsonData);
                    if (string != null && !string.equals("")) {
                        new BaseActivity.copyServerFontBG().execute();
                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    BackgrounImageActivity.this.file = new File(Environment.getExternalStorageDirectory(), ".temp.jpg");
                    BackgrounImageActivity backgrounImageActivity = BackgrounImageActivity.this;
                    intent.putExtra("output", FileProvider.getUriForFile(backgrounImageActivity, BackgrounImageActivity.this.getApplicationContext().getPackageName() + ".provider", BackgrounImageActivity.this.file));
                    BackgrounImageActivity.this.startActivityForResult(intent, BackgrounImageActivity.SELECT_PICTURE_FROM_CAMERA);
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    BackgrounImageActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(BackgrounImageActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    private void requestStorageGalleryPermission() {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    BackgrounImageActivity.this.makeStickerDir();
                    String string = BackgrounImageActivity.this.appPreference.getString(Constants.jsonData);
                    if (string != null && !string.equals("")) {
                        new BaseActivity.copyServerFontBG().execute();
                    }
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction("android.intent.action.GET_CONTENT");
                    BackgrounImageActivity backgrounImageActivity = BackgrounImageActivity.this;
                    backgrounImageActivity.startActivityForResult(Intent.createChooser(intent, backgrounImageActivity.getString(R.string.select_picture)), BackgrounImageActivity.SELECT_PICTURE_FROM_GALLERY);
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    BackgrounImageActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(BackgrounImageActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            BackgrounImageActivity.this.openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }

    private void colorPickerDialog() {
        new AmbilWarnaDialog(this, this.bColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
            }

            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                BackgrounImageActivity.this.updateColor(i);
            }
        }).show();
    }


    public void updateColor(int i) {
        FileOutputStream fileOutputStream;
        Bitmap createBitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
        createBitmap.eraseColor(i);
        Log.e(TAG, "updateColor: ");
        try {
            File file = new File(new File(this.appPreference.getString(Constants.sdcardPath) + "/bg/"), "tempcolor.png");
            Uri uri ;
            fileOutputStream = new FileOutputStream(file);
            createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            uri = Uri.fromFile(file);
            if (uri != null) {
                Uri fromFile = Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                String[] split = Constants.selectedRatio.split(":");
                UCrop.of(uri, fromFile).withOptions(this.options).withAspectRatio((float) Integer.parseInt(split[0]), (float) Integer.parseInt(split[1])).start(this);
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    public void onSnapFilter(int i, int i2, String str) {
        requestStorageSnapPermission(i, i2, str);
    }

    private void requestStorageSnapPermission(int i, final int i2, final String str) {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {

            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                FileOutputStream fileOutputStream;
                Uri fromFile = null;
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    BackgrounImageActivity.this.makeStickerDir();
                    String string = BackgrounImageActivity.this.appPreference.getString(Constants.jsonData);
                    if (string != null && !string.equals("")) {
                        new copyServerFontBG().execute();
                    }
                    try {
                        Uri uri = null;
                        if (str.equals("null")) {
                            Bitmap decodeResource = BitmapFactory.decodeResource(BackgrounImageActivity.this.getResources(), 1);
                            File file = new File(BackgrounImageActivity.this.appPreference.getString(Constants.sdcardPath) + "/bgs" + i2 + "/");
                            File file2 = new File(file, "dummy");
                            if (file2.exists()) {
                                fromFile = Uri.fromFile(file2);
                            } else {
                                try {
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    fileOutputStream = new FileOutputStream(file2);
                                    decodeResource.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                    fileOutputStream.close();
                                    fromFile = Uri.fromFile(file2);
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            uri = fromFile;
                        } else {
                            BackgrounImageActivity.this.ongetPosition(str);
                        }
                        if (uri != null) {
                            Uri fromFile2 = Uri.fromFile(new File(BackgrounImageActivity.this.getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                            String[] split = Constants.selectedRatio.split(":");
                            UCrop.of(uri, fromFile2).withOptions(BackgrounImageActivity.this.options).withAspectRatio((float) Integer.parseInt(split[0]), (float) Integer.parseInt(split[1])).start(BackgrounImageActivity.this);
                        }
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    BackgrounImageActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(BackgrounImageActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    public void ratioOptions(String str) {
        this.ratio = str;
        Constants.selectedRatio = str;
        TextView textView = this.textview_rat;
        textView.setText(getResources().getString(R.string.ratio) + ": (" + str + ")");
        this.fragmen_container.startAnimation(this.animSlideDown);
        this.fragmen_container.setVisibility(View.GONE);
        this.isSizeSelected = true;
    }

    public void sizeOptions(String str) {
        String[] split = str.split(":");
        int parseInt = Integer.parseInt(split[0]);
        int parseInt2 = Integer.parseInt(split[1]);
        this.ratio = str;
        int gcd = gcd(parseInt, parseInt2);
        Constants.selectedRatio = "" + (parseInt / gcd) + ":" + (parseInt2 / gcd) + ":" + parseInt + ":" + parseInt2;
        TextView textView = this.textview_rat;
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.size));
        sb.append(":");
        sb.append(parseInt);
        sb.append(" x ");
        sb.append(parseInt2);
        textView.setText(sb.toString());
        this.fragmen_container.startAnimation(this.animSlideDown);
        this.fragmen_container.setVisibility(View.GONE);
        this.isSizeSelected = true;
    }

    public void itemClickSeeMoreAdapter(ArrayList<BackgroundImage> arrayList) {
        seeMore(arrayList);
    }

    private void seeMore(ArrayList<BackgroundImage> arrayList) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        BackgroundFragment1 backgroundFragment1 = (BackgroundFragment1) supportFragmentManager.findFragmentByTag("back_category_frgm");
        if (backgroundFragment1 != null) {
            beginTransaction.remove(backgroundFragment1);
        }
        BackgroundFragment1 newInstance = BackgroundFragment1.newInstance(arrayList);
        this.mFragments.add(new WeakReference(newInstance));
        beginTransaction.setCustomAnimations(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
        beginTransaction.add(R.id.frameContainerBackground, newInstance, "back_category_frgm");
        beginTransaction.addToBackStack("back_category_frgm");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void ongetPosition(String str) {
        this.dialogIs = new ProgressDialog(this);
        this.dialogIs.setMessage(getResources().getString(R.string.plzwait));
        this.dialogIs.setCancelable(false);
        this.dialogIs.show();
        final File cacheFolder = getCacheFolder(this);
        MyApplication.getInstance().addToRequestQueue(new ImageRequest(str, bitmap -> {
            try {
                BackgrounImageActivity.this.dialogIs.dismiss();
                try {
                    File file = new File(cacheFolder, "localFileName.png");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    try {
                        Uri fromFile = Uri.fromFile(file);
                        Uri fromFile2 = Uri.fromFile(new File(BackgrounImageActivity.this.getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                        String[] split = Constants.selectedRatio.split(":");
                        UCrop.of(fromFile, fromFile2).withOptions(BackgrounImageActivity.this.options).withAspectRatio((float) Integer.parseInt(split[0]), (float) Integer.parseInt(split[1])).start(BackgrounImageActivity.this);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }, 0, 0, null, volleyError -> BackgrounImageActivity.this.dialogIs.dismiss()));
    }

    private void requestStorageSnapsPermission(final String str) {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    BackgrounImageActivity.this.makeStickerDir();
                    String string = BackgrounImageActivity.this.appPreference.getString(Constants.jsonData);
                    if (string != null && !string.equals("")) {
                        new BaseActivity.copyServerFontBG().execute();
                    }
                    BackgrounImageActivity backgrounImageActivity = BackgrounImageActivity.this;
                     backgrounImageActivity.dialogIs = new ProgressDialog(backgrounImageActivity);
                    BackgrounImageActivity.this.dialogIs.setMessage(BackgrounImageActivity.this.getResources().getString(R.string.plzwait));
                    BackgrounImageActivity.this.dialogIs.setCancelable(false);
                    BackgrounImageActivity.this.dialogIs.show();
                    BackgrounImageActivity backgrounImageActivity2 = BackgrounImageActivity.this;
                    final File cacheFolder = backgrounImageActivity2.getCacheFolder(backgrounImageActivity2);
                    MyApplication.getInstance().addToRequestQueue(new ImageRequest(str, bitmap -> {
                        try {
                            BackgrounImageActivity.this.dialogIs.dismiss();
                            try {
                                File file = new File(cacheFolder, "localFileName.png");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                try {
                                    Uri fromFile = Uri.fromFile(file);
                                    Uri fromFile2 = Uri.fromFile(new File(BackgrounImageActivity.this.getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                                    String[] split = Constants.selectedRatio.split(":");
                                    UCrop.of(fromFile, fromFile2)
                                            .withOptions(BackgrounImageActivity.this.options)
                                            .withAspectRatio((float) Integer.parseInt(split[0]), (float) Integer.parseInt(split[1]))
                                            .start(BackgrounImageActivity.this);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        } catch (Exception e4) {
                            e4.printStackTrace();
                        }
                    }, 0, 0, null, volleyError -> BackgrounImageActivity.this.dialogIs.dismiss()));
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    BackgrounImageActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(BackgrounImageActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    public void ongetPositions(String str) {
        requestStorageSnapsPermission(str);
    }

    public File getCacheFolder(Context context) {
        File file;
        if (Environment.getExternalStorageState().equals("mounted")) {
            file = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        } else {
            file = null;
        }
        return !file.isDirectory() ? context.getCacheDir() : file;
    }
}
