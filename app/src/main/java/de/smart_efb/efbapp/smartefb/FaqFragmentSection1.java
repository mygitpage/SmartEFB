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
 * Created by ich on 12.08.16.
 */
public class FaqFragmentSection1 extends Fragment {

    // fragment view
    View viewFragmentSection1;

    // fragment context
    Context fragmentFaqSectionOneContext = null;

    // for prefs
    private SharedPreferences prefs;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSection1 = layoutInflater.inflate(R.layout.fragment_faq_section_1, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(faqFragmentSection1BrodcastReceiver, filter);

        return viewFragmentSection1;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentFaqSectionOneContext = getActivity().getApplicationContext();

        // open sharedPrefs
        prefs =  fragmentFaqSectionOneContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentFaqSectionOneContext.MODE_PRIVATE);

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentFaqSectionOneContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentFaqSectionOneContext, startServiceIntent);
        }

        // show actual faq section one
        displayActualFaqSectionOne();
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(faqFragmentSection1BrodcastReceiver);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver faqFragmentSection1BrodcastReceiver = new BroadcastReceiver() {

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
                    String textCaseClose = fragmentFaqSectionOneContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
            }
        }
    };


    // show fragment ressources
    private void displayActualFaqSectionOne () {

        // set movement methode info section two
        TextView tmpShowOverviewSectionTwoTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionTwoTitle);
        tmpShowOverviewSectionTwoTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info section three
        TextView tmpShowOverviewSectionThreeTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionThreeTitle);
        tmpShowOverviewSectionThreeTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info section four
        TextView tmpShowOverviewSectionFourTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionFourTitle);
        tmpShowOverviewSectionFourTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info section five
        TextView tmpShowOverviewSectionFiveTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionFiveTitle);
        tmpShowOverviewSectionFiveTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info text missing faq
        TextView tmpShowMissingFaq = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionOneEnd);
        tmpShowMissingFaq.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
