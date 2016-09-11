package de.smart_efb.efbapp.smartefb;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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


        // init the DB
        myDb = new DBAdapter(context);

        // init the prefs
        prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);

        // alarm time from the prefs
        int tmpAlarmTime = 0;

        // get start time and end time for evaluation
        Long startEvaluationDate = prefs.getLong("startDataEvaluationInMills", System.currentTimeMillis());
        Long endEvaluationDate = prefs.getLong("endDataEvaluationInMills", System.currentTimeMillis());

        // get evaluate pause time and active time in seconds
        evaluatePauseTime = prefs.getInt("evaluatePauseTimeInSeconds", 43200); // default value 43200 is 12 hours
        evaluateActivTime = prefs.getInt("evaluateActivTimeInSeconds", 43200); // default value 43200 is 12 hours

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
        if (prefs.getBoolean("showArrangementEvaluate", false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

            switch (evaluateState) {

                case "pause": // alarm comes out of pause
                    // next cycle is evaluate -> set evaluate time
                    calendar.add(Calendar.SECOND, evaluateActivTime);
                    tmpAlarmTime = evaluateActivTime * 1000; // make mills-seconds
                    // set intent -> next state evaluate
                    evaluateAlarmIntent.putExtra("evaluateState","evaluate");
                    // update table ourArrangement in db -> evaluation enable
                    myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()),"set");



                    Log.d("AlarmReceiver","Aus Pause --------->");


                    break;
                case "evaluate": // alarm comes out of evaluate
                    // next cycle is pause -> set pause time
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpAlarmTime = evaluatePauseTime * 1000; // make mills-seconds
                    // set intent -> next state pause
                    evaluateAlarmIntent.putExtra("evaluateState","pause");
                    // update table ourArrangement in db -> evaluation disable
                    myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()),"delete");



                    Log.d("AlarmReceiver","Aus Evaluation <---------");




                    break;
                default:
                    // next cycle is pause -> set pause time
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpAlarmTime = evaluatePauseTime;
                    // set intent -> next state pause
                    evaluateAlarmIntent.putExtra("evaluateState","pause");
            }

            // crealte pending intent
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // set alarm manager
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpAlarmTime, pendingIntentOurArrangementEvaluate);


        }
        else { // delete alarm - it is out of time

            // update table ourArrangement in db -> evaluation disable
            myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()),"delete");

            // crealte pending intent
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(context, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // delete alarm
            manager.cancel(pendingIntentOurArrangementEvaluate);



            Log.d("End A - Alarm:"," CANCELED!!!!!!! ");

        }

        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
        Intent tmpIntent = new Intent();
        tmpIntent.setAction("ARRANGEMENT_EVALUATE_STATUS_UPDATE");
        context.sendBroadcast(tmpIntent);

    }
}