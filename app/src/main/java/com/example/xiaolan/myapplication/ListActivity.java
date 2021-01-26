package com.example.xiaolan.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.kingvcn.rfid.RFID125KAPI;
import cn.kingvcn.rfid.response.rfid125k.onGetTagResponse;
import cn.kingvcn.rfid.socket.onConnectResult;
import com.example.xiaolan.myapplication.Book.Book;
import com.example.xiaolan.myapplication.User.UserBean;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class ListActivity extends Activity {

    private LinearLayout ly_title;
    private TextView tv_book_name, tv_author, tv_status, tv_borrower;
    private boolean isConnect = false;
    boolean flag = true, flag2 = false, flag3 = false;
    private int status, status0;
    private String borrow, book_name, status1, borrower;
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
        judge();
    }

    private void judge() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnect) {
                    getTag();
                }
            }
        }, 1000);
    }

    private void getTag() {
        RFID125KAPI.getTagData(new onGetTagResponse() {
            @Override
            public void onCardNumber(String s) {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = s;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onWieGandData(String s) {
                Message msg = new Message();
                msg.what = 4;
                msg.obj = s;
                mHandler.sendMessage(msg);
            }
        });
    }


    private void init() {
        tv_author = findViewById(R.id.author);
        tv_book_name = findViewById(R.id.book_name);
        tv_borrower = findViewById(R.id.borrower);
        tv_status = findViewById(R.id.status);
        ly_title = findViewById(R.id.ly_booktitle);
        ly_title.getBackground().setAlpha(90);
        tv_book_name.setText(Static.book_name);
        //tv_author.setText(Static.author);
        count = Static.count;
        if (Static.status == 1) {
            tv_status.setText("借出");
            tv_borrower.setText(Static.borrower);
            flag2 = true;

        } else {

            tv_status.setText("在馆");
            tv_borrower.setText(Static.borrower);
        }

        // System.out.println(Static.borrower);
        while (flag) {
            linkGetWay("192.168.6.1", 101);
            flag = false;
        }

    }


    private void linkGetWay(String ip, int port) {
        RFID125KAPI.creatLink(ip, port, new onConnectResult() {
            @Override
            public void onConnectSuccess() {
                isConnect = true;
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onConnectFail() {
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        });

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d("TAG", "RFIDcg ");
                    break;
                case 2:
                    Log.d("TAG", "RFIDsb");
                    break;
                case 3:
                 /*   if (msg.obj.toString().equals(Static.xi)) {

                        if (count == 0) {
                            //书不在馆内,还回来flag=flase status=0;
                            borrow = "无";
                            status = 0;
                            update();
                            postRequest(Static.book_name);
                            count = 1;

                        } else {
                            //书在馆内,借出去flag=true status=1;
                            borrow = Static.username;
                            System.out.println(borrow);
                            status = 1;
                            update();
                            postRequest(Static.book_name);
                            count = 0;
                        }
                    }
                    } else if (msg.obj.equals(Static.hong)) {
                        System.out.println(count);
                        if (count == 0) {
                            //书不在馆内,还回来flag=flase status=0;
                            borrow = "无";
                            status = 0;
                            update();
                            postRequest(Static.book_name);
                            count = 1;

                        } else {
                            //书在馆内,借出去flag=true status=1;
                            borrow = Static.username;
                            status = 1;
                            update();
                            postRequest(Static.book_name);
                            count = 0;

                        }*/
                    break;
                case 4:
                default:
                    break;
            }
        }
    };


    private void update() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, toJson());
        Request request = new Request.Builder()
                .url(Static.ip + "/updateBook")
                .post(body)
                .build();

        new Thread(()->{
            Response response = null;
            try {
                response = Static.client.newCall(request).execute();
                final String responseData = response.body().string();
                System.out.println(responseData);
                if (response.isSuccessful()) {
                    //  System.out.println(responseData);
                    System.out.println("更新成功");
                } else {
                    System.out.println("sb");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String toJson() {
        Book book = new Book();
        book.setStatus1(status);
        book.setBook_name(Static.book_name);
        book.setBorrower(borrow);
        Gson gson = new Gson();
        return gson.toJson(book);
    }


    private void postRequest(String book_name) {
        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("book_name", book_name)
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url(Static.ip + "/idSelectBookName")
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
                        mHandler1.obtainMessage(1, response.body().string()).sendToTarget();
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

    private Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String ReturnMessage = (String) msg.obj;
                // Log.i("获取的返回信息",ReturnMessage);
                final UserBean userBean = new Gson().fromJson(ReturnMessage, UserBean.class);
                String jsonString = String.valueOf(userBean.getData());
                    System.out.println(jsonString);
            }
        }

    };
}
