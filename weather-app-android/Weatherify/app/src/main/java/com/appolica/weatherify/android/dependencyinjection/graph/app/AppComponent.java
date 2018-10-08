package com.appolica.weatherify.android.dependencyinjection.graph.app;

import android.content.Context;

import com.appolica.weatherify.android.dependencyinjection.graph.db.DBModule;
import com.appolica.weatherify.android.dependencyinjection.graph.db.DbComponent;
import com.appolica.weatherify.android.dependencyinjection.graph.location.LocationComponent;
import com.appolica.weatherify.android.dependencyinjection.graph.location.LocationModule;
import com.appolica.weatherify.android.dependencyinjection.graph.network.NetworkComponent;
import com.appolica.weatherify.android.dependencyinjection.graph.network.NetworkModule;
import com.appolica.weatherify.android.dependencyinjection.graph.network.NetworkReceiverComponent;
import com.appolica.weatherify.android.dependencyinjection.graph.network.NetworkReceiverModule;
import com.appolica.weatherify.android.dependencyinjection.graph.thedarkskyapi.TheDarkSkyApiComponent;
import com.appolica.weatherify.android.dependencyinjection.graph.thedarkskyapi.TheDarkSkyApiModule;
import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.preferences.PreferencesComponent;
import com.appolica.weatherify.android.preferences.PreferencesModule;
import com.appolica.weatherify.android.service.ForecastServiceComponent;
import com.appolica.weatherify.android.service.LocationManagerServiceComponent;
import com.appolica.weatherify.android.ui.activity.MainComponent;
import com.appolica.weatherify.android.ui.activity.addlocation.AddLocationComponent;
import com.appolica.weatherify.android.ui.activity.enablelocations.EnableLocationsComponent;
import com.appolica.weatherify.android.ui.activity.favourites.FavouritesComponent;
import com.appolica.weatherify.android.ui.activity.settings.SettingsComponent;
import com.appolica.weatherify.android.ui.fragment.DetailedForecastComponent;

import dagger.Component;

/**
 * Created by aleksandar
 */
@Scopes.Application
@Component(
        modules = {
                AppModule.class,
                DBModule.class,
                NetworkModule.class,
                TheDarkSkyApiModule.class,
                LocationModule.class,
                PreferencesModule.class,
                NetworkReceiverModule.class
        }
)
public interface AppComponent
        extends DbComponent,
        NetworkComponent,
        TheDarkSkyApiComponent,
        LocationComponent,
        PreferencesComponent,
        NetworkReceiverComponent {

    Context context();

    MainComponent getMainComponent();

    EnableLocationsComponent getEnableLocationsComponent();

    FavouritesComponent getFavouritesComponent();

    DetailedForecastComponent getDetailedForecastComponent();

    ForecastServiceComponent getForecastServiceComponent();

    LocationManagerServiceComponent getLocationManagerServiceComponent();

    AddLocationComponent getAddLocationComponent();

    SettingsComponent getSettingsComponent();
}
