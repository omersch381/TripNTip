package com.example.TripNTip.TripNTip;

import android.content.Context;
import android.util.AttributeSet;

public class SquaredImageView extends androidx.appcompat.widget.AppCompatImageView {

    private String name;

    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}