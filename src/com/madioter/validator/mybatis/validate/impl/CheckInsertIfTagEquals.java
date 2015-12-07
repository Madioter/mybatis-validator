package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.InsertMappedStatementItem;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfValueNode;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import com.madioter.validator.mybatis.validate.CheckFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> 检查Insert语句的赋值和结果判断条件一致 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class CheckInsertIfTagEquals extends AbstractValidator {

    /**
     * checkFilterList
     */
    private List<CheckFilter> checkFilterList = new ArrayList<CheckFilter>();

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        TableDao tableDao = connectionManager.getTableDao();
        MappedStatementItem item = statementResource.getNext();
        while (item != null) {
            if (item instanceof InsertMappedStatementItem) {
                try {
                    //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                    Method method = CheckInsertIfTagEquals.this.getClass().getMethod("validateIfTagEquals",
                            InsertMappedStatementItem.class);
                    getProxy().execute(method, item, tableDao);
                    item = statementResource.getNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 验证IF标签内的判断条件是否一致.
     * @author wangyi8
     * @taskId
     * @param item the item
     */
    public void validateIfTagEquals(InsertMappedStatementItem item) {
        String errMsg = item.getInfoMessage();
        if (item.getColumnSqlNode() != null || item.getValueSqlNode() != null) {
            try {
                checkTrimNodeProperty(item.getColumnSqlNode(), errMsg);
                checkTrimNodeProperty(item.getValueSqlNode(), errMsg);
            } catch (ConfigException e) {
                e.printStackTrace();
            }
            List<InsertIfColumnNode> ifColumnNodeList = item.getIfColumnNodeList();
            List<InsertIfValueNode> ifValueNodeList = item.getIfValueNodeList();
            if (ifColumnNodeList.size() != ifValueNodeList.size()) {
                new MapperException(ExceptionCommonConstant.INSERT_COLUMN_VALUE_ERROR,
                        errMsg).printException();
            } else {
                int len = ifColumnNodeList.size();
                for (int i = 0; i < len; i++) {
                    InsertIfColumnNode columnNode = ifColumnNodeList.get(i);
                    InsertIfValueNode valueNode = ifValueNodeList.get(i);
                    if (!StringUtil.replaceBlank(columnNode.getIfTest()).equals(StringUtil.replaceBlank(valueNode.getIfTest()))) {
                        new MapperException(ExceptionCommonConstant.INSERT_COLUMN_VALUE_SAME_TEST_ERROR,
                                errMsg + String.format(MessageConstant.IF_TEST_TEXT, columnNode.getIfTest(), valueNode.getIfTest())).printException();
                    }
                }
            }
        }
    }

    /**
     * 检查TrimSqlNode节点属性编写是否正确
     *
     * @param node 判断节点
     * @param errMsg 异常信息
     * @return false 存在错误
     * @throws ConfigException 配置异常
     */
    private boolean checkTrimNodeProperty(Object node, String errMsg) throws ConfigException {
        //判断prefix属性是否设置
        String prefix = ((String) ReflectHelper.getPropertyValue(node, "prefix")).trim();
        if (!prefix.equals(SymbolConstant.SYMBOL_LEFT_BRACKET) && !prefix.equals("values (")) {
            new MapperException(ExceptionCommonConstant.INSERT_TRIM_PREFIX_ERROR, errMsg).printException();
        }

        //判断suffix属性是否设置
        String suffix = (String) ReflectHelper.getPropertyValue(node, "suffix");
        if (!suffix.equals(SymbolConstant.SYMBOL_RIGHT_BRACKET)) {
            new MapperException(ExceptionCommonConstant.INSERT_TRIM_SUFFIX_ERROR, errMsg).printException();
        }

        //判断suffixesToOverride属性是否设置
        List suffixesToOverrides = (List) ReflectHelper.getPropertyValue(node, "suffixesToOverride");
        if (suffixesToOverrides != null && !suffixesToOverrides.isEmpty()) {
            String suffixesToOverride = (String) suffixesToOverrides.get(0);
            if (!suffixesToOverride.equals(",")) {
                new MapperException(ExceptionCommonConstant.INSERT_TRIM_SUFFIXES_TO_OVERRIDE_ERROR, errMsg).printException();
            }
        }
        return true;
    }
}
