package it.bastoner.taboom.filters;

import android.text.InputFilter;
import android.text.Spanned;

public class MinMaxFilter implements InputFilter {
    private int mIntMin , mIntMax ;
    public MinMaxFilter ( int minValue , int maxValue) {
        this . mIntMin = minValue ;
        this . mIntMax = maxValue ;
    }
    public MinMaxFilter (String minValue , String maxValue) {
        this . mIntMin = Integer. parseInt (minValue) ;
        this . mIntMax = Integer. parseInt (maxValue) ;
    }
    @Override
    public CharSequence filter (CharSequence source , int start , int end , Spanned dest , int dstart , int dend) {
        // Source is what is inserting the user, dest is what he have already inserted
        try {
            String inputString = dest.toString() + source.toString();
            if (inputString.length() > 2)
                return "";
            int input = Integer. parseInt (inputString) ;
            if (isInRange( mIntMin , mIntMax , input))
                return null;
        } catch (NumberFormatException e) {
            e.printStackTrace() ;
        }
        return "" ;
    }
    private boolean isInRange ( int a , int b , int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a ;
    }
}