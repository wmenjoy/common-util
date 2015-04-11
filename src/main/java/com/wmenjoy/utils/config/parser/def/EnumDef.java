package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Field;

import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MEnum;
import com.wmenjoy.utils.config.parser.annotation.MEnum.Type;
import com.wmenjoy.utils.config.parser.rule.EnumRule;
import com.wmenjoy.utils.config.parser.rule.FieldRule;

public class EnumDef extends FieldDef {

	protected EnumDef(final Field field, final Class<?> clazz,
			final FieldDef alias) throws SystemConfigErrorException {
		super(field, clazz, alias);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public FieldRule<?> getFieldRule(final Field field, final Class<?> clazz)
			throws SystemConfigErrorException {

		final Type type;
		final boolean nullable;
		if (field.isAnnotationPresent(MEnum.class)) {
			final MEnum menum = field.getAnnotation(MEnum.class);
			type = menum.type();
			nullable = menum.nullable();
		} else {
			type = Type.STRING;
			nullable = true;
		}

		return EnumRule.getEnumRule(type, ((Class<Enum>) clazz), nullable);
	}

}
