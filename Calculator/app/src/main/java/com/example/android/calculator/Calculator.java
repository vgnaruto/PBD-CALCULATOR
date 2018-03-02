package com.example.android.calculator;

import android.util.Log;

import java.util.LinkedList;

/**
 * Kelas ini yang akan mengatur operasi matematika
 * Operasi dasar : +,-,*,/
 * Created by toshiba pc on 2/26/2018.
 */

public class Calculator {
    public static double result = 0;
    public static double calculate(String operand1, String operand2, String operator){
        double op1 = Double.parseDouble(operand1);
        double op2 = Double.parseDouble(operand2);
        double result = 0;
        switch(operator){
            case "+":
                //OPERASI JUMLAH
                result = op1 + op2;
                break;
            case "-":
                //OPERASI KURANG
                result = op1 - op2;
                break;
            case "x":
                //OPERASI KALI
                result = op1 * op2;
                break;
            case ":":
                //OPERASI BAGI
                result = op1 / op2;
                break;
        }
        return result;
    }
    public static boolean getAnswer(LinkedList<CustomImageView> ll){
        result = 0;
        int jumlahOperator = 0;
        int jumlahOperand = 0;
        String op1 = "",op2 = "",ot = "";
        boolean bagianOperand = true;
        while(!ll.isEmpty()){
            CustomImageView civ = ll.removeFirst();
//            if(civ ==null){
//                Log.d("CALCULATE","NULL");
//            }else{
//                Log.d("CALCULATE",civ.getText());
//            }
            if(civ != null){
                if(!civ.getIsOperator() && bagianOperand){
                    if(op1.equalsIgnoreCase("")){
                        op1 = civ.getText();
                    }else{
                        op2 = civ.getText();
                        op1 = ""+Calculator.calculate(op1,op2,ot);
                        result = Double.parseDouble(op1);
                        op2 = "";
                        ot = "";
                    }
                    bagianOperand = false;
                    jumlahOperand ++;
                }else if(civ.getIsOperator() && !bagianOperand){
                    ot = civ.getText();
                    jumlahOperator++;
                    bagianOperand = true;
                }else{
                    return false;
                }
            }
        }
        if(jumlahOperand == 1 && jumlahOperator == 0){
            return false;
        }else if(jumlahOperand - jumlahOperator != 1){
            return false;
        }else {
            return true;
        }
    }
}
