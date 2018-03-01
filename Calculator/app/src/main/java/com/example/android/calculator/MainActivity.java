package com.example.android.calculator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    private final int OPERAND_WIDTH = 246;
    private final int OPERATOR_WIDTH = 84;
    private static int INDEX = 0;
    private static MainActivity instances;

    private ImageButton bAddOperator, bAddOperand, bCalculate;
    private EditText etOperand;
    private Spinner sOperator;
    private RelativeLayout rLayout;
    private ArrayAdapter<CharSequence> adapter;
    private CustomImageView[] onCalculationList;
    private int[] coordinatX;
    private ImageView trashZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        //CONNECT ATTRIBUT KE VIEW
        this.trashZone = findViewById(R.id.trash_zone);
        this.bCalculate = findViewById(R.id.bCalculate);
        this.bAddOperator = findViewById(R.id.bAddOperator);
        this.bAddOperand = findViewById(R.id.bAddOperand);
        this.etOperand = findViewById(R.id.etOperand);
        this.sOperator = findViewById(R.id.sOperator);
        this.rLayout = findViewById(R.id.relativeLayout);

        //ADAPTER SPINNER
        this.adapter = ArrayAdapter.createFromResource(this, R.array.operator, android.R.layout.simple_spinner_item);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sOperator.setAdapter(adapter);

        //LISTENER
        this.bAddOperand.setOnClickListener(this);
        this.bAddOperator.setOnClickListener(this);
        this.bCalculate.setOnClickListener(this);

        //INISIALISASI ATTRIBUT
        instances = this;
        this.onCalculationList = new CustomImageView[9];
        this.coordinatX = new int[9];
        for (int i = 0; i < coordinatX.length; i++) {
            coordinatX[i] = posisiX(i);
        }
    }

    @Override
    public void onClick(View view) {
//        Log.d("MainActivity", "clicked");
        if (view == bAddOperand) {
            if (!etOperand.getText().toString().equalsIgnoreCase("")) {
                CustomImageView newTv = createOp(etOperand.getText().toString(), 0);
                newTv.setOnTouchListener(new CustomTouchListener());
                rLayout.addView(newTv);
                etOperand.setText("");
            }
        } else if (view == bAddOperator) {
            CustomImageView newTv = createOp(sOperator.getSelectedItem().toString(), 1);
            newTv.setOnTouchListener(new CustomTouchListener());
            rLayout.addView(newTv);
        } else if (view == bCalculate) {
            LinkedList<String> llOperand = new LinkedList<>();
            LinkedList<String> llOperator = new LinkedList<>();
            int jumlahOperand = 0;
            int jumlahOperator = 0;
            for(int i=0;i<onCalculationList.length;i++){
                CustomImageView civ = onCalculationList[i];
                if(i%2==0){
                    if(civ == null){
                        llOperand.add("NULL");
                    }else{
                        llOperand.add(civ.getText());
                        jumlahOperand++;
                    }
                }else{
                    if(civ == null){
                        llOperator.add("NULL");
                    }else{
                        llOperator.add(civ.getText());
                        jumlahOperator++;
                    }
                }
            }
            if(jumlahOperand - jumlahOperator != 1){
                //tidak valid
            }else{
                String a1 = llOperand.removeFirst();
                String a2 = "";
                String op = "";
                String hasil = "";
                boolean isValid = true;
                while(!llOperand.isEmpty()){
                    a2 = llOperand.removeFirst();
                    op = llOperator.removeFirst();
                    if(a1.equalsIgnoreCase("NULL") || a2.equalsIgnoreCase("NULL") || op.equalsIgnoreCase("NULL")){
                        break;
                    }
                    hasil = Calculator.calculate(a1,a2,op)+"";
                    a1 = hasil;
                    a2 = "";
                    op = "";
                }
                while(!llOperand.isEmpty()){
                    String s = llOperand.removeFirst();
                    if(!s.equalsIgnoreCase("NULL")){
                        isValid = false;
                    }
                }
                while(!llOperator.isEmpty()){
                    String s = llOperator.removeFirst();
                    if(!s.equalsIgnoreCase("NULL")){
                        isValid = false;
                    }
                }
                if(isValid) {
                    Log.d("CALCULATE", hasil);
                    for(CustomImageView civ : onCalculationList){
                        if(civ != null) {
                            this.remove(civ);
                            ((ViewGroup) civ.getParent()).removeView(civ);
                        }
                    }
                    CustomImageView newIV = this.createOp(String.format("%.2f",Double.parseDouble(hasil)),0);
                    newIV.setOnTouchListener(new CustomTouchListener());
                    rLayout.addView(newIV);
                }else{
                    Log.d("CALCULATE", "TIDAK VALID");
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private CustomImageView createOp(String text, int id) {
        float x = 0;
        float y = 0;
        //BITMAP
        Bitmap bitmap;
        if (id == 0) {
            bitmap = Bitmap.createBitmap(OPERAND_WIDTH, 100, Bitmap.Config.ARGB_8888);
            x = 128;
            y = 68;
        } else {
            bitmap = Bitmap.createBitmap(OPERATOR_WIDTH, 100, Bitmap.Config.ARGB_8888);
            x = 42;
            y = 80;
        }
        //CANVAS
//        if(this.canvas == null){
//            this.canvas = new Canvas(bitmap);
//        }
        Canvas canvas = new Canvas(bitmap);
        //IMAGE VIEW
        final RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final CustomImageView tv = new CustomImageView(MainActivity.this);
        tv.setLayoutParams(lParams);
        tv.setImageBitmap(bitmap);

        tv.setId(INDEX++);
        tv.setText(text);

        Paint paint = new Paint();
        switch (id) {
            case 0:
                tv.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                paint.setTextSize(45f);
                tv.setIsOperator(false);
                break;
            case 1:
                tv.setBackgroundColor(getResources().getColor(R.color.lightPurple));
                paint.setTextSize(100f);
                tv.setIsOperator(true);
                break;
        }
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getResources().getColor(R.color.black));
        paint.setAntiAlias(true);
        canvas.drawText(text, x, y, paint);
        float randX = (float) Math.random() * (WIDTH - 400) + 200;
        float randY = (float) (Math.random()) * (HEIGHT - 600) + 200;
        tv.setX(randX);
        tv.setY(randY);
        tv.invalidate();
        return tv;
    }

    public static MainActivity getInstances() {
        return instances;
    }

    public ImageView getTrashZone() {
        return this.trashZone;
    }

    public CustomImageView[] getOnCalculationList() {
        return this.onCalculationList;
    }

    public boolean contains(View v) {
        for (CustomImageView view : onCalculationList) {
            if (view == v) {
                return true;
            }
        }
        return false;
    }

    /**
     * Membuang view dari array.
     */
    public void remove(View v) {
        for (int i = 0; i < onCalculationList.length; i++) {
            if (v == onCalculationList[i]) {
                onCalculationList[i] = null;
                break;
            }
        }
    }

    /**
     * Param 1 : View yang lagi ditekan
     * Berguna untuk menambahkan view ke dalam array
     * View yang masuk ke dalam array hanya view yang valid
     * View yang valid maksudnya slot tempat view akan dimasukkan masih kosong
     */
    public boolean add(CustomImageView v) {
        onCalculationList[getIdxTerdekat(v)] = v;
        setPosition(v);
        return true;
    }

    /**
     * Param 1 : View yang lagi ditekan
     * Berguna untuk meletakan view ke dalam slot terdekatnya.
     */
    private void setPosition(CustomImageView v) {
        int idx = getIdxTerdekat(v);
        v.setX(coordinatX[idx]);
        v.setY(100);
    }

    /**
     * Dipanggil 1x saja
     * Digunakan untuk mengisi nilai array coordinatX saja
     */
    private int posisiX(int indeks) {
        int currX = 110;
        if (indeks == 0) {
            return currX;
        } else {
            for (int i = 0; i < indeks; i++) {
                //operand
                if (i % 2 == 0) {
                    currX += OPERAND_WIDTH + 10;
                }
                //operator
                else {
                    currX += OPERATOR_WIDTH + 10;
                }
            }
        }
        return currX;
    }

    /**
     * param 1 : view yang lagi di tekan
     * Akan mencari selisih terdekat dari array coordinatX dengan coordinat x view yang di tekan
     * Kembalian berupa index coordinatX yang terdekat
     */
    public int getIdxTerdekat(View v) {
        int min = Integer.MAX_VALUE;
        int idx = -1;
        if (!((CustomImageView) v).getIsOperator()) {
            for (int i = 0; i < coordinatX.length; i += 2) {
                int selisih = Math.abs((int) (v.getX() - coordinatX[i]));
                if (min > selisih) {
                    min = selisih;
                    idx = i;
                }
            }
        } else {
            for (int i = 1; i < coordinatX.length; i += 2) {
                int selisih = Math.abs((int) (v.getX() - coordinatX[i]));
                if (min > selisih) {
                    min = selisih;
                    idx = i;
                }
            }
        }
        return idx;
    }

    /**
     * param 1: index yang akan di cek
     * kalau onCalculationList dengan indeks ke idx == null, berarti kosong
     */
    public boolean isKosong(int idx) {
        return onCalculationList[idx] == null;
    }
}
