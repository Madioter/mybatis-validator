package com.madioter.validator.mybatis.util.exception;

/**
 * <Description> <br>
 *
 * @author wangyi8<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月11日 <br>
 */
public class ExceptionCommonConstant {

    /**
     * 配置信息读取异常
     */
    public static final String CONFIG_READ_EXCEPTION = "配置信息读取异常";

    /**
     * set或get方法不存在
     */
    public static final String SET_OR_GET_METHOD_NOT_EXIST = "set或get方法不存在";

    /**
     * GET方法不存在
     */
    public static final String GET_METHOD_NOT_EXIST = "GET方法不存在";

    /**
     * SET方法不存在
     */
    public static final String SET_METHOD_NOT_EXIST = "SET方法不存在";

    /**
     * get方法的返回值数据类型不匹配
     */
    public static final String GET_METHOD_RETURN_TYPE = "get方法的返回值数据类型不匹配";

    /**
     * resuleMap的ID在项目中存在重复
     */
    public static final String REPEAT_RESULT_MAPPING_DEFINITION = "resuleMap的ID在项目中存在重复";

    /**
     * 确认Mybatis的版本是否正确，使用的类属性发生变化，可能导致反射无法取值。
     */
    public static final String MYBATIS_VERSION_EXCEPTION = "确认Mybatis的版本是否正确";

    /**
     * INSERT语句的TRIM标签的prefix属性需要定义为'('
     */
    public static final String INSERT_TRIM_PREFIX_ERROR = "INSERT语句的TRIM标签的prefix属性需要定义为'('";

    /**
     * INSERT语句的TRIM标签的suffix属性需要定义为')'
     */
    public static final String INSERT_TRIM_SUFFIX_ERROR = "INSERT语句的TRIM标签的suffix属性需要定义为')'";

    /**
     * INSERT语句的TRIM标签的suffixesToOverride属性需要定义为','
     */
    public static final String INSERT_TRIM_SUFFIXES_TO_OVERRIDE_ERROR = "INSERT语句的TRIM标签的suffixesToOverride属性需要定义为','";

    /**
     * INSERT语句的COLUMN和VALUES字段对应关系不正确
     */
    public static final String INSERT_COLUMN_VALUE_ERROR = "INSERT语句的COLUMN和VALUES字段对应关系不正确";

    /**
     * INSERT语句的COLUMN和VALUES字段必须有相同的判断条件
     */
    public static final String INSERT_COLUMN_VALUE_SAME_TEST_ERROR = "INSERT语句的COLUMN和VALUES字段必须有相同的判断条件";

    /**
     * 数据表不存在
     */
    public static final String TABLE_NOT_EXIST = "数据表不存在";

    /**
     * 数据字段不存在
     */
    public static final String COLUMN_NOT_EXIST = "数据字段不存在";

    /**
     * 类型暂时不被支持
     */
    public static final String TYPE_NOT_SUPPORT = "类型暂时不被支持";

    /**
     * INSERT语句的赋值标签必须以逗号结尾
     */
    public static final String INSERT_END_WITH_COMMA = "INSERT语句的赋值标签必须以逗号结尾";

    /**
     * Map类型的参数集缺乏对应的实体类，请自己验证
     */
    public static final String MAP_PROPERTY_VALIDATE_ERROR = "Map类型的参数集缺乏对应的实体类，请自己验证";

    /**
     * 缺乏入参类型parameterType，请补充
     */
    public static final String NO_PROPERTY_VALIDATE_ERROR = "缺乏入参类型parameterType，请补充";

    /**
     * 表达式无法解析
     */
    public static final String CAN_NOT_EXPLAIN_ERROR = "表达式无法解析";

    /**
     * if标签解析错误
     */
    public static final String IF_TAG_EXPLAIN_ERROR = "if标签解析错误";

    /**
     * 多表关联查询的别称不能为空
     */
    public static final String TABLE_ALIAS_IS_NULL = "多表关联查询的别称不能为空";
}
