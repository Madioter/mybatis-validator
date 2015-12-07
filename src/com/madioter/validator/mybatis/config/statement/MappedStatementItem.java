package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月20日 <br>
 */
public abstract class MappedStatementItem {


    /**
     * 原数据
     */
    private MappedStatement mappedStatement;

    /**
     * 自验证方法
     *
     * @author wangyi8
     * @taskId
     * @param connectionManager 数据库连接管理器
     * @throws ConfigException 配置异常
     */
    public abstract void validate(ConnectionManager connectionManager) throws ConfigException;

    /**
     * 获取表节点
     * @return the table nodes
     */
    public abstract List<TableNode> getTableNodes();

    /**
     * 获取statement的ID
     * @return the id
     */
    public String getId() {
        return mappedStatement.getId();
    }

    /**
     * Gets resource.
     * @return the resource
     */
    public String getResource() {
        return mappedStatement.getResource();
    }


    /**
     * Get info message string.
     * @author wangyi8
     * @taskId
     * @return the string
     */
    public String getInfoMessage() {
        return String.format(MessageConstant.MAPPER_FILE_ID, this.getResource(), this.getId());
    }

    /**
     * Gets mapped statement.
     * @return mapped statement
     */
    public MappedStatement getMappedStatement() {
        return mappedStatement;
    }

    /**
     * Sets mapped statement.
     * @param mappedStatement the mapped statement
     */
    protected void setMappedStatement(MappedStatement mappedStatement) {
        this.mappedStatement = mappedStatement;
    }
}
