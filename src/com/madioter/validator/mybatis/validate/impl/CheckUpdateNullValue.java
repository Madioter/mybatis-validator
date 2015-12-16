package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.UpdateMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.model.database.Column;
import com.madioter.validator.mybatis.model.database.IsNullAble;
import com.madioter.validator.mybatis.model.database.Table;
import com.madioter.validator.mybatis.model.sql.elementnode.FieldNode;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.UpdateNode;
import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.IfSqlComponent;
import com.madioter.validator.mybatis.util.ClassUtil;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SqlHelperConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月16日 <br>
 */
public class CheckUpdateNullValue extends AbstractValidator {


    /**
     * SELECT_TAG
     */
    private static final String SELECT_TAG = "@select#";

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        TableDao tableDao = connectionManager.getTableDao();
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            if (item instanceof UpdateMappedStatementItem) {
                try {
                    //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                    Method method = CheckUpdateNullValue.this.getClass().getMethod("validateNullValue",
                            UpdateMappedStatementItem.class, TableDao.class);
                    getProxy().execute(this, method, item, tableDao);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Validate null value.
     * @author wangyi8
     * @taskId 0.6
     * @param item the item
     * @param tableDao tableDao
     */
    public void validateNullValue(UpdateMappedStatementItem item, TableDao tableDao) {
        //insert语句对应的表是否存在必填无默认值字段，并且这些字段在赋值语句中是否存在
        UpdateNode updateNode = item.getUpdateNode();
        TableNode tableNode = updateNode.getTableNode();
        if (tableNode == null || StringUtil.isBlank(tableNode.getTableName())) {
            new MapperException(ExceptionCommonConstant.TABLE_NAME_MISS, item.getInfoMessage()).printException();
            return;
        }
        Table table = tableDao.getTable(tableNode.getTableName());
        //获取当前表不允许为空的字段
        List<Column> nullAbleColumns = new ArrayList<Column>();
        for (Column column : table.getColumnList()) {
            if (column.getIsNullAble() == IsNullAble.NO) {
                nullAbleColumns.add(column);
            }
        }

        //获取所有的赋值字段
        List<FieldNode> fieldNodes = item.getUpdateNode().getSetNode().getFieldNodeList();

        //判断赋值字段是否是非空字段
        loop:
        for (FieldNode fieldNode : fieldNodes) {
            for (Column column : nullAbleColumns) {
                if (fieldNode.getColumnName().equals(column.getColumnName())) {
                    //判断赋值是否可能为空值
                    if (StringUtil.containBrace(fieldNode.getExpress())) {
                        // 判断参数属性是否存在，如果存在判断是否为非对象类型，例如int不会为空值
                        Class clz = item.getParameterType();
                        List<String> propertyNames = StringUtil.extractBrace(fieldNode.getExpress());
                        String propertyName = propertyNames.get(0);
                        if (propertyName.contains(SqlHelperConstant.JDBC_TYPE_TAG)) {
                            propertyName = propertyName.substring(0, propertyName.indexOf(SqlHelperConstant.JDBC_TYPE_TAG));
                        }
                        if (checkIsBasicType(clz, propertyName, item.getInfoMessage())) {
                            continue loop;
                        }
                        //进行判断IF条件是否存在
                        checkIfTest(column, propertyName, item);
                        continue loop;
                    } else if (fieldNode.getExpress().contains(SELECT_TAG)) {
                        // TODO 以sql语句进行的赋值，待处理
                        continue loop;
                    } else {
                        // 认为赋值为固定值，不做判断
                        continue loop;
                    }
                }
            }
        }
    }

    /**
     * 验证是否为基础类型
     * @param clz 入参类型
     * @param propertyName 属性名
     * @param message 异常信息提示
     * @return 基础类型返回true
     */
    private boolean checkIsBasicType(Class clz, String propertyName, String message) {
        if (!ClassUtil.ignorePropertyCheck(clz)) {
            try {
                Class returnType = ReflectHelper.getReturnType(propertyName, clz);
                // 判断属性是否为基础数据类型
                if (ClassUtil.basicType(returnType)) {
                    return true;
                }
            } catch (MapperException e) {
                //获取get方法失败
                e.setDescription(message + SymbolConstant.SYMBOL_BLANK + e.getDescription());
                e.printException();
            }
        }
        return false;
    }

    /**
     * 验证是否存在IF条件判断
     * @param column 列信息
     * @param propertyName 属性名
     * @param item 原语句
     */
    private void checkIfTest(Column column, String propertyName, UpdateMappedStatementItem item) {
        List<ISqlComponent> sqlComponents = item.getSqlComponentList();
        boolean flag = checkExpressExist(sqlComponents, propertyName);
        if (!flag) {
            new MapperException(ExceptionCommonConstant.UPDATE_NULL_COLUMN_PROBABLY, item.getInfoMessage()
                    + String.format(MessageConstant.COLUMN_NAME, column.getColumnName())).printException();
        }
    }

    /**
     * 递归检查所有的节点是否存在判空赋默认值的节点
     * @param sqlComponents 当前语句的节点信息
     * @param express 表达式
     * @return boolean
     */
    private boolean checkExpressExist(List<ISqlComponent> sqlComponents, String express) {
        if (sqlComponents == null) {
            return false;
        }
        for (int i = 0; i < sqlComponents.size(); i++) {
            if (sqlComponents.get(i) instanceof IfSqlComponent) {
                String test = StringUtil.replaceBlank(((IfSqlComponent) sqlComponents.get(i)).getTest());
                if (test.contains("null!=" + express) || test.contains(express + "!=null")) {
                    return true;
                }
            } else {
                List<ISqlComponent> sqlComponentList = sqlComponents.get(i).getSubComponents();
                if (sqlComponentList != null) {
                    boolean flag = checkExpressExist(sqlComponentList, express);
                    if (flag) {
                        return flag;
                    }
                }

            }
        }
        return false;
    }
}
