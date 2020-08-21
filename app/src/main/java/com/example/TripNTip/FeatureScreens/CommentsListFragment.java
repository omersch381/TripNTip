package com.example.TripNTip.FeatureScreens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.Comment;
import com.example.TripNTip.TripNTip.ListViewAdapter;

import java.util.ArrayList;

public class CommentsListFragment extends Fragment {

    private ArrayList<Comment> comments;

    public CommentsListFragment(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.comments_list_fragment, container, false);

        ListView listView = v.findViewById(R.id.listView);

        ListViewAdapter adapter = new ListViewAdapter(getContext(),comments);

        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return v;
    }
}