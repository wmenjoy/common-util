package com.wmenjoy.utils.lang.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    public static Method getMethod(final Class<?> clazz, final String methodName,
            final Class<?>... paramTypes) throws NoSuchMethodException, SecurityException {

        if ((clazz == null) || StringUtils.isBlank(methodName)) {
            throw new IllegalArgumentException("clazz 参数不能为null");
        }

        try {
            //查询指定声明的方法
            final Method method = clazz.getDeclaredMethod(methodName, paramTypes);

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
    public static List<Method> getMethod(final Class<?> clazz, final String methodName)
            throws SecurityException {
        return getMethod(clazz, methodName, false);
    }

    public static List<Method> getMethod(final Class<?> clazz, final String methodName,
            final boolean findSuperClassMethod) throws SecurityException {
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
            methodList.addAll(getMethod(clazz, methodName, findSuperClassMethod));
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
    public static Method getMethod(final Class<?> clazz, final String methodName,
            final int paramLength, final Class<?>[] paramClazzArr) throws NoSuchMethodException,
            SecurityException {

        if ((paramLength > 0) && ((paramClazzArr == null) || (paramClazzArr.length == 0))) {
            //复杂初始化
            return getMethod(clazz, methodName, paramLength);
        } else {
            return getMethod(clazz, methodName, paramClazzArr);
        }
    }

    public static Method getMethod(final Class<?> clazz, final String methodName,
            final int paramLength) throws NoSuchMethodException, SecurityException {

        if ((clazz == null) || StringUtils.isBlank(methodName) || (paramLength < 0)) {
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
            throw new IllegalArgumentException(clazz.getSimpleName() + "有两个参数数量一样的方法：" + methodName
                    + ", 请明确方法");
        }

        if (selectMethod != null) {
            return selectMethod;
        } else {
            throw new NoSuchMethodException("注解设置错误，没有名为" + methodName + "，参数长度为:" + paramLength
                    + "的方法");
        }
    }
}
