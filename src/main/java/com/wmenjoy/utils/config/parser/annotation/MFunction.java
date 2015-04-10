package com.wmenjoy.utils.config.parser.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
@MConfigAnnotation
public @interface MFunction {
    /**
     * 方法名字
     *
     * @return
     */
    String name();

    /**
     * 方法的处理方式目前支持CLASS_INIT_FINISH
     * 
     * @return
     */
    Type type() default Type.FIELD_INIT;

    public static enum Type {
        CLASS_INIT_FINISH, FIELD_INIT, FIELD_INIT_AFTER
    }

    /**
     * 对应配置字段的名字
     *
     * @return
     */
    String[] fields() default {};

    /**
     * 方法参数的名字
     *
     * @return
     */
    Class<?>[] paramClazz() default {};
}
