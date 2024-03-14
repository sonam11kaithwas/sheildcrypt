package com.advantal.shieldcrypt.utils_pkg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sonam on 30-06-2022 11:11.
 */
public class DateUtil {
    public static String convertDateToString(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = dateFormat.format(date);
            return strDate;
        } else {
            return "";
        }

    }

    public static String longToDate(Long date) {
        if (date != null) {
            DateFormat simple = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date result = new Date(date);
            String strDate = simple.format(date);
            return strDate;
        } else {
            return "";
        }

    }
    public static String longToDate(Long date,String form) {
        if (date != null) {
            DateFormat simple = new SimpleDateFormat(form);//"dd-MM-yyyy HH:mm:ss"
            Date result = new Date(date);
            String strDate = simple.format(date);
            return strDate;
        } else {
            return "";
        }

    }

    public static String convertDateToStringSlash(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            String strDate = dateFormat.format(date);
            return strDate;
        } else {
            return "";
        }

    }

    public static String convertDateToStringYYYYMMDD(Date date) {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = formatter.format(new Date());
            return strDate;
        } else {
            return "";
        }

    }

    public static String convertDateToStringWithTime(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
            String strDate = dateFormat.format(date);
            return strDate;
        } else {
            return "";
        }

    }

    public static String convertDateToStringWithTimes(Date date) {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
            String strDate = dateFormat.format(date);
            return strDate;
        } else {
            return "";
        }

    }


}
