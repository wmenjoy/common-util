package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Field;

import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MString;
import com.wmenjoy.utils.config.parser.rule.FieldRule;
import com.wmenjoy.utils.config.parser.rule.StringRule;

public class StringDef extends FieldDef {

    protected StringDef(final Field field, final Class<?> clazz, final FieldDef alias)
            throws SystemConfigErrorException {
        super(field, clazz, alias);
    }

    @Override
    public Object getAndCheckValue(final String value) throws DataNotValidException {
        return this.fieldRule.checkAndGetValue(value);
    }

    @Override
    public FieldRule<?> getFieldRule(final Field field, final Class<?> clazz) {
        MString rule = null;
        if (field.isAnnotationPresent(MString.class)) {
            rule = field.getAnnotation(MString.class);
            return new StringRule(rule.nullable(), rule.regex(), rule.escape(), rule.autoTrim(), "");
        } else {
            return new StringRule();
        }

    }
}
