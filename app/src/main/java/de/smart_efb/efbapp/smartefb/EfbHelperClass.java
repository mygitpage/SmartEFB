package de.smart_efb.efbapp.smartefb;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by ich on 28.05.16.
 */
public class EfbHelperClass {


    // return the formated date string of timestamp
    public static String timestampToDateFormat( long timestamp, String format )
    {
        try{
            TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");
            Calendar calendar = Calendar.getInstance(tz);
            calendar.setTimeInMillis(timestamp);
            //calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMANY);
            Date currenTimeZone = (Date) calendar.getTime();

            return sdf.format(currenTimeZone);
        }
        catch (Exception e) {
            // do nothing
        }
        return "";
    }

    // return a random number between min and max
    public static int randomNumber (int min, int max) {

        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }


    // return a timestamp with hour, minute, seconds and milliseconds to zero
    public static Long timestampToNormalDayMonthYearDate(Long startTimestamp)
    {
        try{
            TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");
            Calendar calendar = Calendar.getInstance(tz);

            calendar.setTimeInMillis(startTimestamp);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            return calendar.getTimeInMillis();

        }
        catch (Exception e) {
        }

        return 0L;
    }


    public static String generateRandomString () {

        return UUID.randomUUID().toString();
    }

}
