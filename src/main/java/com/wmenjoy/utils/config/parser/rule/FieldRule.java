package com.wmenjoy.utils.config.parser.rule;

import com.wmenjoy.utils.config.parser.DataNotValidException;


public interface FieldRule<T> {
    /**
     * 校验并且获取处理后的结果
     * 
     * @param value
     * @return
     * @throws DataNotValidException
     */
    public T checkAndGetValue(String value) throws DataNotValidException;

    public boolean nullable();

}
