package com.freethumbnailmaker.nowatermark.activity;

import com.bumptech.glide.load.Key;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class IntegerVersionSignature implements Key {
    private final int currentVersion;

    public IntegerVersionSignature(int i) {
        this.currentVersion = i;
    }

    public boolean equals(Object obj) {
        return obj instanceof IntegerVersionSignature && this.currentVersion == ((IntegerVersionSignature) obj).currentVersion;
    }

    public int hashCode() {
        return this.currentVersion;
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ByteBuffer.allocate(32).putInt(this.currentVersion).array());
    }
}
