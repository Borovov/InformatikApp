package com.example.informatika;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<ListClass> objects;

    BoxAdapter(Context context, ArrayList<ListClass> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override   // кол-во элементов
    public int getCount() {
        return objects.size();
    }

    @Override   // элемент по позиции
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override   // id по позиции
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override   // пункт списка
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_view, parent, false);
        }

        ListClass p = getProduct(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.text_num_list)).setText(p.num + ".");
        ((TextView) view.findViewById(R.id.text_list)).setText(p.name);
        ((TextView) view.findViewById(R.id.text_per_list)).setText(p.per + "%");

        view.setClipToOutline(false);
        return view;
    }

    // товар по позиции
    ListClass getProduct(int position) {
        return ((ListClass) getItem(position));
    }

}
