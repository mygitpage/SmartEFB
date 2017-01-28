package de.smart_efb.efbapp.smartefb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by ich on 28.05.16.
 */
public class EfbHelperClass {


    // return the formated date string of timestamp
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

    }

    // return the formated time string of timestamp
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


    // return a random number between min and max
    public static int randomNumber (int min, int max) {

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;

    }

}
