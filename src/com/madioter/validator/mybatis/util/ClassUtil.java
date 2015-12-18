package com.madioter.validator.mybatis.util;

import com.madioter.validator.mybatis.util.exception.ConfigException;
import com.madioter.validator.mybatis.util.exception.ExceptionCommonConstant;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <Description> 类帮助工具： <br>
 *     含有功能：1、获取接口下的实现类列表
 *              2、判断忽略的类检查
 *              3、判断Class是否为基础数据类型
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月01日 <br>
 */
public class ClassUtil {

    /**
     * 获取所有接口实现类对象实例
     * @param c 类
     * @return List
     */
    public static List getAllInstanceByInterface(Class c) {
        List<Class> clzList = getAllClassByInterface(c);
        List<Object> instanceList = new ArrayList();
        for (int i = 0; i < clzList.size(); i++) {
            try {
                instanceList.add(clzList.get(i).newInstance());
            } catch (Exception e) {

            }
        }
        return instanceList;
    }

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
        if (directory == null || !directory.exists()) {
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


    /**
     * 是否忽略类属性检查
     * @author wangyi8
     * @taskId
     * @param clz the clz
     * @return the boolean 忽略返回是，否则返回否
     */
    public static boolean ignorePropertyCheck(Class clz) {
        if (clz == null) {
            return true;
        }
        for (int i = 0; i < Config.IGNORE_PARAMETER_TYPES.length; i++) {
            if (clz.equals(Config.IGNORE_PARAMETER_TYPES[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断类是否为基础数据类型
     *
     * @param clz clz
     * @return boolean
     */
    public static boolean basicType(Class clz) {
        if (clz == null) {
            return false;
        }
        for (int i = 0; i < Config.JAVA_BASIC_CLASS_TYPE.length; i++) {
            if (clz.equals(Config.JAVA_BASIC_CLASS_TYPE[i])) {
                return true;
            }
        }
        return false;
    }

}
