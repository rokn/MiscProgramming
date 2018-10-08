package com.appolica.weatherify.android.ui.adapter;

import android.animation.Animator;
import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.appolica.weatherify.android.ui.view.layerimageview.LayeredImageView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by aleksandar on 25.08.16.
 */
public class BindingAdapters {

    @BindingAdapter("srcCompat")
    public static void setImageResourceCompat(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView view, final int iconId) {
        if (iconId == 0) {
            return;
        }
        view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), iconId));
    }

    @BindingAdapter("textChangedListener")
    public static void addTextChangedListener(EditText view, TextWatcher textWatcher) {
        view.addTextChangedListener(textWatcher);
    }

    @BindingAdapter("itemDecoration")
    public static void addItemDecoration(RecyclerView view, HorizontalDividerItemDecoration itemDecoration) {
        view.addItemDecoration(itemDecoration);
    }

    @BindingAdapter("postCreatedAnimator")
    public static void setAnimator(LayeredImageView imageView, AnimationProvider animationProvider) {
        if (animationProvider == null) {
            return;
        }

        imageView.post(() -> imageView.setAnimator(animationProvider.createAnimation(imageView)));
    }

    public interface AnimationProvider {
        Animator createAnimation(View view);
    }

}
