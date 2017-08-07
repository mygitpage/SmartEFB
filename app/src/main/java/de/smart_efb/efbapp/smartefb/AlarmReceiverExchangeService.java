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

// this receiver is set with android:process: remote, look at AndroidManifest, because should run when app is close
public class AlarmReceiverExchangeService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // send intent to service to start the service
        Intent startServiceIntent = new Intent(context, ExchangeServiceEfb.class);

        // set command = "ask new data" on server
        startServiceIntent.putExtra("com","ask_new_data");

        // start service
        context.startService(startServiceIntent);

    }
}
