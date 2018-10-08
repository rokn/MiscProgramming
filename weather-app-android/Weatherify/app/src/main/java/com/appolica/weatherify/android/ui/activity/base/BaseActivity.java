package com.appolica.weatherify.android.ui.activity.base;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by aleksandar on 30.08.16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected BillingProcessor billingProcessor;
    private InterstitialAd interstitialAd;

    @Override
    protected void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }

        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void requestNewInterstitialAd(String addUnitId) {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(addUnitId);

        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        interstitialAd.loadAd(request);
    }

    protected void showInterstitialAd() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            requestNewInterstitialAd(interstitialAd.getAdUnitId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor != null &&
                billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }
}
