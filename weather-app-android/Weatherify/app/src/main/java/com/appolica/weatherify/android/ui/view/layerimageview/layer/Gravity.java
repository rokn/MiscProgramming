package com.appolica.weatherify.android.ui.view.layerimageview.layer;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Gravity {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true,
            value = {GRAVITY_CENTER,
                    GRAVITY_CENTER_HORIZONTAL,
                    GRAVITY_CENTER_VERTICAL,
                    GRAVITY_LEFT,
                    GRAVITY_TOP,
                    GRAVITY_RIGHT,
                    GRAVITY_BOTTOM,
                    GRAVITY_NONE})
    public @interface Values {
    }

    public static final int GRAVITY_CENTER = 0b00000011;
    public static final int GRAVITY_CENTER_HORIZONTAL = 0b00000001;
    public static final int GRAVITY_CENTER_VERTICAL = 0b00000010;
    public static final int GRAVITY_LEFT = 0b10000000;
    public static final int GRAVITY_TOP = 0b01000000;
    public static final int GRAVITY_RIGHT = 0b00100000;
    public static final int GRAVITY_BOTTOM = 0b00010000;
    public static final int GRAVITY_NONE = 0b00000000;
}
