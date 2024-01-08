package com.freethumbnailmaker.nowatermark.model;

public class ThumbnailData {
    private String cat_id;
    private String cat_name;
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

    public String toString() {
        return "ClassPojo [cat_id = " + this.cat_id + ", thumb_img = " + this.thumb_img + ", cat_name = " + this.cat_name + "]";
    }
}
