package com.example.android.calculator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by toshiba pc on 3/8/2018.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    private Node[] onCalculationList;
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Node[] getOnCalculationList() {
        return onCalculationList;
    }

    public void setOnCalculationList(Node[] onCalculationList) {
        this.onCalculationList = onCalculationList;
    }
}
