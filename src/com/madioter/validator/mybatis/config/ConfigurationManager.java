package com.madioter.validator.mybatis.config;

import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月11日 <br>
 */
public class ConfigurationManager {

    /**
     * 文件资源管理类
     */
    private FileResource fileResource;

    /**
     * ResultMap标签
     */
    private ResultMapResource resultMapResource;

    /**
     * MappedStatement标签
     */
    private StatementResource statementResource;

    /**
     * 构造方法
     * @param configuration the configuration
     * @throws Exception the exception
     */
    public ConfigurationManager(Configuration configuration) throws Exception {
        try {
            createFileResource(configuration);
            createResultMapResource(configuration.getResultMaps());
            createStatementResource(configuration.getMappedStatements());
        } catch (Exception e) {
            throw new ConfigException(ExceptionCommonConstant.CONFIG_READ_EXCEPTION, e);
        }
    }

    /**
     * 创建MappedStatement资源对象
     *
     * @author wangyi8
     * @taskId
     * @param mappedStatements Collection
     * @throws ConfigException <br>
     */
    private void createStatementResource(Collection<MappedStatement> mappedStatements) throws ConfigException {
        statementResource = new StatementResource(mappedStatements);
    }

    /**
     * 创建ResultMap资源对象
     *
     * @author wangyi8
     * @taskId
     * @param resultMaps Collection
     */
    private void createResultMapResource(Collection<ResultMap> resultMaps) {
        resultMapResource = new ResultMapResource(resultMaps);

    }

    /**
     * 创建文件资源对象
     *
     * @author wangyi8
     * @taskId
     * @param configuration 配置对象
     * @throws NoSuchFieldException the no such field exception
     * @throws IllegalAccessException the illegal access exception
     */
    public void createFileResource(Configuration configuration) throws NoSuchFieldException, IllegalAccessException {
        Class clz = configuration.getClass();
        Field field = clz.getDeclaredField("loadedResources");
        field.setAccessible(true);
        fileResource = new FileResource((Set<String>) field.get(configuration));
    }

    /**
     * Gets file resource.
     * @return file resource
     */
    public FileResource getFileResource() {
        return fileResource;
    }

    /**
     * Gets result map resource.
     * @return result map resource
     */
    public ResultMapResource getResultMapResource() {
        return resultMapResource;
    }

    /**
     * Gets statement resource.
     * @return statement resource
     */
    public StatementResource getStatementResource() {
        return statementResource;
    }
}
