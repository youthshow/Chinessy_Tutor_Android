package com.chinessy.tutor.android.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.adapter.BindedStuListAdapter;
import com.chinessy.tutor.android.beans.BasicBean;
import com.chinessy.tutor.android.beans.getTeacherBinds;
import com.chinessy.tutor.android.clients.ConstValue;
import com.chinessy.tutor.android.utils.LogUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindedStuListActivity extends AppCompatActivity {
    private RecyclerView mRv_bindedstulists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binded_stu_list);

        webRequest();
        SystemSetting();

        mRv_bindedstulists = (RecyclerView) findViewById(R.id.rv_bindedstulists);
        mRv_bindedstulists.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv_bindedstulists.setLayoutManager(layoutManager);


    }

    private void SystemSetting() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setTitle(R.string.one2one_binded_stu_list);
        actionBar.setElevation(0f);
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置back按钮是否可见
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void webRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getTeacherBinds,
                // StringRequest stringRequest = new StringRequest(Request.Method.GET, ConstValue.BasicUrl + "getPlayUrl"+"?roomId=002",
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        LogUtils.d(ConstValue.getTeacherBinds + " :-->" + response.toString());

                        BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                        if ("true".equals(basicBean.getStatus().toString())) {
                            getTeacherBinds getTeacherBindBeans = new Gson().fromJson(response.toString(), getTeacherBinds.class);
                            List<getTeacherBinds.DataBean.StudentBean> student = getTeacherBindBeans.getData().getStudent();
                            BindedStuListAdapter mAdapter = new BindedStuListAdapter(BindedStuListActivity.this, student);
                            mAdapter.setOnItemClickListener(new BindedStuListAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
//                Intent intent = new Intent();
//                intent.setClass(mActivity, TutorActivity.class);
//                intent.putExtra("tutor", tutor);
//                intent.putExtra("position", position);
//                TutorsFragment.this.startActivityForResult(intent, Config.RC_MAIN_TO_TUTOR);
                                }
                            });
                            // specify an adapter (see also next example)
                            mRv_bindedstulists.setAdapter(mAdapter);

                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(ConstValue.getTeacherBinds + " :error-->" + error.toString());
            }
        }) {
            @Override
            protected Map getParams() {
                //在这里设置需要post的参数
                Map map = new HashMap();
                map.put("userId", Chinessy.chinessy.getUser().getId());
                return map;
            }
        };

        Chinessy.requestQueue.add(stringRequest);
    }
}
