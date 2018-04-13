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
import java.util.Calendar;

/**
 * Created by ich on 29.10.2016.
 */
public class AlarmReceiverOurGoals extends BroadcastReceiver {


    // evaluate pause time and active time (get from prefs)
    int evaluatePauseTime = 0;
    int evaluateActivTime = 0;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment goals
    SharedPreferences prefs;

    // Pending intent for alarm manager
    PendingIntent pendingIntentOurGoalsEvaluate;

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

        // get start time and end time for evaluation
        Long startEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());
        Long endEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());

        // get evaluate pause time and active time in seconds
        evaluatePauseTime = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, ConstansClassOurGoals.defaultTimeForActiveAndPauseEvaluationJointlyGoals); // default value 43200 is 12 hours
        evaluateActivTime = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, ConstansClassOurGoals.defaultTimeForActiveAndPauseEvaluationJointlyGoals); // default value 43200 is 12 hours

        // get alarmManager
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // get calendar and init
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

       // create new intent for pending intent to send and receive
        evaluateAlarmIntent = new Intent(context, AlarmReceiverOurGoals.class);

        //get extra data from received intent
        String evaluateState = "";
        try {
            evaluateState = intent.getExtras().getString("evaluateState");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // set alarm manager when current time is between start date and end date and evaluation is enable
        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

            switch (evaluateState) {

                case "pause": // alarm comes out of pause
                    // next cycle is evaluate -> set evaluate time
                    calendar.add(Calendar.SECOND, evaluateActivTime);
                    tmpAlarmTime = evaluateActivTime * 1000; // make mills-seconds
                    // set intent -> next state evaluate
                    evaluateAlarmIntent.putExtra("evaluateState","evaluate");
                    // update table ourGoalsJointlyGoals in db -> evaluation enable
                    myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""),"set");

                    break;
                case "evaluate": // alarm comes out of evaluate
                    // next cycle is pause -> set pause time
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpAlarmTime = evaluatePauseTime * 1000; // make mills-seconds
                    // set intent -> next state pause
                    evaluateAlarmIntent.putExtra("evaluateState","pause");
                    // update table ourGoalsJointlyGoals in db -> evaluation disable
                    myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""),"delete");

                    break;
                default:
                    // next cycle is pause -> set pause time
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpAlarmTime = evaluatePauseTime;
                    // set intent -> next state pause
                    evaluateAlarmIntent.putExtra("evaluateState","pause");
            }

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
                mNotificationManager.notify(103, mBuilder.build());
            }
        }
        else { // delete alarm - it is out of time
            // update table ourGoals in db -> evaluation disable
            myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""),"delete");

            // create pending intent
            pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // delete alarm
            manager.cancel(pendingIntentOurGoalsEvaluate);
        }

        // close db connection
        myDb.close();

        // send intent to receiver in OurGoalsFragmentJointlyGoalsNow to update listView OurGoals (when active)
        Intent tmpIntent = new Intent();
        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
        tmpIntent.putExtra("UpdateJointlyEvaluationLink","1");
        context.sendBroadcast(tmpIntent);
    }
}
