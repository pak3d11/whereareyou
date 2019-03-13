package com.kj_project.whereareyou.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.BaseColumns._ID;
import static android.provider.Telephony.TextBasedSmsColumns.ADDRESS;

public class SmsUtil {
    /**
     * Email_Address pattern check
     */
    private static final Pattern EMAIL_ADDRESS_PATTERN
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private static final Pattern NAME_ADDR_EMAIL_PATTERN =
            Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");

    /**
     * 문자메시지 삭제
     * @param context
     * @param phoneNumber
     */
    public static void deleteSMS(Context context, String phoneNumber) {
//    context = HueServiceApp.get().getApplicationContext();


        String queryPhoneNumber = phoneNumber;
        queryPhoneNumber = queryPhoneNumber.replaceAll("-", "");

        String[] phoneNumberQuery = new String[]{queryPhoneNumber};

        Cursor cursorInbox = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, ADDRESS + "=?", phoneNumberQuery, null);

        while (cursorInbox.moveToNext()) {
            try {
                String pid = cursorInbox.getString(cursorInbox.getColumnIndex(_ID));
                String uri = "content://sms/" + pid;
                context.getContentResolver().delete(Uri.parse(uri), null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Cursor cursorSent = context.getContentResolver().query(Uri.parse("content://sms/sent"), null, ADDRESS + "=?", phoneNumberQuery, null);
        while (cursorSent.moveToNext()) {
            try {
                String pid = cursorSent.getString(cursorSent.getColumnIndex(_ID));
                String uri = "content://sms/" + pid;
                context.getContentResolver().delete(Uri.parse(uri), null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursorSent.close();

        Cursor cursorMMS = context.getContentResolver().query(Uri.parse("content://mms/inbox"), null, null, null, null);
        while (cursorMMS.moveToNext()) {
            int mmsId = cursorMMS.getInt(cursorMMS.getColumnIndex("_id"));
            String sel = "msg_id=" + mmsId;

            String uriString = MessageFormat.format("content://mms/{0,number,#}/addr", mmsId);
            Uri uri = Uri.parse(uriString);
            Cursor c2 = context.getContentResolver().query(uri, null, sel, null, null);

            if (c2 != null) {
                while (c2.moveToNext()) {
                    String address = c2.getString(c2.getColumnIndex("address"));
                    if (queryPhoneNumber.equals(address)) {
                        try {
                            String mmsUri = "content://mms/" + mmsId;
                            context.getContentResolver().delete(Uri.parse(mmsUri), null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                c2.close();
            }

        }
        cursorMMS.close();

        Cursor cursorMMSSent = context.getContentResolver().query(Uri.parse("content://mms/sent"), null, null, null, null);
        while (cursorMMSSent.moveToNext()) {
            int mmsId = cursorMMSSent.getInt(cursorMMSSent.getColumnIndex("_id"));
            String sel = "msg_id=" + mmsId;

            String uriString = MessageFormat.format("content://mms/{0,number,#}/addr", mmsId);
            Uri uri = Uri.parse(uriString);
            Cursor c = context.getContentResolver().query(uri, null, sel, null, null);
            while (c != null && c.moveToNext()) {
                String address = c.getString(c.getColumnIndex("address"));
                if (queryPhoneNumber.equals(address)) {
                    try {
                        String mmsUri = "content://mms/" + mmsId;
                        context.getContentResolver().delete(Uri.parse(mmsUri), null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            c.close();
        }
        cursorMMSSent.close();
    }

    /**
     * threadId로 전화번호 검색
     * @param context
     * @param number
     * @return
     */
    public static long getThreadIdFromNumber(Context context, String number) {
        String[] phoneNumber = new String[]{number}; //the wanted phone number
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"}, "address=?", phoneNumber, null);

        long threadId = 0;
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToLast();
                threadId = cursor.getLong(cursor.getColumnIndex("thread_id"));
            }

        } catch (Exception e) {
            Log.d("getThreadIdFromNumber","sms1");
        }

        cursor.close();

        if (threadId == 0) {
            Cursor sendCursor = context.getContentResolver().query(Uri.parse("content://sms/sent"), new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"}, "address=?", phoneNumber, null);

            try {
                if (sendCursor.getCount() > 0) {
                    sendCursor.moveToLast();
                    threadId = sendCursor.getLong(sendCursor.getColumnIndex("thread_id"));
                }

            }catch (Exception e){
                Log.d("getThreadIdFromNumber","sms2");
            }

            sendCursor.close();
        }
        if(threadId == 0){
            threadId = getThreadId(context, number);

        }
        return threadId;
    }

    /**
     * ThreadId반환
     * @param context
     * @param recipient
     * @return
     */
    public static long getThreadId(Context context, String recipient) {
        Set<String> recipients = new HashSet<String>();

        recipients.add(recipient);
        return getThreadId(context, recipients);
    }

    /**
     * ThreadId반환
     * @param context
     * @param recipients
     * @return
     */
    public static long getThreadId(
            Context context, Set<String> recipients) {
        Uri.Builder uriBuilder = Uri.parse("content://mms-sms/threadID").buildUpon();

        for (String recipient : recipients) {
            if (isEmailAddress(recipient)) {
                recipient = extractAddrSpec(recipient);
            }

            uriBuilder.appendQueryParameter("recipient", recipient);
        }

        Uri uri = uriBuilder.build();
        Cursor cursor = com.kj.kr.whereareyou.whereareyou.service.util_alt.SqliteWrapper.query(context, context.getContentResolver(),
                uri, new String[]{"_id"}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(0);
                    cursor.close();
                    return id;
                } else {

                }
            } finally {
                cursor.close();
            }
        }

        return -1;
    }

    /**
     * email주소 여부
     * @param address
     * @return
     */
    private static boolean isEmailAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }

        String s = extractAddrSpec(address);
        Matcher match = EMAIL_ADDRESS_PATTERN.matcher(s);
        return match.matches();
    }
    private static String extractAddrSpec(String address) {
        Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);

        if (match.matches()) {
            return match.group(2);
        }
        return address;
    }
}

