package com.chinessy.tutor.android.beans;

import java.util.List;

/**
 * Created by susan on 2016/11/21.
 */

public class getTeacherBinds {


    /**
     * data : {"student":[{"user_id":"134","head_img_key":"","name":"lingeng","binding_minutes":"15"},{"user_id":"311","head_img_key":"","name":"好好爱自己","binding_minutes":"10"}]}
     * msg : 获取成功！老师端一对一绑定信息列表
     * status : true
     */

    private DataBean data;
    private String msg;
    private String status;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class DataBean {
        private List<StudentBean> student;

        public List<StudentBean> getStudent() {
            return student;
        }

        public void setStudent(List<StudentBean> student) {
            this.student = student;
        }

        public static class StudentBean {
            /**
             * user_id : 134
             * head_img_key :
             * name : lingeng
             * binding_minutes : 15
             */

            private String user_id;
            private String head_img_key;
            private String name;
            private String binding_minutes;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getHead_img_key() {
                return head_img_key;
            }

            public void setHead_img_key(String head_img_key) {
                this.head_img_key = head_img_key;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBinding_minutes() {
                return binding_minutes;
            }

            public void setBinding_minutes(String binding_minutes) {
                this.binding_minutes = binding_minutes;
            }
        }
    }
}
