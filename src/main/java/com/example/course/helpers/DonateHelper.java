package com.example.course.helpers;

import java.math.BigDecimal;


public class DonateHelper
{
    public static float round(float num, int decimalPlace)
    {
        BigDecimal bd = new BigDecimal(Float.toString(num));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
