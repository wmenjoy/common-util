package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wmenjoy.utils.config.parser.DataAccessErrorException;
import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.config.parser.ErrorAnnotationConfigException;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MAlias;
import com.wmenjoy.utils.config.parser.annotation.MArray;
import com.wmenjoy.utils.lang.StringUtils;
import com.wmenjoy.utils.lang.reflect.FieldUtil;

public abstract class FieldDefUtil {

	static final Map<Class<?>, Class<? extends FieldDef>> fieldDefMap = new HashMap<Class<?>, Class<? extends FieldDef>>();

	static {
		fieldDefMap.put(String.class, StringDef.class);
		fieldDefMap.put(int.class, NumberDef.class);
		fieldDefMap.put(Integer.class, NumberDef.class);
		fieldDefMap.put(byte.class, NumberDef.class);
		fieldDefMap.put(Byte.class, NumberDef.class);
		fieldDefMap.put(short.class, NumberDef.class);
		fieldDefMap.put(Short.class, NumberDef.class);
		fieldDefMap.put(long.class, NumberDef.class);
		fieldDefMap.put(Long.class, NumberDef.class);
		fieldDefMap.put(float.class, NumberDef.class);
		fieldDefMap.put(Float.class, NumberDef.class);
		fieldDefMap.put(double.class, NumberDef.class);
		fieldDefMap.put(Double.class, NumberDef.class);
		fieldDefMap.put(Date.class, DateDef.class);
		fieldDefMap.put(int[].class, ArrayDef.class);
		fieldDefMap.put(Integer[].class, ArrayDef.class);
		fieldDefMap.put(byte[].class, ArrayDef.class);
		fieldDefMap.put(Byte[].class, ArrayDef.class);
		fieldDefMap.put(long[].class, ArrayDef.class);
		fieldDefMap.put(Long[].class, ArrayDef.class);
		fieldDefMap.put(short[].class, ArrayDef.class);
		fieldDefMap.put(Short[].class, ArrayDef.class);
		fieldDefMap.put(float[].class, ArrayDef.class);
		fieldDefMap.put(Float[].class, ArrayDef.class);
		fieldDefMap.put(double[].class, ArrayDef.class);
		fieldDefMap.put(Double[].class, ArrayDef.class);
		fieldDefMap.put(List.class, ArrayDef.class);
		fieldDefMap.put(Set.class, ArrayDef.class);
		fieldDefMap.put(String[].class, ArrayDef.class);
		fieldDefMap.put(boolean.class, BooleanDef.class);
		fieldDefMap.put(Boolean.class, BooleanDef.class);
		fieldDefMap.put(boolean[].class, ArrayDef.class);
		fieldDefMap.put(Boolean[].class, ArrayDef.class);
		fieldDefMap.put(Date[].class, ArrayDef.class);
	}

	/**
	 * 解析具体的Field
	 * 
	 * @param field
	 * @param clazz
	 * @return
	 * @throws SystemConfigErrorException
	 * @throws DataAccessErrorException
	 * @throws ErrorAnnotationConfigException
	 */
	public static <T> FieldDef getFieldDef(final String fieldName,
			final Class<T> clazz) throws SystemConfigErrorException,
			ErrorAnnotationConfigException, DataAccessErrorException {
		final FieldDef fd;

		final Field field = getField(clazz, fieldName);
		Field aliasField = null;
		FieldDef subFieldDef = null;
		FieldDef aliasFd = null;
		boolean isArray = false;
		boolean aliasFieldIsArray = false;
		boolean hasAlias = false;
		if (isArray(field)) {
			subFieldDef = getSubFieldDef(field);
			isArray = true;
		}

		if (field.isAnnotationPresent(MAlias.class)) {
			hasAlias = true;
			final MAlias aliasAnnotation = field.getAnnotation(MAlias.class);
			final String aliasName = aliasAnnotation.value();
			aliasField = getField(clazz, aliasName);
			aliasFieldIsArray = isArray(aliasField);
		}

		if (hasAlias && aliasFieldIsArray) {
			aliasFd = initFieldDef(aliasField, (FieldDef) null,
					getSubFieldDef(aliasField));
		} else if (hasAlias) {
			aliasFd = initFieldDef(aliasField, aliasField.getType(), null);

		}

		if (isArray) {
			fd = initFieldDef(field, aliasFd, subFieldDef);
		} else {
			fd = initFieldDef(field, field.getType(), aliasFd);

		}
		return fd;
	}

	public static FieldDef getSubFieldDef(final Field field)
			throws SystemConfigErrorException {
		return initFieldDef(field, FieldUtil.getSubFieldClass(field), null);
	}

	public static boolean isArray(final Field field) {
		return FieldUtil.isArray(field) || FieldUtil.isListOrSet(field)
				|| field.isAnnotationPresent(MArray.class);
	}

	private static Field getField(final Class<?> clazz, final String fieldName)
			throws ErrorAnnotationConfigException, DataAccessErrorException {

		if (StringUtils.isBlank(fieldName)) {
			throw new ErrorAnnotationConfigException("MData配置错误， 字段名有空值");
		}

		try {
			final Field field = FieldUtil.getField(clazz, fieldName);

			if (FieldUtil.isObjectField(field)) {
				throw new ErrorAnnotationConfigException(
						"不支持final 和 static 的字段，请检查配置：" + fieldName);
			}

			return field;
		} catch (final SecurityException e) {
			throw new ErrorAnnotationConfigException("注解设置错误，没有" + fieldName
					+ "该字段，请检查配置是否正确！");
		} catch (final NoSuchFieldException e) {
			throw new DataAccessErrorException("注解设置错误，没有" + fieldName
					+ "该字段，请检查配置是否正确！");
		}
	}

	public static FieldDef initFieldDef(final Field field,
			final Class<?> clazz, final FieldDef aliasDef)
			throws SystemConfigErrorException {
		Class<? extends FieldDef> fieldDefClazz;

		if (!field.getType().isEnum()) {
			fieldDefClazz = fieldDefMap.get(clazz);
		} else {
			fieldDefClazz = EnumDef.class;
		}

		Constructor<? extends FieldDef> constructor;
		try {
			constructor = fieldDefClazz.getDeclaredConstructor(Field.class,
					Class.class, FieldDef.class);
			constructor.setAccessible(true);
			return constructor.newInstance(field, clazz, aliasDef);
		} catch (final SecurityException e) {
			throw new SystemConfigErrorException("无法访问"
					+ fieldDefClazz.getName() + "的构造函数", e);
		} catch (final NoSuchMethodException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "没有参数为Field, Class, FieldDef类型的构造函数", e);
		} catch (final IllegalArgumentException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		} catch (final InstantiationException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		} catch (final IllegalAccessException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		} catch (final InvocationTargetException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		}

	}

	public static FieldDef initFieldDef(final Field field,
			final FieldDef aliasFd, final FieldDef subFieldDef)
			throws SystemConfigErrorException {
		Class<? extends FieldDef> fieldDefClazz;

		if (!field.getType().isEnum()) {
			fieldDefClazz = fieldDefMap.get(field.getType());
		} else {
			fieldDefClazz = EnumDef.class;
		}

		try {
			final Constructor<? extends FieldDef> constructor = fieldDefClazz
					.getDeclaredConstructor(Field.class, Class.class,
							FieldDef.class, FieldDef.class);
			constructor.setAccessible(true);
			return constructor.newInstance(field, field.getType(), aliasFd,
					subFieldDef);
		} catch (final SecurityException e) {
			throw new SystemConfigErrorException("无法访问"
					+ fieldDefClazz.getName() + "的构造函数", e);
		} catch (final NoSuchMethodException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "没有参数为Field, clazz,FieldDef, FieldDef类型的构造函数", e);
		} catch (final IllegalArgumentException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		} catch (final InstantiationException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		} catch (final IllegalAccessException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		} catch (final InvocationTargetException e) {
			throw new SystemConfigErrorException(fieldDefClazz.getName()
					+ "初始化对象失败", e);
		}
	}

	public static <T> T getInstance(final Class<T> clazz)
			throws DataAccessErrorException {
		try {
			return clazz.newInstance();
		} catch (final InstantiationException e1) {
			throw new DataAccessErrorException(clazz.getName() + "初始化对象失败", e1);
		} catch (final IllegalAccessException e1) {
			throw new DataAccessErrorException("没有权限访问" + clazz.getName()
					+ "对象的构造函数", e1);
		}
	}

	public static void handle(final FieldDef fieldDef, final String value,
			final Object target) throws DataNotValidException,
			DataAccessErrorException {
		try {
			fieldDef.handle(value, target);
		} catch (final IllegalArgumentException e) {
			throw new DataNotValidException(fieldDef.getDeclareClassName()
					+ "对象的" + fieldDef.getName() + "字段在设置" + value + "的时候发生错误",
					e);
		} catch (final IllegalAccessException e) {
			throw new DataAccessErrorException("没有权限访问"
					+ fieldDef.getDeclareClassName() + "对象的"
					+ fieldDef.getName() + "字段", e);
		}
	}

	public static FieldDef getFieldDef(final String fieldName,
			final FieldDef aliasFieldDef, final Field field)
			throws SystemConfigErrorException {

		if (FieldDefUtil.isArray(field)) {
			final FieldDef subFieldDef = FieldDefUtil.getSubFieldDef(field);
			return FieldDefUtil.initFieldDef(field, aliasFieldDef, subFieldDef);
		} else {
			return FieldDefUtil.initFieldDef(field, field.getType(),
					aliasFieldDef);
		}
	}

}
