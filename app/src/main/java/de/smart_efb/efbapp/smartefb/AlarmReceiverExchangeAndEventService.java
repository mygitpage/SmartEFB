package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by ich on 31.07.2017.
 */

// this receiver is set with android:process: remote, look at AndroidManifest, because should run when app is close
public class AlarmReceiverExchangeAndEventService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // start exchange job intent service
        // send job intent service to start the service
        Intent startServiceIntent = new Intent(context, ExchangeJobIntentServiceEfb.class);

        // set command = "ask new data" on server
        startServiceIntent.putExtra("com","ask_new_data");
        startServiceIntent.putExtra("dbid",0L);
        startServiceIntent.putExtra("receiverBroadcast","");

        // start job intent service
        ExchangeJobIntentServiceEfb.enqueueWork (context, startServiceIntent);
    }
}
