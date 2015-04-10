package com.wmenjoy.utils.config.parser;

import java.util.Map;

public interface IConfigParser<T> {
    /**
     * 将line 解析程对象
     * @param line
     * @return
     */
    public T parse(String line) throws DataNotValidException, DataAccessErrorException;
    
    
    /**
     * 处理对象初始化，需要外部条件的情况
     * @param line
     * @return
     */
    public T parse(T target, String line) throws DataNotValidException, DataAccessErrorException;

    /**
     * 处理默认值的情况 
     * 
     * @param line
     * @param nullable
     * @return
     */
    public T parse(String line, boolean nullable) throws DataNotValidException,
            DataAccessErrorException;

    
    /**
     * 处理对象初始化，需要外部条件的情况
     * 
     * @param line
     * @return
     */
    public T parse(T target, String line, boolean nullable) throws DataNotValidException, DataAccessErrorException;
    
    /**
     * 获取个默认值的对象，前提是，对象容许默认值
     * 
     * @return
     */
    public T getDefaultObject() throws DataNotValidException, DataAccessErrorException;

    /**
     * 支持map转对象
     * 
     * @param key
     * @return
     * @throws DataAccessErrorException
     */
    public T parse(Map<String, String> key) throws DataNotValidException, DataAccessErrorException;
    
    
    /**
     * 处理对象初始化，需要外部条件的情况
     * 
     * @param line
     * @return
     */
    public T parse(T target, Map<String, String> key) throws DataNotValidException, DataAccessErrorException;
}
