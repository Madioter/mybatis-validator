package com.madioter.validator.mybatis.model.sql.sqltag;

import com.madioter.validator.mybatis.util.exception.ConfigException;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月27日 <br>
 */
public class SelectIfNode extends IfNode {
    /**
     * 构造方法
     *
     * @param sqlNode if标签
     * @throws ConfigException 配置异常
     */
    public SelectIfNode(Object sqlNode) throws ConfigException {
        super(sqlNode);
    }
}
