package com.madioter.validator.mybatis.database;

import com.madioter.validator.mybatis.model.database.Column;
import com.madioter.validator.mybatis.model.database.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description>数据库信息缓存，缓存查询到的数据表和数据字段 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月12日 <br>
 */
public class DataBaseCache {

    /**
     * 表缓存
     */
    private List<Table> tableList = new ArrayList<Table>();

    /**
     * connectionManager
     */
    private ConnectionManager connectionManager;

    /**
     * Instantiates a new Data base cache.
     *
     * @param connectionManager the connection manager
     */
    public DataBaseCache(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * 获取表信息
     * @param table 表定义
     * @return
     */
    public Table getTable(Table table) {
        if (table == null || table.getTableName() == null) {
            return null;
        }
        for (int i = 0; i < tableList.size(); i++) {
            if (tableList.get(i).equals(table)) {
                return tableList.get(i);
            }
        }
        boolean exist = connectionManager.getTableDao().checkExistInner(table.getTableName());
        if (exist) {
            List<Column> columns = connectionManager.getColumnDao().selectColumnsByTable(table);
            table.setColumnList(columns);
            tableList.add(table);
            return table;
        } else {
            return null;
        }
    }
}
