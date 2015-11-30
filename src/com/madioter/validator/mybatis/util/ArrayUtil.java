package com.madioter.validator.mybatis.util;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月30日 <br>
 */
public class ArrayUtil {

    /**
     * 验证对象数组中是否存在obj对象
     * @param arr 数组
     * @param obj 对象
     * @return boolean
     */
    public static boolean contains(Object[] arr, Object obj) {
        if (obj == null) {
            return false;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(obj)) {
                return true;
            }
        }
        return false;
    }
}
