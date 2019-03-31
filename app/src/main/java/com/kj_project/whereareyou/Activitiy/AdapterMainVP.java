package com.kj_project.whereareyou.Activitiy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kj_project.whereareyou.Fragment.SendSmsFragment;
import com.kj_project.whereareyou.Fragment.SettingFragment;

//ViewPager 용 Adapter
public class AdapterMainVP extends FragmentStatePagerAdapter {

    private int tabCount;

    public AdapterMainVP(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new SendSmsFragment();

            case 1:
                return new SettingFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
