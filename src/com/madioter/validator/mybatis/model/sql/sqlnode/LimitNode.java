package com.madioter.validator.mybatis.model.sql.sqlnode;

import com.madioter.validator.mybatis.model.sql.elementnode.SelectElement;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SqlConstant;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.List;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月30日 <br>
 */
public class LimitNode implements SelectElement {

    /**
     * 异常表达式
     */
    private static final String SQL_EXPRESS_TEXT = "表达式: %s";

    /**
     * 起始行变量名
     */
    private String startParam;

    /**
     * 结束行变量名
     */
    private String endParam;

    /**
     * 分页部分构造函数
     *
     * @param limitText  List<String>
     */
    public LimitNode(List<String> limitText) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limitText.size(); i++) {
            builder.append(limitText.get(i));
        }
        String[] strArr = builder.toString().split(",");
        if (strArr.length > 0) {
            startParam = strArr[0].trim();
        }
        if (strArr.length > 1) {
            endParam = strArr[1].trim();
        }
    }

    /**
     * Gets start param.
     * @return start param
     */
    public String getStartParam() {
        return startParam;
    }

    /**
     * Sets start param.
     * @param startParam the start param
     */
    public void setStartParam(String startParam) {
        this.startParam = startParam;
    }

    /**
     * Gets end param.
     * @return the end param
     */
    public String getEndParam() {
        return endParam;
    }

    /**
     * Sets end param.
     * @param endParam the end param
     */
    public void setEndParam(String endParam) {
        this.endParam = endParam;
    }

    /**
     * 自验证方法
     * @param parameterType 参数类型
     * @param errMsg 异常信息
     */
    public void validate(Class parameterType, String errMsg) {
        checkPropertyExist(startParam, parameterType, errMsg);
        if (endParam != null) {
            checkPropertyExist(endParam, parameterType, errMsg);
        }
    }

    /**
     * 验证属性是否存在
     * @param value 属性定义
     * @param clz 入参类型
     * @param errMsg 异常信息
     */
    private void checkPropertyExist(String value, Class clz, String errMsg) {
        if (value.contains(SymbolConstant.SYMBOL_LEFT_BRACE)) {
            if (clz == null || clz.equals(Map.class)) {
                return;
            } else {
                String[] str = value.split("\\#\\{");
                for (int i = 0; i < str.length; i++) {
                    if (str[i].contains(SymbolConstant.SYMBOL_RIGHT_BRACE)) {
                        String propertyName = str[i].substring(0, str[i].indexOf(SymbolConstant.SYMBOL_RIGHT_BRACE));
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
    }

    @Override
    public String toString() {
        return SqlConstant.LIMIT + SymbolConstant.SYMBOL_BLANK + startParam + SymbolConstant.SYMBOL_COMMA + endParam;
    }

    @Override
    public void rebuild() {
        return;
    }

    @Override
    public Boolean getIsComplete() {
        return true;
    }
}
