package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 20.06.16.
 */

public class SettingsEfbFragmentC extends Fragment {

    View contentFragmentC;

    // fragment context
    Context fragmentSettingsHelpContext = null;

    // for prefs
    private SharedPreferences prefs;

    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        contentFragmentC = layoutInflater.inflate(R.layout.fragment_settings_efb_c, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(settingsFragmentCBrodcastReceiver, filter);

        return contentFragmentC;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentSettingsHelpContext = getActivity().getApplicationContext();

        // show view
        setDisplayView();

        // open sharedPrefs
        prefs =  fragmentSettingsHelpContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentSettingsHelpContext.MODE_PRIVATE);

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentSettingsHelpContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentSettingsHelpContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(settingsFragmentCBrodcastReceiver);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver settingsFragmentCBrodcastReceiver = new BroadcastReceiver() {

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
                    String textCaseClose = fragmentSettingsHelpContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
            }
        }
    };


    private void setDisplayView() {

        // link item link to faq question for app
        TextView textViewConnectedWithServerIntroText = (TextView) contentFragmentC.findViewById(R.id.settingsConnectToServerSucsessfullIntro);
        textViewConnectedWithServerIntroText.setMovementMethod(LinkMovementMethod.getInstance());

        // get textview for Link to data privacy external
        TextView tmpLinkDataprivacy = (TextView) contentFragmentC.findViewById(R.id.settingsEfbFragmentCDataPrivacyLink);
        tmpLinkDataprivacy.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
