package com.chinessy.tutor.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinessy.tutor.android.Chinessy;
import com.chinessy.tutor.android.FAQActivity;
import com.chinessy.tutor.android.GuideActivity;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.clients.InternalClient;
import com.chinessy.tutor.android.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.tutor.android.models.User;
import com.rey.material.app.SimpleDialog;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Activity mActivity;

    RelativeLayout mRlEmailSupport;
    RelativeLayout mRlAbout;
    RelativeLayout mRlPersonalInfo;
    RelativeLayout mRlFAQ;
    Button mBtnLogout;
    TextView mTvName;
    TextView mTvInfo;
    TextView mTvScore;
    TextView mTvOutstandingCommissions;
    TextView mTvClosedCommissions;
    TextView mTvServedTime;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyFragment() {
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

    class EmailSupportOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");

            //调用系统发送邮件
            intent.putExtra(Intent.EXTRA_EMAIL, "help@chinessy.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "I need help using Chinessy");
            intent.putExtra(Intent.EXTRA_TEXT, "User Name:" + Chinessy.chinessy.getUser().getUserProfile().getName());
            mActivity.startActivity(intent);
        }
    }
    class AboutOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

        }
    }

    class FAQOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, FAQActivity.class);
            mActivity.startActivity(intent);
        }
    }

    class BtnLogoutClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
            simpleDialog.title(R.string.Logout);
            simpleDialog.message(R.string.dialog_logout_message);
            simpleDialog.positiveAction(R.string.OK);
            simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleDialog.cancel();

                    Intent intent = new Intent();
                    intent.setClass(mActivity, GuideActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                    Chinessy.chinessy.logout();
                }
            });
            simpleDialog.negativeAction(R.string.Cancel);
            simpleDialog.negativeActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleDialog.cancel();
                }
            });
            simpleDialog.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        mActivity = getActivity();

        mRlEmailSupport = (RelativeLayout)rootView.findViewById(R.id.my_rl_emailsupport);
        mRlAbout = (RelativeLayout)rootView.findViewById(R.id.my_rl_about);
        mRlFAQ = (RelativeLayout)rootView.findViewById(R.id.my_rl_faq);
        mTvName = (TextView)rootView.findViewById(R.id.my_tv_name);
        mRlPersonalInfo = (RelativeLayout)rootView.findViewById(R.id.my_rl_personalinfo);
        mTvInfo = (TextView)rootView.findViewById(R.id.my_tv_info);
        mTvScore = (TextView)rootView.findViewById(R.id.my_tv_score);
        mTvOutstandingCommissions = (TextView)rootView.findViewById(R.id.my_tv_outstandingcommissions);
        mTvClosedCommissions = (TextView)rootView.findViewById(R.id.my_tv_closedcommissions);
        mTvServedTime = (TextView)rootView.findViewById(R.id.my_tv_servedtime);

        mRlEmailSupport.setOnClickListener(new EmailSupportOnClickListener());
        mRlAbout.setOnClickListener(new AboutOnClickListener());
        mRlFAQ.setOnClickListener(new FAQOnClickListener());

        mBtnLogout = (Button)rootView.findViewById(R.id.my_btn_logout);
        mBtnLogout.setOnClickListener(new BtnLogoutClickListener());

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    void refreshContent(){
        String name = Chinessy.chinessy.getUser().getUserProfile().getName();
        name = name.equals("") ? Chinessy.chinessy.getUser().getEmail() : name;
        mTvName.setText(name);
        mTvScore.setText(Chinessy.chinessy.getUser().getUserProfile().getScore() + "");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            InternalClient.postJson(mActivity, "internal/tutor/get_served_info", jsonObject, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        switch (response.getInt("code")){
                            case 10000:
                                User user = Chinessy.chinessy.getUser();
                                user.setServedMinutes(response);
                                long totalMinutes = user.getPaiedMinutes() + user.getUnpaiedMinutes();
                                mTvInfo.setText(getString(R.string.referral_code)+": " + user.getUserProfile().getReferralCode());
                                int hours = (int)Math.floor(totalMinutes/60.0);
                                int minutes = (int)totalMinutes%60;
                                mTvServedTime.setText(hours+getString(R.string.hours)+minutes+getString(R.string.mins));
                                mTvOutstandingCommissions.setText(user.getOutstandingCommissions() + getString(R.string.yuan));
                                mTvClosedCommissions.setText(user.getClosedCommissions() + getString(R.string.yuan));
                                break;
                            default:
                                SimpleJsonHttpResponseHandler.defaultHandler(mActivity, response.getString("message"));
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyFragment");
        refreshContent();
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MyFragment");
    }
}
