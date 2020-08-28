package com.example.TripNTip.TripNTip;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.TripNTip.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<Comment> arrayList;
    private Context context;

    public ListViewAdapter(Context context, ArrayList<Comment> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.comment_view, null);
            RelativeLayout relativeLayout = (RelativeLayout) convertView;

            Comment currentComment = arrayList.get(position);

            String author = currentComment.getAuthor();
            String timestamp = currentComment.getTimestamp();
            String message = currentComment.getMessage();

            String commentContent = author + "\t\t\t" + timestamp + "\n\t\t" + message;

            TextView test = new TextView(context);
            test.setText(commentContent);
            relativeLayout.addView(test);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return Math.max(arrayList.size(), 1);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}