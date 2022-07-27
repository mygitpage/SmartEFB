package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentB extends Fragment {

    // fragment view
    View viewFragmentContactdetails;

    // fragment context
    Context fragmentContactdetailsContext = null;

    // shared prefs for settings
    SharedPreferences prefs;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentContactdetails = layoutInflater.inflate(R.layout.fragment_settings_efb_b, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(settingsFragmentBBrodcastReceiver, filter);

        return viewFragmentContactdetails;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentContactdetailsContext = getActivity().getApplicationContext();

        // open sharedPrefs
        prefs =  fragmentContactdetailsContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentContactdetailsContext.MODE_PRIVATE);

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentContactdetailsContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentContactdetailsContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(settingsFragmentBBrodcastReceiver);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver settingsFragmentBBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentContactdetailsContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
            }
        }
    };


}