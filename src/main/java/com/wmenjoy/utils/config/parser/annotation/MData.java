package com.wmenjoy.utils.config.parser.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@MConfigAnnotation
public @interface MData {

    /**
     * 字段
     * 
     * @return
     */
    String fieldStr() default "";

    /***
     * 字段分隔符
     * 
     * @return
     */
    String fieldSep() default "\\|";

    /***
     * 字段数组
     * 
     * @return
     */
    String[] fields() default {};

    /**
     * 忽略没有对应field的key
     * 
     * @return
     */
    boolean ignoreNoFieldKey() default true;

}
