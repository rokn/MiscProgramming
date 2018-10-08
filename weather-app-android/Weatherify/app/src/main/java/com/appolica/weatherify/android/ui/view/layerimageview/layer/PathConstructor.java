package com.appolica.weatherify.android.ui.view.layerimageview.layer;

import android.graphics.Path;
import android.graphics.RectF;

public class PathConstructor {

    private Path path;

    public PathConstructor() {

    }

    public PathConstructor(Path path) {
        this.path = path;
    }

    public static PathConstructor with(Path path) {
        return new PathConstructor(path).beginPath();
    }

    public PathConstructor beginPath() {
        return this;
    }

    public PathConstructor reset() {
        path.reset();

        return this;
    }

    public PathConstructor moveTo(float x, float y) {
        path.moveTo(x, y);

        return this;
    }

    public PathConstructor lineTo(float x, float y) {
        path.lineTo(x, y);

        return this;
    }

    public PathConstructor addRect(RectF rectF) {
        path.addRect(rectF, Path.Direction.CCW);

        return this;
    }

    public PathConstructor arcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo) {
        path.arcTo(oval, startAngle, sweepAngle, forceMoveTo);

        return this;
    }

    public Path close() {
        path.close();

        return path;
    }

    public Path end() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
