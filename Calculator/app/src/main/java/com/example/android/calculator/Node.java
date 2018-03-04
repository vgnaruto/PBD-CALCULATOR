package com.example.android.calculator;

/**
 * Created by toshiba pc on 3/3/2018.
 */

public class Node {
    public String text;
    public float cX,cY;
    public float width,height;
    public int id;

    public Node(String text, float cX, float cY, float width, float height, int id) {
        this.text = text;
        this.cX = cX;
        this.cY = cY;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getX() {
        return cX;
    }

    public void setX(float cX) {
        this.cX = cX;
    }

    public float getY() {
        return cY;
    }

    public void setY(float cY) {
        this.cY = cY;
    }

    public boolean getIsOperator(){
        return id == 1;
    }
}
