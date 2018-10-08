package com.appolica.weatherify.android.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.appolica.weatherify.android.R;

/**
 * Created by Bogomil Kolarov on 26.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class FavouritesBindingAdapters {

    @BindingAdapter({"temperatureColor"})
    public static void setTemperature(TextView textView, double temperature) {

        final Context context = textView.getContext();
        final Resources resources = context.getResources();

        final int colorId;
        if (temperature <= resources.getInteger(R.integer.lowTemperature)) {

            colorId = R.color.colorFavouritesLowTemperature;

        } else if (temperature >= resources.getInteger(R.integer.highTemperature)) {

            colorId = R.color.colorFavouritesHighTemperature;

        } else {

            colorId = R.color.colorFavouritesMidTemperature;

        }

        final int color = ContextCompat.getColor(context, colorId);

        textView.setTextColor(color);
    }

}
