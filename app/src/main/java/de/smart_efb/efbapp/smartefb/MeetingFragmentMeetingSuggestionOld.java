package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 21.11.2017.
 */

public class MeetingFragmentMeetingSuggestionOld extends Fragment {

    // fragment view
    View viewFragmentMeetingSuggestion;

    // fragment context
    Context fragmentMeetingSuggestionContextOld = null;

    // the fragment
    Fragment fragmentThisFragmentContext;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs
    SharedPreferences prefs;

    // ListView for meetings and suggestion
    ListView listViewMeetingSuggestion = null;

    // reference cursorAdapter for the listview
    MeetingSuggestionOldOverviewCursorAdapter dataAdapterListViewMeetingSuggestionOld = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingSuggestion = layoutInflater.inflate(R.layout.fragment_meeting_suggestion_old, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(meetingFragmentMeetingOverviewOldBrodcastReceiver, filter);

        return viewFragmentMeetingSuggestion;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingSuggestionContextOld = getActivity().getApplicationContext();

        // init the fragment meeting old
        initFragmentMeetingSuggestionOld();

        // show actual meeting and suggestion informations
        displayActualSuggestionInformation();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentMeetingSuggestionContextOld, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentMeetingSuggestionContextOld, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(meetingFragmentMeetingOverviewOldBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    private void initFragmentMeetingSuggestionOld() {

        // init the DB
        myDb = new DBAdapter(fragmentMeetingSuggestionContextOld);

        // find the listview for display meetings and suggestion, etc.
        listViewMeetingSuggestion = (ListView) viewFragmentMeetingSuggestion.findViewById(R.id.listViewOldMeetingDates);

        // init the prefs
        prefs = fragmentMeetingSuggestionContextOld.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentMeetingSuggestionContextOld.MODE_PRIVATE);

    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver meetingFragmentMeetingOverviewOldBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with arrangements
            Boolean updateListView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                String tmpExtraMeeting = intentExtras.getString("Meeting","0");
                String tmpExtraMeetingSettings = intentExtras.getString("MeetingSettings","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentMeetingSuggestionContextOld.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();

                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingSettings != null && tmpExtraMeetingSettings.equals("1")) {

                    // meeting settings change
                    updateListView = true;
                }

                if (updateListView) {
                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentThisFragmentContext).attach(fragmentThisFragmentContext).commit();
                }

            }
        }
    };


    // show actual suggestion overview
    private void displayActualSuggestionInformation () {

        String tmpSubtitle = "";

        // get all meetings and suggestion from database in correct order
        Long nowTime = System.currentTimeMillis();
        Cursor cursorMeetingSuggestion = myDb.getAllRowsMeetingsAndSuggestion("old_meeting", nowTime);

        if (cursorMeetingSuggestion.getCount() > 0 && listViewMeetingSuggestion != null) {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMeetingSuggestionOverviewOld", "string", fragmentMeetingSuggestionContextOld.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "meeting_suggestion_old");

            // set relative layout with old meeting text visibility gone
            RelativeLayout tmpNoSuggestionsText = (RelativeLayout) viewFragmentMeetingSuggestion.findViewById(R.id.meetingSuggestionOldOverviewNothingAvailable);
            tmpNoSuggestionsText.setVisibility(View.GONE);

            // set listview visible
            listViewMeetingSuggestion.setVisibility(View.VISIBLE);

            // new dataadapter
            dataAdapterListViewMeetingSuggestionOld = new MeetingSuggestionOldOverviewCursorAdapter(
                    getActivity(),
                    cursorMeetingSuggestion,
                    0);

            // Assign adapter to ListView
            listViewMeetingSuggestion.setAdapter(dataAdapterListViewMeetingSuggestionOld);
        }
        else {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMeetingSuggestionOverviewOldNotAvailable", "string", fragmentMeetingSuggestionContextOld.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "meeting_suggestion_old");

            //  set relative layout with old meeting text visibility show
            RelativeLayout tmpNoSuggestionsText = (RelativeLayout) viewFragmentMeetingSuggestion.findViewById(R.id.meetingSuggestionOldOverviewNothingAvailable);
            tmpNoSuggestionsText.setVisibility(View.VISIBLE);
        }
    }

}
