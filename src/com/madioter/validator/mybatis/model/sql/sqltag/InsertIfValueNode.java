package com.madioter.validator.mybatis.model.sql.sqltag;

import com.madioter.validator.mybatis.util.SqlHelperConstant;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.List;

/**
 * <Description> insert的值定义节点 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class InsertIfValueNode extends IfNode {

    /**
     * 异常信息
     */
    private static final String ERROR_MSG = "表达式为：%s";

    /**
     * 构造方法
     *
     * @param sqlNode if标签
     * @throws ConfigException 配置异常
     */
    public InsertIfValueNode(Object sqlNode) throws ConfigException {
        super(sqlNode);
    }

    /**
     * 自验证方法
     *
     * @param parameterType  参数类型
     * @throws MapperException 异常
     */
    public boolean validate(Class parameterType) throws MapperException {
        super.validate(parameterType);
        MapperException mapperException = null;
        if (getIfContent() == null) {
            throw new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, String.format(ERROR_MSG, getContents()));
        }
        if (!getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
            mapperException = new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, String.format(ERROR_MSG, getIfContent()));
        }
        if (!StringUtil.containBrace(getIfContent())) {
            //不包含赋值标记，表示赋值为固定值，不做验证
            return true;
        }
        List<String> propertyNames = StringUtil.extractBrace(getIfContent());
        String propertyName = propertyNames.get(0);
        if (propertyName.contains(SqlHelperConstant.JDBC_TYPE_TAG)) {
            propertyName = propertyName.substring(0, propertyName.indexOf(SqlHelperConstant.JDBC_TYPE_TAG));
        }

        boolean result = ReflectHelper.haveGetMethod(propertyName, parameterType);
        if (!result) {
            if (mapperException != null) {
                mapperException.appendMessage(ExceptionCommonConstant.COLUMN_NOT_EXIST);
            } else {
                mapperException = new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST, String.format(ERROR_MSG, getIfContent()));
            }
        }
        if (mapperException != null) {
            throw mapperException;
        } else {
            return true;
        }
    }
}
