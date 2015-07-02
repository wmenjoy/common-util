package com.wmenjoy.utils.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wmenjoy.utils.lang.StringUtils;

public abstract class FieldUtil {

	/***
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isArray(final Field field) {
		return ClassUtil.isArray(field.getType());
	}

	public static boolean isList(final Field field) {
		return ClassUtil.isList(field.getType());
	}

	public static boolean isSet(final Field field) {
		return ClassUtil.isSet(field.getType());
	}

	public static boolean isListOrSet(final Field field) {
		return isList(field) || isSet(field);
	}

	public static boolean isCollection(final Field field) {
		return ClassUtil.isCollection(field.getType());
	}

	public static boolean isNumber(final Field field) {
		return ClassUtil.isNumber(field.getType());
	}

	public static boolean isFinal(final Field field) {
		return Modifier.isFinal(field.getModifiers());
	}

	public static boolean isStatic(final Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	public static boolean isObjectField(final Field field) {
		return !Modifier.isStatic(field.getModifiers())
				&& !Modifier.isFinal(field.getModifiers())
				&& !field.isSynthetic();
	}

	public static boolean isObjectPublicField(final Field field) {
		return Modifier.isPublic(field.getModifiers()) && isObjectField(field);
	}

	public static List<Field> getFields(final Class<?> clazz) {

		final List<Field> fieldList = new ArrayList<Field>();

		final Field[] fields = clazz.getDeclaredFields();

		for (final Field field : fields) {
			if (!isObjectField(field)) {
				continue;
			}
			fieldList.add(field);
		}

		if (clazz.getSuperclass() != Object.class) {
			fieldList.addAll(getFields(clazz.getSuperclass()));
		}

		return fieldList;
	}

	public static Class<?> getSubFieldClass(final Field field) {
		if (field == null) {
			throw new IllegalArgumentException("field 参数不能为空");
		}

		if (isArray(field)) {
			return field.getType().getComponentType();
		} else if (isListOrSet(field)) {
			final Type type = field.getGenericType();
			final ParameterizedType pType = (ParameterizedType) type;
			final Class<?> subClazz = (Class<?>) pType.getActualTypeArguments()[0];
			return subClazz;
		} else {
			throw new IllegalArgumentException("不支持subField的类型");
		}

	}

	public static Field getField(final Class<?> clazz, final String fieldName)
			throws NoSuchFieldException, SecurityException {

		if ((clazz == null) || StringUtils.isBlank(fieldName)) {
			throw new IllegalArgumentException("fieldName 或者clazz参数不能为空");
		}

		try {
			final Field field = clazz.getDeclaredField(fieldName);

			return field;
		} catch (final NoSuchFieldException e) {
			if (clazz.getSuperclass() != Object.class) {
				return getField(clazz.getSuperclass(), fieldName);
			}
			throw e;
		}
	}

	public static Map<String, Field> getBeanPropertyFields(Class<?> cl) {
		final Map<String, Field> properties = new HashMap<String, Field>();
		for (; cl != null; cl = cl.getSuperclass()) {
			final Field[] fields = cl.getDeclaredFields();
			for (final Field field : fields) {
				if (Modifier.isTransient(field.getModifiers())
						|| Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				field.setAccessible(true);

				properties.put(field.getName(), field);
			}
		}

		return properties;
	}
}
