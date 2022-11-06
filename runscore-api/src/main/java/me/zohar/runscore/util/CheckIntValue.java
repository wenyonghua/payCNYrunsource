package me.zohar.runscore.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckIntValue {

    /**
     * 判断输入的数据数字
     *
     * @param str
     * @return
     */
    public static boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断输入的是金额
     * @param str
     * @return
     */
    public static boolean isNumeric1(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(Exception e){

            return false;
        }
    }

    public static  void main(String args[]){
       System.out.println(isNumeric1("这个呢过"));
       String ss="";
        Double serverChangeValue=Double.valueOf(ss);//提现手续费

        Boolean va=null;
       // v.isNaN();
//        if(va.isNaN()){
//
//        }
    }

}
