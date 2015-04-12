package com.wmenjoy.utils.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.wmenjoy.utils.config.parser.DataAccessErrorException;
import com.wmenjoy.utils.config.parser.ErrorAnnotationConfigException;
import com.wmenjoy.utils.lang.StringUtils;

public abstract class MethodUtil {
	/**
	 * 找指定名称，指定参数的方法
	 * 
	 * @param clazz
	 * @param methodName
	 * @param paramTypes
	 * @return
	 * @throws NoSuchMethodException
	 * @throws ErrorAnnotationConfigException
	 * @throws DataAccessErrorException
	 */
	public static Method getMethod(final Class<?> clazz,
			final String methodName, final Class<?>... paramTypes)
			throws NoSuchMethodException, SecurityException {

		if ((clazz == null) || StringUtils.isBlank(methodName)) {
			throw new IllegalArgumentException("clazz 参数不能为null");
		}

		try {
			// 查询指定声明的方法
			final Method method = clazz.getDeclaredMethod(methodName,
					paramTypes);

			return method;
		} catch (final NoSuchMethodException e) {
			if (clazz.getSuperclass() != Object.class) {
				return getMethod(clazz.getSuperclass(), methodName, paramTypes);
			} else {
				throw e;
			}
		}

	}

	/**
	 * 获取当前类的指定名称的所有方法，不包括父类
	 * 
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static List<Method> getMethod(final Class<?> clazz,
			final String methodName) throws SecurityException {
		return getMethod(clazz, methodName, false);
	}

	public static List<Method> getMethod(final Class<?> clazz,
			final String methodName, final boolean findSuperClassMethod)
			throws SecurityException {
		if ((clazz == null) || StringUtils.isBlank(methodName)) {
			throw new IllegalArgumentException("clazz 或者methodName 参数不能为null");
		}
		final Method[] methods = clazz.getDeclaredMethods();

		final List<Method> methodList = new ArrayList<Method>();
		for (final Method method : methods) {

			if (StringUtils.equals(method.getName(), methodName)) {
				methodList.add(method);
			}

		}

		if (findSuperClassMethod && (clazz.getSuperclass() != Object.class)) {
			methodList
					.addAll(getMethod(clazz, methodName, findSuperClassMethod));
		}

		return methodList;
	}

	/**
	 * 
	 * 查找，指定param，或者指定param长度的方法
	 * 
	 * @param clazz
	 * @param methodName
	 * @param fieldLength
	 * @param paramClazzArr
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(final Class<?> clazz,
			final String methodName, final int paramLength,
			final Class<?>[] paramClazzArr) throws NoSuchMethodException,
			SecurityException {

		if ((paramLength > 0)
				&& ((paramClazzArr == null) || (paramClazzArr.length == 0))) {
			// 复杂初始化
			return getMethod(clazz, methodName, paramLength);
		} else {
			return getMethod(clazz, methodName, paramClazzArr);
		}
	}

	public static Method getMethod(final Class<?> clazz,
			final String methodName, final int paramLength)
			throws NoSuchMethodException, SecurityException {

		if ((clazz == null) || StringUtils.isBlank(methodName)
				|| (paramLength < 0)) {
			throw new IllegalArgumentException("参数设置错误");
		}

		final List<Method> methodList = getMethod(clazz, methodName);
		boolean hasMoreThanOneSameParamLengthMethod = false;
		Method selectMethod = null;
		for (final Method method : methodList) {
			if (method.getParameterTypes().length == paramLength) {
				if (selectMethod != null) {
					hasMoreThanOneSameParamLengthMethod = true;
					break;
				} else {
					selectMethod = method;
				}
			} else {
				continue;
			}
		}
		if (hasMoreThanOneSameParamLengthMethod) {
			throw new IllegalArgumentException(clazz.getSimpleName()
					+ "有两个参数数量一样的方法：" + methodName + ", 请明确方法");
		}

		if (selectMethod != null) {
			return selectMethod;
		} else {
			throw new NoSuchMethodException("注解设置错误，没有名为" + methodName
					+ "，参数长度为:" + paramLength + "的方法");
		}
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
				types[i] = ClassUtil.name2class(parameterTypes[i]);
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

	private static final ConcurrentMap<String, Method> Signature_METHODS_CACHE = new ConcurrentHashMap<String, Method>();

}
