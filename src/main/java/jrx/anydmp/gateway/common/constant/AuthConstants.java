package jrx.anydmp.gateway.common.constant;


/**
 * @author peidong.meng
 * @date 2018/4/17
 */
public class AuthConstants {


    public static String LOGIN_URL="/login";
    public static String INDEX_URL="/index";
    public static String CURRENT_SGIN = "sgin";
    public static String AUTH_KEY = "Authorization";
    public static String AUTH_PRO = "Bearer ";

    public static Long Time_Out = 10000L;

    public static String UTF8 = "UTF-8";
    public static String STATUS_CODE = "statusCode";
    public static Integer SUCCESS_CODE = 200;
    public static String RESULT = "result";
    public static String HEADER = "header";
    
    public static final String TOKEN_EXPIRE_MESSAGE = "Token has expire";
    public static final String MESSAGE = "message";
    
    public static String TOKEN = "token";
    public static String USER = "user";
    public static String RESOURCE = "resource";
    public static String PROJECT = "project";
    public static String CONTENT = "content";
    public static String VIEWSCOPE = "viewScope";
    public static String LOCLCONTENT = "localContent";

    public static String TOKENTIMEOUT = "token has time out";
    /** 不鉴权模式 */
    public static String NOAUTHMODE = "0";
    /** 仅登陆模式 */
    public static String ONLYLOGINMODE = "1";

}
