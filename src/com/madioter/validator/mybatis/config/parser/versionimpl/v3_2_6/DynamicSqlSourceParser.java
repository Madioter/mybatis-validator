package com.madioter.validator.mybatis.config.parser.versionimpl.v3_2_6;

import com.madioter.validator.mybatis.config.parser.ISqlSourceType;
import com.madioter.validator.mybatis.config.parser.SqlSourceVo;
import com.madioter.validator.mybatis.config.tagnode.ForEachNode;
import com.madioter.validator.mybatis.config.tagnode.SelectIfNode;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class DynamicSqlSourceParser implements ISqlSourceType {

    /**
     * Mybatis解析SqlNode的contents属性名
     */
    private static final String CONTENTS = "contents";

    /**
     * text
     */
    private static final String TEXT = "text";

    @Override
    public boolean matches(Object object) {
        if (object.getClass().getName().equals("org.apache.ibatis.scripting.xmltags.DynamicSqlSource")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SqlSourceVo parser(SqlSource sqlSource) throws ConfigException {
        SqlSourceVo sqlSourceVo = new SqlSourceVo();
        Object rootSqlNode = ReflectHelper.getPropertyValue(sqlSource, "rootSqlNode");
        List<Object> contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, CONTENTS);
        List<SelectIfNode> selectIfNodeList = new ArrayList<SelectIfNode>();
        /**
         * 语句字符碎片
         */
        List<String> fragments = new ArrayList<String>();
        for (int i = 0; i < contents.size(); i++) {
            Object node = contents.get(i);
            if (node.getClass().getName().endsWith("TextSqlNode")) {
                String text = (String) ReflectHelper.getPropertyValue(node, TEXT);
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
            } else if (node.getClass().getName().endsWith("ForEachSqlNode")) {
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(new ForEachNode(node).toString())));
            } else if (node.getClass().getName().endsWith("MixedSqlNode")) {
                convertMixedNode(node, fragments);
            } else if (node.getClass().getName().endsWith("IfSqlNode")) {
                SelectIfNode selectIfNode = new SelectIfNode(node);
                selectIfNodeList.add(selectIfNode);
                String content = selectIfNode.getIfContent();
                String[] contentArr = StringUtil.splitWithBlank(content);
                for (int j = 0; j < contentArr.length; j++) {
                    fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(contentArr[j])));
                }
            }
        }
        //对sql语句进行标准化，使用单个空格进行分割字符串，并且所有字符串改为小写
        StringBuilder standardSql = new StringBuilder();
        for (int i = 0; i < fragments.size(); i++) {
            standardSql.append(StringUtil.toLowerCaseExceptBrace(fragments.get(i))).append(SymbolConstant.SYMBOL_BLANK);
        }
        sqlSourceVo.setSql(standardSql.toString());
        sqlSourceVo.setSelectIfNodeList(selectIfNodeList);
        return sqlSourceVo;
    }

    /**
     * 解析MixedSqlNode 循环
     * @param node MixedSqlNode节点
     * @param fragments 字符串碎片
     * @throws ConfigException 异常
     */
    private void convertMixedNode(Object node, List<String> fragments) throws ConfigException {
        List<Object> sqlNodeList = (List) ReflectHelper.getPropertyValue(node, CONTENTS);
        for (int k = 0; k < sqlNodeList.size(); k++) {
            if (sqlNodeList.get(k).getClass().getName().endsWith("TextSqlNode")) {
                String text = (String) ReflectHelper.getPropertyValue(sqlNodeList.get(k), TEXT);
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
            } else if (sqlNodeList.get(k).getClass().getName().endsWith("MixedSqlNode")) {
                convertMixedNode(sqlNodeList.get(k), fragments);
            }
        }
    }
}
