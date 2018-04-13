package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import java.util.Calendar;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ich on 12.02.2018.
 */

public class EfbSetAlarmManager {

    Context context;

    // point to shared preferences
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;


    public EfbSetAlarmManager (Context mContext) {

        this.context = mContext;

        // get the shared preferences
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }


    // set alarm manager to start every wakeUpTimeExchangeService seconds the service to check server for new data
    public void setAlarmForExchangeService () {

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
    }


    // set alarmmanager for remember meeting; ; same function in EfbXmlParser to start Alarm when new meeting/ suggestion comes in!
    void setAlarmManagerForRememberMeeting () {

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
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstStartRememberMeeting, repeatingMeetingRemember, pendingIntentRememberMeeting);
    }


    // set alarmmanager for our arrangement evaluation time
    void setAlarmManagerForOurArrangementEvaluation () {

        PendingIntent pendingIntentOurArrangementEvaluate;

        // init the DB
        DBAdapter myDb = new DBAdapter(context);

        // get all arrangements with the same block id
        Cursor cursor = myDb.getAllRowsCurrentOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""), "equalBlockId");

        if (cursor.getCount() > 0) {

            // get reference to alarm manager
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // create intent for backcall to broadcast receiver
            Intent evaluateAlarmIntent = new Intent(context, AlarmReceiverOurArrangement.class);

            // get evaluate pause time and active time
            int evaluatePauseTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours
            int evaluateActivTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours

            // get start time and end time for evaluation
            Long startEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis());
            Long endEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis());

            Long tmpSystemTimeInMills = System.currentTimeMillis();
            int tmpEvalutePaAcTime = evaluateActivTime * 1000;
            String tmpIntentExtra = "evaluate";
            String tmpChangeDbEvaluationStatus = "set";
            Long tmpStartPeriod = 0L;

            // get calendar and init
            Calendar calendar = Calendar.getInstance();

            // set alarm manager when current time is between start date and end date and evaluation is enable
            if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false) &&  System.currentTimeMillis() >= prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, 0) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

                calendar.setTimeInMillis(startEvaluationDate);

                do {
                    tmpStartPeriod = calendar.getTimeInMillis();
                    calendar.add(Calendar.SECOND, evaluateActivTime);
                    tmpIntentExtra = "evaluate";
                    tmpChangeDbEvaluationStatus = "set";
                    tmpEvalutePaAcTime = evaluateActivTime * 1000; // make mills-seconds
                    if (calendar.getTimeInMillis() < tmpSystemTimeInMills) {
                        tmpStartPeriod = calendar.getTimeInMillis();
                        calendar.add(Calendar.SECOND, evaluatePauseTime);
                        tmpIntentExtra = "pause";
                        tmpChangeDbEvaluationStatus = "delete";
                        tmpEvalutePaAcTime = evaluatePauseTime * 1000; // make mills-seconds
                    }
                } while (calendar.getTimeInMillis() < tmpSystemTimeInMills);

                if (tmpChangeDbEvaluationStatus.equals("delete")) {
                    // update table ourArrangement in db -> delete evaluation possible
                    myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""), "delete");
                }
                else {

                    if (cursor != null) {

                        cursor.moveToFirst();

                        do {

                            if (tmpStartPeriod > cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME))) {
                                myDb.changeStatusEvaluationPossibleOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)), "set");
                            } else {
                                myDb.changeStatusEvaluationPossibleOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)), "delete");
                            }
                        } while (cursor.moveToNext());
                    }
                }

                // put extras to intent -> "evaluate" or "delete"
                evaluateAlarmIntent.putExtra("evaluateState", tmpIntentExtra);

                // create call (pending intent) for alarm manager
                pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // set alarm
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpEvalutePaAcTime, pendingIntentOurArrangementEvaluate);
            }
            else { // delete alarm - it is out of time

                // update table ourArrangement in db -> evaluation disable
                myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, ""), "delete");
                // create pending intent
                pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                // delete alarm
                manager.cancel(pendingIntentOurArrangementEvaluate);
            }
        }

        // close DB connection
        myDb.close();
    }


    // set alarmmanager for our goals evaluation time
    void setAlarmManagerForOurGoalsEvaluation () {

        PendingIntent pendingIntentOurGoalsEvaluate;

        // init the DB
        DBAdapter myDb = new DBAdapter(context);

        // get all jointly goals with the same block id
        Cursor cursor = myDb.getAllJointlyRowsOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""), "equalBlockId");

        if (cursor.getCount() > 0) {

            // get reference to alarm manager
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // create intent for backcall to broadcast receiver
            Intent evaluateAlarmIntent = new Intent(context, AlarmReceiverOurGoals.class);

            // get start time and end time for evaluation
            Long startEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());
            Long endEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());

            // get evaluate pause time and active time in seconds
            int evaluatePauseTime = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, ConstansClassOurGoals.defaultTimeForActiveAndPauseEvaluationJointlyGoals); // default value 43200 is 12 hours
            int evaluateActivTime = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, ConstansClassOurGoals.defaultTimeForActiveAndPauseEvaluationJointlyGoals); // default value 43200 is 12 hours

            Long tmpSystemTimeInMills = System.currentTimeMillis();
            int tmpEvalutePaAcTime = evaluateActivTime * 1000;
            String tmpIntentExtra = "evaluate";
            String tmpChangeDbEvaluationStatus = "set";
            Long tmpStartPeriod = 0L;

            // get calendar and init
            Calendar calendar = Calendar.getInstance();

            // set alarm manager when current time is between start date and end date and evaluation is enable
            if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false) && System.currentTimeMillis() >= prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, 0) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

                calendar.setTimeInMillis(startEvaluationDate);

                do {
                    tmpStartPeriod = calendar.getTimeInMillis();
                    calendar.add(Calendar.SECOND, evaluateActivTime);
                    tmpIntentExtra = "evaluate";
                    tmpChangeDbEvaluationStatus = "set";
                    tmpEvalutePaAcTime = evaluateActivTime * 1000; // make mills-seconds
                    if (calendar.getTimeInMillis() < tmpSystemTimeInMills) {
                        tmpStartPeriod = calendar.getTimeInMillis();
                        calendar.add(Calendar.SECOND, evaluatePauseTime);
                        tmpIntentExtra = "pause";
                        tmpChangeDbEvaluationStatus = "delete";
                        tmpEvalutePaAcTime = evaluatePauseTime * 1000; // make mills-seconds
                    }
                } while (calendar.getTimeInMillis() < tmpSystemTimeInMills);

                if (tmpChangeDbEvaluationStatus.equals("delete")) {
                    // update table ourGoals in db -> delete evaluation possible
                    myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""), "delete");
                } else {

                    if (cursor != null) {

                        cursor.moveToFirst();

                        do {

                            if (tmpStartPeriod > cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_LAST_EVAL_TIME))) {
                                myDb.changeStatusEvaluationPossibleOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)), "set");
                            } else {
                                myDb.changeStatusEvaluationPossibleOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)), "delete");
                            }
                        } while (cursor.moveToNext());
                    }
                }

                // put extras to intent -> "evaluate" or "delete"
                evaluateAlarmIntent.putExtra("evaluateState", tmpIntentExtra);

                // create call (pending intent) for alarm manager
                pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // set alarm
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpEvalutePaAcTime, pendingIntentOurGoalsEvaluate);

            } else { // delete alarm - it is out of time

                // update table ourGoals in db -> evaluation disable
                myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, ""), "delete");
                // crealte pending intent
                pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                // delete alarm
                manager.cancel(pendingIntentOurGoalsEvaluate);
            }
        }

        // close DB connection
        myDb.close();
    }




}
