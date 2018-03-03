package com.example.android.calculator;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
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
    private ImageView trashZone;

    private GestureDetector mGD;

    private int[] coordinatX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        instances = this;

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
        this.sOperator.setOnTouchListener(this);
        this.etOperand.setOnTouchListener(this);

        //INISIALISASI ATTRIBUT
        this.onCalculationList = new CustomImageView[9];
        this.mGD = new GestureDetector(this,new CustomGestureDetector());
        this.coordinatX = new int[9];
        for (int i = 0; i < coordinatX.length; i++) {
            coordinatX[i] = posisiX(i);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View view) {
//        Log.d("MainActivity", "clicked");
        if (view == bAddOperand) {
            if (!etOperand.getText().toString().equalsIgnoreCase("")) {
                CustomImageView newTv = createOp(etOperand.getText().toString(), 0);
                newTv.setOnTouchListener(this);
                rLayout.addView(newTv);
                etOperand.setText("");
            }
        } else if (view == bAddOperator) {
            CustomImageView newTv = createOp(sOperator.getSelectedItem().toString(), 1);
            newTv.setOnTouchListener(this);
            rLayout.addView(newTv);
        } else if (view == bCalculate) {
            LinkedList<CustomImageView> llList = new LinkedList<>();
            for (CustomImageView civ : onCalculationList) {
                if (civ == null) {
                    llList.add(null);
                } else {
                    llList.add(civ);
                }
            }
            if (Calculator.getAnswer(llList)) {
                CustomImageView civ = createOp((int) Double.parseDouble("" + Calculator.result) + "", 0);
                civ.setOnTouchListener(this);
                for (CustomImageView civ2 : onCalculationList) {
                    remove(civ2);
                    rLayout.removeView(civ2);
                }
                rLayout.addView(civ);
//                Log.d("CALCULATE",""+Calculator.result);
            } else {
                Toast toast = Toast.makeText(this, "Data tidak valid", Toast.LENGTH_LONG);
                toast.show();
//                Log.d("CALCULATE", "TIDAK VALID");
            }
        }
    }

    @SuppressLint("NewApi")
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
                tv.setBackground(getResources().getDrawable(R.drawable.operand_border));
                paint.setTextSize(45f);
                tv.setIsOperator(false);
                break;
            case 1:
                tv.setBackground(getResources().getDrawable(R.drawable.operator_border));
                paint.setTextSize(100f);
                tv.setIsOperator(true);
                break;
        }
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getResources().getColor(R.color.black));
        paint.setAntiAlias(true);
        canvas.drawText(text, x, y, paint);
        float randX = (float) Math.random() * (WIDTH - 600) + 300;
        float randY = (float) (Math.random()) * (HEIGHT - 1000) + 400;
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view != this.etOperand && view != this.sOperator) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    //kalau di area atas
                    if (view.getY() <= 250) {
                        //kalau view sudah ada di slot
                        if (MainActivity.getInstances().contains(view)) {
                            int idx = MainActivity.getInstances().getIdxTerdekat(view);
                            //kalau slot baru masih kosong
                            if (MainActivity.getInstances().isKosong(idx)) {
                                MainActivity.getInstances().remove(view);
                                MainActivity.getInstances().add((CustomImageView) view);
                            }
                            //kalau slot baru tidak kosong
                            else {
                                float tempX = (float) Math.random() * (MainActivity.WIDTH - 400) + 200;
                                float tempY = (float) (Math.random()) * (MainActivity.HEIGHT - 600) + 200;
                                view.setX(tempX);
                                view.setY(tempY);
                                MainActivity.getInstances().remove(view);
                            }
                        }
                        //kalau view belum di slot
                        else {
                            //kalau slot baru tidak kosong
                            if (!MainActivity.getInstances().isKosong(MainActivity.getInstances().getIdxTerdekat(view))) {
                                float tempX = (float) Math.random() * (MainActivity.WIDTH - 400) + 200;
                                float tempY = (float) (Math.random()) * (MainActivity.HEIGHT - 600) + 200;
                                view.setX(tempX);
                                view.setY(tempY);
                            }
                            //kalau slot baru kosong
                            else {
                                MainActivity.getInstances().add((CustomImageView) view);
                            }
                        }
                    }
                    //kalau bukan di area atas
                    else {
                        //kalau view udah ada di array
                        if (MainActivity.getInstances().contains(view)) {
                            MainActivity.getInstances().remove(view);
                        }
                        //kalau view belum ada di array
                        else {
                            //do nothing
                        }
                    }

                    if (view.getX() <= 0) {
                        view.setX(1);
                    }
                    if (view.getX() + view.getWidth() >= MainActivity.WIDTH) {
                        view.setX(MainActivity.WIDTH - view.getWidth() - 1);
                    }
                    if (view.getY() <= 0) {
                        view.setY(1);
                    }
                    if (view.getY() + view.getHeight() >= MainActivity.HEIGHT) {
                        view.setY(MainActivity.HEIGHT - view.getHeight() - 1);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
//                Log.d("CALCULATOR",trashZone.getX()+" "+trashZone.getWidth()+" "+trashZone.getY()+" "+trashZone.getHeight());
//                Log.d("CALCULATOR",view.getX()+(view.getWidth()/2)+" "+view.getY()+(view.getHeight()/2));
//                Log.d("CALCULATOR",
//                        "getX > tzGetX: "+(view.getX()+(view.getWidth()/2) > trashZone.getX())
//                                +"\ngetX<tzGetX+width"+(view.getX()+(view.getWidth()/2) < trashZone.getX()+trashZone.getWidth())
//                                +"\ngetY>tzGetY: "+(view.getY()+(view.getHeight()/2) > trashZone.getY())
//                                +"\ngetY<tzGetY+height: "+(view.getY()+(view.getHeight()/2) < trashZone.getY()+trashZone.getHeight()));

                    if (view.getX() + (view.getWidth() / 2) > MainActivity.getInstances().getTrashZone().getX() && view.getX() + (view.getWidth() / 2) < (MainActivity.getInstances().getTrashZone().getX() + MainActivity.getInstances().getTrashZone().getWidth()) &&
                            view.getY() + (view.getHeight() / 2) > MainActivity.getInstances().getTrashZone().getY() && view.getY() + (view.getHeight() / 2) < (MainActivity.getInstances().getTrashZone().getY() + MainActivity.getInstances().getTrashZone().getHeight())) {
//                    Log.d("CALCULATOR-TRASH_ZONE","MASUK");
                        if (MainActivity.getInstances().contains(view)) {
                            MainActivity.getInstances().remove(view);
                        }
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    //JARI MENGGERAKAN VIEW DI BAGIAN TENGAH VIEW SEBAGAI TITIK PUSATNYA
                    if (view.getX() > 0 && view.getX() + view.getWidth() < MainActivity.WIDTH && view.getY() > 0 && view.getY() < MainActivity.HEIGHT) {
                        view.setX(motionEvent.getRawX() - view.getWidth() / 2.0f);
                        view.setY(motionEvent.getRawY() - view.getHeight());
                    } else {
                        if (view.getX() <= 0) {
                            view.setX(1);
                        }
                        if (view.getX() + view.getWidth() >= MainActivity.WIDTH) {
                            view.setX(MainActivity.WIDTH - view.getWidth() - 1);
                        }
                        if (view.getY() <= 0) {
                            view.setY(1);
                        }
                        if (view.getY() + view.getHeight() >= MainActivity.HEIGHT) {
                            view.setY(MainActivity.HEIGHT - view.getHeight() - 1);
                        }
                    }
                    break;
            }
        }else if(view == sOperator || view == etOperand){
            return mGD.onTouchEvent(motionEvent);
        }
        return true;
    }

    public EditText getEditText(){
        return this.etOperand;
    }
    public Spinner getSpinner(){
        return this.sOperator;
    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public void onLongPress(MotionEvent e) {
//            Log.d("LONGPRESS",e.getRawX()+" "+e.getRawY()+
//                    "\n"+etOperand.getX()+" "+(etOperand.getX()+etOperand.getWidth())+
//                    "\n"+etOperand.getY()+" "+(etOperand.getY()+etOperand.getHeight()));
//            Log.d("LONGPRESS",e.getRawX()+" "+e.getRawY()+
//                    "\n"+sOperator.getX()+" "+(sOperator.getX()+sOperator.getWidth())+
//                    "\n"+sOperator.getY()+" "+(sOperator.getY()+sOperator.getHeight()));
            //EDIT TEXT OPERAND
            if(e.getRawX() >= etOperand.getX() && e.getRawX() <= etOperand.getX() + etOperand.getWidth()){
                if((e.getRawY()-100) >= etOperand.getY() && (e.getRawY()-100) <= etOperand.getY() + etOperand.getHeight()){
                    if(!etOperand.getText().toString().equalsIgnoreCase("")) {
                        CustomImageView newTv = createOp(etOperand.getText().toString(), 0);
                        newTv.setX(e.getRawX());
                        newTv.setY(e.getRawY()-100);
                        newTv.setOnTouchListener(MainActivity.getInstances());
                        rLayout.addView(newTv);
                        etOperand.setText("");
                    }
                }
            }
            //SPINNER
            else if(e.getRawX() >= sOperator.getX() && e.getRawX() <= sOperator.getX() + sOperator.getWidth()){
                if((e.getRawY()-75) >= sOperator.getY() && (e.getRawY()-75) <= sOperator.getY() + sOperator.getHeight()){
                    CustomImageView newTv = createOp(sOperator.getSelectedItem().toString(), 1);
                    newTv.setX(e.getRawX());
                    newTv.setY(e.getRawY()-75);
                    newTv.setOnTouchListener(MainActivity.getInstances());
                    rLayout.addView(newTv);
                }
            }
        }
    }
}
