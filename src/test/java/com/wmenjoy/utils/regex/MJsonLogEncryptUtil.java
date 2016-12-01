package com.wmenjoy.utils.regex;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wmenjoy.utils.lang.StringUtils;
import com.wmenjoy.utils.log.ExceptionLogger;



/**
 * 1、处理json对象，关键字段，进行混淆处理
 * @author jinliang.liu
 *
 */
public class MJsonLogEncryptUtil {

    //需要加密的json串mobile节点
    public static final String MOBILE_NODE = "mobile|phone|contactPhone|ContactPhone|contact|mob|contactphone|telephone|invoiceContactPhone|mob|sjrPhone|contactMob|expressphone|PhoneNum";

    //需要加密的json串节点
    public static final String NON_EMAIL_AND_MOBILE_NODE = "cardNo|identityNo|contact_cardid|credentialsNo|certNo|cardNum|cardno|iDCard|idNo|air_CertificateNo";

    //需要加密的json串节点
    public static final String EMAIL_NODE = "email|contactEmail";

    //“:"编码 json目前最多支持两层对象嵌套，即，有一个json属性是一个json对象。支持多层嵌套
    public static String NODE_VALUE_INTERVAL_REGEX = "%22%3A%22|(?:\\\\)*\":(?:\\\\)*\"";

    //"json的编码支持多层嵌套
    public static String QUATE_REGEX="%22|(?:\\\\)*\"";

    //正则匹配 支持两层嵌套的json对象
    public static final String MOBILE_NODE_REGEX="((?:"+QUATE_REGEX+")(?:" + MOBILE_NODE +")(?:"+NODE_VALUE_INTERVAL_REGEX+")1\\d{2})(?:\\d{4})(\\d{4}(?:"+QUATE_REGEX+"))";

    //正则匹配 非emai，非手机号    “certNo":"sdfsfs"         \"certNo\":\"sdsfs\"   \\\"certNo\\\":\\\"sdsfs\\\" 等等此处将正则表达式
    public static final String NON_EMAIL_AND_MOBILE_NODE_REGEX= "((?:"+QUATE_REGEX+")(?:" + NON_EMAIL_AND_MOBILE_NODE +")(?:"+NODE_VALUE_INTERVAL_REGEX+"))(?:(?:.(?!(?:"+QUATE_REGEX+")))*.)("+QUATE_REGEX+")";

    //email 正则表达式:
    public static final String EMAIL_NODE_REGEX=
            "((?:"+QUATE_REGEX+")(?:" + EMAIL_NODE +")(?:"+NODE_VALUE_INTERVAL_REGEX+")[a-zA-Z0-9_+.-])(?:[a-zA-Z0-9_+.-]*)(@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z0-9]{2,4}(?:"+QUATE_REGEX+"))";


    /**
     * 使用正则替换的替换后表达式
     */
    //替换后的手机号字符串
    public static final String MOBILE_ENCYRPT_REGEX="$1****$2";

    //替换后的非手机和邮箱的字符串
    public static final String NON_EMAIL_AND_MOBILE_ENCYRPT_REGEX="$1******$2";
    //替换后的邮箱字符串
    public static final String EMAIL_ENCYRPT_REGEX="$1***$2";

    /**
     *  json 相关的编码
     */
    //json 串开始的字符串
    public static final String JSON_START_STRING = "{";

    //json串结束的字符
    public static final String JSON_END_STRING = "}";

    //josn传 uri encode 开始字符
    public static final String JSON_ENCODE_START_STRING = "%7B";

    //josn传 uri encode 结束字符
    public static final String JSON_ENCODE_END_STRING = "%7D";

    /**
     * 预先编译好的pattern
     */
    //非手机号和邮箱的正则Pattern对象，静态化，减少编译时间
    private static Pattern NON_EMAIL_AND_MOBILE_PATTERN = Pattern.compile(NON_EMAIL_AND_MOBILE_NODE_REGEX);

    //json对象中进行匹配手机号的Pattern 对象，减少编译缓解，可做性能优化
    private static Pattern MOBILE_PATTERN = Pattern.compile(MOBILE_NODE_REGEX);

    //json对象中进行匹配手机号的Pattern 对象，减少编译缓解，可做性能优化
    private static Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_NODE_REGEX);


    //需要做混淆的commandId
    private static Set<Integer> needEncryptCommandSet = new HashSet<Integer>();
    //需要处理的url
    private static Set<String> needEncryptURLSet = new HashSet<String>();
    //需要排除的commondID
    private static  Set<Integer> exceptCommandIdSet = new HashSet<Integer>();
    //请求参数需要处理的set
    private static Set<Integer> requestParamNeedEncryptCommandSet = new HashSet<Integer>();

    private static  int[] exceptCommandIDArr = {207,208,210,213,219,410,411,412,420,447,1103,1201,1217,1250,1410,1422,1427,2002,2005,10101};

    static {

        initComandSet();
        initURLSet();
        initExceptCommandIDSet();
        initRequestParamNeedEncryptCommandSet();
    }

    /**
     * 如果commandID为空, false
     * 如果commandID 为不需要屏蔽手机号的commandId返回为false
     * @return
     */
    private static boolean isExceptCommandID(){
        Integer commandId = TraceUtils.getCommandID();

        if(commandId == null){
            return false;
        }

        if(commandId == -1){
            return false;
        }



        if(exceptCommandIdSet.contains(commandId)){
            return true;
        }

        //TODO： 暂时排除有commandID的, 等确定以后修符
        return true;
    }



    public static boolean needExceptionMobileEncypt(final String url){

        if(isExceptCommandID() && !needEncyptURL(url)){
            return true;
        }

        return false;
    }

    /**
     * 针对json传做手机号的加密
     * @param jsonStr
     * @return
     */
    public static String encryptJson4Phone(final String jsonStr){
        if (MUrlLogEncryptUtil.isDecryptLog())
        {
            return jsonStr;//敏感信息加密
        }

        if(!isJsonObject(jsonStr)){
            return jsonStr;
        }
        return encryptPhone(jsonStr);
    }

    //初始化带排除的commandID
    private static void initExceptCommandIDSet() {
        for(int comandId: exceptCommandIDArr){
            exceptCommandIdSet.add(comandId);
        }

    }

    private static void initURLSet() {


        //获取支付方式
        needEncryptURLSet.add("http://l-qtt/conf/bankList");
    }

    /**
     * 初始化commandid的集合
     */
    private static void initComandSet() {
        //酒店
        needEncryptCommandSet.add(1);


    }

    private static void initRequestParamNeedEncryptCommandSet() {
        requestParamNeedEncryptCommandSet.add(1);
    }

    /**
     * 针对json传非手机号和邮箱做加密
     * @param jsonStr 待处理的json字符串， 可以是经过uri 字符转换的字符串
     * @return
     */
    public static String encryptJson4Other(final String jsonStr){
        if (MUrlLogEncryptUtil.isDecryptLog())
        {
            return jsonStr;//敏感信息加密
        }

        if(!isJsonObject(jsonStr)){
            return jsonStr;
        }
        return encryptOther(jsonStr);
    }


    /**
     * 针对json传做邮箱的加密
     * @param jsonStr
     * @return
     */
    public static String encryptJson4Email(final String jsonStr){
        if (MUrlLogEncryptUtil.isDecryptLog())
        {
            return jsonStr;//敏感信息加密
        }

        if(!isJsonObject(jsonStr)){
            return jsonStr;
        }
        return encryptEmail(jsonStr);
    }

    static String encryptEmail(final String jsonStr){
        Matcher matcher = EMAIL_PATTERN.matcher(jsonStr);
        return matcher.replaceAll(EMAIL_ENCYRPT_REGEX);
    }



    /**
     * TODO：由于统计需求问题， 暂时屏蔽
     * @param jsonStr
     * @return
     */
    static String encryptPhone(final String jsonStr){
        Matcher matcher = MOBILE_PATTERN.matcher(jsonStr);
        return matcher.replaceAll(MOBILE_ENCYRPT_REGEX);
    }

    /**
     * 针对非手机号，非邮箱的进行混淆
     * @param jsonStr
     * @return
     */
    static String encryptOther(final String jsonStr){
        Matcher matcher = NON_EMAIL_AND_MOBILE_PATTERN.matcher(jsonStr);
        return matcher.replaceAll(NON_EMAIL_AND_MOBILE_ENCYRPT_REGEX);
    }

    /**
     * 根据commandID 来对结果串进行
     * @param jsonStr
     * @return
     */
    public static String encryptJsonByCommand(final String jsonStr){
        if(!isJsonObject(jsonStr)){
            return jsonStr;
        }

        if(!needEncyptCommand()){
            return jsonStr;
        }

        return encryptOther(encryptEmail(encryptPhone(jsonStr)));
    }



    /**
     * 对结果进行处理，
     * 1、如果结果不是json对象，直接返回
     *  2、TODO:屏蔽掉手机号码的替换 ------------待确定方案修订
     * @param result 返回的json对象
     * @param url 请求的url
     * @return
     */
    public static String encryptResult(final String result, final String url) {
        if (MUrlLogEncryptUtil.isDecryptLog())
        {
            return result;//敏感信息加密
        }

        if (!isJsonObject(result)) {
            return result;
        }

        try {
            if (needEncyptURL(url) || needEncyptCommand()) {
                String encyptedPhoneResult = result;
                if (!isExceptCommandID()) {
                    encyptedPhoneResult = encryptPhone(result);
                }
                return encryptOther(encryptEmail(encyptedPhoneResult));
            }
        } catch (Exception e) {
            ExceptionLogger.error(e);
        }
        return result;
    }





    /**
     * 是否需要对url做特殊处理
     * @param url
     * @return
     */
    private static boolean needEncyptURL(final String url) {
        if(StringUtils.isBlank(url)){
            return false;
        }

        String baseURL = HttpUtil.getBaseURL(url);

        if(needEncryptURLSet.contains(baseURL)){
            return true;
        }
        return false;
    }

    /**
     * 判断对应的commandId是否需要执行加密操作
     * @return
     */
    private static boolean needEncyptCommand() {
        Integer commandId = TraceUtils.getCommandID();

        if(commandId == null){
            return false;
        }

        if(needEncryptCommandSet.contains(commandId)){
            return true;
        }

        return false;
    }

    /**
     * 简单判断一个字符串是不是json对象
     * @param jsonStr 带判定的json对象
     * @return
     */
    private static boolean isJsonObject(final String jsonStr) {

        if (jsonStr == null) {
            return false;
        }
        if ((jsonStr.startsWith(JSON_START_STRING) && jsonStr
                .endsWith(JSON_END_STRING))
                || (jsonStr.startsWith(JSON_ENCODE_START_STRING) && jsonStr
                        .endsWith(JSON_ENCODE_END_STRING))) {
            return true;
        }

        return false;
    }

    /**
     * @param value
     * @return
     */
    public static String encryptJsonRequestParamByCommand(final String value) {
        if(!isJsonObject(value)){
            return value;
        }

        try {
            if (!needRequestParamEncryptCommand()) {
                return value;
            }
            String encyptedPhoneResult = value;
            if (!isExceptCommandID()) {
                encyptedPhoneResult = encryptPhone(value);
            }
            return encryptOther(encryptEmail(encyptedPhoneResult));
        } catch (Exception e) {

            return value;
        }
    }

    private static boolean needRequestParamEncryptCommand() {
        Integer commandId = TraceUtils.getCommandID();

        if(commandId == null){
            return false;
        }

        if(requestParamNeedEncryptCommandSet.contains(commandId)){
            return true;
        }

        return false;
    }
}
