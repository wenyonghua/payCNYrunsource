package me.zohar.runscore.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class ApiLog
 * @classdesc 用于打印请求和响应日志的注解
 * @author chh
 * @date 2020/8/26  14:43
 * @version 1.0.0
 * @see
 * @since
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLog {

}
