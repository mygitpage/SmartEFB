package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
        //IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        //getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentNowCommentBrodcastReceiver, filter);



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


    // show actual suggestion overview
    private void displayActualSuggestionInformation () {

        String tmpSubtitle = "";

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



