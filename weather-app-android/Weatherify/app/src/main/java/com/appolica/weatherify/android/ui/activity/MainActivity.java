package com.appolica.weatherify.android.ui.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.databinding.ActivityMainBinding;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.event.DBFavoritesUpdatedEvent;
import com.appolica.weatherify.android.event.DBForecastAddedEvent;
import com.appolica.weatherify.android.event.DBForecastDeletedEvent;
import com.appolica.weatherify.android.event.PurchaseStatusEvent;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.appolica.weatherify.android.network.NetworkStateReceiver;
import com.appolica.weatherify.android.preferences.ads.AdsPreferences;
import com.appolica.weatherify.android.service.LocationManagerService;
import com.appolica.weatherify.android.service.LocationProvidersChangedReceiver;
import com.appolica.weatherify.android.ui.activity.base.BaseActivity;
import com.appolica.weatherify.android.ui.activity.enablelocations.EnableLocationsActivity;
import com.appolica.weatherify.android.ui.activity.settings.RefreshAnimationListener;
import com.appolica.weatherify.android.ui.adapter.MainPagerAdapter;
import com.appolica.weatherify.android.ui.fragment.CanScrollListener;
import com.appolica.weatherify.android.ui.refresh.RefreshHeader;
import com.appolica.weatherify.android.ui.viewpager.PageSelectedNotifyAdapter;
import com.appolica.weatherify.android.ui.viewpager.ads.AdsShowingPageChangeListener;
import com.appolica.weatherify.android.utils.ProvidersUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MainActivity extends BaseActivity implements AdsShowingPageChangeListener.OnShouldShowAd,
        CanScrollListener,
        RefreshAnimationListener,
        NetworkStateReceiver.NetworkStateReceiverListener{
    public static final String WAIT_FOR_LOCATION = "WAIT_FOR_LOCATION";
    public static final String GOTO_LOCATION_ID = "GOTO_LOCATION_ID";

    private static final String TAG = "MainActivity";
    private static final int DURATION_TO_CLOSE_REFRESH_HEADER = 500;
    private static final long MINIMUM_INTERVAL_BETWEEN_REFRESHES_MS = 1000 * 2;
    private static final String BUNDLE_WAIT_FOR_LOCATION = "WAIT_FOR_LOCATION";

    //todo set accurate interval when done testing
    private static final int MIN_MEMORY = 45;
    private static final int MEMORY_PER_FRAGMENT = 5;
    private static final int MIN_FRAGMENTS_OFFSET = 1;
    private static final int MAX_FRAGMENTS_OFFSET = 6;

    @Inject
    protected DBManager dbManager;
    @Inject
    protected AdsPreferences adsPreferences;
    @Inject
    protected NetworkStateReceiver networkStateReceiver;

    private ActivityMainBinding binding;
    private MainPagerAdapter pagerAdapter;

    private AdsShowingPageChangeListener pageChangeListener;
    private RefreshHeader refreshHeader;
    private boolean canRefresh = true;
    private long lastRefreshTime;
    private LocationProvidersChangedReceiver providersChangedReceiver;
    private PageSelectedNotifyAdapter pageNotifier;

    private boolean waitForLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final WeatherifyApplication application = (WeatherifyApplication) getApplication();
        final AppComponent appComponent = application.getAppComponent();

        final MainComponent component = appComponent.getMainComponent();

        component.inject(this);

        waitForLocation = getIntent().getBooleanExtra(WAIT_FOR_LOCATION, false);

        if (!ProvidersUtils.hasAllSettingsEnabled(this)) {

            dbManager.deleteCurrentLocationEntry(DBManager.DELETE_WITHOUT_EVENT);

            if (dbManager.isDBEmpty() && !waitForLocation) {
                goToEnableLocations();
                return;
            }
        } else if (!waitForLocation) {
            waitForLocation = true;
            final Intent serviceIntent =
                    LocationManagerService.createIntent(this, LocationManagerService.UPDATE_CURRENT_LOCATION);
            startService(serviceIntent);
        }

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        pageChangeListener = new AdsShowingPageChangeListener(this);
        pageNotifier = new PageSelectedNotifyAdapter(pagerAdapter);

        binding.detailedForecastViewPager.addOnPageChangeListener(pageNotifier);
        binding.setAdapter(pagerAdapter);
        pagerAdapter.setCanScrollListener(this);
        binding.detailedForecastViewPager.setOffscreenPageLimit(getFragmentOffsetLimit());

        requestNewInterstitialAd(getString(R.string.ad_unit_id_main_interstitial));

        updatePageChangeListener();

        networkStateReceiver.addListener(this);

        refreshHeader = (RefreshHeader) LayoutInflater.from(this).inflate(R.layout.layout_refresh_header, (ViewGroup) binding.getRoot(), false);

        refreshHeader.setRefreshAnimationListener(this);
        refreshHeader.setPtrFrameLayout(binding.pullToRefreshLayout);

        binding.pullToRefreshLayout.addPtrUIHandler(refreshHeader);
        binding.pullToRefreshLayout.setHeaderView(refreshHeader);
        binding.pullToRefreshLayout.setDurationToCloseHeader(DURATION_TO_CLOSE_REFRESH_HEADER);
        binding.pullToRefreshLayout.disableWhenHorizontalMove(true);
        binding.pullToRefreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return canRefresh &&
                        System.currentTimeMillis() - lastRefreshTime > MINIMUM_INTERVAL_BETWEEN_REFRESHES_MS;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                application.updateForecastData();

                Handler handler = new Handler();

                handler.postDelayed(() -> refreshHeader.endRefresh(), 2000);
            }
        });

        providersChangedReceiver = new LocationProvidersChangedReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePagerData();
        EventBus.getDefault().register(this);
        registerReceiver(providersChangedReceiver, LocationProvidersChangedReceiver.getFilter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver,
                new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        if (dbManager.isDBEmpty() && !waitForLocation) {
            goToEnableLocations();
        }

        String goToLocationId = getIntent().getStringExtra(GOTO_LOCATION_ID);
        if (goToLocationId != null) {
            int position = pagerAdapter.getForecastLocationIds().indexOf(goToLocationId);

            if (position >= 0) {
                goToPosition(position);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        if (providersChangedReceiver != null) {
            unregisterReceiver(providersChangedReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final Intent serviceIntent =
                LocationManagerService.createIntent(
                        this,
                        LocationManagerService.UPDATE_CURRENT_LOCATION
                                | LocationManagerService.UPDATE_FAV_LOCATIONS);

        stopService(serviceIntent);

        if (dbManager != null) {
            dbManager.releaseHelper();
        }
    }

    private void goToEnableLocations() {
        Intent intent = new Intent(this, EnableLocationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, R.anim.do_not_move);
    }

    @Override
    public void showAd() {
        showInterstitialAd();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    private void updatePageChangeListener() {
        binding.detailedForecastViewPager.removeOnPageChangeListener(pageChangeListener);

        if (!adsPreferences.adsRemoved()) {
            binding.detailedForecastViewPager.addOnPageChangeListener(pageChangeListener);
        }
    }

    private void updatePagerData() {
        ForecastLocation currentForecastLocation = dbManager.getCurrentForecastLocation();

        pagerAdapter.setForecastLocationIds(getLocationIds(currentForecastLocation), currentForecastLocation != null);
        pageNotifier.setChildCount(pagerAdapter.getCount());
    }

    private List<String> getLocationIds(ForecastLocation currentForecastLocation) {
        final List<String> locationIds = new ArrayList<>();

        if (currentForecastLocation != null) {
            locationIds.add(currentForecastLocation.getGoogleLocationId());
        }

        locationIds.addAll(dbManager.getFavouriteForecastsLocationIds());

        return locationIds;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBForecastAdded(DBForecastAddedEvent event) {
        Log.d(TAG, "onDBItemsCountChange: ");
        pagerAdapter.addForecastLocationId(event.getLocationId(), event.isCurrentLocation());
        pageNotifier.setChildCount(pagerAdapter.getCount());

        final int position;
        if (event.isCurrentLocation()) {
            position = 0;
        } else {
            position = pagerAdapter.getCount() - 1;
        }

        goToPosition(position);
        waitForLocation = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBForecastDeleted(DBForecastDeletedEvent event) {
        Log.d(TAG, "onDBForecastDeleted: ");

        if (pagerAdapter.getForecastLocationIds() == null
                || (pagerAdapter.getForecastLocationIds().size() <= 1 && event.isCurrentLocation())) {
            goToEnableLocations();
            return;
        }

        updatePagerData();

        int currentItemPosition = getCurrentItemPosition();
        if (event.getDeletedPosition() < currentItemPosition) {
            binding.detailedForecastViewPager.setCurrentItem(currentItemPosition);
        }

    }

    private int getCurrentItemPosition() {
        return binding.detailedForecastViewPager.getCurrentItem();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBFavoritesUpdated(DBFavoritesUpdatedEvent event) {
        Log.d(TAG, "onDBFavoritesUpdated: ");

        if (dbManager.isDBEmpty()) {
            goToEnableLocations();
            return;
        }

        // Save the current item so we can find its position later
        final int currentPosition = getCurrentItemPosition();
        final String currentLocationId = pagerAdapter.getForecastLocationIds().get(currentPosition);

        updatePagerData();

        // Find the index of our saved item
        final List<String> data = pagerAdapter.getForecastLocationIds();
        final int newPosition = data.indexOf(currentLocationId);

        if (newPosition != currentPosition && newPosition > -1) {
            binding.detailedForecastViewPager.setCurrentItem(newPosition, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPurchaseStatusChange(PurchaseStatusEvent event) {
        updatePageChangeListener();
    }

    @Override
    public void onNetworkAvailable() {
        // TODO: 26.05.17
//        updateForecasts();
    }

    @Override
    public void onNetworkUnavailable() {

    }

    private int getFragmentOffsetLimit() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        if (memoryClass < MIN_MEMORY) {
            return MIN_FRAGMENTS_OFFSET;
        }

        int fragmentCount = (((memoryClass - MIN_MEMORY) / MEMORY_PER_FRAGMENT) * 2);

        fragmentCount = Math.max(MIN_FRAGMENTS_OFFSET, Math.min(fragmentCount, MAX_FRAGMENTS_OFFSET)); // Clamp

        return fragmentCount;
    }

    private void goToPosition(int position) {
        binding.detailedForecastViewPager.setCurrentItem(position, true);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDBUpdateEvent(DBUpdateEntryEvent event) {
//        Log.d(TAG, "onDBUpdateEvent: ");
//
//        updatedForecasts++;
//
//        if (updatedForecasts == favouriteForecasts) {
//            Log.d(TAG, "onDBUpdateEvent: reset refresh count");
//            updatedForecasts = 0;
//            refreshHeader.endRefresh();
//        }
//    }

    @Override
    public void onPerformedScroll(boolean canScrollUp) {
        if (this.canRefresh == canScrollUp) {
            this.canRefresh = !canScrollUp;
        }
    }

    @Override
    public void onRefreshAnimationFinished() {
        lastRefreshTime = System.currentTimeMillis();
    }
}
