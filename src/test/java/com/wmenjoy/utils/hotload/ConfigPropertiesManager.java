package com.wmenjoy.utils.hotload;

import java.util.Map;

/***
 * 负责统一管理config的配置文件热发
 *
 * @author jinliang.liu
 *
 */
public enum ConfigPropertiesManager implements Properties {

    /** ugc 文案 */
    UGC_MESSAGE_CONFIG("fileName", "ugc文案修改")

    ;
    /** 配置信息 */
    Properties config;
    /** 描述信息 */
    String desc;

    ConfigPropertiesManager(final String fileName, final String desc) {
        this.config = new MapConfig(fileName).toProperties();
        this.desc = desc;
    }

    @Override
    public String getProperty(final String key) {
        return getConfig().getProperty(key);
    }

    private Properties getConfig() {
        return this.config;
    }

    @Override
    public String getProperty(final String key, final String defaultValue) {
        return getConfig().getProperty(key, defaultValue);
    }

    @Override
    public int size() {
        return getConfig().size();
    }

    @Override
    public boolean isEmpty() {
        return getConfig().isEmpty();
    }

    @Override
    public Map<String, String> toReadonlyMap() {
        return getConfig().toReadonlyMap();
    }

    @Override
    public String getRestrictProperty(final String key, final String defaultValue) {
        return getConfig().getRestrictProperty(key, defaultValue);
    }

    @Override
    public boolean getRestrictTrue(final String key) {
        return getConfig().getRestrictTrue(key);
    }

    @Override
    public boolean getRestrictFalse(final String key) {
        return getConfig().getRestrictFalse(key);
    }

    @Override
    public boolean getBoolean(final String key, final boolean defaultValue) {
        return getConfig().getBoolean(key, defaultValue);
    }

    @Override
    public byte getByte(final String key, final byte defaultValue) {
        return getConfig().getByte(key, defaultValue);
    }

    @Override
    public char getChar(final String key, final char defaultValue) {
        return getConfig().getChar(key, defaultValue);
    }

    @Override
    public double getDouble(final String key, final double defaultValue) {
        return getConfig().getDouble(key, defaultValue);
    }

    @Override
    public float getFloat(final String key, final float defaultValue) {
        return getConfig().getFloat(key, defaultValue);
    }

    @Override
    public int getInt(final String key, final int defaultValue) {
        return getConfig().getInt(key, defaultValue);
    }

    @Override
    public long getLong(final String key, final long defaultValue) {
        return getConfig().getLong(key, defaultValue);
    }

    @Override
    public short getShort(final String key, final short defaultValue) {
        return getConfig().getShort(key, defaultValue);
    }

}
