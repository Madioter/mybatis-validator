package com.madioter.validator.mybatis.config.tagnode;

import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;
import com.madioter.validator.mybatis.util.SymbolConstant;
import org.apache.ibatis.builder.xml.dynamic.IfSqlNode;

/**
 * <Description> insert的字段名定义节点 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月20日 <br>
 */
public class InsertIfColumnNode extends IfNode {

    /**
     * 表字段名
     */
    private static final String TABLE_COLUMN_NAME = "：表名：%s, 字段名：%s";

    /**
     * 字符表达式
     */
    private static final String ERROR_MSG = "表达式为：%s";

    /**
     * 构造方法
     *
     * @param sqlNode if节点
     * @throws ConfigException 配置异常
     */
    public InsertIfColumnNode(IfSqlNode sqlNode) throws ConfigException {
        super(sqlNode);
    }

    /**
     * 自验证方法
     *
     * @param columnDao 验证工具服务
     * @param tableName 表名
     * @throws MapperException 发生对应关系异常
     */
    public boolean validate(ColumnDao columnDao, String tableName) throws MapperException {
        MapperException mapperException = null;
        if (getIfContent() == null) {
            throw  new MapperException(ExceptionCommonConstant.IF_TAG_EXPLAIN_ERROR, String.format(ERROR_MSG, getContents()));
        }
        if (!getIfContent().trim().endsWith(SymbolConstant.SYMBOL_COMMA)) {
            mapperException = new MapperException(ExceptionCommonConstant.INSERT_END_WITH_COMMA, String.format(ERROR_MSG, getIfContent()));
        }
        String columnName = getIfContent().replace(SymbolConstant.SYMBOL_COMMA, "").trim();
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
}
