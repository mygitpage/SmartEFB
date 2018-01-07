package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentD extends Fragment {

    // shared prefs for the app
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // fragment context
    Context fragmentContextD = null;

    // fragment view
    View viewFragmentD;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentD = layoutInflater.inflate(R.layout.fragment_settings_efb_d, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(settingsFragmentDBrodcastReceiver, filter);

        return viewFragmentD;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentContextD = getActivity().getApplicationContext();

        prefs = fragmentContextD.getSharedPreferences("smartEfbSettings", fragmentContextD.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // show actual view
        displayActualView();
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(settingsFragmentDBrodcastReceiver);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeServiceEfb
    private BroadcastReceiver settingsFragmentDBrodcastReceiver = new BroadcastReceiver() {

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
                    String textCaseClose = fragmentContextD.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
            }
        }
    };


    void displayActualView() {

        // check Our Arrangement on? -> show acoustic signal check box for new arrangement, sketch, comment, etc.
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false)) {
            LinearLayout placeholderArrangementAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerArrangementAcoustics);
            placeholderArrangementAcoustics.setVisibility(View.VISIBLE);
        }

        // check Our Arrangement evaluation on? -> show acoustic signal check box for new arrangement, sketch, comment, etc.
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false) && prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) {
            LinearLayout placeholderArrangementEvaluationAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerArrangementEvaluationAcoustics);
            placeholderArrangementEvaluationAcoustics.setVisibility(View.VISIBLE);
        }





    }
}
