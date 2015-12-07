package com.madioter.validator.mybatis.validate;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.database.ConnectionManager;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public interface IDoValidate {

    /**
     * 执行验证方法
     * @param configurationManager xml解析后的配置信息
     * @param connectionManager 数据库连接信息
     */
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager);

}
