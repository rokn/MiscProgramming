package com.appolica.weatherify.android.ui.view.layerimageview.drawer.complex;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.appolica.weatherify.android.ui.view.layerimageview.drawer.BaseDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.BmpDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.PathDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.BmpLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.PathLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.complex.PathBmpLayer;

public class PathBmpDrawer extends BaseDrawer {

    private PathDrawer pathDrawer;
    private BmpDrawer bmpDrawer;

    private PathBmpLayer layer;

    public PathBmpDrawer(PathBmpLayer layer) {
        this.layer = layer;
        this.pathDrawer = new PathDrawer(layer.getPathLayer());
        this.bmpDrawer = new BmpDrawer(layer.getBmpLayer());
    }

    public PathBmpDrawer(PathLayer pathLayer, BmpLayer bmpLayer) {
        this(new PathBmpLayer(pathLayer, bmpLayer));
    }

    @Override
    public void draw(Canvas canvas, Rect drawingRect) {
        if (layer.shouldDraw()) {
            if (pathDrawer != null) {
                pathDrawer.draw(canvas, drawingRect);
            }

            if (bmpDrawer != null) {
                bmpDrawer.draw(canvas, drawingRect);
            }
        }
    }

    @Override
    public void updateRect(boolean changed, Rect rect) {
        if (pathDrawer != null) {
            pathDrawer.updateRect(changed, rect);
        }

        if (bmpDrawer != null) {
            bmpDrawer.updateRect(changed, rect);
        }
    }

    @Override
    public void destroy() {
        if (pathDrawer != null) {
            pathDrawer.destroy();
        }

        if (bmpDrawer != null) {
            bmpDrawer.destroy();
        }
    }

    public PathDrawer getPathDrawer() {
        return pathDrawer;
    }

    public BmpDrawer getBmpDrawer() {
        return bmpDrawer;
    }

    public PathBmpLayer getLayer() {
        return layer;
    }
}
