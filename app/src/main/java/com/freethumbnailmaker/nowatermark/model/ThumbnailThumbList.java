package com.freethumbnailmaker.nowatermark.model;

public class ThumbnailThumbList {
    private String back_image;
    private String cat_id;
    private String post_id;
    private String post_thumb;
    private String ratio;

    public String getCat_id() {
        return this.cat_id;
    }

    public void setCat_id(String str) {
        this.cat_id = str;
    }

    public String getRatio() {
        return this.ratio;
    }

    public void setRatio(String str) {
        this.ratio = str;
    }

    public String getBack_image() {
        return this.back_image;
    }

    public void setBack_image(String str) {
        this.back_image = str;
    }

    public String getPost_id() {
        return this.post_id;
    }

    public void setPost_id(String str) {
        this.post_id = str;
    }

    public String getPost_thumb() {
        return this.post_thumb;
    }

    public void setPost_thumb(String str) {
        this.post_thumb = str;
    }

    public String toString() {
        return "ClassPojo [cat_id = " + this.cat_id + ", ratio = " + this.ratio + ", back_image = " + this.back_image + ", post_id = " + this.post_id + ", post_thumb = " + this.post_thumb + "]";
    }
}
