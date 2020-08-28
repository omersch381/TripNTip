package com.example.TripNTip.TripNTip;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.TripNTip.R;

public class TripView extends LinearLayout {

    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public TripView(Context context) {
        super(context);
        generateTextViews(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
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
