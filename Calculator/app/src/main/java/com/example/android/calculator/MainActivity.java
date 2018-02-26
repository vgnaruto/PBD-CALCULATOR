package com.example.android.calculator;

import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    private final int OPERAND_WIDTH = 276;
    private final int OPERATOR_WIDTH = 84;
    private static int INDEX = 0;
    private static MainActivity instances;

    private Button bAddOperator, bAddOperand;
    private EditText etOperand;
    private Spinner sOperator;
    private RelativeLayout rLayout;
    private ArrayAdapter<CharSequence> adapter;
    private CustomTextView[] onCalculationList;
    private ImageView trashZone;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        this.onCalculationList = new CustomTextView[7];

        this.trashZone = findViewById(R.id.trash_zone);
        this.bAddOperator = findViewById(R.id.bAddOperator);
        this.bAddOperand = findViewById(R.id.bAddOperand);
        this.etOperand = findViewById(R.id.etOperand);
        this.sOperator = findViewById(R.id.sOperator);
        this.rLayout = findViewById(R.id.relativeLayout);

        this.adapter = ArrayAdapter.createFromResource(this, R.array.operator, android.R.layout.simple_spinner_item);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sOperator.setAdapter(adapter);

        this.bAddOperand.setOnClickListener(this);
        this.bAddOperator.setOnClickListener(this);

        instances = this;
    }

    @Override
    public void onClick(View view) {
        Log.d("MainActivity", "clicked");
        if (view == bAddOperand) {
            if (!etOperand.getText().toString().equalsIgnoreCase("")) {
                CustomTextView newTv = createOp(etOperand.getText().toString(), 0);
                newTv.setOnTouchListener(new CustomTouchListener());
                newTv.setId(INDEX++);
                newTv.setIsOperator(false);
                rLayout.addView(newTv);
                etOperand.setText("");
            }
        } else if (view == bAddOperator) {
            CustomTextView newTv = createOp(sOperator.getSelectedItem().toString(), 1);
            newTv.setOnTouchListener(new CustomTouchListener());
            newTv.setIsOperator(true);
            newTv.setId(INDEX++);
            rLayout.addView(newTv);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private CustomTextView createOp(String text, int id) {
        final RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final CustomTextView tv = new CustomTextView(this);
        tv.setLayoutParams(lParams);
        tv.setText(text);
        switch (id){
            case 0:
                tv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.lightBlue, null));
                break;
            case 1:
                tv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.lightYellow, null));
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        float tempX = (float)Math.random()*(WIDTH-400)+200;
        float tempY = (float)(Math.random())*(HEIGHT - 600)+200;
        tv.setX(tempX);
        tv.setY(tempY);
//        Log.d("xy",tempX+" "+tempY);
        tv.setPadding(30,50,30,50);
        return tv;
    }
    public static MainActivity getInstances() {
        return instances;
    }
    public ImageView getTrashZone(){
        return this.trashZone;
    }
    public CustomTextView[] getOnCalculationList(){
        return this.onCalculationList;
    }

    public boolean contains(View v){
        for(CustomTextView view : onCalculationList){
            if(view == v){
                return true;
            }
        }
        return false;
    }
    public void remove(View v){
        for(int i=0;i<onCalculationList.length;i++){
            if(v == onCalculationList[i]){
                onCalculationList[i] = null;
                break;
            }
        }
    }
    public boolean add(View v){
        CustomTextView currView = (CustomTextView) v;
        //view = operator
        if(currView.getIsOperator()){
            for(int i=1;i<onCalculationList.length;i+=2){
                if(onCalculationList[i] == null){
                    onCalculationList[i] = currView;
                    setCoordinat(v,i);
                    return true;
                }
            }
        }
        //view = operand
        else{
            for(int i=0;i<onCalculationList.length;i+=2){
                if(onCalculationList[i] == null){
                    onCalculationList[i] = currView;
                    setCoordinat(v,i);
                    return true;
                }
            }
        }
        return false;
    }
    private void setCoordinat(View v, int indeks){
        int currX = 30;
        if(indeks == 0){
            v.setX(currX);
        }else{
            for(int i=0;i<indeks;i++){
                //operand
                if(i % 2 == 0){
                    currX += OPERAND_WIDTH;
                }
                //operator
                else{
                    currX += OPERATOR_WIDTH;
                }
                currX += 40;
                Log.d("COORDINATE",i+" : "+currX);
            }
            if(indeks % 2 == 0){
                currX -= 20;
            }
            v.setX(currX);
        }
        v.setY(50);
    }
    public boolean isKosong(String value){
        if(value.equalsIgnoreCase("OPERAND")){
            for(int i=0;i<onCalculationList.length;i+=2){
                if(onCalculationList[i] == null){
                    return true;
                }
            }
        }
        else if(value.equalsIgnoreCase("OPERATOR")){
            for(int i=1;i<onCalculationList.length;i+=2){
                if(onCalculationList[i] == null){
                    return true;
                }
            }
        }
        return false;
    }
}
