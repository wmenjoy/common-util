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
public @interface MEnum {
    /**
     * 枚举类型的处理方式
     *
     * @return
     */
    Type type() default Type.STRING;

    public static enum Type {
        INT, STRING
    }

    /**
     * 是否可以为空
     *
     * @return
     */
    boolean nullable() default true;
}
