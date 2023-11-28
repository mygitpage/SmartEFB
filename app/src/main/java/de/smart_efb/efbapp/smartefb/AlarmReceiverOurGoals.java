package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
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
            pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
                contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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
                if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true)) {
                    // show notification with sound
                    mNotificationManager.notify(3201, mBuilderSound.build());
                }
                else {
                    // show notification with no sound
                    mNotificationManager.notify(3202, mBuilderNoSound.build());
                }
             }
        }
        else { // delete alarm - it is out of time
            // update table ourGoals in db -> evaluation disable
            myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""),"delete");

            // create pending intent
            pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
