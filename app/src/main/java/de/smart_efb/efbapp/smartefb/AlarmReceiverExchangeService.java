package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by ich on 31.07.2017.
 */
public class AlarmReceiverExchangeService extends BroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {



        // send intent to service to start the service
        Intent startServiceIntent = new Intent(context, ExchangeServiceEfb.class);
        startServiceIntent.putExtra("test","daten");
        //tmpIntent.setAction("ARRANGEMENT_EVALUATE_STATUS_UPDATE");
        context.startService(startServiceIntent);

    }
}
