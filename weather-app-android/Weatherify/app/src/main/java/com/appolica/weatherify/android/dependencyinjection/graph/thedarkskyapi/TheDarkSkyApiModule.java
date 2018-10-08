package com.appolica.weatherify.android.dependencyinjection.graph.thedarkskyapi;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.model.Forecast;

import dagger.Module;
import dagger.Provides;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by aleksandar
 */
@Module
public class TheDarkSkyApiModule {
    @Provides
    @Scopes.Application
    TheDarkSkyForecastApi provideTheDarkSkyForecastApi(Retrofit retrofit) {
        return retrofit.create(TheDarkSkyForecastApi.class);
    }

    public interface TheDarkSkyForecastApi {
        String EXCLUDE_QUERY = "exclude";
        String UNITS_QUERY = "units";
        String API_KEY_SEGMENT = "apiKey";


        @GET(API_KEY_SEGMENT + "/{location}?")
        Call<Forecast> getForecast(@Path("location") String location,
                                   @Query(UNITS_QUERY) String units,
                                   @Query(EXCLUDE_QUERY) String flags);
    }
}
