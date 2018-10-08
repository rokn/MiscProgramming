package com.appolica.weatherify.android.ui.view.layerimageview.layer.complex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import com.appolica.weatherify.android.ui.view.layerimageview.layer.BmpLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.Layer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.PathLayer;


public class PathBmpLayer extends Layer {

    private PathLayer pathLayer;
    private BmpLayer bmpLayer;

    private boolean shouldDraw = false;

    public PathBmpLayer(PathLayer pathLayer, BmpLayer bmpLayer) {
        this.pathLayer = pathLayer;
        this.bmpLayer = bmpLayer;
    }

    @Override
    public void destroy() {
        pathLayer.destroy();
        bmpLayer.destroy();
    }

    public PathLayer getPathLayer() {
        return pathLayer;
    }

    public BmpLayer getBmpLayer() {
        return bmpLayer;
    }

    public void setShouldDraw(boolean shouldDraw) {
        this.shouldDraw = shouldDraw;
    }

    public boolean shouldDraw() {
        return shouldDraw;
    }

    public static class Builder {
        private PathLayer pathLayer;
        private BmpLayer bmpLayer;
        private boolean shouldDraw;

        public static Builder instance() {
            return new Builder();
        }

        public ExtendedPathBuilder pathLayer() {
            return ExtendedPathBuilder.instance(this);
        }

        public Builder pathLayer(PathLayer pathLayer) {
            this.pathLayer = pathLayer;
            return this;
        }

        public ExtendedBmpBuilder bmpLayer() {
            return ExtendedBmpBuilder.instance(this);
        }

        public Builder bmpLayer(BmpLayer bmpLayer) {
            this.bmpLayer = bmpLayer;
            return this;
        }

        public Builder shouldDraw(boolean shouldDraw) {
            this.shouldDraw = shouldDraw;
            return this;
        }

        public PathBmpLayer build() {
            final PathBmpLayer pathBmpLayer = new PathBmpLayer(pathLayer, bmpLayer);
            pathBmpLayer.setShouldDraw(shouldDraw);
            return pathBmpLayer;
        }
    }

    public static class ExtendedPathBuilder extends PathLayer.Builder {
        private Builder mainBuilder;

        public static ExtendedPathBuilder instance(Builder mainBuilder) {
            return new ExtendedPathBuilder(mainBuilder);
        }

        public ExtendedPathBuilder(Builder mainBuilder) {
            this.mainBuilder = mainBuilder;
        }

        @Override
        public ExtendedPathBuilder path(Path path) {
            super.path(path);
            return this;
        }

        @Override
        public ExtendedPathBuilder pathPaint(Paint pathPaint) {
            super.pathPaint(pathPaint);
            return this;
        }

        @Override
        public ExtendedPathBuilder pathPosition(Point pathPosition) {
            super.pathPosition(pathPosition);
            return this;
        }

        @Override
        public ExtendedPathBuilder shouldClipPath(boolean shouldClipPath) {
            super.shouldClipPath(shouldClipPath);
            return this;
        }

        @Override
        public ExtendedPathBuilder shouldDrawPath(boolean shouldDrawPath) {
            super.shouldDrawPath(shouldDrawPath);
            return this;
        }

        public Builder end() {
            return mainBuilder.pathLayer(build());
        }
    }

    public static class ExtendedBmpBuilder extends BmpLayer.Builder {
        private Builder mainBuilder;

        public static ExtendedBmpBuilder instance(Builder mainBuilder) {
            return new ExtendedBmpBuilder(mainBuilder);
        }

        @Override
        public ExtendedBmpBuilder bitmap(Bitmap bitmap) {
            super.bitmap(bitmap);
            return this;
        }

        @Override
        public ExtendedBmpBuilder bitmap(Context context, int resId) {
            super.bitmap(context, resId);
            return this;
        }

        @Override
        public ExtendedBmpBuilder shouldDraw(boolean shouldDrawBitmap) {
            super.shouldDraw(shouldDrawBitmap);
            return this;
        }

        @Override
        public ExtendedBmpBuilder bitmapPaint(Paint bitmapPaint) {
            super.bitmapPaint(bitmapPaint);
            return this;
        }

        @Override
        public ExtendedBmpBuilder bitmapRect(RectF bitmapRect) {
            super.bitmapRect(bitmapRect);
            return this;
        }

        @Override
        public ExtendedBmpBuilder bitmapPosition(PointF bitmapPosition) {
            super.bitmapPosition(bitmapPosition);
            return this;
        }

        @Override
        public ExtendedBmpBuilder scale(BmpLayer.Scale scale) {
            super.scale(scale);
            return this;
        }

        @Override
        public ExtendedBmpBuilder gravity(int gravity) {
            super.gravity(gravity);
            return this;
        }

        public ExtendedBmpBuilder(Builder mainBuilder) {
            this.mainBuilder = mainBuilder;
        }

        public PathBmpLayer.Builder end() {
            return mainBuilder.bmpLayer(build());
        }
    }
}
