package com.kj_project.whereareyou.utils;

import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class StringUtil {
    /**
     * 전화번호 format
     * @param phoneNumber
     * @return
     */
    public static String formattingPhoneNumber(String phoneNumber) {
        String result = phoneNumber;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                result = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
            } else {
                result = PhoneNumberUtils.formatNumber(phoneNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean isNullOrEmpty(String data) {
        return (data == null || data.length() == 0) ? true : false;
    }

    public static boolean isNullOrEmpty(Object data) {
        return isNullOrEmpty(data.toString());
    }

    /**
     * 문자열을 쪼개서 포멧형식에 맞게 패키징
     *
     * @param data  원본 문자열
     * @param split 분할 처리할 문자열
     * @return
     */
    public static String stringPacking(String data, String split) {
        return stringPacking(data, split, "'%s'", split);
    }

    /**
     * 문자열을 쪼개서 포멧형식에 맞게 패키징
     *
     * @param data   원본 문자열
     * @param split  분할 처리할 문자열
     * @param format 패키징할 포멧
     * @return
     */
    public static String stringPacking(String data, String split, String format) {
        return stringPacking(data, split, format, split);
    }

    /**
     * 문자열을 쪼개서 포멧형식에 맞게 패키징
     *
     * @param data      원본 문자열
     * @param split     분할 처리할 문자열
     * @param format    패키징할 포멧
     * @param seperator 패키징 사이에 구분할 문자열
     * @return
     */
    public static String stringPacking(String data, String split, String format, String seperator) {

        if (data == null)
            return "";

        StringBuffer sb = new StringBuffer();

        String[] arrays = data.split(split);

        if (arrays != null && arrays.length > 0) {
            for (int i = 0; i < arrays.length; i++) {

                if (sb.length() > 0)
                    sb.append(seperator);

                sb.append(String.format(format, arrays[i]));
            }
        }

        return sb.toString();
    }

    /**
     * 문자열을 쪼개서 포멧형식에 맞게 패키징
     *
     * @param arrays    원본 문자열
     * @param format    패키징할 포멧
     * @param seperator 패키징 사이에 구분할 문자열
     * @return
     */
    public static String stringPacking(List<String> arrays, String format, String seperator) {

        if (arrays == null)
            return "";

        StringBuffer sb = new StringBuffer();

        if (arrays != null && arrays.size() > 0) {
            for (int i = 0; i < arrays.size(); i++) {

                if (sb.length() > 0)
                    sb.append(seperator);

                sb.append(String.format(format, arrays.get(i)));
            }
        }

        return sb.toString();
    }

    /**
     * 배열을 문자열로 변환
     *
     * @param collection 배열
     * @param seperator  구분자
     * @return
     */
    public static String implode(Collection<?> collection, String seperator) {

        if (collection == null || collection.size() == 0)
            return "";

        StringBuffer sb = new StringBuffer();

        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (sb.length() > 0)
                sb.append(seperator);

            sb.append(iterator.next());
        }

        return sb.toString();
    }

    /**
     * +82전화번호 format
     * @param str
     * @return
     */
    public static String replacePhoneNum(String str) {
        String returnStr = "";
        if (str != null) {
            returnStr = str.replace("+82", "0");
        }
        return returnStr;
    }

    /**
     * 전화번호format2
     * @param phoneNumber
     * @return
     */
    public static String formattingPhoneNumber2(String phoneNumber) {
        String result = phoneNumber.replaceAll("-", "");
        ;
        return result;
    }

    /**
     * 전화번호format3
     * @param phoneNumber
     * @return
     */
    public static String formattingPhoneNumber3(String phoneNumber) {
        String result = phoneNumber.replaceAll("-", "");
        try {
            String subresult = result.substring(0, 3);
            if ("010".equals(subresult)) {
                phoneNumber = "+82" + result.substring(1);
            }

        } catch (Exception e) {
            phoneNumber = "";
        }
        return phoneNumber;
    }

    /**
     * 전화번호format4
     * @param phoneNumber
     * @return
     */
    public static String formattingPhoneNumber4(String phoneNumber) {
        String result = phoneNumber.replaceAll("-", "");
        try {
            String subresult = result.substring(0, 3);
            if ("010".equals(subresult)) {
                phoneNumber = "82" + result.substring(1);
            }

        } catch (Exception e) {
            phoneNumber = "";
        }
        return phoneNumber;
    }

    /**
     * 전화번호format5
     * @param phoneNumber
     * @return
     */
    public static String formattingPhoneNumber5(String phoneNumber) {
        String result = "";
        try {
            if ("+82".equals(phoneNumber.substring(0, 3))) {
                result = phoneNumber.substring(3);
                result = result.replaceAll(" ", "");
                result = "0" + result;
                phoneNumber = result;
            }

        } catch (Exception e) {
            phoneNumber = "";
        }
        return phoneNumber;
    }

    /**
     * 010시작하는 위치 반환
     *
     * @param phoneNumber
     * @return
     */
    public static int contactPostion(List<String> phoneNumber) {
        int position = 0;
        if (phoneNumber.size() > 0) {
            for (int i = 0; i < phoneNumber.size(); i++) {
                String result = "";
                try {
                    result = phoneNumber.get(i).substring(0, 3);
                } catch (Exception e) {
                    result = "";
                }
                if ("010".equals(result)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 010시작인지 체크
     *
     * @param phoneNumber
     * @return
     */
    public static boolean contactYn(String phoneNumber) {
        boolean result = false;
        try {
            if ("010".equals(phoneNumber.substring(0, 3))) {
                result = true;
            } else {
                result = false;
            }

        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static Date convertToDate(String date, String formatString) {
        Date cDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            cDate = format.parse(date);
        } catch (IllegalArgumentException e) {
            Log.d("logd", e.getMessage());
        } catch (ParseException e) {
            Log.d("logd", e.getMessage());
        }
        return cDate;
    }

    /**
     * 마스킹전화번호
     * @param pn
     * @return
     */
    public static String maskingPhoneNum(String pn) {
        String str1 = "";
        String str2 = "";
        String str3 = "";

        if( pn.length() == 11 ) {
            str1 = pn.substring(0, 3);
            str2 = "****";
            str3 = pn.substring(7);
            str2 = "****";
        } else if( pn.length() == 10 ) {
            str1 = pn.substring(0, 3);
            str2 = "***";
            str3 = pn.substring(6);
        }

        return str1 + "-" + str2 + "-" + str3;
    }

    public static int getByteEditText(String str) {
        if( !StringUtil.isNullOrEmpty(str) ) {
            try {
                return str.getBytes("UTF-8").length;
            } catch(UnsupportedEncodingException e) {
                Log.d("logd", e.toString());
            }
        }
        return 0;
    }

}
