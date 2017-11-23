package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

    // ListView for meetings and suggestion
    ListView listViewMeetingSuggestion = null;

    // reference cursorAdapter for the listview
    MeetingOverviewCursorAdapter dataAdapterListViewMeeting = null;





    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeeting = layoutInflater.inflate(R.layout.fragment_meeting_overview, null);

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

    }




    private void initFragmentMeeting () {

        // init the DB
        myDb = new DBAdapter(fragmentMeetingContext);



        // find the listview for display meetings and suggestion, etc.
        listViewMeetingSuggestion = (ListView) viewFragmentMeeting.findViewById(R.id.listViewMeetingDates);




    }







    // show actual meetings, suggestion, cnaceled meetings and suggestions, old meetings
    private void displayActualMeetingSuggestionInformation () {

        String tmpSubtitle = "";


        // get all meetings from database in correct order
        Long nowTime = System.currentTimeMillis();
        Cursor cursorMeetingSuggestion = myDb.getAllRowsMeetingsAndSuggestion("future_meeting", nowTime);



        Log.d("Meeting","Anzahl Elemente: "+cursorMeetingSuggestion.getCount());


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
