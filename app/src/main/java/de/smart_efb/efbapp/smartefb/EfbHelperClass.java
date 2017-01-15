package de.smart_efb.efbapp.smartefb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ich on 28.05.16.
 */
public class EfbHelperClass {


    public static String timestampToDateFormat( long timestamp, String format )
    {




        try{

            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");

            //getDefault();
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);

        }
        catch (Exception e) {

        }


        return "";


        /*

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return formatter.format(calendar.getTime());
        */



    }

    public static String timestampToTimeFormat( long timestamp, String format )
    {


        try{

            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");

            //getDefault();
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);

        }
        catch (Exception e) {

        }

        return "";
    }




}
