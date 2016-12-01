
package com.wmenjoy.utils.hotload;

import java.util.Map;

public interface Properties {
    /**
     * Searches for the property with the specified key in this property list.
     * The method returns <code>null</code> if the property is not found.
     *
     * @param key the property key.
     * @return the value in this property list with the specified key value.
     */
    String getProperty(String key);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return the value in this property list with the specified key value if
     *         it exists, otherwise the specified default value.
     */
    String getProperty(String key, String defaultValue);

    /**
     * Returns the number of properties.
     *
     * @return the number of properties.
     */
    int size();

    /**
     * Returns <code>true</code> if this <code>Properties</code> contains no
     * properties.
     *
     * @return <code>true</code> if this <code>Properties</code> contains no
     *         properties.
     */
    boolean isEmpty();

    /**
     * Returns a read only map containing all of the properties.
     *
     * @return a read only map containing all of the properties.
     */
    Map<String, String> toReadonlyMap();

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or the found value is empty string.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return the value in this property list with the specified key value if
     *         it exists and is not empty, otherwise the specified default
     *         value.
     */
    String getRestrictProperty(String key, String defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns <code>true</code> if the property is found and its
     * value is equals to <code>"true"</code> (case insensitive), otherwise
     * <code>false</code>.
     *
     * @param key the property key.
     * @return <code>true</code> if the property is found and its value is
     *         equals to <code>"true"</code> (case insensitive), otherwise
     *         <code>false</code>.
     */
    boolean getRestrictTrue(String key);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns <code>false</code> if the property is found and its
     * value is equals to <code>"false"</code> (case insensitive), otherwise
     * <code>true</code>.
     *
     * @param key the property key.
     * @return <code>false</code> if the property is found and its value is
     *         equals to <code>"false"</code> (case insensitive), otherwise
     *         <code>true</code>.
     */
    boolean getRestrictFalse(String key);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value can not be parsed to a <code>boolean</code> value.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return a <code>boolean</code> value parsed from the value in this
     *         property list with the specified key value if it exists and its
     *         value can be parsed to <code>boolean</code> type, otherwise the
     *         specified default value.
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value can not be parsed to a <code>byte</code> value.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return a <code>byte</code> value parsed from the value in this property
     *         list with the specified key value if it exists and its value can
     *         be parsed to <code>type</code> type, otherwise the specified
     *         default value.
     */
    byte getByte(String key, byte defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value is empty.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return the first character of the value in this property list with the
     *         specified key value if it exists and its value is not empty,
     *         otherwise the specified default value.
     */
    char getChar(String key, char defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value can not be parsed to a <code>double</code> value.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return a <code>double</code> value parsed from the value in this
     *         property list with the specified key value if it exists and its
     *         value can be parsed to <code>double</code> type, otherwise the
     *         specified default value.
     */
    double getDouble(String key, double defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value can not be parsed to a <code>float</code> value.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return a <code>float</code> value parsed from the value in this property
     *         list with the specified key value if it exists and its value can
     *         be parsed to <code>float</code> type, otherwise the specified
     *         default value.
     */
    float getFloat(String key, float defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value can not be parsed to an <code>int</code> value.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return a <code>int</code> value parsed from the value in this property
     *         list with the specified key value if it exists and its value can
     *         be parsed to <code>int</code> type, otherwise the specified
     *         default value.
     */
    int getInt(String key, int defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value can not be parsed to a <code>long</code> value.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return a <code>long</code> value parsed from the value in this property
     *         list with the specified key value if it exists and its value can
     *         be parsed to <code>long</code> type, otherwise the specified
     *         default value.
     */
    long getLong(String key, long defaultValue);

    /**
     * Searches for the property with the specified key in this property list.
     * The method returns the default value argument if the property is not
     * found or its value can not be parsed to a <code>short</code> value.
     *
     * @param key the property key.
     * @param defaultValue a default value.
     * @return a <code>short</code> value parsed from the value in this property
     *         list with the specified key value if it exists and its value can
     *         be parsed to <code>short</code> type, otherwise the specified
     *         default value.
     */
    short getShort(String key, short defaultValue);
}
