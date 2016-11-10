package com.chinessy.tutor.android.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

import com.chinessy.tutor.android.service.TutorService;
import com.chinessy.tutor.android.utils.ServiceUtil;

import org.chinessy.tutor.android.service.IndependentTutorService;

import java.util.Date;

/**
 * Created by larry on 15/12/10.
 */
public class TimeTickReceiver extends BroadcastReceiver{
    final String tag = "TimeTickReceiver";
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceUtil.onReceiverReceive(context);
    }

    public void registerReceiver(Context context){
        mContext = context;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        mContext.registerReceiver(this, intentFilter);
    }

    public void unregisterReceiver(){
        mContext.unregisterReceiver(this);
    }
}
