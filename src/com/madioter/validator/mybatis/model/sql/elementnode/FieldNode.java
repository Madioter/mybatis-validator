package com.madioter.validator.mybatis.model.sql.elementnode;

import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月16日 <br>
 */
public class FieldNode implements SelectElement {

    /**
     * 赋值字段名
     */
    private String columnName;

    /**
     * 赋值表达式
     */
    private String express;

    /**
     * Instantiates a new Field node.
     */
    public FieldNode() {

    }

    /**
     * Instantiates a new Field node.
     *
     * @param ex the ex
     */
    public FieldNode(String ex) {
        this.express = ex;
        rebuild();
    }

    /**
     * Gets column name.
     * @return the column name
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
     * Gets express.
     * @return the express
     */
    public String getExpress() {
        return express;
    }

    /**
     * Sets express.
     * @param express the express
     */
    public void setExpress(String express) {
        this.express = express;
    }

    @Override
    public void rebuild() {
        String ex = "";
        if (StringUtil.isBlank(this.columnName) || StringUtil.isBlank(this.express)) {
            if (StringUtil.isBlank(this.columnName)) {
                ex = this.express;
            }
            if (StringUtil.isBlank(this.express)) {
                ex = this.columnName;
            }
        }
        //去除末尾的逗号
        if (ex.trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
            ex = ex.trim().substring(0, ex.trim().length() - 1);
        }
        if (StringUtil.isBlank(ex) || !ex.contains(SymbolConstant.SYMBOL_EQUAL)) {
            return;
        }

        int index = ex.indexOf(SymbolConstant.SYMBOL_EQUAL);
        this.columnName = ex.substring(0, index);
        this.express = ex.substring(index + 1);
    }
}
