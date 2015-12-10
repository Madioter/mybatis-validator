package com.madioter.validator.mybatis.parser.mybatis.component;

import com.madioter.validator.mybatis.model.sql.sqltag.component.ISqlComponent;
import com.madioter.validator.mybatis.util.ClassUtil;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月09日 <br>
 */
public interface IComponentNodeParser {

    /**
     * 接口实现类
     */
    public static List<IComponentNodeParser> SUB_CLASSES = ClassUtil.getAllInstanceByInterface(IComponentNodeParser.class);

    /**
     * 判断类是否匹配
     * @param object 对象
     * @return 是否匹配
     */
    public boolean matches(Object object);

    /**
     * Gets component.
     * @param object the object
     * @return the component
     */
    public ISqlComponent getComponent(Object object);
}
