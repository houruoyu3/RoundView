package com.floats.roundview.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.floats.roundview.FileUtil;

/**
 * 悬浮窗贴边状态
 */
public class RoundWindowHideView extends LinearLayout {

    private ImageView hideview;
    private Context context;
    /**
     * 红点消息提示
     */
    private View msg;

    public RoundWindowHideView(Context context) {
        super(context);
        this.context = context;
        if (RoundView.isNearLeft) {
            LayoutInflater.from(context).inflate(FileUtil.getResIdFromFileName(context, "layout", "layout_hide_float_left"), this);
        } else {
            LayoutInflater.from(context).inflate(FileUtil.getResIdFromFileName(context, "layout", "layout_hide_float_right"), this);
        }
        hideview = (ImageView) findViewById(  FileUtil.getResIdFromFileName(context, "id", "hide_float_iv"));

        msg=findViewById(FileUtil.getResIdFromFileName(context, "id", "round_msg"));

        if(RoundView.isMsg){
            msg.setVisibility(VISIBLE);
        }else {
            msg.setVisibility(GONE);
        }

        setupViews();
    }
    private void setupViews() {
        hideview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 只能先创建在移除，不然有问题
                RoundView.getInstance().createSmallWindow(context);
                RoundView.getInstance().removeHideWindow(context);
            }
        });

    }

    public void setVisibilityState(int state){
        this.setVisibility(state);
    }

    /**
     * 红点消息显示
     */
    public void showRoundMsg(){
        msg.setVisibility(VISIBLE);
    }

    /**
     * 红点消息隐藏
     */
    public void hideRoundMsg(){
        msg.setVisibility(GONE);
    }

}
