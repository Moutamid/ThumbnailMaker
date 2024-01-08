package com.freethumbnailmaker.nowatermark.model;

import java.util.ArrayList;

public class ThumbnailWithList {
    private ArrayList<ThumbnailDataList> data;
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

    public ArrayList<ThumbnailDataList> getData() {
        return this.data;
    }

    public void setData(ArrayList<ThumbnailDataList> arrayList) {
        this.data = arrayList;
    }

    public String toString() {
        return "ClassPojo [message = " + this.message + ", error = " + this.error + ", data = " + this.data + "]";
    }
}
