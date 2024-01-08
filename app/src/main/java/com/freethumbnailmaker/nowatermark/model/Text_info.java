package com.freethumbnailmaker.nowatermark.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Text_info implements Parcelable {
    public static final Parcelable.Creator<Text_info> CREATOR = new Parcelable.Creator<Text_info>() {
        public Text_info createFromParcel(Parcel parcel) {
            return new Text_info(parcel);
        }

        public Text_info[] newArray(int i) {
            return new Text_info[i];
        }
    };
    private String font_family;
    private String text;
    private String text_id;
    private String txt_color;
    private String txt_height;
    private String txt_order;
    private String txt_rotation;
    private String txt_width;
    private String txt_x_pos;
    private String txt_y_pos;

    public int describeContents() {
        return 0;
    }

    protected Text_info(Parcel parcel) {
        this.txt_height = parcel.readString();
        this.txt_width = parcel.readString();
        this.text = parcel.readString();
        this.txt_x_pos = parcel.readString();
        this.font_family = parcel.readString();
        this.txt_y_pos = parcel.readString();
        this.txt_order = parcel.readString();
        this.text_id = parcel.readString();
        this.txt_rotation = parcel.readString();
        this.txt_color = parcel.readString();
    }

    public String getTxt_height() {
        return this.txt_height;
    }

    public void setTxt_height(String str) {
        this.txt_height = str;
    }

    public String getTxt_width() {
        return this.txt_width;
    }

    public void setTxt_width(String str) {
        this.txt_width = str;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public String getTxt_x_pos() {
        return this.txt_x_pos;
    }

    public void setTxt_x_pos(String str) {
        this.txt_x_pos = str;
    }

    public String getFont_family() {
        return this.font_family;
    }

    public void setFont_family(String str) {
        this.font_family = str;
    }

    public String getTxt_y_pos() {
        return this.txt_y_pos;
    }

    public void setTxt_y_pos(String str) {
        this.txt_y_pos = str;
    }

    public String getTxt_order() {
        return this.txt_order;
    }

    public void setTxt_order(String str) {
        this.txt_order = str;
    }

    public String getText_id() {
        return this.text_id;
    }

    public void setText_id(String str) {
        this.text_id = str;
    }

    public String getTxt_rotation() {
        return this.txt_rotation;
    }

    public void setTxt_rotation(String str) {
        this.txt_rotation = str;
    }

    public String getTxt_color() {
        return this.txt_color;
    }

    public void setTxt_color(String str) {
        this.txt_color = str;
    }

    public String toString() {
        return "ClassPojo [txt_height = " + this.txt_height + ", txt_width = " + this.txt_width + ", text = " + this.text + ", txt_x_pos = " + this.txt_x_pos + ", font_family = " + this.font_family + ", txt_y_pos = " + this.txt_y_pos + ", txt_order = " + this.txt_order + ", text_id = " + this.text_id + ", txt_rotation = " + this.txt_rotation + ", txt_color = " + this.txt_color + "]";
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.txt_height);
        parcel.writeString(this.txt_width);
        parcel.writeString(this.text);
        parcel.writeString(this.txt_x_pos);
        parcel.writeString(this.font_family);
        parcel.writeString(this.txt_y_pos);
        parcel.writeString(this.txt_order);
        parcel.writeString(this.text_id);
        parcel.writeString(this.txt_rotation);
        parcel.writeString(this.txt_color);
    }
}
