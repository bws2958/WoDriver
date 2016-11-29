package com.wodriver.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wodriver.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by user on 2016-11-27.
 */

public class listAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<CustomList> list;
    LayoutInflater inflater;

    public listAdapter(Context context, int layout, ArrayList<CustomList> list){
        this.context = context;
        this.layout = layout;
        this.list = list;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount(){
        return list.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            view = inflater.inflate(layout, null);
        }
        TextView content1 = (TextView)view.findViewById(R.id.textView1);
//        TextView content2 = (TextView)view.findViewById(R.id.textView2);
//        TextView content3 = (TextView)view.findViewById(R.id.textView3);
//        TextView content4 = (TextView)view.findViewById(R.id.textView4);

        CustomList customList = list.get(position);

        content1.setText(customList.getContent1());
//        content2.setText(customList.getContent2());
//        content3.setText(customList.getContent3());
//        content4.setText(customList.getContent4());

        return view;
    }
}
