package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Field;

import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MNumber;
import com.wmenjoy.utils.config.parser.rule.FieldRule;
import com.wmenjoy.utils.config.parser.rule.NumberRuleUtil;

public class NumberDef extends FieldDef {

    protected NumberDef(final Field field, final Class<?> clazz, final FieldDef alias)
            throws SystemConfigErrorException {
        super(field, clazz, alias);

    }

    @Override
    public FieldRule<?> getFieldRule(final Field field, final Class<?> clazz) {
        MNumber rule = null;
        if (field.isAnnotationPresent(MNumber.class)) {
            rule = field.getAnnotation(MNumber.class);
        }
        return NumberRuleUtil.getNumberRule(rule, clazz, "");
    }

}
