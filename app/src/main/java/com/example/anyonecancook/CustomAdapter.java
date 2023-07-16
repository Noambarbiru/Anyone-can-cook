package com.example.anyonecancook;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> data;
    private int layout;
    private boolean is_list_view;

    public CustomAdapter(Context context, ArrayList<String> data, int layout, boolean is_list_view) {
        this.data = data;
        this.context = context;
        this.layout = layout;
        this.is_list_view = is_list_view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (this.is_list_view) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(this.layout, null);
                holder.txt = (TextView) view.findViewById(R.id.txt);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.txt.setText(data.get(position));

            if (data.get(position).equals("Ingredients:") || data.get(position).equals("Instructions:")) {
                holder.txt.setTypeface(null, Typeface.BOLD);
            } else {
                holder.txt.setTypeface(null, Typeface.NORMAL);
            }
        } else {
            CheckedViewHolder holder;
            if (view == null) {
                holder = new CheckedViewHolder();
                view = LayoutInflater.from(context).inflate(this.layout, null);
                holder.checked_text_view = (CheckedTextView) view.findViewById(R.id.checked_text_view);

                view.setTag(holder);
            } else {
                holder = (CheckedViewHolder) view.getTag();
            }

            holder.checked_text_view.setText(data.get(position));

            if (data.get(position).equals("I HAVE EVERYTHING!\n")) {
                holder.checked_text_view.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                holder.checked_text_view.setTypeface(null, Typeface.NORMAL);
            }
        }
        return view;

    }

    class ViewHolder {
        TextView txt;
    }

    class CheckedViewHolder {
        CheckedTextView checked_text_view;
    }

}