package com.example.android.calculator;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by toshiba pc on 2/25/2018.
 */

public class CustomTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                //kalau di area atas
                if (view.getY() <= 250) {
                    CustomTextView currentView = (CustomTextView) view;
                    //view = operator (+,-,x,/)
                    if (currentView.getIsOperator()) {
                        //kalau view udah ada di array
                        if (MainActivity.getInstances().contains(view)) {
                            MainActivity.getInstances().remove(view);
                            MainActivity.getInstances().add(view);
                        }
                        //kalau view belum ada di array
                        else {
                            //kalau slot operator tidak kosong, view dipentalin
                            if (!MainActivity.getInstances().isKosong("OPERATOR")) {
                                float tempX = (float) Math.random() * (MainActivity.WIDTH - 400) + 200;
                                float tempY = (float) (Math.random()) * (MainActivity.HEIGHT - 600) + 200;
                                view.setX(tempX);
                                view.setY(tempY);
                            }
                            //kalau slot operator masih ada yang kosong.
                            else {
                                MainActivity.getInstances().add(view);
                            }
                        }
                    }
                    //view = operand(1,2,3...)
                    else {
                        //kalau view udah ada di array
                        if (MainActivity.getInstances().contains(view)) {
                            MainActivity.getInstances().remove(view);
                            MainActivity.getInstances().add(view);
                        }
                        //kalau view belum ada di array
                        else {
                            //kalau slot operand tidak kosong, view dipentalin.
                            if (!MainActivity.getInstances().isKosong("OPERAND")) {
                                float tempX = (float) Math.random() * (MainActivity.WIDTH - 400) + 200;
                                float tempY = (float) (Math.random()) * (MainActivity.HEIGHT - 600) + 200;
                                view.setX(tempX);
                                view.setY(tempY);
                            }
                            //kalau view sebelumnya operator, di masukin ke sebelah viewnya.
                            else {
                                MainActivity.getInstances().add(view);
                            }
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
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                //JARI MENGGERAKAN VIEW DI BAGIAN TENGAH VIEW SEBAGAI TITIK PUSATNYA
                view.setX(motionEvent.getRawX() - view.getWidth() / 2.0f);
                view.setY(motionEvent.getRawY() - view.getHeight());
                break;
        }
        return true;
    }
}
