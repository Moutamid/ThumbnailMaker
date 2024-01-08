package com.freethumbnailmaker.nowatermark.utils;

import android.media.ExifInterface;
import android.text.TextUtils;
import com.alibaba.fastjson.asm.Opcodes;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

public class ExifUtils {
    private ExifUtils() {
    }

    public static int getExifRotation(String str) {
        try {
            String attribute = new ExifInterface(str).getAttribute("Orientation");
            if (TextUtils.isEmpty(attribute)) {
                return 0;
            }
            int parseInt = Integer.parseInt(attribute);
            if (parseInt == 3) {
                return Opcodes.GETFIELD;
            }
            if (parseInt == 6) {
                return 90;
            }
            if (parseInt != 8) {
                return 0;
            }
            return VerticalSeekBar.ROTATION_ANGLE_CW_270;
        } catch (Exception e) {
            return 0;
        }
    }
}
