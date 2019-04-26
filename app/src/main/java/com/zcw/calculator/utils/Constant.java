package com.zcw.calculator.utils;

/**
 * 常量类
 * Created by ASUS on 2016/5/9.
 */
public class Constant {

//    /** 匹配数字的正则表达式（包括整数和小数） */
//    public static final String NUMBER_REGEX = "\\-?[0-9]+\\.?[0-9]*";

    /** 匹配数字的正则表达式（包括整数和小数） */
    public static final String NUMBER_REGEX = "\\－?[0-9,]+\\.?[0-9]*E?\\-?[0-9]*";

    /** 用于匹配运算符的正则
     * <br />一目运算
     * <br />log、ln
     */
    public static final String OPERATOR_REGEX1 = "[abchijklm]";

    /** 用于匹配运算符的正则
     * <br />二目运算
     * <br />+、-、*、/
     */
    public static final String OPERATOR_REGEX2 = "[\\+\\-\\*\\/d]";

    /** 用于匹配运算符的正则
     * <br/>此加减乘除符号，不能直接用于运算，需要转换成+-/*
     * <br />二目运算
     * <br />＋、－、×、÷
     */
    public static final String OPERATOR_REGEX2S = "[＋－×÷]";
}
