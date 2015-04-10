package com.wmenjoy.utils.config.parser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wmenjoy.utils.config.parser.DataAccessErrorException;
import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.config.parser.ErrorAnnotationConfigException;
import com.wmenjoy.utils.config.parser.MapFieldSet;
import com.wmenjoy.utils.config.parser.SystemConfigErrorException;
import com.wmenjoy.utils.config.parser.annotation.MAlias;
import com.wmenjoy.utils.config.parser.annotation.MData;
import com.wmenjoy.utils.config.parser.annotation.MFunction;
import com.wmenjoy.utils.config.parser.annotation.MNumber;

public class MapFieldSetTest {
    @MData(fieldStr = "key|keyValue")
    public static class TestData {
        @MNumber(max = "5", min = "2")
        private Integer key;
        private String map;
        @MAlias("keyValue")
        private List<Integer> keyValueArray;
        private Date date;
        /**
         * 是否支持红包
         */
        @MFunction(name = "setSupported", fields = { "key", "date" })
        private boolean supported;

        private String keyValue;

        private Type type = Type.D;
        
        public static enum Type {
            A,B,C,D
        }
        
        public int getKey() {
            return key;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        public List<Integer> getKeyValueArray() {
            return keyValueArray;
        }

        public void setKeyValueArray(List<Integer> keyValueArray) {
            this.keyValueArray = keyValueArray;
        }

        public String getKeyValue() {
            return keyValue;
        }

        public void setKeyValue(String keyValue) {
            this.keyValue = keyValue;
        }

        public void setSupported(int key, Date t) {
            this.supported = true;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "TestData [key=" + key + ", map=" + map + ", keyValue=" + keyValueArray
                    + ", keyValueStr=" + keyValue + "]" + ", date=" + date + ", supported="
                    + supported + ", type=" + type;
        }

    }

    public static void main(String[] args) throws DataNotValidException,
            SystemConfigErrorException, DataAccessErrorException, ErrorAnnotationConfigException {
        long BeginT = System.currentTimeMillis();
        Map<String, String> keyValueMap = new HashMap<String, String>();

        keyValueMap.put("key", "3");
        keyValueMap.put("map", "key is 3");
        keyValueMap.put("keyValue", "1,2, 3,4");
        keyValueMap.put("date", "2015-03-14");
       // keyValueMap.put("type", "A");
        MapFieldSet<TestData> fieldSet = MapFieldSet.compile(TestData.class);
        System.out.println(System.currentTimeMillis() - BeginT);
        TestData testData = fieldSet.parse(keyValueMap);
        TestData testData2 = fieldSet.parse("3|1,2, 3,4");
        System.out.println(System.currentTimeMillis() - BeginT);
        System.out.println(testData);
        System.out.println(testData2);
    }

}
