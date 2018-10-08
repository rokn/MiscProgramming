package com.appolica.weatherify.android.dependencyinjection.graph.db;

import android.content.Context;

import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.db.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Bogomil Kolarov on 25.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

@Module
public class DBModule {
    @Provides
    public DatabaseHelper provideDatabaseHelper(Context context) {
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    @Provides
    public DBManager provideDBManager(DatabaseHelper databaseHelper) {
        try {
            return new DBManager(databaseHelper);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database failure!");
        }
    }
}
