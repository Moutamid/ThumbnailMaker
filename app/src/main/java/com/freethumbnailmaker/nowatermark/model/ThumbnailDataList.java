package com.freethumbnailmaker.nowatermark.model;

import java.util.ArrayList;

public class ThumbnailDataList {
    private String cat_id;
    private String cat_name;
    ArrayList<ThumbnailThumbFull> poster_list;
    private String thumb_img;

    public String getCat_id() {
        return this.cat_id;
    }

    public void setCat_id(String str) {
        this.cat_id = str;
    }

    public String getThumb_img() {
        return this.thumb_img;
    }

    public void setThumb_img(String str) {
        this.thumb_img = str;
    }

    public String getCat_name() {
        return this.cat_name;
    }

    public void setCat_name(String str) {
        this.cat_name = str;
    }

    public ArrayList<ThumbnailThumbFull> getPoster_list() {
        return this.poster_list;
    }

    public void setPoster_list(ArrayList<ThumbnailThumbFull> arrayList) {
        this.poster_list = arrayList;
    }

    public String toString() {
        return "ClassPojo [cat_id = " + this.cat_id + ", thumb_img = " + this.thumb_img + ", cat_name = " + this.cat_name + "]";
    }
}
