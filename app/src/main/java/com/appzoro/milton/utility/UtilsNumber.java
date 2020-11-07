package com.appzoro.milton.utility;

import android.text.InputFilter;
import android.widget.EditText;

public class UtilsNumber {

    public static void setNumberFormat(EditText editText) {

        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            if (source.length() > 0) {
                if (!Character.isDigit(source.charAt(0)))
                    return "";
                else {
                    if (dstart == 3) {
                        return source + ") ";
                    } else if (dstart == 0) {
                        return "(" + source;
                    } else if ((dstart == 5) || (dstart == 9))
                        return "-" + source;
                    else if (dstart >= 14)
                        return "";
                }
            }
            return null;
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    public static void removeWhiteSpace(EditText editText) {

        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
           // if (source.length() > 0) {
              if(source.toString().equals(" ")){
                  return "";
              }else {
                  return source;
              }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

}
