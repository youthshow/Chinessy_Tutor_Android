package org.chinessy.tutor.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.chinessy.tutor.android.broadcastreceiver.BootCompletedReceiver;
import com.chinessy.tutor.android.broadcastreceiver.TimeTickReceiver;
import com.chinessy.tutor.android.service.TutorService;
import com.chinessy.tutor.android.utils.ServiceUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class IndependentTutorService extends Service {
    public static final String tag = "IndependentTutorService";
    Context mContext;

    Timer mTimer = new Timer();
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            checkOtherService();
        }
    };

    TimeTickReceiver mTimeTickReceiver = new TimeTickReceiver();
    public IndependentTutorService() {

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

        mTimer.schedule(mTimerTask, 1, 60000);
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
        mTimer.cancel();
        mTimeTickReceiver.unregisterReceiver();
    }

    void checkOtherService(){
        if(!ServiceUtil.isServiceRunning(mContext, TutorService.class.getName())){
            Intent intent = new Intent();
            intent.setClass(mContext, TutorService.class);
            IndependentTutorService.this.startService(intent);
        }
//        Date date = new Date();
//        Log.e(tag, "IndependentTutorService in checkOtherService:" + date.toString());
    }
}
