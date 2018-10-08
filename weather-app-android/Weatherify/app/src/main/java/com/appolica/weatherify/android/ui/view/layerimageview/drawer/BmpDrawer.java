package com.appolica.weatherify.android.ui.view.layerimageview.drawer;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.appolica.weatherify.android.ui.view.layerimageview.layer.BmpLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.Gravity;

public class BmpDrawer extends BaseDrawer {

    private BmpLayer layer;

    public BmpDrawer() {
    }

    public BmpDrawer(BmpLayer layer) {
        this.layer = layer;
    }

    @Override
    public void draw(Canvas canvas, Rect drawingRect) {
        if (!layer.shouldDraw()) {
            return;
        }

        final int gravity = layer.getGravity();
        final PointF bmpPos = layer.getPosition();
        final RectF bmpRect = layer.getRect();

        final float bmpWidth = bmpRect.right - bmpRect.left;
        final float bmpHeight = bmpRect.bottom - bmpRect.top;

        float x = bmpRect.left;
        float y = bmpRect.top;
        if ((gravity & Gravity.GRAVITY_CENTER_HORIZONTAL) == Gravity.GRAVITY_CENTER_HORIZONTAL) {
            x = calcCenterX(drawingRect, bmpRect);
        }

        if ((gravity & Gravity.GRAVITY_CENTER_VERTICAL) == Gravity.GRAVITY_CENTER_VERTICAL) {
            y = calcCenterY(drawingRect, bmpRect);
        }

        if ((gravity & Gravity.GRAVITY_LEFT) == Gravity.GRAVITY_LEFT) {
            x = drawingRect.left;
        }

        if ((gravity & Gravity.GRAVITY_TOP) == Gravity.GRAVITY_TOP) {
            y = drawingRect.top;
        }

        if ((gravity & Gravity.GRAVITY_RIGHT) == Gravity.GRAVITY_RIGHT) {
            x = drawingRect.right - bmpWidth;
        }

        if ((gravity & Gravity.GRAVITY_BOTTOM) == Gravity.GRAVITY_BOTTOM) {
            y = drawingRect.bottom - bmpHeight;
        }

        if (gravity == Gravity.GRAVITY_NONE) {
            x = bmpRect.left + bmpPos.x;
            y = bmpRect.top + bmpPos.y;
        }

        bmpRect.offsetTo(x, y);

        canvas.drawBitmap(layer.getBitmap(), null, bmpRect, layer.getPaint());
    }

    private float calcCenterX(Rect drawingRect, RectF bitmapRect) {
        final float bmpWidth = bitmapRect.right - bitmapRect.left;
        final int drawWidth = drawingRect.right - drawingRect.left;

        return drawingRect.left + (drawWidth - bmpWidth) / 2;
    }

    private float calcCenterY(Rect drawingRect, RectF bitmapRect) {
        final float bmpHeight = bitmapRect.bottom - bitmapRect.top;
        final int drawHeight = drawingRect.bottom - drawingRect.top;

        return drawingRect.top + (drawHeight - bmpHeight) / 2;
    }

    @Override
    public void updateRect(boolean changed, Rect rect) {
        final RectF bitmapRect = layer.getRect();

        final float bmpWidth = bitmapRect.right - bitmapRect.left;
        final float bmpHeight = bitmapRect.bottom - bitmapRect.top;
        final int imgViewWidth = rect.right - rect.left;
        final int imgViewHeight = rect.bottom - rect.top;

        float scale = 1;
        switch (layer.getScale()) {
            case NO_SCALE:
                scale = 1;
                break;
            case INSIDE:
                scale = calcScaleInside(bmpWidth, bmpHeight, imgViewWidth, imgViewHeight);
                break;
        }

        final float scaledBmpWidth = bmpWidth * scale;
        final float scaledBmpHeight = bmpHeight * scale;

        bitmapRect.left = 0;
        bitmapRect.top = 0;
        bitmapRect.right = scaledBmpWidth;
        bitmapRect.bottom = scaledBmpHeight;
    }

    private float calcScaleInside(float bmpWidth, float bmpHeight, int imgViewWidth, int imgViewHeight) {
        float scale;
        if (bmpWidth <= imgViewWidth && bmpHeight <= imgViewHeight) {
            // No scale needed
            scale = 1;
        } else if (bmpWidth < bmpHeight) {
            // Scale by height
            scale = (float) imgViewHeight / bmpHeight;
        } else {
            // Scale by width
            scale = (float) imgViewWidth / bmpWidth;
        }

        return scale;
    }

    @Override
    public void destroy() {
        layer.destroy();
        layer = null;
    }

    public BmpLayer getLayer() {
        return layer;
    }
}
