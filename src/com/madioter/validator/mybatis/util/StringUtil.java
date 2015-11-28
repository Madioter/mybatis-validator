package com.madioter.validator.mybatis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月13日 <br>
 */
public class StringUtil {

    /**
     * 首字母大写
     * @param str 字符串
     * @return String
     */
    public static String upperFirst(String str) {
        return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
    }

    /**
     * 获取属性的get方法名
     * @param propertyName 属性名
     * @return String
     */
    public static String getMethodName(String propertyName) {
        return "get" + upperFirst(propertyName);
    }

    /**
     * 获取属性的set方法名
     * @param propertyName 属性名
     * @return String
     */
    public static String setMethodName(String propertyName) {
        return "set" + upperFirst(propertyName);
    }

    /**
     * 清除字符串中的空白字符
     * @param text 文本
     * @return String
     */
    public static String replaceBlank(String text) {
        String dest = "";
        if (text != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(text);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 按空白字符进行分割字符串
     * @param text 文本
     * @return String[]
     */
    public static String[] splitWithBlank(String text) {
        return text.trim().split("\\s+|\t|\r|\n");
    }

    /**
     * 字符数组转换为列表
     * @param arr 字符数组
     * @return List<String>
     */
    public static List<String> arrayToList(String[] arr) {
        List<String> stringList = new ArrayList<String>();
        if (arr == null || arr.length == 0) {
            return stringList;
        }
        for (int i = 0; i < arr.length; i++) {
            stringList.add(arr[i]);
        }
        return stringList;
    }
}
