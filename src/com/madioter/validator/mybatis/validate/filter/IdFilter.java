package com.madioter.validator.mybatis.validate.filter;

import com.madioter.validator.mybatis.config.statement.MappedStatementItem;
import com.madioter.validator.mybatis.util.StringUtil;
import com.madioter.validator.mybatis.validate.CheckFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月04日 <br>
 */
public class IdFilter implements CheckFilter {

    /**
     * 包含的语句表ID
     */
    private List<String> includes = new ArrayList<String>();

    /**
     * 不包含的语句表ID
     */
    private List<String> excludes = new ArrayList<String>();

    public IdFilter() {
        //includes.add("wkfBatchHandle.queryBatchWorkformDetailInfo");
    }

    @Override
    public boolean doFilter(Object... params) {
        String id = null;
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof MappedStatementItem) {
                id = ((MappedStatementItem) params[i]).getId();
            }
        }
        if (StringUtil.isBlank(id)) {
            return true;
        }
        if (excludes != null && excludes.contains(id)) {
            return false;
        }
        if (includes != null && !includes.isEmpty() && includes.contains(id)) {
            return true;
        } else if (includes == null || includes.isEmpty()) {
            return true;
        }
        return false;
    }
}
