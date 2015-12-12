package com.madioter.validator.mybatis.model.database;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月12日 <br>
 */
public enum IsNullAble {

    /**
     * 是否可以为空
     */
    NO, YES;

    /**
     * 获取枚举值
     * @param isNullAble 获取枚举值
     * @return
     */
    public static IsNullAble getType(String isNullAble) {
        for (IsNullAble type : IsNullAble.values()) {
            if (type.toString().equals(isNullAble.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
}
