package com.wmenjoy.utils.config.parser.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wmenjoy.utils.lang.DateUtil;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@MConfigAnnotation
public @interface MDate {
    /**
     * 时间格式
     *
     * @return
     */
    String format() default DateUtil.FORMAT_DEFAULT;

    /**
     * 是否为空
     *
     * @return
     */
    boolean nullable() default true;

}
