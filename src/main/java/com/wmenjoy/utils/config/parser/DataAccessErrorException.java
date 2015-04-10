package com.wmenjoy.utils.config.parser;

public class DataAccessErrorException extends BaseConfigException{

    /**
     * 
     */
    private static final long serialVersionUID = 4929342198695238039L;

    public DataAccessErrorException() {
        super();
    }

    public DataAccessErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessErrorException(String message) {
        super(message);

    }

    public DataAccessErrorException(Throwable cause) {
        super(cause);
    }

    
    
}
