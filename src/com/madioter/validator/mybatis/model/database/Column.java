package com.madioter.validator.mybatis.model.database;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月12日 <br>
 */
public class Column {

    /**
     * 对应的表
     */
    private Table table;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 是否可为空
     */
    private IsNullAble isNullAble;

    /**
     * 数据类型
     */
    private MySqlDataType dataType;

    /**
     * 字段默认值
     */
    private String columnDefault;

    /**
     * 字段键
     */
    private MySqlColumnKey columnKey;

    /**
     * 扩展信息，例如：auto_increment
     */
    private String extra;

    /**
     * 字段备注
     */
    private String columnComment;

    /**
     * Gets table.
     * @return the table
     */
    public Table getTable() {
        return table;
    }

    /**
     * Sets table.
     * @param table the table
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Gets column name.
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets column name.
     * @param columnName the column name
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets is null able.
     * @return the is null able
     */
    public IsNullAble getIsNullAble() {
        return isNullAble;
    }

    /**
     * Sets is null able.
     * @param isNullAble the is null able
     */
    public void setIsNullAble(String isNullAble) {
        this.isNullAble = IsNullAble.getType(isNullAble);
    }

    /**
     * Gets data type.
     * @return the data type
     */
    public MySqlDataType getDataType() {
        return dataType;
    }

    /**
     * Sets data type.
     * @param dataType the data type
     */
    public void setDataType(String dataType) {
        this.dataType = MySqlDataType.getType(dataType);
    }

    /**
     * Gets column default.
     * @return the column default
     */
    public String getColumnDefault() {
        return columnDefault;
    }

    /**
     * Sets column default.
     * @param columnDefault the column default
     */
    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    /**
     * Gets column key.
     * @return the column key
     */
    public MySqlColumnKey getColumnKey() {
        return columnKey;
    }

    /**
     * Sets column key.
     * @param columnKey the column key
     */
    public void setColumnKey(String columnKey) {
        this.columnKey = MySqlColumnKey.getType(columnKey);
    }

    /**
     * Gets extra.
     * @return the extra
     */
    public String getExtra() {
        return extra;
    }

    /**
     * Sets extra.
     * @param extra the extra
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * Gets column comment.
     * @return the column comment
     */
    public String getColumnComment() {
        return columnComment;
    }

    /**
     * Sets column comment.
     * @param columnComment the column comment
     */
    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
