package com.wmenjoy.utils.lang.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ClassUtil {

	public static boolean isArray(final Class<?> clazz) {
		return (clazz != null) && clazz.isArray();
	}

	public static boolean isList(final Class<?> clazz) {
		return (clazz != null) && (clazz == List.class);
	}

	public static boolean isSet(final Class<?> clazz) {
		return (clazz != null) && (clazz == Set.class);
	}

	public static boolean isCollection(final Class<?> clazz) {
		return (clazz != null) && Collection.class.isAssignableFrom(clazz);
	}

	public static boolean isNumber(final Class<?> clazz) {
		return (clazz != null) && Number.class.isAssignableFrom(clazz);
	}

	public static <T> T getInstance(final Class<T> clazz)
			throws InstantiationException, IllegalAccessException {
		return clazz == null ? null : clazz.newInstance();
	}

	public static boolean isEnum(final Class<?> clazz) {
		return (clazz != null) && clazz.isEnum();
	}

	public static Class<?> forNameWithThreadContextClassLoader(final String name)
			throws ClassNotFoundException {
		return forName(name, Thread.currentThread().getContextClassLoader());
	}

	public static Class<?> forNameWithCallerClassLoader(final String name,
			final Class<?> caller) throws ClassNotFoundException {
		return forName(name, caller.getClassLoader());
	}

	public static ClassLoader getCallerClassLoader(final Class<?> caller) {
		return caller.getClassLoader();
	}

	/**
	 * get class loader
	 * 
	 * @param cls
	 * @return class loader
	 */
	public static ClassLoader getClassLoader(final Class<?> cls) {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (final Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system
			// class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = cls.getClassLoader();
		}
		return cl;
	}

	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
	 * class will be used as fallback.
	 * <p>
	 * Call this method if you intend to use the thread context ClassLoader in a
	 * scenario where you absolutely need a non-null ClassLoader reference: for
	 * example, for class path resource loading (but not necessarily for
	 * <code>Class.forName</code>, which accepts a <code>null</code> ClassLoader
	 * reference as well).
	 * 
	 * @return the default ClassLoader (never <code>null</code>)
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public static ClassLoader getClassLoader() {
		return getClassLoader(ClassUtil.class);
	}

	/**
	 * Same as <code>Class.forName()</code>, except that it works for primitive
	 * types.
	 */
	public static Class<?> forName(final String name)
			throws ClassNotFoundException {
		return forName(name, getClassLoader());
	}

	/**
	 * Replacement for <code>Class.forName()</code> that also returns Class
	 * instances for primitives (like "int") and array class names (like
	 * "String[]").
	 * 
	 * @param name
	 *            the name of the Class
	 * @param classLoader
	 *            the class loader to use (may be <code>null</code>, which
	 *            indicates the default class loader)
	 * @return Class instance for the supplied name
	 * @throws ClassNotFoundException
	 *             if the class was not found
	 * @throws LinkageError
	 *             if the class file could not be loaded
	 * @see Class#forName(String, boolean, ClassLoader)
	 */
	public static Class<?> forName(final String name,
			final ClassLoader classLoader) throws ClassNotFoundException,
			LinkageError {

		final Class<?> clazz = resolvePrimitiveClassName(name);
		if (clazz != null) {
			return clazz;
		}

		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)) {
			final String elementClassName = name.substring(0, name.length()
					- ARRAY_SUFFIX.length());
			final Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[Ljava.lang.String;" style arrays
		final int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
		if (internalArrayMarker != -1 && name.endsWith(";")) {
			String elementClassName = null;
			if (internalArrayMarker == 0) {
				elementClassName = name.substring(
						INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
			} else if (name.startsWith("[")) {
				elementClassName = name.substring(1);
			}
			final Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getClassLoader();
		}
		return classLoaderToUse.loadClass(name);
	}

	/**
	 * Resolve the given class name as primitive class, if appropriate,
	 * according to the JVM's naming rules for primitive classes.
	 * <p>
	 * Also supports the JVM's internal class names for primitive arrays. Does
	 * <i>not</i> support the "[]" suffix notation for primitive arrays; this is
	 * only supported by {@link #forName}.
	 * 
	 * @param name
	 *            the name of the potentially primitive class
	 * @return the primitive class, or <code>null</code> if the name does not
	 *         denote a primitive class or primitive array class
	 */
	public static Class<?> resolvePrimitiveClassName(final String name) {
		Class<?> result = null;
		// Most class names will be quite long, considering that they
		// SHOULD sit in a package, so a length check is worthwhile.
		if (name != null && name.length() <= 8) {
			// Could be a primitive - likely.
			result = primitiveTypeNameMap.get(name);
		}
		return result;
	}

	/** 数组class名字的后缀 */
	public static final String ARRAY_SUFFIX = "[]";
	/** Prefix for internal array class names: "[L" */
	private static final String INTERNAL_ARRAY_PREFIX = "[L";

	/**
	 * 原始类型名字，到原始类型的映射， forName不支持原始类型
	 */
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(
			16);

	/**
	 * 包装类到原始类型的映射
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(
			8);

	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(
			8);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);

		primitiveTypeToWrapperMap.put(boolean.class, Boolean.class);
		primitiveTypeToWrapperMap.put(byte.class, Byte.class);
		primitiveTypeToWrapperMap.put(char.class, Character.class);
		primitiveTypeToWrapperMap.put(double.class, Double.class);
		primitiveTypeToWrapperMap.put(float.class, Float.class);
		primitiveTypeToWrapperMap.put(int.class, Integer.class);
		primitiveTypeToWrapperMap.put(long.class, Long.class);
		primitiveTypeToWrapperMap.put(short.class, Short.class);

		final Set<Class<?>> primitiveTypeNames = new HashSet<Class<?>>(16);
		primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
		primitiveTypeNames.addAll(Arrays.asList(new Class<?>[] {
				boolean[].class, byte[].class, char[].class, double[].class,
				float[].class, int[].class, long[].class, short[].class }));
		for (final Iterator<Class<?>> it = primitiveTypeNames.iterator(); it
				.hasNext();) {
			final Class<?> primitiveClass = it.next();
			primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
		}
	}

	public static String toShortString(final Object obj) {
		if (obj == null) {
			return "null";
		}
		return obj.getClass().getSimpleName() + "@"
				+ System.identityHashCode(obj);

	}

	/**
	 * class和对象是否是兼容的
	 * 
	 * @param c
	 * @param o
	 * @return
	 */
	public static boolean isCompatible(final Class<?> c, final Object o) {
		final boolean pt = c.isPrimitive();
		if (o == null) {
			return !pt;
		}

		final Class<?> realClass = pt ? primitiveTypeToWrapperMap.get(c) : c;

		if (realClass == o.getClass()) {
			return true;
		}
		return c.isInstance(o);
	}

	/**
	 * is compatible.
	 * 
	 * @param cs
	 *            class array.
	 * @param os
	 *            object array.
	 * @return compatible or not.
	 */
	public static boolean isCompatible(final Class<?>[] cs, final Object[] os) {
		final int len = cs.length;
		if (len != os.length) {
			return false;
		}
		if (len == 0) {
			return true;
		}
		for (int i = 0; i < len; i++) {
			if (!isCompatible(cs[i], os[i])) {
				return false;
			}
		}
		return true;
	}

	public static Object getEmptyObject(final Class<?> returnType) {
		return getEmptyObject(returnType, new HashMap<Class<?>, Object>(), 0);
	}

	private static Object getEmptyObject(final Class<?> returnType,
			final Map<Class<?>, Object> emptyInstances, final int level) {
		if (level > 2) {
			return null;
		}
		if (returnType == null) {
			return null;
		} else if (returnType == boolean.class || returnType == Boolean.class) {
			return false;
		} else if (returnType == char.class || returnType == Character.class) {
			return '\0';
		} else if (returnType == byte.class || returnType == Byte.class) {
			return (byte) 0;
		} else if (returnType == short.class || returnType == Short.class) {
			return (short) 0;
		} else if (returnType == int.class || returnType == Integer.class) {
			return 0;
		} else if (returnType == long.class || returnType == Long.class) {
			return 0L;
		} else if (returnType == float.class || returnType == Float.class) {
			return 0F;
		} else if (returnType == double.class || returnType == Double.class) {
			return 0D;
		} else if (returnType.isArray()) {
			return Array.newInstance(returnType.getComponentType(), 0);
		} else if (returnType.isAssignableFrom(ArrayList.class)) {
			return new ArrayList<Object>(0);
		} else if (returnType.isAssignableFrom(HashSet.class)) {
			return new HashSet<Object>(0);
		} else if (returnType.isAssignableFrom(HashMap.class)) {
			return new HashMap<Object, Object>(0);
		} else if (String.class.equals(returnType)) {
			return "";
		} else if (!returnType.isInterface()) {
			try {
				Object value = emptyInstances.get(returnType);
				if (value == null) {
					value = returnType.newInstance();
					emptyInstances.put(returnType, value);
				}
				Class<?> cls = value.getClass();
				while (cls != null && cls != Object.class) {
					final Field[] fields = cls.getDeclaredFields();
					for (final Field field : fields) {
						final Object property = getEmptyObject(field.getType(),
								emptyInstances, level + 1);
						if (property != null) {
							try {
								if (!field.isAccessible()) {
									field.setAccessible(true);
								}
								field.set(value, property);
							} catch (final Throwable e) {
							}
						}
					}
					cls = cls.getSuperclass();
				}
				return value;
			} catch (final Throwable e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public static boolean isPrimitives(final Class<?> cls) {
		if (cls.isArray()) {
			return isPrimitive(cls.getComponentType());
		}
		return isPrimitive(cls);
	}

	public static boolean isPrimitive(final Class<?> cls) {
		return cls.isPrimitive() || cls == String.class || cls == Boolean.class
				|| cls == Character.class || Number.class.isAssignableFrom(cls)
				|| Date.class.isAssignableFrom(cls);
	}

	public static Constructor<?> findConstructor(final Class<?> clazz,
			final Class<?> paramType) throws NoSuchMethodException {
		Constructor<?> targetConstructor;
		try {
			targetConstructor = clazz
					.getConstructor(new Class<?>[] { paramType });
		} catch (final NoSuchMethodException e) {
			targetConstructor = null;
			final Constructor<?>[] constructors = clazz.getConstructors();
			for (final Constructor<?> constructor : constructors) {
				if (Modifier.isPublic(constructor.getModifiers())
						&& constructor.getParameterTypes().length == 1
						&& constructor.getParameterTypes()[0]
								.isAssignableFrom(paramType)) {
					targetConstructor = constructor;
					break;
				}
			}
			if (targetConstructor == null) {
				throw e;
			}
		}
		return targetConstructor;
	}

	/**
	 * 检查对象是否是指定接口的实现。
	 * <p>
	 * 不会触发到指定接口的{@link Class}，所以如果ClassLoader中没有指定接口类时，也不会出错。
	 * 
	 * @param obj
	 *            要检查的对象
	 * @param interfaceClazzName
	 *            指定的接口名
	 * @return 返回{@code true}，如果对象实现了指定接口；否则返回{@code false}。
	 */
	public static boolean isInstance(final Object obj,
			final String interfaceClazzName) {
		for (Class<?> clazz = obj.getClass(); clazz != null
				&& !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
			final Class<?>[] interfaces = clazz.getInterfaces();
			for (final Class<?> itf : interfaces) {
				if (itf.getName().equals(interfaceClazzName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Class<?> getWrapperClass(final Class<?> c) {
		return c.isPrimitive() ? primitiveTypeToWrapperMap.get(c) : c;
	}

	/**
	 * 获取类路径
	 * 
	 * @param cls
	 * @return
	 */
	public static String getCodeBasePath(final Class<?> cls) {
		if (cls == null) {
			return null;
		}
		final ProtectionDomain domain = cls.getProtectionDomain();
		if (domain == null) {
			return null;
		}
		final CodeSource source = domain.getCodeSource();
		if (source == null) {
			return null;
		}
		final URL location = source.getLocation();
		if (location == null) {
			return null;
		}
		return location.getFile();
	}

	public static Class<?> getGenericClass(final Class<?> cls) {
		return getGenericClass(cls, 0);
	}

	public static Class<?> getGenericClass(final Class<?> cls, final int i) {
		try {
			final ParameterizedType parameterizedType = ((ParameterizedType) cls
					.getGenericInterfaces()[0]);
			final Object genericClass = parameterizedType
					.getActualTypeArguments()[i];
			if (genericClass instanceof ParameterizedType) { // 处理多级泛型
				return (Class<?>) ((ParameterizedType) genericClass)
						.getRawType();
			} else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
				return (Class<?>) ((GenericArrayType) genericClass)
						.getGenericComponentType();
			} else {
				return (Class<?>) genericClass;
			}
		} catch (final Throwable e) {
			throw new IllegalArgumentException(cls.getName()
					+ " generic type undefined!", e);
		}
	}

	/**
	 * get method name. "void do(int)", "void do()",
	 * "int do(java.lang.String,boolean)"
	 * 
	 * @param m
	 *            method.
	 * @return name.
	 */
	public static String getName(final Method m) {
		final StringBuilder ret = new StringBuilder();
		ret.append(getName(m.getReturnType())).append(' ');
		ret.append(m.getName()).append('(');
		final Class<?>[] parameterTypes = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (i > 0) {
				ret.append(',');
			}
			ret.append(getName(parameterTypes[i]));
		}
		ret.append(')');
		return ret.toString();
	}

	/**
	 * get constructor name. "()", "(java.lang.String,int)"
	 * 
	 * @param c
	 *            constructor.
	 * @return name.
	 */
	public static String getName(final Constructor<?> c) {
		final StringBuilder ret = new StringBuilder("(");
		final Class<?>[] parameterTypes = c.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			if (i > 0) {
				ret.append(',');
			}
			ret.append(getName(parameterTypes[i]));
		}
		ret.append(')');
		return ret.toString();
	}

	/**
	 * get class desc. boolean[].class => "[Z" Object.class =>
	 * "Ljava/lang/Object;"
	 * 
	 * @param c
	 *            class.
	 * @return desc.
	 * @throws NotFoundException
	 */
	public static String getDesc(Class<?> c) {
		final StringBuilder ret = new StringBuilder();

		while (c.isArray()) {
			ret.append('[');
			c = c.getComponentType();
		}

		if (c.isPrimitive()) {
			ret.append(primitiveTypeJVMExprMap.get(c));
		} else {
			ret.append('L');
			ret.append(c.getName().replace('.', '/'));
			ret.append(';');
		}
		return ret.toString();
	}

	/**
	 * get class array desc. [int.class, boolean[].class, Object.class] =>
	 * "I[ZLjava/lang/Object;"
	 * 
	 * @param cs
	 *            class array.
	 * @return desc.
	 * @throws NotFoundException
	 */
	public static String getDesc(final Class<?>[] cs) {
		if (cs.length == 0) {
			return "";
		}

		final StringBuilder sb = new StringBuilder(64);
		for (final Class<?> c : cs) {
			sb.append(getDesc(c));
		}
		return sb.toString();
	}

	/**
	 * get method desc. int do(int arg1) => "do(I)I" void do(String arg1,boolean
	 * arg2) => "do(Ljava/lang/String;Z)V"
	 * 
	 * @param m
	 *            method.
	 * @return desc.
	 */
	public static String getDesc(final Method m) {
		final StringBuilder ret = new StringBuilder(m.getName()).append('(');
		final Class<?>[] parameterTypes = m.getParameterTypes();
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
	public static String getDesc(final Constructor<?> c) {
		final StringBuilder ret = new StringBuilder("(");
		final Class<?>[] parameterTypes = c.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			ret.append(getDesc(parameterTypes[i]));
		}
		ret.append(')').append('V');
		return ret.toString();
	}

	/**
	 * get method desc. "(I)I", "()V", "(Ljava/lang/String;Z)V"
	 * 
	 * @param m
	 *            method.
	 * @return desc.
	 */
	public static String getDescWithoutMethodName(final Method m) {
		final StringBuilder ret = new StringBuilder();
		ret.append('(');
		final Class<?>[] parameterTypes = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			ret.append(getDesc(parameterTypes[i]));
		}
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	/**
	 * name to desc. java.util.Map[][] => "[[Ljava/util/Map;"
	 * 
	 * @param name
	 *            name.
	 * @return desc.
	 */
	public static String name2desc(final String name) {
		final StringBuilder sb = new StringBuilder();
		String realName = name;
		int c = 0;
		final int index = realName.indexOf('[');
		if (index > 0) {
			c = (realName.length() - index) / 2;
			realName = realName.substring(0, index);
		}
		while (c-- > 0) {
			sb.append("[");
		}

		final Class<?> primitiveType = "void".equals(realName) ? void.class
				: primitiveTypeNameMap.get(realName);

		if (primitiveType != null) {
			sb.append(primitiveTypeJVMExprMap.get(primitiveType));
		} else {
			sb.append('L').append(realName.replace('.', '/')).append(';');
		}
		return sb.toString();
	}

	/**
	 * get name. java.lang.Object[][].class => "java.lang.Object[][]"
	 * 
	 * @param c
	 *            class.
	 * @return name.
	 */
	public static String getName(final Class<?> c) {
		Class<?> realClass = c;
		if (realClass.isArray()) {
			final StringBuilder sb = new StringBuilder();
			do {
				sb.append("[]");
				realClass = realClass.getComponentType();
			} while (realClass.isArray());

			return realClass.getName() + sb.toString();
		}
		return realClass.getName();
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
			final Class<?> clazz = JVMExprToPrimitiveTypeMap
					.get(desc.charAt(c));

			if (clazz != null) {
				sb.append(clazz.getName());
			} else {
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

	private final static Map<Class<?>, Character> primitiveTypeJVMExprMap = new HashMap<Class<?>, Character>();
	private final static Map<Character, Class<?>> JVMExprToPrimitiveTypeMap = new HashMap<Character, Class<?>>();

	static {
		primitiveTypeJVMExprMap.put(void.class, 'V');
		primitiveTypeJVMExprMap.put(int.class, 'I');
		primitiveTypeJVMExprMap.put(boolean.class, 'Z');
		primitiveTypeJVMExprMap.put(byte.class, 'B');
		primitiveTypeJVMExprMap.put(char.class, 'C');
		primitiveTypeJVMExprMap.put(double.class, 'D');
		primitiveTypeJVMExprMap.put(float.class, 'F');
		primitiveTypeJVMExprMap.put(short.class, 'S');
		primitiveTypeJVMExprMap.put(long.class, 'L');
		JVMExprToPrimitiveTypeMap.put('V', void.class);
		JVMExprToPrimitiveTypeMap.put('I', int.class);
		JVMExprToPrimitiveTypeMap.put('Z', boolean.class);
		JVMExprToPrimitiveTypeMap.put('B', byte.class);
		JVMExprToPrimitiveTypeMap.put('C', char.class);
		JVMExprToPrimitiveTypeMap.put('D', double.class);
		JVMExprToPrimitiveTypeMap.put('F', float.class);
		JVMExprToPrimitiveTypeMap.put('S', short.class);
		JVMExprToPrimitiveTypeMap.put('L', long.class);

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
		return name2class(ClassUtil.getClassLoader(), name);
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
	private static Class<?> name2class(final ClassLoader cl, final String name)
			throws ClassNotFoundException {
		int c = 0;

		String realName = name;
		final int index = name.indexOf('[');
		if (index > 0) {
			c = (name.length() - index) / 2;
			realName = name.substring(0, index);
		}
		if (c > 0) {
			final StringBuilder sb = new StringBuilder();
			while (c-- > 0) {
				sb.append("[");
			}
			final Class<?> primitiveType = "void".equals(realName) ? void.class
					: primitiveTypeNameMap.get(realName);
			if (primitiveType != null) {
				sb.append(primitiveTypeJVMExprMap.get(primitiveType));
			} else {
				sb.append('L').append(realName).append(';'); // "java.lang.Object"
																// ==>
																// "Ljava.lang.Object;"
			}
			realName = sb.toString();
		} else {
			return "void".equals(realName) ? void.class : primitiveTypeNameMap
					.get(realName);

		}

		ClassLoader realClassLoader = cl;
		if (realClassLoader == null) {
			realClassLoader = ClassUtil.getClassLoader();
		}
		Class<?> clazz = NAME_CLASS_CACHE.get(realName);
		if (clazz == null) {
			clazz = Class.forName(realName, true, realClassLoader);
			NAME_CLASS_CACHE.put(realName, clazz);
		}
		return clazz;
	}

	private static final ConcurrentMap<String, Class<?>> NAME_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

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
		return desc2class(ClassUtil.getClassLoader(), desc);
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
		final Class<?> primitiveType = JVMExprToPrimitiveTypeMap.get(desc
				.charAt(0));
		if (primitiveType != null) {
			return primitiveType;
		}

		if ('L' == desc.charAt(0)) {
			desc = desc.substring(1, desc.length() - 1).replace('/', '.');
		} else if ('[' == desc.charAt(0)) {
			desc = desc.replace('/', '.'); // "[[Ljava/lang/Object;" ==>
			// "[[Ljava.lang.Object;"

		} else {
			throw new ClassNotFoundException("Class not found: " + desc);
		}

		if (cl == null) {
			cl = ClassUtil.getClassLoader();
		}
		Class<?> clazz = DESC_CLASS_CACHE.get(desc);
		if (clazz == null) {
			clazz = Class.forName(desc, true, cl);
			DESC_CLASS_CACHE.put(desc, clazz);
		}
		return clazz;
	}

	private static final ConcurrentMap<String, Class<?>> DESC_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

	/**
	 * get class array instance.
	 * 
	 * @param desc
	 *            desc.
	 * @return Class class array.
	 * @throws ClassNotFoundException
	 */

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
		final Class<?>[] ret = desc2classArray(ClassUtil.getClassLoader(), desc);
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
	private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
}
