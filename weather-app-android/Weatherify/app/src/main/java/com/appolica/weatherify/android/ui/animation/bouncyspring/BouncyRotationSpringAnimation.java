package com.appolica.weatherify.android.ui.animation.bouncyspring;

import com.annimon.stream.Stream;
import com.appolica.weatherify.android.utils.Utils;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.List;


public class BouncyRotationSpringAnimation {

    private static final String TAG = "BouncyRotationSpring";

    private final SpringSystem springSystem;
    private short direction = -1;
    private double startRotation;
    private double dRotation;

    private final double minDegrees;
    private final double maxDegrees;

    private ActivateListener activateListener;
    private UpdateListener updateListener;
    private RestListener restListener;

    private List<OnRotationUpdateListener> updateListeners = new ArrayList<>();
    private List<StartListener> startListeners = new ArrayList<>();
    private List<StopListener> stopListeners = new ArrayList<>();
    private List<RepeatListener> repeatListeners = new ArrayList<>();

    private String springId;

    public static BouncyRotationSpringAnimation instance(SpringSystem springSystem,
                                                         double initRotation,
                                                         double minDegrees,
                                                         double maxDegrees,
                                                         OnRotationUpdateListener updateListener) {
        return new BouncyRotationSpringAnimation(springSystem, initRotation, minDegrees, maxDegrees, updateListener);
    }

    public BouncyRotationSpringAnimation(
            SpringSystem springSystem,
            double initRotation,
            double minDegrees,
            double maxDegrees,
            OnRotationUpdateListener updateListener) {

        this(springSystem, initRotation, minDegrees, maxDegrees);

        updateListeners.add(updateListener);
    }

    public BouncyRotationSpringAnimation(
            SpringSystem springSystem,
            double initRotation,
            double minDegrees,
            double maxDegrees) {

        this.startRotation = initRotation;
        this.springSystem = springSystem;
        this.minDegrees = minDegrees;
        this.maxDegrees = maxDegrees;
    }

    public void start() {
        if (springId != null) {
            stop();
        }

        final Spring spring = springSystem.createSpring();
        springId = spring.getId();

        activateListener = new ActivateListener();
        updateListener = new UpdateListener();
        restListener = new RestListener();

        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(30, 3));

        spring.addListener(activateListener);
        spring.addListener(updateListener);
        spring.addListener(restListener);

        spring.setEndValue(1);
    }

    public void stop() {
        if (springId != null) {
            final Spring spring = springSystem.getSpringById(springId);

            spring.removeListener(restListener);
            spring.addListener(new SimpleSpringListener() {
                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    spring.destroy();
                    Stream.of(stopListeners)
                            .forEach(listener -> listener.onStop(BouncyRotationSpringAnimation.this));
                }
            });
        }
    }

    private class ActivateListener extends SimpleSpringListener {
        @Override
        public void onSpringActivate(Spring spring) {
            super.onSpringActivate(spring);
            final double endRotation = direction * Utils.randDouble(minDegrees, maxDegrees);

            dRotation = Math.abs(startRotation) + Math.abs(endRotation);
            direction *= -1;

            Stream.of(startListeners)
                    .forEach(listener -> listener.onStart(BouncyRotationSpringAnimation.this));
        }
    }

    private class UpdateListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            super.onSpringUpdate(spring);
            final double newRotation = startRotation + direction * spring.getCurrentValue() * dRotation;
            Stream.of(updateListeners).forEach(listener -> listener.onRotationUpdate((float) newRotation));
        }
    }

    private class RestListener extends SimpleSpringListener {
        @Override
        public void onSpringAtRest(Spring spring) {
            super.onSpringAtRest(spring);
            startRotation = startRotation + direction * dRotation;

            Stream.of(repeatListeners)
                    .forEach(listener -> listener.onRepeat(BouncyRotationSpringAnimation.this));

            spring.setCurrentValue(0);
            spring.setEndValue(1);
        }
    }

    public void addUpdateListener(OnRotationUpdateListener listener) {
        updateListeners.add(listener);
    }

    public void addListener(AnimationListener listener) {
        startListeners.add(listener);
        stopListeners.add(listener);
        repeatListeners.add(listener);
    }

    public void addStartListener(StartListener listener) {
        startListeners.add(listener);
    }

    public void addStopListener(StopListener listener) {
        stopListeners.add(listener);
    }

    public void addRepeatListener(RepeatListener listener) {
        repeatListeners.add(listener);
    }

    public interface OnRotationUpdateListener {
        void onRotationUpdate(float rotation);
    }

    public interface StartListener {
        void onStart(BouncyRotationSpringAnimation animation);
    }

    public interface StopListener {
        void onStop(BouncyRotationSpringAnimation animation);
    }

    public interface RepeatListener {
        void onRepeat(BouncyRotationSpringAnimation animation);
    }

    public interface AnimationListener extends StartListener, StopListener, RepeatListener {
    }

}
