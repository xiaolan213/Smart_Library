package com.example.xiaolan.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.*;
import com.example.xiaolan.myapplication.User.UserBean;
import com.google.gson.Gson;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToBorrow extends Activity {

    private LinearLayout ly_content;
    private ListView listView;
    private String book_name;
    MyItemAdater adapter;
    Handler handler;

    private SimpleAdapter simplead;
    List<Map<String,String>> arrayList = new ArrayList<Map<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_borrow);
        overridePendingTransition(R.anim.in_from_right,R.anim.out_from_left);
        init();
        bookList();
    }

    public void init(){
        ly_content=findViewById(R.id.ly_toborrow_content);
        ly_content.getBackground().setAlpha(90);
        listView=findViewById(R.id.list_toborrow_content);
    }

    public void getBook(String borrower){
        RequestBody formBody = new FormBody.Builder()
                .add("borrower",borrower)
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url(Static.ip + "/idSelectBorrower")
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
                        String responseData = response.body().string();
                        mHandler.obtainMessage(1, responseData).sendToTarget();
                        System.out.println("aaaa");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String ReturnMessage = (String) msg.obj;
                final UserBean userBean = new Gson().fromJson(ReturnMessage, UserBean.class);
                try {
                   // System.out.println("aaaaaaaaaaaaaaaa");
                    JSONArray data = new JSONArray(userBean.getData().toString());
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        if (jsonObject != null) {
                            book_name = jsonObject.optString("book_name", null);
                            HashMap<String,String> map = new HashMap<>();
                            map.put("book_name",book_name);
                            //System.out.println(map.get("book_name")+map.get("status")+"dddd");
                            arrayList.add(map);
                            simplead=new SimpleAdapter(ToBorrow.this,arrayList,R.layout.item1,new String[]{"book_name"},
                                    new int[]{R.id.tv_toboname});
                            listView.setAdapter(simplead);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    };
    private void bookList() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                arrayList.clear();
                getBook(Static.username);

                handler.postDelayed(this, 3000);
            }
        });
    }
}
