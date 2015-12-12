package com.madioter.validator.mybatis.model.database;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月12日 <br>
 */
public enum MySqlDataType {

    /**
     * 日期类型
     */
    DATE, DATETIME, TIME, TIMESTAMP,

    /**
     * 字符类型
     */
    VARCHAR, CHAR, TEXT, MEDIUMTEXT, LONGTEXT,

    /**
     * 数值类型
     */
    INT, TINYINT, SMALLINT, BIGINT, DECIMAL, DOUBLE,

    /**
     * 其他类型
     */
    SET, LONGBLOB, ENUM, BLOB;

    /**
     * 获取类型枚举
     * @param dataType 类型字符串
     * @return
     */
    public static MySqlDataType getType(String dataType) {
        for (MySqlDataType type : MySqlDataType.values()) {
            if (type.toString().equals(dataType.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
}
