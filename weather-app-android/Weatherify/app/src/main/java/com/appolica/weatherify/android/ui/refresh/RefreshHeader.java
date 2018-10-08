package com.appolica.weatherify.android.ui.refresh;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Region;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.ui.activity.settings.RefreshAnimationListener;
import com.appolica.weatherify.android.ui.animation.SimpleAnimatorListener;
import com.appolica.weatherify.android.utils.Utils;
import com.gelitenight.waveview.library.WaveView;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by Alexander Iliev on 10.04.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
public class RefreshHeader extends ConstraintLayout implements PtrUIHandler {
    private static final String TAG = "RefreshHeader";

    private static final int SUN_RADIUS_DP = 8;
    private static final int SUN_RAYS_COUNT = 8;
    private static final int SUN_RAYS_RADIUS_MARGIN_DP = 3;
    private static final int SUN_RAY_WIDTH_DP = 2;
    private static final int SUN_RAY_LENGTH_DP = 6;
    private static final int ANGLE_BETWEEN_RAYS = 360 / SUN_RAYS_COUNT;

    private static final int BOTTOM_LINE_HEIGHT_DP = 15;
    private static final float SUN_CENTER_VERTICAL_DIVIDER = 1.6f;

    private static final int BOTTOMLINE_CURVE_RADIUS_DP = 55;
    private static final float CURVED_LINE_CURVE_FACTOR = 2.2f;

    private static final float RAY_OUT_DURATION = 2000;

    private Point baseSunCenterPoint;
    private Point sunCenterPoint;
    private Paint sunPaint;
    private Paint bottomCurvePaint;
    private Paint sunRaysExitPaint;
    private Path bottomCurvePath;
    private Path sunRaysExitPath;
    private PathMeasure sunRaysExitMeasure;
    private WaveView refreshWave;
    private WaveHelper waveHelper;
    private float sunRayLength;
    private float sunRotation;
    private long exitRaysAnimationStartTime;
    private float[] dummy = new float[2];
    private float[] exitRayStartCoords = new float[2];
    private float[] exitRayEndCoords = new float[2];
    private ValueAnimator sunInfiniteRotation;
    private ValueAnimator sunSingleRotation;
    private ValueAnimator sunColorChangeAnim;
    private ValueAnimator sunExitAnimator;
    private boolean shouldAnimateExitRays;
    private long singleRotationDuration;
    private Interpolator raysOutInterpolator;
    private RefreshAnimationListener refreshAnimationListener;

    public RefreshHeader(Context context) {
        super(context);
        init();
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setWillNotDraw(false);

        raysOutInterpolator = PathInterpolatorCompat.create(0.3f, 0.0f, 0.58f, 1.0f);

        singleRotationDuration =
                getContext().getResources().getInteger(R.integer.refreshSunRotationDuration);
        sunRayLength = Utils.convertDpToPixels(getContext(), SUN_RAY_LENGTH_DP);

        sunRotation = 0f;

        sunPaint = new Paint();
        sunPaint.setStrokeWidth(Utils.convertDpToPixels(getContext(), SUN_RAY_WIDTH_DP));
        sunPaint.setAntiAlias(true);
        sunPaint.setDither(true);
        sunPaint.setStrokeCap(Paint.Cap.ROUND);
        sunPaint.setColor(
                ContextCompat.getColor(getContext(), R.color.mainScreenTimeArcText)
        );

        bottomCurvePaint = new Paint();
        bottomCurvePaint.setStrokeWidth(0);
        bottomCurvePaint.setStyle(Paint.Style.STROKE);
        bottomCurvePaint.setAntiAlias(true);
        bottomCurvePaint.setDither(true);
        bottomCurvePaint.setColor(
                ContextCompat.getColor(getContext(), R.color.mainScreenWeeklyAbbreviation)
        );

        sunRaysExitPaint = new Paint();
        sunRaysExitPaint.setStrokeWidth(Utils.convertDpToPixels(getContext(), SUN_RAY_WIDTH_DP));
        sunRaysExitPaint.setStyle(Paint.Style.STROKE);
        sunRaysExitPaint.setAntiAlias(true);
        sunRaysExitPaint.setDither(true);
        sunRaysExitPaint.setStrokeCap(Paint.Cap.ROUND);
        sunRaysExitPaint.setColor(
                ContextCompat.getColor(getContext(), R.color.mainScreenTimeArcText)
        );

        sunInfiniteRotation =
                (ValueAnimator) AnimatorInflater.loadAnimator(getContext(), R.animator.refresh_sun_rotate);
        sunSingleRotation = sunInfiniteRotation.clone();

        sunInfiniteRotation.addUpdateListener(animation -> sunRotation = (float) animation.getAnimatedValue());
        sunInfiniteRotation.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!shouldAnimateExitRays) {
                    shouldAnimateExitRays = true;
                    exitRaysAnimationStartTime = System.currentTimeMillis();
                    sunSingleRotation.start();
                }
            }
        });

        sunSingleRotation.setRepeatCount(0);
        sunSingleRotation.addUpdateListener(animation -> sunRotation = (float) animation.getAnimatedValue());
        sunSingleRotation.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sunColorChangeAnim.start();
                sunExitAnimator.start();
            }
        });

        sunColorChangeAnim = new ValueAnimator();
        sunColorChangeAnim.setIntValues(ContextCompat.getColor(getContext(), R.color.refreshSun),
                ContextCompat.getColor(getContext(), R.color.refreshWave));
        sunColorChangeAnim.setEvaluator(new ArgbEvaluator());
        sunColorChangeAnim.setDuration(getContext().getResources().getInteger(R.integer.sunExitAnimationsDuration));
        sunColorChangeAnim.addUpdateListener(valueAnimator -> sunPaint.setColor((Integer) valueAnimator.getAnimatedValue()));

        sunExitAnimator =
                (ValueAnimator) AnimatorInflater.loadAnimator(getContext(), R.animator.refresh_sun_color_change_and_dive);
        sunExitAnimator
                .addUpdateListener(animation -> sunCenterPoint.y = (int) (getHeight() / 2 + getHeight() / 7 +
                        (float) animation.getAnimatedValue()));
        sunExitAnimator
                .addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getWaveView().setWaveColor(
                                Color.TRANSPARENT,
                                ContextCompat.getColor(getContext(), R.color.refreshWave));
                        getWaveHelper().start();
                    }
                });
    }

    @Override
    public void onViewAdded(View view) {
        super.onViewAdded(view);

        if (view instanceof WaveView) {
            ((WaveView) view).setShapeType(WaveView.ShapeType.SQUARE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initializeDrawableInfo();

        if (shouldAnimateExitRays) {
            animateExitRays(canvas);
        }

        canvas.clipPath(bottomCurvePath, Region.Op.DIFFERENCE);
        canvas.drawPath(bottomCurvePath, bottomCurvePaint);

        animateSun(canvas);

        invalidate();
    }

    private void initializeDrawableInfo() {
        if (sunCenterPoint == null) {
            setSunCenterPoint();
        }

        if (bottomCurvePath == null) {
            initializeBottomLine();
        }

        if (sunRaysExitPath == null) {
            initializeRaysOut();
        }
    }

    private void initializeBottomLine() {
        bottomCurvePath = new Path();

        float centerX = getWidth() / 2;
        float height = getHeight();

        bottomCurvePath.moveTo(0, height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP));
        bottomCurvePath.lineTo(centerX - Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP),
                height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP));
        bottomCurvePath.cubicTo(
                centerX - Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP / CURVED_LINE_CURVE_FACTOR),
                height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP),
                centerX - Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP / CURVED_LINE_CURVE_FACTOR),
                height,
                centerX,
                height
        );
        bottomCurvePath.cubicTo(
                centerX + Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP / CURVED_LINE_CURVE_FACTOR),
                height,
                centerX + Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP / CURVED_LINE_CURVE_FACTOR),
                height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP),
                centerX + Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP),
                height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP)
        );
        bottomCurvePath.lineTo(getWidth(), height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP));
        bottomCurvePath.lineTo(getWidth(), height);
        bottomCurvePath.lineTo(0, height);
        bottomCurvePath.lineTo(0, Utils.convertDpToPixels(getContext(), 80));
    }

    private void initializeRaysOut() {
        sunRaysExitPath = new Path();

        float centerX = getWidth() / 2;
        float height = getHeight();

        float startRadius =
                Utils.convertDpToPixels(getContext(), SUN_RADIUS_DP + SUN_RAYS_RADIUS_MARGIN_DP);
        Point startPoint = getPointFromCenter(sunCenterPoint, 0, startRadius);

        sunRaysExitPath.moveTo(startPoint.x, startPoint.y);
        sunRaysExitPath.lineTo(startPoint.x, startPoint.y + Utils.convertDpToPixels(getContext(), SUN_RAY_LENGTH_DP));
        sunRaysExitPath.cubicTo(
                centerX - Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP / CURVED_LINE_CURVE_FACTOR),
                height,
                centerX - Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP / CURVED_LINE_CURVE_FACTOR),
                height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP),
                centerX - Utils.convertDpToPixels(getContext(), BOTTOMLINE_CURVE_RADIUS_DP),
                height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP)
        );

        sunRaysExitPath.lineTo(0, height - Utils.convertDpToPixels(getContext(), BOTTOM_LINE_HEIGHT_DP));

        sunRaysExitMeasure = new PathMeasure(sunRaysExitPath, false);
    }

    private void animateExitRays(Canvas canvas) {
        long timeElapsed = System.currentTimeMillis() - exitRaysAnimationStartTime;

        long timeBetweenRays = singleRotationDuration / SUN_RAYS_COUNT;
        float exitPathLength = sunRaysExitMeasure.getLength();

        int raysToAnimate = (int) Math.min((timeElapsed / timeBetweenRays) + 1, SUN_RAYS_COUNT);


        for (int i = 0; i < raysToAnimate; i++) {
            long rayTimeProgress = timeElapsed - i * timeBetweenRays;


            if (rayTimeProgress >= RAY_OUT_DURATION) {
                continue;
            }

            float rayPathProgress = exitPathLength *
                    raysOutInterpolator.getInterpolation(rayTimeProgress / RAY_OUT_DURATION);

            float rayStartDistance = rayPathProgress + Utils.convertDpToPixels(getContext(), SUN_RAY_LENGTH_DP);

            sunRaysExitMeasure.getPosTan(rayStartDistance, exitRayStartCoords, dummy);
            sunRaysExitMeasure.getPosTan(rayPathProgress, exitRayEndCoords, dummy);

            canvas.drawLine(
                    exitRayStartCoords[0], exitRayStartCoords[1],
                    exitRayEndCoords[0], exitRayEndCoords[1],
                    sunRaysExitPaint
            );
        }
    }

    private void animateSun(Canvas canvas) {
        canvas.drawCircle(
                sunCenterPoint.x,
                sunCenterPoint.y,
                Utils.convertDpToPixels(getContext(), SUN_RADIUS_DP),
                sunPaint);

        float startRadius =
                Utils.convertDpToPixels(getContext(), SUN_RADIUS_DP + SUN_RAYS_RADIUS_MARGIN_DP);

        for (int i = 0; i < SUN_RAYS_COUNT; i++) {
            float currentRayRotation = this.sunRotation + i * ANGLE_BETWEEN_RAYS;

            if (shouldAnimateExitRays && currentRayRotation < 0) {
                continue;
            }

            Point startPoint =
                    getPointFromCenter(sunCenterPoint, currentRayRotation, startRadius);
            Point endPoint =
                    getPointFromCenter(sunCenterPoint, currentRayRotation, startRadius + sunRayLength);

            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, sunPaint);
        }
    }

    public void endRefresh() {
        sunInfiniteRotation.setRepeatCount(0);
    }

    public void setPtrFrameLayout(final PtrFrameLayout pullToRefreshLayout) {
        getWaveHelper().addAnimationListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pullToRefreshLayout.refreshComplete();
            }
        });
    }

    public void setRefreshAnimationListener(RefreshAnimationListener listener) {
        refreshAnimationListener = listener;
    }

    private Point getPointFromCenter(Point center, float rotation, float radius) {
        double rotationRadians = Math.toRadians(rotation);

        int a = (int) (radius * Math.sin(rotationRadians));
        int b = (int) (radius * Math.cos(rotationRadians));

        return new Point(center.x + a, center.y + b);
    }

    private void setSunCenterPoint() {
        baseSunCenterPoint = new Point(getWidth() / 2, (int) (getHeight() / SUN_CENTER_VERTICAL_DIVIDER));
        sunCenterPoint = new Point(baseSunCenterPoint);
    }

    private void resetSunCenterPoint() {
        sunCenterPoint.x = baseSunCenterPoint.x;
        sunCenterPoint.y = baseSunCenterPoint.y;
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        resetRefreshAnimation();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        getWaveView().invalidate();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        sunInfiniteRotation.start();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        refreshAnimationListener.onRefreshAnimationFinished();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
    }

    private void resetRefreshAnimation() {
        getWaveView().setShowWave(false);
        shouldAnimateExitRays = false;
        sunPaint.setColor(ContextCompat.getColor(getContext(), R.color.refreshSun));
        resetSunCenterPoint();
    }

    private WaveView getWaveView() {
        if (refreshWave == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View currentChild = getChildAt(i);
                if (currentChild instanceof WaveView) {
                    refreshWave = (WaveView) currentChild;
                    break;
                }
            }
        }

        return refreshWave;
    }

    private WaveHelper getWaveHelper() {
        if (waveHelper == null && getWaveView() != null) {
            waveHelper = new WaveHelper(getWaveView());
        }

        return waveHelper;
    }
}
