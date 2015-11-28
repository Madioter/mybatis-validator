package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.util.exception.ConfigException;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月20日 <br>
 */
public abstract class MappedStatementItem {

    /**
     * 自验证方法
     *
     * @param connectionManager 数据库连接管理器
     * @throws ConfigException 配置异常
     */
    public abstract void validate(ConnectionManager connectionManager) throws ConfigException;
}
