package com.example.TripNTip;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

public class TripView extends LinearLayout {
    //    private Drawable d;
    private TextView textView;

    //    public TripView(Context context, StorageReference listRef) {
//        super(context);
//        this.listRef = listRef;
//        generateTextViews(context);
//    }
    public TripView(Context context) {
        super(context);
        generateTextViews(context);
    }

    private void generateTextViews(Context context) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.textColor));
        addView(textView);
    }

    public void setText(CharSequence charSequence) {
        textView.setText(String.valueOf(charSequence));
    }
}
