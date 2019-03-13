package com.kj_project.whereareyou.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference RUD 클래스
 */
public class PreferenceUtil {

    private Context preferenceCon = null;

    public PreferenceUtil(Context preferenceCon){
        if (this.preferenceCon == null) this.preferenceCon = preferenceCon;
    }

    /**
     * Preference Data Update
     * @param context
     * @param categoryName
     * @param preferenceName
     * @param preferenceValue
     */
    public void setPreferences(Context context, String categoryName, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(categoryName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    /**
     * Preference Data Delete
     * @param context
     * @param categoryName
     * @param preferenceName
     */
    public void deletePreferences(Context context, String categoryName, String preferenceName){
        SharedPreferences sharedPreferences = context.getSharedPreferences(categoryName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(preferenceName);
        editor.apply();
    }

    /**
     * Preference Data read
     * @param context
     * @param categoryName
     * @param preferenceName
     * @param defaultValue
     * @return
     */

    public String getPreferences(Context context, String categoryName, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(categoryName, Context.MODE_PRIVATE);

        if (!sharedPreferences.contains(preferenceName)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(preferenceName, defaultValue);
            editor.apply();
        }

        return sharedPreferences.getString(preferenceName, defaultValue);
    }
}
