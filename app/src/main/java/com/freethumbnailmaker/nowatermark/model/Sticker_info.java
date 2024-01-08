package com.freethumbnailmaker.nowatermark.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Sticker_info implements Parcelable {
    public static final Parcelable.Creator<Sticker_info> CREATOR = new Parcelable.Creator<Sticker_info>() {
        public Sticker_info createFromParcel(Parcel parcel) {
            return new Sticker_info(parcel);
        }

        public Sticker_info[] newArray(int i) {
            return new Sticker_info[i];
        }
    };
    String st_field1;
    String st_field2 = "";
    String st_field3;
    String st_field4;
    private String st_height;
    String st_hue;
    private String st_image;
    private String st_order;
    private String st_res_id;
    private String st_res_uri;
    private String st_rotation;
    String st_scale_prog;
    private String st_width;
    private String st_x_pos;
    String st_x_rotateprog;
    private String st_y_pos;
    String st_y_rotateprog;
    String st_y_rotation;
    String st_z_rotateprog;
    private String sticker_id;

    public int describeContents() {
        return 0;
    }

    protected Sticker_info(Parcel parcel) {
        this.st_order = parcel.readString();
        this.st_image = parcel.readString();
        this.st_rotation = parcel.readString();
        this.st_height = parcel.readString();
        this.st_y_pos = parcel.readString();
        this.st_x_pos = parcel.readString();
        this.st_res_uri = parcel.readString();
        this.st_width = parcel.readString();
        this.sticker_id = parcel.readString();
        this.st_res_id = parcel.readString();
        this.st_y_rotation = parcel.readString();
        this.st_x_rotateprog = parcel.readString();
        this.st_y_rotateprog = parcel.readString();
        this.st_z_rotateprog = parcel.readString();
        this.st_scale_prog = parcel.readString();
        this.st_hue = parcel.readString();
        this.st_field1 = parcel.readString();
        this.st_field2 = parcel.readString();
        this.st_field3 = parcel.readString();
        this.st_field4 = parcel.readString();
    }

    public String getSt_y_rotation() {
        return this.st_y_rotation;
    }

    public void setSt_y_rotation(String str) {
        this.st_y_rotation = str;
    }

    public String getSt_x_rotateprog() {
        return this.st_x_rotateprog;
    }

    public void setSt_x_rotateprog(String str) {
        this.st_x_rotateprog = str;
    }

    public String getSt_y_rotateprog() {
        return this.st_y_rotateprog;
    }

    public void setSt_y_rotateprog(String str) {
        this.st_y_rotateprog = str;
    }

    public String getSt_z_rotateprog() {
        return this.st_z_rotateprog;
    }

    public void setSt_z_rotateprog(String str) {
        this.st_z_rotateprog = str;
    }

    public String getSt_scale_prog() {
        return this.st_scale_prog;
    }

    public void setSt_scale_prog(String str) {
        this.st_scale_prog = str;
    }

    public String getSt_hue() {
        return this.st_hue;
    }

    public void setSt_hue(String str) {
        this.st_hue = str;
    }

    public String getSt_field1() {
        return this.st_field1;
    }

    public void setSt_field1(String str) {
        this.st_field1 = str;
    }

    public String getSt_field2() {
        return this.st_field2;
    }

    public void setSt_field2(String str) {
        this.st_field2 = str;
    }

    public String getSt_field3() {
        return this.st_field3;
    }

    public void setSt_field3(String str) {
        this.st_field3 = str;
    }

    public String getSt_field4() {
        return this.st_field4;
    }

    public void setSt_field4(String str) {
        this.st_field4 = str;
    }

    public String getSt_order() {
        return this.st_order;
    }

    public void setSt_order(String str) {
        this.st_order = str;
    }

    public String getSt_image() {
        return this.st_image;
    }

    public void setSt_image(String str) {
        this.st_image = str;
    }

    public String getSt_rotation() {
        return this.st_rotation;
    }

    public void setSt_rotation(String str) {
        this.st_rotation = str;
    }

    public String getSt_height() {
        return this.st_height;
    }

    public void setSt_height(String str) {
        this.st_height = str;
    }

    public String getSt_y_pos() {
        return this.st_y_pos;
    }

    public void setSt_y_pos(String str) {
        this.st_y_pos = str;
    }

    public String getSt_x_pos() {
        return this.st_x_pos;
    }

    public void setSt_x_pos(String str) {
        this.st_x_pos = str;
    }

    public String getSt_res_uri() {
        return this.st_res_uri;
    }

    public void setSt_res_uri(String str) {
        this.st_res_uri = str;
    }

    public String getSt_width() {
        return this.st_width;
    }

    public void setSt_width(String str) {
        this.st_width = str;
    }

    public String getSticker_id() {
        return this.sticker_id;
    }

    public void setSticker_id(String str) {
        this.sticker_id = str;
    }

    public String getSt_res_id() {
        return this.st_res_id;
    }

    public void setSt_res_id(String str) {
        this.st_res_id = str;
    }

    public String toString() {
        return "ClassPojo [st_order = " + this.st_order + ", st_image = " + this.st_image + ", st_rotation = " + this.st_rotation + ", st_height = " + this.st_height + ", st_y_pos = " + this.st_y_pos + ", st_x_pos = " + this.st_x_pos + ", st_res_uri = " + this.st_res_uri + ", st_width = " + this.st_width + ", sticker_id = " + this.sticker_id + ", st_res_id = " + this.st_res_id + "]";
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.st_order);
        parcel.writeString(this.st_image);
        parcel.writeString(this.st_rotation);
        parcel.writeString(this.st_height);
        parcel.writeString(this.st_y_pos);
        parcel.writeString(this.st_x_pos);
        parcel.writeString(this.st_res_uri);
        parcel.writeString(this.st_width);
        parcel.writeString(this.sticker_id);
        parcel.writeString(this.st_res_id);
        parcel.writeString(this.st_y_rotation);
        parcel.writeString(this.st_x_rotateprog);
        parcel.writeString(this.st_y_rotateprog);
        parcel.writeString(this.st_z_rotateprog);
        parcel.writeString(this.st_scale_prog);
        parcel.writeString(this.st_hue);
        parcel.writeString(this.st_field1);
        parcel.writeString(this.st_field2);
        parcel.writeString(this.st_field3);
        parcel.writeString(this.st_field4);
    }
}
