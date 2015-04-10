package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Field;
import java.util.Date;

import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MDate;
import com.wmenjoy.utils.config.parser.rule.DateRule;
import com.wmenjoy.utils.config.parser.rule.FieldRule;

public class DateDef extends FieldDef {

    protected DateDef(final Field field, final Class<Date> clazz, final FieldDef alias)
            throws SystemConfigErrorException {
        super(field, clazz, alias);
    }

    @Override
    public FieldRule<?> getFieldRule(final Field field, final Class<?> clazz) {
        MDate rule = null;
        if (field.isAnnotationPresent(MDate.class)) {
            rule = field.getAnnotation(MDate.class);
            return new DateRule(rule.nullable(), rule.format());
        } else {
            return new DateRule();
        }

    }

}
