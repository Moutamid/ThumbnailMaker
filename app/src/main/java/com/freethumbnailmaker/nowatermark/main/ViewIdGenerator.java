package com.freethumbnailmaker.nowatermark.main;

import android.annotation.SuppressLint;

import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

public class ViewIdGenerator {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint({"NewApi"})
    public static int generateViewId() {
        int i;
        int i2;
        return View.generateViewId();
    }
}
