package com.madioter.validator.mybatis.validate.filter;

import com.madioter.validator.mybatis.validate.CheckFilter;
import com.madioter.validator.mybatis.validate.impl.AbstractValidator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class FilterProxy {

    /**
     * 过滤规则
     */
    private List<CheckFilter> filters = new ArrayList<CheckFilter>();

    public FilterProxy() {
        filters.add(new IdFilter());
    }

    /**
     * 执行过滤规则
     * @param validator 验证实例
     * @param method 过滤后调用的方法名
     * @param params 方法参数
     * @throws InvocationTargetException 异常
     * @throws IllegalAccessException 异常
     */
    public Object execute(AbstractValidator validator, Method method, Object... params) throws InvocationTargetException, IllegalAccessException {
        boolean flag;
        for (int i = 0; i < filters.size(); i++) {
            flag = filters.get(i).doFilter(params);
            if (!flag) {
                return null;
            }
        }
        return method.invoke(validator, params);
    }
}
