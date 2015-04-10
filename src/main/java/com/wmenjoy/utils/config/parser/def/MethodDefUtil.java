package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.Method;

import com.wmenjoy.utils.config.parser.DataAccessErrorException;
import com.wmenjoy.utils.config.parser.ErrorAnnotationConfigException;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MFunction;
import com.wmenjoy.utils.lang.reflect.MethodUtil;

public abstract class MethodDefUtil {


    /**
     * 获取指定类的指定指定方法的MethodDef
     * @param clazz
     * @param mfun
     * @return
     * @throws SystemConfigErrorException
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static MethodDef getMethodDef(final Class<?> clazz, final MFunction mfun)
            throws SystemConfigErrorException, ErrorAnnotationConfigException,
            DataAccessErrorException {

        if (mfun == null) {
            throw new ErrorAnnotationConfigException("MFuction 参数不能为空");
        }

        final Method method;
        try {
            method = MethodUtil.getMethod(clazz, mfun.name(), mfun.fields().length,
                    mfun.paramClazz());
        } catch (final NoSuchMethodException e) {
            throw new ErrorAnnotationConfigException("注解设置错误，没有名为" + mfun.name() + "，参数为:"
                    + mfun.fields() + "方法");
        } catch (final SecurityException e) {
            throw new DataAccessErrorException("没有权限访问" + clazz.getName() + "的" + mfun.name()
                    + "的方法");
        }
        final FieldDef[] fieldDefs = new FieldDef[method.getParameterTypes().length];

        for (int i = 0; i < fieldDefs.length; i++) {
            fieldDefs[i] = FieldDefUtil.getFieldDef(mfun.fields()[i], clazz);
        }

        return new MethodDef(method, fieldDefs);
    }

}
