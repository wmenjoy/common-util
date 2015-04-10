package com.wmenjoy.utils.config.parser.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.wmenjoy.utils.config.parser.DataAccessErrorException;
import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MEnum.Type;
import com.wmenjoy.utils.lang.StringUtils;

public class EnumRule<T> extends BaseFieldRule<T> {

    final Type type;
    final Class<T> clazz;
    final boolean nullable;
    Map<String, T> enumMap;

    public static <T> EnumRule<T> getEnumRule(Type type, Class<T> clazz, boolean nullable)
            throws SystemConfigErrorException {
        return new EnumRule<T>(type, clazz, nullable);
    }

    private EnumRule(Type type, Class<T> clazz, boolean nullable) throws SystemConfigErrorException {
        this.type = type;
        this.clazz = clazz;
        this.nullable = nullable;

        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        if (!clazz.isEnum()) {
            throw new IllegalArgumentException("clazz is not enum!");
        }

        try {
            enumMap = new HashMap<String, T>();
            Method valuesMethod = clazz.getDeclaredMethod("values");
            T[] values = (T[])valuesMethod.invoke(clazz);
            Method orignalMethod = clazz.getMethod("ordinal");
            Method nameMethod = clazz.getMethod("name");
            for (T t : values) {
                if (type == Type.STRING) {
                    String name = (String)nameMethod.invoke(t);
                    enumMap.put(name, t);
                } else {
                    int value = (Integer)orignalMethod.invoke(t);
                    enumMap.put(value + "", t);
                }
            }
        } catch (NoSuchMethodException e) {
            //不会出现
            throw new IllegalArgumentException("clazz is not enum!");
        } catch (SecurityException e) {
            throw new SystemConfigErrorException("can not access the valueOf Method!");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("clazz is not enum!");
        } catch (IllegalAccessException e) {
            throw new SystemConfigErrorException("can not access the valueOf Method!");
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("clazz is not enum!");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T checkAndGetValue(String value) throws DataNotValidException {

        if (StringUtils.isBlank(value) && nullable) {
            return null;
        }

        if (StringUtils.isBlank(value) && !nullable) {
            throw new DataNotValidException("value 参数不能为空");
        }
        return enumMap.get(value);
    }

    public static enum ke {
        KEY, C;
    }

    public static void main(String[] args) throws DataAccessErrorException, DataNotValidException,
            SystemConfigErrorException {

        EnumRule<ke> keRule = new EnumRule<EnumRule.ke>(Type.INT, ke.class, true);
        System.out.println(keRule.checkAndGetValue("1"));
    }
}
