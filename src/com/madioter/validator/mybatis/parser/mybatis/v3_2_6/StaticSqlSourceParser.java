package com.madioter.validator.mybatis.parser.mybatis.v3_2_6;

import com.madioter.validator.mybatis.parser.mybatis.ISqlSourceType;
import com.madioter.validator.mybatis.model.mybatis.SqlSourceVo;
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
public class StaticSqlSourceParser implements ISqlSourceType {
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

        //在标准sql中，把变量赋值补充全
        if (parameterMappings.size() > 0) {
            List<String> items = StringUtil.arrayToList(standardSql.toString().split("\\?"));
            standardSql = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                standardSql.append(items.get(i));
                if (i < parameterMappings.size()) {
                    standardSql.append(SymbolConstant.SYMBOL_LEFT_BRACE + parameterMappings.get(i).getProperty() +
                            ",jdbcType=" + parameterMappings.get(i).getJdbcTypeName() + SymbolConstant.SYMBOL_RIGHT_BRACE);
                    //最后一项不补充问号
                } else if (i < items.size() - 1) {
                    standardSql.append("?");
                }
            }
        }

        sqlSourceVo.setSql(standardSql.toString());
        sqlSourceVo.setParameterMappings(parameterMappings);
        return sqlSourceVo;
    }
}
