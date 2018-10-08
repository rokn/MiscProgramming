package com.appolica.weatherify.android.ui.view.layerimageview.drawer;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.appolica.weatherify.android.ui.view.layerimageview.layer.PathLayer;

public class PathDrawer extends BaseDrawer {

    private PathLayer layer;

    public PathDrawer(PathLayer layer) {
        this.layer = layer;
    }

    @Override
    public void draw(Canvas canvas, Rect drawingRect) {

        if (layer.shouldClipPath()) {
            canvas.clipPath(layer.getPath());
        }

        if (layer.shouldDrawPath()) {
            canvas.drawPath(layer.getPath(), layer.getPaint());
        }
    }

    @Override
    public void updateRect(boolean changed, Rect rect) {

    }

    @Override
    public void destroy() {
        layer.destroy();
        layer = null;
    }
}
