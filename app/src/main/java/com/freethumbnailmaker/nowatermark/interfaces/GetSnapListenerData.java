package com.freethumbnailmaker.nowatermark.interfaces;

import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import java.util.ArrayList;

public interface GetSnapListenerData {
    void onSnapFilter(int i, int i2, String str);

    void onSnapFilter(ArrayList<BackgroundImage> arrayList, int i);
}
