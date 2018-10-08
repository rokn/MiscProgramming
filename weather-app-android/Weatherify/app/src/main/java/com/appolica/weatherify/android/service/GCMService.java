package com.appolica.weatherify.android.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

public class GCMService extends GcmTaskService {
    public static final String TAG_TASK_PERIODIC_LOG = "periodic";
    private static final String TAG = "GCMService";

    public GCMService() {
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.i(TAG, "onRunTask: " + taskParams.getTag());
        switch (taskParams.getTag()) {
            case TAG_TASK_PERIODIC_LOG:
                final Intent localIntent =
                        LocationManagerService.createIntent(
                                getApplicationContext(),
                                LocationManagerService.UPDATE_FAV_LOCATIONS
                                        | LocationManagerService.UPDATE_CURRENT_LOCATION);

                startService(localIntent);

                return GcmNetworkManager.RESULT_SUCCESS;
            default:
                Log.d(TAG, "onRunTask: unknown tag");
                return GcmNetworkManager.RESULT_FAILURE;
        }
    }
}
