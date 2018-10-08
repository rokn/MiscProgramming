package com.appolica.weatherify.android.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.databinding.LayoutDetailedForecastBinding;
import com.appolica.weatherify.android.databinding.LayoutForecastDetailsBinding;
import com.appolica.weatherify.android.databinding.LayoutForecastDetailsTimearcBinding;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.event.DBUpdateEntryEvent;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.appolica.weatherify.android.ui.activity.favourites.FavouritesActivityWithAnimation;
import com.appolica.weatherify.android.ui.view.scrollview.ScrollDetectorAdapter;
import com.appolica.weatherify.android.ui.viewpager.PageSelectedNotifyAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;
/**
 * Created by Alexander Iliev
 */

public class DetailedForecastFragment extends Fragment
        implements PageSelectedNotifyAdapter.OnFragmentSelectedListener,
        DetailedForecastContract.Controller {

    public static final String LOCATION_GOOGLE_ID_BUNDLE_KEY = "name";
    public static final String LOCATION_CURRENT_BUNDLE_KEY = "isCurrentLocation";

    private static final String TAG = "DetailedForecastFragmen";

    @Inject
    protected DBManager dbManager;

    private LayoutDetailedForecastBinding binding;
    private ForecastLocation forecastLocation;
    private CanScrollListener canScrollListener;
    private ViewTreeObserver.OnScrollChangedListener scrollChangedListener;
    private DetailedForecastViewModel detailsModel;
    private ScrollDetectorAdapter scrollDetector;

    public static DetailedForecastFragment newInstance(String forecastLocationId, boolean isCurrent) {
        final DetailedForecastFragment fragment = new DetailedForecastFragment();

        Bundle args = new Bundle();
        args.putString(DetailedForecastFragment.LOCATION_GOOGLE_ID_BUNDLE_KEY, forecastLocationId);
        args.putBoolean(DetailedForecastFragment.LOCATION_CURRENT_BUNDLE_KEY, isCurrent);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding =
                DataBindingUtil.inflate(
                        inflater,
                        R.layout.layout_detailed_forecast,
                        container,
                        false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final WeatherifyApplication application =
                (WeatherifyApplication) getActivity().getApplication();
        final AppComponent appComponent = application.getAppComponent();

        appComponent.getDetailedForecastComponent().inject(this);

        final DetailedForecastComponent component = appComponent.getDetailedForecastComponent();

        detailsModel = new DetailedForecastViewModel(this, getContext(), component);
        final LayoutForecastDetailsBinding detailsBinding = binding.forecastDetails;
        final LayoutForecastDetailsTimearcBinding arcBinding = detailsBinding.detailsTimearc;

        scrollDetector = new ScrollDetectorAdapter();

        scrollDetector.addView((precipImage, rect) -> detailsModel.precipitationScrolledIn(precipImage), detailsBinding.precipitationItem.detailsImage);
        scrollDetector.addView((humidImage, rect) -> detailsModel.humidityScrolledIn(humidImage), detailsBinding.humidityItem.detailsImage);
        scrollDetector.addGroup(group -> detailsModel.arcScrolledIn(group), arcBinding.orbitingView, arcBinding.imageViewTimeArc, arcBinding.textViewTime);

        bindForecastData();

        binding.executePendingBindings();

        animateWind(detailsBinding.windSpeedItem.detailsImage);
        setDetailIconsInitLevels();
    }

    private void bindForecastData() {
        final Bundle arguments = getArguments();
        final String forecastLocationId = arguments.getString(LOCATION_GOOGLE_ID_BUNDLE_KEY);
        final boolean isCurrentLocation = arguments.getBoolean(LOCATION_CURRENT_BUNDLE_KEY);

        forecastLocation = dbManager.getForecastLocation(forecastLocationId, isCurrentLocation);
        detailsModel.onCreate(forecastLocationId, isCurrentLocation);

        binding.setDetailsModel(detailsModel);

        if (scrollChangedListener == null) {
            initializeScrollListener();
        }

        binding.scrollViewDetailedForecast.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);
    }

    private void initializeScrollListener() {
        scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            int[] screenLocation = new int[2];

            @Override
            public void onScrollChanged() {
                binding.scrollViewDetailedForecast.getLocationOnScreen(screenLocation);

                if (screenLocation[0] == 0) {
                    boolean canScrollUp =
                            binding.scrollViewDetailedForecast.canScrollVertically(-1);

                    canScrollListener.onPerformedScroll(canScrollUp);
                }
            }
        };
    }

    private void setDetailIconsInitLevels() {
        final LayoutForecastDetailsBinding detailsBinding = binding.forecastDetails;
        setClipInitLevel(detailsBinding.precipitationItem.detailsImage, 0);
        setClipInitLevel(detailsBinding.humidityItem.detailsImage, 0);
    }

    private void setClipInitLevel(ImageView imageView, int initLevel) {
        final LayerDrawable layerDrawable = (LayerDrawable) imageView.getDrawable();
        final ClipDrawable clipDrawable = (ClipDrawable) layerDrawable.getDrawable(1);

        clipDrawable.setLevel(initLevel);
    }

    private void animateWind(ImageView detailsImage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            AnimatedVectorDrawableCompat drawableCompat = (AnimatedVectorDrawableCompat) detailsImage.getDrawable();
            drawableCompat.start();
        } else {
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) detailsImage.getDrawable();
            drawable.start();
        }
    }

    public void setCanScrollListener(CanScrollListener canScrollListener) {
        this.canScrollListener = canScrollListener;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        binding.scrollViewDetailedForecast.getViewTreeObserver().removeOnScrollChangedListener(scrollDetector);
        scrollDetector = null;
        detailsModel.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDBUpdateEvent(DBUpdateEntryEvent event) {
        Log.d(TAG, "onDBUpdateEvent: ");
        if ((forecastLocation.getGoogleLocationId() == null && event.getForecastLocationId() == null)
                || event.getForecastLocationId().equals(forecastLocation.getGoogleLocationId())) {

            forecastLocation =
                    dbManager.getForecastLocation(
                            forecastLocation.getGoogleLocationId(),
                            forecastLocation.isCurrentLocation());

            detailsModel.updateData();
        }
    }

    @Override
    public void onFragmentSelected() {
        if (scrollDetector != null) {
            scrollDetector.updateParentRect(binding.scrollViewDetailedForecast);
            binding.scrollViewDetailedForecast.getViewTreeObserver().addOnScrollChangedListener(scrollDetector);
        }
    }

    @Override
    public void onFragmentDeselected() {
        binding.scrollViewDetailedForecast.getViewTreeObserver().removeOnScrollChangedListener(scrollDetector);
    }

    @Override
    public void onShouldOpenFavourites() {
        final Activity activity = getActivity();
        final Intent intent = new Intent(activity, FavouritesActivityWithAnimation.class);

        int[] coordsOnScreen = new int[2];

        final int imageSize = (int) activity.getResources().getDimension(R.dimen.top_buttons_image_size);

        binding.currentForecast.goToFavouritesButton.getLocationOnScreen(coordsOnScreen);
        final int centerX = coordsOnScreen[0] +
                imageSize / 2;
        final int centerY = coordsOnScreen[1] +
                imageSize / 2;

        intent.putExtra(FavouritesActivityWithAnimation.CENTER_X_EXTRA, centerX);
        intent.putExtra(FavouritesActivityWithAnimation.CENTER_Y_EXTRA, centerY);

        startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, R.anim.do_not_move);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.releaseHelper();
        EventBus.getDefault().unregister(this);
    }
}
