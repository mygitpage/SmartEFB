package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

/**
 * Created by ich on 09.02.2018.
 */

public class AlarmReceiverRestartAlarmAfterTimeChange extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Restart Exchange timer after time change (same code look mainActivity)
        // get calendar and init
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // set calendar object with seconds
        calendar.add(Calendar.SECOND, ConstansClassMain.wakeUpTimeExchangeService);
        int tmpAlarmTime = ConstansClassMain.wakeUpTimeExchangeService * 1000; // make mills-seconds

        // make intent for alarm receiver
        Intent startIntentService = new Intent (context.getApplicationContext(), AlarmReceiverExchangeAndEventService.class);

        // make pending intent
        final PendingIntent pIntentService = PendingIntent.getBroadcast(context, 0, startIntentService, PendingIntent.FLAG_UPDATE_CURRENT );

        // get alarm manager service
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // set alarm manager to call exchange receiver
        try {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpAlarmTime, pIntentService);
        }
        catch (NullPointerException e) {
            // do nothing
        }

        // Restart meeting timer after time change (same code look mainActivity)
        PendingIntent pendingIntentRememberMeeting;

        Long firstStartRememberMeeting = System.currentTimeMillis() + 1000; // first start point for meeting remember function
        Long repeatingMeetingRemember = 24L * 60L * 60L * 1000L; // one day

        // get reference to alarm manager
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // create intent for backcall to broadcast receiver
        Intent rememberMeetingAlarmIntent = new Intent(context, AlarmReceiverMeeting.class);

        // create call (pending intent) for alarm manager
        pendingIntentRememberMeeting = PendingIntent.getBroadcast(context, 0, rememberMeetingAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set alarm
        try {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstStartRememberMeeting, repeatingMeetingRemember, pendingIntentRememberMeeting);
        }
        catch (NullPointerException e) {
            // do nothing
        }

    }

}
