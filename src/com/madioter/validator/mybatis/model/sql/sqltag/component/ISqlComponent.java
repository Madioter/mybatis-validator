package com.madioter.validator.mybatis.model.sql.sqltag.component;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public interface ISqlComponent {

    /**
     * Gets sub components.
     * @return the sub components
     */
    public List<ISqlComponent> getSubComponents();
}
