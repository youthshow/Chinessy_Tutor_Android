package com.chinessy.tutor.android.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.beans.liveBeans;
import com.chinessy.tutor.android.clients.ConstValue;
import com.chinessy.tutor.android.rtmp.LivePublisherActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.util.TextUtils;

public class LiveRoomActivity extends AppCompatActivity {
    private LivePublisherActivity mPublisherFragment;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        SystemSetting();
        Request();
        findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showLiveFragment(String pushurl) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mPublisherFragment = new LivePublisherActivity();
        Bundle bundle = new Bundle();
        bundle.putString("url", pushurl);
        mPublisherFragment.setArguments(bundle);
        transaction.replace(R.id.content_layout, mPublisherFragment);
        transaction.commit();
    }

    private void SystemSetting() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void Request() {
        Log.d("VolleyPostPost", "VolleyPostPost -> ");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getPushUrl,
                // StringRequest stringRequest = new StringRequest(Request.Method.GET, ConstValue.BasicUrl + "getPlayUrl"+"?roomId=002",
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("=============", "response -> " + response);
                        //Toast.makeText()

                        liveBeans Beans = new Gson().fromJson(response.toString(), liveBeans.class);
                        if ("true".equals(Beans.getStatus().toString())) {
                            String rtmpUrl = Beans.getData();

                            if (!TextUtils.isEmpty(rtmpUrl)) {
                                showLiveFragment(rtmpUrl);
                            }
                            Log.d("=============", "playUrl -> " + rtmpUrl);

                            //    if (!TextUtils.isEmpty(playUrl)) {
                            Log.d("-----------", "playUrl" + rtmpUrl);

                              /*  if (mVideoPlay) {
                                    if (mPlayType == TXLivePlayer.PLAY_TYPE_VOD_FLV || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_HLS || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_MP4) {
                                        if (mVideoPause) {
                                            mLivePlayer.resume();
                                            // mBtnPlay.setBackgroundResource(R.drawable.play_pause);
                                        } else {
                                            mLivePlayer.pause();
                                            //  mBtnPlay.setBackgroundResource(R.drawable.play_start);
                                        }
                                        mVideoPause = !mVideoPause;

                                    } else {
                                        stopPlayRtmp();
                                        mVideoPlay = !mVideoPlay;
                                    }

                                } else {
                                    if (startPlayRtmp()) {
                                        mVideoPlay = !mVideoPlay;
                                    }
                                }
*/
                            //   }
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyPostPost", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map getParams() {
                //在这里设置需要post的参数
                Map map = new HashMap();
                map.put("roomId", "002");

                return map;
            }
        };

        Chinessy.requestQueue.add(stringRequest);
    }
}
