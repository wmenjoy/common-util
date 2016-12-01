package com.wmenjoy.utils.hotload;

public enum ResourceManager {

    //文件
    CityWeighConfig("cityWeightConfig", new CityDistanceLimitConfig()),
    ;

    private String name;
    private BaseFileConfig fileConfig;

    private ResourceManager(final String name, final BaseFileConfig fileConfig){
        this.name = name;
        this.fileConfig = fileConfig;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BaseFileConfig getFileConfig() {
        return this.fileConfig;
    }

    public void setFileConfig(final BaseFileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }






}
