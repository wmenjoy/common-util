package com.wmenjoy.utils.config.parser.rule;

import com.wmenjoy.utils.config.parser.annotation.MNumber;
import com.wmenjoy.utils.config.parser.rule.NumberRule;

public abstract class NumberRuleUtil {

    public static NumberRule<? extends Number> getNumberRule(MNumber rule, Class<?> realClazz,
            String classDefaultValue) {

        String max = null;
        String min = null;
        String defaultValue = classDefaultValue;
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

        public IntRule(boolean nullable, String max, String min, String defaultValue, String regex) {
            super(nullable, max, min, defaultValue, regex);
        }

        @Override
        protected Integer parse(String value) {
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
        public boolean inRange(Integer value) {

            return value >= minValue && value <= maxValue;
        }

    }

    static class ByteRule extends NumberRule<Byte> {

        public ByteRule(boolean nullable, String max, String min, String defaultValue, String regex) {
            super(nullable, max, min, defaultValue, regex);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected Byte parse(String value) {
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
        public boolean inRange(Byte value) {

            return value >= minValue && value <= maxValue;
        }

    }

    static class ShortRule extends NumberRule<Short> {

        public ShortRule(boolean nullable, String max, String min, String defaultValue, String regex) {
            super(nullable, max, min, defaultValue, regex);
        }

        @Override
        protected Short parse(String value) {
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
        public boolean inRange(Short value) {

            return value >= minValue && value <= maxValue;
        }

    }

    static class LongRule extends NumberRule<Long> {

        public LongRule(boolean nullable, String max, String min, String defaultValue, String regex) {
            super(nullable, max, min, defaultValue, regex);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected Long parse(String value) {
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
        public boolean inRange(Long value) {

            return value >= minValue && value <= maxValue;
        }

    }

    static class FloatRule extends NumberRule<Float> {

        public FloatRule(boolean nullable, String max, String min, String defaultValue, String regex) {
            super(nullable, max, min, defaultValue, regex);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected Float parse(String value) {
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
        public boolean inRange(Float value) {

            return value >= minValue && value <= maxValue;
        }

    }

    static class DoubleRule extends NumberRule<Double> {

        public DoubleRule(boolean nullable, String max, String min, String defaultValue,
                String regex) {
            super(nullable, max, min, defaultValue, regex);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected Double parse(String value) {
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
        public boolean inRange(Double value) {

            return value >= minValue && value <= maxValue;
        }

    }

}
