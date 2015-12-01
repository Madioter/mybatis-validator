package com.madioter.validator.mybatis.config.tagnode;

import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> if标签节点 <br>
 *
 * 解决3.1.1和3.2.6版本类路径不同
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月20日 <br>
 */
public abstract class IfNode {

    /**
     * contents
     */
    private static final String CONTENTS = "contents";

    /**
     * if条件
     */
    private String ifTest;

    /**
     * if标签内容
     */
    private String ifContent = "";

    /**
     * if内部嵌套节点
     */
    private List<Object> contents;

    /**
     * 构造方法
     *
     * @param sqlNode if标签
     * @throws ConfigException 配置异常
     */
    public IfNode(Object sqlNode) throws ConfigException {
        this.ifTest = ((String) ReflectHelper.getPropertyValue(sqlNode, "test")).trim();
        Object contentNode = (Object) ReflectHelper.getPropertyValue(sqlNode, CONTENTS);
        this.contents = (ArrayList<Object>) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (int i = 0; i < contents.size(); i++) {
            String nodeText = "";
            if (contents.get(i).getClass().getName().endsWith("TextSqlNode")) {
                nodeText = (String) ReflectHelper.getPropertyValue(contents.get(i), "text");
            } else if (contents.get(i).getClass().getName().endsWith("ForEachSqlNode")) {
                nodeText = new ForEachNode(contents.get(i)).toString();
            }
            if (nodeText.trim() != null && !nodeText.trim().equals("")) {
                this.ifContent = this.ifContent + SymbolConstant.SYMBOL_BLANK + nodeText.trim();
            }
        }
    }

    /**
     * 验证条件是否存在
     * @param parameterType 入参
     * @return boolean
     * @throws MapperException 异常
     */
    public boolean validate(Class parameterType) throws MapperException {
        //TODO 验证查询条件中的入参属性是否存在
        return true;
    }

    /**
     * Gets if test.
     * @return if test
     */
    public String getIfTest() {
        return ifTest;
    }

    /**
     * Sets if test.
     * @param ifTest the if test
     */
    public void setIfTest(String ifTest) {
        this.ifTest = ifTest;
    }

    /**
     * Gets if content.
     * @return the if content
     */
    public String getIfContent() {
        return ifContent;
    }

    /**
     * Sets if content.
     * @param ifContent the if content
     */
    public void setIfContent(String ifContent) {
        this.ifContent = ifContent;
    }

    /**
     * Gets contents.
     * @return contents
     */
    public List<Object> getContents() {
        return contents;
    }

    /**
     * Sets contents.
     * @param contents the contents
     */
    public void setContents(List<Object> contents) {
        this.contents = contents;
    }
}
