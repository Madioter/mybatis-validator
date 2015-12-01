package com.madioter.validator.mybatis.config.parser;

import com.madioter.validator.mybatis.util.exception.ConfigException;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public interface ISqlSourceType {

    /**
     * 判断类是否匹配
     * @param object 对象
     * @return 是否匹配
     */
    public boolean matches(Object object);

    /**
     * 对DynamicSqlSource对象进行解析
     * @param sqlSource DynamicSqlSourceParser
     * @return SqlSourceVo
     * @throws ConfigException 异常
     */
    public SqlSourceVo parser(SqlSource sqlSource) throws ConfigException;
}
