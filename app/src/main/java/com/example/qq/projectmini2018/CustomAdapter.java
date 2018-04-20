package com.example.qq.projectmini2018;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context mContext;

    String[] strName;
    String[] phoneNum;
    String[] imgPath;
    String[] email;
    int[] id;

    public CustomAdapter(Context mContext, String[] strName, String[] phoneNum, String[] imgPath, String[] email) {
        this.mContext = mContext;
        this.strName = strName;
        this.phoneNum = phoneNum;
        this.imgPath = imgPath;
        this.email = email;
    }

    public CustomAdapter(int[] id) {
        this.id = id;
    }

    @Override
    public int getCount() {
        return strName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.listview_row, parent, false);

        TextView textView = convertView.findViewById(R.id.textView1);
        textView.setText(strName[position]);

        ImageView imageView = convertView.findViewById(R.id.newImageview);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath[position]));

        return convertView;
    }
}
