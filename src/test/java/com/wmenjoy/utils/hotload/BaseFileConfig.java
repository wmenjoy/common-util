package com.wmenjoy.utils.hotload;

import com.wmenjoy.utils.log.ExceptionLogger;

/**
 * 公用配置方法
 * @author jinliang.liu
 *
 * @param <V>
 */
public abstract class BaseFileConfig<V> implements IFileConfig{
    private volatile V resource = null;

    /***
     * 文件名
     */
    private String fileName;



    public String getFileName() {
        return this.fileName;
    }

    protected BaseFileConfig(final String fileName){
        this.fileName = fileName;
        //第一次调用
        config();
        //注册
        HotLoadManager.getInstance().register(getFilePath(), this);
    }

    protected BaseFileConfig(){
        //第一次调用
        config();
        //注册
        HotLoadManager.getInstance().register(getFilePath(), this);
    }

    //想要注册的文件路径
    public abstract String getFilePath();


    /**
     * 配置方法
     */
    @Override
    public void config(){
        V v = null;
        try {
            v = getResourceFromFile();
        } catch (Exception e) {
            ExceptionLogger.error(e);
        }

        if (v != null) {
            this.resource = v;
        }
    }
    /**
     * 从资源文件返回对象
     *
     * 	@return
     */
    public abstract V getResourceFromFile();

    public V getResource() {
        return this.resource;
    }

    public void setResource(final V resource) {
        this.resource = resource;
    }

}
