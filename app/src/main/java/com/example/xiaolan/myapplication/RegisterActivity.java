package com.example.xiaolan.myapplication;

import android.animation.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.xiaolan.myapplication.User.UserBean;
import com.google.gson.Gson;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText ed_regpwd, ed_reguser, ed_repwd1;
    private Button btn_reg;
    private TextWatcher textWatcher;
    private View progress;
    private float mWidth, mHeight;
    private View mInputLayout;
    private Handler handler;
    private Runnable runnable;
    private String status, username, password, password1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    public void init() {
        View v = findViewById(R.id.ly_content1);
        v.getBackground().setAlpha(60);
        ed_regpwd = findViewById(R.id.ed_regpwd);
        ed_repwd1 = findViewById(R.id.ed_regpwd1);
        ed_reguser = findViewById(R.id.ed_reguser);
        btn_reg = findViewById(R.id.btn_reg);
        mInputLayout = findViewById(R.id.input_reglayout);
        progress = findViewById(R.id.layout_progress1);
        btn_reg.setOnClickListener(this);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed_regpwd.getText().length() > 0 && ed_repwd1.getText().length() > 0 && ed_reguser.getText().length() > 0) {
                    btn_reg.setEnabled(true);
                    btn_reg.setBackgroundResource(R.drawable.button_circle);
                } else {
                    btn_reg.setEnabled(false);
                    btn_reg.setBackgroundResource(R.drawable.isclick);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        ed_reguser.addTextChangedListener(textWatcher);
        ed_repwd1.addTextChangedListener(textWatcher);
        ed_regpwd.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reg:
                username = ed_reguser.getText().toString().trim();
                password = ed_regpwd.getText().toString().trim();
                password1 = ed_repwd1.getText().toString().trim();
                if (password.equals(password1)) {
                    btn_reg.setFocusable(true);
                    btn_reg.setFocusableInTouchMode(true);
                    btn_reg.requestFocus();

                    //计算控件宽高
                    mWidth = btn_reg.getMeasuredWidth();
                    mHeight = btn_reg.getMeasuredHeight();

                    // 隐藏输入框
                    ed_reguser.setVisibility(View.INVISIBLE);
                    ed_regpwd.setVisibility(View.INVISIBLE);
                    ed_repwd1.setVisibility(View.INVISIBLE);
                    inputAnimator(mInputLayout, mWidth, mHeight);
                    postRequest(username, password);
                    btn_reg.setEnabled(false);
                    handler = new Handler();
                    handler.postDelayed(runnable = new Runnable() {
                        @Override
                        public void run() {
                            if ("0".equals(status)) {
                                showdialog();
                            } else if ("1".equals(status)) {
                                Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_LONG).show();
                                recovery();
                                btn_reg.setEnabled(true);
                            } else {
                                Toast.makeText(RegisterActivity.this, "注册错误", Toast.LENGTH_LONG).show();
                                recovery();
                                btn_reg.setEnabled(true);
                            }
                        }
                    }, 1500);


                } else {
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                }

        }
    }

    private void showdialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder = new AlertDialog.Builder(this).setTitle("注册成功")
                .setMessage("是否自动登陆?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //传入用户名
                        Static.username = username;
                        startActivity(new Intent(RegisterActivity.this,SecondActivity.class));
                        overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
                        finish();
                    }
                });

        builder.create().show();
    }


    /**
     * 输入框的动画效果
     *
     * @param view 控件
     * @param w    宽
     * @param h    高
     */
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(500);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    /**
     * 出现进度动画
     *
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(500);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
    }

    /**
     * 恢复初始状态
     */
    private void recovery() {
        progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        ed_reguser.setVisibility(View.VISIBLE);
        ed_regpwd.setVisibility(View.VISIBLE);
        ed_repwd1.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f, 1f);
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }

    private void postRequest(String username, String password) {
        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url(Static.ip + "/insert")
                .post(formBody)
                .build();
        //新建一个线程，用于得到服务器响应的参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    //回调
                    response = Static.client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //将服务器响应的参数response.body().string())发送到hanlder中，并更新ui
                        mHandler.obtainMessage(1, response.body().string()).sendToTarget();
                        System.out.println("cg");

                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String ReturnMessage = (String) msg.obj;
                Log.i("获取的返回信息", ReturnMessage);
                final UserBean userBean = new Gson().fromJson(ReturnMessage, UserBean.class);
                status = userBean.getStatus();
                System.out.println(status);
            }

        }
    };
}
