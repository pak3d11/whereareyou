package com.kj_project.whereareyou.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kj_project.whereareyou.utils.LocationManager;

/**
 * 현재위치 Update
 */
public class LocationRequestService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager.instance().retrieveLocationUpdate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
