package com.wmenjoy.utils.config.parser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			A, B, C, D
		}

		public int getKey() {
			return this.key;
		}

		public String getMap() {
			return this.map;
		}

		public void setMap(final String map) {
			this.map = map;
		}

		public void setKey(final Integer key) {
			this.key = key;
		}

		public List<Integer> getKeyValueArray() {
			return this.keyValueArray;
		}

		public void setKeyValueArray(final List<Integer> keyValueArray) {
			this.keyValueArray = keyValueArray;
		}

		public String getKeyValue() {
			return this.keyValue;
		}

		public void setKeyValue(final String keyValue) {
			this.keyValue = keyValue;
		}

		public void setSupported(final int key, final Date t) {
			this.supported = true;
		}

		public Date getDate() {
			return this.date;
		}

		public void setDate(final Date date) {
			this.date = date;
		}

		@Override
		public String toString() {
			return "TestData [key=" + this.key + ", map=" + this.map
					+ ", keyValue=" + this.keyValueArray + ", keyValueStr="
					+ this.keyValue + "]" + ", date=" + this.date
					+ ", supported=" + this.supported + ", type=" + this.type;
		}

	}

	public static void main(final String[] args) throws DataNotValidException,
			SystemConfigErrorException, DataAccessErrorException,
			ErrorAnnotationConfigException {
		final long BeginT = System.currentTimeMillis();
		final Map<String, String> keyValueMap = new HashMap<String, String>();

		keyValueMap.put("key", "3");
		keyValueMap.put("map", "key is 3");
		keyValueMap.put("keyValue", "1,2, 3,4");
		keyValueMap.put("date", "2015-03-14");
		keyValueMap.put("type", "A");
		final MapFieldSet<TestData> fieldSet = MapFieldSet
				.compile(TestData.class);
		System.out.println(System.currentTimeMillis() - BeginT);
		final TestData testData = fieldSet.parse(keyValueMap);
		final TestData testData2 = fieldSet.parse("3|1,2, 3,4");
		System.out.println(System.currentTimeMillis() - BeginT);
		System.out.println(testData);
		System.out.println(testData2);
	}

}
