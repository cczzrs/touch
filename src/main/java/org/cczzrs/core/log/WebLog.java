package org.cczzrs.core.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @fileName WebLog.java
 * @author CCZZRS
 * @date 2020-10-31 10:42:20
 * @description web请求接口日志
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface WebLog {
    /**
     * 日志描述信息
     * @return
     */
    String description() default "";
    
    /**
     * 是否打印返回结果数据
     * @return
     */
    boolean isPrintResult() default false;
}
