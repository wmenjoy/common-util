package com.wmenjoy.utils.regex;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wmenjoy.utils.lang.StringUtils;


public class MUrlLogEncryptUtil {

    private static Set<String> needEncryptURLSet = new HashSet<String>();
    // 需要加密的json串mobile节点
    public static final String MOBILE_NODE = "mobile|phone|contactPhone|ContactPhone|contact|mob|contactphone|telephone|invoiceContactPhone|mob|sjrPhone|contactMob|expressphone";

    // 正则匹配
    public static final String MOBILE_NODE_REGEX = "((?:" + MOBILE_NODE
            + ")=1\\d{2})(?:\\d{4})(\\d{4})";

    // 替换后的手机号字符串
    public static final String MOBILE_ENCYRPT_REGEX = "$1****$2";

    // json对象中进行匹配手机号的Pattern 对象，减少编译缓解，可做性能优化
    private static Pattern MOBILE_PATTERN = Pattern.compile(MOBILE_NODE_REGEX);

    private final static String email_regexp = "^[a-zA-Z0-9_+.-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z0-9]{2,4}$";
    private final static String mobile_regexp = "(?:1\\d{2})(?:\\d{4})(?:\\d{4})";

    /**
     * 需要加密的参数key
     */
    private static final Set<String> encodePhoneParamSet = new HashSet<String>();
    static {
        encodePhoneParamSet.add("mobile");
        encodePhoneParamSet.add("mobiles");
        encodePhoneParamSet.add("phonenum");
        encodePhoneParamSet.add("phone");
        encodePhoneParamSet.add("contactmob");
        encodePhoneParamSet.add("contactPhone");
        encodePhoneParamSet.add("telephone");
        encodePhoneParamSet.add("verifyKey");
    }

    /**
     * 需要加密的参数key card info
     */
    private static final Set<String> encodeCardParamSet = new HashSet<String>();
    static {
        encodeCardParamSet.add("cardno");
        encodeCardParamSet.add("cardnum");
        encodeCardParamSet.add("cardno");
        encodeCardParamSet.add("identityNo");
        encodeCardParamSet.add("credentialsno");
    }
    private static final Set<String> pwdencodeParamSet = new HashSet<String>();
    static {
        pwdencodeParamSet.add("password");
        pwdencodeParamSet.add("newpassword");
        pwdencodeParamSet.add("oldpassword");
        pwdencodeParamSet.add("spwd");
        pwdencodeParamSet.add("newSpwd");
    }

    // json对象的处理
    private static final Set<String> emailEncodeParam = new HashSet<String>();
    static {
        emailEncodeParam.add("email");
        emailEncodeParam.add("username");
        emailEncodeParam.add("contact");
    }

    // json对象的处理
    private static final Set<String> jsonEncodeParam = new HashSet<String>();
    static {
        // 处理请求参数为手机号的
        jsonEncodeParam.add("order");
        jsonEncodeParam.add("hcsOrderPojo");
    }

    private static final String ORDER_DETAIL = "orderDetail";

    private static final String PAYFORM = "payForm";

    static {
    }

    public static String encryptUrlParam4Mobile(String url) {

        String baseUrl = HttpUtil.getBaseURL(url);
        if(StringUtils.containsIgnoreCase(url, "conf/bankList")){
            url = MJsonLogEncryptUtil.encryptOther(MJsonLogEncryptUtil.encryptEmail(MJsonLogEncryptUtil.encryptPhone(url)));
        }
        if (StringUtils.isBlank(url) || !needEncryptURLSet.contains(baseUrl)) {
            if (StringUtils.contains(url,"subscribe")){
                Matcher matcher = MOBILE_PATTERN.matcher(url);
                return matcher.replaceAll(MOBILE_ENCYRPT_REGEX);
            }
            return url;
        }


        Matcher matcher = MOBILE_PATTERN.matcher(url);
        return matcher.replaceAll(MOBILE_ENCYRPT_REGEX);
    }

    /**
     * 处理 a=***&b=**这种形式的参数替换
     *
     * @param paramStr
     * @return
     */
    public static String encryptParams4Mobile(final String paramStr) {
        if (StringUtils.isBlank(paramStr)) {
            return paramStr;
        }
        Matcher matcher = MOBILE_PATTERN.matcher(paramStr);
        return matcher.replaceAll(MOBILE_ENCYRPT_REGEX);
    }

    public static boolean isDecryptLog() {
        // TODO Auto-generated method stub
        return false;
    }

}
