package com.wmenjoy.utils.config.parser;

/***
 * 注解错误异常
 * @author jinliang.liu
 *
 */
public class ErrorAnnotationConfigException extends BaseConfigException{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ErrorAnnotationConfigException() {
        super();
    }

    public ErrorAnnotationConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorAnnotationConfigException(String message) {
        super(message);
    }

    public ErrorAnnotationConfigException(Throwable cause) {
        super(cause);
    }

    
}
