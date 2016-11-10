package com.chinessy.tutor.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.chinessy.tutor.android.clients.InternalClient;
import com.chinessy.tutor.android.handlers.JusTalkHandler;
import com.chinessy.tutor.android.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.tutor.android.models.User;
import com.chinessy.tutor.android.service.TutorService;
import com.chinessy.tutor.android.utils.ServiceUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.chinessy.tutor.android.service.IndependentTutorService;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by larry on 15/7/10.
 */
public class Chinessy extends Application implements Application.ActivityLifecycleCallbacks{
    final static String tag = "ChinessyApplication";

    public final static int HANDLER_JUSTALK_LOGIN = 100;
    public final static int HANDLER_CHECK_SERVICE = 101;

    public static Chinessy chinessy;
    private List<Activity> activityList = new ArrayList<Activity>();

    private User user;

    int activityCount =0;
    Timer mTimer = new Timer(true);
    boolean ifInBackground = false;

    JusTalkHandler jusTalkHandler;

    public Handler mHandler = new ChinessyHandler();
    @Override
    public void onCreate() {
        super.onCreate();
        if(shouldInit()) {
            jusTalkHandler = new JusTalkHandler(this.getApplicationContext());
        }
        Chinessy.chinessy = this;
        registerActivityLifecycleCallbacks(this);

        MobclickAgent.updateOnlineConfig(getApplicationContext());
        MobclickAgent.openActivityDurationTrack(false);
        AnalyticsConfig.enableEncrypt(true);
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void finishActivitys(){
        for(Activity activity : activityList) {
            if(null != activity) {
                activity.finish();
            }
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        getJusTalkHandler().logout(null, null);
        getJusTalkHandler().destroy();
        finishActivitys();
        System.exit(0);
    }

    public boolean isLogined(){
        SharedPreferences sp = getSharedPreferences(Config.SP_SETTINGS, MODE_PRIVATE);
        return !sp.getString("user_access_token", "").equals("");
    }

    public void autoLogin(){
        User user = new User();
        user.localRead(chinessy.getApplicationContext());
        setUser(user);
    }

    public void afterLogin(User user){
        setUser(user);
        user.localSave(chinessy.getApplicationContext());
        if(user.getUserProfile().getStatus().equals(User.STATUS_AVAILABLE) || user.getUserProfile().getStatus().equals(User.STATUS_BUSY)){
            getJusTalkHandler().login(null, null);
        }
    }

    public void logout(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", chinessy.getUser().getAccessToken());
            jsonObject.put("status", User.STATUS_OFFLINE);

            InternalClient.postJson(chinessy.getApplicationContext(), "internal/tutor/status", jsonObject, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")){
                            case 10000:
//                                Toast.makeText(chinessy.getApplicationContext(), "offline", Toast.LENGTH_SHORT).show();
                                break;
                            default:
//                                Toast.makeText(chinessy.getApplicationContext(), "offline1", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setUser(new User());
        getUser().localSave(chinessy.getApplicationContext());
        getJusTalkHandler().logout(null, null);
    }

//    public static void shareReferralCode(Activity activity){
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, chinessy.getUser().getUserProfile().getShareMessage(activity));
//        sendIntent.setType("text/plain");
//        activity.startActivity(Intent.createChooser(sendIntent, activity.getTitle()));
//    }

    public User getUser() {
        if(user==null){
            autoLogin();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JusTalkHandler getJusTalkHandler() {
        return jusTalkHandler;
    }

    public Handler getChinessyHandler() {
        return mHandler;
    }

    class ChinessyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Chinessy.HANDLER_JUSTALK_LOGIN:
                    Chinessy.chinessy.getJusTalkHandler().justLogin();
//                    Toast.makeText(Chinessy.chinessy.getApplicationContext(), "just logined", Toast.LENGTH_LONG).show();
                    break;
                case Chinessy.HANDLER_CHECK_SERVICE:
                    checkOtherService();
                    break;
            }
        }

        void checkOtherService(){
            Context context = getApplicationContext();
            if(!ServiceUtil.isServiceRunning(context, TutorService.class.getName())){
                Intent intent = new Intent();
                intent.setClass(context, TutorService.class);
                context.startService(intent);
            }
            if(!ServiceUtil.isServiceRunning(context, IndependentTutorService.class.getName())){
                Intent intent = new Intent();
                intent.setClass(context, IndependentTutorService.class);
                context.startService(intent);
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        Log.d(tag, "on app create ");
        addActivity(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        activityCount--;
//        Log.d(tag, "on app paused " + activityPaused);
//        Log.d(tag, "App in background " + (activityPaused == activityCount));
//        Date now = new Date();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(activityCount==0){
//                    Log.d(tag, "In Background");
                    ifInBackground = true;
                }else{
//                    Log.d(tag, "In Foreground");
                    ifInBackground = false;
                }
                this.cancel();
            }
        }, 1000);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        activityCount++;
        if(ifInBackground){
            Chinessy.chinessy.getUser().activeUser(getApplicationContext());
        }
//        Log.d(tag, "on app resume "+ activityCount);

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }
}
