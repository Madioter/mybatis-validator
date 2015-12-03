package com.madioter.validator.mybatis.config.parser;

import com.madioter.validator.mybatis.util.ClassUtil;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import java.util.List;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> Mybatis中使用的SqlSource为接口，存在多种实现方式，需要按类型解析 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class SqlSourceParser {

    /**
     * 对sqlSource对象进行解析
     * @param sqlSource sqlSource对象
     * @return SqlSourceVo
     * @throws ConfigException 异常
     */
    public static SqlSourceVo parser(SqlSource sqlSource) throws ConfigException {
        try {
            List<Class> classList = ISqlSourceType.SUB_CLASSES;
            for (int i = 0; i < classList.size(); i++) {
                ISqlSourceType sqlSourceType = (ISqlSourceType) classList.get(i).newInstance();
                if (sqlSourceType.matches(sqlSource)) {
                    return sqlSourceType.parser(sqlSource);
                }
            }
            throw new ConfigException(ExceptionCommonConstant.CLASS_FOUNT_EXCEPTION);
        } catch (Exception e) {
            throw new ConfigException(ExceptionCommonConstant.CLASS_FOUNT_EXCEPTION, e);
        }
    }
}
