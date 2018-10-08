package com.appolica.weatherify.android.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.appolica.weatherify.android.event.DBFavoritesUpdatedEvent;
import com.appolica.weatherify.android.event.DBForecastAddedEvent;
import com.appolica.weatherify.android.event.DBForecastDeletedEvent;
import com.appolica.weatherify.android.event.DBUpdateEntryEvent;
import com.appolica.weatherify.android.model.Forecast;
import com.appolica.weatherify.android.model.ForecastDataBlock;
import com.appolica.weatherify.android.model.ForecastDataPoint;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.appolica.weatherify.android.model.LocationCoordinates;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.appolica.weatherify.android.model.ForecastLocation.COLUMN_GOOGLE_ID;
import static com.appolica.weatherify.android.model.ForecastLocation.COLUMN_IS_CURRENT_LOCATION;

/**
 * Created by Alexander Iliev on 31.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class DBManager {
    public static final boolean DELETE_WITH_EVENT = true;
    public static final boolean DELETE_WITHOUT_EVENT = false;

    private static final String TAG = "DBManager";
    private static final int HOURLY_MAX_HOURS = 24;
    private static final boolean NOTIFY_NEW_LOCATION = true;
    private static final boolean NOTIFY_OLD_LOCATION = false;

    private DatabaseHelper databaseHelper;
    private Dao<ForecastLocation, Integer> forecastLocationDao;
    private Dao<Forecast, Integer> forecastDao;
    private Dao<ForecastDataPoint, Integer> forecastDataPointDao;
    private Dao<ForecastDataBlock, Integer> forecastDataBlockDao;

    private final Boolean dbLock = true;

    public DBManager(DatabaseHelper databaseHelper) throws SQLException {
        this.databaseHelper = databaseHelper;

        forecastLocationDao = databaseHelper.getForecastLocationDao();
        forecastDao = databaseHelper.getForecastDao();
        forecastDataPointDao = databaseHelper.getForecastDataPointDao();
        forecastDataBlockDao = databaseHelper.getForecastDataBlockDao();
    }

    public void releaseHelper() {
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }

    public boolean isOpen() {
        return databaseHelper != null && databaseHelper.isOpen();
    }

    public boolean isDBEmpty() {
        return getCurrentForecastLocation() == null && getFavoriteForecastLocations().size() == 0;
    }

    @Nullable
    public ForecastLocation getCurrentForecastLocation() {
        try {
            synchronized (dbLock) {
                PreparedQuery<ForecastLocation> preparedQuery = forecastLocationDao
                        .queryBuilder()
                        .where()
                        .eq(COLUMN_IS_CURRENT_LOCATION, true)
                        .prepare();

                return forecastLocationDao.queryForFirst(preparedQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ForecastLocation> getFavoriteForecastLocations() {
        try {
            synchronized (dbLock) {
                PreparedQuery<ForecastLocation> preparedQuery = forecastLocationDao
                        .queryBuilder()
                        .orderBy(ForecastLocation.ORDER_POSITION, true)
                        .where()
                        .eq(COLUMN_IS_CURRENT_LOCATION, false)
                        .prepare();

                return forecastLocationDao.query(preparedQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Nullable
    public ForecastLocation getForecastLocation(String locationId, boolean isCurrentLocation) {
        try {
            List<ForecastLocation> forecastLocations;

            synchronized (dbLock) {
                if (isCurrentLocation) {
                    forecastLocations = databaseHelper.getForecastLocationDao().queryForEq(COLUMN_IS_CURRENT_LOCATION, true);
                } else {
                    forecastLocations = databaseHelper.getForecastLocationDao().queryForEq(COLUMN_GOOGLE_ID, locationId);
                }
            }

            for (ForecastLocation forecastLocation : forecastLocations) {
                if (forecastLocation.isCurrentLocation() == isCurrentLocation) {
                    return forecastLocation;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getFavouriteForecastsLocationIds() {
        return Stream.of(getFavoriteForecastLocations())
                .map(ForecastLocation::getGoogleLocationId)
                .collect(Collectors.toList());
    }

    public void deleteCurrentLocationEntry(boolean withEvent) {
        deleteEntry(getCurrentForecastLocation(), withEvent);
    }

    public void deleteEntry(@Nullable ForecastLocation forecastLocation, boolean withEvent) {
        if (forecastLocation == null) {
            return;
        }

        try {
            synchronized (dbLock) {
                TransactionManager.callInTransaction(databaseHelper.getConnectionSource(), () -> {
                    deleteEntry(forecastLocation);

                if (withEvent) {
                    EventBus.getDefault().post(new DBForecastDeletedEvent(
                            forecastLocation.isCurrentLocation(),
                            forecastLocation.getOrderPosition()));
                }

                    return null;
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteEntry(ForecastLocation forecastLocation) throws SQLException {
        deleteForecast(forecastLocation.getForecast());
        forecastLocationDao.delete(forecastLocation);

        final int deletedPosition = forecastLocation.getOrderPosition();
        if (deletedPosition > 0) {

            PreparedQuery<ForecastLocation> orderedLocationsQuery = forecastLocationDao.queryBuilder()
                    .orderBy(ForecastLocation.ORDER_POSITION, true)
                    .where()
                    .gt(ForecastLocation.ORDER_POSITION, deletedPosition)
                    .or()
                    .eq(ForecastLocation.ORDER_POSITION, deletedPosition)
                    .prepare();

            CloseableIterator<ForecastLocation> updateEntriesIterator =
                    forecastLocationDao.iterator(orderedLocationsQuery);

            for (int orderPosition = deletedPosition; updateEntriesIterator.hasNext(); orderPosition++) {
                ForecastLocation toUpdateLocation = updateEntriesIterator.nextThrow();

                UpdateBuilder<ForecastLocation, Integer> updateBuilder = forecastLocationDao.updateBuilder();
                updateBuilder
                        .updateColumnValue(ForecastLocation.ORDER_POSITION, orderPosition)
                        .where()
                        .idEq(toUpdateLocation.getId());

                forecastLocationDao.update(updateBuilder.prepare());
            }
        }
    }

    private void deleteForecast(Forecast forecast) throws SQLException {
        if (forecast == null) {
            return;
        }

        forecastDataPointDao.delete(forecast.getHourly().getData());
        forecastDataPointDao.delete(forecast.getDaily().getData());
        forecastDataPointDao.delete(forecast.getCurrently());
        forecastDataBlockDao.delete(forecast.getDaily());
        forecastDataBlockDao.delete(forecast.getHourly());
        forecastDao.delete(forecast);
    }

    public void deleteAll(Collection<ForecastLocation> deletedFavourites, boolean withEvent) {
        try {
            synchronized (dbLock) {
                TransactionManager.callInTransaction(databaseHelper.getConnectionSource(), () -> {

                    for (ForecastLocation deletedLocation : deletedFavourites) {
                        deleteEntry(deletedLocation);
                    }

                    return null;
                });
            }

            if (withEvent) {
                boolean isCurrentLocationDeleted = Stream.of(deletedFavourites)
                        .anyMatch(ForecastLocation::isCurrentLocation);

                DBForecastDeletedEvent deletedEvent = new DBForecastDeletedEvent(
                                                            isCurrentLocationDeleted,
                                                            DBForecastDeletedEvent.DELETED_ALL_POSITION);
                EventBus.getDefault().post(deletedEvent);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getFavouritesCount() throws SQLException {
        synchronized (dbLock) {
            return (int) forecastLocationDao.queryBuilder().countOf();
        }
    }

    public void updateCurrentLocation(LocationCoordinates locationCoordinates) {
        ForecastLocation oldLocation = getCurrentForecastLocation();
        ForecastLocation newLocation;
        boolean hadLocation = oldLocation != null;
        if(hadLocation) {
            newLocation = oldLocation;
            newLocation.setName(locationCoordinates.getName());
            newLocation.setCurrentLocation(locationCoordinates.isCurrentLocaiton());
            newLocation.setLatitude(locationCoordinates.getLatitude());
            newLocation.setLongitude(locationCoordinates.getLongitude());
        } else {
            newLocation = ForecastLocation.getBuilder()
                    .setName(locationCoordinates.getName())
                    .setCurrentLocation(locationCoordinates.isCurrentLocaiton())
                    .setLatitude(locationCoordinates.getLatitude())
                    .setLongitude(locationCoordinates.getLongitude())
                    .setGoogleLocationId(null)
                    .setOrderPosition(0)
                    .build();
        }

        try {
            synchronized (dbLock) {
                if (hadLocation) {
                    forecastLocationDao.update(newLocation);
                } else {
                    forecastLocationDao.create(newLocation);
                }

                notifyUpdateHappened(newLocation, !hadLocation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateForecastForLocation(@NonNull Forecast newForecast, LocationCoordinates locationCoordinates) {
        ForecastLocation forecastLocation = getForecastLocation(locationCoordinates.getLocationId(), locationCoordinates.isCurrentLocaiton());

        if (forecastLocation == null) {
            Log.d(TAG, "updateForecastForLocation: no forecastLocation for given coordinates");
            return;
        }

        Forecast oldForecast = forecastLocation.getForecast();
        forecastLocation.setForecast(newForecast);
        linkNewForecastData(newForecast);

        try {
            synchronized (dbLock) {
                TransactionManager.callInTransaction(databaseHelper.getConnectionSource(), () -> {
                    forecastDataPointDao.create(newForecast.getCurrently());
                    forecastDataBlockDao.create(newForecast.getHourly());
                    forecastDataBlockDao.create(newForecast.getDaily());
                    forecastDataPointDao.create(newForecast.getHourly().getDataLimited(HOURLY_MAX_HOURS));
                    forecastDataPointDao.create(newForecast.getDaily().getData());
                    databaseHelper.getForecastDao().create(newForecast);
                    databaseHelper.getForecastLocationDao().update(forecastLocation);
                    return null;
                });
            }

            notifyUpdateHappened(forecastLocation, NOTIFY_OLD_LOCATION);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createForecastLocation(LocationCoordinates locationCoordinates) {
        if (!locationCoordinates.isCurrentLocaiton() &&
            getForecastLocation(locationCoordinates.getLocationId(), locationCoordinates.isCurrentLocaiton()) != null) {
            return;
        }

        ForecastLocation forecastLocation;
        try {
            forecastLocation = ForecastLocation.getBuilder()
                    .setName(locationCoordinates.getName())
                    .setCurrentLocation(locationCoordinates.isCurrentLocaiton())
                    .setLatitude(locationCoordinates.getLatitude())
                    .setLongitude(locationCoordinates.getLongitude())
                    .setGoogleLocationId(locationCoordinates.getLocationId())
                    .setOrderPosition(locationCoordinates.isCurrentLocaiton() ? 0 : getFavouritesCount() + 1)
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            synchronized (dbLock) {
                TransactionManager.callInTransaction(databaseHelper.getConnectionSource(), () -> {
                    if (locationCoordinates.isCurrentLocaiton()) {
                        deleteCurrentLocationEntry(DELETE_WITHOUT_EVENT);
                    }

                    databaseHelper.getForecastLocationDao().create(forecastLocation);
                    return null;
                });
            }

            notifyUpdateHappened(forecastLocation, NOTIFY_NEW_LOCATION);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void linkNewForecastData(Forecast forecast) {
        Stream.ofNullable(forecast.getHourly().getData()).forEach(dataPoint -> dataPoint.setForecastDataBlock(forecast.getHourly()));
        Stream.ofNullable(forecast.getDaily().getData()).forEach(dataPoint -> dataPoint.setForecastDataBlock(forecast.getDaily()));
    }

    private void notifyUpdateHappened(ForecastLocation forecastLocation, boolean isLocationNew) {
        if (isLocationNew) {
            EventBus.getDefault().post(new DBForecastAddedEvent(
                                                forecastLocation.isCurrentLocation(),
                                                forecastLocation.getGoogleLocationId()));
        } else {
            EventBus.getDefault().post(new DBUpdateEntryEvent(forecastLocation.getGoogleLocationId(), forecastLocation.isCurrentLocation()));
        }
    }

    public void updateLocationsOrders(final Collection<ForecastLocation> favorites, boolean withEvent) {
        try {
            synchronized (dbLock) {
                TransactionManager.callInTransaction(databaseHelper.getConnectionSource(),
                        () -> {
                            for (ForecastLocation favorite : favorites) {
                                if(favorite.isCurrentLocation()) {
                                    continue;
                                }

                                PreparedQuery<ForecastLocation> preparedQuery = forecastLocationDao.updateBuilder()
                                        .updateColumnExpression(ForecastLocation.ORDER_POSITION, String.valueOf(favorite.getOrderPosition()))
                                        .where()
                                        .eq(ForecastLocation.COLUMN_GOOGLE_ID, favorite.getGoogleLocationId())
                                        .prepare();

                                forecastLocationDao.update((PreparedUpdate<ForecastLocation>) preparedQuery);
                            }

                            return null;
                        });
            }

            if (withEvent) {
                EventBus.getDefault().post(new DBFavoritesUpdatedEvent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
