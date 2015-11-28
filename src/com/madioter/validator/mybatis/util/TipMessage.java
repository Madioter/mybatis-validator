package com.madioter.validator.mybatis.util;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月13日 <br>
 */
public class TipMessage {

    /**
     * 提示信息打印
     *
     * @param title 标题
     * @param description 描述
     */
    public static void tip(String title, String description) {
        System.out.println(title);
        System.out.println(description);
    }
}
