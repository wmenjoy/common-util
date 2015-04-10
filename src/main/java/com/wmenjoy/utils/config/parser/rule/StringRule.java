package com.wmenjoy.utils.config.parser.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




import com.wmenjoy.utils.config.parser.DataNotValidException;
import com.wmenjoy.utils.lang.StringUtils;


public class StringRule extends BaseFieldRule<String> {
    Pattern pattern;

    private String regex = "";

    /**
     * 转义字符
     * 
     * @return
     */
    private String escape = "\\";

    /**
     * 自动做trim
     * 
     * @return
     */
    private boolean autoTrim = true;

    private String defaultValue = null;

    public StringRule() {
        super(true);
    }

    public StringRule(boolean nullable, String regex, String escape, boolean autoTrim,
            String defaultValue) {
        super(nullable);
        this.autoTrim = autoTrim;
        this.regex = regex;
        this.escape = escape;
        if (StringUtils.isNotBlank(regex)) {
            pattern = Pattern.compile(regex);
        }
        this.defaultValue = defaultValue;
    }

    @Override
    public String checkAndGetValue(String value) throws DataNotValidException {

        //处理空串
        if (!nullable && (StringUtils.isBlank(value))) {
            throw new DataNotValidException("不能为空");
        }

        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        //处理转义
        final String tmpValue = filter(value);

        if (pattern != null) {
            if (value == null) {
                throw new DataNotValidException("不能为空");
            }

            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                throw new DataNotValidException("不符合正则表达式:" + regex + ", value:" + value);
            }
        }

        return tmpValue;
    }

    private String filter(String value) {
        //转义字符待处理
        if (autoTrim) {
            return StringUtils.trim(value);
        }

        //TODO 转义字符待实现
        return value;
    }

}
