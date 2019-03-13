package com.kj_project.whereareyou.utils;

import android.content.Context;
import android.util.Log;

/**
 * 기타 유틸 모아놓는 클래스
 */
public class KJUtil {

    private Context utilCon = null;

    public KJUtil(Context utilCon){
        if (this.utilCon == null) this.utilCon = utilCon;
    }

    /**
     * 로그 태그로 필터링해서 보는용
     * @param logKind
     * @param logMsg
     */
    public void kjLog(String logKind, String logMsg) {
        String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        switch (logKind) {
            case "e":
                Log.e("kj", "[" + className + "." + methodName + "():" + lineNumber + "]" + logMsg);
                break;
            case "d":
                Log.d("kj", "[" + className + "." + methodName + "():" + lineNumber + "]" + logMsg);
                break;
            case "i":
                Log.i("kj", "[" + className + "." + methodName + "():" + lineNumber + "]" + logMsg);
                break;
            case "w":
                Log.w("kj", "[" + className + "." + methodName + "():" + lineNumber + "]" + logMsg);
                break;
            case "v":
                Log.v("kj", "[" + className + "." + methodName + "():" + lineNumber + "]" + logMsg);
                break;
        }

    }
}
