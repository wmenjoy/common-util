package com.wmenjoy.utils.config.parser.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.lang.StringUtils;


public abstract class NumberRule<T extends Number> extends BaseFieldRule<T> {
    /**
     * 最大值
     */
    protected T maxValue;
    /***
     * 最小值
     */
    protected T minValue;
    /***
     * 默认值
     */
    protected T defaultValue;
    /***
     * 模式
     */
    private Pattern pattern;

    private String regex;

    public NumberRule(boolean nullable, String max, String min, String defaultValue, String regex) {
        if (StringUtils.isBlank(max)) {
            this.maxValue = getTypeMaxValue();
        } else {
            this.maxValue = parse(max);
        }

        if (StringUtils.isBlank(min)) {
            this.minValue = getTypeMinValue();
        } else {
            this.minValue = parse(min);
        }

        if (StringUtils.isNotBlank(defaultValue)) {
            this.defaultValue = parse(defaultValue);
        } else {
            if (StringUtils.isNotBlank(min)) {
                this.defaultValue = parse(min);
            } else {
                this.defaultValue = parse("0");
            }

        }

        if (StringUtils.isNotBlank(regex)) {
            pattern = Pattern.compile(regex);
            this.regex = regex;
        }

        if (!inRange(this.defaultValue)) {
            throw new IllegalArgumentException("默认值设置的不正确");
        }
    }

    protected abstract T parse(String value);

    protected abstract T getTypeMaxValue();

    protected abstract T getTypeMinValue();

    public boolean inRange(T value) {

        @SuppressWarnings("unchecked")
        Comparable<T> maxValueC = ((Comparable<T>)maxValue);
        @SuppressWarnings("unchecked")
        Comparable<T> minValueC = (Comparable<T>)minValue;

        return maxValueC.compareTo(value) >= 0 && minValueC.compareTo(value) <= 0;
    };

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public T checkAndGetValue(String value) throws DataNotValidException {

        if (StringUtils.isBlank(value) && !nullable) {
            throw new DataNotValidException("不能为空");
        }

        if (StringUtils.isNotBlank(value) && pattern != null) {
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                throw new DataNotValidException("正则表达式不匹配：" + regex + ", value:" + value);
            }
        }

        if (StringUtils.isBlank(value)) {
            return defaultValue;
        } else {
            T realValue = parse(value.trim());

            if (!inRange(realValue)) {
                throw new DataNotValidException("该字段不在" + minValue + "-" + maxValue
                        + "的范围内, value:" + value);
            }

            return realValue;
        }
    }

}
