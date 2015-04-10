package com.wmenjoy.utils.config.parser;

import java.util.Map;

import com.wmenjoy.utils.config.parser.annotation.MData;
import com.wmenjoy.utils.config.parser.def.FieldDef;
import com.wmenjoy.utils.config.parser.def.FieldDefUtil;
import com.wmenjoy.utils.lang.StringUtils;

/**
 * @author jinliang.liu
 * 
 * @description <p>
 *              处理"A|B|C|D" 到 Object对象的转变，目前支持一层平铺对象
 *              </p>
 * 
 *              <p>
 *              可以对数据类型做基本校验
 *              </p>
 *              <p>
 *              1、对于所有字段可以做非空校验 nullable
 *              </p>
 *              <p>
 *              2、对于数字（byte,int, short,long, float, number)， 使用
 *              <code>MNumber</code> 注解，可以标注 最小值min，最大值max，数字的正则表达式格式
 *              </p>
 *              <p>
 *              3、对于字符串 可以使用<code>MString</code>注解， 标注正则表达式，是否自动trim
 *              </p>
 *              <p>
 *              4、对于<code>Collection<code> 和数组，可以使用<code>MArray</code>
 *              注解，自动将字符串（比如逗号分割的）转化为Collection或者Array
 *              </p>
 *              <p>
 *              5、可以处理日期和boolean类型，对应的注解为<code>MDate</code>,
 *              <code>MBoolean</code>
 *              </p>
 *              <p>
 *              6、可以使用<code>MAlias</code>处理别名，就是一个配置文件的字段，对应多个不同类型的Field
 *              </p>
 *              <p>
 *              7、可以通过<code>MData</code>或者参数指定，配置文件的顺序
 *              </p>
 * @param <T> 具体对象的类型
 *        <p>
 *        使用例子
 *        </p>
 *        <p>
 *        <blockquote>
 * 
 *        <pre>
 * &#064;Mata(fieldDesc = &quot;phone|num&quot;)
 * public class TestData {
 *     &#064;MString(regex = &quot;\\d{11}&quot;)
 *     String phone;
 *     &#064;MNumber(min = &quot;0&quot;)
 *     int num;
 * 
 *     //解析代码
 *     public void main(String[] args) {
 *         FieldSet&lt;TestData&gt; parser = FieldSet.compile(TestData.class);
 *         String line = &quot;18201270703|3&quot;;
 *         TestData data = parser.parse(line);
 *     }
 * }
 * 
 * </pre>
 * 
 *        </blockquote>
 *        </p>
 * 
 * 
 */
public class FieldSet<T> implements IConfigParser<T> {

    private final static String DEFAULT_FIELD_SEPERATE_CHAR = "\\|";
    /**
     * 文件字段对应的类的字段
     */
    private FieldDef[] fieldDefSet;
    /**
     * 待处理的数据对象
     */
    private Class<T> clazz;

    /**
     * 对象可否默认初始化
     */
    private boolean canBeDefaultInital = true;

    private final String[] fieldNameArray;

    private int nonBlankFieldNum;

    private String fieldSeperator;

    private FieldSet(Class<T> clazz, String[] fieldNameArray, String fieldSeperator)
            throws SystemConfigErrorException, ErrorAnnotationConfigException,
            DataAccessErrorException {
        this.clazz = clazz;
        this.fieldNameArray = fieldNameArray;
        canBeDefaultInital = true;
        this.fieldSeperator = fieldSeperator;
        initFieldSet(clazz);
    }

    private void initFieldSet(Class<T> clazz) throws SystemConfigErrorException,
            ErrorAnnotationConfigException, DataAccessErrorException {
        fieldDefSet = new FieldDef[fieldNameArray.length];
        int i = 0;
        for (String fieldName : fieldNameArray) {

            final FieldDef fd = FieldDefUtil.getFieldDef(fieldName, clazz);

            if (!fd.nullable()) {
                nonBlankFieldNum++;

                if (canBeDefaultInital) {
                    canBeDefaultInital = false;
                }
            }

            fieldDefSet[i++] = fd;

        }
    }

    public boolean isCanBeDefaultInital() {
        return canBeDefaultInital;
    }

    /**
     * 编译注解类
     * 
     * @param clazz
     * @return
     * @throws ErrorAnnotationConfigException
     * @throws DataAccessErrorException
     * @throws SystemConfigErrorException
     */
    public static <T> FieldSet<T> compile(final Class<T> clazz)
            throws ErrorAnnotationConfigException, SystemConfigErrorException,
            DataAccessErrorException {
        if (clazz == null) {
            throw new NullPointerException("参数T为null");
        }

        if (!clazz.isAnnotationPresent(MData.class)) {
            throw new ErrorAnnotationConfigException(clazz.getName() + "必须有MData的注解");
        }

        MData mdata = clazz.getAnnotation(MData.class);
        if (mdata == null) {
            throw new ErrorAnnotationConfigException(clazz.getName() + "必须有MData的注解");
        }

        String fieldDesc = mdata.fieldStr();

        final String[] fieldNameArray;
        if (StringUtils.isBlank(fieldDesc)) {
            fieldNameArray = mdata.fields();
        } else {
            fieldNameArray = fieldDesc.split(mdata.fieldSep());
        }
        if (fieldNameArray == null || fieldNameArray.length <= 0) {
            throw new ErrorAnnotationConfigException("MData注解错误，fieldStr和fieldDesc至少有一个不为空");
        }

        return new FieldSet<T>(clazz, fieldNameArray, mdata.fieldSep());
    }

    /***
     * 手动传字符串
     * 
     * @param clazz
     * @param fieldDesc
     * @param seperator
     * @return
     * @throws SystemConfigErrorException
     * @throws DataAccessErrorException
     * @throws ErrorAnnotationConfigException
     */
    public static <T> FieldSet<T> compile(final Class<T> clazz, final String fieldDesc,
            final String seperator) throws SystemConfigErrorException,
            ErrorAnnotationConfigException, DataAccessErrorException {
        if (clazz == null) {
            throw new NullPointerException("参数T为null");
        }

        if (StringUtils.isBlank(fieldDesc)) {
            throw new SystemConfigErrorException("fieldDesc必须不能为空");
        }

        String fieldSeperator = StringUtils.isBlank(seperator) ? DEFAULT_FIELD_SEPERATE_CHAR
                : seperator;
        String[] fieldNameArray = fieldDesc.split(fieldSeperator);

        if (fieldNameArray == null || fieldNameArray.length <= 0) {
            throw new SystemConfigErrorException("fieldDesc配置有问题");
        }

        return new FieldSet<T>(clazz, fieldNameArray, fieldSeperator);
    }

    public static <T> FieldSet<T> compile(final Class<T> clazz, String fieldDesc)
            throws SystemConfigErrorException, ErrorAnnotationConfigException,
            DataAccessErrorException {
        return compile(clazz, fieldDesc, DEFAULT_FIELD_SEPERATE_CHAR);
    }

    @Override
    public T parse(final String line) throws DataNotValidException, DataAccessErrorException {
        if (StringUtils.isBlank(line)) {
            throw new DataNotValidException("解析的行不能为空");
        }

        final T data = FieldDefUtil.getInstance(clazz);

        final String[] fieldStrArray = line.split(this.fieldSeperator);

        //非空字段的数量
        if (nonBlankFieldNum > fieldStrArray.length) {
            throw new DataNotValidException("数据格式有问题");
        }

        //处理字符串
        for (int i = 0; i < fieldStrArray.length && i < fieldDefSet.length; i++) {
            FieldDef fd = fieldDefSet[i];
            handle(data, fieldStrArray[i], fd);
        }
        //处理最后一个为空串的情况
        for (int i = fieldStrArray.length; i < this.fieldDefSet.length; i++) {
            FieldDef fd = fieldDefSet[i];
            handle(data, null, fd);
        }
        return data;
    }

    private void handle(final T data, final String valueStr, FieldDef fd)
            throws DataNotValidException, DataAccessErrorException {
        FieldDefUtil.handle(fd, valueStr, data);
        if (fd.getAlias() != null) {
            FieldDefUtil.handle(fd.getAlias(), valueStr, data);
        }
    }

    @Override
    public T getDefaultObject() throws DataNotValidException, DataAccessErrorException {
        if (!this.canBeDefaultInital) {
            throw new DataNotValidException("该对象不可以进行默认初始化");
        }
        final T data = FieldDefUtil.getInstance(clazz);

        for (int i = 0; i < fieldDefSet.length; i++) {
            FieldDef fd = fieldDefSet[i];
            FieldDefUtil.handle(fd, null, data);

            if (fd.getAlias() != null) {
                FieldDefUtil.handle(fd.getAlias(), null, data);
            }
        }
        return data;

    }

    @Override
    public T parse(String line, boolean nullable) throws DataNotValidException,
            DataAccessErrorException {
        if (this.canBeDefaultInital && nullable) {
            return getDefaultObject();
        } else {
            return parse(line);
        }
    }

    @Override
    public T parse(Map<String, String> valueMap) throws DataNotValidException,
            DataAccessErrorException {
        if (valueMap == null || valueMap.size() == 0) {
            throw new DataNotValidException("解析的行不能为空");
        }

        final T data = FieldDefUtil.getInstance(clazz);

        if (nonBlankFieldNum > valueMap.size()) {
            throw new DataNotValidException("数据格式有问题");
        }

        //处理字符串
        for (int i = 0; i < fieldNameArray.length && i < fieldDefSet.length; i++) {
            FieldDef fd = fieldDefSet[i];
            FieldDefUtil.handle(fd, valueMap.get(fieldNameArray[i]), data);

            if (fd.getAlias() != null) {
                FieldDefUtil.handle(fd.getAlias(), valueMap.get(fieldNameArray[i]), data);
            }
        }

        return data;
    }


    @Override
    public T parse(T target, String line) throws DataNotValidException, DataAccessErrorException {
        if (StringUtils.isBlank(line)) {
            throw new DataNotValidException("解析的行不能为空");
        }

        if (target == null) {
            return null;
        }

        final String[] fieldStrArray = line.split(this.fieldSeperator);

        //非空字段的数量
        if (nonBlankFieldNum > fieldStrArray.length) {
            throw new DataNotValidException("数据格式有问题");
        }

        //处理字符串
        for (int i = 0; i < fieldStrArray.length && i < fieldDefSet.length; i++) {
            FieldDef fd = fieldDefSet[i];
            handle(target, fieldStrArray[i], fd);
        }
        //处理最后一个为空串的情况
        for (int i = fieldStrArray.length; i < this.fieldDefSet.length; i++) {
            FieldDef fd = fieldDefSet[i];
            handle(target, null, fd);
        }
        return target;
    }

    @Override
    public T parse(T target, String line, boolean nullable) throws DataNotValidException,
            DataAccessErrorException {
        if (this.canBeDefaultInital && nullable) {
            return target;
        } else {
            return parse(target, line);
        }
    }

    @Override
    public T parse(T target, Map<String, String> valueMap) throws DataNotValidException,
            DataAccessErrorException {
        if (valueMap == null || valueMap.size() == 0) {
            throw new DataNotValidException("解析的行不能为空");
        }

        if (nonBlankFieldNum > valueMap.size()) {
            throw new DataNotValidException("数据格式有问题");
        }

        //处理字符串
        for (int i = 0; i < fieldNameArray.length && i < fieldDefSet.length; i++) {
            FieldDef fd = fieldDefSet[i];
            FieldDefUtil.handle(fd, valueMap.get(fieldNameArray[i]), target);

            if (fd.getAlias() != null) {
                FieldDefUtil.handle(fd.getAlias(), valueMap.get(fieldNameArray[i]), target);
            }
        }

        return target;
    }

}
