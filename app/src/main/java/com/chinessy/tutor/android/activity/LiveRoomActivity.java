package com.chinessy.tutor.android.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.beans.liveBeans;
import com.chinessy.tutor.android.clients.ConstValue;
import com.chinessy.tutor.android.rtmp.AudienceListAdapter;
import com.chinessy.tutor.android.rtmp.LiveCameraActivity;
import com.chinessy.tutor.android.rtmp.MessageAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.util.TextUtils;

public class LiveRoomActivity extends AppCompatActivity {

    private RecyclerView rvAudience;

    private ListView lvmessage;
    private List<String> messageData = new LinkedList<>();
    private MessageAdapter messageAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        SystemSetting();
        Request();
        findViewById(R.id.ivExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lvmessage = (ListView) findViewById(R.id.lvmessage);
        for (int x = 0; x < 20; x++) {
            messageData.add("Johnny: 默认聊天内容" + x);
        }
        messageAdapter = new MessageAdapter(this, messageData);
        lvmessage.setAdapter(messageAdapter);
        lvmessage.setSelection(messageData.size());


        rvAudience = (RecyclerView) findViewById(R.id.rv_audience);
        rvAudience.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvAudience.setLayoutManager(layoutManager);

        List list = new ArrayList<Integer>();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);

        AudienceListAdapter mAdapter = new AudienceListAdapter(LiveRoomActivity.this, list);
        mAdapter.setOnItemClickListener(new AudienceListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Dialog_AudienceInfo();
            }
        });
        // specify an adapter (see also next example)
        rvAudience.setAdapter(mAdapter);
    }

    /*
        private void showLiveFragment(String pushurl) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mPublisherFragment = new LivePublisherActivity();
            Bundle bundle = new Bundle();
            bundle.putString("url", pushurl);
            mPublisherFragment.setArguments(bundle);
            transaction.replace(R.id.content_layout, mPublisherFragment);
            transaction.commit();
        }
    */
    private void SystemSetting() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void Request() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getPushUrl,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        liveBeans Beans = new Gson().fromJson(response.toString(), liveBeans.class);
                        if ("true".equals(Beans.getStatus().toString())) {
                            String rtmpUrl = Beans.getData();

                            if (!TextUtils.isEmpty(rtmpUrl)) {
                                //  showLiveFragment(rtmpUrl);

                                Intent intent = new Intent(LiveRoomActivity.this, LiveCameraActivity.class);
                                intent.putExtra("rtmpUrl", rtmpUrl);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error:getPushUrl", error.getMessage(), error);
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

    public void Dialog_AudienceInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_audienceinfo, null);
        ImageView cancel = (ImageView) view.findViewById(R.id.iv_cancel);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();
    }
}
