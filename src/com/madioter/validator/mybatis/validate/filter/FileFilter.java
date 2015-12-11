package com.madioter.validator.mybatis.validate.filter;

import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.util.SymbolConstant;
import com.madioter.validator.mybatis.validate.CheckFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月11日 <br>
 */
public class FileFilter implements CheckFilter {

    /**
     * 包含的文件地址
     */
    private List<String> includes = new ArrayList<String>();

    /**
     * 不包含的文件地址
     */
    private List<String> excludes = new ArrayList<String>();

    @Override
    public boolean doFilter(Object... params) {
        String fileResource = null;
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof MappedStatementItem) {
                fileResource = ((MappedStatementItem) params[i]).getResource();
            }
        }
        if (StringUtil.isBlank(fileResource)) {
            return true;
        }
        for (String exclude : excludes) {
            if (matches(exclude, fileResource)) {
                return false;
            }
        }
        if (includes != null || !includes.isEmpty()) {
            return true;
        } else {
            for (String include : includes) {
                if (matches(include, fileResource)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Sets includes.
     * @param includes the includes
     */
    public void setIncludes(List<String> includes) {
        for (int i = 0; i < includes.size(); i++) {
            this.includes.add(getRegex(includes.get(i)));
        }
    }

    /**
     * Sets excludes.
     * @param excludes the excludes
     */
    public void setExcludes(List<String> excludes) {
        for (int i = 0; i < excludes.size(); i++) {
            this.excludes.add(getRegex(excludes.get(i)));
        }
    }

    private boolean matches(String regex, String fileResource) {
        return fileResource.matches(regex);
    }

    private String getRegex(String str) {
        if (str.contains(SymbolConstant.SYMBOL_ASTERISK)) {
            str = str.replace(SymbolConstant.SYMBOL_ASTERISK, SymbolConstant.SYMBOL_POINT + SymbolConstant.SYMBOL_ASTERISK);
        }
        if (str.contains(SymbolConstant.SYMBOL_BACK_SLASH)) {
            str = str.replace(SymbolConstant.SYMBOL_BACK_SLASH, SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_BACK_SLASH);
        }
        if (str.contains(SymbolConstant.SYMBOL_SLASH)) {
            str = str.replace(SymbolConstant.SYMBOL_SLASH, SymbolConstant.SYMBOL_BACK_SLASH + SymbolConstant.SYMBOL_BACK_SLASH);
        }
        if (!str.startsWith(SymbolConstant.SYMBOL_POINT + SymbolConstant.SYMBOL_ASTERISK)) {
            str = SymbolConstant.SYMBOL_POINT + SymbolConstant.SYMBOL_ASTERISK + str;
        }
        if (!str.endsWith(SymbolConstant.SYMBOL_POINT + SymbolConstant.SYMBOL_ASTERISK)) {
            str = str + SymbolConstant.SYMBOL_POINT + SymbolConstant.SYMBOL_ASTERISK;
        }
        return str;
    }
}
