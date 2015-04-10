package com.wmenjoy.utils.config.parser.rule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;





import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.lang.StringUtils;


public class ArrayRule<T, E> extends BaseFieldRule<T> {
    private String speratorChar = ",";
    Class<E> subClazz;

    Class<T> mainClazz;
    /***
     * 字段规则
     */
    FieldRule<?> subRule;

    public ArrayRule(Class<T> mainClazz, Class<E> subClazz, FieldRule<?> subRule)
            throws SystemConfigErrorException {
        super(true);
        this.mainClazz = mainClazz;
        this.subClazz = subClazz;
        this.subRule = subRule;
        checkRule();
    }

    private void checkRule() throws SystemConfigErrorException {
        if (!mainClazz.isArray() && mainClazz != List.class && mainClazz != Set.class) {
            throw new SystemConfigErrorException("不支持的数组类型" + mainClazz.getName());
        }
        if (!Number.class.isAssignableFrom(subClazz) && String.class != subClazz
                && boolean.class != subClazz && Boolean.class != subClazz && Date.class != subClazz
                && !subClazz.isEnum()) {
            throw new SystemConfigErrorException("不支持的数组基础类型" + subClazz.getName());
        }
    }

    public ArrayRule(Class<T> mainClazz, Class<E> subClazz, FieldRule<?> subRule, boolean nullable,
            String speratorChar) throws SystemConfigErrorException {
        super(nullable);
        this.mainClazz = mainClazz;
        this.subRule = subRule;
        this.subClazz = subClazz;
        if (StringUtils.isNotBlank(speratorChar)) {
            this.speratorChar = speratorChar;
        }
        checkRule();

    }

    @SuppressWarnings("unchecked")
    public T getInstance(int length) {
        if (List.class == mainClazz) {
            return (T)new ArrayList<E>();
        } else if (Set.class == mainClazz) {
            return (T)new HashSet<E>();
        } else {
            return (T)Array.newInstance(subClazz, length);
        }
    }

    @SuppressWarnings("unchecked")
    public void put(T array, int index, E value) {
        if (List.class == mainClazz) {
            ((List<E>)array).add(value);
        } else if (Set.class == mainClazz) {
            ((Set<E>)array).add(value);
        } else {
            Array.set(array, index, value);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T checkAndGetValue(String value) throws DataNotValidException {
        if (StringUtils.isBlank(value) && nullable) {
            return null;
        }

        if (StringUtils.isBlank(value) && !nullable) {
            throw new IllegalArgumentException("值不能为空");
        }

        final String[] fields = value.split(speratorChar);

        T array = getInstance(fields.length);

        for (int i = 0; i < fields.length; i++) {

            try {
                E result = null;
                result = (E)subRule.checkAndGetValue(fields[i]);
                put(array, i, result);
            } catch (DataNotValidException e) {
                throw new DataNotValidException("在设置子字段时" + fields[i] + e.getMessage(), e);
            }

        }

        return array;

    }

}
