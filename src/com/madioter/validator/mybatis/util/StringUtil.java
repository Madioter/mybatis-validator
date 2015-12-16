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

    /**
     * 是否含有括号
     * @param express 字符串
     * @return Boolean
     */
    public static boolean containBracket(String express) {
        return express.contains(SymbolConstant.SYMBOL_LEFT_BRACKET) && express.contains(SymbolConstant.SYMBOL_RIGHT_BRACKET);
    }

    /**
     * 是否含有大括号
     * @param express 字符串
     * @return Boolean
     */
    public static boolean containBrace(String express) {
        return express.contains(SymbolConstant.SYMBOL_LEFT_BRACE) && express.contains(SymbolConstant.SYMBOL_RIGHT_BRACE);
    }

    /**
     * 提取小括号内部的内容，括号支持嵌套
     * @param express 字符串
     * @return List<String>
     */
    public static List<String> extractBracket(String express) {
        List<String> result = new ArrayList<String>();
        List<String> fragments = new ArrayList<String>();
        String[] strArr = express.split(SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_LEFT_BRACKET);
        int length = strArr.length;
        String lastTemp = "";
        for (int i = length - 1; i > 0; i--) {
            if (lastTemp.trim().equals("")) {
                lastTemp = strArr[i];
            } else {
                lastTemp = strArr[i] + lastTemp;
            }
            if (lastTemp.contains(SymbolConstant.SYMBOL_RIGHT_BRACKET)) {
                String temp = lastTemp.substring(0, lastTemp.indexOf(SymbolConstant.SYMBOL_RIGHT_BRACKET));
                if (temp.equals("")) {
                    lastTemp = lastTemp.replaceFirst(SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_RIGHT_BRACKET,
                            SqlHelperConstant.FRAGMENT_BLANK_TAG);
                } else {
                    fragments.add(temp);
                    lastTemp = SqlHelperConstant.FRAGMENT_TAG + (fragments.size() - 1) + SymbolConstant.SYMBOL_AT + lastTemp.substring(temp.length() + 1);
                }
            }
        }

        for (int i = 0; i < fragments.size(); i++) {
            String temp = fragments.get(i);
            //把相应的变量进行替换
            for (int k = i; k >= 0; k--) {
                if (temp.contains(SqlHelperConstant.FRAGMENT_TAG + k + SymbolConstant.SYMBOL_AT)) {
                    temp = temp.replace(SqlHelperConstant.FRAGMENT_TAG + k + SymbolConstant.SYMBOL_AT,
                            SymbolConstant.SYMBOL_LEFT_BRACKET + fragments.get(k) + SymbolConstant.SYMBOL_RIGHT_BRACKET);
                }
            }
            temp = temp.replace(SqlHelperConstant.FRAGMENT_BLANK_TAG,
                    SymbolConstant.SYMBOL_LEFT_BRACKET + SymbolConstant.SYMBOL_RIGHT_BRACKET);
            result.add(temp);
        }
        return result;
    }

    /**
     * 提取大括号内部的内容，这里SymbolConstant.SYMBOL_LEFT_BRACE}结构不支持嵌套
     * @param express 字符串
     * @return List<String>
     */
    public static List<String> extractBrace(String express) {
        List<String> result = new ArrayList<String>();
        String[] str = express.split("\\#\\{");
        for (int i = 0; i < str.length; i++) {
            if (str[i].contains(SymbolConstant.SYMBOL_RIGHT_BRACE)) {
                String propertyName = str[i].substring(0, str[i].indexOf(SymbolConstant.SYMBOL_RIGHT_BRACE));
                result.add(propertyName);
            }
        }
        return result;
    }

    /**
     * 字符串改小写，除了#{}符号内的字符串
     * @param express 字符串
     * @return
     */
    public static String toLowerCaseExceptBrace(String express) {
        if (containBrace(express)) {
            List<String> list = extractBrace(express);
            express = express.toLowerCase();
            for (int i = 0; i < list.size(); i++) {
                express = express.replace(list.get(i).toLowerCase(), list.get(i));
            }
            return express;
        } else {
            return express.toLowerCase();
        }
    }


    /**
     * 获取字符串中 匹配正则的字符串列表
     * @author wangyi8
     * @taskId
     * @param str the str 字符串
     * @param regex the regex 正则
     * @return the list 字符串列表
     */
    public static List<String> matchesRegex(String str, String regex) {
        List<String> result = new ArrayList<String>();
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    /**
     * 判断字符串是否有值
     * @param str 字符串
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().equals("");
    }
}
