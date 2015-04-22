package com.zncm.lovetuer.global;

//常量
public class GlobalConstants {
    public static final String HTTP_CHARSET = "utf-8";
    public static final int NETWORK_TIME_OUT = 45 * 1000;
    //-----
    public static final String APP_KEY = "98451f8ae9f8ab04ed882d3699a185af";
    public static final String APP_SECRET = "bd05dbc4b78294f5dac3544937ec4b5c";
    public static final String USER_AVATAR_URL = "http://www.tuer.me/user/avatar/";
    public static final String CALLBACK_URL = "http://www.tuer.me/api/apply";
    public static final String CALLBACK_ERROR_URL = CALLBACK_URL + "?error=access_denied";
    public static final String BASE_URL = "http://www.tuer.me/";
    public static final String BASE_API_URL = "http://api.tuer.me/";
    public static final String OAUTH = BASE_URL + "oauth/authorize?client_id=" + APP_KEY + "&redirect_uri=" + CALLBACK_URL + "&type=h5";
    public static final String OAUTH_RENEW = BASE_URL + "oauth/authorize?client_id=" + APP_KEY + "&redirect_uri=" + CALLBACK_URL + "&type=h5&x_renew=true";
    public static final String PAGE_COUNT = "20";
    public static final String KEY_PARAM_DATA = "key_param_data";
    public static final String KEY_DATA = "key_data";
    public static final String KEY_ID = "key_id";
    public static final String KEY_MODIFY = "key_modify";
    public static final int REQUESTCODE_ADD = 100;
    public static final int BUG_ID = 3284;
    //~~~~~~~~~~~~~~~~~~~~~~~`
    public static final String KEY_TYPE = "key_type";
    public static final long DAY = 24 * 60 * 60 * 1000;
    public static long DB_PAGE_SIZE = 50;
    public static final String KEY_LIST_DATA = "key_list_data ";
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    public static final String PATH_ROOT = "LoveTuer";
    public static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20MB
}
