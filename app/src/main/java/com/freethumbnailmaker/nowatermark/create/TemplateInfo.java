package com.freethumbnailmaker.nowatermark.create;

import com.freethumbnailmaker.nowatermark.text.TextInfo;
import com.freethumbnailmaker.nowatermark.utils.ElementInfo;
import java.util.ArrayList;

public class TemplateInfo {
    private String FRAME_NAME;
    int OVERLAY_BLUR = 0;
    private String OVERLAY_NAME = "";
    int OVERLAY_OPACITY = 0;
    private String PROFILE_TYPE;
    private String RATIO;
    private String SEEK_VALUE;
    private String TEMPCOLOR = "";
    private int TEMPLATE_ID;
    private String TEMP_PATH = "";
    private String THUMB_URI;
    private String TYPE;
    ArrayList<ElementInfo> elementInfoArrayList;
    ArrayList<TextInfo> textInfoArrayList;

    public TemplateInfo(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, int i, int i2) {
        this.THUMB_URI = str;
        this.FRAME_NAME = str2;
        this.RATIO = str3;
        this.PROFILE_TYPE = str4;
        this.SEEK_VALUE = str5;
        this.TYPE = str6;
        this.TEMP_PATH = str7;
        this.OVERLAY_OPACITY = i;
        this.TEMPCOLOR = str8;
        this.OVERLAY_NAME = str9;
        this.OVERLAY_BLUR = i2;
    }

    public TemplateInfo() {
    }

    public int getTEMPLATE_ID() {
        return this.TEMPLATE_ID;
    }

    public void setTEMPLATE_ID(int i) {
        this.TEMPLATE_ID = i;
    }

    public String getTHUMB_URI() {
        return this.THUMB_URI;
    }

    public void setTHUMB_URI(String str) {
        this.THUMB_URI = str;
    }

    public String getFRAME_NAME() {
        return this.FRAME_NAME;
    }

    public void setFRAME_NAME(String str) {
        this.FRAME_NAME = str;
    }

    public String getRATIO() {
        return this.RATIO;
    }

    public void setRATIO(String str) {
        this.RATIO = str;
    }

    public String getPROFILE_TYPE() {
        return this.PROFILE_TYPE;
    }

    public void setPROFILE_TYPE(String str) {
        this.PROFILE_TYPE = str;
    }

    public String getSEEK_VALUE() {
        return this.SEEK_VALUE;
    }

    public void setSEEK_VALUE(String str) {
        this.SEEK_VALUE = str;
    }

    public String getTYPE() {
        return this.TYPE;
    }

    public void setTYPE(String str) {
        this.TYPE = str;
    }

    public int getOVERLAY_OPACITY() {
        return this.OVERLAY_OPACITY;
    }

    public void setOVERLAY_OPACITY(int i) {
        this.OVERLAY_OPACITY = i;
    }

    public String getTEMPCOLOR() {
        return this.TEMPCOLOR;
    }

    public void setTEMPCOLOR(String str) {
        this.TEMPCOLOR = str;
    }

    public String getOVERLAY_NAME() {
        return this.OVERLAY_NAME;
    }

    public void setOVERLAY_NAME(String str) {
        this.OVERLAY_NAME = str;
    }

    public String getTEMP_PATH() {
        return this.TEMP_PATH;
    }

    public void setTEMP_PATH(String str) {
        this.TEMP_PATH = str;
    }

    public int getOVERLAY_BLUR() {
        return this.OVERLAY_BLUR;
    }

    public void setOVERLAY_BLUR(int i) {
        this.OVERLAY_BLUR = i;
    }

    public ArrayList<TextInfo> getTextInfoArrayList() {
        return this.textInfoArrayList;
    }

    public void setTextInfoArrayList(ArrayList<TextInfo> arrayList) {
        this.textInfoArrayList = arrayList;
    }

    public ArrayList<ElementInfo> getElementInfoArrayList() {
        return this.elementInfoArrayList;
    }

    public void setElementInfoArrayList(ArrayList<ElementInfo> arrayList) {
        this.elementInfoArrayList = arrayList;
    }
}
