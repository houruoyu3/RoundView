package com.floats.roundview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.floats.roundview.ui.RoundView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.showbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoundView.getInstance().showRoundView(MainActivity.this);

            }
        });

        findViewById(R.id.hidebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoundView.getInstance().hideRoundView(MainActivity.this);
            }
        });

        findViewById(R.id.closebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoundView.getInstance().closeRoundView(MainActivity.this);
            }
        });

        findViewById(R.id.showMsgbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoundView.getInstance().showRoundMsg();
            }
        });
        findViewById(R.id.hideMsgbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoundView.getInstance().hideRoundMsg();
            }
        });
        findViewById(R.id.exitbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }
}
