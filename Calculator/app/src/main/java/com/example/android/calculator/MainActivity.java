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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static final int WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
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

    private ImageView ivCanvas;
    private Canvas mCanvas;

    private ArrayList<Node> nodes;
    private Node[] onCalculationList;
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
        this.ivCanvas = findViewById(R.id.iv_canvas);
        this.cbPrecedence = findViewById(R.id.cbPrecedence);

        //ADAPTER SPINNER
        this.adapter = ArrayAdapter.createFromResource(this, R.array.operator, android.R.layout.simple_spinner_item);
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sOperator.setAdapter(adapter);

        //LISTENER
        this.bAddOperand.setOnClickListener(this);
        this.bAddOperator.setOnClickListener(this);
        this.bCalculate.setOnClickListener(this);
        this.ivCanvas.setOnTouchListener(this);

        //INISIALISASI ATTRIBUT
        nodes = new ArrayList<>();
        this.onCalculationList = new Node[9];
        this.coordinatX = new int[9];
        for (int i = 0; i < coordinatX.length; i++) {
            coordinatX[i] = posisiX(i);
        }

        //INISIALISASI CANVAS
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        this.ivCanvas.setImageBitmap(bitmap);
        this.mCanvas = new Canvas(bitmap);

        drawSlot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View view) {
//        Log.d("MainActivity", "clicked");
        if (view == bAddOperand) {
            if (!etOperand.getText().toString().equalsIgnoreCase("")) {
                draw(etOperand.getText().toString(), 0);
                etOperand.setText("");
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
                    drawSlot();
                    draw((int) Double.parseDouble("" + Calculator.result) + "", 0);
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
                    drawSlot();
                    draw((int) Double.parseDouble("" + Calculator.result) + "", 0);
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
//        Node temp = nodes.get(0);
//        Log.d("ONTOUCH", motionEvent.getRawX() + " " + motionEvent.getRawY() +
//                "\n" + temp.getX() + " " + (temp.getX() + temp.getWidth()) +
//                "\n" + temp.getY() + " " + (temp.getY() + temp.getHeight()));
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Node n : nodes) {
                    if (n.id == 1) {
                        if (motionEvent.getX() >= n.getX() && motionEvent.getX() <= n.getX() + n.getWidth()) {
                            if (motionEvent.getY() >= n.getY() && motionEvent.getY() <= n.getY() + n.getHeight()) {
                                currentN = n;
                            }
                        }
                    } else if (n.id == 0) {
                        if (motionEvent.getX() >= n.getX() && motionEvent.getX() <= n.getX() + n.getWidth()) {
                            if (motionEvent.getY() >= n.getY() && motionEvent.getY() <= n.getY() + n.getHeight()) {
                                currentN = n;
                            }
                        }
                    }
                }
//                Log.d("ONTOUCH", "DOWN " + (currentN != null));
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("ONTOUCH", "MOVE");
                if (currentN != null) {
                    resetCanvas();
                    drawSlot();
                    for (Node n2 : nodes) {
                        if (n2 == currentN) {
                            redraw(n2, motionEvent.getX(), motionEvent.getY());
                        } else {
                            redraw(n2);
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
                                float tempX = (float) Math.random() * (MainActivity.WIDTH - 400) + 200;
                                float tempY = (float) (Math.random()) * (MainActivity.HEIGHT - 600) + 200;
                                currentN.setX(tempX);
                                currentN.setY(tempY);
                                removeFromList(currentN);
                                resetCanvas();
                                drawSlot();
                                for (Node n : nodes) {
                                    redraw(n);
                                }
                            }
                        }
                        //kalau view belum di slot
                        else {
                            //kalau slot baru tidak kosong
                            if (!isKosong(getIdxTerdekat(currentN))) {
                                float tempX = (float) Math.random() * (MainActivity.WIDTH - 400) + 200;
                                float tempY = (float) (Math.random()) * (MainActivity.HEIGHT - 600) + 200;
                                currentN.setX(tempX);
                                currentN.setY(tempY);
                                resetCanvas();
                                drawSlot();
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
                        }
                        //kalau view belum ada di array
                        else {
                            //do nothing
                        }
                    }
                    if (currentN.cX >= WIDTH - 300 && currentN.cX <= WIDTH) {
                        if (currentN.cY >= HEIGHT - 300 && currentN.cY <= HEIGHT) {
                            resetCanvas();
                            drawSlot();
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

        return true;
    }

    /**
     * mereset canvas menjadi kosong
     */
    public void resetCanvas() {
        drawSlot();
        mCanvas.drawColor(getResources().getColor(R.color.lightOrange));
        this.ivCanvas.invalidate();
    }

    /**
     * @param1 : text yang bakal di tampilih
     * @param2 : id node. 0 = operand, 1 = operator
     * Untuk menambahkan node baru
     */
    private void draw(String text, int id) {
        Paint paint = new Paint();
        Paint paintBg = new Paint();
        float w = 0;
        float h = 0;
        switch (id) {
            case 0:
                paint.setTextSize(45f);
                paintBg.setColor(getResources().getColor(R.color.orange));
                w = 246;
                break;
            case 1:
                paint.setTextSize(90f);
                paintBg.setColor(getResources().getColor(R.color.darkLightOrange));
                w = 84;
                break;
        }
        h = 100;
        paintBg.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.black));
        float randX = (float) Math.random() * (WIDTH - 600) + 300;
        float randY = (float) (Math.random()) * (HEIGHT - 1000) + 400;
//        mCanvas.drawRect(randX-20, randY-paint.getTextSize(), randX + paint.measureText(text)+20, randY+h, paintBg);
        mCanvas.drawRect(randX, randY, randX + w, randY + h, paintBg);
//        mCanvas.drawText(text, randX, randY-10, paint);
        mCanvas.drawText(text, randX + (w / 2) - ((paint.measureText(text)) / 2), randY + (h / 2) + (paint.getTextSize() / 2), paint);
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
            Paint paintBg = new Paint();
            float w = 0;
            float h = 0;
            switch (n.id) {
                case 0:
                    paint.setTextSize(45f);
                    paintBg.setColor(getResources().getColor(R.color.orange));
                    w = 246;
                    break;
                case 1:
                    paint.setTextSize(90f);
                    paintBg.setColor(getResources().getColor(R.color.darkLightOrange));
                    w = 84;
                    break;
            }
            h = 100;
            paintBg.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.black));
            mCanvas.drawRect(n.cX, n.cY, n.cX + w, n.cY + h, paintBg);
            mCanvas.drawText(n.text, n.cX + (w / 2) - ((paint.measureText(n.text)) / 2), n.cY + (h / 2) + (paint.getTextSize() / 2), paint);
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
            Paint paintBg = new Paint();
            float w = 0;
            float h = 0;
            switch (n.id) {
                case 0:
                    paint.setTextSize(45f);
                    paintBg.setColor(getResources().getColor(R.color.orange));
                    w = 246;
                    break;
                case 1:
                    paint.setTextSize(90f);
                    paintBg.setColor(getResources().getColor(R.color.darkLightOrange));
                    w = 84;
                    break;
            }
            h = 100;
            paintBg.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.black));
            n.cX = currX;
            n.cY = currY;
            mCanvas.drawRect(n.cX, n.cY, n.cX + w, n.cY + h, paintBg);
            mCanvas.drawText(n.text, n.cX + (w / 2) - ((paint.measureText(n.text)) / 2), n.cY + (h / 2) + (paint.getTextSize() / 2), paint);
            ivCanvas.invalidate();
        }
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
     * menggambar slot
     */
    private void drawSlot() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.darkLightOrange));
        for (int i = 0; i < coordinatX.length; i++) {
            float y = 100;
            float x = coordinatX[i];
            float height = 100;
            float width = 0;
            if (i % 2 == 0) {
                width = 246;
            } else {
                width = 84;
            }
            mCanvas.drawRect(x, y, x + width, y + height, paint);
        }
        ivCanvas.invalidate();
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
        drawSlot();
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
}
