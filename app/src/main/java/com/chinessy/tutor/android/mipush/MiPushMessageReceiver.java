package com.chinessy.tutor.android.mipush;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCli;
import com.justalk.cloud.lemon.MtcCliConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

public class MiPushMessageReceiver extends PushMessageReceiver {
   
    @Override
    public void onReceiveMessage(Context context, MiPushMessage message) {
        MtcWakeLock.acquire(context, 180000);    	
        context.startService(new Intent(context, MtcService.class));
        if (MtcCli.Mtc_CliGetState() == MtcCliConstants.EN_MTC_CLI_STATE_LOGINED) {
        	MtcCli.Mtc_CliRefresh();
        } else {
	    	SharedPreferences sp = context.getSharedPreferences("SP", context.MODE_PRIVATE);
			String username = sp.getString("username", "");
			String password = sp.getString("password", "");
			String server = sp.getString("server", "");
			JSONObject json = new JSONObject();
	        try {
	            json.put(MtcApi.KEY_SERVER_ADDRESS, server);
	            json.put(MtcApi.KEY_PASSWORD, password);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	        
	        if (MtcApi.login(username, json) == MtcConstants.ZOK) {
	        	System.out.println("login ok");
	        }
        }
    }
    
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
       
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                List<String> arguments = message.getCommandArguments();
                String regId = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
                if (!TextUtils.isEmpty(regId)) {
                    MiPushClient.setAlias(context, "JusTalkCloudSample", null);
                    MiPush.register(context, regId);
                }
            }
        }
    }
}
