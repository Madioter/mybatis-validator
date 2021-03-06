package com.madioter.validator.mybatis.database;

import com.madioter.validator.mybatis.model.database.Table;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月13日 <br>
 */
public class TableDao {

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
    public TableDao(ConnectionManager connectionManager, ConnectionHolder connectionHolder, String tableSchema) {
        this.connectionManager = connectionManager;
        this.connectionHolder = connectionHolder;
        this.tableSchema = tableSchema;
    }

    /**
     * 验证表是否存在
     *
     * @param tableName 表名
     * @return boolean
     */
    public boolean checkExist(String tableName) {
        DataBaseCache dataBaseCache = connectionManager.getDataBaseCache();
        Table table = new Table(tableName, tableSchema);
        if (dataBaseCache.getTable(table) != null) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 通过表名获取表配置
     * @param tableName the table name
     * @return the table
     */
    public Table getTable(String tableName) {
        DataBaseCache dataBaseCache = connectionManager.getDataBaseCache();
        Table table = new Table(tableName, tableSchema);
        return dataBaseCache.getTable(table);
    }

    /**
     * 验证表是否存在
     *
     * @param tableName 表名
     * @return boolean
     */
    public boolean checkExistInner(String tableName) {
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        //去除带有schema的表名
        if (tableName.contains(SymbolConstant.SYMBOL_POINT) && tableSchema != null &&
                tableName.substring(0, tableName.indexOf(SymbolConstant.SYMBOL_POINT)).equals(tableSchema)) {
            tableName = tableName.substring(tableName.indexOf(SymbolConstant.SYMBOL_POINT) + 1);
        }

        try {
            Connection con = connectionHolder.getConnection();
            StringBuilder sql = new StringBuilder("SELECT count(1) FROM TABLES WHERE TABLE_NAME = ?\n");
            List<Object> params = new ArrayList<Object>();
            params.add(tableName);
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
}
