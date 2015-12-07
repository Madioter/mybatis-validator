package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import com.madioter.validator.mybatis.util.exception.MapperException;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class CheckStatementPropertyExist {


    public void validatePropertyExist(){
        if(this.columnSqlNode!=null||this.valueSqlNode!=null)

        {
            if (ifColumnNodeList.size() != ifValueNodeList.size()) {
                new MapperException(ExceptionCommonConstant.INSERT_COLUMN_VALUE_ERROR,
                        String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())).printException();
            } else {

                for (int i = 0; i < len; i++) {
                    InsertIfColumnNode columnNode = ifColumnNodeList.get(i);
                    InsertIfValueNode valueNode = ifValueNodeList.get(i);
                    //验证字段是否存在
                    try {
                        columnNode.validate(columnDao, tableName, parameterType);
                    } catch (MapperException e) {
                        e.setDescription(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())
                                + SymbolConstant.SYMBOL_COLON + e.getDescription());
                        e.printException();
                    }
                    //验证属性是否存在
                    try {
                        valueNode.validate(parameterType);
                    } catch (MapperException e) {
                        e.setDescription(String.format(MAPPER_FILE_ID, mappedStatement.getResource(), mappedStatement.getId())
                                + SymbolConstant.SYMBOL_COLON + e.getDescription());
                        e.printException();
                    }
                }
            }
        }
    }
    }
}
