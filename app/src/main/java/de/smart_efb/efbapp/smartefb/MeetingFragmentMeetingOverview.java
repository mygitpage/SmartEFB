package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 21.11.2017.
 */


public class MeetingFragmentMeetingOverview extends Fragment {

    // fragment view
    View viewFragmentMeeting;

    // fragment context
    Context fragmentMeetingContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // ListView for meetings and suggestion
    ListView listViewMeetingSuggestion = null;

    // reference cursorAdapter for the listview
    MeetingOverviewCursorAdapter dataAdapterListViewMeeting = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeeting = layoutInflater.inflate(R.layout.fragment_meeting_overview, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(meetingFragmentMeetingOverviewBrodcastReceiver, filter);

        return viewFragmentMeeting;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingContext = getActivity().getApplicationContext();

        // init the fragment meeting
        initFragmentMeeting();

        // show actual meeting and suggestion informations
        displayActualMeetingSuggestionInformation();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentMeetingContext, ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            fragmentMeetingContext.startService(startServiceIntent);
        }
    }


    private void initFragmentMeeting () {

        // init the DB
        myDb = new DBAdapter(fragmentMeetingContext);

        // find the listview for display meetings and suggestion, etc.
        listViewMeetingSuggestion = (ListView) viewFragmentMeeting.findViewById(R.id.listViewMeetingDates);

        // init the prefs
        prefs = fragmentMeetingContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentMeetingContext.MODE_PRIVATE);

    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(meetingFragmentMeetingOverviewBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeServiceEfb
    private BroadcastReceiver meetingFragmentMeetingOverviewBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with arrangements
            Boolean updateListView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                String tmpExtraMeeting = intentExtras.getString("Meeting","0");
                String tmpExtraMeetingNewMeeting = intentExtras.getString("MeetingNewMeeting","0");
                String tmpExtraMeetingSettings = intentExtras.getString("MeetingSettings","0");
                String tmpExtraMeetingCanceledMeetingByCoach = intentExtras.getString("MeetingCanceledMeetingByCoach","0");
                String tmpCommand = intentExtras.getString("Command");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");
                String tmpExtraSendInBackgroundRefreshView = intentExtras.getString("MeetingSendInBackgroundRefreshView");

                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentMeetingContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingNewMeeting != null && tmpExtraMeetingNewMeeting.equals("1")) {
                    // new meeting on smartphone -> update meeting view and show toast
                    String updateNewMeeting = fragmentMeetingContext.getString(R.string.toastMessageMeetingNewMeeting);
                    Toast toast = Toast.makeText(context, updateNewMeeting, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingCanceledMeetingByCoach != null && tmpExtraMeetingCanceledMeetingByCoach.equals("1")) {
                    // meeting canceled by coach -> update meeting view -> show toast and update view
                    String updateNewMeeting = fragmentMeetingContext.getString(R.string.toastMessageMeetingCanceledMeetingByCoach);
                    Toast toast = Toast.makeText(context, updateNewMeeting, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpCommand != null && tmpCommand.length() > 0) { // send successfull?

                    if (tmpCommand.equals("ask_parent_activity")) {

                        // get successfull message from parent
                        String successfullMessage = ((ActivityMeeting) getActivity()).getSuccessefullMessageForSending ();
                        if (successfullMessage.length() > 0) {
                            Toast toast = Toast.makeText(context, successfullMessage, Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }

                        // update the view
                        updateListView = true;
                    }
                }
                else if (tmpSendNotSuccessefull != null && tmpSendNotSuccessefull.equals("1") && tmpCommand != null && tmpCommand.length() > 0) { // send not successfull?

                    if (tmpCommand.equals("ask_parent_activity")) {

                        // get not successfull message from parent
                        String notSuccessfullMessage = ((ActivityMeeting) getActivity()).getNotSuccessefullMessageForSending ();
                        if (notSuccessfullMessage.length() > 0) {
                            Toast toast = Toast.makeText(context, notSuccessfullMessage, Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                    }
                    else if (tmpCommand.equals("look_message") && tmpMessage != null && tmpMessage.length() > 0) {

                        Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        if( v != null) v.setGravity(Gravity.CENTER);
                        toast.show();
                    }
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingSettings != null && tmpExtraMeetingSettings.equals("1")) {

                    // meeting settings change
                    updateListView = true;
                }
                else if (tmpExtraSendInBackgroundRefreshView != null && tmpExtraSendInBackgroundRefreshView.equals("1")) {

                    // meeting change -> send in background
                    updateListView = true;
                }

                // update the list view because data has change?
                if (updateListView) {
                    updateListView();
                }
            }
        }
    };


    // update the list view with meetings
    public void updateListView () {

        if (listViewMeetingSuggestion != null) {
            listViewMeetingSuggestion.destroyDrawingCache();
            listViewMeetingSuggestion.setVisibility(ListView.INVISIBLE);
            listViewMeetingSuggestion.setVisibility(ListView.VISIBLE);

            displayActualMeetingSuggestionInformation ();
        }
    }




    // show actual meetings, suggestion, cnaceled meetings and suggestions, old meetings
    private void displayActualMeetingSuggestionInformation () {

        String tmpSubtitle = "";

        // get all meetings from database in correct order
        Long nowTime = System.currentTimeMillis();
        Cursor cursorMeetingSuggestion = myDb.getAllRowsMeetingsAndSuggestion("future_meeting", nowTime);

        if (cursorMeetingSuggestion.getCount() > 0 && listViewMeetingSuggestion != null) {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMeetingOverview", "string", fragmentMeetingContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "meeting_overview");

            // set no suggestions text visibility gone
            TextView tmpNoSuggestionsText = (TextView) viewFragmentMeeting.findViewById(R.id.meetingOverviewNoMeetingAvailable);
            tmpNoSuggestionsText.setVisibility(View.GONE);

            // set listview visible
            listViewMeetingSuggestion.setVisibility(View.VISIBLE);

            // new dataadapter
            dataAdapterListViewMeeting = new MeetingOverviewCursorAdapter(
                    getActivity(),
                    cursorMeetingSuggestion,
                    0);

            // Assign adapter to ListView
            listViewMeetingSuggestion.setAdapter(dataAdapterListViewMeeting);

        }
        else {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMeetingOverviewNoMeetings", "string", fragmentMeetingContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "meeting_overview");

            // set no suggestions text visibility gone
            TextView tmpNoSuggestionsText = (TextView) viewFragmentMeeting.findViewById(R.id.meetingOverviewNoMeetingAvailable);
            tmpNoSuggestionsText.setVisibility(View.VISIBLE);

        }
    }

}
