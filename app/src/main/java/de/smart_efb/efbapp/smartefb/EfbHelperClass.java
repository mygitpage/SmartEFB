package de.smart_efb.efbapp.smartefb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ich on 28.05.16.
 */
public class EfbHelperClass {


    public static String timestampToDateFormat( long timestamp, String format )
    {


        SimpleDateFormat formatter = new SimpleDateFormat(format);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return formatter.format(calendar.getTime());



    }


}
