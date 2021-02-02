package com.floats.roundview.ui;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.floats.roundview.FileUtil;

/**
 * 悬浮窗展开状态
 */
public class RoundWindowBigView extends LinearLayout {

    private Context context;
    private ImageView iv_content;

    /**
     * 红点消息提示
     */
    private View msg;


    public RoundWindowBigView(Context context) {
        super(context);
        this.context = context;
        if (RoundView.isNearLeft) {
            LayoutInflater.from(context).inflate(FileUtil.getResIdFromFileName(context, "layout", "pop_left"), this);
        } else {
            LayoutInflater.from(context).inflate(FileUtil.getResIdFromFileName(context, "layout", "pop_right"), this);
        }
        iv_content = (ImageView) findViewById(  FileUtil.getResIdFromFileName(context, "id", "iv_content"));

        msg=findViewById(FileUtil.getResIdFromFileName(context, "id", "round_msg"));

        if(RoundView.isMsg){
            msg.setVisibility(VISIBLE);
        }else {
            msg.setVisibility(GONE);
        }

        setupViews();
    }

    private void setupViews() {
        iv_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // 只能先创建在移除，不然有问题
                        RoundView.getInstance().createSmallWindow(context);
                        RoundView.getInstance().removeBigWindow(context);
                    }
                });
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
