package com.kj_project.whereareyou.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.xizzhu.rxlocation.AndroidLocationProvider;
import com.github.xizzhu.rxlocation.LocationUpdateRequest;
import com.github.xizzhu.rxlocation.RxLocationProvider;
import com.gun0912.tedpermission.PermissionListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

public class LocationManager {
    private static final LocationManager instance = new LocationManager();

    public static LocationManager instance() {
        return instance;
    }

    private RxLocationProvider locationProvider;
    private CompositeDisposable disposable = new CompositeDisposable();
    NotificationManager notifiyMgr ;
//    private LocationManager() {
//        locationProvider = new AndroidLocationProvider();
//    }

    /**
     * 위치 업데이트 받기 시작
     */
//    public void retrieveLocationUpdate() {
//        stopLocationUpdate();
//
//        PreferenceManager preferenceManager = PreferenceManager.getInstance(HyuServiceApp.instance().context());
//        if (!preferenceManager.getLocationBlock()) {
//            preferenceManager.setIsInBlockZone(false);
//            return;
//        }
//
//        PermissionUtil.location(HyuServiceApp.instance().context(), new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//                // 폰에 저장된 마지막 위치 좌표로 검사
//                disposable.add(lastLocation().subscribe(location -> checkInArea(location), e -> {}));
//
//                LocationUpdateRequest locationUpdateRequest = new LocationUpdateRequest.Builder()
////      .priority(LocationUpdateRequest.PRIORITY_LOW_POWER)
//                        .intervalInMillis(TimeUnit.MINUTES.toMillis(5)) // 5분 마다
////          .intervalInMillis(TimeUnit.SECONDS.toMillis(5)) // 5초 마다. 테스트용
////          .smallestDistanceInMeters(100f) // 100미터 이상 움직여야
//                        .build();
//
//                // 바뀐 위치 정보로 검사
//                disposable.add(locationProvider.getLocationUpdates(locationUpdateRequest).subscribe(location -> checkInArea(location), Throwable::printStackTrace));
//            }
//
//            @Override
//            public void onPermissionDenied(ArrayList<String> deniedPermissions) {}
//        });
//    }

    /**
     * 위치업데이트 stop
     */
    public void stopLocationUpdate() {
        disposable.clear();
    }

    /**
     * 차단지역 안에있는지 체크
     */
//    private void checkInArea(Location location) {
//        Location locationB = new Location("");
//        locationB.setLatitude(location.getLatitude());
//        locationB.setLongitude(location.getLongitude());
//
//        PreferenceManager preferenceManager = PreferenceManager.getInstance(HyuServiceApp.instance().context());
//        Location locationA = preferenceManager.getBlockLocation();
//        if (locationA == null) {
//            preferenceManager.setIsInBlockZone(false);
//            return;
//        }
//
//        int distance = (int) locationA.distanceTo(locationB); // meter
//        int km = 1000;
//        if (PreferenceManager.getInstance(HyuServiceApp.instance().context()).getMapKm() == 3) {
//            km = 3000;
//        }
//        preferenceManager.setIsInBlockZone(distance <= km); // 1km OR 3km 이내로 진입했는가'
//
//        if(RealmManager.getInstance().getBlockContacts().size() > 0 && CheckBoxBlockingUtil.checkLocationYn(HyuServiceApp.instance().context())){
//
//            Intent intent = new Intent(
//                    HyuServiceApp.instance().context(),//현재제어권자
//                    DeviceLocalDBDeleteService.class); // 이동할 컴포넌트
//            HyuServiceApp.instance().context().startService(intent); // 서비스 시작
//            notifiyMgr = (NotificationManager)HyuServiceApp.instance().context().getSystemService(Context.NOTIFICATION_SERVICE);
//            notifiyMgr.cancelAll();
//            Log.d("ServiceUse_location","삭제시작한다");
//        }else {
//            Log.d("ServiceUse_location","삭제안한다");
//        }
//
//    }

    public Single<Location> lastLocation() {
        return locationProvider.getLastLocation();
    }
}
