package com.madioter.validator.mybatis.util.exception;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月21日 <br>
 */
public class NotSupportException extends Exception {

    /**
     * 该对象暂时未支持，请手动检查:
     */
    private static final String ERROR_MSG = "该对象暂时未支持，请手动检查:";

    /**
     * 异常信息
     */
    private String message;

    /**
     * 构造方法
     * @param message 异常信息
     */
    public NotSupportException(String message) {
        this.message = message;
    }

    /**
     * 打印异常
     */
    public void printException(){
        System.err.println(ERROR_MSG + message);
    }
}
