package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Field;

import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MBoolean;
import com.wmenjoy.utils.config.parser.rule.BooleanRule;
import com.wmenjoy.utils.config.parser.rule.FieldRule;

public class BooleanDef extends FieldDef {

    protected BooleanDef(final Field field, final Class<?> clazz, final FieldDef alias)
            throws SystemConfigErrorException {
        super(field, clazz, alias);

    }

    @Override
    public FieldRule<?> getFieldRule(final Field field, final Class<?> clazz) {
        MBoolean rule = null;
        if (field.isAnnotationPresent(MBoolean.class)) {
            rule = field.getAnnotation(MBoolean.class);
        }

        if (rule == null) {
            return new BooleanRule();
        } else {
            return new BooleanRule(rule.nullable(), rule.defaultValue());
        }
    }

}
