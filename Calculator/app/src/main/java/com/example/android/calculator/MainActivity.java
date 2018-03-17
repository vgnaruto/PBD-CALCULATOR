package com.example.android.calculator;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static int WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    public static int longClicked = 0;
    public static Node currentN;
    private final int OPERAND_WIDTH = 246;
    private final int OPERATOR_WIDTH = 84;
    private static MainActivity instances;

    private ImageButton bAddOperator, bAddOperand, bCalculate;
    private EditText etOperand;
    private Spinner sOperator;
    private RelativeLayout rLayout;
    private ArrayAdapter<CharSequence> adapter;
    private ImageView trashZone;
    private CheckBox cbPrecedence;
    private CustomTextView tvResult;
    private LinearLayout llResult;
    private TextView topTv;
    private View hiddenSpinner;

    private ImageView ivCanvas;
    private Canvas mCanvas;

    private ArrayList<Node> nodes;
    private Node[] onCalculationList;
    private int[] coordinatX;

    private GestureDetector mGestureDetector;

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
        this.ivCanvas = findViewById(R.id.iv_canvas);
        this.cbPrecedence = findViewById(R.id.cbPrecedence);
        this.tvResult = findViewById(R.id.tvResult);
        this.llResult = findViewById(R.id.llResult);
        this.topTv = findViewById(R.id.topTv);
        this.hiddenSpinner = findViewById(R.id.hiddenSpinner);

        //ADAPTER SPINNER
        this.adapter = ArrayAdapter.createFromResource(this, R.array.operator, android.R.layout.simple_spinner_item);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sOperator.setAdapter(adapter);

        //LISTENER
        this.bAddOperand.setOnClickListener(this);
        this.bAddOperator.setOnClickListener(this);
        this.bCalculate.setOnClickListener(this);
        this.hiddenSpinner.setOnClickListener(this);
        this.ivCanvas.setOnTouchListener(this);
        this.tvResult.setOnTouchListener(this);
        this.etOperand.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Log.d("EDITTEXT","LONGPRESS");
                draw(etOperand.getText().toString(), 0, view.getX() + (view.getWidth() / 2), view.getY());
                return false;
            }
        });
        this.hiddenSpinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Log.d("EDITTEXT","SPINNER");
                draw(sOperator.getSelectedItem().toString(), 1, view.getX() + (view.getWidth() / 2), view.getY());
                longClicked = 1;
                return false;
            }
        });


        //INISIALISASI ATTRIBUT
        nodes = new ArrayList<>();
        this.onCalculationList = new Node[9];
        this.coordinatX = new int[9];
        for (int i = 0; i < coordinatX.length; i++) {
            coordinatX[i] = posisiX(i);
        }
        this.mGestureDetector = new GestureDetector(this, new CGTResult());
    }

    /**
     * bakal dipanggil pas on create selesai di lakuin.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mCanvas == null) {
            if (hasFocus) {
                //INISIALISASI CANVAS
                Bitmap bitmap = Bitmap.createBitmap(ivCanvas.getWidth(), ivCanvas.getHeight(), Bitmap.Config.ARGB_8888);
                this.ivCanvas.setImageBitmap(bitmap);
                this.mCanvas = new Canvas(bitmap);
                resetCanvas();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View view) {
//        Log.d("MainActivity", "clicked");
        if (view == bAddOperand) {
            if (!etOperand.getText().toString().equalsIgnoreCase("")) {
                draw(etOperand.getText().toString(), 0);
            }
        } else if (view == hiddenSpinner) {
            if (longClicked == 1) {
                longClicked = 0;
            } else {
                sOperator.performClick();
            }
        } else if (view == bAddOperator) {
            draw(sOperator.getSelectedItem().toString(), 1);
        } else if (view == bCalculate) {
            LinkedList<Node> llList = new LinkedList<>();
            for (Node civ : onCalculationList) {
                if (civ == null) {
                    llList.add(null);
                } else {
                    llList.add(civ);
                }
            }
            if (cbPrecedence.isChecked()) {
                String token = Calculator.listToString(llList);
                if (Calculator.calculate(token)) {
                    resetCanvas();
//                    draw((int) Double.parseDouble("" + Calculator.result) + "", 0);
                    tvResult.setText(Calculator.resultString);
                    tvResult.setOnCalculationList(onCalculationList);
                    for (Node civ2 : onCalculationList) {
                        markRemove(civ2);
                    }
                    onCalculationList = new Node[9];
                    for (Node n : nodes) {
                        redraw(n);
                    }
                } else {
                    Toast toast = Toast.makeText(this, "Data tidak valid", Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                if (Calculator.getAnswer(llList)) {
                    resetCanvas();
//                    draw((int) Double.parseDouble("" + Calculator.result) + "", 0);
                    tvResult.setText(Calculator.resultString);
                    tvResult.setOnCalculationList(onCalculationList);
                    for (Node civ2 : onCalculationList) {
                        markRemove(civ2);
                    }
                    onCalculationList = new Node[9];
                    for (Node n : nodes) {
                        redraw(n);
                    }
//                Log.d("CALCULATE",""+Calculator.result);
                } else {
                    Toast toast = Toast.makeText(this, "Data tidak valid", Toast.LENGTH_LONG);
                    toast.show();
//                Log.d("CALCULATE", "TIDAK VALID");
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == ivCanvas) {
//        Node temp = nodes.get(0);
//        Log.d("ONTOUCH", motionEvent.getX() + " " + motionEvent.getY() +
//                "\n" + temp.getX() + " " + (temp.getX() + temp.getWidth()) +
//                "\n" + temp.getY() + " " + (temp.getY() + temp.getHeight()));
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (Node n : nodes) {
                        if (motionEvent.getX() >= n.getX() && motionEvent.getX() <= n.getX() + n.getWidth()) {
                            if (motionEvent.getY() >= n.getY() && motionEvent.getY() <= n.getY() + n.getHeight()) {
                                currentN = n;
                                if(contains(currentN)){
                                    removeFromList(currentN);
                                }
                                break;
                            }
                        }
                    }
                    if(currentN != null){
                        nodes.remove(currentN);
                        nodes.add(currentN);
                    }
//                Log.d("ONTOUCH", "DOWN " + (currentN != null));
                    break;
                case MotionEvent.ACTION_MOVE:
//                    Log.d("KOORDINAT","meX: "+motionEvent.getRawX()+"||meY: "+motionEvent.getRawY()+
//                            "\nllX: "+llResult.getX()+"llWidth: "+llResult.getWidth()+
//                            "\nllY: "+llResult.getY()+"llHeight: "+llResult.getHeight());
//                Log.d("ONTOUCH", "MOVE");
                    if (currentN != null) {
                        //cek x layar
                        if (motionEvent.getX() - (currentN.getWidth() / 2) > 0 && motionEvent.getX() + (currentN.getWidth() / 2) < ivCanvas.getWidth()) {
                            //cek y layar
                            if (motionEvent.getY() - currentN.getHeight() > 0 && motionEvent.getY() < ivCanvas.getHeight()) {
                                //cek x , y precedence
                                if (motionEvent.getX() - (currentN.getWidth() / 2) > cbPrecedence.getX() + cbPrecedence.getWidth() + 10
                                        || motionEvent.getY() - currentN.getHeight() > cbPrecedence.getY() + cbPrecedence.getHeight() + 10
                                        || motionEvent.getY() < cbPrecedence.getY() - 10) {
                                    //cek x , y llResult
                                    if (motionEvent.getX() + (currentN.getWidth() / 2) < llResult.getX() - 10
                                            || motionEvent.getY() - currentN.getHeight() > llResult.getY() + llResult.getHeight() + 10
                                            || motionEvent.getY() < llResult.getY() - 10) {
                                        resetCanvas();
                                        for (Node n2 : nodes) {
                                            if (n2 == currentN) {
                                                redraw(n2, motionEvent.getX(), motionEvent.getY());
                                            } else {
                                                redraw(n2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                Log.d("ONTOUCH", "UP");
                    //kalau di area atas
                    if (currentN != null) {
                        if (currentN.getY() <= 250) {
                            //kalau view sudah ada di slot
                            if (contains(currentN)) {
                                int idx = getIdxTerdekat(currentN);
                                //kalau slot baru masih kosong
                                if (isKosong(idx)) {
                                    removeFromList(currentN);
                                    add(currentN);
                                }
                                //kalau slot baru tidak kosong
                                else {
                                    float tempX = (float) Math.random() * (ivCanvas.getWidth() - tvResult.getWidth() - cbPrecedence.getWidth() - 400) + cbPrecedence.getWidth();
                                    float tempY = (float) (Math.random()) * (ivCanvas.getHeight() - topTv.getHeight() - bAddOperand.getHeight() - 125) + topTv.getHeight();
                                    currentN.setX(tempX);
                                    currentN.setY(tempY);
                                    removeFromList(currentN);
                                    resetCanvas();
                                    for (Node n : nodes) {
                                        redraw(n);
                                    }
                                }
                            }
                            //kalau view belum di slot
                            else {
                                //kalau slot baru tidak kosong
                                if (!isKosong(getIdxTerdekat(currentN))) {
                                    float tempX = (float) Math.random() * (ivCanvas.getWidth() - tvResult.getWidth() - cbPrecedence.getWidth() - 400) + cbPrecedence.getWidth();
                                    float tempY = (float) (Math.random()) * (ivCanvas.getHeight() - topTv.getHeight() - bAddOperand.getHeight() - 125) + topTv.getHeight();
                                    currentN.setX(tempX);
                                    currentN.setY(tempY);
                                    resetCanvas();
                                    for (Node n : nodes) {
                                        redraw(n);
                                    }
                                }
                                //kalau slot baru kosong
                                else {
                                    add(currentN);
                                }
                            }
                        }
                        //kalau bukan di area atas
                        else {
                            //kalau view udah ada di array
                            if (contains(currentN)) {
                                removeFromList(currentN);
                                for (Node n2 : nodes) {
                                    redraw(n2);
                                }
                            }
                            //kalau view belum ada di array
                            else {
                                //gambar ulang
                                resetCanvas();
                                for (Node n2 : nodes) {
                                    redraw(n2);
                                }
                            }
                        }
                        if (currentN.cX+(currentN.getWidth()/2) >=  trashZone.getX() && currentN.cX+currentN.getWidth()/2 <= trashZone.getX() + trashZone.getWidth()) {
                            if (currentN.cY+(currentN.getHeight()/2) >= trashZone.getY() && currentN.cY+(currentN.getHeight()/2) <= trashZone.getY() + trashZone.getHeight()) {
                                resetCanvas();
                                for (Node n : nodes) {
                                    if (n == currentN) {
                                        nodes.remove(n);
                                        break;
                                    }
                                }
                                for (Node n : nodes) {
                                    redraw(n);
                                }
                            }
                        }
                    }
                    currentN = null;
                    break;
            }
        } else if (view == tvResult) {
            return mGestureDetector.onTouchEvent(motionEvent);
        }
        return true;
    }

    /**
     * mereset canvas menjadi kosong
     */
    public void resetCanvas() {
        mCanvas.drawColor(getResources().getColor(R.color.lightOrange));
        drawSlot();
        this.ivCanvas.invalidate();
    }

    /**
     * @param1 : text yang bakal di tampilih
     * @param2 : id node. 0 = operand, 1 = operator
     * @param3 : x jari
     * @param4 : y jari
     * Untuk menambahkan node baru
     */
    private void draw(String text, int id, float currentX, float currentY) {
        this.etOperand.setText("");
        Paint paint = new Paint();
        Paint fillPaint = new Paint();
        Paint strokePaint = new Paint();
        float w = 0;
//        float h = 100;
        float h = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (100/3.5f),getResources().getDisplayMetrics()));
        switch (id) {
            case 0:
                //paint.setTextSize(35f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (40f/3.5f),getResources().getDisplayMetrics())));
                fillPaint.setColor(getResources().getColor(R.color.orange));
//                w = 246;
                w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (296/3.5f),getResources().getDisplayMetrics()));
                break;
            case 1:
                //paint.setTextSize(90f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (70f/3.5f),getResources().getDisplayMetrics())));
                fillPaint.setColor(getResources().getColor(R.color.darkLightOrange));
//                w = 84;
                w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (104/3.5f),getResources().getDisplayMetrics()));
                break;
        }
        paint.setColor(getResources().getColor(R.color.black));
        strokePaint.setColor(getResources().getColor(R.color.black));
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(10f);
        float currX = currentX - (w / 2);
        float currY = currentY - h;
//        Log.d("ONTOUCH", randX + " " + randY);
        RectF nRect = new RectF(currX, currY, currX + w, currY + h);
        mCanvas.drawRoundRect(nRect, 30, 30, fillPaint);
        mCanvas.drawRoundRect(nRect, 30, 30, strokePaint);
        mCanvas.drawText(text, currX + (w / 2) - ((paint.measureText(text)) / 2), currY + (h / 2)-10 + (paint.getTextSize() / 2), paint);
        nodes.add(new Node(text, currX, currY, w, h, id));
        ivCanvas.invalidate();
    }

    /**
     * @param1 : text yang bakal di tampilih
     * @param2 : id node. 0 = operand, 1 = operator
     * Untuk menambahkan node baru
     */
    private void draw(String text, int id) {
        this.etOperand.setText("");
        Paint paint = new Paint();
        Paint fillPaint = new Paint();
        Paint strokePaint = new Paint();
        float w = 0;
//        float h = 100;
        float h = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (100/3.5f),getResources().getDisplayMetrics()));
        switch (id) {
            case 0:
                //paint.setTextSize(35f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (40f/3.5f),getResources().getDisplayMetrics())));
                fillPaint.setColor(getResources().getColor(R.color.orange));
                w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (296/3.5f),getResources().getDisplayMetrics()));
//                w = 246;
                break;
            case 1:
                //paint.setTextSize(90f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (70f/3.5f),getResources().getDisplayMetrics())));
                fillPaint.setColor(getResources().getColor(R.color.darkLightOrange));
                w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (104/3.5f),getResources().getDisplayMetrics()));
//                w = 84;
                break;
        }
        paint.setColor(getResources().getColor(R.color.black));
        strokePaint.setColor(getResources().getColor(R.color.black));
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(10f);
        float randX = (float) Math.random() * (ivCanvas.getWidth() - tvResult.getWidth() - cbPrecedence.getWidth() - 400) + cbPrecedence.getWidth();
        float randY = (float) (Math.random()) * (ivCanvas.getHeight() - topTv.getHeight() - bAddOperand.getHeight() - 125) + topTv.getHeight();
//        Log.d("ONTOUCH", randX + " " + randY);
        RectF nRect = new RectF(randX, randY, randX + w, randY + h);
        mCanvas.drawRoundRect(nRect, 30, 30, fillPaint);
        mCanvas.drawRoundRect(nRect, 30, 30, strokePaint);
        mCanvas.drawText(text, randX + (w / 2) - ((paint.measureText(text)) / 2), randY + (h / 2)-10 + (paint.getTextSize() / 2), paint);
        nodes.add(new Node(text, randX, randY, w, h, id));
        ivCanvas.invalidate();
    }

    /**
     * @param1 : node yang di pilih
     * menggambar ulang node dengan posisi yang sama
     */
    public void redraw(Node n) {
        if (n.id != -1) {
            Paint paint = new Paint();
            Paint fillPaint = new Paint();
            Paint strokePaint = new Paint();
            float w = 0;
//            float h = 100;
            float h = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (100/3.5f),getResources().getDisplayMetrics()));
            switch (n.id) {
                case 0:
                    //paint.setTextSize(35f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (40f/3.5f),getResources().getDisplayMetrics())));
                    fillPaint.setColor(getResources().getColor(R.color.orange));
//                    w = 246;
                    w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (296/3.5f),getResources().getDisplayMetrics()));
                    break;
                case 1:
                    //paint.setTextSize(90f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (70f/3.5f),getResources().getDisplayMetrics())));
                    fillPaint.setColor(getResources().getColor(R.color.darkLightOrange));
//                    w = 84;
                    w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (104/3.5f),getResources().getDisplayMetrics()));
                    break;
            }
            paint.setColor(getResources().getColor(R.color.black));
            strokePaint.setColor(getResources().getColor(R.color.black));
            fillPaint.setStyle(Paint.Style.FILL);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(10f);
            RectF nRect = new RectF(n.cX, n.cY, n.cX + w, n.cY + h);
            mCanvas.drawRoundRect(nRect, 30, 30, fillPaint);
            mCanvas.drawRoundRect(nRect, 30, 30, strokePaint);
            mCanvas.drawText(n.text, n.cX + (w / 2) - ((paint.measureText(n.text)) / 2), n.cY + (h / 2)-10 + (paint.getTextSize() / 2), paint);
            ivCanvas.invalidate();
        }
    }

    /**
     * @param1 : node yang akan di redraw
     * @param2 : posisi X node baru
     * @param3 : posisi Y node baru
     * Menggambar ulang node dengan posisi berbeda
     */
    public void redraw(Node n, float currX, float currY) {
        if (n.id != -1) {
            Paint paint = new Paint();
            Paint fillPaint = new Paint();
            Paint strokePaint = new Paint();
            float w = 0;
//            float h = 100;
            float h = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (100/3.5f),getResources().getDisplayMetrics()));
            switch (n.id) {
                case 0:
                    //paint.setTextSize(35f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (40f/3.5f),getResources().getDisplayMetrics())));
                    fillPaint.setColor(getResources().getColor(R.color.orange));
//                    w = 246;
                    w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (296/3.5f),getResources().getDisplayMetrics()));
                    break;
                case 1:
                    //paint.setTextSize(90f);
                    paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (70f/3.5f),getResources().getDisplayMetrics())));
                    fillPaint.setColor(getResources().getColor(R.color.darkLightOrange));
//                    w = 84;
                    w = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (104/3.5f),getResources().getDisplayMetrics()));
                    break;
            }
            paint.setColor(getResources().getColor(R.color.lightBlue));
            strokePaint.setColor(getResources().getColor(R.color.lightBlue));
            fillPaint.setStyle(Paint.Style.FILL);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(10f);
            RectF nRect = new RectF(currX - (w / 2)-10, currY - h-10, currX + (w / 2)+10, currY+10);
            mCanvas.drawRoundRect(nRect, 30, 30, fillPaint);
            mCanvas.drawRoundRect(nRect, 30, 30, strokePaint);
            mCanvas.drawText(n.text, currX - ((paint.measureText(n.text)) / 2), currY - (h / 2)-10 + (paint.getTextSize() / 2), paint);
            n.cX = currX - (w / 2);
            n.cY = currY - h;
            ivCanvas.invalidate();
        }
    }

    /**
     * Dipanggil 1x saja
     * Digunakan untuk mengisi nilai array coordinatX saja
     */
    private int posisiX(int indeks) {
        int currX = 150;
        if (indeks == 0) {
            return currX;
        } else {
            for (int i = 0; i < indeks; i++) {
                //operand
                if (i % 2 == 0) {
//                    currX += OPERAND_WIDTH + 10;
                    currX += Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (296/3.5f),getResources().getDisplayMetrics())) + Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (10/3.5f),getResources().getDisplayMetrics()));
                }
                //operator
                else {
//                    currX += OPERATOR_WIDTH + 10;
                    currX += Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (104/3.5f),getResources().getDisplayMetrics())) + Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (10/3.5f),getResources().getDisplayMetrics()));
                }
            }
        }
        return currX;
    }

    /**
     * menggambar slot
     */
    private void drawSlot() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.darkLightOrange));
        for (int i = 0; i < coordinatX.length; i++) {
            float y = 100;
            float x = coordinatX[i];
            float height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (100/3.5f),getResources().getDisplayMetrics()));
            Log.d("HEIGHT",""+height);
            float width;
            if (i % 2 == 0) {
                width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (296/3.5f),getResources().getDisplayMetrics()));
            } else {
                width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (104/3.5f),getResources().getDisplayMetrics()));
            }
            RectF nRect = new RectF(x, y, x + width, y + height);
            mCanvas.drawRoundRect(nRect, 30, 30, paint);
        }
    }

    public boolean contains(Node n) {
        for (Node view : onCalculationList) {
            if (view == n) {
                return true;
            }
        }
        return false;
    }

    /**
     * param 1 : node yang lagi di tekan
     * Akan mencari selisih terdekat dari array coordinatX dengan coordinat x node yang di tekan
     * Kembalian berupa index coordinatX yang terdekat
     */
    public int getIdxTerdekat(Node n) {
        int min = Integer.MAX_VALUE;
        int idx = -1;
        if (!n.getIsOperator()) {
            for (int i = 0; i < coordinatX.length; i += 2) {
                int selisih = Math.abs((int) (n.getX() - coordinatX[i]));
                if (min > selisih) {
                    min = selisih;
                    idx = i;
                }
            }
        } else {
            for (int i = 1; i < coordinatX.length; i += 2) {
                int selisih = Math.abs((int) (n.getX() - coordinatX[i]));
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

    /**
     * Membuang node dari array.
     */
    public void removeFromList(Node n) {
        for (int i = 0; i < onCalculationList.length; i++) {
            if (n == onCalculationList[i]) {
                onCalculationList[i] = null;
                break;
            }
        }
    }

    /**
     * untuk mengecek apakah onCalculationList kosong atau tidak.
     */
    public boolean isEmpty() {
        for (Node n : onCalculationList) {
            if (n != null) {
                return false;
            }
        }
        return true;
    }

    public void markRemove(Node n) {
        for (Node ns : nodes) {
            if (ns == n) {
                n.id = -1;
            }
        }
    }

    /**
     * Param 1 : View yang lagi ditekan
     * Berguna untuk menambahkan view ke dalam array
     * View yang masuk ke dalam array hanya view yang valid
     * View yang valid maksudnya slot tempat view akan dimasukkan masih kosong
     */
    public boolean add(Node n) {
        onCalculationList[getIdxTerdekat(n)] = n;
        setPosition(n);
        resetCanvas();
        for (Node n2 : nodes) {
            redraw(n2);
        }
        return true;
    }

    /**
     * Param 1 : View yang lagi ditekan
     * Berguna untuk meletakan view ke dalam slot terdekatnya.
     */
    private void setPosition(Node n) {
        int idx = getIdxTerdekat(n);
        n.setX(coordinatX[idx]);
        n.setY(100);
    }

    private class CGTResult implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
//            Log.d("GESTURE","ON SINGLE TAP UP");
            if (!tvResult.getText().toString().equalsIgnoreCase("n/a")) {
                draw(tvResult.getText().toString(), 0);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
//            Log.d("GESTURE", "LONG PRESS");
//          masih belum bisa, salah recognizing koordinat x-nya
//            Log.d("KOORDINAT","meX: "+motionEvent.getRawX()+"||meY: "+motionEvent.getRawY()+
//                    "\nllX: "+llResult.getX()+"llWidth: "+llResult.getWidth()+
//                    "\nllY: "+llResult.getY()+"llHeight: "+llResult.getHeight());

            if (!tvResult.getText().toString().equalsIgnoreCase("n/a")) {
                if (isEmpty()) {
                    Node[] historyNodes = tvResult.getOnCalculationList();
                    for (int i = 0; i < historyNodes.length; i++) {
                        if (historyNodes[i] != null) {
                            Log.d("GESTURE", "MASUK" + " " + historyNodes[i].id);
                            nodes.add(historyNodes[i]);
                            add(historyNodes[i]);
                        }
                    }
                    tvResult.setText("n/a");
                } else {
                    Toast toast = Toast.makeText(MainActivity.instances, "Harap kosongkan slot di atas!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

//            if(motionEvent.getRawX() >= llResult.getX() && motionEvent.getRawX() <= llResult.getWidth() + llResult.getX()){
//                if(motionEvent.getRawY() >= llResult.getY() && motionEvent.getRawY() <= llResult.getHeight() + llResult.getY()){
//                    if (!tvResult.getText().toString().equalsIgnoreCase("n/a")) {
//                        if(isEmpty()) {
//                            Node[] historyNodes = tvResult.getOnCalculationList();
//                            for (int i = 0; i < historyNodes.length; i++) {
//                                if (historyNodes[i] != null) {
//                                    Log.d("GESTURE", "MASUK" + " " + historyNodes[i].id);
//                                    nodes.add(historyNodes[i]);
//                                    add(historyNodes[i]);
//                                }
//                            }
//                            tvResult.setText("n/a");
//                        }else{
//                            Toast toast = Toast.makeText(MainActivity.instances, "Harap kosongkan slot di atas!", Toast.LENGTH_LONG);
//                            toast.show();
//                        }
//                    }
//                }
//            }
//            else if(motionEvent.getRawX() >= sOperator.getX() && motionEvent.getRawX() <= sOperator.getWidth() + sOperator.getX()){
//                if(motionEvent.getRawY() >= sOperator.getY() && motionEvent.getRawY() <= sOperator.getHeight() + sOperator.getY()){
//                    draw(sOperator.getSelectedItem().toString(), 1);
//                }
//            }
//            else if(motionEvent.getRawX() >= etOperand.getX() && motionEvent.getRawX() <= etOperand.getWidth() + etOperand.getX()){
//                if(motionEvent.getRawY() >= etOperand.getY() && motionEvent.getRawY() <= etOperand.getHeight() + etOperand.getY()){
//                    draw(etOperand.getText().toString(), 0);
//                }
//            }
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }
}
