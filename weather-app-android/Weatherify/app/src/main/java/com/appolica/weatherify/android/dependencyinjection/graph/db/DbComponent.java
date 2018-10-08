package com.appolica.weatherify.android.dependencyinjection.graph.db;

import com.appolica.weatherify.android.db.DBManager;

/**
 * Created by Bogomil Kolarov on 25.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public interface DbComponent {
    DBManager getDBManager();
}
