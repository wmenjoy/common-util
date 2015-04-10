package com.wmenjoy.utils.config.parser.rule;

import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.lang.StringUtils;


public class BooleanRule extends BaseFieldRule<Boolean> {

    boolean defaultValue = false;

    public BooleanRule() {
        super(true);
    }

    public BooleanRule(boolean nullable, boolean defaultValue) {
        super(nullable);
        this.defaultValue = defaultValue;
    }

    @Override
    public Boolean checkAndGetValue(String value) throws DataNotValidException {
        if (StringUtils.isBlank(value) && nullable) {
            throw new DataNotValidException("不能为空");
        }

        boolean realValue = defaultValue;

        if ("true".equalsIgnoreCase(value.trim())) {
            realValue = true;
        }

        return realValue;
    }

}
