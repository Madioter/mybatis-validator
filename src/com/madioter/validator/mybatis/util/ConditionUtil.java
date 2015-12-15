package com.madioter.validator.mybatis.util;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月15日 <br>
 */
public class ConditionUtil {

    /**
     * 互斥条件判断
     * @author wangyi8
     * @taskId
     * @param ifTest the if test
     * @param test the test
     * @return the boolean
     */
    public static boolean mutuallyExclusive(List<String> ifTest, String test) {
        //目前只做简单的互斥限制 == value 和 != value . >value 和 <= value , >= value 和 < value
        if (test.contains("==")) {

        }
        return true;
    }

    /**
     * 是否存在非空值判断
     * @param test 判断表达式
     * @return boolean 存在非空判断
     */
    public static boolean containNotNullCheck(String test) {
        //做简单的空值判断 TODO 不严谨，有待改进
        if (test.contains("null!=") || test.contains("!=null")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否存在空值判断
     * @param test 判断表达式
     * @return boolean 存在空值判断
     */
    public static boolean containNullCheck(String test) {
        //做简单的空值判断 TODO 不严谨，有待改进
        test = StringUtil.replaceBlank(test);
        if (test.contains("null==") || test.contains("==null")) {
            return true;
        } else {
            return false;
        }
    }
}
