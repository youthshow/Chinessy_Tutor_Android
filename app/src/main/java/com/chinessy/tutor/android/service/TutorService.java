package com.chinessy.tutor.android.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.Config;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.activity.SplashActivity;
import com.chinessy.tutor.android.broadcastreceiver.TimeTickReceiver;
import com.chinessy.tutor.android.utils.ServiceUtil;

import org.chinessy.tutor.android.service.IndependentTutorService;

import java.util.Timer;
import java.util.TimerTask;

public class TutorService extends Service {
    public static final String tag = "TutorService";

    Context mContext;

    Timer mTimer = new Timer();
    TimerTask mTimerTask = new CheckTutorStatusTimerTask();

    TimeTickReceiver mTimeTickReceiver = new TimeTickReceiver();

    static final int NOTIFICATION_ID = 0x123;

    public TutorService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
/*
老版本 notification 写法
        Notification notification = new Notification(R.mipmap.ic_launcher, getText(R.string.app_name),
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                getText(R.string.notify_foregroundservice), pendingIntent);
        startForeground(Config.NOTIFICATION_TUTOR_ONGOING, notification);
*/
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        // builder.setContentInfo("补充内容");
        builder.setContentText(getText(R.string.notify_foregroundservice));
        builder.setContentTitle(getText(R.string.app_name));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        // builder.setPriority(Notification.PRIORITY_HIGH); //设置该通知优先级
        builder.setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_ALL);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
        // builder.setTicker("新消息");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);
        mTimer.schedule(mTimerTask, 1, 2 * 60000);

        mTimeTickReceiver.registerReceiver(mContext);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mTimer.cancel();
        mTimeTickReceiver.unregisterReceiver();
    }

    void checkOtherService() {
        if (!ServiceUtil.isServiceRunning(mContext, IndependentTutorService.class.getName())) {
            Intent intent = new Intent();
            intent.setClass(mContext, IndependentTutorService.class);
            TutorService.this.startService(intent);
        }
//        Date date = new Date();
//        Log.e(tag, "TutorService in checkOtherService:" + date.toString());
    }

    class CheckTutorStatusTimerTask extends TimerTask {
        @Override
        public void run() {
//            Toast.makeText(TutorService.this, count+"", Toast.LENGTH_SHORT).show();
//            Date date = new Date();
//            Log.d(tag, "in timertask, logined at: " + date.toString());
            checkOtherService();
            Chinessy.chinessy.mHandler.sendEmptyMessage(Chinessy.HANDLER_JUSTALK_LOGIN);
        }
    }
}
