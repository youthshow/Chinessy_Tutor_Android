package com.chinessy.tutor.android.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinessy.tutor.android.R;

public class AppointmentActivity extends AppCompatActivity {
    private LinearLayout mLlChososeArea;
    private TextView mTvDeenAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mTvDeenAgree = (TextView) findViewById(R.id.tv_been_agree);
        mLlChososeArea = (LinearLayout) findViewById(R.id.ll_choose_area);
        findViewById(R.id.btn_disagree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo 点击不同意,应该提交消失
            }
        });
        findViewById(R.id.btn_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLlChososeArea.setVisibility(View.GONE);
                mTvDeenAgree.setText("已同意!");
            }
        });
        findViewById(R.id.btn_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLlChososeArea.setVisibility(View.GONE);
                mTvDeenAgree.setText("已拒绝!");
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setTitle(R.string.one2one_appointment);
        actionBar.setElevation(0f);
        actionBar.setDisplayHomeAsUpEnabled(true);// 设置back按钮是否可见
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
