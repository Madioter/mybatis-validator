package com.madioter.validator.mybatis.config;

import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.util.ArrayUtil;
import com.madioter.validator.mybatis.util.Config;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class StatementResource {

    private Map<String, MappedStatementItem> mappedStatementMap;

    private Iterator<Map.Entry<String, MappedStatementItem>> itemIterator;

    public StatementResource(Collection<MappedStatement> mappedStatements) throws ConfigException {
        mappedStatementMap = new HashMap<String, MappedStatementItem>();
        Iterator iterator = mappedStatements.iterator();
        while (iterator.hasNext()) {
            Object temp = iterator.next();
            if (temp instanceof MappedStatement) {
                MappedStatement statement = (MappedStatement) temp;
                if (ArrayUtil.contains(Config.IGNORE_STATEMENT_MAPPER_ID, statement.getId())) {
                    continue;
                }
                if (!mappedStatementMap.containsKey(statement.getId())) {
                    if (statement.getSqlCommandType() == SqlCommandType.INSERT) {
                        mappedStatementMap.put(statement.getId(), new InsertMappedStatementItem(statement));
                    } else if (statement.getSqlCommandType() == SqlCommandType.UPDATE) {
                        mappedStatementMap.put(statement.getId(), new UpdateMappedStatementItem(statement));
                    } else if (statement.getSqlCommandType() == SqlCommandType.SELECT) {
                        mappedStatementMap.put(statement.getId(), new SelectMappedStatementItem(statement));
                    }
                }
            }
        }
        itemIterator = mappedStatementMap.entrySet().iterator();
    }

    public MappedStatementItem getNext() {
        if (itemIterator.hasNext()) {
            return itemIterator.next().getValue();
        }
        return null;
    }
}

