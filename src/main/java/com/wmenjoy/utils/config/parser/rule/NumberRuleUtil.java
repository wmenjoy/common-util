package com.wmenjoy.utils.config.parser.rule;

import com.wmenjoy.utils.config.parser.annotation.MNumber;

public abstract class NumberRuleUtil {

	public static NumberRule<? extends Number> getNumberRule(
			final MNumber rule, final Class<?> realClazz,
			final String classDefaultValue) {

		String max = null;
		String min = null;
		final String defaultValue = classDefaultValue;
		String regex = null;
		boolean nullable = true;
		if (rule != null) {
			max = rule.max();
			min = rule.min();
			regex = rule.regex();
			nullable = rule.nullable();
		}

		if (int.class == realClazz || Integer.class == realClazz) {
			return new IntRule(nullable, max, min, defaultValue, regex);
		} else if (byte.class == realClazz || Byte.class == realClazz) {
			return new ByteRule(nullable, max, min, defaultValue, regex);
		} else if (short.class == realClazz || Short.class == realClazz) {
			return new ShortRule(nullable, max, min, defaultValue, regex);
		} else if (long.class == realClazz || Long.class == realClazz) {
			return new LongRule(nullable, max, min, defaultValue, regex);
		} else if (float.class == realClazz || Float.class == realClazz) {
			return new FloatRule(nullable, max, min, defaultValue, regex);
		} else if (double.class == realClazz || Double.class == realClazz) {
			return new DoubleRule(nullable, max, min, defaultValue, regex);
		} else {
			throw new IllegalArgumentException("不支持的数字类型");
		}
	}

	static class IntRule extends NumberRule<Integer> {

		public IntRule(final boolean nullable, final String max,
				final String min, final String defaultValue, final String regex) {
			super(nullable, max, min, defaultValue, regex);
		}

		@Override
		protected Integer parse(final String value) {
			return Integer.parseInt(value);
		}

		@Override
		protected Integer getTypeMaxValue() {
			return Integer.MAX_VALUE;
		}

		@Override
		protected Integer getTypeMinValue() {
			return Integer.MIN_VALUE;
		}

		@Override
		public boolean inRange(final Integer value) {

			return value >= this.minValue && value <= this.maxValue;
		}

	}

	static class ByteRule extends NumberRule<Byte> {

		public ByteRule(final boolean nullable, final String max,
				final String min, final String defaultValue, final String regex) {
			super(nullable, max, min, defaultValue, regex);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Byte parse(final String value) {
			return Byte.parseByte(value);
		}

		@Override
		protected Byte getTypeMaxValue() {
			return Byte.MAX_VALUE;
		}

		@Override
		protected Byte getTypeMinValue() {
			return Byte.MIN_VALUE;
		}

		@Override
		public boolean inRange(final Byte value) {

			return value >= this.minValue && value <= this.maxValue;
		}

	}

	static class ShortRule extends NumberRule<Short> {

		public ShortRule(final boolean nullable, final String max,
				final String min, final String defaultValue, final String regex) {
			super(nullable, max, min, defaultValue, regex);
		}

		@Override
		protected Short parse(final String value) {
			return Short.parseShort(value);
		}

		@Override
		protected Short getTypeMaxValue() {
			return Short.MAX_VALUE;
		}

		@Override
		protected Short getTypeMinValue() {
			return Short.MIN_VALUE;
		}

		@Override
		public boolean inRange(final Short value) {

			return value >= this.minValue && value <= this.maxValue;
		}

	}

	static class LongRule extends NumberRule<Long> {

		public LongRule(final boolean nullable, final String max,
				final String min, final String defaultValue, final String regex) {
			super(nullable, max, min, defaultValue, regex);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Long parse(final String value) {
			return Long.parseLong(value);
		}

		@Override
		protected Long getTypeMaxValue() {
			return Long.MAX_VALUE;
		}

		@Override
		protected Long getTypeMinValue() {
			return Long.MIN_VALUE;
		}

		@Override
		public boolean inRange(final Long value) {

			return value >= this.minValue && value <= this.maxValue;
		}

	}

	static class FloatRule extends NumberRule<Float> {

		public FloatRule(final boolean nullable, final String max,
				final String min, final String defaultValue, final String regex) {
			super(nullable, max, min, defaultValue, regex);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Float parse(final String value) {
			return Float.parseFloat(value);
		}

		@Override
		protected Float getTypeMaxValue() {
			return Float.MAX_VALUE;
		}

		@Override
		protected Float getTypeMinValue() {
			return Float.MIN_VALUE;
		}

		@Override
		public boolean inRange(final Float value) {

			return value >= this.minValue && value <= this.maxValue;
		}

	}

	static class DoubleRule extends NumberRule<Double> {

		public DoubleRule(final boolean nullable, final String max,
				final String min, final String defaultValue, final String regex) {
			super(nullable, max, min, defaultValue, regex);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Double parse(final String value) {
			return Double.parseDouble(value);
		}

		@Override
		protected Double getTypeMaxValue() {
			return Double.MAX_VALUE;
		}

		@Override
		protected Double getTypeMinValue() {
			return Double.MIN_VALUE;
		}

		@Override
		public boolean inRange(final Double value) {
			return value >= this.minValue && value <= this.maxValue;
		}

	}

}
