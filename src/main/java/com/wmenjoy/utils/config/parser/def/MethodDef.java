package com.wmenjoy.utils.config.parser.def;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.wmenjoy.utils.config.parser.DataAccessErrorException;
import com.wmenjoy.utils.config.parser.DataNotValidException;

public class MethodDef {
    private final Method method;
    private final FieldDef[] fieldDefs;

    protected MethodDef(final Method method, final FieldDef[] fieldDefs) {
        super();
        this.method = method;
        this.method.setAccessible(true);
        this.fieldDefs = fieldDefs;
    }

    public void handle(final Object obj, final String... params) throws DataNotValidException,
            DataAccessErrorException {

        final Object[] args = new Object[params.length];

        for (int i = 0; (i < this.fieldDefs.length) && (i < params.length); i++) {
            args[i] = this.fieldDefs[i].getAndCheckValue(params[i]);
        }

        for (int i = params.length; i < this.fieldDefs.length; i++) {
            args[i] = this.fieldDefs[i].getAndCheckValue(null);
        }

        try {
            this.method.invoke(obj, args);
        } catch (final IllegalArgumentException e) {
            throw new DataNotValidException(this.method.getDeclaringClass().getName() + "调用方法"
                    + this.method.getName() + "失败", e);
        } catch (final IllegalAccessException e) {
            throw new DataAccessErrorException("无法访问" + this.method.getDeclaringClass().getName()
                    + "的方法" + this.method.getName(), e);
        } catch (final InvocationTargetException e) {
            throw new DataNotValidException(this.method.getDeclaringClass().getName() + "调用方法"
                    + this.method.getName() + "失败", e);
        }
    }

    public FieldDef[] getFieldDefs() {
        return this.fieldDefs;
    }

}
