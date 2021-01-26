package com.example.xiaolan.myapplication;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import okhttp3.OkHttpClient;

import java.net.*;
import java.util.Enumeration;

public class Static {
    static final String ip="http://192.168.1.24:8080/Spring_Library_war/admin";
    static final OkHttpClient client = new OkHttpClient();
    static String username=null;
    static String author=null;
    static String book_name=null;
    static int status=0;
    static String borrower=null;
    static String xi="009CCC6A5E";
    static String status2=null;
    static String  xiyouji="西游记";
    static boolean flag2=false;
    static int count=0;
}
