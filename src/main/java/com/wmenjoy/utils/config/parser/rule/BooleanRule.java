package com.wmenjoy.utils.config.parser.rule;

import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.lang.StringUtils;

public class BooleanRule extends BaseFieldRule<Boolean> {

	boolean defaultValue = false;

	public BooleanRule() {
		super(true);
	}

	public BooleanRule(final boolean nullable, final boolean defaultValue) {
		super(nullable);
		this.defaultValue = defaultValue;
	}

	@Override
	public Boolean checkAndGetValue(final String value)
			throws DataNotValidException {
		if (StringUtils.isBlank(value) && this.nullable) {
			throw new DataNotValidException("不能为空");
		}

		boolean realValue = this.defaultValue;

		if ("true".equalsIgnoreCase(value.trim())) {
			realValue = true;
		}

		return realValue;
	}

}
