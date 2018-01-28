package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by ich on 24.01.2018.
 */

public class AlarmReceiverMeeting extends BroadcastReceiver {


    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment goals
    SharedPreferences prefs;

    // Pending intent for alarm manager
    PendingIntent pendingIntentMeeting;



    @Override
    public void onReceive(Context context, Intent intent) {

        String notificationContentTitle;
        Intent notificationIntent;
        Intent  mainActivityIntent;
        PendingIntent contentPendingIntent;
        TaskStackBuilder stackBuilder;

        String meetingTextIn15Min = "";
        String lineFeed = "";

        // init the DB
        myDb = new DBAdapter(context);

        // get notification manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // get alarm tone
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // new notification builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        // set basic things to all notifications
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_large_appicon));
        mBuilder.setSmallIcon(R.drawable.notification_smile);
        mBuilder.setAutoCancel(true);

        // needed for back stack -> start main activity after pressing back
        mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);









        Log.d ("ALARM REMEMBER MEET->", "REMEMBER!!!!!!");






        Long nowTime = System.currentTimeMillis();

        Cursor rememberMeeting_15min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_meeting_15min", nowTime);

        if (rememberMeeting_15min != null && rememberMeeting_15min.getCount() > 0) {


            Log.d("ALARM REMEMBER MEET->", "Meeting found!!!");

            rememberMeeting_15min.moveToFirst();

            do {


                String tmpMeetingNotificationText15 = String.format(context.getResources().getString(R.string.alarmReceiverMotificationSubTextRemember15Min), EfbHelperClass.timestampToDateFormat(rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm"), rememberMeeting_15min.getString(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1)));
                meetingTextIn15Min += tmpMeetingNotificationText15 + lineFeed;
                lineFeed = "\n";


                Log.d ("ALARM REMEMBER MEET->", "IN DER DO-SCHLEIFE");



                //Long meetingDate = rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1));









            } while (rememberMeeting_15min.moveToNext());

            // reset line feed
            lineFeed = "";




            Log.d("ALARM REMEMBER MEET->", "Result:"+meetingTextIn15Min);


            //int kategorie = rememberMeeting.getInt(rememberMeeting.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_KATEGORIE));
            /*
            if (nowTime + fiveMinutes > meetingDate) {


                    Log.d ("ALARM MEETING", "Notification Meet:"+EfbHelperClass.timestampToDateFormat(rememberMeeting.getLong(rememberMeeting.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"));


                }
             */







        }





            /*


            String notificationContentTitle;
            Intent notificationIntent;
            Intent  mainActivityIntent;
            PendingIntent contentPendingIntent;
            TaskStackBuilder stackBuilder;

            // get notifocation manager
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // get alarm tone
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // new notification builder
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            // set basic things to all notifications
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_large_appicon));
            mBuilder.setSmallIcon(R.drawable.notification_smile);
            mBuilder.setAutoCancel(true);

            // needed for back stack -> start main activity after pressing back
            mainActivityIntent = new Intent(context, MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // init the DB
            myDb = new DBAdapter(context);

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

            // alarm time from the prefs
            int tmpAlarmTime = 0;


            // get alarmManager
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // get calendar and init
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            // create new intent for pending intent to send and receive
            evaluateAlarmIntent = new Intent(context, de.smart_efb.efbapp.smartefb.AlarmReceiverOurGoals.class);




                // crealte pending intent
                pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // set alarm manager
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpAlarmTime, pendingIntentOurGoalsEvaluate);

                // check notification when our goals evaluation time change
                if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, false)) {

                    // get our arrangement notification string
                    notificationContentTitle = context.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurGoals);

                    // set intent/ pending intent to start our goals
                    notificationIntent = new Intent(context, ActivityOurGoals.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    // generate back stack for pending intent and add main activity
                    stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(mainActivityIntent);

                    // add intent for connect book
                    stackBuilder.addNextIntent(notificationIntent);

                    // generate pending intent
                    contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    // set notofication attributes
                    mBuilder.setContentTitle(notificationContentTitle);
                    mBuilder.setContentIntent(contentPendingIntent);
                    // sound on/off for connect book?
                    if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true)) {
                        mBuilder.setSound(alarmSound);
                    }

                    String subTitleNotification = "";
                    int evaluationPeriod = evaluatePauseTime / 3600; // make hours from seconds
                    int evaluationActivePeriod = evaluateActivTime / 3600; // make hours from seconds;

                    switch (evaluateState) {
                        case "evaluate":
                            subTitleNotification = context.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurGoalsEvaluationPause);
                            subTitleNotification = String.format(subTitleNotification, evaluationActivePeriod, evaluationPeriod);
                            break;
                        case "pause":
                            subTitleNotification = context.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurGoalsEvaluationSet);
                            subTitleNotification = String.format(subTitleNotification, evaluationPeriod);
                            break;
                    }

                    // show long text in notification
                    mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(subTitleNotification));
                    mBuilder.setContentText(subTitleNotification);

                    // show notification
                    mNotificationManager.notify(104, mBuilder.build());
                }



            // close db connection
            myDb.close();
*/

                /*

            // send intent to receiver in OurGoalsFragmentJointlyGoalsNow to update listView OurGoals (when active)
            Intent tmpIntent = new Intent();
            tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
            tmpIntent.putExtra("UpdateJointlyEvaluationLink","1");
            context.sendBroadcast(tmpIntent);
            */
        }
    }
