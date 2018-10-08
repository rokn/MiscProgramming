package com.appolica.weatherify.android.ui.view.layerimageview.layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class BmpLayer extends Layer {

    private Bitmap bitmap;

    private Paint paint;
    private RectF rect;
    private PointF position;

    @Gravity.Values
    private int gravity;
    private Scale scale;
    private float rotation;

    private boolean shouldDraw;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public boolean shouldDraw() {
        return shouldDraw;
    }

    public void setShouldDraw(boolean shouldDraw) {
        this.shouldDraw = shouldDraw;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setRotation(float degrees) {
        this.rotation = degrees;
    }

    public enum Scale {
        NO_SCALE, INSIDE
    }

    public static class Builder {
        private Bitmap bitmap;
        private Paint bitmapPaint;
        private RectF bitmapRect;
        private PointF bitmapPosition;
        private boolean shouldDraw;
        private Scale scale = Scale.INSIDE;
        private float rotation = 0;

        @Gravity.Values
        private int gravity = Gravity.GRAVITY_CENTER;

        public static BmpLayer.Builder instance() {
            return new Builder();
        }

        public BmpLayer.Builder bitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public BmpLayer.Builder bitmap(Context context, int resId) {
            bitmap(BitmapFactory.decodeResource(context.getResources(), resId));
            return this;
        }

        public BmpLayer.Builder bitmapPaint(Paint bitmapPaint) {
            this.bitmapPaint = bitmapPaint;
            return this;
        }

        public BmpLayer.Builder bitmapRect(RectF bitmapRect) {
            this.bitmapRect = bitmapRect;
            return this;
        }

        public BmpLayer.Builder bitmapRect(float left, float top, float right, float bottom) {
            this.bitmapRect = new RectF(left, top, right, bottom);
            return this;
        }

        public BmpLayer.Builder bitmapPosition(PointF bitmapPosition) {
            this.bitmapPosition = bitmapPosition;
            return this;
        }

        public BmpLayer.Builder shouldDraw(boolean shouldDrawBitmap) {
            this.shouldDraw = shouldDrawBitmap;
            return this;
        }

        public BmpLayer.Builder scale(Scale scale) {
            this.scale = scale;
            return this;
        }

        public BmpLayer.Builder gravity(@Gravity.Values int gravity) {
            this.gravity = gravity;
            return this;
        }

        public BmpLayer.Builder rotation(float degrees) {
            this.rotation = degrees;
            return this;
        }

        public BmpLayer build() {
            final BmpLayer layer = new BmpLayer();

            if (bitmapRect == null) {
                bitmapRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
            }

            if (bitmapPosition == null) {
                bitmapPosition = new PointF(0, 0);
            }

            layer.setBitmap(bitmap);
            layer.setPaint(bitmapPaint);
            layer.setRect(bitmapRect);
            layer.setPosition(bitmapPosition);
            layer.setShouldDraw(shouldDraw);
            layer.setScale(scale);
            layer.setGravity(gravity);
            layer.setRotation(rotation);

            return layer;
        }
    }

    @Override
    public void destroy() {
        bitmap.recycle();
        paint = null;
        rect = null;
        position = null;
    }
}
