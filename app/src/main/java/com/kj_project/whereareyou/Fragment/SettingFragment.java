package com.kj_project.whereareyou.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kj_project.whereareyou.R;

public class SettingFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fmt_setting, parentViewGroup, false);
    }

}
