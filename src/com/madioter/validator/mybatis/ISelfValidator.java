package com.madioter.validator.mybatis;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月13日 <br>
 */
public interface ISelfValidator {

    /**
     * 验证方法
     * @param obj 验证参数
     * @throws Exception 验证异常
     */
    public void validator(Object... obj) throws Exception;
}
