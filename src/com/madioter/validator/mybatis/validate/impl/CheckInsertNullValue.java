package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.config.ConfigurationManager;
import com.madioter.validator.mybatis.config.StatementResource;
import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.config.statement.impl.InsertMappedStatementItem;
import com.madioter.validator.mybatis.database.ConnectionManager;
import com.madioter.validator.mybatis.database.TableDao;
import com.madioter.validator.mybatis.model.database.Column;
import com.madioter.validator.mybatis.model.database.IsNullAble;
import com.madioter.validator.mybatis.model.database.Table;
import com.madioter.validator.mybatis.model.sql.elementnode.FunctionNode;
import com.madioter.validator.mybatis.model.sql.elementnode.QueryNode;
import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.model.sql.elementnode.TableNode;
import com.madioter.validator.mybatis.model.sql.elementnode.ValueNode;
import com.madioter.validator.mybatis.model.sql.sqlnode.InsertNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.IfSqlComponent;
import com.madioter.validator.mybatis.util.ClassUtil;
import com.madioter.validator.mybatis.util.ConditionUtil;
import com.madioter.validator.mybatis.util.MessageConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
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
 * <Description> 检查Insert语句赋空值导致数据存储异常问题 <br>
 * 错误场景：
 * <insert id="batchSave" useGeneratedKeys="true" >
 *  INSERT INTO inq_requirement_vendor_date (
 *   basic_unit_adult_cost,
 *   basic_unit_child_cost)
 *  VALUES
 *   <foreach collection="list" item="item" index="index" separator=",">
 *      (#{item.basicUnitAdultCost},
 *       #{item.basicUnitChildCost})
 *   </foreach>
 * </insert>
 * 错误描述：
 *  basic_unit_adult_cost字段设置了不可为空，有默认值，但这个语句会造成空值风险。（还需要进一步排除属性存在默认值的情况）
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId 0.6 <br>
 * @CreateDate 2015年12月12日 <br>
 */
public class CheckInsertNullValue extends AbstractValidator {

    @Override
    public void validate(ConfigurationManager configurationManager, ConnectionManager connectionManager) {
        StatementResource statementResource = configurationManager.getStatementResource();
        TableDao tableDao = connectionManager.getTableDao();
        Map<String, MappedStatementItem> itemMap = statementResource.getMappedStatementMap();
        Set<String> itemKeys = itemMap.keySet();

        for (String itemKey : itemKeys) {
            MappedStatementItem item = itemMap.get(itemKey);
            if (item instanceof InsertMappedStatementItem) {
                try {
                    //中间增加一层动态代理类，通过传入Method 动态调用方法，并在其中增加过滤验证
                    Method method = CheckInsertNullValue.this.getClass().getMethod("validateNullValue",
                            InsertMappedStatementItem.class, TableDao.class);
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
    public void validateNullValue(InsertMappedStatementItem item, TableDao tableDao) {
        //insert语句对应的表是否存在必填无默认值字段，并且这些字段在赋值语句中是否存在
        TableNode tableNode = item.getTableNode();
        Table table = tableDao.getTable(tableNode.getTableName());
        InsertNode insertNode = item.getInsertNode();
        //获取当前表不允许为空的字段
        List<Column> nullAbleColumns = new ArrayList<Column>();
        for (Column column : table.getColumnList()) {
            if (column.getIsNullAble() == IsNullAble.NO) {
                nullAbleColumns.add(column);
            }
        }
        //获取insert的字段及赋值定义
        List<QueryNode> columnList = new ArrayList<QueryNode>();
        List<SelectElement> valueList = new ArrayList<SelectElement>();
        List<SelectElement> columnNodeList = insertNode.getColumnNode().getSelectElementList();
        List<SelectElement> valueNodeList = insertNode.getValueNode().getSelectElementList();
        for (int i = 0; i < columnNodeList.size(); i++) {
            if (columnNodeList.get(i) instanceof QueryNode) {
                columnList.add((QueryNode) columnNodeList.get(i));
                //如果赋值数小于字段数，这里默认给空
                if (valueNodeList.size() > i) {
                    valueList.add(valueNodeList.get(i));
                } else {
                    valueList.add(null);
                }
            }
        }
        loop:
        for (int i = 0; i < nullAbleColumns.size(); i++) {
            for (int k = 0; k < columnList.size(); k++) {
                //字段不允许为空，并且字段存在赋值，判断column有无空值判断，以及value赋值有无空值处理
                if (columnList.get(k).getColumnName().equals(nullAbleColumns.get(i).getColumnName())) {
                    //如果赋值不存在，抛出异常
                    if (valueList.get(k) == null) {
                        new MapperException(ExceptionCommonConstant.INSERT_COLUMN_MISS_VALUE, item.getInfoMessage()
                                + String.format(MessageConstant.COLUMN_NAME, nullAbleColumns.get(i).getColumnName())).printException();
                        continue loop;
                    }
                    String valueName = "";
                    //获取赋值表达式
                    if (valueList.get(k) instanceof ValueNode) {
                        //如果是#{value}赋值方式，获取ValueNode对象的值
                        valueName = ((ValueNode) valueList.get(k)).getValue();
                    } else if (valueList.get(k) instanceof FunctionNode) {
                        //如果是constant(#{value})类似的函数表达式，获取函数表达式
                        valueName = ((FunctionNode) valueList.get(k)).getExpress();
                    }
                    // 如果不含有#{} 结构，认定为默认值，例如 0，now() 等
                    if (!StringUtil.containBrace(valueName)) {
                        continue loop;
                    }
                    // 判断参数属性是否存在，如果存在判断是否为非对象类型，例如int不会为空值
                    Class clz = item.getParameterType();
                    // 如果参数为int，Map，List等类型，跳过判断
                    if (!ClassUtil.ignorePropertyCheck(clz)) {
                        List<String> propertyName = StringUtil.extractBrace(valueName);
                        try {
                            Class returnType = ReflectHelper.getReturnType(propertyName.get(0), clz);
                            // 判断属性是否为基础数据类型
                            if (ClassUtil.basicType(returnType)) {
                                continue loop;
                            }
                        } catch (MapperException e) {
                            //获取get方法失败
                            e.setDescription(item.getInfoMessage() + SymbolConstant.SYMBOL_BLANK + e.getDescription());
                            e.printException();
                        }
                    }

                    //进行判断IF条件是否存在
                    checkIfTest(nullAbleColumns.get(i), columnList.get(k).getColumnName(), valueName, item);
                    continue loop;
                }
            }
            //  循环结束，字段不存在赋值，判断是否存在默认值或自动生成方式，不存在时抛出异常
            checkColumnHaveDefaultValue(nullAbleColumns.get(i), item.getInfoMessage());
        }
    }

    /**
     * 判断是否存在列默认值
     * @param column 列定义
     * @param message 当前分支信息
     */
    private void checkColumnHaveDefaultValue(Column column, String message) {
        String defaultValue = column.getColumnDefault();
        String extra = column.getExtra();
        if (StringUtil.isBlank(defaultValue) && StringUtil.isBlank(extra)) {
            new MapperException(ExceptionCommonConstant.INSERT_NULL_COLUMN_MISS, message
                    + String.format(MessageConstant.COLUMN_NAME, column.getColumnName())).printException();
        }
    }

    /**
     * 判断字段是否存在if标签，并且是否进行了非空判断
     * @param column 类定义
     * @param columnName 字段名
     * @param valueName 对应的value赋值
     * @param item InsertMappedStatementItem
     */
    private void checkIfTest(Column column, String columnName, String valueName, InsertMappedStatementItem item) {
        // 如果是标准的insert表达式，存在Trim的column和value，判断是否存在非空判断
        if (!item.getIfColumnNodeList().isEmpty() && !item.getIfValueNodeList().isEmpty()) {
            List<InsertIfColumnNode> ifColumnNodeList = item.getIfColumnNodeList();
            for (int i = 0; i < ifColumnNodeList.size(); i++) {
                // 获取if标签信息
                String ifContent = ifColumnNodeList.get(i).getIfContent();
                String test = ifColumnNodeList.get(i).getIfTest();

                //去除末尾的逗号
                if (ifContent.endsWith(SymbolConstant.SYMBOL_COMMA)) {
                    ifContent = ifContent.substring(0, ifContent.length() - 1);
                }

                //当字段匹配是进行判断
                if (ifContent.equals(columnName)) {
                    if (!ConditionUtil.containNotNullCheck(test)) {
                        //TODO 不严谨，value赋值存在判空赋默认值的情况：例如
                        /*
                         * <if test="1==1">
                         *     column1,
                         * </if>
                         *
                         * <if test="#{value} == null">
                         *     0,
                         * </if>
                         * <if test="#{value} > 0">
                         *     #{value},
                         * </if>
                         * <if test="#{value} <= 0">
                         *      0,
                         * </if>
                         *
                         * 这里存在两个问题：
                         * 1、if条件互斥如何进行判断
                         * 2、Column字段和value赋值是一对多关系，如何设置关系
                         * 3、Column字段的条件判断和value的条件判断不一致，在另一项检查中这里会报错
                         */
                        new MapperException(ExceptionCommonConstant.INSERT_NULL_COLUMN_MISS, item.getInfoMessage()
                                + String.format(MessageConstant.COLUMN_NAME, columnName)).printException();
                    } else {
                        //如果存在空值验证，意味着空值时，不进行插入，判断是否存在默认值
                        checkColumnHaveDefaultValue(column, item.getInfoMessage());
                    }
                }
            }
        } else if (item.getInsertNode().getValueNode() != null) {
            // batch的情况 没有Trim标签，只存在foreach标签，先获取固定值
            List<String> valueExpresses = StringUtil.extractBrace(valueName);
            List<ISqlComponent> sqlComponents = item.getSqlComponentList();

            // 递归判断是否存在if标签做空值判断并赋默认值，不存在抛出异常
            boolean flag = checkExpressExist(sqlComponents, valueExpresses.get(0));
            if (!flag) {
                new MapperException(ExceptionCommonConstant.INSERT_NULL_COLUMN_PROBABLY, item.getInfoMessage()
                        + String.format(MessageConstant.COLUMN_NAME, columnName)).printException();
            }

        } else if (item.getInsertNode().getSqlNodeList() != null) {
            // insert into table （columns） select columns from table 的情况

        }

    }

    /**
     * 递归检查所有的节点是否存在判空赋默认值的节点
     * @param sqlComponents 当前语句的节点信息
     * @param express 表达式
     * @return boolean
     */
    private boolean checkExpressExist(List<ISqlComponent> sqlComponents, String express) {
        for (int i = 0; i < sqlComponents.size(); i++) {
            if (sqlComponents.get(i) instanceof IfSqlComponent) {
                String test = StringUtil.replaceBlank(((IfSqlComponent) sqlComponents.get(i)).getTest());
                if (test.contains("null==" + express) || test.contains(express + "==null")) {
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
