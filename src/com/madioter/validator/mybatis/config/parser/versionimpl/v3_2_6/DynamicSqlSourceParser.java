package com.madioter.validator.mybatis.config.parser.versionimpl.v3_2_6;

import com.madioter.validator.mybatis.config.parser.ISqlSourceType;
import com.madioter.validator.mybatis.config.parser.SqlSourceVo;
import com.madioter.validator.mybatis.config.tagnode.ForEachNode;
import com.madioter.validator.mybatis.config.tagnode.SelectIfNode;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
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
        List<Object> contents = (List) ReflectHelper.getPropertyValue(rootSqlNode, MyBatisTagConstant.CONTENTS);
        List<SelectIfNode> selectIfNodeList = new ArrayList<SelectIfNode>();
        /**
         * 语句字符碎片
         */
        List<String> fragments = new ArrayList<String>();
        for (int i = 0; i < contents.size(); i++) {
            Object node = contents.get(i);
            if (node.getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE)) {
                String text = (String) ReflectHelper.getPropertyValue(node, MyBatisTagConstant.TEXT);
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
            } else if (node.getClass().getName().endsWith("ForEachSqlNode")) {
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(new ForEachNode(node).toString())));
            } else if (node.getClass().getName().endsWith(MyBatisTagConstant.MIXED_SQL_NODE)) {
                convertMixedNode(node, fragments);
            } else if (node.getClass().getName().endsWith(MyBatisTagConstant.IF_SQL_NODE)) {
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
        List<Object> sqlNodeList = (List) ReflectHelper.getPropertyValue(node, MyBatisTagConstant.CONTENTS);
        for (int k = 0; k < sqlNodeList.size(); k++) {
            if (sqlNodeList.get(k).getClass().getName().endsWith(MyBatisTagConstant.TEXT_SQL_NODE)) {
                String text = (String) ReflectHelper.getPropertyValue(sqlNodeList.get(k), MyBatisTagConstant.TEXT);
                fragments.addAll(StringUtil.arrayToList(StringUtil.splitWithBlank(text)));
            } else if (sqlNodeList.get(k).getClass().getName().endsWith(MyBatisTagConstant.MIXED_SQL_NODE)) {
                convertMixedNode(sqlNodeList.get(k), fragments);
            }
        }
    }
}
