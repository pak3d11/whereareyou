package com.kj_project.whereareyou;

import android.content.Context;

import com.kj_project.whereareyou.utils.SettingUtil;

/**
 * 버전관리관련 클래스
 */
public class VersionManagement {

    private SettingUtil setting;
    private String preferenceVersion;

    public VersionManagement(Context context){
        this.setting = new SettingUtil(context);
        this.preferenceVersion = context.getString(R.string.preference_version);
    }

    public void setPreferenceVersion(){
        setting.setPreferenceVersion(preferenceVersion);
    }

    public String getPreferenceVersion(){
        return setting.getPreferenceVersion();
    }


}
