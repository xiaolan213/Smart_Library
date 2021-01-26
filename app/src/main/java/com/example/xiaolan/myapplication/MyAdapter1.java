package com.example.xiaolan.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapter1 extends BaseAdapter {
    Context c;
    ArrayList<HashMap<String, String>> arrayList;

    public MyAdapter1(Context c, ArrayList<HashMap<String, String>> arrayList) {
        this.c = c;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = View.inflate(c,R.layout.item1,null);
        TextView txTitle = view.findViewById(R.id.tv_toboname);
        txTitle.setText(arrayList.get(i).get("book_name"));
        TextView txDate = view.findViewById(R.id.tv_author);
        txDate.setText(arrayList.get(i).get("author"));
        TextView txSrc = view.findViewById(R.id.tv_status);
        txSrc.setText(arrayList.get(i).get("status"));
        return view;
    }
}
