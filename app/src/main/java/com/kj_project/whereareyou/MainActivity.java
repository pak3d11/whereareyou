package com.kj_project.whereareyou;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kj_project.whereareyou.utils.KJUtil;
import com.kj_project.whereareyou.utils.SettingUtil;

public class MainActivity extends AppCompatActivity {

    private Context mainCon = null;
    private SettingUtil setting = null;
    private KJUtil kjUtil = null;
    private EditText setNumber;
    private Button saveNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSetting();
        layoutSetting();
    }

    //앱 실행 초기설정 함수
    private void initSetting(){
        mainCon = MainActivity.this;

        if (setting == null) setting = new SettingUtil(mainCon);
        if (kjUtil == null) kjUtil = new KJUtil(mainCon);

        //preference 버전체크
        VersionManagement vm = new VersionManagement(mainCon);
        String currentVersion = vm.getPreferenceVersion();
        if (currentVersion.equals("0") || !currentVersion.equals(getString(R.string.preference_version))) vm.setPreferenceVersion();
    }

    private void layoutSetting(){
        saveNumber = findViewById(R.id.saveNumber);
        setNumber = findViewById(R.id.setNumber);

        //View Text Setting
        if (!setting.getPhoneNumber().equals("")) setNumber.setText(setting.getPhoneNumber());

        //View Event Setting
        saveNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting.setPhoneNumber(String.valueOf(setNumber.getText()));
                kjUtil.kjLog("d", setting.getPhoneNumber());

            }
        });
    }
}
