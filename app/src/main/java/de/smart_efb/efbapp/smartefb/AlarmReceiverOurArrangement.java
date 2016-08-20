package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ich on 21.08.16.
 */
public class AlarmReceiverOurArrangement extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String evaluateState = "";
        try {
            evaluateState = intent.getExtras().getString("evaluateState");
        }
        catch (Exception e) {
            e.printStackTrace();
        }



        switch (evaluateState) {

            case "pause":
                Toast.makeText(context, "Komme aus der Pause", Toast.LENGTH_SHORT).show();
                break;
            case "evaluate":
                Toast.makeText(context, "Aus der Evalaution", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, "Ohne Status", Toast.LENGTH_SHORT).show();



        }


/*
        PendingIntent pendingIntentOurArrangementEvaluate;

        Intent evalauteAlarmIntent = new Intent(ActivityOurArrangement.class, AlarmReceiverOurArrangement.class);
        evalauteAlarmIntent.putExtra("evaluateState","pause");

        pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(ActivityOurArrangement.this, 0, evalauteAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;


        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntentOurArrangementEvaluate);
  */





    }
}
