package com.chinessy.tutor.android.models;

import com.chinessy.tutor.android.utils.DateUtil;
import com.chinessy.tutor.android.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by larry on 15/11/4.
 */
public class VideoCall {
    private int duration;
    private String status;
    private String releaseReason;
    private Date calledAt;
    private Date answeredAt;
    private String callID;
    private String callType;

    public VideoCall(JSONObject jsonObject){
        try {
            JSONObject fieldJson = jsonObject.getJSONObject("video_call").getJSONObject("fields");
            setDuration(fieldJson.getInt("duration"));
            setStatus(fieldJson.getString("status"));
            setReleaseReason(fieldJson.getString("release_reason"));
            setCalledAt(DateUtil.string2Datetime(fieldJson.getString("called_at")));
            setAnsweredAt(DateUtil.string2Datetime(fieldJson.getString("answered_at")));
            setCallID(fieldJson.getString("call_id"));
            setCallType(fieldJson.getString("call_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Date getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Date answeredAt) {
        this.answeredAt = answeredAt;
    }

    public Date getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(Date calledAt) {
        this.calledAt = calledAt;
    }

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getReleaseReason() {
        return releaseReason;
    }

    public void setReleaseReason(String releaseReason) {
        this.releaseReason = releaseReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
