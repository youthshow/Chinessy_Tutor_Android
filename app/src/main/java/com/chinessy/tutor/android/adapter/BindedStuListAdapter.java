package com.chinessy.tutor.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.chinessy.tutor.android.R;
import com.chinessy.tutor.android.activity.BindedStuListActivity;
import com.chinessy.tutor.android.beans.getTeacherBinds;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by susan on 16/2/29.
 */
public class BindedStuListAdapter extends RecyclerView.Adapter<BindedStuListAdapter.ViewHolder> {
    private Context mContext;
    private List<getTeacherBinds.DataBean.StudentBean> mList;


    public BindedStuListAdapter(BindedStuListActivity context, List<getTeacherBinds.DataBean.StudentBean> mData) {
        super();
        this.mContext = context;
        this.mList = mData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_bindstulist, null);
        //创建一个VIewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        getTeacherBinds.DataBean.StudentBean bean = mList.get(position);
        holder.tutoritem_tv_name.setText(bean.getName());
        holder.tutoritem_tv_mins.setText(bean.getBinding_minutes() + "分钟");
        Glide.with(mContext)
                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
                .placeholder(R.mipmap.me_profilepic)
                .into(holder.tutoritem_iv_headimg);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView tutoritem_iv_headimg;
        TextView tutoritem_tv_name;
        TextView tutoritem_tv_mins;


        public ViewHolder(View itemView) {
            super(itemView);
            this.tutoritem_iv_headimg = (CircleImageView) itemView.findViewById(R.id.tutoritem_iv_headimg);
            this.tutoritem_tv_name = (TextView) itemView.findViewById(R.id.tutoritem_tv_name);
            this.tutoritem_tv_mins = (TextView) itemView.findViewById(R.id.tutoritem_tv_mins);


            //为item添加普通点击回调
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (null != mItemClickListener) {
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    private ItemClickListener mItemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    //为RecycleView 添加点击事件
    public interface ItemClickListener {
        //普通点击
        public void onItemClick(View view, int position);
    }
}
