package com.appolica.weatherify.android.ui.activity.settings;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.databinding.ActivitySettingsBinding;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.event.PurchaseStatusEvent;
import com.appolica.weatherify.android.preferences.ads.AdsPreferences;
import com.appolica.weatherify.android.preferences.settings.SettingsPreferences;
import com.appolica.weatherify.android.preferences.units.PreferredUnits;
import com.appolica.weatherify.android.preferences.units.SpeedUnit;
import com.appolica.weatherify.android.preferences.units.TemperatureUnit;
import com.appolica.weatherify.android.ui.activity.Revealable;
import com.appolica.weatherify.android.ui.activity.base.BaseActivity;
import com.appolica.weatherify.android.ui.animation.RevealAnimationBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity
        implements SettingsClickListener,
        WindSwitchAdapterListener.OnWindSwitchListener,
        TemperatureSwitchAdapterListener.OnTemperatureSelectedListener,
        Revealable,
        BillingProcessor.IBillingHandler {

    //// TODO: 02.12.16 change the id
    public static final String PRODUCT_ID = "test_id";
    public static final String CENTER_Y_EXTRA = "centerY";
    public static final String CENTER_X_EXTRA = "centerX";

    private static final String TAG = "SettingsActivity";

    @Inject
    protected SettingsPreferences settingsPreferences;
    @Inject
    protected AdsPreferences adsPreferences;

    private ActivitySettingsBinding binding;
    private View rootLayout;
    private int revealCenterX;
    private int revealCenterY;
    private PreferredUnits preferredUnits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        rootLayout = binding.activitySettings;

        performInjection();

        setupSwitches();

        billingProcessor =
                new BillingProcessor(
                        this,
                        getString(R.string.developer_console_license_key),
                        this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            rootLayout.setVisibility(View.INVISIBLE);

            revealCenterX = intent.getIntExtra(SettingsActivity.CENTER_X_EXTRA, 0);
            revealCenterY = intent.getIntExtra(SettingsActivity.CENTER_Y_EXTRA, 0);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    transitionAnimation(true);
                    rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    private void performInjection() {
        final AppComponent appComponent =
                ((WeatherifyApplication) getApplication()).getAppComponent();
        final SettingsComponent settingsComponent = appComponent.getSettingsComponent();

        settingsComponent.inject(this);
    }

    private void setupSwitches() {
        preferredUnits = new PreferredUnits();
        preferredUnits.updateFrom(settingsPreferences.getPreferredUnits());

        binding.setClickListener(this);

        final WindSwitchAdapterListener windSwitchListener =
                new WindSwitchAdapterListener(this, this);

        final TemperatureSwitchAdapterListener temperatureSwitchListener =
                new TemperatureSwitchAdapterListener(this, this);

        binding.setTemperatureLabels(new ArrayList<>(temperatureSwitchListener.getLabels()));
        binding.setWindLabels(new ArrayList<>(windSwitchListener.getLabels()));
        binding.setWindSwitchListener(windSwitchListener);
        binding.setTemperatureSwitchListener(temperatureSwitchListener);

        final int windPosition =
                windSwitchListener.positionOf(preferredUnits.getSpeedUnit());

        final int temperaturePosition =
                temperatureSwitchListener.positionOf(preferredUnits.getTemperatureUnit());

        binding.toggleSwitchWind.setCheckedTogglePosition(windPosition);
        binding.toggleSwitchTemperature.setCheckedTogglePosition(temperaturePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
    }

    @Override
    public void onBackPressed() {
        onCloseClick();
    }

    @Override
    public void onWindUnitSelected(SpeedUnit windUnit) {
        this.preferredUnits.setSpeedUnit(windUnit);
    }

    @Override
    public void onTemperatureUnitSelected(TemperatureUnit temperatureUnit) {
        this.preferredUnits.setTemperatureUnit(temperatureUnit);
    }

    @Override
    public void onCloseClick() {
        transitionAnimation(false);
        saveSettings();
    }

    private void saveSettings() {
        settingsPreferences.updatePreferredUnits(preferredUnits);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void transitionAnimation(boolean revealing) {
        RevealAnimationBuilder revealAnimationBuilder = new RevealAnimationBuilder();
        int coords[] = new int[2];
        binding.settingsCloseButton.getLocationOnScreen(coords);

        Animator revealAnimation = revealAnimationBuilder
                .setChangeCallback(() -> {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                })
                .setContext(this)
                .setRevealCenterX(revealCenterX)
                .setRevealCenterY(revealCenterY)
                .setRevealing(revealing)
                .setRootLayout(rootLayout)
                .setCoordsOnScreen(coords[0], coords[1])
                .buildAnimation();
        if(revealAnimation != null) {
            if(!revealing){
                binding.setClickListener(null);
            }

            revealAnimation.start();
        }
    }

    void showAlert(int errorCode) {
        String message = getResources().getStringArray(R.array.billing_responses)[errorCode];

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                        .setMessage("Billing Error: " + message)
                        .setPositiveButton("OK", null);

        Log.d(TAG, "Showing showAlert dialog: " + message);

        builder.create().show();
    }

    @Override
    public void onRestorePurchasesClick() {
        Log.d(TAG, "onRestorePurchasesClick: ");
        if (!adsPreferences.adsRemoved() && billingProcessor.loadOwnedPurchasesFromGoogle()) {
            Toast.makeText(this, "account purchases loaded from Google Play Store!", Toast.LENGTH_SHORT).show();
        } else {
            //todo this else block is for testing only (consuming the purchase and returning the ads won't be in the app)
            if (billingProcessor.consumePurchase(PRODUCT_ID)) {
                adsPreferences.setAdsRemovedStatus(false);
                Toast.makeText(this, "Ads remove consumed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRemoveAdsClick() {
        Log.d(TAG, "onRemoveAdsClick: ");
        if (!adsPreferences.adsRemoved()) {
            billingProcessor.purchase(this, PRODUCT_ID);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(TAG, "onProductPurchased() called with: productId = [" + productId + "], details = [" + details + "]");
        Toast.makeText(this, "purchase successful, ads removed", Toast.LENGTH_SHORT).show();
        adsPreferences.setAdsRemovedStatus(true);
        EventBus.getDefault().post(new PurchaseStatusEvent());
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: " + billingProcessor.getPurchaseListingDetails(PRODUCT_ID));
        adsPreferences.setAdsRemovedStatus(billingProcessor.isPurchased(PRODUCT_ID));
        EventBus.getDefault().post(new PurchaseStatusEvent());
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d(TAG, "onBillingError() called with: errorCode = [" + errorCode + "], error = [" + error + "]");
        showAlert(errorCode);
    }

    @Override
    public void onBillingInitialized() {
    }
}
