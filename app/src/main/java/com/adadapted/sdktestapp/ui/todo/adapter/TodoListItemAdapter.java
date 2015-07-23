package com.adadapted.sdktestapp.ui.todo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoItem;

import java.util.List;

/**
 * Created by chrisweeden on 6/26/15.
 */
public class TodoListItemAdapter extends ArrayAdapter<TodoItem> {
    private List<TodoItem> items;

    public TodoListItemAdapter(Context context, List<TodoItem> items) {
        super(context, 0);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public TodoItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_layout, null);
        }

        TodoItem item = items.get(position);

        TextView tv = (TextView)v.findViewById(R.id.list_item_textView);
        tv.setText(item.getName());

        return v;
    }
}
