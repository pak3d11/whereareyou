package com.kj_project.whereareyou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 현재위치 update startReceiver
 */
public class LocationRequestStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LocationRequestService.class));
    }
}

