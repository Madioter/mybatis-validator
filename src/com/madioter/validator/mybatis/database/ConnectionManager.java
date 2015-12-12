package com.madioter.validator.mybatis.database;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class ConnectionManager {

    /**
     * connectionHolder
     */
    private ConnectionHolder connectionHolder;

    /**
     * tableDao
     */
    private TableDao tableDao;

    /**
     * columnDao
     */
    private ColumnDao columnDao;

    /**
     * dataBaseCache
     */
    private DataBaseCache dataBaseCache;

    /**
     * 构造方法
     * @param driverClass 驱动类
     * @param jdbcUrl 数据库url
     * @param user 数据库用户名
     * @param password 数据库用户密码
     * @param tableSchema 表空间
     */
    public ConnectionManager(String driverClass, String jdbcUrl, String user, String password, String tableSchema) {
        connectionHolder = new ConnectionHolder(driverClass, jdbcUrl, user, password);
        tableDao = new TableDao(this, connectionHolder, tableSchema);
        columnDao = new ColumnDao(this, connectionHolder, tableSchema);
        dataBaseCache = new DataBaseCache(this);
    }

    /**
     * 获取表操作类
     * @return TableDao
     */
    public TableDao getTableDao() {
        return tableDao;
    }

    /**
     * 获取字段操作类
     * @return ColumnDao
     */
    public ColumnDao getColumnDao() {
        return columnDao;
    }

    /**
     * 获取缓存对象
     * @return DataBaseCache
     */
    public DataBaseCache getDataBaseCache() {
        return dataBaseCache;
    }
}
