package com.appolica.weatherify.android.ui.view.swiperemovervlayout;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.utils.Utils;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Created by Bogomil Kolarov on 05.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class SwipeRemoveLayout extends FrameLayout implements SwipeListener, SwipeUpdateListener {

    private static final String TAG = "SwipeRemoveLayout";
    public static final int REMOVE_PERCENTAGE = 60;

    private View leftLid;
    private View rightLid;
    private View swipeableView;
    private float idleX;
    private float idleRotation;
    private boolean gotDefaults;

    private SwipeListener swipeListener;

    private TouchHandler touchHandler;

    private boolean removable = true;

    public SwipeRemoveLayout(Context context) {
        super(context);
        init(context);
    }

    public SwipeRemoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeRemoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        final View view = LayoutInflater.from(context).inflate(R.layout.swipe_remove_view, this, true);

        leftLid = view.findViewById(R.id.imageViewTrashCloseL);
        rightLid = view.findViewById(R.id.imageViewTrashCloseR);

        gotDefaults = false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        leftLid.setPivotX(0);
        leftLid.setPivotY(leftLid.getHeight() / 2);

        rightLid.setPivotX(rightLid.getWidth());
        rightLid.setPivotY(rightLid.getHeight() / 2);

        if(gotDefaults) {
            swipeableView.setX(idleX);
            swipeableView.setRotation(idleRotation);
        } else {
            gotDefaults = true;
            idleX = swipeableView.getX();
            idleRotation = swipeableView.getRotation();
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        switch (child.getId()) {
            case R.id.swipeableElement:
                swipeableView = child;
                idleX = swipeableView.getX();
                idleRotation = swipeableView.getRotation();

                touchHandler = new TouchHandler(swipeableView);

                touchHandler.setSwipeListener(this);
                touchHandler.setSwipeUpdateListener(this);

                setOnTouchListener(touchHandler);
                break;
        }
    }

    @Override
    public void onStartSwipeAway(View swipeableView) {
        if (swipeListener != null) {
            swipeListener.onStartSwipeAway(swipeableView);
        }
    }

    @Override
    public void onSwiped(View swipeableView) {
        if (swipeListener != null) {
            swipeListener.onSwiped(swipeableView);
        }
    }

    @Override
    public void onStartReturnToIdle(View swipeableView) {
        setOnTouchListener(null);

        if (swipeListener != null) {
            swipeListener.onStartReturnToIdle(swipeableView);
        }
    }

    @Override
    public void onIdle(View swipeableView) {
        setOnTouchListener(touchHandler);

        if (swipeListener != null) {
            swipeListener.onIdle(swipeableView);
        }
    }

    @Override
    public void onPositionAndRotationUpdate(
            float position,
            float rotation) {

        leftLid.setRotation(-Math.abs(rotation) * 2);
        rightLid.setRotation(Math.abs(rotation) * 2);

    }

    public void setSwipeListener(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public enum SwipeDirection {
        LEFT(-30), RIGHT(30);

        private int endRotation;

        SwipeDirection(int endRotation) {
            this.endRotation = endRotation;
        }

        public int getEndRotation() {
            return endRotation;
        }
    }

    private class GestureHandler extends GestureDetector.SimpleOnGestureListener {

        private static final double SPRING_TENSION = 90.0;
        private static final double SPRING_FRICTION = 5.0;
        private static final double SPRING_DISPLACEMENT_THRESHOLD = 0.15;

        private static final float FLING_VELOCITY_THRESHOLD = 3800f;
        private static final float FLING_DISTANCE_THRESHOLD = 70f;

        private static final int INITIAL_DISTANCE_TO_TRAVEL = 0;

        private View swipeableView;
        private float traveledDistance;

        private float startRotation = 0;
        private float endRotation;
        private float distanceToTravel = INITIAL_DISTANCE_TO_TRAVEL;

        private SwipeDirection lastDirection = null;
        private SwipeListener swipeListener;
        private SwipeUpdateListener swipeUpdateListener;

        private SpringSystem springSystem;
        private final SpringConfig springConfig;

        private GestureHandler(View swipeableView) {
            this.swipeableView = swipeableView;
            this.springSystem = SpringSystem.create();
            springConfig = SpringConfig.fromOrigamiTensionAndFriction(SPRING_TENSION, SPRING_FRICTION);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            reset();

            return isRemovable();
        }

        private void reset() {
            traveledDistance = 0;
            startRotation = 0;
            lastDirection = null;
            distanceToTravel = INITIAL_DISTANCE_TO_TRAVEL;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final SwipeDirection swipeDirection;
            if (distanceX > 0) {
                swipeDirection = SwipeDirection.LEFT;
            } else {
                swipeDirection = SwipeDirection.RIGHT;
            }

            updateProgress(Math.abs(distanceX), swipeDirection);

            lastDirection = swipeDirection;

            getParent().requestDisallowInterceptTouchEvent(true);

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) < FLING_VELOCITY_THRESHOLD
                    || Math.abs(e1.getX() - e2.getX()) < FLING_DISTANCE_THRESHOLD) {
                return false;
            }

            SwipeDirection swipeDirection = resolveDirection(e1, e2);

            animateItemSwipeAway(velocityX, getEndPosition(swipeDirection), swipeDirection);

            return true;
        }

        public boolean onUp(MotionEvent firstDownEvent, MotionEvent currentEvent) {
            final SwipeDirection swipeDirection = resolveDirection(firstDownEvent, currentEvent);
            if (calculateMovedPercentage() >= REMOVE_PERCENTAGE) {
                animateItemSwipeAway(0, getEndPosition(swipeDirection), swipeDirection);
            } else {
                animateItemReturnToIdle(swipeDirection);
            }

            return false;
        }

        private int getEndPosition(SwipeDirection swipeDirection) {
            int endX;
            if (swipeDirection == SwipeDirection.LEFT) {
                endX = -swipeableView.getWidth();
            } else {
                endX = 2 * swipeableView.getWidth();
            }
            return endX;
        }

        private void animateItemSwipeAway(float velocity, int endX, SwipeDirection swipeDirection) {
            final SpringAnimation springAnimation =
                    new SpringAnimation(swipeableView, DynamicAnimation.TRANSLATION_X);

            final SpringForce springForce = new SpringForce();
            springForce.setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
            springForce.setStiffness(SpringForce.STIFFNESS_LOW);
            springForce.setFinalPosition(endX);

            springAnimation.setSpring(springForce);
            springAnimation.setStartVelocity(velocity);

            springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                private float delta = 0;

                @Override
                public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                    delta = Math.abs(value - delta);
                    updateRotation(swipeableView, delta, swipeDirection);
                }
            });

            springAnimation.addEndListener((animation, canceled, value, velocity1) -> {
                if (swipeListener != null) {
                    swipeListener.onSwiped(swipeableView);
                }
            });

            if (swipeListener != null) {
                swipeListener.onStartSwipeAway(swipeableView);
            }

            springAnimation.start();
        }

        private void animateItemReturnToIdle(SwipeDirection swipeDirection) {
            final float distanceToIdle = Math.abs(swipeableView.getX() - idleX);
            final float rotationToIdle = Math.abs(swipeableView.getRotation() - idleRotation);

            if (distanceToIdle == 0) {
                return;
            }

            final Spring spring = springSystem.createSpring();

            spring.setSpringConfig(springConfig);
            spring.setRestDisplacementThreshold(SPRING_DISPLACEMENT_THRESHOLD);

            spring.addListener(new SimpleSpringListener() {
                @Override
                public void onSpringActivate(Spring spring) {
                    if (swipeListener != null) {
                        swipeListener.onStartReturnToIdle(swipeableView);
                    }
                }

                @Override
                public void onSpringUpdate(Spring spring) {
                    final double springPercentage = spring.getCurrentValue();

                    final double translationByPercentage = springPercentage * distanceToIdle;
                    final double rotationByPercentage = springPercentage * rotationToIdle;

                    final float newX;
                    final float newRotation;
                    if (swipeDirection == SwipeDirection.LEFT) {
                        newX = -distanceToIdle + (float) translationByPercentage;
                        newRotation = -rotationToIdle + (float) rotationByPercentage;
                    } else {
                        newX = distanceToIdle - (float) translationByPercentage;
                        newRotation = rotationToIdle - (float) rotationByPercentage;
                    }

                    swipeableView.setX(newX + idleX);
                    swipeableView.setRotation(newRotation + idleRotation);

                    if (swipeUpdateListener != null) {
                        swipeUpdateListener.onPositionAndRotationUpdate(newX, newRotation);
                    }
                }

                @Override
                public void onSpringAtRest(Spring spring) {
                    reset();

                    if (swipeListener != null) {
                        swipeListener.onIdle(swipeableView);
                    }

                    spring.destroy();
                }
            });

            spring.setEndValue(1);
        }

        private void updateProgress(float distanceX, SwipeDirection swipeDirection) {
            updatePosition(swipeableView, distanceX, swipeDirection);
            updateRotation(swipeableView, distanceX, swipeDirection);

            if (swipeUpdateListener != null) {
                swipeUpdateListener.onPositionAndRotationUpdate(
                        swipeableView.getX(),
                        swipeableView.getRotation());
            }
        }

        private void updatePosition(View view, float dx, SwipeDirection direction) {
            final float viewX = view.getX();

            final float newX;

            if (direction == SwipeDirection.LEFT) {
                newX = viewX - dx;
            } else {
                newX = viewX + dx;
            }

            view.setX(newX);
            traveledDistance += dx;
        }

        private void updateRotation(View view, float dx, SwipeDirection direction) {
            final int halfWidth = view.getWidth() / 2;

            if (distanceToTravel == INITIAL_DISTANCE_TO_TRAVEL) {
                // Initial state - Distance equal to half of the width is needed
                // to remove the element.
                distanceToTravel = halfWidth;
            }

            endRotation = direction.getEndRotation();

            // The user changed the direction of his swiping. Start rotating to the other direction
            // starting from the last rotation (the last one before the direction change).
            if (direction != lastDirection) {
                startRotation = view.getRotation();
                distanceToTravel = halfWidth + Math.abs(view.getX());

                traveledDistance = 0;
            }

            // Calculate how much to rotate, depending on how much the finger has moved.
            final float degreesToRotate = Math.abs(startRotation - endRotation);
            final float movedPercentage = calculateMovedPercentage();
            final float rotateDegrees = (movedPercentage / 100) * degreesToRotate;

            // Keep in mind that we calculate how much to rotate by using the whole distance,
            // that has to be traveled. This is why the new rotation is calculated by using
            // startRotation.
            final float newRotation;
            if (direction == SwipeDirection.LEFT) {
                newRotation = startRotation - rotateDegrees;
            } else {
                newRotation = startRotation + rotateDegrees;
            }

            view.setRotation(newRotation);
        }

        private SwipeDirection resolveDirection(MotionEvent startEvent, MotionEvent endEvent) {
            return endEvent.getX() - startEvent.getX() < 0 ? SwipeDirection.LEFT : SwipeDirection.RIGHT;
        }

        private float calculateMovedPercentage() {
            if (distanceToTravel == 0.0f) {
                return 0.0f;
            }

            float percentage;
            if (traveledDistance > distanceToTravel) {
                percentage = 100;
            } else {
                percentage = Utils.valueXPercentageOfMax(traveledDistance, distanceToTravel);
            }

            return percentage;
        }

        public void setSwipeListener(SwipeListener swipeListener) {
            this.swipeListener = swipeListener;
        }

        public void setSwipeUpdateListener(SwipeUpdateListener swipeUpdateListener) {
            this.swipeUpdateListener = swipeUpdateListener;
        }
    }

    private class TouchHandler implements OnTouchListener {

        private final GestureHandler gestureHandler;
        private final GestureDetectorCompat gestureDetector;

        private MotionEvent firstDownEvent;

        private boolean hasMoved = false;

        TouchHandler(View swipeableView) {
            gestureHandler = new GestureHandler(swipeableView);
            gestureDetector = new GestureDetectorCompat(swipeableView.getContext(), gestureHandler);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    firstDownEvent = MotionEvent.obtain(event);
                    return gestureDetector.onTouchEvent(event);
                case MotionEvent.ACTION_MOVE:
                    gestureDetector.onTouchEvent(event);

                    hasMoved = true;

                    return true;
                case MotionEvent.ACTION_UP:
                    boolean handled = gestureDetector.onTouchEvent(event);

                    if (hasMoved && !handled) {
                        gestureHandler.onUp(firstDownEvent, event);
                    }

                    hasMoved = false;

                    break;
                default:
                    return gestureDetector.onTouchEvent(event);
            }

            return false;
        }

        void setSwipeListener(SwipeListener swipeListener) {
            gestureHandler.setSwipeListener(swipeListener);
        }

        void setSwipeUpdateListener(SwipeUpdateListener swipeUpdateListener) {
            gestureHandler.setSwipeUpdateListener(swipeUpdateListener);
        }
    }

}
