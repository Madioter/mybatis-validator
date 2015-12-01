package com.madioter.validator.mybatis.config.tagnode;

import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月21日 <br>
 */
public class UpdateIfSetNode extends IfNode {

    /**
     * 表字段名
     */
    private static final String TABLE_COLUMN_NAME = "表名：%s, 字段名：%s";

    /**
     * 字符表达式
     */
    private static final String ERROR_MSG = "表达式为：%s";

    /**
     * 构造方法
     *
     * @param sqlNode if标签
     * @throws ConfigException 配置异常
     */
    public UpdateIfSetNode(Object sqlNode) throws ConfigException {
        super(sqlNode);
    }

    /**
     * 自验证方法
     *
     * @param columnDao 验证工具服务
     * @param tableName 表名
     * @param parameterType 参数类型
     * @return boolean
     * @throws MapperException 发生对应关系异常
     */
    public boolean validate(ColumnDao columnDao, String tableName, Class parameterType) throws MapperException {
        super.validate(parameterType);

        MapperException mapperException = null;
        if (getIfContent() == null) {
            throw new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, String.format(ERROR_MSG, getContents()));
        }
        if (!getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
            mapperException = new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, String.format(ERROR_MSG, getIfContent()));
        }
        String text = StringUtil.replaceBlank(getIfContent().replace(SymbolConstant.SYMBOL_COMMA, ""));
        String[] strArr = text.split(SymbolConstant.SYMBOL_EQUAL);
        if (strArr.length < 2) {
            throw new MapperException(ExceptionCommonConstant.CAN_NOT_EXPLAIN_ERROR, String.format(ERROR_MSG, getIfContent()));
        }
        String columnName = strArr[0];
        String propertyName = strArr[1].replace(SymbolConstant.SYMBOL_LEFT_BRACE, "").replace(SymbolConstant.SYMBOL_RIGHT_BRACE, "").trim();
        //验证字段是否存在
        boolean result = columnDao.checkColumnExist(columnName, tableName);
        if (!result) {
            if (mapperException != null) {
                mapperException.appendMessage(ExceptionCommonConstant.COLUMN_NOT_EXIST
                        + String.format(TABLE_COLUMN_NAME, tableName, columnName));
            } else {
                mapperException = new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST
                        + String.format(TABLE_COLUMN_NAME, tableName, columnName), String.format(ERROR_MSG, getIfContent()));
            }
        }

        //验证属性是否存在
        result = ReflectHelper.haveGetMethod(propertyName, parameterType);
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

    /**
     * 验证字段是否存在
     *
     * @param columnDao 验证工具服务
     * @param tableName 表名
     * @return boolean
     * @throws MapperException 发生对应关系异常
     */
    public boolean validateColumn(ColumnDao columnDao, String tableName) throws MapperException {
        MapperException mapperException = null;
        if (getIfContent() == null) {
            throw new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, String.format(ERROR_MSG, getContents()));
        }
        //判断表达式是否以逗号结尾
        if (!getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
            mapperException = new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, String.format(ERROR_MSG, getIfContent()));
        }
        String text = StringUtil.replaceBlank(getIfContent().replace(SymbolConstant.SYMBOL_COMMA, ""));
        String[] strArr = text.split(SymbolConstant.SYMBOL_EQUAL);
        //解析和分离
        if (strArr.length < 2) {
            throw new MapperException(ExceptionCommonConstant.CAN_NOT_EXPLAIN_ERROR, String.format(ERROR_MSG, getIfContent()));
        }
        String columnName = strArr[0];
        //验证字段是否存在
        boolean result = columnDao.checkColumnExist(columnName, tableName);
        if (!result) {
            if (mapperException != null) {
                mapperException.appendMessage(ExceptionCommonConstant.COLUMN_NOT_EXIST
                        + String.format(TABLE_COLUMN_NAME, tableName, columnName));
            } else {
                mapperException = new MapperException(ExceptionCommonConstant.COLUMN_NOT_EXIST
                        + String.format(TABLE_COLUMN_NAME, tableName, columnName), String.format(ERROR_MSG, getIfContent()));
            }
        }
        if (mapperException != null) {
            throw mapperException;
        } else {
            return true;
        }
    }

    /**
     * 自验证属性方法
     *
     * @param parameterType 参数类型
     * @return boolean
     * @throws MapperException 发生对应关系异常
     */
    public boolean validateProperty(Class parameterType) throws MapperException {
        MapperException mapperException = null;
        if (getIfContent() == null) {
            throw new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, String.format(ERROR_MSG, getContents()));
        }
        if (!getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
            mapperException = new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, String.format(ERROR_MSG, getIfContent()));
        }
        String text = StringUtil.replaceBlank(getIfContent().replace(SymbolConstant.SYMBOL_COMMA, ""));
        String[] strArr = text.split(SymbolConstant.SYMBOL_EQUAL);
        if (strArr.length < 2) {
            throw new MapperException(ExceptionCommonConstant.CAN_NOT_EXPLAIN_ERROR, String.format(ERROR_MSG, getIfContent()));
        }
        String propertyName = strArr[1].replace(SymbolConstant.SYMBOL_LEFT_BRACE, "").replace(SymbolConstant.SYMBOL_RIGHT_BRACE, "").trim();

        //验证属性是否存在
        Boolean result = ReflectHelper.haveGetMethod(propertyName, parameterType);
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
