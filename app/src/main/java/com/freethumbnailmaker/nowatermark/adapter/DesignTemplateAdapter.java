package com.freethumbnailmaker.nowatermark.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;

import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.bumptech.glide.request.RequestOptions;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.create.BitmapDataObject;
import com.freethumbnailmaker.nowatermark.create.DatabaseHandler;
import com.freethumbnailmaker.nowatermark.create.TemplateInfo;
import com.freethumbnailmaker.nowatermark.utility.GlideApp;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class DesignTemplateAdapter extends ArrayAdapter<TemplateInfo> {
    private static final String TAG = "DesignTemplateAdapter";
    String catName;
    Context context;
    int height;

    public DesignTemplateAdapter(Context context2, List<TemplateInfo> list, String str, int i) {
        super(context2, 0, list);
        this.context = context2;
        this.catName = str;
        this.height = i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.grid_itemthumb, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        TemplateInfo templateInfo = getItem(i);
        if (this.catName.equals("MY_TEMP")) {
            viewHolder.imgDeletePoster.setVisibility(View.VISIBLE);
            try {
                if (templateInfo.getTHUMB_URI().contains("thumb")) {
                    GlideApp.with(this.context).load(new File(templateInfo.getTHUMB_URI()).getAbsoluteFile()).fitCenter().apply(((new RequestOptions().dontAnimate()).placeholder(R.drawable.no_image)).error(R.drawable.no_image)).into(viewHolder.mThumbnail);
                } else if (templateInfo.getTHUMB_URI().contains("raw")) {
                    GlideApp.with(this.context).load(getBitmapDataObject(Uri.parse(templateInfo.getTHUMB_URI()).getPath()).imageByteArray).fitCenter().apply(((new RequestOptions().dontAnimate()).placeholder(R.drawable.no_image)).error(R.drawable.no_image)).into(viewHolder.mThumbnail);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                viewHolder.mThumbnail.setImageBitmap(BitmapFactory.decodeResource(this.context.getResources(), R.drawable.no_image));
            }
        } else {
            GlideApp.with(this.context).load(this.context.getResources().getIdentifier(templateInfo.getTHUMB_URI(), "drawable", this.context.getPackageName())).fitCenter().apply(((new RequestOptions().dontAnimate()).placeholder(R.drawable.no_image)).error(R.drawable.no_image)).into(viewHolder.mThumbnail);
        }
        viewHolder.imgDeletePoster.setOnClickListener(view1 -> DesignTemplateAdapter.this.showOptionsDialog(i));
        return view;
    }


    public void showOptionsDialog(final int i) {
        final Dialog dialog = new Dialog(this.context, R.style.ThemeWithCorners);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.setCancelable(false);

        (dialog.findViewById(R.id.btnDelete)).setOnClickListener(view -> {
            TemplateInfo templateInfo = DesignTemplateAdapter.this.getItem(i);
            DatabaseHandler dbHandler = DatabaseHandler.getDbHandler(DesignTemplateAdapter.this.context);
            boolean deleteTemplateInfo = dbHandler.deleteTemplateInfo(templateInfo.getTEMPLATE_ID());
            dbHandler.close();
            if (deleteTemplateInfo) {
                DesignTemplateAdapter.this.deleteFile(Uri.parse(templateInfo.getTHUMB_URI()));
                DesignTemplateAdapter.this.remove(templateInfo);
                DesignTemplateAdapter.this.notifyDataSetChanged();
                dialog.dismiss();
                return;
            }
            Toast.makeText(DesignTemplateAdapter.this.context, DesignTemplateAdapter.this.context.getResources().getString(R.string.del_error_toast), Toast.LENGTH_SHORT).show();
        });
        (dialog.findViewById(R.id.btnCancel)).setOnClickListener(view -> dialog.dismiss());
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
                    z = this.context.getApplicationContext().deleteFile(file.getName());
                }
            }
            Context context2 = this.context;
            Context context3 = getContext();
            context2.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", FileProvider.getUriForFile(context3, getContext().getApplicationContext().getPackageName() + ".provider", file)));
        } catch (Exception e2) {
            Log.e(TAG, "deleteFile: " + e2);
        }
        return z;
    }

    private BitmapDataObject getBitmapDataObject(String str) {
        try {
            return (BitmapDataObject) new ObjectInputStream(new FileInputStream(new File(str))).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class ViewHolder {
        ImageView imgDeletePoster;
        ImageView mThumbnail;

        public ViewHolder(View view) {
            this.mThumbnail =  view.findViewById(R.id.image);
            this.imgDeletePoster =  view.findViewById(R.id.imgDeletePoster);
        }
    }
}
