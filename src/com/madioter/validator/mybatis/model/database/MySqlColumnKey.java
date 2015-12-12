package com.madioter.validator.mybatis.model.database;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月12日 <br>
 */
public enum MySqlColumnKey {

    /**
     * 键类型
     */
    MUL, PRI, UNI;

    /**
     * 获取枚举值
     * @param columnKey 获取枚举值
     * @return
     */
    public static MySqlColumnKey getType(String columnKey) {
        for (MySqlColumnKey type : MySqlColumnKey.values()) {
            if (type.toString().equals(columnKey.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
}
