package com.appolica.weatherify.android.network;

import android.content.Context;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.dependencyinjection.graph.thedarkskyapi.TheDarkSkyApiModule;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by aleksandar
 */
public class ApiKeyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Context context = WeatherifyApplication.getInstance();
        String apiKey = context.getString(R.string.dark_sky_forecast_api_key);
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        List<String> pathSegments = originalHttpUrl.pathSegments();
        int apiKeyParamIndex = pathSegments.indexOf(TheDarkSkyApiModule.TheDarkSkyForecastApi.API_KEY_SEGMENT);

        HttpUrl url = originalHttpUrl.newBuilder()
                .setPathSegment(apiKeyParamIndex, apiKey)
                .build();

        Request.Builder requestBuilder = original.newBuilder()
                .url(url);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
