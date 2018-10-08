package com.appolica.weatherify.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.model.Forecast;
import com.appolica.weatherify.android.model.ForecastDataBlock;
import com.appolica.weatherify.android.model.ForecastDataPoint;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by aleksandar
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "weatherify.db";
    private static final int DATABASE_VERSION = 8;

    private Dao<Forecast, Integer> forecastDao;
    private Dao<ForecastDataBlock, Integer> forecastDataBlock;
    private Dao<ForecastDataPoint, Integer> forecastDataPointDao;
    private Dao<ForecastLocation, Integer> forecastLocationDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ForecastLocation.class);
            TableUtils.createTable(connectionSource, Forecast.class);
            TableUtils.createTable(connectionSource, ForecastDataBlock.class);
            TableUtils.createTable(connectionSource, ForecastDataPoint.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create database tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ForecastLocation.class, true);
            TableUtils.dropTable(connectionSource, Forecast.class, true);
            TableUtils.dropTable(connectionSource, ForecastDataBlock.class, true);
            TableUtils.dropTable(connectionSource, ForecastDataPoint.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVersion + " to new "
                    + newVersion, e);
        }
    }

    Dao<ForecastLocation, Integer> getForecastLocationDao() throws SQLException {
        if (forecastLocationDao == null) {
            forecastLocationDao = getDao(ForecastLocation.class);
        }
        return forecastLocationDao;
    }

    Dao<Forecast, Integer> getForecastDao() throws SQLException {
        if (forecastDao == null) {
            forecastDao = getDao(Forecast.class);
        }
        return forecastDao;
    }

    Dao<ForecastDataBlock, Integer> getForecastDataBlockDao() throws SQLException {
        if (forecastDataBlock == null) {
            forecastDataBlock = getDao(ForecastDataBlock.class);
        }
        return forecastDataBlock;
    }

    Dao<ForecastDataPoint, Integer> getForecastDataPointDao() throws SQLException {
        if (forecastDataPointDao == null) {
            forecastDataPointDao = getDao(ForecastDataPoint.class);
        }
        return forecastDataPointDao;
    }
}
