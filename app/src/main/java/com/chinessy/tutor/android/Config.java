package com.chinessy.tutor.android;

import java.util.TimeZone;

/**
 * Created by larry on 15/11/3.
 */
public class Config {
    public static final String CLIENT_TYPE = "cns_tutor_app";
    public static final String FOLDER_MAIN = "ChinessyTutor/";
    public static final String FOLDER_HEAD_IMG = FOLDER_MAIN + "img/head/";
    public static final String SP_SETTINGS = "settings";

    public static final TimeZone TIMEZONE_GMT0 = TimeZone.getTimeZone("GMT+0");

    public static final String JUSTALK_CLOUD_APPKEY = "45daa988a3a55fb97ccf5096";
    public static final String JUSTALK_CLOUD_SERVER_ADDRESS = "sudp:dev.ae.justalkcloud.com:9851";
//    public static final String JUSTALK_CLOUD_SERVER_ADDRESS = "sudp:ae.justalkcloud.com:9851";

    //    端口9090的是测试服务器
    //    8090是生产环境的端口
    //   public static final String BASE_URL = "http://apidev.chinessy.com:9090/";
    public static final String BASE_URL = "http://api.chinessy.com:8090/";

    public static final String MIPUSH_APP_ID = "2882303761517405953";
    public static final String MIPUSH_APP_KEY = "5381740525953";


}
