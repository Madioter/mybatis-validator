package com.madioter.validator.mybatis.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月11日 <br>
 */
public class FileResource {

    /**
     * 文件路径集合
     */
    private List<String> filePaths = new ArrayList<String>();

    /**
     * 文件路径迭代器
     */
    private Iterator<String> fileIterator;

    /**
     * xml资源文件配置信息类初始化方法
     *
     * @param fileResources 文件资源集合
     */
    public FileResource(Set<String> fileResources) {
        for (String resource : fileResources) {
            if (resource.trim().startsWith("file")) {
                filePaths.add(resource.substring(resource.indexOf("[") + 1, resource.indexOf("]")));
            }
        }
        fileIterator = filePaths.iterator();
    }

    /**
     * 获取下一个xml资源路径
     *
     * @author wangyi8
     * @taskId
     * @return string
     */
    public String nextResource() {
        if (fileIterator.hasNext()) {
            return fileIterator.next();
        } else {
            return null;
        }
    }
}
