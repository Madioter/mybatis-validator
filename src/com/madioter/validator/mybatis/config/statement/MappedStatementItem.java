package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.mybatis.SqlSourceVo;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.parser.sqlparser.SqlSourceParser;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

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
     * sql节点
     */
    private List<ISqlComponent> sqlComponentList;

    /**
     * sql语句
     */
    private String sql;

    /**
     * Instantiates a new Mapped statement item.
     *
     * @param mappedStatement the mapped statement
     * @throws ConfigException 异常
     */
    public MappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        this.mappedStatement = mappedStatement;
        SqlSource sqlSource = mappedStatement.getSqlSource();
        SqlSourceVo sqlSourceVo = SqlSourceParser.parser(sqlSource);
        initParameter(sqlSourceVo);
    }

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
     * Gets sql component list.
     * @return the sql component list
     */
    public List<ISqlComponent> getSqlComponentList() {
        return sqlComponentList;
    }

    /**
     * Gets sql.
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * Sets sql.
     * @param sql the sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Init parameter.
     * @author wangyi8
     * @taskId
     * @param sqlSourceVo the sql source vo
     */
    public void initParameter(SqlSourceVo sqlSourceVo) {
        this.sql = sqlSourceVo.getSql();
        this.sqlComponentList = sqlSourceVo.getComponentList();
    }
}
