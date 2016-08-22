package de.smart_efb.efbapp.smartefb;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by ich on 22.08.16.
 */
public class AlarmReceiverOurArrangement extends BroadcastReceiver {


    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;



    PendingIntent pendingIntentOurArrangementEvaluate;

    Intent evalauteAlarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {


        // init the DB
        myDb = new DBAdapter(context);

        // init the prefs
        prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);


        String evaluateState = "";
        try {
            evaluateState = intent.getExtras().getString("evaluateState");
        }
        catch (Exception e) {
            e.printStackTrace();
        }



        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = 60000;


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60);



        switch (evaluateState) {

            case "pause":


                evalauteAlarmIntent = new Intent(context, AlarmReceiverOurArrangement.class);
                evalauteAlarmIntent.putExtra("evaluateState","evaluate");


                Log.d("Alarm"," - Aus Pause! -");

                myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()),"set");













                Toast.makeText(context, "Komme aus der Pause", Toast.LENGTH_SHORT).show();
                break;
            case "evaluate":

                evalauteAlarmIntent = new Intent(context, AlarmReceiverOurArrangement.class);
                evalauteAlarmIntent.putExtra("evaluateState","pause");

                Log.d("Alarm"," - Aus Evaluation! -");

                myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()),"delete");


                Toast.makeText(context, "Aus der Evaluation", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "Ohne Status", Toast.LENGTH_SHORT).show();
                evalauteAlarmIntent = new Intent(context, AlarmReceiverOurArrangement.class);
                evalauteAlarmIntent.putExtra("evaluateState","pause");



        }

        pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(context, 0, evalauteAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntentOurArrangementEvaluate);




        /*------------------------*/
        Intent tmpIntent = new Intent();
        tmpIntent.setAction("ARRANGEMENT_EVALUATE_STATUS_UPDATE");
        context.sendBroadcast(tmpIntent);



    }
}
