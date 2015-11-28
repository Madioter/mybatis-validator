package com.madioter.validator.mybatis.util.exception;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月11日 <br>
 */
public class ConfigException extends Exception {

    /**
     * 异常构造方法
     * @param message 异常信息
     * @param e 异常
     */
    public ConfigException(String message, Exception e) {
        super(message, e);
    }
}
