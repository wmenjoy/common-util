package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Field;

import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.rule.FieldRule;
import com.wmenjoy.utils.lang.StringUtils;


/**
 * 配置文件，字段的定义基类 使用配置文件： 每个文件的每一行可以映射为复杂的一个类
 *
 * @version 1.0
 */
public abstract class FieldDef {

    /** 关联对应的Field */
    protected Field field;

    protected Class<?> clazz;

    protected String fieldName;
    /**
     * 别名
     */
    protected FieldDef alias;

    /**
     * 子Field
     */
    protected FieldDef subFieldDef;

    FieldRule<?> fieldRule;

    protected FieldDef(final Field field, final Class<?> clazz, final FieldDef alias)
            throws SystemConfigErrorException {
        this.field = field;
        this.fieldName = field.getName();
        this.field.setAccessible(true);
        this.clazz = clazz;
        this.alias = alias;
        this.fieldRule = this.getFieldRule(field, clazz);
    }

    protected FieldDef(final Field field, final Class<?> clazz, final FieldDef alias, final FieldDef subFieldDef)
            throws SystemConfigErrorException {
        this.field = field;
        this.fieldName = field.getName();
        this.field.setAccessible(true);
        this.clazz = clazz;
        this.alias = alias;
        this.subFieldDef = subFieldDef;
        this.fieldRule = this.getFieldRule(field, clazz);
    }

    public abstract FieldRule<?> getFieldRule(Field field, Class<?> clazz)
            throws SystemConfigErrorException;

    public boolean nullable() {
        return this.fieldRule.nullable();
    };

    public Object getAndCheckValue(final String value) throws DataNotValidException {
        return this.fieldRule.checkAndGetValue(value);
    };

    public FieldDef getAlias() {
        return this.alias;
    }

    public String getDeclareClassName() {
        return this.field.getDeclaringClass().getName();
    }

    protected Class<?> getClazz() {
        return this.clazz;
    };

    public void handle(final String value, final Object target) throws IllegalArgumentException,
            IllegalAccessException, DataNotValidException {
        if (target == null) {
            return;
        }
        try {
            /***
             * 使用对象本身的默认值
             */
            if (StringUtils.isBlank(value) && this.nullable()) {
                return;
            }

            this.field.set(target, this.getAndCheckValue(value));
        } catch (final DataNotValidException e) {
            throw new DataNotValidException(target.getClass().getName() + "的字段" + this.fieldName
                    + e.getMessage(), e);
        }
    }

    public String getName() {
        return this.fieldName;
    }
}
