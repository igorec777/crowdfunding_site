package com.example.course.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class DonateHelper {
    public static float round(float num, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(num));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}
