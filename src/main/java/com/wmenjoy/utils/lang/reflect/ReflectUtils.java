/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wmenjoy.utils.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 qian.lei
 */
public final class ReflectUtils {

	public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

	public static final String JAVA_IDENT_REGEX = "(?:[_$a-zA-Z][_$a-zA-Z0-9]*)";

	public static final String JAVA_NAME_REGEX = "(?:" + JAVA_IDENT_REGEX
			+ "(?:\\." + JAVA_IDENT_REGEX + ")*)";

	public static final String CLASS_DESC = "(?:L" + JAVA_IDENT_REGEX
			+ "(?:\\/" + JAVA_IDENT_REGEX + ")*;)";

	public static final String ARRAY_DESC = "(?:\\[+(?:(?:[VZBCDFIJS])|"
			+ CLASS_DESC + "))";

	public static final String DESC_REGEX = "(?:(?:[VZBCDFIJS])|" + CLASS_DESC
			+ "|" + ARRAY_DESC + ")";

	public static final Pattern DESC_PATTERN = Pattern.compile(DESC_REGEX);

	public static final String METHOD_DESC_REGEX = "(?:(" + JAVA_IDENT_REGEX
			+ ")?\\((" + DESC_REGEX + "*)\\)(" + DESC_REGEX + ")?)";

	public static final Pattern METHOD_DESC_PATTERN = Pattern
			.compile(METHOD_DESC_REGEX);

	public static final Pattern GETTER_METHOD_DESC_PATTERN = Pattern
			.compile("get([A-Z][_a-zA-Z0-9]*)\\(\\)(" + DESC_REGEX + ")");

	public static final Pattern SETTER_METHOD_DESC_PATTERN = Pattern
			.compile("set([A-Z][_a-zA-Z0-9]*)\\((" + DESC_REGEX + ")\\)V");

	public static final Pattern IS_HAS_CAN_METHOD_DESC_PATTERN = Pattern
			.compile("(?:is|has|can)([A-Z][_a-zA-Z0-9]*)\\(\\)Z");

	private static final ConcurrentMap<String, Class<?>> DESC_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

	private static final ConcurrentMap<String, Class<?>> NAME_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

	private static final ConcurrentMap<String, Method> Signature_METHODS_CACHE = new ConcurrentHashMap<String, Method>();

	public static String getSignature(final String methodName,
			final Class<?>[] parameterTypes) {
		final StringBuilder sb = new StringBuilder(methodName);
		sb.append("(");
		if (parameterTypes != null && parameterTypes.length > 0) {
			boolean first = true;
			for (final Class<?> type : parameterTypes) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(type.getName());
			}
		}
		sb.append(")");
		return sb.toString();
	}



	/**
	 * get class desc. Object.class => "Ljava/lang/Object;" boolean[].class =>
	 * "[Z"
	 * 
	 * @param c
	 *            class.
	 * @return desc.
	 * @throws NotFoundException
	 */
	public static String getDesc(final CtClass c) throws NotFoundException {
		final StringBuilder ret = new StringBuilder();
		if (c.isArray()) {
			ret.append('[');
			ret.append(getDesc(c.getComponentType()));
		} else if (c.isPrimitive()) {
			final String t = c.getName();
			if ("void".equals(t)) {
				ret.append(JVM_VOID);
			} else if ("boolean".equals(t)) {
				ret.append(JVM_BOOLEAN);
			} else if ("byte".equals(t)) {
				ret.append(JVM_BYTE);
			} else if ("char".equals(t)) {
				ret.append(JVM_CHAR);
			} else if ("double".equals(t)) {
				ret.append(JVM_DOUBLE);
			} else if ("float".equals(t)) {
				ret.append(JVM_FLOAT);
			} else if ("int".equals(t)) {
				ret.append(JVM_INT);
			} else if ("long".equals(t)) {
				ret.append(JVM_LONG);
			} else if ("short".equals(t)) {
				ret.append(JVM_SHORT);
			}
		} else {
			ret.append('L');
			ret.append(c.getName().replace('.', '/'));
			ret.append(';');
		}
		return ret.toString();
	}

	/**
	 * get method desc. "do(I)I", "do()V", "do(Ljava/lang/String;Z)V"
	 * 
	 * @param m
	 *            method.
	 * @return desc.
	 */
	public static String getDesc(final CtMethod m) throws NotFoundException {
		final StringBuilder ret = new StringBuilder(m.getName()).append('(');
		final CtClass[] parameterTypes = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			ret.append(getDesc(parameterTypes[i]));
		}
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	/**
	 * get constructor desc. "()V", "(Ljava/lang/String;I)V"
	 * 
	 * @param c
	 *            constructor.
	 * @return desc
	 */
	public static String getDesc(final CtConstructor c)
			throws NotFoundException {
		final StringBuilder ret = new StringBuilder("(");
		final CtClass[] parameterTypes = c.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			ret.append(getDesc(parameterTypes[i]));
		}
		ret.append(')').append('V');
		return ret.toString();
	}

	/**
	 * get method desc. "(I)I", "()V", "(Ljava/lang/String;Z)V".
	 * 
	 * @param m
	 *            method.
	 * @return desc.
	 */
	public static String getDescWithoutMethodName(final CtMethod m)
			throws NotFoundException {
		final StringBuilder ret = new StringBuilder();
		ret.append('(');
		final CtClass[] parameterTypes = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			ret.append(getDesc(parameterTypes[i]));
		}
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	

	/**
	 * desc to name. "[[I" => "int[][]"
	 * 
	 * @param desc
	 *            desc.
	 * @return name.
	 */
	public static String desc2name(final String desc) {
		final StringBuilder sb = new StringBuilder();
		int c = desc.lastIndexOf('[') + 1;
		if (desc.length() == c + 1) {
			switch (desc.charAt(c)) {
			case JVM_VOID: {
				sb.append("void");
				break;
			}
			case JVM_BOOLEAN: {
				sb.append("boolean");
				break;
			}
			case JVM_BYTE: {
				sb.append("byte");
				break;
			}
			case JVM_CHAR: {
				sb.append("char");
				break;
			}
			case JVM_DOUBLE: {
				sb.append("double");
				break;
			}
			case JVM_FLOAT: {
				sb.append("float");
				break;
			}
			case JVM_INT: {
				sb.append("int");
				break;
			}
			case JVM_LONG: {
				sb.append("long");
				break;
			}
			case JVM_SHORT: {
				sb.append("short");
				break;
			}
			default:
				throw new RuntimeException();
			}
		} else {
			sb.append(desc.substring(c + 1, desc.length() - 1)
					.replace('/', '.'));
		}
		while (c-- > 0) {
			sb.append("[]");
		}
		return sb.toString();
	}

	public static Class<?> forName(final String name) {
		try {
			return name2class(name);
		} catch (final ClassNotFoundException e) {
			throw new IllegalStateException("Not found class " + name
					+ ", cause: " + e.getMessage(), e);
		}
	}

	/**
	 * name to class. "boolean" => boolean.class "java.util.Map[][]" =>
	 * java.util.Map[][].class
	 * 
	 * @param name
	 *            name.
	 * @return Class instance.
	 */
	public static Class<?> name2class(final String name)
			throws ClassNotFoundException {
		return name2class(ClassHelper.getClassLoader(), name);
	}

	/**
	 * name to class. "boolean" => boolean.class "java.util.Map[][]" =>
	 * java.util.Map[][].class
	 * 
	 * @param cl
	 *            ClassLoader instance.
	 * @param name
	 *            name.
	 * @return Class instance.
	 */
	private static Class<?> name2class(ClassLoader cl, String name)
			throws ClassNotFoundException {
		int c = 0;
		final int index = name.indexOf('[');
		if (index > 0) {
			c = (name.length() - index) / 2;
			name = name.substring(0, index);
		}
		if (c > 0) {
			final StringBuilder sb = new StringBuilder();
			while (c-- > 0) {
				sb.append("[");
			}

			if ("void".equals(name)) {
				sb.append(JVM_VOID);
			} else if ("boolean".equals(name)) {
				sb.append(JVM_BOOLEAN);
			} else if ("byte".equals(name)) {
				sb.append(JVM_BYTE);
			} else if ("char".equals(name)) {
				sb.append(JVM_CHAR);
			} else if ("double".equals(name)) {
				sb.append(JVM_DOUBLE);
			} else if ("float".equals(name)) {
				sb.append(JVM_FLOAT);
			} else if ("int".equals(name)) {
				sb.append(JVM_INT);
			} else if ("long".equals(name)) {
				sb.append(JVM_LONG);
			} else if ("short".equals(name)) {
				sb.append(JVM_SHORT);
			} else {
				sb.append('L').append(name).append(';'); // "java.lang.Object"
															// ==>
															// "Ljava.lang.Object;"
			}
			name = sb.toString();
		} else {
			if ("void".equals(name)) {
				return void.class;
			} else if ("boolean".equals(name)) {
				return boolean.class;
			} else if ("byte".equals(name)) {
				return byte.class;
			} else if ("char".equals(name)) {
				return char.class;
			} else if ("double".equals(name)) {
				return double.class;
			} else if ("float".equals(name)) {
				return float.class;
			} else if ("int".equals(name)) {
				return int.class;
			} else if ("long".equals(name)) {
				return long.class;
			} else if ("short".equals(name)) {
				return short.class;
			}
		}

		if (cl == null) {
			cl = ClassHelper.getClassLoader();
		}
		Class<?> clazz = NAME_CLASS_CACHE.get(name);
		if (clazz == null) {
			clazz = Class.forName(name, true, cl);
			NAME_CLASS_CACHE.put(name, clazz);
		}
		return clazz;
	}

	/**
	 * desc to class. "[Z" => boolean[].class "[[Ljava/util/Map;" =>
	 * java.util.Map[][].class
	 * 
	 * @param desc
	 *            desc.
	 * @return Class instance.
	 * @throws ClassNotFoundException
	 */
	public static Class<?> desc2class(final String desc)
			throws ClassNotFoundException {
		return desc2class(ClassHelper.getClassLoader(), desc);
	}

	/**
	 * desc to class. "[Z" => boolean[].class "[[Ljava/util/Map;" =>
	 * java.util.Map[][].class
	 * 
	 * @param cl
	 *            ClassLoader instance.
	 * @param desc
	 *            desc.
	 * @return Class instance.
	 * @throws ClassNotFoundException
	 */
	private static Class<?> desc2class(ClassLoader cl, String desc)
			throws ClassNotFoundException {
		switch (desc.charAt(0)) {
		case JVM_VOID:
			return void.class;
		case JVM_BOOLEAN:
			return boolean.class;
		case JVM_BYTE:
			return byte.class;
		case JVM_CHAR:
			return char.class;
		case JVM_DOUBLE:
			return double.class;
		case JVM_FLOAT:
			return float.class;
		case JVM_INT:
			return int.class;
		case JVM_LONG:
			return long.class;
		case JVM_SHORT:
			return short.class;
		case 'L':
			desc = desc.substring(1, desc.length() - 1).replace('/', '.'); // "Ljava/lang/Object;"
																			// ==>
																			// "java.lang.Object"
			break;
		case '[':
			desc = desc.replace('/', '.'); // "[[Ljava/lang/Object;" ==>
											// "[[Ljava.lang.Object;"
			break;
		default:
			throw new ClassNotFoundException("Class not found: " + desc);
		}

		if (cl == null) {
			cl = ClassHelper.getClassLoader();
		}
		Class<?> clazz = DESC_CLASS_CACHE.get(desc);
		if (clazz == null) {
			clazz = Class.forName(desc, true, cl);
			DESC_CLASS_CACHE.put(desc, clazz);
		}
		return clazz;
	}

	/**
	 * get class array instance.
	 * 
	 * @param desc
	 *            desc.
	 * @return Class class array.
	 * @throws ClassNotFoundException
	 */
	public static Class<?>[] desc2classArray(final String desc)
			throws ClassNotFoundException {
		final Class<?>[] ret = desc2classArray(ClassHelper.getClassLoader(),
				desc);
		return ret;
	}

	/**
	 * get class array instance.
	 * 
	 * @param cl
	 *            ClassLoader instance.
	 * @param desc
	 *            desc.
	 * @return Class[] class array.
	 * @throws ClassNotFoundException
	 */
	private static Class<?>[] desc2classArray(final ClassLoader cl,
			final String desc) throws ClassNotFoundException {
		if (desc.length() == 0) {
			return EMPTY_CLASS_ARRAY;
		}

		final List<Class<?>> cs = new ArrayList<Class<?>>();
		final Matcher m = DESC_PATTERN.matcher(desc);
		while (m.find()) {
			cs.add(desc2class(cl, m.group()));
		}
		return cs.toArray(EMPTY_CLASS_ARRAY);
	}

	/**
	 * 根据方法签名从类中找出方法。
	 * 
	 * @param clazz
	 *            查找的类。
	 * @param methodName
	 *            方法签名，形如method1(int, String)。也允许只给方法名不参数只有方法名，形如method2。
	 * @return 返回查找到的方法。
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws IllegalStateException
	 *             给定的方法签名找到多个方法（方法签名中没有指定参数，又有有重载的方法的情况）
	 */
	public static Method findMethodByMethodSignature(final Class<?> clazz,
			final String methodName, final String[] parameterTypes)
			throws NoSuchMethodException, ClassNotFoundException {
		String signature = methodName;
		if (parameterTypes != null && parameterTypes.length > 0) {
			signature = methodName + StringUtils.join(parameterTypes);
		}
		Method method = Signature_METHODS_CACHE.get(signature);
		if (method != null) {
			return method;
		}
		if (parameterTypes == null) {
			final List<Method> finded = new ArrayList<Method>();
			for (final Method m : clazz.getMethods()) {
				if (m.getName().equals(methodName)) {
					finded.add(m);
				}
			}
			if (finded.isEmpty()) {
				throw new NoSuchMethodException("No such method " + methodName
						+ " in class " + clazz);
			}
			if (finded.size() > 1) {
				final String msg = String
						.format("Not unique method for method name(%s) in class(%s), find %d methods.",
								methodName, clazz.getName(), finded.size());
				throw new IllegalStateException(msg);
			}
			method = finded.get(0);
		} else {
			final Class<?>[] types = new Class<?>[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				types[i] = ReflectUtils.name2class(parameterTypes[i]);
			}
			method = clazz.getMethod(methodName, types);

		}
		Signature_METHODS_CACHE.put(signature, method);
		return method;
	}

	public static Method findMethodByMethodName(final Class<?> clazz,
			final String methodName) throws NoSuchMethodException,
			ClassNotFoundException {
		return findMethodByMethodSignature(clazz, methodName, null);
	}

	public static boolean isBeanPropertyReadMethod(final Method method) {
		return method != null
				&& Modifier.isPublic(method.getModifiers())
				&& !Modifier.isStatic(method.getModifiers())
				&& method.getReturnType() != void.class
				&& method.getDeclaringClass() != Object.class
				&& method.getParameterTypes().length == 0
				&& ((method.getName().startsWith("get") && method.getName()
						.length() > 3) || (method.getName().startsWith("is") && method
						.getName().length() > 2));
	}

	public static String getPropertyNameFromBeanReadMethod(final Method method) {
		if (isBeanPropertyReadMethod(method)) {
			if (method.getName().startsWith("get")) {
				return method.getName().substring(3, 4).toLowerCase()
						+ method.getName().substring(4);
			}
			if (method.getName().startsWith("is")) {
				return method.getName().substring(2, 3).toLowerCase()
						+ method.getName().substring(3);
			}
		}
		return null;
	}

	public static boolean isBeanPropertyWriteMethod(final Method method) {
		return method != null && Modifier.isPublic(method.getModifiers())
				&& !Modifier.isStatic(method.getModifiers())
				&& method.getDeclaringClass() != Object.class
				&& method.getParameterTypes().length == 1
				&& method.getName().startsWith("set")
				&& method.getName().length() > 3;
	}

	public static String getPropertyNameFromBeanWriteMethod(final Method method) {
		if (isBeanPropertyWriteMethod(method)) {
			return method.getName().substring(3, 4).toLowerCase()
					+ method.getName().substring(4);
		}
		return null;
	}

	public static Map<String, Field> getBeanPropertyFields(Class cl) {
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

	public static Map<String, Method> getBeanPropertyReadMethods(Class cl) {
		final Map<String, Method> properties = new HashMap<String, Method>();
		for (; cl != null; cl = cl.getSuperclass()) {
			final Method[] methods = cl.getDeclaredMethods();
			for (final Method method : methods) {
				if (isBeanPropertyReadMethod(method)) {
					method.setAccessible(true);
					final String property = getPropertyNameFromBeanReadMethod(method);
					properties.put(property, method);
				}
			}
		}

		return properties;
	}

	private ReflectUtils() {
	}
}