package com.appolica.weatherify.android.ui.activity.favourites;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.databinding.ActivityFavouritesBinding;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.event.DBFavoritesUpdatedEvent;
import com.appolica.weatherify.android.event.DBForecastAddedEvent;
import com.appolica.weatherify.android.event.DBForecastDeletedEvent;
import com.appolica.weatherify.android.event.DBUpdateEntryEvent;
import com.appolica.weatherify.android.model.Forecast;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.appolica.weatherify.android.preferences.settings.SettingsPreferences;
import com.appolica.weatherify.android.ui.activity.MainActivity;
import com.appolica.weatherify.android.ui.activity.addlocation.AddLocationActivity;
import com.appolica.weatherify.android.ui.activity.base.BaseActivity;
import com.appolica.weatherify.android.ui.activity.enablelocations.EnableLocationsActivity;
import com.appolica.weatherify.android.ui.activity.favourites.recyclerview.FavouriteTouchHelper;
import com.appolica.weatherify.android.ui.activity.favourites.recyclerview.FavouritesRVAdapter;
import com.appolica.weatherify.android.ui.activity.settings.SettingsActivity;
import com.appolica.weatherify.android.utils.ProvidersUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class FavouritesActivity extends BaseActivity
        implements FavouritesRVAdapter.OnSwipeRemovedItemListener,
        FavouritesClickListener,
        FavouritesRVAdapter.OnReorderedListener {

    public static final int CURRENT_LOCATION_POSITION = 0;
    private static final String TAG = "FavouritesActivity";

    @Inject
    protected DBManager dbManager;

    protected ActivityFavouritesBinding binding;
    protected FavouritesRVAdapter favouritesRVAdapter;

    private List<ForecastLocation> deletedFavourites = new ArrayList<>();
    private Set<ForecastLocation> changedFavourites = new ArraySet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favourites);

        final WeatherifyApplication application = (WeatherifyApplication) getApplication();
        final AppComponent appComponent = application.getAppComponent();
        final FavouritesComponent favouritesComponent = appComponent.getFavouritesComponent();

        favouritesComponent.inject(this);

        final SettingsPreferences settingsPreferences = appComponent.getSettingsPreferences();

        setupRecyclerView(settingsPreferences);

        updateListData();

        favouritesRVAdapter.setSwipeRemovedItemListener(this);

        binding.setHasFavorites(!favouritesRVAdapter.getData().isEmpty());
        binding.setClickListener(this);
    }

    private void setupRecyclerView(SettingsPreferences settingsPreferences) {
        favouritesRVAdapter = new FavouritesRVAdapter(settingsPreferences.getPreferredUnits());

        final FavouriteTouchHelper favouriteTouchHelper = new FavouriteTouchHelper();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(favouriteTouchHelper);

        favouriteTouchHelper.addItemMovedCallback(favouritesRVAdapter);
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewFavSuggestions);

        binding.recyclerViewFavSuggestions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewFavSuggestions.setAdapter(favouritesRVAdapter);

        favouritesRVAdapter.setOnReorderedListener(this);
    }

    protected void updateListData() {
        final List<ForecastLocation> data = getFavouriteItems();

        favouritesRVAdapter.setData(data);
    }

    protected List<ForecastLocation> getFavouriteItems() {
        final List<ForecastLocation> forecastLocations = new ArrayList<>();
        final ForecastLocation currentForecastLocation = dbManager.getCurrentForecastLocation();

        if (currentForecastLocation != null) {
            forecastLocations.add(currentForecastLocation);
        }

        forecastLocations.addAll(dbManager.getFavoriteForecastLocations());

        return forecastLocations;
    }

    @Override
    public void onItemSwiped(ForecastLocation location) {
        favouritesRVAdapter.removeItem(location);
        changedFavourites.remove(location);
        binding.setHasFavorites(!favouritesRVAdapter.getData().isEmpty());

        deletedFavourites.add(location);
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        commitDBChanges();

        EventBus.getDefault().unregister(this);
    }

    protected void commitDBChanges() {
        boolean changesHappened = false;
        if (!changedFavourites.isEmpty()) {
            dbManager.updateLocationsOrders(changedFavourites, false);
            changedFavourites.clear();

            changesHappened = true;
        }

        if (!deletedFavourites.isEmpty()) {
            dbManager.deleteAll(deletedFavourites, false);
            deletedFavourites.clear();

            changesHappened = true;
        }

        if (changesHappened) {
            EventBus.getDefault().post(new DBFavoritesUpdatedEvent());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.releaseHelper();
    }

    @Override
    public void onBackPressed() {
        onCloseClick();
    }

    @Override
    public void onCloseClick() {
        commitDBChanges();
        if (getDbManager().isOpen() && getDbManager().isDBEmpty()) {
            goToEnableLocations();
        } else {
            goToMainActivity();
        }
    }

    protected void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void goToEnableLocations() {
        Intent intent = new Intent(this, EnableLocationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);

        int imageSize = (int) this.getResources().getDimension(R.dimen.top_buttons_image_size);

        int centerX = (int) (view.getX() + imageSize / 2);
        int centerY = (int) (view.getY() + view.getPaddingTop() + imageSize / 2);

        intent.putExtra(SettingsActivity.CENTER_X_EXTRA, centerX);
        intent.putExtra(SettingsActivity.CENTER_Y_EXTRA, centerY);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, R.anim.do_not_move);
    }

    @Override
    public void onReordered(int fromPos) {
        Stream.of(favouritesRVAdapter.getData())
                .forEachIndexed(fromPos, 1, (index, location) -> changedFavourites.add(location));
    }

    @Override
    public void onTextFieldClick() {
        if (ProvidersUtils.networkConnectionEstablished(this)) {
            Intent intent = new Intent(this, AddLocationActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, R.anim.do_not_move);
        } else {
            //todo mind this
            Toast.makeText(this, getString(R.string.add_location_without_internet_toast), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBForecastAdded(DBForecastAddedEvent event) {
        if(event.isCurrentLocation()) {
            if(favouritesRVAdapter.hasCurrentFavouriteLocation()) {
                favouritesRVAdapter.updateItemAt(0, dbManager.getCurrentForecastLocation());
            } else {
                favouritesRVAdapter.addItemTop(dbManager.getCurrentForecastLocation(), true);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBForecastUpdated(DBUpdateEntryEvent event) {
        ForecastLocation location = dbManager.getForecastLocation(event.getForecastLocationId(), event.isCurrentLocation());

        if(location != null) {
            favouritesRVAdapter.updateItemAt(location.getOrderPosition(), location);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBForecastDeleted(DBForecastDeletedEvent event) {
        if(event.isCurrentLocation()) {
            favouritesRVAdapter.removeAt(CURRENT_LOCATION_POSITION);
        }

        updateListData();
    }

    protected FavouritesRVAdapter getFavouritesRVAdapter() {
        return favouritesRVAdapter;
    }

    protected DBManager getDbManager() {
        return dbManager;
    }
}

