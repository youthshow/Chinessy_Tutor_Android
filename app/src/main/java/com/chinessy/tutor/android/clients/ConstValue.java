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


    /**
     * 接口地址：http://192.168.3.239:8090/Chinessy/index.php/Home/Index/getStudio
     * 接口方式：POST
     * 功能描述：点我要直播按钮，生成直播间，并获取老师的个人信息
     * 传递参数：
     * 1)userId		老师的用户ID
     */
    public static final String getStudio = "getStudio";

    /*
    * 接口地址：http://192.168.3.239:8090/Chinessy/index.php/Home/Index/getTeacherBinds
    * 接口方式：POST
    * 功能描述：查询教师端里一对一绑定信息学生列表
    * 传递参数：
    * 1)userId		老师的用户ID
    * */
    public static final String getTeacherBinds = "getTeacherBinds";
}
