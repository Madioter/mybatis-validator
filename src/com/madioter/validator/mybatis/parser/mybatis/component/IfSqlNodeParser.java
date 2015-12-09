package com.madioter.validator.mybatis.parser.mybatis.component;

import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.model.sql.sqltag.component.IfSqlComponent;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class IfSqlNodeParser implements IComponentNodeParser {
    @Override
    public boolean matches(Object object) {
        if (object.getClass().getName().endsWith(MyBatisTagConstant.IF_SQL_NODE)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ISqlComponent getComponent(Object object) {
        return new IfSqlComponent(object);
    }
}
