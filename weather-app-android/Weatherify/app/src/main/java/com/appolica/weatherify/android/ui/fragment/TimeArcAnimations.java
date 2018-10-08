package com.appolica.weatherify.android.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appolica.weatherify.android.ui.animation.SimpleAnimatorListener;
import com.appolica.weatherify.android.ui.view.layerimageview.LayeredImageView;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.LayerDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.complex.PathBmpDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.PathLayer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.complex.PathBmpLayer;
import com.appolica.weatherify.android.utils.Animations;
import com.appolica.weatherify.android.utils.Utils;

import java.util.List;

public class TimeArcAnimations {
    private static final int SECOND = 1000;
    private static final long ANIMATION_DURATION = 3 * SECOND;

    public Animator createArcAnimation(
            ImageView orbitingView,
            LayeredImageView timeArc,
            TextView viewTime,
            int startOrbitColor,
            int endOrbitColor,
            int percentage) {

        final long duration = (long) ((percentage / 100f) * ANIMATION_DURATION);
        final AnimatorSet animatorSet = new AnimatorSet();

        final ValueAnimator orbitAnimation = Animations.createOrbitAnimation(orbitingView, timeArc, percentage);

        final ValueAnimator.AnimatorUpdateListener colorEvaluator =
                Animations.getColorEvaluatorToPercentage(
                        orbitingView.getDrawable(),
                        startOrbitColor,
                        endOrbitColor,
                        percentage);

        orbitAnimation.addUpdateListener(colorEvaluator);

        final Animator gradientAnimation = createGradientAnimation(timeArc, percentage);

        animatorSet.addListener(SimpleAnimatorListener.forEnd(animation -> {
            viewTime.setVisibility(View.VISIBLE);

            Utils.runOnPreDraw(viewTime,
                    (view) -> {
                        final int dWidth = view.getWidth() - orbitingView.getWidth();

                        view.setX(orbitingView.getX() - dWidth / 2);
                        view.setY(orbitingView.getY() - view.getHeight());

                        return false;
                    });
        }));

        animatorSet.play(orbitAnimation).with(gradientAnimation);

        animatorSet.setInterpolator(Animations.getEaseInOutInterpolator());
        animatorSet.setDuration(duration);

        return animatorSet;
    }

    public static Animator createGradientAnimation(LayeredImageView timeArc, int percentage) {
        final List<LayerDrawer> drawers = timeArc.getDrawers();

        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, percentage);
        final PathBmpDrawer layerDrawer = (PathBmpDrawer) drawers.get(1);
        final PathBmpLayer gradientLayer = layerDrawer.getLayer();
        final RectF oval = new RectF();

        valueAnimator.addListener(
                SimpleAnimatorListener.forStart(animator -> {
                    final PathLayer pathLayer = gradientLayer.getPathLayer();

                    timeArc.setLayersDisabled(false);
                    gradientLayer.setShouldDraw(true);
                    gradientLayer.getBmpLayer().setShouldDraw(true);

                    if (pathLayer.getPath() == null) {
                        pathLayer.setPath(new Path());
                    }
                    pathLayer.setShouldClipPath(true);
                }));

        valueAnimator.addUpdateListener(animation -> {
            oval.left = 0;
            oval.top = 0;
            oval.right = timeArc.getWidth();
            oval.bottom = 2 * timeArc.getHeight();

            final int animatedValue = (int) animation.getAnimatedValue();
            final float sweepAngle = (animatedValue / 100f) * 180f;

            gradientLayer
                    .getPathLayer()
                    .getPathConstructor()
                    .reset()
                    .moveTo(timeArc.getWidth() / 2, timeArc.getHeight())
                    .lineTo(0, timeArc.getHeight())
                    .arcTo(oval, 180f, sweepAngle, false)
                    .close();

            timeArc.invalidate();
        });

        return valueAnimator;
    }

}
