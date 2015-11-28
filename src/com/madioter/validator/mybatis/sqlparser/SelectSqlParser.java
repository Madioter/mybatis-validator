package com.madioter.validator.mybatis.sqlparser;

import com.madioter.validator.mybatis.config.selectnode.constractor.SelectNode;
import com.madioter.validator.mybatis.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId  <br>
 * @CreateDate 2015年11月28日 <br>
 */
public class SelectSqlParser {

    /**
     * 简单sql节点
     */
    private List<SelectNode> selectNodeList = new ArrayList<SelectNode>();

    /**
     * sql片段
     */
    private List<String> fragments = new ArrayList<String>();


    /**
     * select语句解析类
     * @param sql sql语句
     */
    public SelectSqlParser(String sql) {
        //对复杂的sql语句进行拆分，拆分方式：对括号和union的select...from结构进行拆分
        String[] strArr = sql.split("\\(");
        int length = strArr.length;
        String lastTemp = "";
        for (int i = length - 1; i > 0; i--) {
            if (lastTemp.trim().equals("")) {
                lastTemp = strArr[i];
            } else {
                lastTemp = strArr[i] + lastTemp;
            }
            String temp = lastTemp.substring(0, lastTemp.indexOf(")"));
            if (temp.equals("")) {
                lastTemp = lastTemp.replaceFirst("\\)", "@frg#blank@");
            } else {
                fragments.add(temp);
                lastTemp = lastTemp.replaceFirst(temp + "\\)", "@frg#" + (fragments.size() - 1) + "@");
            }
        }
        fragments.add(strArr[0] + lastTemp);

        for (int i = 0; i < fragments.size(); i++) {
            String temp = fragments.get(i);
            String[] blankArr = StringUtil.splitWithBlank(temp);
            List<String> blankList = StringUtil.arrayToList(blankArr);
            if (blankList.contains("select") && blankList.contains("from")) {
                //把相应的变量进行替换
                for (int k = i; k >= 0; k--) {
                    if (temp.contains("@frg#" + k + "@")) {
                        temp = temp.replace("@frg#" + k + "@", "(" + fragments.get(k) + ")");
                    }
                }
                temp = temp.replace("@frg#blank@", "()");
                selectNodeList.add(new SelectNode(temp));
                fragments.remove(i);
                fragments.add(i, "@select#" + (selectNodeList.size() - 1) + "@");
            }
        }
    }

    /**
     * Gets select node list.
     * @return select node list
     */
    public List<SelectNode> getSelectNodeList() {
        return selectNodeList;
    }

    /*public static void main(String[] args) {
        String str = "select count(id) from (select id,max(name),now() from table where id in (1,2,3) union select 1 from dual)";
        new SelectSqlParser(str);
    }*/


}
