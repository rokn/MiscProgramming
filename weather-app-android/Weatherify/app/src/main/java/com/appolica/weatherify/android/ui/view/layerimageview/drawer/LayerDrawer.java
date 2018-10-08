package com.appolica.weatherify.android.ui.view.layerimageview.drawer;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.appolica.weatherify.android.ui.view.layerimageview.Animatable;

public interface LayerDrawer extends Animatable {
    void draw(Canvas canvas, Rect drawingRect);

    void updateRect(boolean changed, Rect rect);

    void destroy();

}
