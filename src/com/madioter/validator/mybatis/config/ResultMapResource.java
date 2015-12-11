package com.madioter.validator.mybatis.config;

import com.madioter.validator.mybatis.model.java.ClassModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.mapping.ResultMap;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月12日 <br>
 */
public class ResultMapResource {

    /**
     * 类列表
     */
    private Map<String, ClassModel> classModelCollection = new HashMap<String, ClassModel>();

    /**
     * 获取resultMap对象
     * @param resultMaps Collection<ResultMap>
     */
    public ResultMapResource(Collection<ResultMap> resultMaps) {
        Iterator<ResultMap> iterator = resultMaps.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof ResultMap) {
                classModelCollection.put(((ResultMap) obj).getId(), new ClassModel((ResultMap) obj));
            } /*else {
                try {
                    Method method = obj.getClass().getMethod("getSubject");
                    String message = (String) method.invoke(obj);
                    TipMessage.tip(ExceptionCommonConstant.REPEAT_RESULT_MAPPING_DEFINITION, message);
                } catch (Exception e) {
                    TipMessage.tip(ExceptionCommonConstant.REPEAT_RESULT_MAPPING_DEFINITION, e.getMessage());
                }
            }*/
        }
    }

    /**
     * getClassModeById
     *
     * @author wangyi8
     * @taskId
     * @param id id
     * @return class model
     */
    public ClassModel getClassModeById(String id) {
        return classModelCollection.get(id);
    }

    /**
     * Gets class model collection.
     * @return the class model collection
     */
    public Map<String, ClassModel> getClassModelCollection() {
        return classModelCollection;
    }
}
