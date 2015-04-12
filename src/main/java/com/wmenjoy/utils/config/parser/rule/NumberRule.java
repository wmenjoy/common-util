package com.wmenjoy.utils.config.parser.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.lang.StringUtils;

/**
 * 1、所有数字都有String类型的构造函数 2、最小值和最大值如何去构造这个是个问题，可以用特殊值来代替
 * 
 * @author jinliang.liu
 * 
 * @param <T>
 */
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

	public NumberRule(final boolean nullable, final String max,
			final String min, final String defaultValue, final String regex) {

		if (StringUtils.isBlank(max)) {
			this.maxValue = this.getTypeMaxValue();
		} else {
			this.maxValue = this.parse(max);
		}

		if (StringUtils.isBlank(min)) {
			this.minValue = this.getTypeMinValue();
		} else {
			this.minValue = this.parse(min);
		}

		if (StringUtils.isNotBlank(defaultValue)) {
			this.defaultValue = this.parse(defaultValue);
		} else {
			if (StringUtils.isNotBlank(min)) {
				this.defaultValue = this.parse(min);
			} else {
				this.defaultValue = this.parse("0");
			}

		}

		if (StringUtils.isNotBlank(regex)) {
			this.pattern = Pattern.compile(regex);
			this.regex = regex;
		}

		if (!this.inRange(this.defaultValue)) {
			throw new IllegalArgumentException("默认值设置的不正确");
		}
	}

	protected abstract T parse(String value);

	protected abstract T getTypeMaxValue();

	protected abstract T getTypeMinValue();

	public boolean inRange(final T value) {

		@SuppressWarnings("unchecked")
		final Comparable<T> maxValueC = ((Comparable<T>) this.maxValue);
		@SuppressWarnings("unchecked")
		final Comparable<T> minValueC = (Comparable<T>) this.minValue;

		return maxValueC.compareTo(value) >= 0
				&& minValueC.compareTo(value) <= 0;
	};

	public T getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public T checkAndGetValue(final String value) throws DataNotValidException {

		if (StringUtils.isBlank(value) && !this.nullable) {
			throw new DataNotValidException("不能为空");
		}

		if (StringUtils.isNotBlank(value) && this.pattern != null) {
			final Matcher matcher = this.pattern.matcher(value);
			if (!matcher.matches()) {
				throw new DataNotValidException("正则表达式不匹配：" + this.regex
						+ ", value:" + value);
			}
		}

		if (StringUtils.isBlank(value)) {
			return this.defaultValue;
		} else {
			final T realValue = this.parse(value.trim());

			if (!this.inRange(realValue)) {
				throw new DataNotValidException("该字段不在" + this.minValue + "-"
						+ this.maxValue + "的范围内, value:" + value);
			}

			return realValue;
		}
	}

}
