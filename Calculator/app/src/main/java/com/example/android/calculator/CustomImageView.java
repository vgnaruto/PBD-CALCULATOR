package com.example.android.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by toshiba pc on 2/25/2018.
 */

@SuppressLint("AppCompatCustomView")
public class CustomImageView extends ImageView {
    private int id;
    private boolean isOperator;
    private String text;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
    public void setText(String text){
        this.text =text;
    }
    public String getText(){
        return this.text;
    }
}
