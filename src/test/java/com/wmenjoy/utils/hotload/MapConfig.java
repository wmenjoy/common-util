package com.wmenjoy.utils.hotload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.wmenjoy.utils.lang.StringUtils;
import com.wmenjoy.utils.log.ExceptionLogger;

public class MapConfig extends BaseFileConfig<Map<String, String>>{

    public MapConfig(final String fileName){
        super(fileName);
    }

    @Override
    public String getFilePath() {
        return this.getFileName();
    }

    @Override
    public Map<String, String> getResourceFromFile() {
        java.util.Properties propeties =  readFile(this.getFileName());
        Map<String, String> tempMap = new HashMap<String, String>();
        for (final Map.Entry p : propeties.entrySet()) {
            tempMap.put((String)p.getKey(), (String)p.getValue());
        }

        return tempMap;
    }

    public Map<String, String> getConfig(){
        return this.getResource();
    }

    public static java.util.Properties readFile(final String filePath){

        final java.util.Properties ret = new java.util.Properties();

        if (StringUtils.isBlank(filePath)) {
            return ret;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return ret;
        }

        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            // BOMInputStream bomin = new BOMInputStream(fis);
            br = new BufferedReader(new InputStreamReader(fis));
        } catch (FileNotFoundException e) {
            ExceptionLogger.error(e);
        }
        if (br == null) {
            return ret;
        }

        try {
            ret.load(br);
        } catch (IOException e) {
            ExceptionLogger.error(e);
        }

        return ret;
    }

    private DynamicProperties tempProperties = new DynamicProperties(this);


    public Properties toProperties(){
        return this.tempProperties;
    }

    private static final class DynamicProperties extends
    AbstractProperties {
        private MapConfig mapConfig;


        public DynamicProperties(final MapConfig mapConfig) {
            this.mapConfig = mapConfig;
        }

        @Override
        public final String get(final String key) {
            return this.mapConfig.getConfig().get(key);
        }

        @Override
        public final int size() {
            return this.mapConfig.getConfig().size();
        }

        @Override
        public final Map<String, String> toReadonlyMap() {
            return Collections.unmodifiableMap(this.mapConfig.getConfig());
        }
    }
}
