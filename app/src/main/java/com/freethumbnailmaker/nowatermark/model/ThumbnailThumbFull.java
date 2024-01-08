package com.freethumbnailmaker.nowatermark.model;

import android.os.Parcel;
import android.os.Parcelable;


public class ThumbnailThumbFull implements Parcelable, Comparable, Cloneable {
    public static final Parcelable.Creator<ThumbnailThumbFull> CREATOR = new Parcelable.Creator<ThumbnailThumbFull>() {
        public ThumbnailThumbFull createFromParcel(Parcel parcel) {
            return new ThumbnailThumbFull(parcel);
        }

        public ThumbnailThumbFull[] newArray(int i) {
            return new ThumbnailThumbFull[i];
        }
    };
    int post_id;
    String post_thumb;

    public int describeContents() {
        return 0;
    }

    public ThumbnailThumbFull() {
    }

    public ThumbnailThumbFull(Parcel parcel) {
        this.post_id = parcel.readInt();
        this.post_thumb = parcel.readString();
    }

    public int getPost_id() {
        return this.post_id;
    }

    public void setPost_id(int i) {
        this.post_id = i;
    }

    public String getPost_thumb() {
        return this.post_thumb;
    }

    public void setPost_thumb(String str) {
        this.post_thumb = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.post_id);
        parcel.writeString(this.post_thumb);
    }

    public int compareTo(Object obj) {
        ThumbnailThumbFull thumbnailThumbFull = (ThumbnailThumbFull) obj;
        return (thumbnailThumbFull.post_id != this.post_id || !thumbnailThumbFull.post_thumb.equals(this.post_thumb)) ? 1 : 0;
    }

    public ThumbnailThumbFull clone() {
        try {
            return (ThumbnailThumbFull) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
