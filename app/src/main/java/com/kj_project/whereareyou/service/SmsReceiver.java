package com.kj_project.whereareyou.service;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.github.tamir7.contacts.Contacts;
import com.kj_project.whereareyou.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * SMS수신(Noti,휴서비스DB INSERT)
 */
public class SmsReceiver extends BroadcastReceiver {
    private Handler handler = new Handler();

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

        if (!Telephony.Sms.getDefaultSmsPackage(context).equals(context.getPackageName())) {
            return;
        }
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
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
                        Date messageTime = new Date(currentMessage.getTimestampMillis());

                        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            Log.w("APP# SmsReceiver | onReceive", phoneNumber + " - " + msg + " - " + dFormat.format(messageTime));

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


//    private void showNotification(Context context, String phoneNumber, String message) {
//
//
//        context.sendBroadcast(new Intent(SmsActivity.ACTION_UPDATE_SMS_THREAD));
//
//        String msg = message;
////    String title = ContactManager.getInstance(context).getName(phoneNumber);
//        Intent notificationIntent = new Intent(context, SmsActivity.class);
//        ContactInfo contact = new ContactInfo();
//
//        if (MmsUtil.isEmailAddress(phoneNumber)) {
//            contact = getContactInfoForEmailAddress(context, phoneNumber);
//        } else if (MmsUtil.isAlphaNumber(phoneNumber)) {
//
//            ContactInfo info = getContactInfoForEmailAddress(context, phoneNumber);
//            if (info.contactId > 0) {
//                contact = info;
//            } else {
//                contact = getContactInfoForPhoneNumber(context, phoneNumber);
//            }
//
//        } else {
//            contact = getContactInfoForPhoneNumber(context, phoneNumber);
//        }
//        notificationIntent.putExtra("number", phoneNumber)
//                .putExtra("title", contact.contactName);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//
//        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        switch (audio.getRingerMode()) {
//            case AudioManager.RINGER_MODE_NORMAL:
//                break;
//            case AudioManager.RINGER_MODE_SILENT:
//                break;
//            case AudioManager.RINGER_MODE_VIBRATE:
//                break;
//        }
//
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if(Build.VERSION.SDK_INT >=26){
//            NotificationChannel mChannel = new NotificationChannel("hyuservicenotich","hyuservicenotich", NotificationManager.IMPORTANCE_DEFAULT);
//            mNotificationManager.createNotificationChannel(mChannel);
//            if (msg.length() < 40) {
//                NotificationCompat.Builder notification = new NotificationCompat.Builder(context,mChannel.getId())
//                        .setContentTitle(contact.contactName)
//                        .setContentText(msg)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true);
//                notification.setVibrate(new long[]{1000});
//                notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(1000);
//
//                mNotificationManager.notify(0, notification.build());
//
//            } else {
//
//
//                NotificationCompat.Builder notification = new NotificationCompat.Builder(context,mChannel.getId())
//                        .setContentTitle(contact.contactName)
//                        .setContentText(msg)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
//                        .setContentText(msg);
//
//                notification.setVibrate(new long[]{1000});
//                notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(1000);
//
//                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(notification);
//                style.bigText(msg).setBigContentTitle(contact.contactName);
//
//                // 요약 정보.
//                // 고유ID로 알림을 생성.
//                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                nm.notify(0, notification.build());
//
//            }
//        }else{
//            if (msg.length() < 40) {
//                NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
//                        .setContentTitle(contact.contactName)
//                        .setContentText(msg)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true);
//                notification.setVibrate(new long[]{1000});
//                notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(1000);
//
//                mNotificationManager.notify(0, notification.build());
//
//            } else {
//
//
//                NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
//                        .setContentTitle(contact.contactName)
//                        .setContentText(msg)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
//                        .setContentText(msg);
//
//                notification.setVibrate(new long[]{1000});
//                notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(1000);
//
//                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(notification);
//                style.bigText(msg).setBigContentTitle(contact.contactName);
//
//                // 요약 정보.
//                // 고유ID로 알림을 생성.
//                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                nm.notify(0, notification.build());
//
//            }
//        }
//
//    }

//    private ContactInfo getContactInfoForEmailAddress(Context context, String number) {
//
//        ContactInfo info = new ContactInfo();
//        info.phoneNumbers = number;
//        info.contactName = number;
//
//        Cursor cursor = context.getContentResolver().query(android.provider.ContactsContract.Data.CONTENT_URI,
//                new String[]{
//                        ContactsContract.CommonDataKinds.Email._ID,                    // 0
//                        ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,           // 1
//                        ContactsContract.CommonDataKinds.Email.CONTACT_PRESENCE,       // 2
//                        ContactsContract.CommonDataKinds.Email.CONTACT_ID,             // 3
//                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,           // 4
//                        ContactsContract.Contacts.SEND_TO_VOICEMAIL    // 5
//                }
//                , "UPPER(" + ContactsContract.CommonDataKinds.Email.DATA + ")=UPPER(?) AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'"
//                , new String[]{number}
//                , null
//        );
//
//        if (cursor != null) {
//
//            try {
//                while (cursor.moveToNext()) {
//
//                    // 아이디
//                    info.contactId = cursor.getLong(3);
//
//                    // 이름 추출
//                    String name = cursor.getString(1);
//                    if (StringUtil.isNullOrEmpty(name))
//                        name = cursor.getString(4);
//
//                    // 연락처로 사진 추출
//                    info.contactName = name;
//
//                }
//            } catch (Exception e) {
////        Log.d(TAG, e.getMessage(), e);
//            } finally {
//                cursor.close();
//            }
//        }
//
//        return info;
//    }

//    private ContactInfo getContactInfoForPhoneNumber(Context context, String number) {
//
//        boolean matched = false;
//
//        ContactInfo info = new ContactInfo();
//        info.phoneNumbers = number;
//        info.contactName = number;
//
//        String normalizedNumber = MmsUtil.normalizeNumber(number);
//        String minMatch = MmsUtil.toCallerIDMinMatch(normalizedNumber);
//
//        if (!TextUtils.isEmpty(normalizedNumber) && !TextUtils.isEmpty(minMatch)) {
//            String numberLen = String.valueOf(normalizedNumber.length());
//            String numberE164 = MmsUtil.formatNumberToE164(number, "KR");
//
//            String selection;
//            String[] args;
//            if (TextUtils.isEmpty(numberE164)) {
//                selection = CALLER_ID_SELECTION_WITHOUT_E164;
//                args = new String[]{minMatch, numberLen, normalizedNumber, numberLen};
//            } else {
//                selection = CALLER_ID_SELECTION;
//                args = new String[]{minMatch, numberE164, numberLen, normalizedNumber, numberLen};
//            }
//
//            Cursor cursor = context.getContentResolver().query(
//                    android.provider.ContactsContract.Data.CONTENT_URI,
//                    new String[]{
//                            ContactsContract.CommonDataKinds.Phone._ID,                      // 0
//                            ContactsContract.CommonDataKinds.Phone.NUMBER,                   // 1
//                            ContactsContract.CommonDataKinds.Phone.LABEL,                    // 2
//                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,             // 3
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,               // 4
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_PRESENCE,         // 5
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_STATUS,           // 6
//                            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,        // 7
//                            ContactsContract.Contacts.SEND_TO_VOICEMAIL      // 8
//                    },
//                    selection,
//                    args,
//                    null);
//
//            if (cursor == null) {
//                return info;
//            }
//
//
//            try {
//
//                if (cursor.moveToFirst()) {
//                    matched = true;
//
//                    info.contactName = cursor.getString(3);
//                    info.contactId = cursor.getLong(4);
//                    info.phoneNumbers = cursor.getString(7);
//
//                }
//            } catch (Exception e) {
////        Log.d(TAG, e.getMessage(), e);
//            } finally {
//                cursor.close();
//            }
//        }
//
//        if (!matched) {
//            // 기존 모듈로 재호출 하도록 변경
//            Cursor cursor = context.getContentResolver().query(
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
//                    ContactsContract.CommonDataKinds.Phone.NUMBER + " in ( ?, ? ) ",
//                    new String[]{number, normalizedNumber},
//                    null);
//
//
//            try {
//
//                if (cursor.moveToFirst()) {
//                    info.contactName = cursor.getString(1);
//                    info.contactId = cursor.getLong(0);
//
//                }
//            } catch (Exception e) {
////        Log.d(TAG, e.getMessage(), e);
//            } finally {
//                cursor.close();
//            }
//        }
//
//        return info;
//    }

}
