package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.ResultMapResource;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.SelectMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.model.java.ClassModel;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * <Description> ���<select> ��Ӧ�������Ƿ���ȷ (mybatis�����������ﲻ��ִ��)<br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016��01��18�� <br>
 */
@Deprecated
public class CheckSelectResultSet extends CheckStatementPropertyExist {

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            if (item instanceof SelectMappedStatementItem) {
                try {
                    //�м�����һ�㶯̬�����࣬ͨ������Method ��̬���÷����������������ӹ�����֤
                    Method method = CheckSelectResultSet.this.getClass().getMethod("validateResultSet",
                            SelectMappedStatementItem.class);
                    getProxy().execute(this, method, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void validateResultSet(SelectMappedStatementItem item) {
        System.out.println(1);
    }

}
