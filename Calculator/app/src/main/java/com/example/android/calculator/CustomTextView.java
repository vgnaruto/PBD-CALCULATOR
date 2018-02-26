package com.example.android.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by toshiba pc on 2/25/2018.
 */

@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {
    private int id;
    private boolean isOperator;
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setIsOperator(boolean isIt){
        this.isOperator = isIt;
    }
    public boolean getIsOperator(){
        return this.isOperator;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
}
