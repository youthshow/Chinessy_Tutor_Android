package com.chinessy.tutor.android.handlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.Config;
import com.chinessy.tutor.android.clients.InternalClient;
import com.chinessy.tutor.android.mipush.MiPush;
import com.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcUe;
import com.justalk.cloud.lemon.MtcUeConstants;
import com.justalk.cloud.zmf.ZmfAudio;
import com.justalk.cloud.zmf.ZmfVideo;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by larry on 15/11/4.
 */
public class JusTalkHandler {
    Context mContext;
    BroadcastReceiver mAuthRequiredReceiver = new AuthRequiredBroadcastReceiver();

    BroadcastReceiver mLoginOkReceiver;
    BroadcastReceiver mLoginDidFailReceiver;

    BroadcastReceiver mAuthExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "auth expired", Toast.LENGTH_SHORT).show();
            MtcUe.Mtc_UeRefreshAuth();
        }
    };

    BroadcastReceiver mDidLogoutNotification;
    BroadcastReceiver mLogoutedNotification;

    public JusTalkHandler(Context context){
        mContext = context;
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.registerReceiver(mAuthExpiredReceiver, new IntentFilter(MtcUe.MtcUeAuthorizationExpiredNotification));
        localBroadcastManager.registerReceiver(mAuthRequiredReceiver, new IntentFilter(MtcUe.MtcUeAuthorizationRequireNotification));
    }

    public void login(IOnBroadCastReceived loginOkCallback, IOnBroadCastReceived loginDidFailCallback){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        mLoginOkReceiver = new LoginOkBroadcastReceiver(loginOkCallback);
        mLoginDidFailReceiver = new LoginDidFailBroadcastReceiver(loginDidFailCallback);
        localBroadcastManager.registerReceiver(mLoginOkReceiver, new IntentFilter(MtcApi.MtcLoginOkNotification));
        localBroadcastManager.registerReceiver(mLoginDidFailReceiver, new IntentFilter(MtcApi.MtcLoginDidFailNotification));

        justLogin();
    }

    public void justLogin(){
        String accessToken = Chinessy.chinessy.getUser().getAccessToken();
        if(accessToken!=null && !accessToken.equals("")){
//            MiPushClient.setUserAccount(mContext, Chinessy.chinessy.getUser().getAccountAlias(), null);
//            MiPushClient.setAlias(mContext, Chinessy.chinessy.getUser().getAccountAlias(), null);

            ZmfAudio.initialize(mContext);
            ZmfVideo.initialize(mContext);
            MtcApi.init(mContext, Config.JUSTALK_CLOUD_APPKEY);
            MtcCallDelegate.init(mContext);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(MtcApi.KEY_PASSWORD, "123");
                jsonObject.put(MtcApi.KEY_SERVER_ADDRESS, Config.JUSTALK_CLOUD_SERVER_ADDRESS);
                MtcApi.login(Chinessy.chinessy.getUser().getAccountAlias(), jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void logout(IOnBroadCastReceived didLogoutCallback, IOnBroadCastReceived logoutedCallback){
        justLogout(didLogoutCallback, logoutedCallback);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.unregisterReceiver(mLoginOkReceiver);
        localBroadcastManager.unregisterReceiver(mLoginDidFailReceiver);
    }

    public void justLogout(IOnBroadCastReceived didLogoutCallback, IOnBroadCastReceived logoutedCallback){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        mDidLogoutNotification = new DidLogoutBroadcastReceiver(didLogoutCallback);
        mLogoutedNotification = new LogoutedBroadcastReceiver(logoutedCallback);
        localBroadcastManager.registerReceiver(mDidLogoutNotification, new IntentFilter(MtcApi.MtcDidLogoutNotification));
        localBroadcastManager.registerReceiver(mLogoutedNotification, new IntentFilter(MtcApi.MtcLogoutedNotification));

        MtcApi.logout();
    }

    public void destroy(){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.unregisterReceiver(mAuthExpiredReceiver);
        localBroadcastManager.unregisterReceiver(mAuthRequiredReceiver);

        MtcApi.destroy();
        ZmfVideo.terminate();
        ZmfAudio.terminate();
    }

    class AuthRequiredBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String id;
            String nonce;
            try {
                String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                id = json.getString(MtcUeConstants.MtcUeUriKey);
                nonce = json.getString(MtcUeConstants.MtcUeAuthNonceKey);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            // 发送授权请求到客户服务器,请求中应该包括 id 和 nonce ...
            // authCode 是客户服务器返回的授权信息
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                jsonParams.put("nonce", nonce);
                InternalClient.postJson(context, "video/authorize", jsonParams, new SimpleJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            switch (response.getInt("code")) {
                                case 10000:
                                    String authCode = response.getJSONObject("data").getString("auth_code");
                                    MtcUe.Mtc_UePromptAuthCode(authCode);
                                    break;
                                default:
                                    SimpleJsonHttpResponseHandler.defaultHandler(mContext, response.getString("message"));
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
        }
    }
    class LoginOkBroadcastReceiver extends BroadcastReceiver{
        IOnBroadCastReceived mIOnBroadCastReceived;
        public LoginOkBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            MiPush.start(mContext, Config.MIPUSH_APP_ID, Config.MIPUSH_APP_KEY);

            Toast.makeText(mContext, "auth suceed", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null){
                mIOnBroadCastReceived.callBack();
            }
        }
    }
    class LoginDidFailBroadcastReceiver extends BroadcastReceiver{
        IOnBroadCastReceived mIOnBroadCastReceived;
        public LoginDidFailBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(mContext, "auth failed", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null) {
                mIOnBroadCastReceived.callBack();
            }
        }
    }
    class DidLogoutBroadcastReceiver extends BroadcastReceiver{
        IOnBroadCastReceived mIOnBroadCastReceived;
        public DidLogoutBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "did logout", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null) {
                mIOnBroadCastReceived.callBack();
            }
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            localBroadcastManager.unregisterReceiver(mDidLogoutNotification);
        }
    }
    class LogoutedBroadcastReceiver extends BroadcastReceiver {
        IOnBroadCastReceived mIOnBroadCastReceived;
        public LogoutedBroadcastReceiver(IOnBroadCastReceived iOnBroadCastReceived){
            mIOnBroadCastReceived = iOnBroadCastReceived;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            MiPush.stop(mContext);
            Toast.makeText(context, "logouted", Toast.LENGTH_SHORT).show();
            if(mIOnBroadCastReceived != null) {
                mIOnBroadCastReceived.callBack();
            }
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            localBroadcastManager.unregisterReceiver(mLogoutedNotification);
        }
    }
    public interface IOnBroadCastReceived{
        void callBack();
    }
}
