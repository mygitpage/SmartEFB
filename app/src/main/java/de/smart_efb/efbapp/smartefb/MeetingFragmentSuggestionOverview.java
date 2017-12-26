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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 21.11.2017.
 */

public class MeetingFragmentSuggestionOverview extends Fragment {

    // fragment view
    View viewFragmentSuggestion;

    // fragment context
    Context fragmentSuggestionContext = null;

    // reference to the DB
    DBAdapter myDb;

    // for prefs
    private SharedPreferences prefs;

    // ListView for meetings and suggestion
    ListView listViewMeetingSuggestion = null;

    // reference cursorAdapter for the listview
    MeetingSuggestionOverviewCursorAdapter dataAdapterListViewSuggestion = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSuggestion = layoutInflater.inflate(R.layout.fragment_suggestion_overview, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(meetingFragmentSuggestionOverviewBrodcastReceiver, filter);

        return viewFragmentSuggestion;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentSuggestionContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentSuggestion();

        // show actual meeting and suggestion informations
        displayActualSuggestionInformation();
    }


    private void initFragmentSuggestion () {

        // init the DB
        myDb = new DBAdapter(fragmentSuggestionContext);

        // open sharedPrefs
        prefs = fragmentSuggestionContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentSuggestionContext.MODE_PRIVATE);

        // find the listview for display meetings and suggestion, etc.
        listViewMeetingSuggestion = (ListView) viewFragmentSuggestion.findViewById(R.id.listViewSuggestionDates);
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(meetingFragmentSuggestionOverviewBrodcastReceiver);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver meetingFragmentSuggestionOverviewBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraSuggestionNewSuggestion = intentExtras.getString("MeetingNewSuggestion","0");
                String tmpExtraSuggestionCanceledSuggestionByCoach = intentExtras.getString("MeetingCanceledSuggestionByCoach","0");
                String tmpExtraSuggestionMeetingFound = intentExtras.getString("MeetingFoundFromSuggestion","0");
                String tmpCommand = intentExtras.getString("Command");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");
                String tmpReceiverBroadcast = intentExtras.getString("receiverBroadcast", "");

                if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraSuggestionNewSuggestion != null && tmpExtraSuggestionNewSuggestion.equals("1")) {
                    // new suggestion from coach on smartphone -> update suggestion view and show toast
                    String updateNewSuggestion = fragmentSuggestionContext.getString(R.string.toastMessageSuggestionOverviewNewSuggestion);
                    Toast toast = Toast.makeText(context, updateNewSuggestion, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraSuggestionCanceledSuggestionByCoach != null && tmpExtraSuggestionCanceledSuggestionByCoach.equals("1")) {
                    // suggestion canceled by coach -> update suggestion view -> show toast and update view
                    String updateNewSuggestion = fragmentSuggestionContext.getString(R.string.toastMessageSuggestionOverviewCanceledSuggestionByCoach);
                    Toast toast = Toast.makeText(context, updateNewSuggestion, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraSuggestionMeetingFound != null && tmpExtraSuggestionMeetingFound.equals("1")) {
                    // meeting found from suggestion by coach -> update suggestion view -> show toast and update view
                    String updateNewSuggestion = fragmentSuggestionContext.getString(R.string.toastMessageSuggestionOverviewMeetingFoundByCoach);
                    Toast toast = Toast.makeText(context, updateNewSuggestion, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpReceiverBroadcast != null && tmpReceiverBroadcast.equals("meetingFragmentSuggestionOverview") && tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpCommand != null && tmpCommand.length() > 0) { // send successfull?

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

                    // update the view
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
    private void updateListView () {

        if (listViewMeetingSuggestion != null) {
            listViewMeetingSuggestion.destroyDrawingCache();
            listViewMeetingSuggestion.setVisibility(ListView.INVISIBLE);
            listViewMeetingSuggestion.setVisibility(ListView.VISIBLE);

            displayActualSuggestionInformation ();
        }
    }


    // show actual suggestion overview
    private void displayActualSuggestionInformation () {

        String tmpSubtitle = "";

        // get all suggestion from database in correct order
        Long nowTime = System.currentTimeMillis();
        Cursor cursorMeetingSuggestion = myDb.getAllRowsMeetingsAndSuggestion("future_suggestion", nowTime);

        if (cursorMeetingSuggestion.getCount() > 0 && listViewMeetingSuggestion != null) {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleSuggestionOverview", "string", fragmentSuggestionContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "suggestion_overview");

            // set no suggestions text visibility gone
            TextView tmpNoSuggestionsText = (TextView) viewFragmentSuggestion.findViewById(R.id.meetingOverviewNoSuggestionAvailable);
            tmpNoSuggestionsText.setVisibility(View.GONE);

            // set message for sending successful and not successful -> in cursorAdapter is a button with intent to exchangeServiceEfb
            if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) { // is client comment on -> show other successfull and not successfull text
                // set successfull message in parent activity -> show in toast, when vote from client + comment for suggestion is send successfull
                String tmpSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageSuggestionOverviewVoteAndCommentByClientSuccessfullSend", "string", fragmentSuggestionContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setSuccessefullMessageForSending(tmpSuccessfullMessage);
                // set not successfull message in parent activity -> show in toast, when vote from client + comment for suggestion is send not successfull
                String tmpNotSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageSuggestionOverviewVoteAndCommentByClientNotSuccessfullSend", "string", fragmentSuggestionContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setNotSuccessefullMessageForSending(tmpNotSuccessfullMessage);

            }
            else {
                // set successfull message in parent activity -> show in toast, when vote from client for suggestion is send successfull
                String tmpSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageSuggestionOverviewVoteByClientSuccessfullSend", "string", fragmentSuggestionContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setSuccessefullMessageForSending(tmpSuccessfullMessage);
                // set not successfull message in parent activity -> show in toast, when vote from client for suggestion is send not successfull
                String tmpNotSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageSuggestionOverviewVoteByClientNotSuccessfullSend", "string", fragmentSuggestionContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setNotSuccessefullMessageForSending(tmpNotSuccessfullMessage);
            }

            // set listview visible
            listViewMeetingSuggestion.setVisibility(View.VISIBLE);

            // new dataadapter
            dataAdapterListViewSuggestion = new MeetingSuggestionOverviewCursorAdapter(
                    getActivity(),
                    cursorMeetingSuggestion,
                    0);

            // Assign adapter to ListView
            listViewMeetingSuggestion.setAdapter(dataAdapterListViewSuggestion);
        }
        else {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleSuggestionOverviewNoSuggestion", "string", fragmentSuggestionContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "suggestion_overview");

            // set no suggestions text visibility gone
            TextView tmpNoSuggestionsText = (TextView) viewFragmentSuggestion.findViewById(R.id.meetingOverviewNoSuggestionAvailable);
            tmpNoSuggestionsText.setVisibility(View.VISIBLE);
        }
    }
}
