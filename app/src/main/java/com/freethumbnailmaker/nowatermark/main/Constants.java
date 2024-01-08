package com.freethumbnailmaker.nowatermark.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import com.michael.easydialog.EasyDialog;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.MyApplication;
import com.freethumbnailmaker.nowatermark.utility.ImageUtils;
import com.freethumbnailmaker.nowatermark.utils.ExifUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

public class Constants {
    public static String BASE_URL = "";
    public static String BASE_URL_BG = null;
    public static String BASE_URL_POSTER = null;
    public static String BASE_URL_STICKER = null;
    static int DesignerScreenHeight = 1519;
    static int DesignerScreenWidth = 1080;

    public static int[] Thumbnail_Selection = {R.drawable.f_cover, R.drawable.y_thumb, R.drawable.y_cover, R.drawable.fb_in_post, R.drawable.g_cover, R.drawable.t_cover, R.drawable.l_cover, R.drawable.w_cover};

    public static int aspectRatioHeight = 1;
    public static int aspectRatioWidth = 1;

    public static Bitmap bitmap = null;
    public static Bitmap bitmapSticker = null;
    public static int currentScreenHeight = 1;
    public static int currentScreenWidth = 1;
    public static String fURL = "";
    public static int[] imageId = {R.drawable.btxt0, R.drawable.btxt1, R.drawable.btxt2, R.drawable.btxt3, R.drawable.btxt4, R.drawable.btxt5, R.drawable.btxt6, R.drawable.btxt7, R.drawable.btxt8, R.drawable.btxt9, R.drawable.btxt10, R.drawable.btxt11, R.drawable.btxt12, R.drawable.btxt13, R.drawable.btxt14, R.drawable.btxt15, R.drawable.btxt16, R.drawable.btxt17, R.drawable.btxt18, R.drawable.btxt19, R.drawable.btxt20, R.drawable.btxt21, R.drawable.btxt22, R.drawable.btxt23, R.drawable.btxt24, R.drawable.btxt25, R.drawable.btxt26, R.drawable.btxt27, R.drawable.btxt28, R.drawable.btxt29, R.drawable.btxt30, R.drawable.btxt31, R.drawable.btxt32, R.drawable.btxt33, R.drawable.btxt34, R.drawable.btxt35, R.drawable.btxt36, R.drawable.btxt37, R.drawable.btxt38, R.drawable.btxt39};
    public static String isRated = "isRated";
    public static String jsonData = "jsonData";
    static int multiplier = 10000;
    public static String onTimeHint = "onTimeHint";
    public static String onTimeLayerScroll = "onTimeLayerScroll";
    public static String onTimeRecentHint = "onTimeRecentHint";

    public static int[] overlayArr = {R.drawable.os1, R.drawable.os2, R.drawable.os3, R.drawable.os4, R.drawable.os5, R.drawable.os6, R.drawable.os7, R.drawable.os8, R.drawable.os9, R.drawable.os10, R.drawable.os11, R.drawable.os12, R.drawable.os13, R.drawable.os14, R.drawable.os15, R.drawable.os16, R.drawable.os17, R.drawable.os18, R.drawable.os19, R.drawable.os20, R.drawable.os21, R.drawable.os22, R.drawable.os23, R.drawable.os24, R.drawable.os25, R.drawable.os26, R.drawable.os27, R.drawable.os28, R.drawable.os29, R.drawable.os30, R.drawable.os31, R.drawable.os32, R.drawable.os33, R.drawable.os34, R.drawable.os35, R.drawable.os36, R.drawable.os37, R.drawable.os38, R.drawable.os39, R.drawable.os40, R.drawable.os41, R.drawable.os42, R.drawable.os43, R.drawable.os44, R.drawable.os45};
    public static String rewid = "";
    public static String sdcardPath = null;
    public static String selectedRatio = "1:1";

    public static String uri = "";

    public static int getVersionInfo() {
        try {
            PackageInfo packageInfo = MyApplication.getInstance().getPackageManager().getPackageInfo(MyApplication.getInstance().getPackageName(), 0);

            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }




    public static CharSequence getSpannableString(Context context, Typeface typeface, int i) {
        SpannableStringBuilder append = new SpannableStringBuilder().append(new SpannableString(context.getResources().getString(i)));
        return append.subSequence(0, append.length());
    }

    public static Bitmap resizeBitmap(Bitmap bitmap2, int i, int i2) {
        float f;
        float f2 = (float) i;
        float f3 = (float) i2;
        float width = (float) bitmap2.getWidth();
        float height = (float) bitmap2.getHeight();
        Log.i("testings", f2 + "  " + f3 + "  and  " + width + "  " + height);
        float f4 = width / height;
        float f5 = height / width;
        if (width > f2) {
            f = f2 * f5;
            Log.i("testings", "if (wd > wr) " + f2 + "  " + f);
            if (f > f3) {
                f2 = f3 * f4;
                Log.i("testings", "  if (he > hr) " + f2 + "  " + f3);
                return Bitmap.createScaledBitmap(bitmap2, (int) f2, (int) f3, false);
            }
            Log.i("testings", " in else " + f2 + "  " + f);
        } else {
            if (height > f3) {
                float f6 = f3 * f4;
                Log.i("testings", "  if (he > hr) " + f6 + "  " + f3);
                if (f6 > f2) {
                    f3 = f2 * f5;
                } else {
                    Log.i("testings", " in else " + f6 + "  " + f3);
                    f2 = f6;
                }
            } else if (f4 > 0.75f) {
                f3 = f2 * f5;
                Log.i("testings", " if (rat1 > .75f) ");
            } else if (f5 > 1.5f) {
                f2 = f3 * f4;
                Log.i("testings", " if (rat2 > 1.5f) ");
            } else {
                f = f2 * f5;
                Log.i("testings", " in else ");
                if (f > f3) {
                    f2 = f3 * f4;
                    Log.i("testings", "  if (he > hr) " + f2 + "  " + f3);
                } else {
                    Log.i("testings", " in else " + f2 + "  " + f);
                }
            }
            return Bitmap.createScaledBitmap(bitmap2, (int) f2, (int) f3, false);
        }
        f3 = f;
        return Bitmap.createScaledBitmap(bitmap2, (int) f2, (int) f3, false);
    }



    public static Bitmap getBitmapFromUri(Context context, Uri uri2, float f, float f2) throws IOException {
        int exifRotation;
        try {
            ParcelFileDescriptor openFileDescriptor = context.getContentResolver().openFileDescriptor(uri2, "r");
            FileDescriptor fileDescriptor = openFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor,  null, options);
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            if (f <= f2) {
                f = f2;
            }
            int i = (int) f;
            options2.inSampleSize = ImageUtils.getClosestResampleSize(options.outWidth, options.outHeight, i);
            Bitmap decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileDescriptor,  null, options2);
            Matrix matrix = new Matrix();
            if (decodeFileDescriptor.getWidth() > i || decodeFileDescriptor.getHeight() > i) {
                BitmapFactory.Options resampling = ImageUtils.getResampling(decodeFileDescriptor.getWidth(), decodeFileDescriptor.getHeight(), i);
                matrix.postScale(((float) resampling.outWidth) / ((float) decodeFileDescriptor.getWidth()), ((float) resampling.outHeight) / ((float) decodeFileDescriptor.getHeight()));
            }
            String realPathFromURI = ImageUtils.getRealPathFromURI(uri2, context);
            if (Integer.parseInt(Build.VERSION.SDK) > 4 && (exifRotation = ExifUtils.getExifRotation(realPathFromURI)) != 0) {
                matrix.postRotate((float) exifRotation);
            }
            Bitmap createBitmap = Bitmap.createBitmap(decodeFileDescriptor, 0, 0, decodeFileDescriptor.getWidth(), decodeFileDescriptor.getHeight(), matrix, true);
            openFileDescriptor.close();
            return createBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }



    public static Typeface getHeaderTypeface(Activity activity) {
        return Typeface.createFromAsset(activity.getAssets(), "font/Montserrat-SemiBold.ttf");
    }

    public static Typeface getTextTypeface(Activity activity) {
        return Typeface.createFromAsset(activity.getAssets(), "font/Montserrat-Medium.ttf");
    }

    public static Animation getAnimUp(Activity activity) {
        return AnimationUtils.loadAnimation(activity, R.anim.slide_up);
    }

    public static Animation getAnimDown(Activity activity) {
        return AnimationUtils.loadAnimation(activity, R.anim.slide_down);
    }



    public static File getSaveFileLocation(String str) {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(externalStoragePublicDirectory, ".Thumbnail Maker Stickers/" + str);
    }

    public static boolean saveBitmapObject(Activity activity, Bitmap bitmap2, String str) {
        Bitmap copy = bitmap2.copy(bitmap2.getConfig(), true);
        File file = new File(str);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            boolean compress = copy.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            copy.recycle();
            activity.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
            return compress;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("testing", "Exception" + e.getMessage());
            return false;
        }
    }

    public static String saveBitmapObject1(Bitmap bitmap2) {
        File saveFileLocation = getSaveFileLocation("category1");
        saveFileLocation.mkdirs();
        File file = new File(saveFileLocation, "raw1-" + System.currentTimeMillis() + ".png");
        String absolutePath = file.getAbsolutePath();
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
            return absolutePath;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("testing", "Exception" + e.getMessage());
            return "";
        }
    }

    public static String saveBitmapObject(Activity activity, Bitmap bitmap2) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ".Thumbnail Maker Stickers/Mydesigns");
        file.mkdirs();
        File file2 = new File(file, "thumb-" + System.currentTimeMillis() + ".png");
        if (file2.exists()) {
            file2.delete();
        }
        try {
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file2));
            return file2.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("MAINACTIVITY", "Exception" + e.getMessage());
            Toast.makeText(activity, activity.getResources().getString(R.string.save_err), Toast.LENGTH_SHORT).show();
            return null;
        }
    }



    public static Bitmap guidelines_bitmap(Activity activity, int i, int i2) {
        Activity activity2 = activity;
        int i3 = i;
        int i4 = i2;
        try {
            Bitmap createBitmap = Bitmap.createBitmap(i3, i4, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            paint.setColor(-1);
            paint.setStrokeWidth((float) ImageUtils.dpToPx(activity2, 2.0f));
            paint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 1.0f));
            paint.setStyle(Paint.Style.STROKE);
            Paint paint2 = new Paint();
            paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
            paint2.setStrokeWidth((float) ImageUtils.dpToPx(activity2, 2.0f));
            paint2.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 1.0f));
            paint2.setStyle(Paint.Style.STROKE);
            float f = (float) i4;
            canvas.drawLine((float) (i3 / 4), 0.0f, (float) (i3 / 4), f, paint);
            canvas.drawLine((float) (i3 / 2), 0.0f, (float) (i3 / 2), f, paint);
            int i5 = i3 * 3;
            canvas.drawLine((float) (i5 / 4), 0.0f, (float) (i5 / 4), f, paint);
            float f2 = (float) i3;
            canvas.drawLine(0.0f, (float) (i4 / 4), f2, (float) (i4 / 4), paint);
            canvas.drawLine(0.0f, (float) (i4 / 2), f2, (float) (i4 / 2), paint);
            int i6 = i4 * 3;
            canvas.drawLine(0.0f, (float) (i6 / 4), f2, (float) (i6 / 4), paint);
            canvas.drawLine((float) ((i3 / 4) + 2), 0.0f, (float) ((i3 / 4) + 2), f, paint2);
            canvas.drawLine((float) ((i3 / 2) + 2), 0.0f, (float) ((i3 / 2) + 2), f, paint2);
            canvas.drawLine((float) ((i5 / 4) + 2), 0.0f, (float) ((i5 / 4) + 2), f, paint2);
            canvas.drawLine(0.0f, (float) ((i4 / 4) + 2), f2, (float) ((i4 / 4) + 2), paint2);
            canvas.drawLine(0.0f, (float) ((i4 / 2) + 2), f2, (float) ((i4 / 2) + 2), paint2);
            canvas.drawLine(0.0f, (float) ((i6 / 4) + 2), f2, (float) ((i6 / 4) + 2), paint2);
            return createBitmap;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getTiledBitmap(Activity activity, int i, Bitmap bitmap2, SeekBar seekBar) {
        Rect rect = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());
        Paint paint = new Paint();
        int progress = ThumbnailActivity.seek_tailys.getProgress() + 10;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        paint.setShader(new BitmapShader(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(), i, options), progress, progress, true), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        Bitmap createBitmap = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawRect(rect, paint);
        return createBitmap;
    }





    public static void showHindDialog(View view, Activity activity) {
        View inflate = activity.getLayoutInflater().inflate(R.layout.tooltip_hint_more_option, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.txthint)).setTypeface(getTextTypeface(activity));
        new EasyDialog(activity).setLayout(inflate).setBackgroundColor(activity.getResources().getColor(R.color.titlecolor)).setLocationByAttachedView(view).setGravity(0).setAnimationTranslationShow(0, 1000, -600.0f, 100.0f, -50.0f, 50.0f, 0.0f).setAnimationAlphaShow(1000, 0.3f, 1.0f).setAnimationTranslationDismiss(0, 500, -50.0f, 800.0f).setAnimationAlphaDismiss(500, 1.0f, 0.0f).setMatchParent(false).setMarginLeftAndRight(24, 24).setOutsideColor(activity.getResources().getColor(R.color.transparent)).show();
    }

    public static void showRecentHindDialog(View view, Activity activity) {
        View inflate = activity.getLayoutInflater().inflate(R.layout.tooltip_hint_layerview, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.txthint)).setTypeface(getTextTypeface(activity));
        new EasyDialog(activity).setLayout(inflate).setBackgroundColor(activity.getResources().getColor(R.color.titlecolor)).setLocationByAttachedView(view).setGravity(3).setAnimationTranslationShow(0, 1000, -600.0f, 100.0f, -50.0f, 50.0f, 0.0f).setAnimationAlphaShow(1000, 0.3f, 1.0f).setAnimationTranslationDismiss(0, 500, -50.0f, 800.0f).setAnimationAlphaDismiss(500, 1.0f, 0.0f).setMatchParent(false).setMarginLeftAndRight(24, 24).setOutsideColor(activity.getResources().getColor(R.color.transparent)).show();
    }

    public static void showScrollLayerDialog(View view, Activity activity) {
        View inflate = activity.getLayoutInflater().inflate(R.layout.tooltip_hint_layerscroll, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.txthint)).setTypeface(getTextTypeface(activity));
        new EasyDialog(activity).setLayout(inflate).setBackgroundColor(activity.getResources().getColor(R.color.titlecolor)).setLocationByAttachedView(view).setGravity(3).setAnimationTranslationShow(0, 1000, -600.0f, 100.0f, -50.0f, 50.0f, 0.0f).setAnimationAlphaShow(1000, 0.3f, 1.0f).setAnimationTranslationDismiss(0, 500, -50.0f, 800.0f).setAnimationAlphaDismiss(500, 1.0f, 0.0f).setMatchParent(false).setMarginLeftAndRight(24, 24).setOutsideColor(activity.getResources().getColor(R.color.transparent)).show();
    }
}
