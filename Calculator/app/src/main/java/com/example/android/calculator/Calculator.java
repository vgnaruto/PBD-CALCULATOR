package com.example.android.calculator;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.xpath.XPathExpression;

/**
 * Kelas ini yang akan mengatur operasi matematika
 * Operasi dasar : +,-,*,/
 * Created by toshiba pc on 2/26/2018.
 */

public class Calculator {
    public static String resultString = "";
    public static double result = 0;

    public static double calculate(String operand1, String operand2, String operator) {
        double op1 = Double.parseDouble(operand1);
        double op2 = Double.parseDouble(operand2);
        double result = 0;
        switch (operator) {
            case "+":
                //OPERASI JUMLAH
                result = op1 + op2;
                break;
            case "-":
                //OPERASI KURANG
                result = op1 - op2;
                break;
            case "*":
                //OPERASI KALI
                result = op1 * op2;
                break;
            case "/":
                //OPERASI BAGI
                result = op1 / op2;
                break;
        }
        return result;
    }

    public static boolean getAnswer(LinkedList<Node> ll) {
        result = 0;
        int jumlahOperator = 0;
        int jumlahOperand = 0;
        String op1 = "", op2 = "", ot = "";
        boolean bagianOperand = true;
        while (!ll.isEmpty()) {
            Node n = ll.removeFirst();
//            if(civ ==null){
//                Log.d("CALCULATE","NULL");
//            }else{
//                Log.d("CALCULATE",civ.getText());
//            }
            if (n != null) {
                if (!n.getIsOperator() && bagianOperand) {
                    if (op1.equalsIgnoreCase("")) {
                        op1 = n.getText();
                    } else {
                        op2 = n.getText();
                        op1 = "" + Calculator.calculate(op1, op2, ot);
                        if(op2.equalsIgnoreCase("0") && ot.equalsIgnoreCase("/")){
                            resultString = "n/a";
                            return true;
                        }
                        result = Double.parseDouble(op1);
                        resultString = (int)result+"";
                        op2 = "";
                        ot = "";
                    }
                    bagianOperand = false;
                    jumlahOperand++;
                } else if (n.getIsOperator() && !bagianOperand) {
                    ot = n.getText();
                    jumlahOperator++;
                    bagianOperand = true;
                } else {
                    return false;
                }
            }
        }
        if (jumlahOperand == 1 && jumlahOperator == 0) {
            return false;
        } else if (jumlahOperand - jumlahOperator != 1) {
            return false;
        } else {
            return true;
        }
    }

    public static String listToString(LinkedList<Node> ll) {
        String result = "";
        for (Node n : ll) {
            if (n != null) {
                result = result + n.getText() + " ";
            }
        }
        return result;
    }

    public static boolean calculate(String token) {
        if (isValid(token)) {
            String[] tokens = token.split("\\+|-");
            ArrayList<String> operasi = new ArrayList<>();
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].contains("*")) {
                    String[] tempToken = tokens[i].split("\\*");
                    double result = Double.parseDouble(tempToken[0]) * Double.parseDouble(tempToken[1]);
//                    Log.d("CALCULATE", result + " *");
                    Calculator.result = result;
                    Calculator.resultString = (int)result+"";
                    operasi.add("" + result);
                } else if (tokens[i].contains("/")) {
                    String[] tempToken = tokens[i].split("/");
                    double result = Double.parseDouble(tempToken[0]) / Double.parseDouble(tempToken[1]);
                    if(tempToken[2].equalsIgnoreCase("0")){
                        Calculator.resultString = "n/a";
                        return true;
                    }
                    Calculator.result = result;
                    Calculator.resultString = (int)result+"";
                    operasi.add("" + result);
//                    Log.d("CALCULATE", result + " /");
                } else {
                    operasi.add(tokens[i]);
                }
            }
            String[] semua = token.split(" ");
            ArrayList<String> opTambahKurang = new ArrayList<>();
            for (int i = 0; i < semua.length; i++) {
                if (semua[i].equalsIgnoreCase("+") || semua[i].equalsIgnoreCase("-")) {
                    opTambahKurang.add(semua[i]);
                }
            }
            String od1 = operasi.get(0);
            String od2 = "";
            String op = "";
            for (int i = 1; i < operasi.size(); i++) {
                od2 = operasi.get(i);
                op = opTambahKurang.get(i - 1);
//                Log.d("CALCULATE", "OPERASI: " + od1 + " " + op + " " + od2);
                if (op.equalsIgnoreCase("+")) {
                    result = Double.parseDouble(od1) + Double.parseDouble(od2);
                    Calculator.resultString = (int)result+"";
//                    Log.d("CALCULATE", result + " +");
                    od1 = "" + result;
                } else if (op.equalsIgnoreCase("-")) {
                    result = Double.parseDouble(od1) - Double.parseDouble(od2);
                    Calculator.resultString = (int)result+"";
//                    Log.d("CALCULATE", result + " -");
                    od1 = "" + result;
                }
            }
            for (String top : operasi) {
                Log.d("CALCULATE", "top: " + top);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValid(String str) {
        String[] token = str.split(" ");
        int jumlahOperand = 0;
        int jumlahOperator = 0;
        if (str.length() != 0) {
            for (int i = 0; i < token.length; i++) {
                //operand
                if (i % 2 == 0) {
                    if (token[i].matches("\\+|-|\\*|/")) {
                        return false;
                    }
                    jumlahOperand++;
                }
                //operator
                else {
                    if (!token[i].matches("\\+|-|\\*|/")) {
                        return false;
                    }
                    jumlahOperator++;
                }
            }
        } else {
            return false;
        }
        if(jumlahOperand - jumlahOperator != 1 || jumlahOperand == 1){
            return false;
        }
        return true;
    }
}
