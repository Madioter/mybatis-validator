package com.madioter.validator.mybatis.database;

import com.madioter.validator.mybatis.model.database.Column;
import com.madioter.validator.mybatis.model.database.Table;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月13日 <br>
 */
public class ColumnDao {

    /**
     * connectionHolder
     */
    private ConnectionHolder connectionHolder;

    /**
     * tableSchema
     */
    private String tableSchema;

    /**
     * connectionManager
     */
    private ConnectionManager connectionManager;

    /**
     * 构造方法
     * @param connectionManager connectionManager
     * @param connectionHolder connectionHolder
     * @param tableSchema tableSchema
     */
    public ColumnDao(ConnectionManager connectionManager, ConnectionHolder connectionHolder, String tableSchema) {
        this.connectionManager = connectionManager;
        this.connectionHolder = connectionHolder;
        this.tableSchema = tableSchema;
    }

    /**
     * 验证表字段是否存在
     * @param tableName 表名
     * @param column 字段名
     * @return Boolean 是否存在
     */
    public boolean checkColumnExist(String column, String tableName) {
        DataBaseCache dataBaseCache = this.connectionManager.getDataBaseCache();
        Table table = new Table(tableName, tableSchema);
        table = dataBaseCache.getTable(table);
        if (table == null) {
            return false;
        } else {
            for (Column item : table.getColumnList()) {
                if (item.getColumnName().equals(column)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 验证表字段是否存在
     * @param tableName 表名
     * @param column 字段名
     * @return Boolean 是否存在
     */
    @Deprecated
    public boolean checkColumnExist1(String column, String tableName) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        //去除带有schema的表名
        if (tableName.contains(SymbolConstant.SYMBOL_POINT) && tableSchema != null &&
                tableName.substring(0, tableName.indexOf(SymbolConstant.SYMBOL_POINT)).equals(tableSchema)) {
            tableName = tableName.substring(tableName.indexOf(SymbolConstant.SYMBOL_POINT) + 1);
        }

        try {
            Connection con = connectionHolder.getConnection();
            StringBuilder sql = new StringBuilder("SELECT count(1) FROM COLUMNS WHERE TABLE_NAME = ? and COLUMN_NAME = ?\n");
            List<Object> params = new ArrayList<Object>();
            params.add(tableName);
            params.add(column);
            if (tableSchema != null && !tableSchema.equals("")) {
                sql.append("and TABLE_SCHEMA = ?");
                params.add(tableSchema);
            }
            pstmt = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt, resultSet);
        }
        return false;
    }

    /**
     * 关闭资源
     * @param pstmt pstmt
     * @param resultSet resultSet
     */

    public void close(PreparedStatement pstmt, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过表查询表中的全部字段
     * @param table 表定义
     * @return
     */
    public List<Column> selectColumnsByTable(Table table) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        String tableName = table.getTableName();
        String schema = table.getTableSchema() == null ? tableSchema : table.getTableSchema();
        //去除带有schema的表名
        if (tableName.contains(SymbolConstant.SYMBOL_POINT) && tableSchema != null &&
                tableName.substring(0, tableName.indexOf(SymbolConstant.SYMBOL_POINT)).equals(tableSchema)) {
            tableName = tableName.substring(tableName.indexOf(SymbolConstant.SYMBOL_POINT) + 1);
        }
        List<Column> columnList = new ArrayList<Column>();
        try {
            Connection con = connectionHolder.getConnection();
            StringBuilder sql = new StringBuilder("SELECT COLUMN_NAME,COLUMN_DEFAULT,IS_NULLABLE,DATA_TYPE,\n" +
                    " COLUMN_KEY,EXTRA,COLUMN_COMMENT FROM COLUMNS WHERE TABLE_NAME = ? \n");
            List<Object> params = new ArrayList<Object>();
            params.add(tableName);
            if (schema != null && !StringUtil.isBlank(schema)) {
                sql.append("and TABLE_SCHEMA = ?");
                params.add(schema);
            }
            pstmt = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                Column column = new Column();
                column.setColumnName(resultSet.getString(1));
                column.setColumnDefault(resultSet.getString(2));
                column.setIsNullAble(resultSet.getString(3));
                column.setDataType(resultSet.getString(4));
                column.setColumnKey(resultSet.getString(5));
                column.setExtra(resultSet.getString(6));
                column.setColumnComment(resultSet.getString(7));
                columnList.add(column);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(pstmt, resultSet);
        }
        return columnList;
    }
}
