package com.madioter.validator.mybatis.parser.sqlparser;

import com.madioter.validator.mybatis.model.sql.sqlnode.SelectNode;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
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
        /*对复杂的sql语句进行拆分，拆分方式：对括号进行出入栈处理
            原理：
               1、先按左括号进行拆分字符串
               2、从最后一项开始匹配，找到第一个右括号，将字符串提取出来编入fragments列表，并使用@frg#No@的形式替换当前字符串
               3、一直将所有的括号结构都拆分完全后，进行select...from 结构的匹配，如果存在select...from结构，认为是一条select语句，否则只是语句的一个片段
               4、将语句中的@frg#No@变量替换回来，生成单条sql语句，并编入sql语句列表selectNodeList
        */
        String[] strArr = sql.split("\\(");
        int length = strArr.length;
        String lastTemp = "";
        for (int i = length - 1; i > 0; i--) {
            if (lastTemp.trim().equals("")) {
                lastTemp = strArr[i];
            } else {
                lastTemp = strArr[i] + lastTemp;
            }
            String temp = lastTemp.substring(0, lastTemp.indexOf(SymbolConstant.SYMBOL_RIGHT_BRACKET));
            if (temp.equals("")) {
                lastTemp = lastTemp.replaceFirst("\\)", "@frg#blank@");
            } else {
                fragments.add(temp);
                lastTemp = "@frg#" + (fragments.size() - 1) + SymbolConstant.SYMBOL_AT + lastTemp.substring(temp.length() + 1);
                //lastTemp = lastTemp.replaceFirst(temp + "\\)", "@frg#" + (fragments.size() - 1) + SymbolConstant.SYMBOL_AT);
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
                    if (temp.contains("@frg#" + k + SymbolConstant.SYMBOL_AT)) {
                        temp = temp.replace("@frg#" + k + SymbolConstant.SYMBOL_AT, SymbolConstant.SYMBOL_LEFT_BRACKET + fragments.get(k) + SymbolConstant.SYMBOL_RIGHT_BRACKET);
                    }
                }
                temp = temp.replace("@frg#blank@", "()");
                //解析单句的sql语句结构
                selectNodeList.add(new SelectNode(temp));
                fragments.remove(i);
                fragments.add(i, "@select#" + (selectNodeList.size() - 1) + SymbolConstant.SYMBOL_AT);
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

    public static void main(String[] args) {
        String str = "select wf.id, wf.type, wf.status, wf.nb_status, wf.result, wf.revert_time, wf.send_time, wf.send_flag, wf.add_time, wf.owner_id, wf.owner_name, wf.is_ticket, wfe.order_id, wfe.product_id, wfe.product_type, wfe.product_name, wfe.resource_id, wfe.resource_type, wfe.resource_name, wfe.vendor_id, wfe.vendor_name, wfe.default_channel, wfe.final_channel, wfe.release_time, wfe.tour_date, wfe.departrue_city, wfe.depart_dates, wfe.promotion_num, wfe.budget, wfe.is_changed from wkf_workform wf left join wkf_workform_extend wfe on wf.id = wfe.workform_id left join wkf_workform_vendor wfv on wf.id = wfv.workform_id and wfe.id = wfv.workform_extend_id where wf.del_flag = 0 and wfe.product_type in (0,31) and not exists (select 1 from (select 18884 as vendor_id union select 8594 as vendor_id union select 25390 as vendor_id union select 25253 as vendor_id) a where a.vendor_id = wfe.vendor_id) and wf.type in (SymbolConstant.SYMBOL_LEFT_BRACEitem})  and wf.id = SymbolConstant.SYMBOL_LEFT_BRACEworkformid}  and wf.owner_name = SymbolConstant.SYMBOL_LEFT_BRACEownername}  and wf.is_ticket = SymbolConstant.SYMBOL_LEFT_BRACEisticket}  and wfe.order_id = SymbolConstant.SYMBOL_LEFT_BRACEorderid}  and wf.result = SymbolConstant.SYMBOL_LEFT_BRACEresult}  and wfe.vendor_id = SymbolConstant.SYMBOL_LEFT_BRACEsupplier}  and wf.status = SymbolConstant.SYMBOL_LEFT_BRACEstatus}  and wf.send_flag = SymbolConstant.SYMBOL_LEFT_BRACEsendflag}  and wf.revert_time >= concat(SymbolConstant.SYMBOL_LEFT_BRACEreverttimestart},\" 00:00:00\")  and wf.revert_time <= concat(SymbolConstant.SYMBOL_LEFT_BRACEreverttimeend},\" 23:59:59\")  and wf.add_time >= concat(SymbolConstant.SYMBOL_LEFT_BRACEaddtimestart},\" 00:00:00\")  and wf.add_time <= concat(SymbolConstant.SYMBOL_LEFT_BRACEaddtimeend},\" 23:59:59\")  and wfe.resource_id = SymbolConstant.SYMBOL_LEFT_BRACEresid}  and wfe.is_changed = SymbolConstant.SYMBOL_LEFT_BRACEischanged}  and wfe.resource_type in (SymbolConstant.SYMBOL_LEFT_BRACEitem})  and wfe.resource_name like '%${resname}%'  and wfe.product_id = SymbolConstant.SYMBOL_LEFT_BRACEproductid}  and wfe.product_name = SymbolConstant.SYMBOL_LEFT_BRACEproductname}  and wfe.contact = SymbolConstant.SYMBOL_LEFT_BRACEcontact}  and wfe.departrue_city = SymbolConstant.SYMBOL_LEFT_BRACEdepartruecity}  and wfe.tour_date >= concat(SymbolConstant.SYMBOL_LEFT_BRACEtourdatestart},\" 00:00:00\")  and wfe.tour_date <= concat(SymbolConstant.SYMBOL_LEFT_BRACEtourdateend},\" 23:59:59\")  and wfe.vendor_id = SymbolConstant.SYMBOL_LEFT_BRACEvendorid}  and wf.nb_status = SymbolConstant.SYMBOL_LEFT_BRACEnbstatus}  and wfv.depart_date >= concat(SymbolConstant.SYMBOL_LEFT_BRACEdepartdatestart},\" 00:00:00\")  and wfv.depart_date <= concat(SymbolConstant.SYMBOL_LEFT_BRACEdepartdateend},\" 23:59:59\") group by wf.id order by wf.id desc limit SymbolConstant.SYMBOL_LEFT_BRACEstart},SymbolConstant.SYMBOL_LEFT_BRACElimit}";
        new SelectSqlParser(str);
    }


}
