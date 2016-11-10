package com.chinessy.tutor.android.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinessy.tutor.android.utils.ServiceUtil;

/**
 * Created by larry on 15/12/10.
 */
public class NetworkStatusReceiver extends BroadcastReceiver{
    final String tag = "NetworkStatusReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e(tag, "@@@@@@---message-received---@@@@@@");
        ServiceUtil.onReceiverReceive(context);
    }
}
