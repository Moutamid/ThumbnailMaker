package com.freethumbnailmaker.nowatermark.model;

import java.util.ArrayList;

public class ThumbnailThumb {
    private ArrayList<ThumbnailThumbList> data;
    private String error;
    private String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String str) {
        this.error = str;
    }

    public ArrayList<ThumbnailThumbList> getData() {
        return this.data;
    }

    public void setData(ArrayList<ThumbnailThumbList> arrayList) {
        this.data = arrayList;
    }

    public String toString() {
        return "ClassPojo [message = " + this.message + ", error = " + this.error + ", data = " + this.data + "]";
    }
}
