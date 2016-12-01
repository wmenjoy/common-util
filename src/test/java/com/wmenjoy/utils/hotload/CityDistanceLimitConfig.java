package com.wmenjoy.utils.hotload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 读取配置文件
 * @author jinliang.liu
 *
 */
public class CityDistanceLimitConfig extends BaseFileConfig<Map<String, Long>>{




    public CityDistanceLimitConfig(){
        super();
    }

    @Override
    public Map<String, Long> getResourceFromFile() {
        List<String> lines =readFileWithHeader(getFilePath());
        if(lines != null && lines.size() > 0){
            Map<String, Long> tempMap = new HashMap<String, Long>();

            for(String line: lines){
                String[] fields = line.split("[,，  ]");
                if(fields.length != 2){
                    continue;
                }
                tempMap.put(fields[0], Long.parseLong(fields[1]) * 1000);
            }

            return tempMap;
        }

        return null;

    }


    /**
     * 处理待BOM头文件
     * @param string
     * @return
     */
    private List<String> readFileWithHeader(final String filePath) {
        return FileUtil.readFileWithHeader(filePath);
    }

    @Override
    public String getFilePath() {
        return "GROUP_CITY_DISTANCE_FILE";
    }


}
