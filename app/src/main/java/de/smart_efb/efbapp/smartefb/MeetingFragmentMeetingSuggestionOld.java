package de.smart_efb.efbapp.smartefb;

import android.content.Context;
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

public class MeetingFragmentMeetingSuggestionOld extends Fragment {


    // fragment view
    View viewFragmentMeetingSuggestion;

    // fragment context
    Context fragmentMeetingSuggestionContext = null;

    // reference to the DB
    DBAdapter myDb;

    // ListView for meetings and suggestion
    ListView listViewMeetingSuggestion = null;

    // reference cursorAdapter for the listview
    MeetingSuggestionOldOverviewCursorAdapter dataAdapterListViewMeetingSuggestionOld = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingSuggestion = layoutInflater.inflate(R.layout.fragment_meeting_suggestion_old, null);

        return viewFragmentMeetingSuggestion;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingSuggestionContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentSuggestion();

        // show actual meeting and suggestion informations
        displayActualSuggestionInformation();

    }




    private void initFragmentSuggestion () {

        // init the DB
        myDb = new DBAdapter(fragmentMeetingSuggestionContext);

        // find the listview for display meetings and suggestion, etc.
        listViewMeetingSuggestion = (ListView) viewFragmentMeetingSuggestion.findViewById(R.id.listViewOldMeetingDates);

    }



    // show actual suggestion overview
    private void displayActualSuggestionInformation () {

        String tmpSubtitle = "";

        // get all meetings and suggestion from database in correct order
        Long nowTime = System.currentTimeMillis();
        Cursor cursorMeetingSuggestion = myDb.getAllRowsMeetingsAndSuggestion("old_meeting", nowTime);

        if (cursorMeetingSuggestion.getCount() > 0 && listViewMeetingSuggestion != null) {

            // set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMeetingSuggestionOverviewOld", "string", fragmentMeetingSuggestionContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "meeting_suggestion_old");

            // set old meeting text visibility gone
            TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingSuggestion.findViewById(R.id.meetingSuggestionOldOverviewNothingAvailable);
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
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMeetingSuggestionOverviewOldNotAvailable", "string", fragmentMeetingSuggestionContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "meeting_suggestion_old");

            // set old meeting text visibility show
            TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingSuggestion.findViewById(R.id.meetingSuggestionOldOverviewNothingAvailable);
            tmpNoSuggestionsText.setVisibility(View.VISIBLE);

        }
    }

}
