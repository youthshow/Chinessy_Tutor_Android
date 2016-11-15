package com.chinessy.tutor.android.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.rtmp.LivePublisherActivity;

public class LiveRoomActivity extends AppCompatActivity {
    private LivePublisherActivity mPublisherFragment;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        SystemSetting();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mPublisherFragment = new LivePublisherActivity();
        transaction.replace(R.id.content_layout, mPublisherFragment);
        transaction.commit();
        findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void SystemSetting() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
