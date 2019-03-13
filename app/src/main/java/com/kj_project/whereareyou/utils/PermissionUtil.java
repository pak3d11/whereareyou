package com.kj_project.whereareyou.utils;

import android.Manifest;
import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

public class PermissionUtil {
    public static void location(Context context, PermissionListener listener) {
        TedPermission.with(context)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissionListener(listener)
                .check();
    }
}