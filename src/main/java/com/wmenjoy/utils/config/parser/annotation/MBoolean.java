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
public @interface MBoolean {
    /***
     * 是否容许为空
     *
     * @return
     */
    boolean nullable() default true;

    /**
     * boolean 类型的返回值
     * 
     * @return
     */
    boolean defaultValue() default false;
}
