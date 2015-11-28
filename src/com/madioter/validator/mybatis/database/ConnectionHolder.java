package com.madioter.validator.mybatis.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <Description> 数据库连接类 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月11日 <br>
 */
public class ConnectionHolder {

    /**
     * 连接
     */
    private Connection con;

    /**
     * 驱动类
     */
    private String driverClass;

    /**
     * 数据库url
     */
    private String jdbcUrl;

    /**
     * 数据库用户名
     */
    private String user;

    /**
     * 数据库用户密码
     */
    private String password;

    /**
     * 构造方法
     * @param driverClass 驱动类
     * @param jdbcUrl 数据库url
     * @param user 数据库用户名
     * @param password 数据库用户密码
     */
    public ConnectionHolder(String driverClass, String jdbcUrl, String user, String password) {
        this.driverClass = driverClass;
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    /**
     * 获取数据库连接
     * @return Connection
     */
    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                createConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * 创建连接
     */
    private synchronized void createConnection() {
        try {
            Class.forName(driverClass);
            con = DriverManager.getConnection(jdbcUrl, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
