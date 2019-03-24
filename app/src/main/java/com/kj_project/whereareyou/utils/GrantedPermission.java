package com.kj_project.whereareyou.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class GrantedPermission {

    static boolean checkFlow = false;
    static boolean refreshLocalDBYn = false;
    public static final int START_LOCALDB = 4459;
    static Context thisContext;
    //사용자에게 권한추가 요청
    public static void getGrantedPermission(Context context, Activity activity) {
        thisContext = context;
        TedPermission.with(context).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 기본앱 권한은 필요없다.
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        }).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

}
