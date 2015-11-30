package com.madioter.validator.mybatis;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.ResultMapResource;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.ClassModel;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月11日 <br>
 */
public class Validator {

    /**
     * 配置管理器
     */
    private ConfigurationManager configurationManager;

    /**
     * 数据连接管理
     */
    private ConnectionManager connectionManager;

    /**
     * driverClass
     */
    private String driverClass;

    /**
     * jdbcUrl
     */
    private String jdbcUrl;

    /**
     * user
     */
    private String user;

    /**
     * password
     */
    private String password;

    /**
     * table_schema
     */
    private String tableSchema;

    /**
     * spring配置文件传入参数
     * @param sqlSessionFactory mybatis配置
     */
    public void setSqlSessionFactory(DefaultSqlSessionFactory sqlSessionFactory) {
        try {
            configurationManager = new ConfigurationManager(sqlSessionFactory.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行验证方法
     * @author wangyi8
     * @taskId
     */
    public void validator() {
        connectionManager = new ConnectionManager(driverClass, jdbcUrl, user, password, tableSchema);
        resultMapTagValidator();

        try {
            statementValidator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证statement
     * @throws ConfigException 异常
     */
    private void statementValidator() throws ConfigException {
        StatementResource statementResource = configurationManager.getStatementResource();
        MappedStatementItem item = statementResource.getNext();
        while (item != null) {
            item.validate(connectionManager);
            item = statementResource.getNext();
        }
    }

    /**
     * resultMap标签验证
     */
    private void resultMapTagValidator() {
        ResultMapResource resultMapResource = configurationManager.getResultMapResource();
        ClassModel item = resultMapResource.getNext();
        while (item != null) {
            item.validate();
            item = resultMapResource.getNext();
        }
    }

    /**
     * Sets user.
     * @param user the user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Sets password.
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets jdbc url.
     * @param jdbcUrl the jdbc url
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Sets driver class.
     * @param driverClass the driver class
     */
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    /**
     * Sets table schema.
     * @param tableSchema the table schema
     */
    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }
}
