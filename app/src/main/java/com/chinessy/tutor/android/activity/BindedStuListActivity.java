package com.chinessy.tutor.android.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.adapter.BindedStuListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BindedStuListActivity extends AppCompatActivity {
    private RecyclerView mRv_bindedstulists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binded_stu_list);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setTitle(R.string.one2one_binded_stu_list);
        actionBar.setElevation(0f);
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置back按钮是否可见

        mRv_bindedstulists = (RecyclerView) findViewById(R.id.rv_bindedstulists);
        mRv_bindedstulists.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv_bindedstulists.setLayoutManager(layoutManager);

        List list = new ArrayList<Integer>();
        list.add(1);
        list.add(1);

        BindedStuListAdapter mAdapter = new BindedStuListAdapter(BindedStuListActivity.this, list);
        mAdapter.setOnItemClickListener(new BindedStuListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(BindedStuListActivity.this, "跳转老师详情", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
