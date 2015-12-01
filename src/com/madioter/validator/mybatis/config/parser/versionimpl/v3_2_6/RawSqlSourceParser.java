package com.madioter.validator.mybatis.config.parser.versionimpl.v3_2_6;

import com.madioter.validator.mybatis.config.parser.ISqlSourceType;
import com.madioter.validator.mybatis.config.parser.SqlSourceVo;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class RawSqlSourceParser implements ISqlSourceType {

    @Override
    public boolean matches(Object object) {
        if (object.getClass().getName().equals("org.apache.ibatis.scripting.xmltags.RawSqlSource")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SqlSourceVo parser(SqlSource sqlSource) throws ConfigException {
        SqlSource innerSqlSource = (SqlSource) ReflectHelper.getPropertyValue(sqlSource, "sqlSource");
        StaticSqlSourceParser staticSqlSourceParser = new StaticSqlSourceParser();
        return staticSqlSourceParser.parser(innerSqlSource);
    }
}
