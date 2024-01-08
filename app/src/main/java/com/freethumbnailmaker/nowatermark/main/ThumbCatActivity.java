package com.freethumbnailmaker.nowatermark.main;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.Scopes;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.qintong.library.InsLoadingView;
import com.freethumbnailmaker.nowatermark.MyApplication;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.activity.BaseActivity;
import com.freethumbnailmaker.nowatermark.activity.ImageDownloadManager;
import com.freethumbnailmaker.nowatermark.fragment.MorePoster;
import com.freethumbnailmaker.nowatermark.model.Snap1;
import com.freethumbnailmaker.nowatermark.model.Sticker_info;
import com.freethumbnailmaker.nowatermark.model.Text_info;
import com.freethumbnailmaker.nowatermark.model.ThumbnailCo;
import com.freethumbnailmaker.nowatermark.model.ThumbnailDataList;
import com.freethumbnailmaker.nowatermark.model.ThumbnailInfo;
import com.freethumbnailmaker.nowatermark.model.ThumbnailKey;
import com.freethumbnailmaker.nowatermark.model.ThumbnailThumbFull;
import com.freethumbnailmaker.nowatermark.model.ThumbnailWithList;
import com.freethumbnailmaker.nowatermark.newAdapter.ThumbnailSnapAdapter;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;
import com.freethumbnailmaker.nowatermark.utils.Config;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThumbCatActivity extends BaseActivity {
    public static final String ORIENTATION = "orientation";
    private static final String TAG = "ThumbCatActivity";
    public AppPreference appPreference;
    public String appkey = "";
    int catID = 0;
    public int currentPage = 1;
    public boolean isLastPage = false;
    public boolean isLoading = false;
    int itemCount = 0;
    private ImageView ivBack;
    public InsLoadingView loading_view;
    private boolean mHorizontal;
    LinearLayoutManager mLinearLayoutManager;
    ArrayList<ThumbnailDataList> posterDatas = new ArrayList<>();
    public ProgressDialog progressDialog;
    String ratio = "0";
    RecyclerView recyclerView;
    public String selectedPosition;
    ThumbnailSnapAdapter snapAdapter;
    ArrayList<Object> snapData = new ArrayList<>();
    public ArrayList<Sticker_info> stickerInfoArrayList;
    public ArrayList<Text_info> textInfoArrayList;
    TextView textView;
    public ArrayList<ThumbnailCo> thumbnailCos;
    int totalPage = 0;
    private Typeface typefaceBold;
    private Typeface typefaceNormal;
    ArrayList<String> urlss;

    static void getCurrentPage(ThumbCatActivity thumbCatActivity) {
        int i = thumbCatActivity.currentPage;
        thumbCatActivity.currentPage = i + 1;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_poster_cat);
        this.appPreference = new AppPreference(this);
        if (bundle == null) {
            this.mHorizontal = true;
        } else {
            this.mHorizontal = bundle.getBoolean(ORIENTATION);
        }
        this.textView = findViewById(R.id.txtTitle);
        this.loading_view = findViewById(R.id.loading_view);
        this.recyclerView = findViewById(R.id.recycler);
        this.mLinearLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.mLinearLayoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.ratio = getIntent() != null ? getIntent().getStringExtra("ratio") : "0";
        this.selectedPosition = getIntent().getStringExtra("sizeposition");
        getPosKeyAndCall1();
        this.typefaceBold = Typeface.createFromAsset(getAssets(), "font/Montserrat-SemiBold.ttf");
        this.typefaceNormal = Typeface.createFromAsset(getAssets(), "font/Montserrat-Medium.ttf");
        this.textView.setTypeface(setBoldFont());
        Config.SaveInt("flow", 2, this);
        this.ivBack = findViewById(R.id.iv_back);
        this.ivBack.setOnClickListener(view -> ThumbCatActivity.this.onBackPressed());
    }

    @Override
    public Typeface setBoldFont() {
        return this.typefaceBold;
    }

    @Override
    public Typeface setNormalFont() {
        return this.typefaceNormal;
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(ORIENTATION, this.mHorizontal);
    }

    public void freeMemory() {
        try {
            new Thread(() -> {
                try {
                    Glide.get(ThumbCatActivity.this).clearDiskCache();
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


    public void setupAdapter() {
        Collections.shuffle(this.posterDatas);
        for (int i = 0; i < this.posterDatas.size(); i++) {
            if (this.posterDatas.get(i).getPoster_list().size() != 0) {
                this.snapData.add(new Snap1(GravityCompat.START, this.posterDatas.get(i).getCat_name(), this.posterDatas.get(i).getPoster_list(), Integer.parseInt(this.posterDatas.get(i).getCat_id()), this.ratio));
            }
        }
        if (!this.appPreference.getBoolean("isAdsDisabled", false)) {
            insertAdsInMenuItems();
        } else {
            getData();
        }
    }

    private void getData() {
        if (this.snapData != null) {
            if (this.snapAdapter == null) {
                this.recyclerView.addOnScrollListener(new PaginationListener(this.mLinearLayoutManager) {

                    public void loadMoreItems() {
                        ThumbCatActivity.this.isLoading = true;
                        ThumbCatActivity.getCurrentPage(ThumbCatActivity.this);
                        ThumbCatActivity.this.getPagingData();
                    }

                    public boolean isLastPage() {
                        return ThumbCatActivity.this.isLastPage;
                    }

                    public boolean isLoading() {
                        return ThumbCatActivity.this.isLoading;
                    }
                });
            }
            this.snapAdapter = new ThumbnailSnapAdapter(this, new ArrayList());
            this.recyclerView.setAdapter(this.snapAdapter);
            getPagingData();
        }
    }


    public void getPagingData() {
        final ArrayList arrayList = new ArrayList();
        double size = this.snapData.size();
        Double.isNaN(size);
        this.totalPage = Round(size / 10.0d);
        new Handler().postDelayed(() -> {
            for (int i = 0; i < 10; i++) {
                if (ThumbCatActivity.this.itemCount < ThumbCatActivity.this.snapData.size()) {
                    arrayList.add(ThumbCatActivity.this.snapData.get(ThumbCatActivity.this.itemCount));
                    ThumbCatActivity.this.itemCount++;
                }
            }
            if (ThumbCatActivity.this.currentPage != 1) {
                ThumbCatActivity.this.snapAdapter.removeLoadingView();
            }
            ThumbCatActivity.this.snapAdapter.addData(arrayList);

            ThumbCatActivity.this.loading_view.setVisibility(View.GONE);
            if (ThumbCatActivity.this.currentPage < ThumbCatActivity.this.totalPage) {
                ThumbCatActivity.this.snapAdapter.addLoadingView();
            } else {
                ThumbCatActivity.this.isLastPage = true;
            }
            ThumbCatActivity.this.isLoading = false;
        }, 1500);
    }

    public int Round(double d) {
        return Math.abs(d - Math.floor(d)) > 0.1d ? ((int) d) + 1 : (int) d;
    }

    private void insertAdsInMenuItems() {
        int size = this.snapData.size() / 4;
        if (!this.appPreference.getBoolean("isAdsDisabled", false)) {
            int i = 2;
            for (int i2 = 0; i2 < size; i2++) {
                this.snapData.add(i, "Ads");
                i += 3;
            }
            getData();
        }
    }

    public void itemClickSeeMoreAdapter(ArrayList<ThumbnailThumbFull> arrayList, int i, String str, String str2) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        MorePoster morePoster = (MorePoster) supportFragmentManager.findFragmentByTag("template_category_frgm");
        if (morePoster != null) {
            beginTransaction.remove(morePoster);
        }
        beginTransaction.add(R.id.frameContainerPoster, MorePoster.newInstance(arrayList, i, str, str2), "template_category_frgm");
        beginTransaction.addToBackStack("template_category_frgm");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openPosterActivity(int postId, int catId) {
        requestStoragePermission(postId, catId);
    }

    private void requestStoragePermission(final int postId, final int catId) {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    ThumbCatActivity.this.makeStickerDir();
                    String string = ThumbCatActivity.this.appPreference.getString(Constants.jsonData);
                    if (string.equals("")) {
                        ThumbCatActivity.this.getSticker();
                    } else {
                        new BaseActivity.copyServerFontBG().execute();
                    }
                    ThumbCatActivity.this.setupProgress();
                    ThumbCatActivity.this.getPosKeyAndCall(catId, postId);
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    ThumbCatActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(ThumbCatActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            ThumbCatActivity.this.openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }

    public void getPosKeyAndCall(int catId, int postId) {

        loadPoster(this.appkey, catId, postId);
    }

    public void loadPoster(String appkey, int catId, int postId) {

        MyApplication.getInstance().addToRequestQueue(new StringRequest(1, Constants.BASE_URL_POSTER + "poster/poster", new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("qq_loadPoster", str);
                try {
                    ThumbCatActivity.this.thumbnailCos = (new Gson().fromJson(str, ThumbnailInfo.class)).getData();
                    ThumbCatActivity.this.textInfoArrayList = (ThumbCatActivity.this.thumbnailCos.get(0)).getText_info();
                    ThumbCatActivity.this.stickerInfoArrayList = (ThumbCatActivity.this.thumbnailCos.get(0)).getSticker_info();
                    ThumbnailCo thumbnailCo = ThumbCatActivity.this.thumbnailCos.get(0);
                    ThumbCatActivity.this.ratio = thumbnailCo.getRatio();
                    ThumbCatActivity.this.urlss = new ArrayList<>();
                    ThumbCatActivity.this.urlss.add(thumbnailCo.getBack_image());
                    for (int i = 0; i < ThumbCatActivity.this.stickerInfoArrayList.size(); i++) {
                        if (!(ThumbCatActivity.this.stickerInfoArrayList.get(0)).getSt_image().equals("")) {
                            ThumbCatActivity.this.urlss.add(Constants.BASE_URL_STICKER + (ThumbCatActivity.this.stickerInfoArrayList.get(i)).getSt_image());
                        }
                    }
                    new ContextWrapper(ThumbCatActivity.this).getDir("Images", 0);
                    String str2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/.Resorces/";
                    File file = new File(str2);
                    if (!file.exists()) {
                        file.mkdir();
                    } else {
                        ThumbCatActivity.this.deleteRecursive(file);
                        file.mkdir();
                    }

                    ImageDownloadManager.getInstance(ThumbCatActivity.this.getApplicationContext()).addTask(new ImageDownloadManager.ImageDownloadTask(this, ImageDownloadManager.Task.DOWNLOAD, ThumbCatActivity.this.urlss, str2, new ImageDownloadManager.Callback() {
                        public void onSuccess(ImageDownloadManager.ImageDownloadTask imageDownloadTask, ArrayList<String> arrayList) {
                            if (ThumbCatActivity.this.progressDialog != null && ThumbCatActivity.this.progressDialog.isShowing()) {
                                ThumbCatActivity.this.progressDialog.dismiss();
                            }
                            Log.d(ImageDownloadManager.class.getSimpleName(), "Image save success news ");
                            int i = 0;
                            while (i < arrayList.size()) {
                                try {
                                    if (i == 0) {
                                        (ThumbCatActivity.this.thumbnailCos.get(i)).setBack_image(arrayList.get(i));
                                    } else {
                                        (ThumbCatActivity.this.stickerInfoArrayList.get(i - 1)).setSt_image(arrayList.get(i));
                                    }
                                    i++;
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                            Intent intent = new Intent(ThumbCatActivity.this, ThumbnailActivity.class);
                            intent.putParcelableArrayListExtra("template", ThumbCatActivity.this.thumbnailCos);
                            intent.putParcelableArrayListExtra("text", ThumbCatActivity.this.textInfoArrayList);
                            intent.putParcelableArrayListExtra("sticker", ThumbCatActivity.this.stickerInfoArrayList);
                            intent.putExtra(Scopes.PROFILE, "Background");
                            intent.putExtra("cat_id", 1);
                            intent.putExtra("loadUserFrame", false);
                            intent.putExtra("ratio", (ThumbCatActivity.this.thumbnailCos.get(0)).getRatio());
                            intent.putExtra("sizeposition", ThumbCatActivity.this.selectedPosition);
                            intent.putExtra("Temp_Type", "MY_TEMP");
                            ThumbCatActivity.this.startActivity(intent);
                        }

                        public void onFailure(ImageDownloadManager.ImageSaveFailureReason imageSaveFailureReason) {
                            String simpleName = ImageDownloadManager.class.getSimpleName();
                            Log.d(simpleName, "Image save fail news " + imageSaveFailureReason);
                            if (ThumbCatActivity.this.progressDialog != null && ThumbCatActivity.this.progressDialog.isShowing()) {
                                ThumbCatActivity.this.progressDialog.dismiss();
                            }
                        }
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (ThumbCatActivity.this.progressDialog != null && ThumbCatActivity.this.progressDialog.isShowing()) {
                        ThumbCatActivity.this.progressDialog.dismiss();
                    }
                }
            }
        }, volleyError -> {
            if (ThumbCatActivity.this.progressDialog != null && ThumbCatActivity.this.progressDialog.isShowing()) {
                ThumbCatActivity.this.progressDialog.dismiss();
            }
            Log.e(ThumbCatActivity.TAG, "Error: " + volleyError.getMessage());
        }) {

            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("key", appkey);
                hashMap.put("device", "1");
                hashMap.put("cat_id", String.valueOf(catId));
                hashMap.put("post_id", String.valueOf(postId));
                return hashMap;
            }
        });
    }

    public void setupProgress() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setTitle("Downloading Templates");
        this.progressDialog.setMessage("Downloading is in progress, Please wait...");
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setProgressStyle(0);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    public void deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File deleteRecursive : file.listFiles()) {
                deleteRecursive(deleteRecursive);
            }
        }
        file.delete();
    }

    public void getPosKeyAndCall1() {
        MyApplication.getInstance().addToRequestQueue(new StringRequest(1, Constants.BASE_URL_POSTER + "apps_key", str -> {
            try {
                ThumbnailKey thumbnailKey = (ThumbnailKey) new Gson().fromJson(str, ThumbnailKey.class);
                ThumbCatActivity.this.appkey = thumbnailKey.getKey();
                Log.e(ThumbCatActivity.TAG, "key: " + thumbnailKey.getKey());
                ThumbCatActivity.this.getPosterList(thumbnailKey.getKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, volleyError -> Log.e(ThumbCatActivity.TAG, "Error: " + volleyError.getMessage())) {

            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("device", "1");
                return hashMap;
            }
        });
    }

    public void getPosterList(String str) {
        final String str2 = str;
        MyApplication.getInstance().addToRequestQueue(new StringRequest(1, Constants.BASE_URL_POSTER + "poster/swiperCat", str1 -> {
            try {
                ThumbnailWithList thumbnailWithList = new Gson().fromJson(str1, ThumbnailWithList.class);
                for (int i = 0; i < thumbnailWithList.getData().size(); i++) {
                    ThumbCatActivity.this.posterDatas.add(thumbnailWithList.getData().get(i));
                }
                ThumbCatActivity.this.setupAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, volleyError -> Log.e(ThumbCatActivity.TAG, "Error: " + volleyError.getMessage())) {

            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("key", str2);
                hashMap.put("device", "1");
                hashMap.put("cat_id", String.valueOf(ThumbCatActivity.this.catID));
                hashMap.put("ratio", ThumbCatActivity.this.ratio);
                return hashMap;
            }
        });
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    @Override
    public void toGooglePlay() {
        String packageName = getPackageName();
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
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
    public void onDestroy() {
        super.onDestroy();
        freeMemory();
    }
}
