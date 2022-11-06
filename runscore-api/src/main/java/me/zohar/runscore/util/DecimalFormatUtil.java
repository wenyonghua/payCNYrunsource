package me.zohar.runscore.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DecimalFormatUtil {
    public static final String DEFAULT_FORMAT = "#,###";
    private DecimalFormatUtil() {

    }

    /**
     * 格式化数字表示
     * @param bigDecimal
     * @param format
     * @return
     */
    public static String formatString(BigDecimal bigDecimal, String format) {
        if(bigDecimal == null) {
            return "";
        }
        if(format == null || format.isEmpty()) {
            format = DEFAULT_FORMAT;
        }
        DecimalFormat df = new DecimalFormat(format);
        return df.format(bigDecimal.doubleValue());
    }
   /* public static String formatString2(float data) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(data);
    }*/

    public static void main(String[] args) {
        Double dd=new Double("123456789.03434");
      //  Double dd1=new Double("123,456,789.03");
        BigDecimal dd2=new BigDecimal("123,456,789.03");
        System.out.println(DecimalFormatUtil.formatString(new BigDecimal(dd), null));
       // Double newValue=new Double(DecimalFormatUtil.formatString(new BigDecimal(dd), null).toString());

    //  System.out.println(dd+dd1);

    }


}
