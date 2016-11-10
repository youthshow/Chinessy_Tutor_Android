package com.chinessy.tutor.android.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.chinessy.tutor.android.service.TutorService;
import com.chinessy.tutor.android.utils.ServiceUtil;

import org.chinessy.tutor.android.service.IndependentTutorService;

import java.util.Date;

/**
 * Created by larry on 15/12/10.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    final String tag = "BootCompletedReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceUtil.onReceiverReceive(context);
    }
}
