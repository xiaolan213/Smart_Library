package com.example.xiaolan.myapplication;

import android.animation.*;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import com.example.xiaolan.myapplication.User.UserBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {
    private TextView tv_register;
    private CheckBox checkBox;
    private EditText ed_pwd, ed_username;
    private Button btn_login;
    private TextWatcher textWatcher;
    private View progress;
    private float mWidth, mHeight;
    private View mInputLayout;
    private Handler handler;
    private Runnable runnable;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {

       /* getWindow().setEnterTransition(new Explode().setDuration(2000));
        getWindow().setExitTransition(new Explode().setDuration(2000));*/

        View v = findViewById(R.id.ly_content);
        v.getBackground().setAlpha(60);
        mInputLayout = findViewById(R.id.input_layout);
        progress = findViewById(R.id.layout_progress);
        checkBox = findViewById(R.id.checkBox1);
        ed_username = findViewById(R.id.ed_loginuser);
        ed_pwd = findViewById(R.id.ed_loginpwd);
        btn_login = findViewById(R.id.btn_login);
        tv_register=findViewById(R.id.tv_register);
       // ly_reg=findViewById(R.id.ly_register);
        btn_login.setOnClickListener(this);

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag","被点了");
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                overridePendingTransition(R.anim.zoomin,R.anim.zoomout);

            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ed_pwd.getText().length() > 0 && ed_username.getText().length() > 0) {
                    btn_login.setEnabled(true);
                    btn_login.setBackgroundResource(R.drawable.button_circle);
                } else {
                    btn_login.setEnabled(false);
                    btn_login.setBackgroundResource(R.drawable.isclick);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        ed_username.addTextChangedListener(textWatcher);
        ed_pwd.addTextChangedListener(textWatcher);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    ed_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ed_pwd.setSelection(ed_pwd.getText().length());
                } else {
                    ed_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ed_pwd.setSelection(ed_pwd.getText().length());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                //System.out.println("被点了");
                btn_login.setFocusable(true);
                btn_login.setFocusableInTouchMode(true);
                btn_login.requestFocus();

                //计算控件宽高
                mWidth = btn_login.getMeasuredWidth();
                mHeight = btn_login.getMeasuredHeight();

                // 隐藏输入框
                ed_username.setVisibility(View.INVISIBLE);
                ed_pwd.setVisibility(View.INVISIBLE);
                inputAnimator(mInputLayout, mWidth, mHeight);
                String username1=ed_username.getText().toString().trim();
                String password1=ed_pwd.getText().toString().trim();
                postRequest(username1,password1);
                btn_login.setEnabled(false);
                handler = new Handler();
                handler.postDelayed(runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("status"+status);
                        if ("0".equals(status)){
                           Static.username=username1;//传入用户名
                            startActivity(new Intent(MainActivity.this, SecondActivity.class));
                            Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                            finish();
                        }else if("2".equals(status)){
                            Toast.makeText(MainActivity.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                            recovery();
                            btn_login.setEnabled(true);
                        }else {
                            Toast.makeText(MainActivity.this, "账号未注册", Toast.LENGTH_LONG).show();
                            recovery();
                            btn_login.setEnabled(true);
                        }
                    }
                }, 1000);


        }
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
        ed_username.setVisibility(View.VISIBLE);
        ed_pwd.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f, 1f);
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }

    private void postRequest(String username,String password)  {
        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url(Static.ip+"/validate")
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

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==1){
                String ReturnMessage = (String) msg.obj;
                Log.i("获取的返回信息",ReturnMessage);
                final UserBean userBean = new Gson().fromJson(ReturnMessage, UserBean.class);
                //取出json的status值
                status=userBean.getStatus();
                System.out.println(status);
            }
        }
    };
}
