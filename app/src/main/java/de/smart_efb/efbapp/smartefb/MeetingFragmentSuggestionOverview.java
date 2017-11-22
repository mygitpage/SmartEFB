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

public class MeetingFragmentSuggestionOverview extends Fragment{


        // fragment view
        View viewFragmentSuggestion;

        // fragment context
        Context fragmentSuggestionContext = null;

        // reference to the DB
        DBAdapter myDb;

        // ListView for meetings and suggestion
        ListView listViewMeetingSuggestion = null;

        // reference cursorAdapter for the listview
        MeetingSuggestionOverviewCursorAdapter dataAdapterListViewSuggestion = null;





        @Override
        public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

            viewFragmentSuggestion = layoutInflater.inflate(R.layout.fragment_suggestion_overview, null);

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



            // find the listview for display meetings and suggestion, etc.
            listViewMeetingSuggestion = (ListView) viewFragmentSuggestion.findViewById(R.id.listViewSuggestionDates);




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