package com.example.xiaolan.myapplication;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kingvcn.kv_wsn.ZigBeeAPI;
import cn.kingvcn.kv_wsn.response.onHumitureResponse;
import cn.kingvcn.kv_wsn.response.onPhotoresistanceResponse;
import cn.kingvcn.kv_wsn.socket.SocketConnectEvent;
import cn.kingvcn.kv_wsn.socket.onSocketLinkListener;
import com.example.xiaolan.myapplication.User.UserBean;
import com.example.xiaolan.myapplication.Zigbee.ZigBee;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    private String status;
    String TAG = "TAG";
    private boolean IsConnection = false;
    private String hum1, lux1, temp1, fwdata1, seat_num1, flame1, smoke1, fan1, lamp1;
    GifDrawable gifDrawable;
    private TickerView tick_time, tick_temp, tick_hum, tick_lux, tick_num, tick_seat, tick_flame, tick_smoke;
    final int msg_one = 1;
    private LinearLayout ly_clock, ly_temp, ly_num, ly_relay, ly_flame;
    SwitchButton switch_fan, switch_light;
    AppCompatImageView img_lamp;
    GifImageView gif_fan;
    private Handler mhandler;
    private Runnable runnable;


    public FirstFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        // Inflate the layout for this fragment
        //init();

        tick_time = view.findViewById(R.id.ticker_time);
        tick_temp = view.findViewById(R.id.ticker_temp);
        tick_lux = view.findViewById(R.id.ticker_lux);
        tick_hum = view.findViewById(R.id.ticker_hum);
        tick_num = view.findViewById(R.id.ticker_num);
        tick_seat = view.findViewById(R.id.ticker_seatnum);
        tick_flame = view.findViewById(R.id.ticker_flame);
        tick_smoke = view.findViewById(R.id.ticker_smoke);
        img_lamp = view.findViewById(R.id.img_lampclose);
        gif_fan = view.findViewById(R.id.gif_fan);
        switch_fan = view.findViewById(R.id.switch_fan);
        switch_light = view.findViewById(R.id.switch_light);
        tick_flame.setCharacterLists(TickerUtils.provideNumberList());
        tick_smoke.setCharacterLists(TickerUtils.provideNumberList());
        tick_num.setCharacterLists(TickerUtils.provideNumberList());
        tick_seat.setCharacterLists(TickerUtils.provideNumberList());
        tick_lux.setCharacterLists(TickerUtils.provideNumberList());
        tick_hum.setCharacterLists(TickerUtils.provideNumberList());
        tick_temp.setCharacterLists(TickerUtils.provideNumberList());
        tick_time.setCharacterLists(TickerUtils.provideNumberList());
        ly_num = view.findViewById(R.id.ly_library_num);
        ly_clock = view.findViewById(R.id.lv_clock);
        ly_temp = view.findViewById(R.id.ly_temp);
        ly_relay = view.findViewById(R.id.ly_relay);
        ly_flame = view.findViewById(R.id.ly_flame);
        ly_flame.getBackground().setAlpha(90);
        ly_relay.getBackground().setAlpha(90);
        ly_num.getBackground().setAlpha(90);
        ly_temp.getBackground().setAlpha(90);
        ly_clock.getBackground().setAlpha(90);
        initDrawable();
        switch_light.setClickable(false);
        switch_fan.setClickable(false);

        open();
        new TimeThread().start();
        getData();

        return view;
    }

    private void initDrawable() {
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.fan_d);
            gif_fan.setImageDrawable(gifDrawable);
            gifDrawable.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void open(){
        switch_fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_fan.startAnimate();
                if (switch_fan.isChecked()){
                    gifDrawable.start();

                }else {
                    gifDrawable.stop();
                }

            }
        });
        switch_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_light.startAnimate();

            }
        });
    }

    private void getData() {
        mhandler = new Handler();
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                showzigbee(1);
                tick_temp.setText(temp1 + "℃");
                tick_hum.setText(hum1 + "%");
                tick_lux.setText(lux1 + "lux");
                tick_flame.setText(flame1);
                tick_num.setText(fwdata1);
                tick_seat.setText(seat_num1);
                tick_smoke.setText(smoke1);
                mhandler.postDelayed(this, 1000);
            }
        });

    }

    //获取系统时间
    public class TimeThread extends  Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msg_one;
                    mHandler1.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    private Handler mHandler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case msg_one:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEE");
                    tick_time.setText(format.format(date));
                    break;
                default:
                    break;
            }
        }
    };

    private void showzigbee(int id) {
        //建立请求表单，添加上传服务器的参数
        RequestBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(id))
                .build();
        //发起请求
        final Request request = new Request.Builder()
                .url(Static.ip + "/showzigbee")
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
                // Log.i("获取的返回信息",ReturnMessage);
                final UserBean userBean = new Gson().fromJson(ReturnMessage, UserBean.class);
                status = userBean.getStatus();
                String jsonString = String.valueOf(userBean.getData());
            //    System.out.println(jsonString);
                JsonElement je = new JsonParser().parse(jsonString);
                hum1 = je.getAsJsonObject().get("hum").toString();
                temp1 = je.getAsJsonObject().get("temp").toString();

                if ("1.0".equals(je.getAsJsonObject().get("fan").toString())) {
                    switch_fan.setChecked(true);

                } else {
                    switch_fan.setChecked(false);

                }
                lux1 = je.getAsJsonObject().get("lux").toString();


                if ("1.0".equals(je.getAsJsonObject().get("lamp").toString())) {
                    img_lamp.setImageResource(R.drawable.lamp_open);
                    switch_light.setChecked(true);
                } else {
                    img_lamp.setImageResource(R.drawable.lamp_close);
                    switch_light.setChecked(false);
                }

                fwdata1 = je.getAsJsonObject().get("fwdata").toString();
                seat_num1 = je.getAsJsonObject().get("seatnum").toString();


                // Log.d(TAG, "flame"+je.getAsJsonObject().get("flame").toString());

                if ("1.0".equals(je.getAsJsonObject().get("flame").toString())) {
                    flame1 = "着火了!!!";
                } else {
                    flame1 = "一切正常";
                }

                if ("1.0".equals(je.getAsJsonObject().get("smoke").toString())) {
                    smoke1 = "有烟雾!!!";
                } else {
                    smoke1 = "一切正常";
                }
            }

        }
    };
}
