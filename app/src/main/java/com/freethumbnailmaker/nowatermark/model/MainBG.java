package com.freethumbnailmaker.nowatermark.model;

import java.util.ArrayList;

public class MainBG {
    int category_id;
    ArrayList<BackgroundImage> category_list;
    String category_name;

    public MainBG(int i, String str, ArrayList<BackgroundImage> arrayList) {
        this.category_id = i;
        this.category_name = str;
        this.category_list = arrayList;
    }

    public int getCategory_id() {
        return this.category_id;
    }

    public void setCategory_id(int i) {
        this.category_id = i;
    }

    public String getCategory_name() {
        return this.category_name;
    }

    public void setCategory_name(String str) {
        this.category_name = str;
    }

    public ArrayList<BackgroundImage> getCategory_list() {
        return this.category_list;
    }

    public void setCategory_list(ArrayList<BackgroundImage> arrayList) {
        this.category_list = arrayList;
    }
}
