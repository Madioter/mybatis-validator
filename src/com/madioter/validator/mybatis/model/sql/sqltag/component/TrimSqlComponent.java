package com.madioter.validator.mybatis.model.sql.sqltag.component;

import com.madioter.validator.mybatis.parser.mybatis.component.IComponentNodeParser;
import com.madioter.validator.mybatis.util.MyBatisTagConstant;
import com.madioter.validator.mybatis.util.ReflectHelper;
import com.madioter.validator.mybatis.util.SymbolConstant;
import java.util.List;
import org.apache.ibatis.builder.xml.dynamic.TrimSqlNode;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public class TrimSqlComponent implements ISqlComponent {

    /**
     * tirmSqlNode
     */
    private Object trimSqlNode;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * 后缀
     */
    private String suffix;

    /**
     * 前缀多余去除
     */
    private List<String> prefixesToOverride;

    /**
     * 后缀多余去除
     */
    private List<String> suffixesToOverride;

    /**
     * 内容
     */
    private ISqlComponent content;

    /**
     * 构造方法
     * @param trimSqlNode TrimSqlNode
     */
    public TrimSqlComponent(Object trimSqlNode) {
        try {
            this.trimSqlNode = trimSqlNode;
            prefix = (String) ReflectHelper.getPropertyValue(trimSqlNode, MyBatisTagConstant.PREFIX);
            suffix = (String) ReflectHelper.getPropertyValue(trimSqlNode, MyBatisTagConstant.SUFFIX);
            prefixesToOverride = (List<String>) ReflectHelper.getPropertyValue(trimSqlNode, MyBatisTagConstant.PREFIXES_TO_OVERRIDE);
            suffixesToOverride = (List<String>) ReflectHelper.getPropertyValue(trimSqlNode, MyBatisTagConstant.SUFFIXES_TO_OVERRIDE);
            Object contents = ReflectHelper.getPropertyValue(trimSqlNode, MyBatisTagConstant.CONTENTS);
            for (IComponentNodeParser componentNodeParser : IComponentNodeParser.SUB_CLASSES) {
                if (componentNodeParser.matches(contents)) {
                    content = componentNodeParser.getComponent(contents);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public String toString() {
        String contentText = content.toString();
        for (int i = 0; i < prefixesToOverride.size(); i++) {
            if (contentText.startsWith(prefixesToOverride.get(i))) {
                contentText = contentText.substring(prefixesToOverride.get(i).length());
            }
        }
        for (int i = 0; i < suffixesToOverride.size(); i++) {
            if (contentText.endsWith(suffixesToOverride.get(i))) {
                contentText = contentText.substring(0, contentText.length() - suffixesToOverride.get(i).length() - 1);
            }
        }
        if (prefix != null) {
            contentText = prefix + SymbolConstant.SYMBOL_BLANK + contentText;
        }
        if (suffix != null) {
            contentText = contentText + SymbolConstant.SYMBOL_BLANK + suffix;
        }
        return contentText.trim();
    }

    /**
     * Gets trim sql node.
     * @return the trim sql node
     */
    public Object getTrimSqlNode() {
        return trimSqlNode;
    }

    /**
     * Gets content.
     * @return the content
     */
    public ISqlComponent getContent() {
        return content;
    }
}
