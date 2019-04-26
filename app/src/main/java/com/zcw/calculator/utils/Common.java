package com.zcw.calculator.utils;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 公共方法类
 * Created by ASUS on 2016/5/16.
 */
public class Common {

    private static Toast toast = null;

    /**
     * 匹配字符串中的最后一个字符
     * @param regex 要匹配的正则
     * @param string 要匹配的字符串
     * @return 如果字符串中的最后一个字符，能用正则表达式匹配上，返回true；否则返回false。
     */
    public static boolean matchLastChar(String regex, String string) {
        int index = string.length() - 1;
        char ch = index >= 0 ? string.charAt(index) : ' ';

        if(Pattern.matches(regex, String.valueOf(ch)))
            return true;
        else
            return false;
    }

    /**
     * 避免Toast重复显示
     * @param context
     * @param resId 要显示的字符串Id
     */
    public static void ToastShow(Context context, int resId){
        if (toast == null) {
            toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }

    /**
     * 避免Toast重复显示
     * @param context
     * @param message 要显示的字符串
     */
    public static void ToastShow(Context context, String message){
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 获取当前时间
     * @param format 时间格式，如“yyyy-MM-dd HH:mm:ss”
     * @return
     */
    public static String getCurrentTime(String format){
        SimpleDateFormat formatter = new SimpleDateFormat (format);
        Date curDate = new Date(System.currentTimeMillis());    //获取当前时间
        String result = formatter.format(curDate);
        return result;
    }
}
