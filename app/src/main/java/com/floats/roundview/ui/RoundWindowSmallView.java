package com.floats.roundview.ui;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.floats.roundview.FileUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 悬浮窗常规状态
 */
public class RoundWindowSmallView extends LinearLayout {

    private static final String TAG = "RoundView";
    private Context context;

    private WindowManager windowManager;

    private WindowManager.LayoutParams mParams;

    private float mPrevX;
    private float mPrevY;
    private int mWidth,mHeight;

    private Timer mAnimationTimer;
    private AnimationTimerTask mAnimationTask;
    private Handler mHandler = new Handler();
    private int mAnimationPeriodTime = 16;

    private long mLastTouchDownTime;  //按下的时间
    private final int TOUCH_TIME_THRESHOLD = 150;  //少于150毫秒算点击事件
    private long lastClickTime;

    /**
     * 小悬浮窗对象
     */
    private View view;

    /**
     * 红点消息提示
     */
    private View msgLeft,msgRight;


    public RoundWindowSmallView(final Context context) {
        super(context);
        this.context=context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mWidth=getContext().getResources().getDisplayMetrics().widthPixels;
        mHeight = getContext().getResources().getDisplayMetrics().heightPixels;

        Point point = new Point();
        if (windowManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                windowManager.getDefaultDisplay().getRealSize(point);
                mWidth=point.x;
                //mHeight =point.y;
            }
        }

        // 获取布局文件
        LayoutInflater.from(context).inflate(FileUtil.getResIdFromFileName(context, "layout", "round_view"), this);
        view = findViewById(FileUtil.getResIdFromFileName(context, "id", "rl_content"));
        msgLeft=findViewById(FileUtil.getResIdFromFileName(context, "id", "round_msg_left"));
        msgRight=findViewById(FileUtil.getResIdFromFileName(context, "id", "round_msg_right"));

        if(RoundView.isMsg){
            if(RoundView.isNearLeft){
                msgLeft.setVisibility(VISIBLE);
            }else {
                msgRight.setVisibility(VISIBLE);
            }
        }else {
            msgLeft.setVisibility(GONE);
            msgRight.setVisibility(GONE);
        }

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:   //按下
                        mLastTouchDownTime = System.currentTimeMillis();
                        handler.removeMessages(1);
                        mPrevX = motionEvent.getRawX();
                        mPrevY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = motionEvent.getRawX() - mPrevX;
                        float deltaY = motionEvent.getRawY() - mPrevY;
                        mParams.x += deltaX;
                        mParams.y += deltaY;
                        mPrevX = motionEvent.getRawX();
                        mPrevY = motionEvent.getRawY();

                        //判断是否越界
                        if (mParams.x < 0) mParams.x = 0;
                        if (mParams.x > mWidth - view.getWidth())
                            mParams.x = mWidth - view.getWidth();
                        if (mParams.y < 0) mParams.y = 0;
                        if (mParams.y > mHeight - view.getHeight())
                            mParams.y = mHeight - view.getHeight();

                        try {
                            windowManager.updateViewLayout(view, mParams);
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:  //松开
                        if (isOnClickEvent()) {
                            //添加点击事件
                            if (isFastDoubleClick()) {
                                //防止连续点击，如果连续点击这里什么也不做
                            } else {
                                //Toast.makeText(context, "你点击了悬浮球", Toast.LENGTH_SHORT).show();
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RoundView.getInstance().createBigWindow(context);
                                        RoundView.getInstance().removeSmallWindow(context);
                                    }
                                });
                                return false;
                            }
                        }

                        //贴边
                        mAnimationTimer = new Timer();
                        mAnimationTask = new AnimationTimerTask();
                        mAnimationTimer.schedule(mAnimationTask, 0,mAnimationPeriodTime);
                        timehide();  //3s钟后隐藏
                        break;

                }

                return false;
            }
        });
    }


    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }


    //防止连续点击
    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {   //点击间隔设置大于动画执行时间，避免重复点击造成多次执行动画
            return true;
        }
        lastClickTime = time;
        return false;
    }
    protected boolean isOnClickEvent() {
        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD;
    }

    class AnimationTimerTask extends TimerTask {

        int mStepX;
        int mDestX;

        public AnimationTimerTask() {
            if (mParams.x > mWidth / 2) {
                RoundView.isNearLeft = false;
                mDestX = mWidth - view.getWidth();
                mStepX = (mWidth - mParams.x) / 10;
            } else {
                RoundView.isNearLeft = true;
                mDestX = 0;
                mStepX = -((mParams.x) / 10);
            }
        }

        @Override
        public void run() {
            if (Math.abs(mDestX - mParams.x) <= Math.abs(mStepX)) {
                mParams.x = mDestX;
            } else {
                mParams.x += mStepX;
            }
            try {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateViewPosition();
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            if (mParams.x == mDestX) {
                mAnimationTask.cancel();
                mAnimationTimer.cancel();
            }
        }


    }

    public void setVisibilityState(int state){
        if(state==View.VISIBLE){
            timehide();
        }else if(state==View.GONE){
            handler.removeMessages(1);
        }
        this.setVisibility(state);
    }

    public void updateViewPosition(){
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 显示小浮标是否添加3秒隐藏
     */
    public void timehide() {
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessageDelayed(msg, 3000);
    }

    /**
     * 小浮标停止隐藏
     */
    public void stopDelayed() {
        if (handler != null) {
            handler.removeMessages(1);
        }
    }
    /**
     * handler处理：隐藏半球
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    hidePop();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 退出动画
     *
     */
    public void hidePop(){
        Animation mAnimation = null;
        if (RoundView.isNearLeft) {
            mAnimation = AnimationUtils.loadAnimation(context,  FileUtil.getResIdFromFileName(context, "anim", "slide_out_left"));
        } else {
            mAnimation = AnimationUtils.loadAnimation(context,   FileUtil.getResIdFromFileName(context, "anim", "slide_out_right"));
        }
        view.startAnimation(mAnimation);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画执行完毕后切换视图
                // 只能先创建在移除，不然有问题
                RoundView.getInstance().createHideWindow(context);
                RoundView.getInstance().removeSmallWindow(context);
            }
        });
    }

    /**
     * 红点消息显示
     */
    public void showRoundMsg(){
        if(RoundView.isNearLeft){
            msgLeft.setVisibility(VISIBLE);
        }else {
            msgRight.setVisibility(VISIBLE);
        }
    }

    /**
     * 红点消息隐藏
     */
    public void hideRoundMsg(){
        msgLeft.setVisibility(GONE);
        msgRight.setVisibility(GONE);
    }
}
