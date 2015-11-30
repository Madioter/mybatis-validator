package com.madioter.validator.mybatis.config.tagnode;

import com.madioter.validator.mybatis.database.ColumnDao;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.exception.MapperException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.builder.xml.dynamic.ForEachSqlNode;
import org.apache.ibatis.builder.xml.dynamic.IfSqlNode;
import org.apache.ibatis.builder.xml.dynamic.SqlNode;
import org.apache.ibatis.builder.xml.dynamic.TextSqlNode;

/**
 * <Description> if标签节点 <br>
 *
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
    private List<SqlNode> contents;

    /**
     * 构造方法
     *
     * @param sqlNode if标签
     * @throws ConfigException 配置异常
     */
    public IfNode(IfSqlNode sqlNode) throws ConfigException {
        this.ifTest = ((String) ReflectHelper.getPropertyValue(sqlNode, "test")).trim();
        SqlNode contentNode = (SqlNode) ReflectHelper.getPropertyValue(sqlNode, CONTENTS);
        this.contents = (ArrayList<SqlNode>) ReflectHelper.getPropertyValue(contentNode, CONTENTS);
        for (int i = 0; i < contents.size(); i++) {
            String nodeText = "";
            if (contents.get(i) instanceof TextSqlNode) {
                nodeText = (String) ReflectHelper.getPropertyValue(contents.get(i), "text");
            } else if (contents.get(i) instanceof ForEachSqlNode) {
                nodeText = new ForEachNode((ForEachSqlNode)contents.get(i)).toString();
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
    public List<SqlNode> getContents() {
        return contents;
    }

    /**
     * Sets contents.
     * @param contents the contents
     */
    public void setContents(List<SqlNode> contents) {
        this.contents = contents;
    }
}
