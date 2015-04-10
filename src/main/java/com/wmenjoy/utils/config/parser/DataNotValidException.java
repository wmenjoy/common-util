package com.wmenjoy.utils.config.parser;

public class DataNotValidException extends BaseConfigException{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String message;
    
    
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataNotValidException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public DataNotValidException(String message) {
        super(message);
        this.message = message;
    }
}
