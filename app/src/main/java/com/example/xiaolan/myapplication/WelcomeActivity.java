package com.example.xiaolan.myapplication;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends Activity implements View.OnClickListener {
    private int recLen = 5;//跳过倒计时提示5秒
    Timer timer = new Timer();  //定义一个计时器
    private Handler handler;
    private Runnable runnable;
    private Button btn;
    private Animator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_welcome);
        init();
        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
        /**
         * 正常情况下不点击跳过
         */
        handler = new Handler();
        handler.postDelayed(runnable =new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
                finish();
            }
        },5000);

    }

    private void init() {
        btn = findViewById(R.id.btn_in);
        btn.setOnClickListener(this);
        View view=findViewById(R.id.lg_1);
        view.getBackground().setAlpha(70);
    }

    TimerTask task= new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recLen--;
                    btn.setText("跳过"+recLen);
                    if(recLen<0){
                        timer.cancel();
                        btn.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_in:
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
                finish();
                if(runnable!=null){
                    handler.removeCallbacks(runnable);
                }
                break;
                default:
                    break;
        }
    }
}
