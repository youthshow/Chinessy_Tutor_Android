package com.chinessy.tutor.android.rtmp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chinanetcenter.StreamPusher.sdk.SPConfig;
import com.chinanetcenter.StreamPusher.sdk.SPManager;
import com.chinanetcenter.StreamPusher.sdk.SPSurfaceView;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.rtmp.adapter.SettingsAdapter;
import com.chinessy.tutor.android.rtmp.object.SettingItem;
import com.chinessy.tutor.android.rtmp.utils.DialogUtils;
import com.chinessy.tutor.android.rtmp.utils.SettingsPanelViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PushStreamActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "PushStreamActivity";
    private SPSurfaceView mPreviewView;
    private TextView mRtmpUrlTv;
    private TextView mInfoTv;
    private SPConfig mSPConfig = null;
    private ImageButton btn_record;
    private ImageButton mFlashImageBtn = null;
    private ImageButton mMuteImageBtn = null;
    private SettingsPanelViewUtil mSettingPanelUtil;
    private Handler mErrorHandler;
    private Handler mStateHandler;

    private int mPushSpeed = 0;
    private int mVideoBitrate = 0;
    private int mFrameRate = 0;
    private int mFrameEncodeRate = 0;

    private List<SettingItem> mSettingItems = new ArrayList<SettingItem>() {{
        add(new SettingItem(R.id.setting_set_rtmpurl, "更新url", 0, true));
        add(new SettingItem(R.id.setting_set_camera_focus, "聚焦模式", 0, true));
//		add(new SettingItem(R.id.setting_set_audio_loop,"耳返",0, true));
        add(new SettingItem(R.id.setting_set_bitrate, "码率调节", 0, true));
//		add(new SettingItem(R.id.setting_set_encode_mode,"编码模式",0, true));
        add(new SettingItem(R.id.setting_set_fps, "帧率调节", 0, true));
        add(new SettingItem(R.id.setting_set_auto_bitrate, "自动码率", 0, true));
        add(new SettingItem(R.id.setting_set_flip, "图像倒转", 0, false));
    }};
    private static final String RTMP_URL = "";
    private int mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean mIsUserPushing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_stream);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initHandle();
        initLayout();

        // init
        initFromIntent();
        initLayoutState();
        SPManager.init(this, mSPConfig);
        checkAndRequestPermission();
        showRtmpUrl();
        Log.i(TAG, "onCreate -- ");
    }

    private void initHandle() {
        mErrorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null && msg.obj != null) {
                    String content = "(" + msg.what + ") " + msg.obj.toString();
                    Toast.makeText(PushStreamActivity.this, content, Toast.LENGTH_SHORT).show();
                }
                switch (msg.what) {
                    case SPManager.ERROR_PUSH_INIT_FAILED:
                        resetPushState();
                        if (mRtmpUrlTv == null || !mRtmpUrlTv.getText().toString().startsWith("rtmp://")) {
                            DialogUtils.showRtmpUrlInputDialog(PushStreamActivity.this,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                                EditText et = (EditText) ((AlertDialog) dialog)
                                                        .findViewById(android.R.id.edit);
                                                if (et != null) {
                                                    if (SPManager.setConfig(SPManager.getConfig().setRtmpUrl(et.getText().toString()))) {
                                                        mRtmpUrlTv.setText(et.getText());
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }

                        break;
                    case SPManager.ERROR_AUTH_FAILED:
                    case SPManager.ERROR_AUTHORIZING:
                    case SPManager.ERROR_PUSH_DISCONN:
                        resetPushState();
                        break;
                    default:
                        break;
                }
            }
        };
        mStateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null && msg.obj != null) {
                    switch (msg.what) {
                        case SPManager.STATE_PUSH_SPEED:
                            mPushSpeed = Integer.parseInt((String) msg.obj);
                            showPushInfo();
                            break;
                        case SPManager.STATE_VIDEO_BITRATE:
                            mVideoBitrate = Integer.parseInt((String) msg.obj);
                            showPushInfo();
                            break;
                        case SPManager.STATE_FRAME_RATE:
                            mFrameRate = Integer.parseInt((String) msg.obj);
                            showPushInfo();
                            break;
                        case SPManager.STATE_ENCODE_FRAME_RATE:
                            mFrameEncodeRate = Integer.parseInt((String) msg.obj);
                            showPushInfo();
                            break;
                        case SPManager.STATE_CAMERA_OPEN_SUCCESS:
                            SPManager.PushState state = SPManager.getPushState();
                            mFlashImageBtn.setSelected(SPManager.getPushState().isFlashing);
                            setButtonEnabled(mFlashImageBtn, SPManager.getPushState().isSupportFlash);
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        SPManager.setOnErrorListener(new SPManager.OnErrorListener() {
            @Override
            public void onError(int what, String extra) {
                mErrorHandler.obtainMessage(what, extra).sendToTarget();
            }
        });
        SPManager.setOnStateListener(new SPManager.OnStateListener() {
            @Override
            public void onState(int what, String extra) {
                mStateHandler.obtainMessage(what, extra).sendToTarget();
            }
        });
    }

    private void initFromIntent() {
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("rtmp", null);
        mCurrentCameraId = bundle.getInt("camera", -1);
        int encoderState = bundle.getInt("encoder", -1);
        int frameRate = bundle.getInt("frame_rate");
        int bitrate = bundle.getInt("bitrate");
        mVideoBitrate = bitrate;
        SPManager.VideoResolution videoResolution = (SPManager.VideoResolution) bundle.getSerializable("video_resolution");
        String appId = bundle.getString("appId");
        String authKey = bundle.getString("authKey");
        boolean hasVideo = bundle.getBoolean("has_video");
        boolean hasAudio = bundle.getBoolean("has_audio");

        int screenOrientation = bundle.getInt("screenOrientation");
        int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        switch (screenOrientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;

            default:
                break;
        }
        setRequestedOrientation(requestedOrientation);
        if (TextUtils.isEmpty(url)) {
            url = RTMP_URL;
        }

        mSPConfig = SPManager.getConfig();
        mSPConfig.setRtmpUrl(url);
        mSPConfig.setSurfaceView(mPreviewView);
        mSPConfig.setCameraId(mCurrentCameraId);
        mSPConfig.setEncoderMode(encoderState);
        mSPConfig.setMinFps(frameRate);
        mSPConfig.setVideoBitrate(bitrate);
        mSPConfig.setVideoResolution(videoResolution);
        mSPConfig.setAppIdAndAuthKey(appId, authKey);
        mSPConfig.setHasVideo(hasVideo);
        mSPConfig.setHasAudio(hasAudio);
        // other config
        HashMap<String, String> otherConfig = (HashMap<String, String>) bundle.get("other_config");
        if (otherConfig != null) {
            // get other config
        }
    }

    private void initLayout() {
        mPreviewView = (SPSurfaceView) findViewById(R.id.preview);
        mInfoTv = (TextView) findViewById(R.id.tv_info);

        btn_record = (ImageButton) findViewById(R.id.btn_record);
        btn_record.setOnClickListener(this);

        findViewById(R.id.btn_switch).setOnClickListener(this);

        mFlashImageBtn = (ImageButton) findViewById(R.id.btn_flash);
        mFlashImageBtn.setOnClickListener(this);

        mMuteImageBtn = (ImageButton) findViewById(R.id.btn_mute);
        mMuteImageBtn.setOnClickListener(this);

        findViewById(R.id.btn_beauty).setOnClickListener(this);
        findViewById(R.id.btn_setting).setOnClickListener(this);
        mSettingPanelUtil = new SettingsPanelViewUtil(this, new SettingsAdapter(this, mSettingItems), this);
    }

    private void initLayoutState() {
        setButtonEnabled(mFlashImageBtn, false);
    }

    protected void resetPushState() {
        SPManager.PushState state = SPManager.getPushState();
        SPConfig config = SPManager.getConfig();

        mIsUserPushing = state.isPushing;
        btn_record.setSelected(mIsUserPushing);
        mCurrentCameraId = config.getCameraId();

        setButtonEnabled(mFlashImageBtn, state.isSupportFlash);
        mFlashImageBtn.setSelected(state.isFlashing);
        mMuteImageBtn.setSelected(state.isMute);

    }

    private void showRtmpUrl() {
        mRtmpUrlTv = (TextView) findViewById(R.id.tv_rtmpurl);
        mRtmpUrlTv.setText(getIntent().getExtras().getString("rtmp", null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume -- ");
        SPManager.onResume();
        if (mIsUserPushing) {
            SPManager.startPushStream();
        }
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.btn_record:
                mIsUserPushing = !mIsUserPushing;
                boolean mIsSuccess = false;
                if (mIsUserPushing) {
                    mIsSuccess = SPManager.startPushStream();
                } else {
                    mIsSuccess = SPManager.stopPushStream();
                }
                if (!mIsSuccess) {
                    mIsUserPushing = !mIsUserPushing;
                }
                btn_record.setSelected(mIsUserPushing);
                break;
            case R.id.btn_switch:
                if (SPManager.switchCamera()) {
                    mCurrentCameraId = mCurrentCameraId == 0 ? 1 : 0;
                    mFlashImageBtn.setSelected(false);
                    setButtonEnabled(mFlashImageBtn, false);
                }
                break;
            case R.id.btn_flash:
                if (SPManager.flashCamera(v.isSelected() ? SPManager.SWITCH_OFF : SPManager.SWITCH_ON)) {
                    v.setSelected(!v.isSelected());
                }
                break;
            case R.id.btn_mute:
                if (SPManager.muteMic(v.isSelected() ? SPManager.SWITCH_OFF : SPManager.SWITCH_ON)) {
                    v.setSelected(!v.isSelected());
                }
                break;
            case R.id.btn_beauty:
                String filterNames[] = new String[SPManager.FILTER_TYPE_ALL.length];
                for (int i = 0; i < SPManager.FILTER_TYPE_ALL.length; i++) {
                    filterNames[i] = SPManager.FILTER_TYPE_ALL[i].toString();
                }
                DialogUtils.showSingleChoiceDialog(this, "请选择美颜模式", filterNames, SPManager.getPushState().filter.ordinal(), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (SPManager.switchFilter(SPManager.FilterType.values()[which])) {
                            v.setSelected(which != 0);
                            Toast.makeText(PushStreamActivity.this, SPManager.FILTER_TYPE_ALL[which].toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;
            case R.id.btn_setting:
                if (mSettingPanelUtil.isShowing()) {
                    mSettingPanelUtil.dismiss();
                } else {
                    mSettingPanelUtil.show();
                }
            default:
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        mFlashImageBtn.setSelected(false);
        super.onPause();
        Log.i(TAG, "onPause -- ");
        if (mIsUserPushing) {
            SPManager.stopPushStream();
        }
        SPManager.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy -- ");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void setButtonEnabled(ImageButton button, boolean enabled) {
        if (button == null)
            return;
        if (enabled) {
            button.setEnabled(true);
            button.clearColorFilter();
        } else {
            button.setEnabled(false);
            button.setColorFilter(0xAA000000);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SettingItem item = mSettingItems.get(position);
        if (item == null) return;
        SPConfig config = SPManager.getConfig();
        SPManager.PushState state = SPManager.getPushState();
        switch (item.settingId) {
            case R.id.setting_set_rtmpurl:
                if (state.isPushing) {
                    Toast.makeText(PushStreamActivity.this, " 该参数只能在非推流状态下设置", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog dialog = DialogUtils.showRtmpUrlInputDialog(PushStreamActivity.this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            EditText et = (EditText) ((AlertDialog) dialog).findViewById(android.R.id.edit);
                            if (et != null) {
                                if (SPManager.setConfig(SPManager.getConfig().setRtmpUrl(et.getText().toString()))) {
                                    mRtmpUrlTv.setText(et.getText());
                                } else {
                                    Toast.makeText(PushStreamActivity.this, "正在推流，无法修改参数", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
                EditText et = (EditText) dialog.findViewById(android.R.id.edit);
                et.setText(config.getRtmpUrl());
                break;
            case R.id.setting_set_camera_focus:
                DialogUtils.showSingleChoiceDialog(this, "请选择聚焦模式", new String[]{"自动聚焦", "手动聚焦"}, SPManager.getConfig().isCameraManualFocusMode() ? 1 : 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPManager.cameraManualFocusMode(which == 1 ? SPManager.SWITCH_ON : SPManager.SWITCH_OFF);

                    }
                });
                break;
            case R.id.setting_set_audio_loop:
                DialogUtils.showSingleChoiceDialog(this, "请选择耳返模式", new String[]{"关闭", "打开"}, SPManager.getPushState().audioLoopActive ? 1 : 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPManager.switchAudioLoop(which == 1 ? SPManager.SWITCH_ON : SPManager.SWITCH_OFF);

                    }
                });
                break;
            case R.id.setting_set_bitrate:
                if (state.isPushing) {
                    Toast.makeText(PushStreamActivity.this, " 该参数只能在非推流状态下设置", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder promptSb = new StringBuilder();
                promptSb.append("码率范围:\n360P ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_360P.getMinBitrate() / 1024);
                promptSb.append("k -- ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_360P.getMaxBitrate() / 1024);
                promptSb.append("K\n480P ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_480P.getMinBitrate() / 1024);
                promptSb.append("k -- ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_480P.getMaxBitrate() / 1024);
                promptSb.append("K\n540P ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_540P.getMinBitrate() / 1024);
                promptSb.append("k -- ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_540P.getMaxBitrate() / 1024);
                promptSb.append("k\n720P ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_720P.getMinBitrate() / 1024);
                promptSb.append("k -- ");
                promptSb.append(SPManager.VideoResolution.VIDEO_RESOLUTION_720P.getMaxBitrate() / 1024);
                promptSb.append("k");
                DialogUtils.showSingleInputNumberDialog(this, "请输入码率", promptSb.toString(), "", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            EditText et = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_text);
                            String numString = et.getText().toString();
                            int bitrate = TextUtils.isEmpty(numString) ? 0 : Integer.parseInt(numString) * 1024;
                            SPManager.setConfig(SPManager.getConfig().setVideoBitrate(bitrate));
                        }
                    }
                });
                break;
            case R.id.setting_set_encode_mode:
                if (state.isPushing) {
                    Toast.makeText(PushStreamActivity.this, " 该参数只能在非推流状态下设置", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogUtils.showSingleChoiceDialog(this, "请选择编码模式", new String[]{"硬编", "软编"}, config.getEncoderMode(), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPManager.setConfig(SPManager.getConfig().setEncoderMode(which));

                    }
                });
                break;
            case R.id.setting_set_fps:
                if (state.isPushing) {
                    Toast.makeText(PushStreamActivity.this, " 该参数只能在非推流状态下设置", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogUtils.showSingleInputNumberDialog(this, "请输入帧率", "帧率(15~30)", "", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            EditText et = (EditText) ((AlertDialog) dialog).findViewById(R.id.edit_text);
                            String numString = et.getText().toString();
                            SPManager.setConfig(SPManager.getConfig().setMinFps(TextUtils.isEmpty(numString) ? 0 : Integer.parseInt(numString)));
                        }
                    }
                });
                break;
            case R.id.setting_set_flip:
                SPManager.flipCamera();
                break;
            case R.id.setting_set_auto_bitrate:
                if (state.isPushing) {
                    Toast.makeText(PushStreamActivity.this, " 该参数只能在非推流状态下设置", Toast.LENGTH_SHORT).show();
                    return;
                }
                final SPConfig spConfig = SPManager.getConfig();
                DialogUtils.showSingleChoiceDialog(this, "请选择聚码率自适应状态", new String[]{"关闭", "打开"}, spConfig.isAutoBitrate() ? 1 : 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPManager.setConfig(spConfig.setAutoBitrate(which == 1));
                    }
                });
                break;

            default:
                break;
        }

    }

    private void showPushInfo() {
        String content = "speed: " + mPushSpeed + "KB/S"
                + "\nbitrate :" + mVideoBitrate / 1024 + "k"
                + "\nencode fps:" + mFrameEncodeRate
                + "\npush fps:" + mFrameRate;
        mInfoTv.setText(content);
    }
}
