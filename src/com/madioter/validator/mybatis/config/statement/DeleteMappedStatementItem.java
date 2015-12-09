package com.madioter.validator.mybatis.config.statement;

import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.parser.statementparser.insert.BaseInsertStatementParser;
import com.madioter.validator.mybatis.parser.statementparser.insert.BatchInsertStatementParser;
import com.madioter.validator.mybatis.parser.statementparser.insert.InsertStatementParser;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.List;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <Description> 删除语句 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class DeleteMappedStatementItem extends MappedStatementItem {

    /**
     * 构造方法
     * @param mappedStatement 原数据
     * @throws ConfigException 配置异常
     */
    public DeleteMappedStatementItem(MappedStatement mappedStatement) throws ConfigException {
        super.setMappedStatement(mappedStatement);
        //TODO 待补充
    }

    @Override
    public void validate(ConnectionManager connectionManager) throws ConfigException {

    }

    @Override
    public List<TableNode> getTableNodes() {
        return null;
    }

    @Override
    public void addTableNode(TableNode tableNode) {

    }
}