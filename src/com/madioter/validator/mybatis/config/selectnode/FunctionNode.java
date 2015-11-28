package com.madioter.validator.mybatis.config.selectnode;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月25日 <br>
 */
public class FunctionNode implements SelectElement {

    /**
     * 函数表达式
     */
    private String express;

    /**
     * 别称
     */
    private String alias;

    /**
     * Gets express.
     * @return express express
     */
    public String getExpress() {
        return express;
    }

    /**
     * Sets express.
     * @param express the express
     */
    public void setExpress(String express) {
        this.express = express;
    }

    /**
     * Gets alias.
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets alias.
     * @param alias the alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
