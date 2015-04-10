package com.wmenjoy.utils.config.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wmenjoy.utils.config.parser.annotation.MAlias;
import com.wmenjoy.utils.config.parser.annotation.MData;
import com.wmenjoy.utils.config.parser.annotation.MFunction;
import com.wmenjoy.utils.config.parser.annotation.MIgnore;
import com.wmenjoy.utils.config.parser.def.FieldDef;
import com.wmenjoy.utils.config.parser.def.FieldDefUtil;
import com.wmenjoy.utils.config.parser.def.MethodDef;
import com.wmenjoy.utils.config.parser.def.MethodDefUtil;
import com.wmenjoy.utils.lang.StringUtils;
import com.wmenjoy.utils.lang.reflect.FieldUtil;

/**
 * 以map的形式处理
 * 
 * @author jinliang.liu
 * 
 * @param <T>
 */
public class MapFieldSet<T> implements IConfigParser<T> {

    private final static String DEFAULT_FIELD_SEPERATE_CHAR = "\\|";
    /**
     * 文件字段对应的类的字段
     */

    private final Map<String, FieldDef> fieldDefMap;
    /**
     * 待处理的数据对象
     */
    private final Class<T> clazz;

    private final boolean ignoreNoFieldKey;

    private final String[] fieldNameArray;

    private final String fieldSep;
    /**
     * 对象可否默认初始化
     */
    private boolean canBeDefaultInital = true;

    private int nonBlankFieldNum;

    private List<MethodDef> methodDefList;

    private MapFieldSet(Class<T> clazz, boolean ignoreNoFieldKey)
            throws SystemConfigErrorException, DataAccessErrorException,
            ErrorAnnotationConfigException {
        this.clazz = clazz;
        this.nonBlankFieldNum = 0;
        this.ignoreNoFieldKey = ignoreNoFieldKey;
        this.fieldNameArray = null;
        this.fieldSep = null;
        this.fieldDefMap = new HashMap<String, FieldDef>();
        init();

    }

    private MapFieldSet(Class<T> clazz, boolean ignoreNoFieldKey, String[] fieldNameArray,
            String fieldSep) throws SystemConfigErrorException, DataAccessErrorException,
            ErrorAnnotationConfigException {
        this.clazz = clazz;
        this.nonBlankFieldNum = 0;
        this.ignoreNoFieldKey = ignoreNoFieldKey;
        this.fieldNameArray = fieldNameArray;
        this.fieldSep = fieldSep;
        this.fieldDefMap = new HashMap<String, FieldDef>();
        init();
    }

    private void init() throws SystemConfigErrorException, DataAccessErrorException,
            ErrorAnnotationConfigException {
        List<Field> fields = FieldUtil.getFields(clazz);

        if (fields == null) {
            throw new SystemConfigErrorException("没有获取任何的field");
        }

        this.methodDefList = new ArrayList<MethodDef>();
        for (Field field : fields) {

            if (field.isAnnotationPresent(MIgnore.class)) {
                continue;
            }

            if (field.isAnnotationPresent(MFunction.class)) {
                MFunction mfun = field.getAnnotation(MFunction.class);
                methodDefList.add(MethodDefUtil.getMethodDef(clazz, mfun));
                continue;
            }
            
            
            final String fieldName;
            if (field.isAnnotationPresent(MAlias.class)) {
                MAlias alias = field.getAnnotation(MAlias.class);
                fieldName = alias.value();
            } else {
                fieldName = field.getName();
            }

            FieldDef fieldDef = FieldDefUtil.getFieldDef(fieldName,
                    this.fieldDefMap.get(fieldName), field);

            if (!fieldDef.nullable()) {
                this.nonBlankFieldNum++;
                if (this.canBeDefaultInital) {
                    this.canBeDefaultInital = false;
                }
            }

            this.fieldDefMap.put(fieldName, fieldDef);
        }

    }

    /**
     * 编译MapFieldSet 可以指定是否强制支持cvs格式的数据
     * 
     * @param clazz: 需要转换的类对象
     * @param ingoreNoFieldKey：是否忽略没有在类中找到对应Field的column
     * @param supportedParseCsv： 是否必须支持csv格式的文件
     * @return
     * @throws DataNotValidException: 数据不合法
     * @throws SystemConfigErrorException： 系统配置错误
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static <T> MapFieldSet<T> compile(Class<T> clazz, boolean ingoreNoFieldKey,
            boolean supportedParseCsv) throws SystemConfigErrorException,
            ErrorAnnotationConfigException, DataAccessErrorException {
        if (supportedParseCsv && !clazz.isAnnotationPresent(MData.class)) {
            throw new ErrorAnnotationConfigException("如果支持解析csv格式的形式，请配置MData注解，或者是有指定cvs顺序参数的方法");
        }

        boolean ignoreNoFieldKey = false;

        String[] fieldNameArray = null;

        String fieldSep = DEFAULT_FIELD_SEPERATE_CHAR;
        //如果有
        if (clazz.isAnnotationPresent(MData.class)) {
            MData mdata = clazz.getAnnotation(MData.class);
            ignoreNoFieldKey = mdata.ignoreNoFieldKey();

            if (mdata.fields().length > 0) {
                fieldNameArray = mdata.fields();
            } else if (StringUtils.isNotBlank(mdata.fieldStr())) {
                fieldNameArray = mdata.fieldStr().split(mdata.fieldSep());
            }
            fieldSep = mdata.fieldSep();
            return new MapFieldSet<T>(clazz, ignoreNoFieldKey, fieldNameArray, fieldSep);
        }

        if (supportedParseCsv) {
            throw new ErrorAnnotationConfigException(clazz.getName() + "必须有MData的注解");
        } else {
            return new MapFieldSet<T>(clazz, ingoreNoFieldKey);
        }

    }

    /**
     * 不强制支持解析CSV格式的数据， 如果Mdata有配置，可以支持
     * 
     * @param clazz
     * @return
     * @throws DataNotValidException
     * @throws SystemConfigErrorException
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static <T> MapFieldSet<T> compile(Class<T> clazz) throws SystemConfigErrorException,
            ErrorAnnotationConfigException, DataAccessErrorException {
        return compile(clazz, true, false);
    }

    /**
     * 不强制支持解析CSV格式的数据， 如果Mdata有配置，可以支持
     * 
     * @param clazz
     * @param ingoreNoFieldKey：是否忽略没有类对应Field的字段
     * @return
     * @throws DataNotValidException
     * @throws SystemConfigErrorException
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static <T> MapFieldSet<T> compile(Class<T> clazz, boolean ingoreNoFieldKey)
            throws SystemConfigErrorException, ErrorAnnotationConfigException,
            DataAccessErrorException {
        return compile(clazz, ingoreNoFieldKey, false);
    }

    /**
     * 使用可方法，既可以支持CSV，又可以支持key=value形式的数据
     * 
     * @param clazz
     * @param ingoreNoFieldKey
     * @param fieldNameArrayStr
     * @param fieldSep
     * @return
     * @throws DataNotValidException
     * @throws SystemConfigErrorException
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static <T> MapFieldSet<T> compile(Class<T> clazz, boolean ingoreNoFieldKey,
            String fieldNameArrayStr, String fieldSep) throws SystemConfigErrorException,
            DataAccessErrorException, ErrorAnnotationConfigException {

        if (clazz == null) {
            throw new NullPointerException("参数T为null");
        }

        if (StringUtils.isBlank(fieldNameArrayStr)) {
            throw new SystemConfigErrorException("fieldDesc必须不能为空");
        }

        String fieldSeperator = StringUtils.isBlank(fieldSep) ? DEFAULT_FIELD_SEPERATE_CHAR
                : fieldSep;
        String[] fieldNameArray = fieldNameArrayStr.split(fieldSeperator);

        if (fieldNameArray == null || fieldNameArray.length <= 0) {
            throw new SystemConfigErrorException("fieldDesc配置有问题");
        }

        return new MapFieldSet<T>(clazz, ingoreNoFieldKey, fieldNameArray, fieldSep);
    }

    /**
     * 使用可方法，既可以支持CSV，又可以支持key=value形式的数据
     * 
     * @param clazz
     * @param ingoreNoFieldKey
     * @param fieldNameArrayStr
     * @return
     * @throws SystemConfigErrorException
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static <T> MapFieldSet<T> compile(Class<T> clazz, boolean ingoreNoFieldKey,
            String fieldNameArrayStr) throws SystemConfigErrorException, DataAccessErrorException,
            ErrorAnnotationConfigException {

        return compile(clazz, ingoreNoFieldKey, fieldNameArrayStr, DEFAULT_FIELD_SEPERATE_CHAR);
    }

    /**
     * 使用可方法，既可以支持CSV，又可以支持key=value形式的数据
     * 
     * @param clazz
     * @param fieldNameArrayStr
     * @return
     * @throws SystemConfigErrorException
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static <T> MapFieldSet<T> compile(Class<T> clazz, String fieldNameArrayStr)
            throws SystemConfigErrorException, DataAccessErrorException,
            ErrorAnnotationConfigException {

        return compile(clazz, true, fieldNameArrayStr, DEFAULT_FIELD_SEPERATE_CHAR);
    }

    /**
     * 使用可方法，既可以支持CSV，又可以支持key=value形式的数据
     * 
     * @param clazz
     * @param fieldNameArrayStr
     * @param fieldSep
     * @return
     * @throws SystemConfigErrorException
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     */
    public static <T> MapFieldSet<T> compile(Class<T> clazz, String fieldNameArrayStr,
            String fieldSep) throws SystemConfigErrorException, DataAccessErrorException,
            ErrorAnnotationConfigException {

        return compile(clazz, true, fieldNameArrayStr, fieldSep);
    }

    public T parse(Map<String, String> dataStore) throws DataAccessErrorException,
            DataNotValidException {

        if (dataStore == null || dataStore.size() == 0) {
            if (this.canBeDefaultInital) {
                return FieldDefUtil.getInstance(clazz);
            } else {
                throw new DataAccessErrorException("dataStore 不能为空");
            }

        }

        T target = FieldDefUtil.getInstance(clazz);

        return parse(target, dataStore);
    }

    @Override
    public T parse(String line) throws DataNotValidException, DataAccessErrorException {
        if (this.fieldNameArray == null) {
            throw new UnsupportedOperationException("不支持直接解析line到对象， 必须指定line如何解析");
        }

        final T data = FieldDefUtil.getInstance(clazz);

        return parse(data, line);
    }

    private void process(String fieldName, String value, T data) throws DataNotValidException,
            DataAccessErrorException {
        final FieldDef fieldDef = this.fieldDefMap.get(fieldName);
        if (fieldDef == null) {
            if (this.ignoreNoFieldKey) {
                return;
            } else {
                throw new DataNotValidException(fieldName + "不存在对应的Field");
            }
        } else {
            handle(data, value, fieldDef);
        }

    }

    private void handle(T data, String value, FieldDef fd) throws DataNotValidException,
            DataAccessErrorException {
        FieldDefUtil.handle(fd, value, data);
        if (fd.getAlias() != null) {
            FieldDefUtil.handle(fd.getAlias(), value, data);
        }

    }

    @Override
    public T parse(String line, boolean nullable) throws DataNotValidException,
            DataAccessErrorException {
        if (nullable && StringUtils.isBlank(line)) {
            return getDefaultObject();
        } else {
            return parse(line);
        }
    }

    @Override
    public T getDefaultObject() throws DataNotValidException, DataAccessErrorException {
        return this.parse((Map<String, String>)null);
    }

    @Override
    public T parse(T target, String line) throws DataNotValidException, DataAccessErrorException {

        if (target == null) {
            return null;
        }

        if (this.fieldNameArray == null) {
            throw new UnsupportedOperationException("不支持直接解析line到对象， 必须指定line如何解析");
        }

        if (StringUtils.isBlank(line) && canBeDefaultInital) {
            return target;
        }

        final String[] fieldStrArray = line.split(this.fieldSep);

        //非空字段的数量
        if (nonBlankFieldNum > fieldStrArray.length) {
            throw new DataNotValidException("数据格式有问题");
        }

        Map<String, String> dataStore = new HashMap<String, String>();
        for (int i = 0; i < fieldStrArray.length; i++) {
            dataStore.put(fieldNameArray[i], fieldStrArray[i]);
        }

        return parse(target, dataStore);
    }

    @Override
    public T parse(T target, String line, boolean nullable) throws DataNotValidException,
            DataAccessErrorException {
        if (nullable && StringUtils.isBlank(line)) {
            return target;
        } else {
            return parse(target, line);
        }
    }

    @Override
    public T parse(T target, Map<String, String> dataStore) throws DataNotValidException,
            DataAccessErrorException {

        if (target == null) {
            return null;
        }

        if (dataStore == null || dataStore.size() == 0) {
            if (this.canBeDefaultInital) {
                return target;
            } else {
                throw new DataAccessErrorException("dataStore 不能为空");
            }

        }

        if (dataStore.size() < nonBlankFieldNum) {
            throw new DataAccessErrorException("dataStore的数据不满足对象非空字段的数量");
        }

        for (Entry<String, String> entry : dataStore.entrySet()) {
            process(entry.getKey(), entry.getValue(), target);
        }

        if (this.methodDefList != null) {
            for (MethodDef methodDef : this.methodDefList) {

                if (methodDef.getFieldDefs() != null && methodDef.getFieldDefs().length > 0) {
                    String[] params = new String[methodDef.getFieldDefs().length];
                    for (int i = 0; i < params.length; i++) {
                        params[i] = dataStore.get(methodDef.getFieldDefs()[i].getName());
                    }
                    methodDef.handle(target, params);
                } else {
                    methodDef.handle(target);
                }
            }
        }

        return target;
    }

}
