package com.madioter.validator.mybatis.parser.mybatis.v3_1_1;

import com.madioter.validator.mybatis.parser.mybatis.ISqlSourceType;
import com.madioter.validator.mybatis.model.mybatis.SqlSourceVo;
import com.madioter.validator.mybatis.model.sql.sqltag.ForEachNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfColumnNode;
import com.madioter.validator.mybatis.model.sql.sqltag.InsertIfValueNode;
import com.madioter.validator.mybatis.model.sql.sqltag.SelectIfNode;
import com.madioter.validator.mybatis.model.sql.sqltag.UpdateIfSetNode;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.util.exception.ConfigException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <Description> 不同版本的类实现不同 <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class DynamicSqlSourceParser implements ISqlSourceType {

    @Override
    public boolean matches(Object object) {
        if (object.getClass().getName().equals("org.apache.ibatis.builder.xml.dynamic.DynamicSqlSource")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对DynamicSqlSource对象进行解析
     * @param sqlSource DynamicSqlSourceParser
     * @return SqlSourceVo
     * @throws ConfigException 异常
     */
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
            } else if (node.getClass().getName().endsWith(MyBatisTagConstant.SET_SQL_NODE)) {
                List<UpdateIfSetNode> updateIfSetNodeList = createSetNodeList(node);
                sqlSourceVo.setSetNodeList(updateIfSetNodeList);
                sqlSourceVo.setSetSqlNode(node);
            } else if (node.getClass().getName().endsWith(MyBatisTagConstant.TRIM_SQL_NODE) && sqlSourceVo.getIfColumnNodes() == null) {
                List<InsertIfColumnNode> insertIfColumnNodes = createColumnNodeList(node);
                sqlSourceVo.setIfColumnNodes(insertIfColumnNodes);
                sqlSourceVo.setColumnSqlNode(node);
            } else if (node.getClass().getName().endsWith(MyBatisTagConstant.TRIM_SQL_NODE) && sqlSourceVo.getIfValueNodes() == null) {
                List<InsertIfValueNode> ifValueNodes = createValueNodeList(node);
                sqlSourceVo.setIfValueNodes(ifValueNodes);
                sqlSourceVo.setValueSqlNode(node);
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
     * 构建表字段节点信息
     *
     * @param node xml节点
     * @return List<UpdateIfSetNode> 列表
     * @throws ConfigException <br>
     */
    private List<UpdateIfSetNode> createSetNodeList(Object node) throws ConfigException {
        List<UpdateIfSetNode> updateIfSetNodeList = new ArrayList<UpdateIfSetNode>();
        Object contentNode = ReflectHelper.getPropertyValue(node, MyBatisTagConstant.CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(contentNode, MyBatisTagConstant.CONTENTS);
        for (Object sqlNode : contents) {
            if (sqlNode.getClass().getName().endsWith(MyBatisTagConstant.IF_SQL_NODE)) {
                updateIfSetNodeList.add(new UpdateIfSetNode(sqlNode));
            }
        }
        return updateIfSetNodeList;
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

    /**
     * 构建表字段节点信息
     *
     * @param node xml节点
     * @throws ConfigException <br>
     */
    private List<InsertIfColumnNode> createColumnNodeList(Object node) throws ConfigException {
        List<InsertIfColumnNode> ifColumnNodeList = new ArrayList<InsertIfColumnNode>();
        Object contentNode = ReflectHelper.getPropertyValue(node, MyBatisTagConstant.CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(contentNode, MyBatisTagConstant.CONTENTS);
        for (Object sqlNode : contents) {
            if (sqlNode.getClass().getName().endsWith(MyBatisTagConstant.IF_SQL_NODE)) {
                ifColumnNodeList.add(new InsertIfColumnNode(sqlNode));
            }
        }
        return ifColumnNodeList;
    }

    /**
     * 构建表value节点信息
     *
     * @param node xml节点
     * @throws ConfigException <br>
     */
    private List<InsertIfValueNode> createValueNodeList(Object node) throws ConfigException {
        List<InsertIfValueNode> ifValueNodeList = new ArrayList<InsertIfValueNode>();
        Object contentNode = ReflectHelper.getPropertyValue(node, MyBatisTagConstant.CONTENTS);
        List<Object> contents = (List) ReflectHelper.getPropertyValue(contentNode, MyBatisTagConstant.CONTENTS);
        for (Object sqlNode : contents) {
            if (sqlNode.getClass().getName().endsWith(MyBatisTagConstant.IF_SQL_NODE)) {
                ifValueNodeList.add(new InsertIfValueNode(sqlNode));
            }
        }
        return ifValueNodeList;
    }
}