package com.example.navigate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    //数据集合list
    List<String[]> list;
    //添加反射器
    LayoutInflater inflater;
    //构造器 上下文
    public ListAdapter(Context context){
        inflater=LayoutInflater.from(context);
    }
    //传入数据集合
    public void setList( List<String[]> list){
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public String[] getItem(int position) {//没什么用
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {//没什么用
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //反射listview布局，后面null先默认如此，没啥用
        View view=inflater.inflate(R.layout.listview,null);
        //获取控件位置
        TextView context=(TextView)view.findViewById(R.id.tv_context);
        context.setTextColor(Color.argb(255,255,255,255));
        //填充信息
        context.setText(list.get(position)[0]);
        //将含有信息的view返回到ListView
        return view;
    }
}
