package com.wmenjoy.utils.config.parser.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Documented
@Retention(RetentionPolicy.RUNTIME)
@MConfigAnnotation
public @interface MString {

    /**
     *
     * @return
     */
    String regex() default "";

    /**
     * 是否可以为null
     *
     * @return
     */
    boolean nullable() default true;

    /**
     * TODO: 保留暂不支持 转义字符
     *
     * @return
     */
    String escape() default "\\";

    /**
     * 自动做trim
     *
     * @return
     */
    boolean autoTrim() default false;

}
