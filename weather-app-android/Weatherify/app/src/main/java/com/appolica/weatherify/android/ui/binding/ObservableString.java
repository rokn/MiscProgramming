package com.appolica.weatherify.android.ui.binding;

import android.content.Context;
import android.databinding.ObservableField;


public class ObservableString extends ObservableField<String> {

    public void set(Context context, int stringId) {
        set(context.getString(stringId));
    }
}
