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
/**
 * 支持List, Set, Array三种形式， 可以指定分隔符
 * @author jinliang.liu
 *
 */
public @interface MArray {
    /***
     * 
     * @return
     */
    boolean nullable() default true;

    /***
     * 每个Field的正则表达式
     * 
     * @return
     */
    String regex() default "";

    /***
     * 默认分隔符
     * 
     * @return
     */
    String speratorChar() default ",";

}
