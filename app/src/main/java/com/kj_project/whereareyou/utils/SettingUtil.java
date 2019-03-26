package com.kj_project.whereareyou.utils;

import android.content.Context;

/**
 * Preference에 Data를 셋팅해주는 클래스
 */
public class SettingUtil {
    private Context settingCon = null;
    private PreferenceUtil preferences = null;

    public SettingUtil(Context settingCon){
        if (this.settingCon == null) this.settingCon = settingCon;
        if (this.preferences == null) preferences = new PreferenceUtil(this.settingCon);
    }

    /**
     * Preference 버전 저장
     * @param isPreferenceVersion
     */
    public void setPreferenceVersion(String isPreferenceVersion) {
        preferences.setPreferences(this.settingCon, "setting", "isPreferenceVersion", isPreferenceVersion);
    }

    /**
     * Preference 버전 불러오기
     * @return
     */
    public String getPreferenceVersion(){
        return preferences.getPreferences(this.settingCon, "setting", "isPreferenceVersion", "0");
    }

    /**
     * 상대방 전화번호 저장
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber){
        preferences.setPreferences(this.settingCon, "setting", "phoneNumber", phoneNumber);
    }

    /**
     * 상대방 전화번호 가져오기
     * @return
     */
    public String getPhoneNumber(){
        return preferences.getPreferences(this.settingCon, "setting", "phoneNumber", "0");
    }
}
