package de.smart_efb.efbapp.smartefb;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by ich on 22.08.16.
 */
public class AlarmReceiverOurArrangement extends BroadcastReceiver {

    // evaluate pause time and active time (get from prefs)
    int evaluatePauseTime = 0;
    int evaluateActivTime = 0;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;

    // Pending intent for alarm manager
    PendingIntent pendingIntentOurArrangementEvaluate;

    // intent for this alarm receiver
    Intent evaluateAlarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        String notificationContentTitle;
        Intent notificationIntent;
        Intent  mainActivityIntent;
        PendingIntent contentPendingIntent;
        TaskStackBuilder stackBuilder;

        // get notifocation manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // new notification builder without sound
        NotificationCompat.Builder mBuilderNoSound = new NotificationCompat.Builder(context, ConstansClassMain.uniqueNotificationChannelIdNoSound);

        // new notification builder with sound
        NotificationCompat.Builder mBuilderSound = new NotificationCompat.Builder(context, ConstansClassMain.uniqueNotificationChannelIdSound);

        // set basic things to all notifications (channel with or without sound)
        mBuilderNoSound.setSmallIcon(R.drawable.notification_smile);
        mBuilderNoSound.setAutoCancel(true);
        mBuilderSound.setSmallIcon(R.drawable.notification_smile);
        mBuilderSound.setAutoCancel(true);

        // needed for back stack -> start main activity after pressing back
        mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // init the DB
        myDb = new DBAdapter(context);

        // init the prefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // alarm time from the prefs
        int tmpAlarmTime = 0;

        // get start time and end time for evaluation
        Long startEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis());
        Long endEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis());

        // get evaluate pause time and active time in seconds
        evaluatePauseTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours
        evaluateActivTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours

        // get alarmManager
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // get calendar and init
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // create new intent for pending intent to send and receive
        evaluateAlarmIntent = new Intent(context, AlarmReceiverOurArrangement.class);

        //get extra data from received intent
        String evaluateState = "";
        try {
            evaluateState = intent.getExtras().getString("evaluateState");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // set alarm manager when current time is between start date and end date and evaluation is enable
        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

            switch (evaluateState) {

                case "pause": // alarm comes out of pause
                    // next cycle is evaluate -> set evaluate time
                    calendar.add(Calendar.SECOND, evaluateActivTime);
                    tmpAlarmTime = evaluateActivTime * 1000; // make mills-seconds
                    // set intent -> next state evaluate
                    evaluateAlarmIntent.putExtra("evaluateState","evaluate");
                    // update table ourArrangement in db -> evaluation enable
                    myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""),"set");

                    break;
                case "evaluate": // alarm comes out of evaluate
                    // next cycle is pause -> set pause time
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpAlarmTime = evaluatePauseTime * 1000; // make mills-seconds
                    // set intent -> next state pause
                    evaluateAlarmIntent.putExtra("evaluateState","pause");
                    // update table ourArrangement in db -> evaluation disable
                    myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""),"delete");

                    break;
                default:
                    // next cycle is pause -> set pause time
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpAlarmTime = evaluatePauseTime * 1000;
                    // set intent -> next state pause
                    evaluateAlarmIntent.putExtra("evaluateState","pause");
            }

            // create pending intent
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // set alarm manager
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpAlarmTime, pendingIntentOurArrangementEvaluate);

            // check notification when our arrangement evaluation time change
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, false)) {

                // get our arrangement notification string
                notificationContentTitle = context.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurArrangement);

                // set intent/ pending intent to start our arrangement
                notificationIntent = new Intent(context, ActivityOurArrangement.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // generate back stack for pending intent and add main activity
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(mainActivityIntent);

                // add intent for connect book
                stackBuilder.addNextIntent(notificationIntent);

                // generate pending intent
                contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                String subTitleNotification = "";
                int evaluationPeriod = evaluatePauseTime / 3600; // make hours from seconds
                int evaluationActivePeriod = evaluateActivTime / 3600; // make hours from seconds;

                switch (evaluateState) {
                    case "evaluate":
                        subTitleNotification = context.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurArrangementEvaluationPause);
                        subTitleNotification = String.format(subTitleNotification, evaluationActivePeriod, evaluationPeriod);
                        break;
                    case "pause":
                        subTitleNotification = context.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurArrangementEvaluationSet);
                        subTitleNotification = String.format(subTitleNotification, evaluationPeriod);
                        break;
                }

                // set notification attributes (with or without sound)
                mBuilderSound.setContentTitle(notificationContentTitle);
                mBuilderSound.setContentIntent(contentPendingIntent);
                mBuilderSound.setStyle(new NotificationCompat.BigTextStyle().bigText(subTitleNotification));
                mBuilderSound.setContentText(subTitleNotification);
                mBuilderNoSound.setContentTitle(notificationContentTitle);
                mBuilderNoSound.setContentIntent(contentPendingIntent);
                mBuilderNoSound.setStyle(new NotificationCompat.BigTextStyle().bigText(subTitleNotification));
                mBuilderNoSound.setContentText(subTitleNotification);

                // sound on/off for meeting?
                if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, true)) {
                    // show notification with sound
                    mNotificationManager.notify(2201, mBuilderSound.build());
                }
                else {
                    // show notification with no sound
                    mNotificationManager.notify(2202, mBuilderNoSound.build());
                }
            }
        }
        else { // delete alarm - it is out of time

            // update table ourArrangement in db -> evaluation disable
            myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""),"delete");

            // crealte pending intent
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // delete alarm
            manager.cancel(pendingIntentOurArrangementEvaluate);
        }

        // close db connection
        myDb.close();

        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
        Intent tmpIntent = new Intent();
        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
        tmpIntent.putExtra("UpdateEvaluationLink","1");
        context.sendBroadcast(tmpIntent);
    }
}