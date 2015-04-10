package com.wmenjoy.utils.lang.reflect;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    public static <T> T getInstance(final Class<T> clazz) throws InstantiationException,
            IllegalAccessException {
        return clazz == null ? null : clazz.newInstance();
    }

    public static boolean isEnum(final Class<?> clazz){
        return (clazz != null) && clazz.isEnum();
    }
}
