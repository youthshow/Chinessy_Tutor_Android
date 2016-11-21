package com.chinessy.tutor.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.MainActivity;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.activity.LiveRoomActivity;
import com.chinessy.tutor.android.beans.BasicBean;
import com.chinessy.tutor.android.clients.ConstValue;
import com.chinessy.tutor.android.clients.InternalClient;
import com.chinessy.tutor.android.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.tutor.android.models.User;
import com.chinessy.tutor.android.utils.LogUtils;
import com.google.gson.Gson;
import com.rey.material.app.SimpleDialog;

import cz.msebera.android.httpclient.Header;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static final int HANDLER_STATUS_CHANGE_SUCCEED = 101;
    static final int HANDLER_STATUS_CHANGE_FAILED = 102;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Activity mActivity;

    Button mBtnOnOffline;
    Button mBtnOnLive;
    ImageView mIvStaus;
    //    ProgressDialog mProgressDialog;
    Handler mHandler = new HomeFragmentHandler();
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mActivity = getActivity();

        mBtnOnOffline = (Button) rootView.findViewById(R.id.home_btn_onoffline);
        mBtnOnLive = (Button) rootView.findViewById(R.id.home_btn_onlive);
        mIvStaus = (ImageView) rootView.findViewById(R.id.home_iv_fireballoon);

        mBtnOnOffline.setOnClickListener(new BtnOnOfflineClickListener());
        mBtnOnLive.setOnClickListener(new BBtnOnLiveClickListener());
        webRequest();
        syncTutorStatus();
        return rootView;
    }

    class BBtnOnLiveClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), LiveRoomActivity.class));
        }
    }

    class BtnOnOfflineClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            User tutor = Chinessy.chinessy.getUser();
            String status = tutor.getUserProfile().getStatus();
            if (status.equals(User.STATUS_AVAILABLE) || status.equals(User.STATUS_BUSY)) {
                status = User.STATUS_OFFLINE;
//                Chinessy.chinessy.getJusTalkHandler().logout(null, new JusTalkHandler.IOnBroadCastReceived() {
//                    @Override
//                    public void callBack() {
//                        mHandler.sendEmptyMessage(HomeFragment.HANDLER_STATUS_CHANGE_SUCCEED);
//                    }
//                });
                mHandler.sendEmptyMessage(HomeFragment.HANDLER_STATUS_CHANGE_SUCCEED);
            } else if (status.equals(User.STATUS_OFFLINE)) {
                status = User.STATUS_AVAILABLE;
//                Chinessy.chinessy.getJusTalkHandler().login(new JusTalkHandler.IOnBroadCastReceived() {
//                    @Override
//                    public void callBack() {
//                        mHandler.sendEmptyMessage(HomeFragment.HANDLER_STATUS_CHANGE_SUCCEED);
//                    }
//                }, new JusTalkHandler.IOnBroadCastReceived() {
//                    @Override
//                    public void callBack() {
//                        mHandler.sendEmptyMessage(HomeFragment.HANDLER_STATUS_CHANGE_FAILED);
//                    }
//                });
                Chinessy.chinessy.getJusTalkHandler().login(null, null);
                mHandler.sendEmptyMessage(HomeFragment.HANDLER_STATUS_CHANGE_SUCCEED);
            } else {
                final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
                simpleDialog.message(R.string.user_status_invalid);
                simpleDialog.positiveAction(R.string.OK);
                simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        simpleDialog.dismiss();
                    }
                });
                simpleDialog.show();
                return;
            }
            JSONObject jsonObject = new JSONObject();
//            mProgressDialog = new ProgressDialog(mActivity);
//            mProgressDialog.setMessage(getString(R.string.Waiting));
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.show();
            try {
                jsonObject.put("access_token", tutor.getAccessToken());
                jsonObject.put("status", status);
                InternalClient.postJson(mActivity, "internal/tutor/status", jsonObject, new SimpleJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            switch (response.getInt("code")) {
                                case 10000:

                                    String newStatus = response.getJSONObject("data").getString("status");
                                    Chinessy.chinessy.getUser().getUserProfile().setStatus(newStatus, mActivity);
                                    mHandler.sendEmptyMessage(HomeFragment.HANDLER_STATUS_CHANGE_SUCCEED);
                                    break;
                                default:
                                    SimpleJsonHttpResponseHandler.defaultHandler(mActivity, response.getString("message"));
                                    mHandler.sendEmptyMessage(HomeFragment.HANDLER_STATUS_CHANGE_FAILED);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void syncTutorStatus() {
        String status = Chinessy.chinessy.getUser().getUserProfile().getStatus();
        if (status.equals(User.STATUS_AVAILABLE) || status.equals(User.STATUS_BUSY)) {
            mBtnOnOffline.setBackgroundResource(R.drawable.btn_long_red);
            mBtnOnOffline.setText(R.string.be_offline);
            mIvStaus.setImageResource(R.mipmap.fireballoon);
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        } else {
            mBtnOnOffline.setBackgroundResource(R.drawable.btn_long_main);
            mBtnOnOffline.setText(R.string.be_online);
            mIvStaus.setImageResource(R.mipmap.fireballoon_off);
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.offline_app_title);
        }
    }

    class HomeFragmentHandler extends Handler {
        int mCount = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HomeFragment.HANDLER_STATUS_CHANGE_SUCCEED:
                    mCount++;
                    break;
                case HomeFragment.HANDLER_STATUS_CHANGE_FAILED:
                    mCount--;
                    break;
            }
            if (mCount >= 2) {
                mCount = 0;
                syncTutorStatus();
//                if(mProgressDialog!=null){
////                    mProgressDialog.cancel();
//                }
            } else if (mCount == 0) {
//                if(mProgressDialog!=null){
////                    mProgressDialog.cancel();
//                }
            } else if (mCount <= -2) {
                mCount = 0;
//                if(mProgressDialog!=null){
////                    mProgressDialog.cancel();
//                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart("HomeFragment");
        syncTutorStatus();
        final String status = Chinessy.chinessy.getUser().getUserProfile().getStatus();
        Chinessy.chinessy.getUser().syncStatus(mActivity, new User.ISyncStatusCallback() {
            @Override
            public void callback(String newStatus) {
                if (!status.equals(newStatus)) {
                    syncTutorStatus();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // MobclickAgent.onPageEnd("HomeFragment");
    }


    private void webRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getStudio,
                // StringRequest stringRequest = new StringRequest(Request.Method.GET, ConstValue.BasicUrl + "getPlayUrl"+"?roomId=002",
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        LogUtils.d(ConstValue.getStudio + " :-->" + response.toString());
                        BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                        if ("true".equals(basicBean.getStatus().toString())) {

                        }
                    }


                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(ConstValue.getStudio + " :error-->" + error.toString());
            }
        })

        {
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
