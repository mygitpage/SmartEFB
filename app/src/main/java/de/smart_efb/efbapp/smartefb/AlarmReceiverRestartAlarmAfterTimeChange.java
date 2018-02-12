package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ich on 09.02.2018.
 */

public class AlarmReceiverRestartAlarmAfterTimeChange extends BroadcastReceiver {

    // point to shared preferences
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("TIME CHANGE-->", "Starte Alarm Service!");

        // get the shared preferences
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);

        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // new alarm manager service for start all needed alarms
            EfbSetAlarmManager efbSetAlarmManager = new EfbSetAlarmManager(context);

            // start exchange service with intent, when case is open!
            efbSetAlarmManager.setAlarmForExchangeService();

            // start check meeting remember alarm manager, when function meeting is on and case is not closed
            if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, false)) {
                efbSetAlarmManager.setAlarmManagerForRememberMeeting();
            }
            // start check our arrangement alarm manager, when function our arrangement is on and case is not closed
            if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false)) {
                efbSetAlarmManager.setAlarmManagerForOurArrangementEvaluation();
            }
            // start check our goals alarm manager, when function our goals is on and case is not closed
            if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false)) {
                efbSetAlarmManager.setAlarmManagerForOurGoalsEvaluation();
            }
        }
    }
}
