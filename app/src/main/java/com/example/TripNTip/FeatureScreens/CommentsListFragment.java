package com.example.TripNTip.FeatureScreens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.Comment;

import java.util.ArrayList;

public class CommentsListFragment extends Fragment {

    private ListView listView;
    private String[] listItem;
    private ArrayList<Comment> comments;

    public CommentsListFragment() {
    }

    public static CommentsListFragment newInstance() {
        return new CommentsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.comments_list_fragment, container, false);
        listView = (ListView) v.findViewById(R.id.listView);

        comments = new ArrayList<>();
        // read from firebase the comments into the arrayList
        listItem = new String[]{"hello", "hi"};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext()
                , android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value = adapter.getItem(position);
                Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}