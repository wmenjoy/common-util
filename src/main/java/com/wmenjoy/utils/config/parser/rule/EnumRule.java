package com.wmenjoy.utils.config.parser.rule;

import java.util.HashMap;
import java.util.Map;

import com.wmenjoy.utils.config.parser.DataAccessErrorException;
import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MEnum.Type;
import com.wmenjoy.utils.lang.StringUtils;

public class EnumRule<T extends Enum<T>> extends BaseFieldRule<T> {

	final Type type;
	final Class<T> clazz;
	final boolean nullable;
	Map<String, T> enumMap;

	public static <T extends Enum<T>> EnumRule<T> getEnumRule(final Type type,
			final Class<T> clazz, final boolean nullable)
			throws SystemConfigErrorException {
		return new EnumRule<T>(type, clazz, nullable);
	}

	private EnumRule(final Type type, final Class<T> clazz,
			final boolean nullable) throws SystemConfigErrorException {
		this.type = type;
		this.clazz = clazz;
		this.nullable = nullable;

		if (clazz == null) {
			throw new IllegalArgumentException("clazz is null");
		}

		if (!clazz.isEnum()) {
			throw new IllegalArgumentException("clazz is not enum!");
		}

		this.enumMap = new HashMap<String, T>();

		final T[] enums = clazz.getEnumConstants();

		for (final T t : enums) {
			final Enum enumValue = t;

			if (type == Type.STRING) {
				final String name = enumValue.name();
				this.enumMap.put(name, t);
			} else {
				this.enumMap.put(enumValue.ordinal() + "", t);
			}
		}
	}

	@Override
	public T checkAndGetValue(final String value) throws DataNotValidException {

		if (StringUtils.isBlank(value) && this.nullable) {
			return null;
		}

		if (StringUtils.isBlank(value) && !this.nullable) {
			throw new DataNotValidException("value 参数不能为空");
		}

		final T result = this.enumMap.get(value);

		if (result == null) {
			throw new DataNotValidException("value没有对应的枚举值" + value);
		}

		return result;
	}

	public static enum ke {
		KEY, C;
	}

	public static void main(final String[] args)
			throws DataAccessErrorException, DataNotValidException,
			SystemConfigErrorException {

		final EnumRule<?> rule = new EnumRule<EnumRule.ke>(Type.STRING,
				ke.class, true);

		;
		System.out.println(rule.checkAndGetValue("KEY"));
	}
}
