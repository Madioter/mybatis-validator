package com.madioter.validator.mybatis.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class ClassUtil {

    /**
     * @Description: 根据一个接口返回该接口的所有类
     * @param c 接口
     * @return List<Class>    实现接口的所有类
     */
    @SuppressWarnings("unchecked")
    public static List<Class> getAllClassByInterface(Class c) {
        try {
            List returnClassList = new ArrayList<Class>();
            //判断是不是接口,不是接口不作处理
            if (c.isInterface()) {
                String packageName = c.getPackage().getName();  //获得当前包名
                List<Class> allClass = getClasses(packageName);//获得当前包以及子包下的所有类

                //判断是否是一个接口
                for (int i = 0; i < allClass.size(); i++) {
                    if (c.isAssignableFrom(allClass.get(i))) {
                        if (!c.equals(allClass.get(i))) {
                            returnClassList.add(allClass.get(i));
                        }
                    }
                }
            }
            return returnClassList;
        } catch (Exception e) {
            return new ArrayList<Class>();
        }
    }

    /**
     *
     * @Description: 根据包名获得该包以及子包下的所有类不查找jar包中的
     * @param packageName 包名
     * @return List<Class>    包下所有类
     * @throws ClassNotFoundException 异常
     * @throws IOException 异常
     */
    private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClass(directory, packageName));
        }
        return classes;
    }

    /**
     * 获取文件路径下的所有符合条件的类
     * @param directory 文件路径
     * @param packageName 报名
     * @return List<Class>
     * @throws ClassNotFoundException 异常
     */
    private static List<Class> findClass(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
