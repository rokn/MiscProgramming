package com.appolica.weatherify.android.ui.view.layerimageview;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.annimon.stream.Stream;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.DrawerFactory;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.LayerDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.BmpLayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bogomil Kolarov on 18.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class LayeredImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "LayeredImageView";

    private List<LayerDrawer> drawers = new ArrayList<>();
    private boolean layersDisabled = true;

    private Rect drawingRect = new Rect();

    private Animator animator;

    public LayeredImageView(Context context) {
        super(context);
    }

    public LayeredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LayeredImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateRects(changed, left, top, right, bottom);
    }

    private void updateRects(boolean changed, int left, int top, int right, int bottom) {
        if (drawers == null) {
            return;
        }

        Stream.of(drawers).forEach(drawer -> {
            final Rect rect = new Rect(left, top, right, bottom);
            drawer.updateRect(changed, rect);
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (areLayersDisabled()) {
            super.onDraw(canvas);
        } else {
            getDrawingRect(drawingRect);
            drawLayers(canvas, drawingRect);
        }
    }

    private void drawLayers(Canvas canvas, Rect drawingRect) {
        canvas.save();
        if (drawers != null) {
            Stream.of(drawers).forEach(drawer -> drawer.draw(canvas, drawingRect));
        }
        canvas.restore();
    }

    public void mergeLayers() {

        final Bitmap mergedBitmap =
                Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(mergedBitmap);
        drawLayers(canvas, drawingRect);

        final BmpLayer mergedLayer =
                BmpLayer.Builder.instance()
                        .bitmap(mergedBitmap)
                        .shouldDraw(true)
                        .build();

        Stream.of(drawers).forEach(LayerDrawer::destroy);

        drawers.clear();
        addDrawer(DrawerFactory.createFor(mergedLayer));
    }

    public void setDrawer(LayerDrawer drawer) {
        drawers.clear();
        addDrawer(drawer);
    }

    public void addDrawer(LayerDrawer drawer) {
        drawers.add(drawer);
        notifyDrawersChanged();
    }

    public void addDrawers(List<LayerDrawer> drawers) {
        this.drawers.addAll(drawers);
        notifyDrawersChanged();
    }

    public void setDrawers(List<LayerDrawer> drawers) {
        this.drawers = drawers;
        notifyDrawersChanged();
    }

    public List<LayerDrawer> getDrawers() {
        return drawers;
    }

    public void setLayers(int... resIds) {
        if (resIds == null) {
            return;
        }

        List<LayerDrawer> drawers = new ArrayList<>();

        for (int i = 0; i < resIds.length; i++) {

            final BmpLayer layer =
                    BmpLayer.Builder.instance()
                            .bitmap(getContext(), resIds[i])
                            .shouldDraw(true)
                            .build();

            drawers.add(DrawerFactory.createFor(layer));
        }

        setDrawers(drawers);
    }

    public boolean areLayersDisabled() {
        return layersDisabled;
    }

    public void setLayersDisabled(boolean layersDisabled) {
        this.layersDisabled = layersDisabled;

        if (!layersDisabled) {
            setImageDrawable(null);
        }

        notifyDrawersChanged();
    }

    public void notifyDrawersChanged() {
        invalidate();
        if (!isInLayout()) {
            requestLayout();
        }
    }

    public void startLayerAnimation(int index) {
        drawers.get(index).startAnimation();
    }

    public void startAnimator() {
        if (animator != null) {
            animator.start();
        }
    }

    public Animator getAnimator() {
        return animator;
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }
}
