package com.example.xiaolan.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.kingvcn.rfid.RFID125KAPI;
import cn.kingvcn.rfid.response.rfid125k.onGetTagResponse;
import cn.kingvcn.rfid.socket.onConnectResult;
import com.example.xiaolan.myapplication.Book.Book;
import com.example.xiaolan.myapplication.User.UserBean;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment implements SearchView.OnQueryTextListener {

    private LinearLayout ly_borrow, ly_list;
    private ListView listView;
    private String book_name, status1, author,borrower,tag,status2;
    private Handler handler;
    private int status=0;
    private ScrollView scrollView;
    private SearchView searchView;
    private ArrayAdapter<String> arr_adapte;
    List<String> list = new ArrayList<String>();
    MyAdapter adapter;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    private boolean isConnect = false;
            boolean flag=true;
            boolean flag2=false;


    Context context;

    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        // Inflate the layout for this fragment
        ly_list = view.findViewById(R.id.ly_list);
        ly_borrow = view.findViewById(R.id.ly_borrow);
        ly_list.getBackground().setAlpha(90);
        ly_borrow.getBackground().setAlpha(90);
        listView = view.findViewById(R.id.listview);
        scrollView=view.findViewById(R.id.scrollview);
        searchView=view.findViewById(R.id.searchView);
        context = getContext();
       // getBook();

        bookList();
        //让ScrollView获得焦点
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);
        scrollView.requestFocus();
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        judge();
        while (flag) {
            linkGetWay("192.168.6.1", 101);
            flag = false;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
                HashMap<String,String> map=(HashMap<String,String>)listView.getItemAtPosition(position);
                Static.book_name=map.get("book_name");
                Static.author=map.get("author");
                if(map.get("status").equals("借出")){
                    Static.count=0;
                    Static.status=1;
                    Static.borrower=map.get("borrower");
                }else {
                    Static.status=0;
                    Static.borrower="无";
                    Static.count=1;
                }

               // Static.status=status;
                startActivity(new Intent(getContext(),ListActivity.class));*/

            }
        });

        return view;
    }



    private void bookList() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                arrayList.clear();
                getBook();

                handler.postDelayed(this, 3000);
            }
        });


    }

    public void getBook() {
        RequestBody formBody = new FormBody.Builder()
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url(Static.ip + "/showbook")
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
                        final String responseData = response.body().string();
                        mHandler.obtainMessage(1, responseData).sendToTarget();
                        //System.out.println("cg");

                    } else {
                        throw new IOException("Unexpected code:" + response);
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

                    JSONArray data = new JSONArray(userBean.getData().toString());
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        if (jsonObject != null) {

                            book_name = jsonObject.optString("book_name", null);
                            author = jsonObject.optString("author", null);
                            status1 = jsonObject.optString("status1", null);
                            if ("1.0".equals(status1)) {
                                status1 = "借出";
                            } else {
                                status1 = "在馆";
                            }
                            borrower=jsonObject.optString("borrower",null);

                            HashMap<String,String> map = new HashMap<>();
                            map.put("status",status1);
                            map.put("book_name",book_name);
                            map.put("atuhor",author);
                            map.put("borrower",borrower);
                            //System.out.println(map.get("book_name")+map.get("status")+"dddd");
                            arrayList.add(map);
                            adapter = new MyAdapter(getContext(),arrayList);
                            listView.setAdapter(adapter);
                      /*      list.add(book_name);
                            arr_adapte = new ArrayAdapter<>(context,
                                    android.R.layout.simple_list_item_1, list);
                            listView.setAdapter(arr_adapte);*/
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    };


    public void updateLayout(Object[] obj) {
        listView.setAdapter(new ArrayAdapter<Object>(context,
                android.R.layout.simple_list_item_1, obj));
    }


    public Object[] searchItem(String name) {
        ArrayList<String> mSearchList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            int index = list.get(i).indexOf(name);
            // 存在匹配的数据
            if (index != -1) {
                mSearchList.add(list.get(i));
               // listView.setVisibility(View.GONE);
            }else {
                //listView.setVisibility(View.VISIBLE);

            }
        }
        return mSearchList.toArray();
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Object[] obj = searchItem(s);
        updateLayout(obj);
        return false;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d("TAG", "RFIDcg ");
                    break;
                case 2:
                    Log.d("TAG", "RFIDsb");
                    break;
                case 3:
                    if(msg.obj.toString().equals(Static.xi)){
                        up();
                         /*   if (status2.equals("在馆")){
                                tag=Static.xi;
                                status=1;
                                borrower=Static.username;
                                update();
                            }else {
                                tag=Static.xi;
                                status=0;
                                borrower="无";
                                update();
                            }*/


                    }else {
                        System.out.println("bbbbb");
                    }
                    break;
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

        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();
    }

    private String toJson() {
        Book book = new Book();
        book.setStatus1(status);
        book.setBook_name(Static.book_name);
        book.setBorrower(borrower);
        book.setTag(tag);
        Gson gson = new Gson();
        return gson.toJson(book);
    }

    private void linkGetWay(String ip, int port) {
        RFID125KAPI.creatLink(ip, port, new onConnectResult() {
            @Override
            public void onConnectSuccess() {
                isConnect = true;
                Message msg = new Message();
                msg.what = 1;
                mHandler1.sendMessage(msg);
            }

            @Override
            public void onConnectFail() {
                Message msg = new Message();
                msg.what = 2;
                mHandler1.sendMessage(msg);
            }
        });

    }





    private void judge() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                getTag1();
              /*  if (isConnect) {
                    System.out.println("cccc");

                }else {
                    System.out.println("ddddd");
                }*/
            }
        }, 1000);
    }


    private void getTag1() {
        RFID125KAPI.getTagData(new onGetTagResponse() {
            @Override
            public void onCardNumber(String s) {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = s;
                mHandler1.sendMessage(msg);

            }

            @Override
            public void onWieGandData(String s) {
                Message msg = new Message();
                msg.what = 4;
                msg.obj = s;
                mHandler1.sendMessage(msg);
            }
        });
    }


    private void SearchBookStatus(String tag){
        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("tag", tag)
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url(Static.ip + "/SelectBookTag")
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
                        //mHandler.obtainMessage(2, responseData).sendToTarget();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(responseData);
                            int a= (int) jsonObject.getJSONObject("data").get("status1");
                            if (a==1){
                                status2="借出";
                            }else {
                                status2="在馆";
                            }
                            System.out.println(status2+"ddd");
                           /* System.out.println("status是"+jsonObject.getJSONObject("data").get("status1"));
                            if ("1".equals(jsonObject.getJSONObject("data").get("status1"))){
                                status2="借出";
                            }else {
                                status2="在馆";
                            }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("查询成功");
                        System.out.println(status2);

                    } else {
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private void up(){
        SearchBookStatus(Static.xi);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (status2!=null){
                    if (status2.equals("在馆")){
                        tag=Static.xi;
                        status=1;
                        borrower=Static.username;
                        //status2="借出";
                        update();
                    }else {
                        tag=Static.xi;
                        status=0;
                        borrower="无";
                       // status2="在馆";
                        update();
                    }
                }
            }
        },1000);

    }
}
