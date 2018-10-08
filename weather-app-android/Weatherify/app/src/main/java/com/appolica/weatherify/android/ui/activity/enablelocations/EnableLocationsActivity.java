package com.appolica.weatherify.android.ui.activity.enablelocations;

import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.databinding.ActivityEnableLocationsBinding;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.delayedtask.DelayedQueueTask;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.location.AppLocationManager;
import com.appolica.weatherify.android.network.NetworkStateReceiver;
import com.appolica.weatherify.android.preferences.ads.AdsPreferences;
import com.appolica.weatherify.android.preferences.permissions.PermissionsPreferences;
import com.appolica.weatherify.android.ui.activity.MainActivity;
import com.appolica.weatherify.android.ui.activity.base.BaseActivity;
import com.appolica.weatherify.android.ui.activity.favourites.FavouritesActivity;
import com.appolica.weatherify.android.ui.activity.favourites.FavouritesActivityWithAnimation;
import com.appolica.weatherify.android.utils.ProvidersUtils;
import com.appolica.weatherify.android.utils.Utils;
import com.google.android.gms.ads.AdListener;

import javax.inject.Inject;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.RuntimePermissions;

import static com.appolica.weatherify.android.ui.activity.settings.SettingsActivity.PRODUCT_ID;

@RuntimePermissions
public class EnableLocationsActivity extends BaseActivity
        implements BillingProcessor.IBillingHandler,
        EnableLocationsContract.Controller,
        AppLocationManager.AppLocationCallback, NetworkStateReceiver.NetworkStateReceiverListener {

    private static final String TAG = "EnableLocationsActivity";
    private static final long MINIMUM_COMPASS_ANIMATION_DURATION = 2000;

    @Inject
    protected DBManager dbManager;
    @Inject
    protected AdsPreferences adsPreferences;
    @Inject
    protected PermissionsPreferences permissionsPreferences;
    @Inject
    protected AppLocationManager locationManager;
    @Inject
    protected NetworkStateReceiver networkStateReceiver;

    private EnableLocationsViewModel viewModel;
    private ActivityEnableLocationsBinding binding;

    private DelayedQueueTask delayedQueueTask = new DelayedQueueTask(MINIMUM_COMPASS_ANIMATION_DURATION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enable_locations);

        final WeatherifyApplication application = (WeatherifyApplication) getApplication();
        final AppComponent appComponent = application.getAppComponent();

        EnableLocationsComponent component =
                appComponent.getEnableLocationsComponent();

        component.inject(this);

        if (!(ProvidersUtils.checkLocationPermission(this))) {
            dbManager.deleteCurrentLocationEntry(DBManager.DELETE_WITHOUT_EVENT);
        }

        if (!dbManager.isDBEmpty()) {
            continueToMainActivity();
            return;
        }

        billingProcessor =
                new BillingProcessor(
                        this,
                        getString(R.string.developer_console_license_key),
                        this);

        requestNewInterstitialAd(
                getString(R.string.ad_unit_id_favourites_interstitial)
        );

        viewModel = new EnableLocationsViewModel(this);

        checkLocationSettings();

        binding.setModel(viewModel);

        networkStateReceiver.addListener(this);
    }

    @SuppressWarnings("MissingPermission")
    private void checkLocationSettings() {
        if (ProvidersUtils.checkLocationPermission(this) &&
                ProvidersUtils.locationProvidersEnabled(this)) {
            viewModel.setHideSettingsButton(true);

            animateCompassArrow();
            if(ProvidersUtils.networkConnectionEstablished(this)) {
                delayedQueueTask.start();
                locationManager.getLocation(this);
            } else {
                Toast.makeText(this, R.string.enable_network_service, Toast.LENGTH_SHORT).show();
            }
        } else {
            viewModel.setHideSettingsButton(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationSettings();
        registerReceiver(networkStateReceiver,
                new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @SuppressWarnings("MissingPermission")
    @NeedsPermission("android.permission.ACCESS_FINE_LOCATION")
    void requestCurrentLocationForecast() {
        Log.d(TAG, "requestCurrentLocationForecast: ");

        checkLocationSettings();
    }

    @Override
    public void onLocationGet(Location location) {
        if (delayedQueueTask.isFinished()) {
            continueToMainActivity();
        } else {
            delayedQueueTask.addTask(this::continueToMainActivity);
        }
    }

    @Override
    public void onLocationGetError() {

    }

    private void animateCompassArrow() {
        Utils.runOnPreDraw(binding.imageViewArrow, view -> {
            viewModel.animateCompassArrow(binding.imageViewArrow);
            return true;
        });

    }

    void continueToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.WAIT_FOR_LOCATION, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        overridePendingTransition(R.anim.do_not_move, android.R.anim.fade_out);
        finish();
    }

    @OnNeverAskAgain("android.permission.ACCESS_FINE_LOCATION")
    void redirectToSettings() {
        if (!permissionsPreferences.shouldShowSettings()) {
            permissionsPreferences.setSettingsHiddenOnce(true);
            return;
        }

        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));

        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EnableLocationsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: "
                + billingProcessor.getPurchaseListingDetails(PRODUCT_ID)
                + " owned "
                + billingProcessor.isPurchased(PRODUCT_ID));

        adsPreferences.setAdsRemovedStatus(billingProcessor.isPurchased(PRODUCT_ID));
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onGoToFavourites() {
        Log.d(TAG, "onGoToFavouritesClick: ");

        if (adsPreferences.adsRemoved()) {
            openFavourites();
            finish();
        } else {
            openFavourites();
            getInterstitialAd().setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    finish();
                }
            });
            showInterstitialAd();

        }
    }

    protected void openFavourites() {
        int coords[] = new int[2];
        View button = binding.rippleLayout;
        button.getLocationOnScreen(coords);
        coords[0] += button.getWidth() / 2;
        Intent favouritesIntent = new Intent(this, FavouritesActivity.class);
        favouritesIntent.putExtra(FavouritesActivityWithAnimation.CENTER_X_EXTRA, coords[0]);
        favouritesIntent.putExtra(FavouritesActivityWithAnimation.CENTER_Y_EXTRA, coords[1]);

        startActivity(favouritesIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onGoToSettings() {
        if (!(ProvidersUtils.checkLocationPermission(this))) {
            EnableLocationsActivityPermissionsDispatcher.requestCurrentLocationForecastWithCheck(this);
        } else if (!ProvidersUtils.locationProvidersEnabled(this)){
            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(viewIntent);
        } else {
            checkLocationSettings();
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
        dbManager.releaseHelper();
    }

    @Override
    public void onNetworkAvailable() {
        checkLocationSettings();
    }

    @Override
    public void onNetworkUnavailable() {

    }
}
