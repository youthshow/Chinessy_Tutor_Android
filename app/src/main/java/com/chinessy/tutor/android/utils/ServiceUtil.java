package com.chinessy.tutor.android.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinessy.tutor.android.service.TutorService;

import org.chinessy.tutor.android.service.IndependentTutorService;

import java.util.Date;
import java.util.List;

/**
 * Created by larry on 15/12/10.
 */
public class ServiceUtil {

    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static void onReceiverReceive(Context context){
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
