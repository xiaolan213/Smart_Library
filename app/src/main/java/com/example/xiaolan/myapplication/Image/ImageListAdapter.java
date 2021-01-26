package com.example.xiaolan.myapplication.Image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.xiaolan.myapplication.R;

import java.util.List;

public class ImageListAdapter extends ArrayAdapter<ImageListArray> {

    private int recourceId;

    public ImageListAdapter(Context context, int resource, List<ImageListArray> objects) {
        super(context, resource, objects);
        recourceId = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ImageListArray imageListArray = getItem(position); //得到集合中指定位置的一组数据，并且实例化
        View view = LayoutInflater.from(getContext()).inflate(recourceId,parent,false); //用布局裁剪器(又叫布局膨胀器)，将导入的布局裁剪并且放入到当前布局中
        ImageView imageView = view.findViewById(R.id.IamgeView_List);//从裁剪好的布局里获取ImageView布局ID
        TextView textView = view.findViewById(R.id.TextView_List); //从裁剪好的布局里获取TextView布局Id
        imageView.setImageResource(imageListArray.getImageId());//将当前一组imageListArray类中的图片iamgeId导入到ImageView布局中
        textView.setText(imageListArray.getName());//将当前一组imageListArray类中的TextView内容导入到TextView布局中
        return view;
    }


}
