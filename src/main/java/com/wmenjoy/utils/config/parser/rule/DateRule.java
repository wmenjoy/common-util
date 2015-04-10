package com.wmenjoy.utils.config.parser.rule;

import java.util.Date;

import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.lang.DateUtil;
import com.wmenjoy.utils.lang.StringUtils;


public class DateRule extends BaseFieldRule<Date> {
    private String format = DateUtil.FORMAT_DEFAULT;

    public DateRule() {
        super(true);
    }

    public DateRule(boolean nullable, String format) {
        super(nullable);
        this.format = format;
    }

    @Override
    public Date checkAndGetValue(String value) throws DataNotValidException {
        if (StringUtils.isBlank(value) && !nullable) {
            throw new DataNotValidException("字段不合法：" + value);
        }
        Date realDate = null;
        if (StringUtils.isBlank(value)) {
            realDate = new Date();
        } else {
            realDate = DateUtil.getDate(value.trim(), format);
        }
        return realDate;
    }

}
