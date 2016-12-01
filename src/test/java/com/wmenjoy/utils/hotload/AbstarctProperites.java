package com.wmenjoy.utils.hotload;


import java.util.Map;

import com.wmenjoy.utils.lang.StringUtils;


/**
 *
 * @author rj
 * @version 1.0
 */
abstract class AbstractProperties implements Properties {
    AbstractProperties() {
    } // Sub classes must be in the same package.

    abstract String get(String key);

    @Override
    public abstract int size();

    @Override
    public final boolean isEmpty() {
        return (this.size() == 0);
    }

    @Override
    public final String getProperty(final String key) {
        return this.get(key);
    }

    @Override
    public final String getProperty(final String key, final String defaultValue) {
        final String val = this.get(key);
        return (val == null) ? defaultValue : val; // compatible with JDK implementation.
    }

    @Override
    public abstract Map<String, String> toReadonlyMap();

    ////////////////////////////////////////////////////////////////////////////

    @Override
    public final String getRestrictProperty(final String key, final String defaultValue) {
        final String value = this.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    @Override
    public final boolean getRestrictTrue(final String key) {
        final String value = this.get(key);
        return StringUtils.isEmpty(value) ? false : value.equalsIgnoreCase("true");
    }

    @Override
    public final boolean getRestrictFalse(final String key) {
        final String value = this.get(key);
        return StringUtils.isEmpty(value) ? true : !value.equalsIgnoreCase("false");
    }

    @Override
    public final boolean getBoolean(final String key, final boolean defaultValue) {
        final String value = this.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    @Override
    public final byte getByte(final String key, final byte defaultValue) {
        final String value = this.get(key);
        try {
            return StringUtils.isEmpty(value) ? defaultValue : Byte.parseByte(value);
        } catch (final NumberFormatException e) {

            return defaultValue;
        }
    }

    @Override
    public final char getChar(final String key, final char defaultValue) {
        final String value = this.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : value.charAt(0);
    }

    @Override
    public final double getDouble(final String key, final double defaultValue) {
        final String value = this.get(key);
        try {
            return StringUtils.isEmpty(value) ? defaultValue : Double.parseDouble(value);
        } catch (final NumberFormatException e) {

            return defaultValue;
        }
    }

    @Override
    public final float getFloat(final String key, final float defaultValue) {
        final String value = this.get(key);
        try {
            return StringUtils.isEmpty(value) ? defaultValue : Float.parseFloat(value);
        } catch (final NumberFormatException e) {

            return defaultValue;
        }
    }

    @Override
    public final int getInt(final String key, final int defaultValue) {
        final String value = this.get(key);
        try {
            return StringUtils.isEmpty(value) ? defaultValue : Integer.parseInt(value);
        } catch (final NumberFormatException e) {

            return defaultValue;
        }
    }

    @Override
    public final long getLong(final String key, final long defaultValue) {
        final String value = this.get(key);
        try {
            return StringUtils.isEmpty(value) ? defaultValue : Long.parseLong(value);
        } catch (final NumberFormatException e) {

            return defaultValue;
        }
    }

    @Override
    public final short getShort(final String key, final short defaultValue) {
        final String value = this.get(key);
        try {
            return StringUtils.isEmpty(value) ? defaultValue : Short.parseShort(value);
        } catch (final NumberFormatException e) {

            return defaultValue;
        }
    }

}
