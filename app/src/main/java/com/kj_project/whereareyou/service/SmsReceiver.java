package com.kj_project.whereareyou.service;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.kj_project.whereareyou.MainActivity;
import com.kj_project.whereareyou.utils.StringUtil;
import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SMS수신(Noti,휴서비스DB INSERT)
 */
public class SmsReceiver extends BroadcastReceiver {
    private Handler handler = new Handler();
    Context smsReceiverContext;
    private static final String CALLER_ID_SELECTION = " Data._ID IN "
            + " (SELECT DISTINCT lookup.data_id "
            + " FROM "
            + " (SELECT data_id, normalized_number, length(normalized_number) as len "
            + " FROM phone_lookup "
            + " WHERE min_match = ?) AS lookup "
            + " WHERE lookup.normalized_number = ? OR"
            + " (lookup.len <= ? AND "
            + " substr(?, ? - lookup.len + 1) = lookup.normalized_number))";

    // query params for caller id lookup without E164 number as param
    private static final String CALLER_ID_SELECTION_WITHOUT_E164 = " Data._ID IN "
            + " (SELECT DISTINCT lookup.data_id "
            + " FROM "
            + " (SELECT data_id, normalized_number, length(normalized_number) as len "
            + " FROM phone_lookup "
            + " WHERE min_match = ?) AS lookup "
            + " WHERE "
            + " (lookup.len <= ? AND "
            + " substr(?, ? - lookup.len + 1) = lookup.normalized_number))";




    public class ContactInfo {
        public long contactId;
        public String contactName;
        public String phoneNumbers;

        @Override
        public String toString() {
            return contactName;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean delete = false;
        boolean newSms = false;
        Contacts.initialize(context);
        smsReceiverContext  = context;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d("Choi Jinyoung", "문자받았다");


            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdusObj.length];
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        Log.d("choi", "문자 수신 번호 : " + phoneNumber);

                        String formattedPhoneNumber = StringUtil.formattingPhoneNumber(phoneNumber);
                        String msg = currentMessage.getDisplayMessageBody();
                        if(!msg.equals("문자 수신시 전송테스트")){
                            context.sendBroadcast(new Intent(MainActivity.ACTION_UPDATE_SMS_THREAD));
                        }


                        Date messageTime = new Date(currentMessage.getTimestampMillis());

                        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        long diffInMs = new Date().getTime() - messageTime.getTime();
                        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                        if (diffInSec > 60 || diffInSec < -60) {
                            return;
                        }

                        String format = bundle.getString("format");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
                        } else {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        }
//                            try {
//
//                                if (Long.parseLong(SmsDetailActivity.notiThreadId) != SmsUtil.getThreadIdFromNumber(context, phoneNumber)) {
//                                    showNotification(context, phoneNumber, msg);
//                                    newSms = false;
//                                } else {
//                                    context.sendBroadcast(new Intent(SmsActivity.ACTION_UPDATE_SMS_THREAD));
//                                    newSms = true;
//                                    Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);// (Context.VIBRATE_SERVICE)
//                                    long millisecond = 1000;  // 1초
//                                    vibrator.vibrate(millisecond);
//
//                                }
//                            } catch (Exception e) {
//                                showNotification(context, phoneNumber, msg);
//                            }

                    }
                    String insertData = "";
                    ContentValues values = new ContentValues();
                    boolean insertYn = false;
                    for (SmsMessage msg : messages) {
                        values = new ContentValues();
                        insertData = insertData + msg.getMessageBody();
                        insertYn = true;
                        values.put(Telephony.Sms.ADDRESS, msg.getDisplayOriginatingAddress());
                        values.put(Telephony.Sms.BODY, insertData);

                    }
                    if (insertYn && values != null) {
                        context.getApplicationContext().getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
//                        if (!delete) {
//                            LocalDBUtil.insertPerSms(context.getApplicationContext(), newSms);
//                        }
                    }
                }
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);
            }

            //마지막 SMS > RealmDB.insert

        }
    }





}
