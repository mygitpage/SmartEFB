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
import android.util.Log;

import java.util.Calendar;

/**
 * Created by ich on 31.07.2017.
 */

// this receiver is set with android:process: remote, look at AndroidManifest, because should run when app is close
public class AlarmReceiverExchangeAndEventService extends BroadcastReceiver {

    // shared prefs for the app
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    @Override
    public void onReceive(Context context, Intent intent) {

        // start exchange service
        // send intent to service to start the service
        Intent startServiceIntent = new Intent(context, ExchangeServiceEfb.class);

        // set command = "ask new data" on server
        startServiceIntent.putExtra("com","ask_new_data");
        startServiceIntent.putExtra("dbid",0L);
        startServiceIntent.putExtra("receiverBroadcast","");

        // start service
        context.startService(startServiceIntent);
    }
}
