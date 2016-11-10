package com.chinessy.tutor.android.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinessy.tutor.android.service.TutorService;
import com.chinessy.tutor.android.utils.ServiceUtil;

import org.chinessy.tutor.android.service.IndependentTutorService;

import java.util.Date;

/**
 * Created by larry on 15/12/10.
 */
public class SmsReceiver extends BroadcastReceiver{
    final String tag = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceUtil.onReceiverReceive(context);
    }
}
