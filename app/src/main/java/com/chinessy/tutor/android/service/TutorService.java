package com.chinessy.tutor.android.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.Config;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.SplashActivity;
import com.chinessy.tutor.android.broadcastreceiver.BootCompletedReceiver;
import com.chinessy.tutor.android.broadcastreceiver.TimeTickReceiver;
import com.chinessy.tutor.android.utils.ServiceUtil;

import org.chinessy.tutor.android.service.IndependentTutorService;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TutorService extends Service {
    public static final String tag = "TutorService";

    Context mContext;

    Timer mTimer = new Timer();
    TimerTask mTimerTask = new CheckTutorStatusTimerTask();

    TimeTickReceiver mTimeTickReceiver = new TimeTickReceiver();
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

        Notification notification = new Notification(R.mipmap.ic_launcher, getText(R.string.app_name),
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name),
                getText(R.string.notify_foregroundservice), pendingIntent);
        startForeground(Config.NOTIFICATION_TUTOR_ONGOING, notification);

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

    void checkOtherService(){
        if(!ServiceUtil.isServiceRunning(mContext, IndependentTutorService.class.getName())){
            Intent intent = new Intent();
            intent.setClass(mContext, IndependentTutorService.class);
            TutorService.this.startService(intent);
        }
//        Date date = new Date();
//        Log.e(tag, "TutorService in checkOtherService:" + date.toString());
    }

    class CheckTutorStatusTimerTask extends TimerTask{
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
