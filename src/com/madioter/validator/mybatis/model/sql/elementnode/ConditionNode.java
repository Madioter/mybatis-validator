package com.madioter.validator.mybatis.model.sql.elementnode;

import com.madioter.validator.mybatis.config.statement.SelectMappedStatementItem;
import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.util.ArrayUtil;
import com.madioter.validator.mybatis.util.Config;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.SqlUtil;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <Description> where条件部分<br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class ConditionNode implements SelectElement {

    /**
     * 异常表达式
     */
    private static final String SQL_EXPRESS_TEXT = "表达式: %s";

    /**
     * 条件字段
     */
    private String columnName = "";

    /**
     * 条件类型
     */
    private String conditionType = "";

    /**
     * 条件值
     */
    private String value = "";

    /**
     * Gets column name.
     * @return column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets column name.
     * @param columnName the column name
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Gets condition type.
     * @return the condition type
     */
    public String getConditionType() {
        return conditionType;
    }

    /**
     * Sets condition type.
     * @param conditionType the condition type
     */
    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    /**
     * Gets value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 结构重构方法
     */
    public void rebuild() {
        if (this.conditionType.equals("") || this.value.equals("")) {
            String express = columnName.trim() + SymbolConstant.SYMBOL_BLANK + conditionType.trim() + SymbolConstant.SYMBOL_BLANK + value.trim();
            String[] str = express.split("(=|!=|<=|<|>|>=|<>|\\sin\\s|\\sis\\s|\\sexists\\s)");
            List<String> removeBlank = new ArrayList<String>();
            for (int i = 0; i < str.length; i++) {
                if (!str[i].trim().isEmpty()) {
                    removeBlank.add(str[i].trim());
                }
            }
            if (removeBlank.size() == 2) {
                this.columnName = removeBlank.get(0).trim();
                this.value = removeBlank.get(1).trim();
                //两个字符串存在包含关系，先替换较大的字符串
                if (removeBlank.get(0).contains(removeBlank.get(1))) {
                    this.conditionType = express.replace(removeBlank.get(0), "").replace(removeBlank.get(1), "").trim();
                } else {
                    this.conditionType = express.replace(removeBlank.get(1), "").replace(removeBlank.get(0), "").trim();
                }
            }
        }

        //区分哪部分属于value，哪部分属于columnName
        if (!SqlUtil.checkIsColumn(this.columnName)) {
            String temp = this.value;
            this.value = this.columnName;
            this.columnName = temp;
        }
    }

    @Override
    public String toString() {
        return this.columnName + SymbolConstant.SYMBOL_BLANK + this.conditionType + SymbolConstant.SYMBOL_BLANK + this.value;
    }

    /**
     * 自验证方法
     * @param aliasTable 表信息
     * @param columnDao 字段查询Dao
     * @param clz paramType输入参数类型
     * @param errMsg 异常信息
     */
    public void validate(Map<String, TableNode> aliasTable, ColumnDao columnDao, Class clz, String errMsg) {
        rebuild();

        // 排除exists和not exists的情况, 内部sql会单独做验证，这里不做
        if (this.columnName.equals(SqlConstant.EXISTS) || this.conditionType.equals(SqlConstant.EXISTS)) {
            return;
        }
        //验证是否符合字段条件，存在类似 1=1 的恒等比较，需要过滤掉
        if (SqlUtil.checkIsColumn(this.columnName)) {
            checkColumnExist(this.columnName, aliasTable, columnDao, errMsg);
        }
        // 验证两表关联的比较
        if (SqlUtil.checkIsColumn(this.value)) {
            checkColumnExist(this.value, aliasTable, columnDao, errMsg);
        }

        if (StringUtil.containBrace(this.value)) {
            if (clz == null || ArrayUtil.contains(Config.IGNORE_PARAMETER_TYPES, clz)) {
                return;
            } else {
                List<String> propertyNames = StringUtil.extractBrace(value);
                for (String propertyName : propertyNames) {
                    if (conditionType.equals("in") && (propertyName.equals("item") || propertyName.startsWith("item."))) {
                        // foreach 条件在foreach标签中判断，这里不判断
                        return;
                    }
                    try {
                        ReflectHelper.haveGetMethod(propertyName, clz);
                    } catch (MapperException e) {
                        e.setDescription(errMsg + String.format(SQL_EXPRESS_TEXT, this.toString()) + e.getDescription());
                        e.printException();
                    }
                }
            }
        }

    }

    /**
     * 验证表字段是否存在
     *
     * @param express 需要验证的表达式
     * @param aliasTable 表信息
     * @param columnDao 字段查询dao
     * @param errMsg 异常信息
     */
    private void checkColumnExist(String express, Map<String, TableNode> aliasTable, ColumnDao columnDao, String errMsg) {
        String[] strArr = express.split("\\" + SymbolConstant.SYMBOL_POINT);
        TableNode curTableNode = null;
        String curColumnName = null;
        if (strArr.length > 1) {
            curTableNode = aliasTable.get(strArr[0]);
            curColumnName = strArr[1];
        } else if (aliasTable.size() == 1) {
            Iterator<TableNode> tableNodeIterator = aliasTable.values().iterator();
            curTableNode = tableNodeIterator.next();
            curColumnName = strArr[0];
        } else if (aliasTable.containsKey(SelectMappedStatementItem.CURRENT_TABLE)) {
            curTableNode = aliasTable.get(SelectMappedStatementItem.CURRENT_TABLE);
            curColumnName = strArr[0];
        }
        if (curTableNode == null) {
            new MapperException(ExceptionCommonConstant.TABLE_ALIAS_IS_NULL,
                    errMsg + String.format(SQL_EXPRESS_TEXT, this.toString())).printException();
        } else if (curTableNode.isCanCheck()) {
            boolean exist = columnDao.checkColumnExist(curColumnName, curTableNode.getTableName());
            if (!exist) {
                new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST,
                        errMsg + String.format(SQL_EXPRESS_TEXT, this.toString())).printException();
            }
        }
    }
}
