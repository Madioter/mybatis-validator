package com.madioter.validator.mybatis.config;

import com.madioter.validator.mybatis.model.ClassModel;
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
     * 迭代器
     */
    private Iterator<Map.Entry<String, ClassModel>> classIterator;

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
        Set<Map.Entry<String, ClassModel>> entrySet = classModelCollection.entrySet();
        classIterator = entrySet.iterator();
    }

    /**
     * Get next class model.
     *
     * @author wangyi8
     * @taskId
     * @return class model
     */
    public ClassModel getNext() {
        if (classIterator.hasNext()) {
            return classIterator.next().getValue();
        }
        return null;
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


}
