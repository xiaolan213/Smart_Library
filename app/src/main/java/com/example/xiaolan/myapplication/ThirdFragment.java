package com.example.xiaolan.myapplication;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.example.xiaolan.myapplication.Book.Book;
import com.example.xiaolan.myapplication.Image.ImageListAdapter;
import com.example.xiaolan.myapplication.Image.ImageListArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {

    private Button btn_exit;
    private TextView tv_name,tv_settting;
    private LinearLayout ly_profile,ly_profile1,ly_tel,ly_book1,ly_exit;
    private List<ImageListArray> BookList = new ArrayList<>();
    private ListView listView;
    public ThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_third, container, false);
        tv_name=view.findViewById(R.id.tv_name);
        ly_exit=view.findViewById(R.id.ly_exit);
        ly_exit.getBackground().setAlpha(90);
        btn_exit=view.findViewById(R.id.btn_exit);
        tv_settting=view.findViewById(R.id.tv_settting);
        ly_profile=view.findViewById(R.id.ly_profile);
        ly_profile.getBackground().setAlpha(100);
        ly_profile1=view.findViewById(R.id.ly_profile1);
        ly_profile1.getBackground().setAlpha(80);
        ly_tel=view.findViewById(R.id.ly_tel);
        ly_tel.getBackground().setAlpha(90);
        ly_book1=view.findViewById(R.id.ly_book1);
        ly_book1.getBackground().setAlpha(90);
        BookList.clear();
        tv_name.setText(Static.username);//接收用户名
        addingData(); //初始化数据
        //创建适配器，在适配器中导入数据 1.当前类 2.list_view一行的布局 3.数据集合
        ImageListAdapter imageListAdapter = new ImageListAdapter(getContext(),R.layout.imagelist,BookList);
        listView = view.findViewById(R.id.lv_book); //将适配器导入Listview

        listView.setAdapter(imageListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    startActivity(new Intent(getContext(),ToBorrow.class));
                   getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_from_left);
                }else {
                    Toast.makeText(getContext(),"敬请期待",Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConsumerDialogCircle();
            }
        });

        return view;
    }

    public void addingData(){
        ImageListArray zaijie =new ImageListArray("在借中",R.drawable.zaijie);
        BookList.add(zaijie);
        ImageListArray lgq =new ImageListArray("临过期",R.drawable.guoqi);
        BookList.add(lgq);
        ImageListArray ygq =new ImageListArray("已过期",R.drawable.yiguoqi);
        BookList.add(ygq);
        ImageListArray list =new ImageListArray("借阅清单",R.drawable.listbook);
        BookList.add(list);

    }

    private void showConsumerDialogCircle() {
        final Dialog dialog = new Dialog(getContext(), R.style.BottomDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circle1, null);

        view.findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getActivity().finish();
            }
        });

        view.findViewById(R.id.tv_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(getContext(),MainActivity.class));
                getActivity().overridePendingTransition(R.anim.in_from_right,R.anim.out_from_left);
                getActivity().finish();
            }
        });
        dialog.setContentView(view);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view
                .getLayoutParams();
        layoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        layoutParams.width = getResources().getDisplayMetrics().widthPixels - (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                        getResources().getDisplayMetrics());
        view.setLayoutParams(layoutParams);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }
}
