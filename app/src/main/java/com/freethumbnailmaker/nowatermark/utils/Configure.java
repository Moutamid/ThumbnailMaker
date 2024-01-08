package com.freethumbnailmaker.nowatermark.utils;

import android.content.Context;
import android.os.Environment;
import com.freethumbnailmaker.nowatermark.R;
import java.io.File;

public class Configure {
    public static File GetFileDir(Context context) {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return context.getExternalFilesDir((String) null);
        }
        return context.getFilesDir();
    }

    public static File GetCacheDir(Context context) {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return context.getExternalCacheDir();
        }
        return context.getCacheDir();
    }

    public static File GetSaveDir(Context context) {
        File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(externalStoragePublicDirectory.getPath() + File.separator + context.getString(R.string.app_name));
        return (file.exists() || file.mkdirs()) ? file : externalStoragePublicDirectory;
    }

    public static File GetProjectDir(Context context) {
        File GetFileDir = GetFileDir(context);
        File file = new File(GetFileDir.getPath() + File.separator + "project");
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetProjectBitmapDir(Context context) {
        File GetFileDir = GetFileDir(context);
        File file = new File(GetFileDir.getPath() + File.separator + "project");
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetFontDir(Context context) {
        File GetFileDir = GetFileDir(context);
        File file = new File(GetFileDir.getPath() + File.separator + "font");
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetTextDir(Context context) {
        File GetFileDir = GetFileDir(context);
        File file = new File(GetFileDir.getPath() + File.pathSeparator + "text");
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }

    public static File GetUnsplashJsonDir(Context context) {
        File GetFileDir = GetFileDir(context);
        File file = new File(GetFileDir.getPath() + File.separator + "unsplash");
        return (file.exists() || file.mkdirs()) ? file : GetFileDir;
    }


}
