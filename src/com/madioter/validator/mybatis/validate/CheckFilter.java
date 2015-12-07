package com.madioter.validator.mybatis.validate;

/**
 * <Description> 验证过滤器 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public interface CheckFilter {

    /**
     * 过滤方法
     * @param params 过滤条件
     * @return boolean
     */
    public boolean doFilter(Object...params);
}
