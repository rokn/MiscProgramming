package com.appolica.weatherify.android.dependencyinjection.graph.network;

import android.content.Context;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.network.ApiKeyInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by aleksandar on 25.08.16.
 */
@Module
public class NetworkModule {
    @Provides
    @Scopes.Application
    ApiKeyInterceptor provideApiKeyInterceptor() {
        return new ApiKeyInterceptor();
    }

    @Provides
    @Scopes.Application
    OkHttpClient provideOkHttpClient(ApiKeyInterceptor apiKeyInterceptor) {
        OkHttpClient httpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(apiKeyInterceptor)
                        .build();
        return httpClient;
    }

    @Provides
    @Scopes.Application
    ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Provides
    @Scopes.Application
    Retrofit provideRetrofit(OkHttpClient client, ObjectMapper mapper, Context context) {
        String baseUrl = context.getString(R.string.request_base_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .client(client)
                .build();
        return retrofit;
    }
}
