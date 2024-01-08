package com.freethumbnailmaker.nowatermark.model;

import java.util.ArrayList;

public class Snap1 {
    int cat_id;
    private int mGravity;
    private String mText;
    private String ratio;
    private ArrayList<ThumbnailThumbFull> thumbnailThumbFulls;

    public String getRatio() {
        return this.ratio;
    }

    public void setRatio(String str) {
        this.ratio = str;
    }

    public Snap1(int mGravity, String mText, ArrayList<ThumbnailThumbFull> arrayList, int cat_id, String ratio) {
        this.mGravity = mGravity;
        this.mText = mText;
        this.thumbnailThumbFulls = arrayList;
        this.cat_id = cat_id;
        this.ratio = ratio;
    }

    public String getText() {
        return this.mText;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public ArrayList<ThumbnailThumbFull> getPosterThumbFulls() {
        return this.thumbnailThumbFulls;
    }

    public int getCat_id() {
        return this.cat_id;
    }
}
