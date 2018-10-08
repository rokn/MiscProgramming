package com.appolica.weatherify.android.ui.view.layerimageview.layer;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class PathLayer extends Layer {
    private Path path;
    private Paint paint;
    private Point pathPosition;

    private PathConstructor pathConstructor;

    private boolean shouldClipPath;
    private boolean shouldDrawPath;

    public PathLayer() {
        pathConstructor = new PathConstructor();
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public PathConstructor getPathConstructor() {
        if (path == null) {
            path = new Path();
        }

        pathConstructor.setPath(path);
        return pathConstructor;
    }

    public void setPathConstructor(PathConstructor pathConstructor) {
        this.pathConstructor = pathConstructor;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Point getPathPosition() {
        return pathPosition;
    }

    public void setPathPosition(Point pathPosition) {
        this.pathPosition = pathPosition;
    }

    public boolean shouldClipPath() {
        return shouldClipPath;
    }

    public void setShouldClipPath(boolean shouldClipPath) {
        this.shouldClipPath = shouldClipPath;
    }

    public boolean shouldDrawPath() {
        return shouldDrawPath;
    }

    public void setShouldDrawPath(boolean shouldDrawPath) {
        this.shouldDrawPath = shouldDrawPath;
    }

    public static class Builder {
        private Path path;
        private Paint pathPaint;
        private Point pathPosition;
        private boolean shouldClipPath;
        private boolean shouldDrawPath;

        public static Builder instance() {
            return new Builder();
        }

        public Builder path(Path path) {
            this.path = path;
            return this;
        }

        public Builder pathPaint(Paint pathPaint) {
            this.pathPaint = pathPaint;
            return this;
        }

        public Builder pathPosition(Point pathPosition) {
            this.pathPosition = pathPosition;
            return this;
        }

        public Builder shouldClipPath(boolean shouldClipPath) {
            this.shouldClipPath = shouldClipPath;
            return this;
        }

        public Builder shouldDrawPath(boolean shouldDrawPath) {
            this.shouldDrawPath = shouldDrawPath;
            return this;
        }

        public PathLayer build() {
            final PathLayer layer = new PathLayer();

            layer.setPath(path);
            layer.setPaint(pathPaint);
            layer.setPathPosition(pathPosition);
            layer.setShouldClipPath(shouldClipPath);
            layer.setShouldDrawPath(shouldDrawPath);

            return layer;
        }
    }

    @Override
    public void destroy() {
        path = null;
        paint = null;
        pathPosition = null;
    }
}
