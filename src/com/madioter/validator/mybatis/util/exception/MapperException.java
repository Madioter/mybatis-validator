package com.madioter.validator.mybatis.util.exception;

import com.madioter.validator.mybatis.util.SymbolConstant;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月13日 <br>
 */
public class MapperException extends Exception {

    /**
     * 异常信息
     */
    private String message;

    /**
     * 异常
     */
    private Exception e;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 异常构造方法
     * @param message 异常信息
     * @param e 异常
     */
    public MapperException(String message, Exception e) {
        this.message = message;
        this.e = e;
    }

    /**
     * 打印异常
     * @param message 异常信息
     * @param description 异常描述
     */
    public MapperException(String message, String description) {
        this.message = message;
        this.description = description;
    }

    /**
     * 打印异常方法
     * @return
     */
    public void printException() {
        if (e != null) {
            System.err.println(message + SymbolConstant.SYMBOL_COLON);
            e.printStackTrace();
        } else {
            System.err.println(message + SymbolConstant.SYMBOL_COLON + description);
        }
    }

    /**
     * Gets description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * appendMessage message.
     * @param msg the message
     */
    public void appendMessage(String msg) {
        this.message = this.message + "," + msg;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * 合并异常
     * @param mapperException mapperException
     */
    public void mergeException(MapperException mapperException){
        if (mapperException != null) {
            return;
        }
        this.message = this.message + SymbolConstant.SYMBOL_COMMA + mapperException.getMessage();
        this.description = this.description + SymbolConstant.SYMBOL_COMMA + mapperException.getDescription();
    }
}
