package com.madioter.validator.mybatis.validate.impl;

import com.madioter.validator.mybatis.validate.IDoValidate;
import com.madioter.validator.mybatis.validate.filter.FilterProxy;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public abstract class AbstractValidator implements IDoValidate {

    /**
     * 过滤器代理
     */
    private FilterProxy proxy;

    /**
     * Instantiates a new Check statement table exist.
     */
    public AbstractValidator() {
        proxy = new FilterProxy();
    }

    /**
     * Sets proxy.
     * @param proxy the proxy
     */
    public void setProxy(FilterProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Gets proxy.
     * @return the proxy
     */
    public FilterProxy getProxy() {
        return proxy;
    }
}
