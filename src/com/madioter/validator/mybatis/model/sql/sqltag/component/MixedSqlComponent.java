package com.madioter.validator.mybatis.model.sql.sqltag.component;

import com.madioter.validator.mybatis.parser.mybatis.component.IComponentNodeParser;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class MixedSqlComponent implements ISqlComponent {

    /**
     * mixedSqlNode
     */
    private Object mixedSqlNode;

    /**
     * contents
     */
    private List<ISqlComponent> contents = new ArrayList<ISqlComponent>();

    /**
     * 构造方法
     * @param mixedSqlNode MixedSqlNode
     */
    public MixedSqlComponent(Object mixedSqlNode) {
        try {
            this.mixedSqlNode = mixedSqlNode;
            List sqlNodeList = (List) ReflectHelper.getPropertyValue(mixedSqlNode, MyBatisTagConstant.CONTENTS);
            for (int i = 0; i < sqlNodeList.size(); i++) {
                Object sqlNode = sqlNodeList.get(i);
                if (sqlNode != null) {
                    for (IComponentNodeParser componentNodeParser : IComponentNodeParser.SUB_CLASSES) {
                        if (componentNodeParser.matches(sqlNode)) {
                            contents.add(componentNodeParser.getComponent(sqlNode));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ISqlComponent sqlComponent : contents) {
            builder.append(sqlComponent.toString()).append(SymbolConstant.SYMBOL_BLANK);
        }
        return builder.toString().trim();
    }
}
