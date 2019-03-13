package com.kj_project.whereareyou.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * 알람 event시 작동되는 receiver
         */
        Log.w("ALARM", "onReceive");

    }
}
