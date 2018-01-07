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

public class MeetingFragmentSuggestionFromClient extends Fragment {

    // fragment view
    View viewFragmentSuggestionFromCLient;

    // fragment context
    Context fragmentSuggestionFromClientContext = null;

    // the fragment
    Fragment fragmentThisFragmentContext;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;

    // for prefs
    private SharedPreferences prefs;

    // reference cursorAdapter for the listview
    MeetingSuggestionFromClientOverviewCursorAdapter dataAdapterListViewSuggestionFromClient = null;

    // ListView for meetings and suggestion
    ListView listViewMeetingSuggestionFromClient = null;



    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentSuggestionFromCLient = layoutInflater.inflate(R.layout.fragment_suggestion_from_client_overview, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(meetingFragmentSuggestionFromClientBrodcastReceiver, filter);

        return viewFragmentSuggestionFromCLient;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentSuggestionFromClientContext = getActivity().getApplicationContext();

        fragmentThisFragmentContext = this;

        initFragmentSuggestionFromClient();

        displayActualSuggestionInformation();
    }


    private void initFragmentSuggestionFromClient () {

        // init the DB
        myDb = new DBAdapter(fragmentSuggestionFromClientContext);

        // open sharedPrefs
        prefs =  fragmentSuggestionFromClientContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentSuggestionFromClientContext.MODE_PRIVATE);

        // find the listview for display meetings and suggestion, etc.
        listViewMeetingSuggestionFromClient = (ListView) viewFragmentSuggestionFromCLient.findViewById(R.id.listViewSuggestionFromClientDates);
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(meetingFragmentSuggestionFromClientBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ExchangeServiceEfb
    private BroadcastReceiver meetingFragmentSuggestionFromClientBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras;

            // true-> update the list view with arrangements
            Boolean updateListView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // check intent order
                String tmpExtraMeeting = intentExtras.getString("Meeting","0");
                String tmpExtraMeetingSettings = intentExtras.getString("MeetingSettings","0");
                String tmpExtraSuggestionFromClientNewInvitation = intentExtras.getString("MeetingNewInvitationSuggestion","0");
                String tmpExtraSuggestionFromClientCanceledByCoach = intentExtras.getString("MeetingCanceledClientSuggestionByCoach","0");
                String tmpExtraSuggestionFromClientMeetingFound = intentExtras.getString("MeetingFoundFromClientSuggestion","0");
                String tmpCommand = intentExtras.getString("Command");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");
                String tmpExtraSuggestionFromClientUpdateListView = intentExtras.getString("SuggestionFromClientUpdateListView","0"); // broadcast from MeetingSuggestionFromClientOverviewCursorAdapter when count down timer (wait time) is finish
                String tmpReceiverBroadcast = intentExtras.getString("receiverBroadcast", "");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentSuggestionFromClientContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraSuggestionFromClientNewInvitation != null && tmpExtraSuggestionFromClientNewInvitation.equals("1")) {
                    // new invitation for client suggestion from coach on smartphone -> update client suggestion view and show toast
                    String updateNewSuggestion = fragmentSuggestionFromClientContext.getString(R.string.toastMessageSuggestionFromClientNewInvitation);
                    Toast toast = Toast.makeText(context, updateNewSuggestion, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraSuggestionFromClientCanceledByCoach != null && tmpExtraSuggestionFromClientCanceledByCoach.equals("1")) {
                    // timezone for client suggestion is canceled by coach  -> update client suggestion view and show toast
                    String updateNewSuggestion = fragmentSuggestionFromClientContext.getString(R.string.toastMessageSuggestionFromCanceledByCoach);
                    Toast toast = Toast.makeText(context, updateNewSuggestion, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraSuggestionFromClientMeetingFound != null && tmpExtraSuggestionFromClientMeetingFound.equals("1")) {
                    // meeting found from client suggestion by coach -> update client suggestion view -> show toast and update view
                    String updateNewSuggestion = fragmentSuggestionFromClientContext.getString(R.string.toastMessageSuggestionFromClientMeetingFoundByCoach);
                    Toast toast = Toast.makeText(context, updateNewSuggestion, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpReceiverBroadcast != null && tmpReceiverBroadcast.equals("meetingFragmentSuggestionFromClient") && tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpCommand != null && tmpCommand.length() > 0) { // send successfull?

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
                else if (tmpExtraSuggestionFromClientUpdateListView != null && tmpExtraSuggestionFromClientUpdateListView.equals("update")) {

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingSettings != null && tmpExtraMeetingSettings.equals("1")) {

                    // meeting settings change
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

        if (listViewMeetingSuggestionFromClient != null) {
            listViewMeetingSuggestionFromClient.destroyDrawingCache();
            listViewMeetingSuggestionFromClient.setVisibility(ListView.INVISIBLE);
            listViewMeetingSuggestionFromClient.setVisibility(ListView.VISIBLE);

            displayActualSuggestionInformation ();
        }
    }



    // show actual suggestion overview
    private void displayActualSuggestionInformation () {

        String tmpSubtitle;

        // check is function on
        if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientSuggestion_OnOff, false)) {

            // get all suggestion from client from database in correct order
            Long nowTime = System.currentTimeMillis();
            Cursor cursorMeetingSuggestionFromClient = myDb.getAllRowsMeetingsAndSuggestion("future_suggestion_from_client", nowTime);

            if (cursorMeetingSuggestionFromClient.getCount() > 0 && listViewMeetingSuggestionFromClient != null) {

                // set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleSuggestionFromClientOverview", "string", fragmentSuggestionFromClientContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle(tmpSubtitle, "suggestion_from_client");

                // set no suggestions text visibility gone
                TextView tmpNoSuggestionsText = (TextView) viewFragmentSuggestionFromCLient.findViewById(R.id.meetingOverviewNoSuggestionFromClientAvailable);
                tmpNoSuggestionsText.setVisibility(View.GONE);

                // set message for sending successful and not successful -> in cursorAdapter is a button with intent to exchangeServiceEfb
                // set successfull message in parent activity -> show in toast, when suggestion from client is send successfull
                String tmpSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageSuggestionFromClientByClientSuccessfullSend", "string", fragmentSuggestionFromClientContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setSuccessefullMessageForSending(tmpSuccessfullMessage);
                // set not successfull message in parent activity -> show in toast, when suggestion from client is send not successfull
                String tmpNotSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageSuggestionFromClientByClientNotSuccessfullSend", "string", fragmentSuggestionFromClientContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setNotSuccessefullMessageForSending(tmpNotSuccessfullMessage);

                // set listview visible
                listViewMeetingSuggestionFromClient.setVisibility(View.VISIBLE);

                // new dataadapter
                dataAdapterListViewSuggestionFromClient = new MeetingSuggestionFromClientOverviewCursorAdapter(
                        getActivity(),
                        cursorMeetingSuggestionFromClient,
                        0);

                // Assign adapter to ListView
                listViewMeetingSuggestionFromClient.setAdapter(dataAdapterListViewSuggestionFromClient);

            } else {

                // set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleSuggestionFromClientOverviewNoSuggestion", "string", fragmentSuggestionFromClientContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle(tmpSubtitle, "suggestion_from_client");

                // set no suggestions text visibility show
                TextView tmpNoSuggestionsText = (TextView) viewFragmentSuggestionFromCLient.findViewById(R.id.meetingOverviewNoSuggestionFromClientAvailable);
                tmpNoSuggestionsText.setVisibility(View.VISIBLE);
            }
        }
        else {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleSuggestionFromClientOverviewFunctionNotPossible", "string", fragmentSuggestionFromClientContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle(tmpSubtitle, "suggestion_from_client");

            // set function off text visibility show
            TextView tmpNoSuggestionsText = (TextView) viewFragmentSuggestionFromCLient.findViewById(R.id.meetingOverviewSuggestionFromClientFunctionOff);
            tmpNoSuggestionsText.setVisibility(View.VISIBLE);
        }
    }

}



