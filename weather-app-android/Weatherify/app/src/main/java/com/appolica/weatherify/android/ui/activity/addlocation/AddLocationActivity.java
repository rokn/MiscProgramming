package com.appolica.weatherify.android.ui.activity.addlocation;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.databinding.ActivityAddLocationBinding;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.model.LocationCoordinates;
import com.appolica.weatherify.android.service.FetchForecastIntentService;
import com.appolica.weatherify.android.ui.AddLocationClickListener;
import com.appolica.weatherify.android.ui.activity.MainActivity;
import com.appolica.weatherify.android.ui.activity.base.BaseActivity;
import com.appolica.weatherify.android.ui.activity.favourites.FavouritesActivity;
import com.appolica.weatherify.android.ui.adapter.AddLocationAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import javax.inject.Inject;

public class AddLocationActivity extends BaseActivity
        implements ResultCallback<PlaceBuffer>,
        AddLocationClickListener,
        AddLocationAdapter.SuggestedLocationClick {

    public static final int LETTERS_TO_START_AUTOSUGGESTION = 2;
    private static final String TAG = "AddLocationActivity";
    @Inject
    protected GoogleApiClient googleApiClient;
    @Inject
    protected DBManager dbManager;

    protected ActivityAddLocationBinding binding;

    private AutocompleteFilter autocompleteFilter;
    private AddLocationAdapter adapter;
    private ObservableField<String> locationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_location);

        final WeatherifyApplication application = (WeatherifyApplication) getApplication();
        final AppComponent appComponent = application.getAppComponent();

        final AddLocationComponent component = appComponent.getAddLocationComponent();

        component.inject(this);

        autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        adapter = new AddLocationAdapter(this);
        setupObservableField();

        binding.setLayoutAdapter(adapter);
        binding.setShowEmptyState(false);
        binding.setLocationText(locationText);
        binding.setLayoutManager(new LinearLayoutManager(this));
        binding.setItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .colorResId(R.color.addLocationResultItemsDivider)
                .marginResId(R.dimen.add_location_result_divider_start_margin, R.dimen.add_location_result_divider_end_margin)
                .build());
        binding.setClickListener(this);

        binding.addLocationSearchBar.setOnFocusChangeListener((v, hasFocus) ->
            binding.setShowClearButton(locationText.get().length() > 0 && hasFocus)
        );

        binding.setActionListener((view, actionId, event) -> {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        });

        ensureClientIsConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        onCloseClick();
    }

    private void setupObservableField() {
        locationText = new ObservableField<>();
        locationText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                ensureClientIsConnected();
                binding.setShowClearButton(locationText.get().length() > 0);

                if (locationText.get().length() >= LETTERS_TO_START_AUTOSUGGESTION) {
                    PendingResult<AutocompletePredictionBuffer> result =
                            Places.GeoDataApi.getAutocompletePredictions(googleApiClient, locationText.get(),
                                    null, autocompleteFilter);

                    result.setResultCallback(autocompletePredictions -> {
                        if (locationText.get().length() >= LETTERS_TO_START_AUTOSUGGESTION) {
                            adapter.insertAutosuggestionLocations(autocompletePredictions);
                            binding.setShowEmptyState(true);
                            autocompletePredictions.release();
                        }
                    });
                } else {
                    binding.setShowEmptyState(false);
                    adapter.clearAutosuggestionLocations();
                }
            }
        });
    }

    private void ensureClientIsConnected() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onClearClick() {
        binding.addLocationSearchBar.setText("");
    }

    @Override
    public void onCloseClick() {
        returnToFavourites();
    }

    protected void returnToFavourites() {
        Intent intent = new Intent(this, FavouritesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.do_not_move, android.R.anim.fade_out);
    }

    @Override
    public void locationClick(int position) {
        ensureClientIsConnected();

        String locationID = adapter.getItem(position).getPlaceID();
        PendingResult<PlaceBuffer> result = Places.GeoDataApi.getPlaceById(googleApiClient, locationID);
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull PlaceBuffer places) {
        if (places.getCount() < 1) {
            return;
        }

        Place place = places.get(0);
        LatLng latLng = place.getLatLng();
        LocationCoordinates locationCoordinates =
                new LocationCoordinates(latLng.latitude, latLng.longitude);
        locationCoordinates.setName((String) place.getName());
        locationCoordinates.setLocationId(place.getId());

        Log.d(TAG, "onResult: forecastLocation added: " + place.getName());
        saveLocationInDB(locationCoordinates);
        fetchForecastForLocation(locationCoordinates);

        continueToMainActivity(locationCoordinates.getLocationId());
    }

    private void saveLocationInDB(LocationCoordinates locationCoordinates) {
        dbManager.createForecastLocation(locationCoordinates);
    }

    private void fetchForecastForLocation(LocationCoordinates locationCoordinates) {
        Intent intent = new Intent(getApplicationContext(), FetchForecastIntentService.class);
        intent.putExtra(FetchForecastIntentService.EXTRA_LOCATION, locationCoordinates);
        startService(intent);
    }

    protected void continueToMainActivity(String locationId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.WAIT_FOR_LOCATION, true);
        intent.putExtra(MainActivity.GOTO_LOCATION_ID, locationId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
