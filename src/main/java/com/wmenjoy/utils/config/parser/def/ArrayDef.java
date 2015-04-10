package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Field;

import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MArray;
import com.wmenjoy.utils.config.parser.rule.ArrayRule;
import com.wmenjoy.utils.config.parser.rule.FieldRule;

public class ArrayDef extends FieldDef {

    protected ArrayDef(final Field field, final Class<?> clazz, final FieldDef aliasFieldDef,
            final FieldDef subFd) throws SystemConfigErrorException {
        super(field, clazz, aliasFieldDef, subFd);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public FieldRule<?> getFieldRule(final Field field, final Class<?> clazz)
            throws SystemConfigErrorException {
        final Class<?> subClazz = this.subFieldDef.getClazz();
        final FieldRule<?> subRule = this.subFieldDef.getFieldRule(field, subClazz);
        if (field.isAnnotationPresent(MArray.class)) {
            final MArray rule = field.getAnnotation(MArray.class);
            return new ArrayRule(this.getClazz(), subClazz, subRule, rule.nullable(),
                    rule.speratorChar());
        } else {
            return new ArrayRule(this.getClazz(), subClazz, subRule);
        }
    }

}
