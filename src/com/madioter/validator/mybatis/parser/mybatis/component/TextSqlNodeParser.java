package com.madioter.validator.mybatis.parser.mybatis.component;

import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.TextSqlComponent;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class TextSqlNodeParser implements IComponentNodeParser {
    @Override
    public boolean matches(Object object) {
        if (object.getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ISqlComponent getComponent(Object object) {
        return new TextSqlComponent(object);
    }
}
