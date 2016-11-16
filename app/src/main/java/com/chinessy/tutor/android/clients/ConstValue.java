package com.chinessy.tutor.android.clients;

/**
 * Created by susan on 2016/11/16.
 */

public class ConstValue {
    public static final String BasicUrl = "http://120.76.223.83:8090/Chinessy/index.php/Home/Index/";

    /**
     * 获取推流地址(直播网址)
     * 接口方式：POST
     * 功能描述：直播间的推流地址（如果不传key和time，将返回不含防盗链的url）
     * 传递参数：
     * 1)roomId		直播间号
     * 2)Key		安全密钥
     * 3)Time		过期时间 sample格式，例： 2016-11-12 12:00:00
     */
    public static final String getPushUrl = "getPushUrl";
}
