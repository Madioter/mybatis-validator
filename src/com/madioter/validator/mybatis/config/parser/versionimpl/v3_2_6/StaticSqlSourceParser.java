package com.madioter.validator.mybatis.config.parser.versionimpl.v3_2_6;

import com.madioter.validator.mybatis.config.parser.ISqlSourceType;
import com.madioter.validator.mybatis.config.parser.SqlSourceVo;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.List;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class StaticSqlSourceParser implements ISqlSourceType{
    @Override
    public boolean matches(Object object) {
        if (object.getClass().getName().equals("org.apache.ibatis.scripting.xmltags.StaticSqlSource")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SqlSourceVo parser(SqlSource sqlSource) throws ConfigException {
        SqlSourceVo sqlSourceVo = new SqlSourceVo();
        String sql = (String) ReflectHelper.getPropertyValue(sqlSource, "sql");
        List<ParameterMapping> parameterMappings = (List) ReflectHelper.getPropertyValue(sqlSource, "parameterMappings");

        //sql标准化
        String[] fragments = StringUtil.splitWithBlank(sql);
        StringBuilder standardSql = new StringBuilder();
        int k = 0;
        for (int i = 0; i < fragments.length; i++) {
            standardSql.append(StringUtil.toLowerCaseExceptBrace(fragments[i])).append(SymbolConstant.SYMBOL_BLANK);
        }
        sqlSourceVo.setSql(standardSql.toString());
        sqlSourceVo.setParameterMappings(parameterMappings);
        return sqlSourceVo;
    }
}
