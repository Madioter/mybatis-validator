package com.madioter.validator.mybatis.config.statement.insert;


import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月21日 <br>
 */
public interface InsertStatementParser {

    /**
     * statement解析器
     * @param mappedStatement 元数据
     * @param statementItem insert对象
     * @throws ConfigException 配置异常
     */
    void parser(MappedStatement mappedStatement,InsertMappedStatementItem statementItem) throws ConfigException;
}
