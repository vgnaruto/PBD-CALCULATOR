package com.example.android.calculator;

/**
 * Kelas ini yang akan mengatur operasi matematika
 * Operasi dasar : +,-,*,/
 * Created by toshiba pc on 2/26/2018.
 */

public class Calculator {
    //TODO LENGKAPIN KODE DI BAWAH INI( UNTUK ARIO)
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
}
