package com.kj_project.whereareyou.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartUpBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("hyu_alarm", "StartUpBootReceiver " + intent.getAction());

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("hyu_alarm", "StartUpBootReceiver BOOT_COMPLETED");

            Log.d("hyu_alarm", "StartUpBootReceiver alerm");
            //TODO
            //스케쥴
//            if ( !BlockAlarmReceiver.isRegistedAlarm(context)) {
//                Log.d("hyu_alarm", "StartUpBootReceiver registed");
//                BlockAlarmReceiver.registAlarm(context);
//            }
        }
    }
}