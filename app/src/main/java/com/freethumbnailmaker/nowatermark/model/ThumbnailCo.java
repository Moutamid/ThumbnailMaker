package com.freethumbnailmaker.nowatermark.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class ThumbnailCo implements Parcelable {
    public static final Parcelable.Creator<ThumbnailCo> CREATOR = new Parcelable.Creator<ThumbnailCo>() {
        public ThumbnailCo createFromParcel(Parcel parcel) {
            return new ThumbnailCo(parcel);
        }

        public ThumbnailCo[] newArray(int i) {
            return new ThumbnailCo[i];
        }
    };
    private String back_image;
    private String cat_id;
    private String post_id;
    private String post_thumb;
    private String ratio;
    private ArrayList<Sticker_info> sticker_info;
    private ArrayList<Text_info> text_info;

    public int describeContents() {
        return 0;
    }

    protected ThumbnailCo(Parcel parcel) {
        this.cat_id = parcel.readString();
        this.ratio = parcel.readString();
        this.back_image = parcel.readString();
        this.post_id = parcel.readString();
        this.post_thumb = parcel.readString();
    }

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

    public ArrayList<Text_info> getText_info() {
        return this.text_info;
    }

    public void setText_info(ArrayList<Text_info> arrayList) {
        this.text_info = arrayList;
    }

    public ArrayList<Sticker_info> getSticker_info() {
        return this.sticker_info;
    }

    public void setSticker_info(ArrayList<Sticker_info> arrayList) {
        this.sticker_info = arrayList;
    }

    public String toString() {
        return "ClassPojo [cat_id = " + this.cat_id + ", ratio = " + this.ratio + ", back_image = " + this.back_image + ", post_id = " + this.post_id + ", post_thumb = " + this.post_thumb + ", text_info = " + this.text_info + ", sticker_info = " + this.sticker_info + "]";
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.cat_id);
        parcel.writeString(this.ratio);
        parcel.writeString(this.back_image);
        parcel.writeString(this.post_id);
        parcel.writeString(this.post_thumb);
    }
}
