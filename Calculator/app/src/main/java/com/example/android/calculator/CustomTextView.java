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
        this.onCalculationList = new Node[9];
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.onCalculationList = new Node[9];
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.onCalculationList = new Node[9];
    }

    public Node[] getOnCalculationList() {
        return onCalculationList;
    }

    public void setOnCalculationList(Node[] onCalculationList) {
        for(int i=0;i<onCalculationList.length;i++){
            if(onCalculationList[i] != null){
                Node tempNode = new Node(onCalculationList[i].getText(),onCalculationList[i].getX(),onCalculationList[i].getY(),onCalculationList[i].getWidth(),onCalculationList[i].getHeight(),onCalculationList[i].id);
                this.onCalculationList[i] = tempNode;
            }
        }
    }
}
