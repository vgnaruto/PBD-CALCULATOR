package com.example.android.calculator;

import android.util.Log;
import android.view.GestureDetector;
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

        return true;
    }
}
