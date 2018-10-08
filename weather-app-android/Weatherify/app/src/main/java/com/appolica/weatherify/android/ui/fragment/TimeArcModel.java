package com.appolica.weatherify.android.ui.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.databinding.ObservableInt;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.model.Forecast;
import com.appolica.weatherify.android.model.ForecastDataPoint;
import com.appolica.weatherify.android.ui.binding.ObservableString;
import com.appolica.weatherify.android.ui.view.layerimageview.LayeredImageView;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.DrawerFactory;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.LayerDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.BmpLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.Gravity;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.complex.PathBmpLayer;
import com.appolica.weatherify.android.ui.view.scrollview.ScrollViewShowDetector;
import com.appolica.weatherify.android.utils.TimeUtils;

import java.util.List;

public class TimeArcModel {
    private static final String TAG = "TimeArcModel";

    private ForecastTime forecastTime;
    private ObservableString leftLabel = new ObservableString();
    private ObservableString rightLabel = new ObservableString();
    private ObservableString leftTime = new ObservableString();
    private ObservableString rightTime = new ObservableString();
    private ObservableString time = new ObservableString();
    private ObservableInt timeColor = new ObservableInt();

    private int startOrbitColor;
    private int endOrbitColor;

    private int orbitingResId;
    private int archGradientResId;
    private LayerDrawer arcLayer;

    private TimeArcAnimations timeArcAnimations;

    private List<ForecastDataPoint> dailyForecast;
    private boolean arcAnimationRun;

    public static TimeArcModel instance(Context context) {
        return new TimeArcModel(context);
    }

    public TimeArcModel(Context context) {
        final BmpLayer bmpLayer = BmpLayer.Builder.instance()
                .bitmap(context, R.drawable.arch_no_gradient)
                .shouldDraw(true)
                .gravity(Gravity.GRAVITY_NONE)
                .build();

        arcLayer = DrawerFactory.createFor(bmpLayer);
        timeArcAnimations = new TimeArcAnimations();
    }

    public void update(ForecastTime forecastTime, Context context, Forecast forecast) {

        dailyForecast = forecast.getDaily().getData();
        this.forecastTime = forecastTime;
        this.time.set(forecastTime.getTimeString(context));

        final ForecastDataPoint todayForecast = dailyForecast.get(0);
        final ForecastDataPoint tomorrowForecast = dailyForecast.get(1);
        final String timeZone = forecastTime.getTimeZone();

        if (forecastTime.isBetween(todayForecast.getSunriseTime(), todayForecast.getSunsetTime())) {
            orbitingResId = R.drawable.sunrise;
            leftLabel.set(context, R.string.sunrise);
            rightLabel.set(context, R.string.sunset);
            leftTime.set(TimeUtils.getTimeString(context, todayForecast.getSunriseTime(), timeZone));
            rightTime.set(TimeUtils.getTimeString(context, todayForecast.getSunsetTime(), timeZone));
            archGradientResId = R.drawable.arch_gradient;
            updateTextColor(context, todayForecast, true);
        } else {
            orbitingResId = R.drawable.sundown;
            leftLabel.set(context, R.string.sunset);
            rightLabel.set(context, R.string.sunrise);
            leftTime.set(TimeUtils.getTimeString(context, todayForecast.getSunsetTime(), timeZone));
            rightTime.set(TimeUtils.getTimeString(context, tomorrowForecast.getSunriseTime(), timeZone));
            archGradientResId = R.drawable.arch_night_gradient;
            updateTextColor(context, todayForecast, false);
        }

        startOrbitColor = getStartOrbitColor(context, todayForecast);
        endOrbitColor = getEndOrbitColor(context, todayForecast);
    }

    private void updateTextColor(Context context, ForecastDataPoint forecast, boolean isDay) {
        final long percentage = forecastTime.getPercentageOfDayNight(forecast.getForecastDataBlock().getData());

        final float fraction = percentage / 100f;

        final int color;
        if (isDay) {
            final int fromColor = getStartOrbitColor(context, forecast);
            final int toColor = getEndOrbitColor(context, forecast);

            final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
            color = (int) argbEvaluator.evaluate(fraction, fromColor, toColor);
        } else {
            color = getStartOrbitColor(context, forecast);
        }

        timeColor.set(color);
    }

    public int getStartOrbitColor(Context context, ForecastDataPoint forecastDataPoint) {
        final int fromColorId;
        if (forecastTime.isBetween(forecastDataPoint.getSunriseTime(), forecastDataPoint.getSunsetTime())) {
            fromColorId = R.color.colorSunrise;
        } else {
            fromColorId = R.color.colorMoonrise;
        }

        return ContextCompat.getColor(context, fromColorId);
    }

    public int getEndOrbitColor(Context context, ForecastDataPoint forecastDataPoint) {
        final int toColorId;
        if (forecastTime.isBetween(forecastDataPoint.getSunriseTime(), forecastDataPoint.getSunsetTime())) {
            toColorId = R.color.colorSundown;
        } else {
            toColorId = R.color.colorMoondown;
        }

        return ContextCompat.getColor(context, toColorId);
    }

    public void arcScrolledIn(ScrollViewShowDetector.ObservedGroup group) {
        if (arcAnimationRun || dailyForecast == null) {
            return;
        }

        arcAnimationRun = true;

        final ImageView orbitingView = (ImageView) group.getViewById(R.id.orbitingView);
        final LayeredImageView arcView = (LayeredImageView) group.getViewById(R.id.imageViewTimeArc);
        final TextView timeView = (TextView) group.getViewById(R.id.textViewTime);

        ensureArcLayers(arcView);

        final int percentage = forecastTime.getPercentageOfDayNight(dailyForecast);

        final Animator arcAnimation =
                timeArcAnimations.createArcAnimation(
                        orbitingView,
                        arcView,
                        timeView,
                        startOrbitColor,
                        endOrbitColor,
                        percentage);

        if (arcAnimation != null) {
            arcAnimation.start();
        }
    }

    private void ensureArcLayers(LayeredImageView timeArc) {
        final List<LayerDrawer> drawers = timeArc.getDrawers();

        if (drawers.size() > 1) {
            drawers.remove(1);
        }

        final PathBmpLayer pathBmpLayer =
                PathBmpLayer.Builder.instance()
                        .bmpLayer()
                        .bitmap(timeArc.getContext(), archGradientResId)
                        .end()
                        .pathLayer()
                        .end()
                        .build();

        drawers.add(1, DrawerFactory.createFor(pathBmpLayer));
        timeArc.notifyDrawersChanged();
    }

    public ObservableString getLeftLabel() {
        return leftLabel;
    }

    public void setLeftLabel(String leftLabel) {
        this.leftLabel.set(leftLabel);
    }

    public ObservableString getRightLabel() {
        return rightLabel;
    }

    public void setRightLabel(String rightLabel) {
        this.rightLabel.set(rightLabel);
    }

    public ObservableString getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(String leftTime) {
        this.leftTime.set(leftTime);
    }

    public ObservableString getRightTime() {
        return rightTime;
    }

    public void setRightTime(String rightTime) {
        this.rightTime.set(rightTime);
    }

    public ObservableString getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public int getOrbitingResId() {
        return orbitingResId;
    }

    public void setOrbitingResId(int orbitingResId) {
        this.orbitingResId = orbitingResId;
    }

    public LayerDrawer getArcLayer() {
        return arcLayer;
    }

    public void setArcLayer(LayerDrawer arcLayer) {
        this.arcLayer = arcLayer;
    }

    public ObservableInt getTimeColor() {
        return timeColor;
    }

    public void setTimeColor(ObservableInt timeColor) {
        this.timeColor = timeColor;
    }

    public static String getTAG() {
        return TAG;
    }

    public int getStartOrbitColor() {
        return startOrbitColor;
    }

    public void setStartOrbitColor(int startOrbitColor) {
        this.startOrbitColor = startOrbitColor;
    }

    public int getEndOrbitColor() {
        return endOrbitColor;
    }

    public void setEndOrbitColor(int endOrbitColor) {
        this.endOrbitColor = endOrbitColor;
    }

    public void setForecastTime(ForecastTime forecastTime) {
        this.forecastTime = forecastTime;
    }
}

