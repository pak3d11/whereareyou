package com.kj_project.whereareyou.Activitiy;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.github.tamir7.contacts.Contact;
import com.kj_project.whereareyou.Fragment.SettingFragment;
import com.kj_project.whereareyou.R;
import com.kj_project.whereareyou.VersionManagement;
import com.kj_project.whereareyou.utils.KJUtil;
import com.kj_project.whereareyou.utils.GrantedPermission;
import com.kj_project.whereareyou.utils.NumDialog;
import com.kj_project.whereareyou.utils.NumDialogListener;
import com.kj_project.whereareyou.utils.SettingUtil;
import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mainCon = null;
    private SettingUtil setting = null;
    private KJUtil kjUtil = null;
    public static final String ACTION_UPDATE_SMS_THREAD = "whereareyou.update_sms_thread";
    private com.kj_project.whereareyou.utils.Settings settings;
    private NumDialog numDialog;
    boolean havePermission =false;
    public TabLayout tabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_SMS_THREAD);
        ApnUtils.initDefaultApns(this, new ApnUtils.OnApnFinishedListener() {
            @Override
            public void onFinished() {
                //메세지 전송시 필요
                settings = com.kj_project.whereareyou.utils.Settings.get(MainActivity.this, true);
            }
        });
        this.registerReceiver(receiver, filter);

        initSetting();
        layoutSetting();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("SMS전송","SMS보냅니다..");
            sendMsg();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(!havePermission){
            checkPermissions();
        }

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

        //전화번호 입력 체크
        if (setting.getPhoneNumber().equals("")){
            numDialog = new NumDialog(this);
            numDialog.setDialogListener(new NumDialogListener() {
                @Override
                public void onPositiveClick(String number) {
                    SettingFragment sf = (SettingFragment)getSupportFragmentManager().findFragmentById(R.id.view_pager);
                    sf.setResult(number);
                }

                @Override
                public void onNegativeClick() {
                }
            });
            numDialog.show();
        }
    }

    private void layoutSetting(){
        tabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.view_pager);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_send_sms)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_setting)));

        mViewPager.setAdapter(new AdapterMainVP(getSupportFragmentManager(), tabLayout.getTabCount()));

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void checkPermissions() {

        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();

        boolean isOnNotificationListener = true;
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            isOnNotificationListener = false;
        }
        //기본문자앱 체크
        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

                GrantedPermission.getGrantedPermission(getApplicationContext() , this);
                return;
            }else{
                //권한있음
                havePermission = true;
            }
        }
    }



    public void sendMsg() {
        send("문자 수신시 전송테스트", null);
    }
    public void send(@Nullable String text, @Nullable Bitmap bitmap) {
        new SendMessageAsyncTask(bitmap).execute(text);
    }

    private class SendMessageAsyncTask extends AsyncTask<String, Integer, List<Contact>> {
        private Bitmap bitmap = null;

        public SendMessageAsyncTask(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Contact> doInBackground(String... params) {
            String text = params[0];
            String phoneNumber = setting.getPhoneNumber().replaceAll("-", "");
            if (TextUtils.isEmpty(phoneNumber)) {
                return null;
            }

            com.klinker.android.send_message.Settings sendSettings = new com.klinker.android.send_message.Settings();
            sendSettings.setMmsc(settings.getMmsc());
            sendSettings.setProxy(settings.getMmsProxy());
            sendSettings.setPort(settings.getMmsPort());
            sendSettings.setUseSystemSending(true);
            Transaction transaction = new Transaction(MainActivity.this, sendSettings);

            Message message;

            message = new Message(text, phoneNumber);

            //이미지 사용안함
//            if (bitmap != null) {
//                message.setImage(bitmap);
//
//            }
            transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
            return null;
        }

        @Override
        protected void onPostExecute(List<Contact> r) {
            super.onPostExecute(r);
            //SMS전송 성공테스트후 진행
        }
    }
}
