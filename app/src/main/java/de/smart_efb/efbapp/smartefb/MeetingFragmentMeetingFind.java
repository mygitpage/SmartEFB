package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 04.01.2017.
 */
public class MeetingFragmentMeetingFind extends Fragment {


    // fragment view
    View viewFragmentMeetingFind;

    // fragment context
    Context fragmentMeetingFindContext = null;

    // reference to the DB
    DBAdapter myDb;

    // number of radio buttons for places -> result number of place (1 = Werder (Havel), 2 = Bad Belzig, 0 = no place selected)
    static final int countNumberPlaces = 2;

    // number of checkboxes for choosing timezones (look fragment meetingNow)
    static final int countNumberTimezones = 15;

    // boolean status array checkbox
    Boolean [] makeMeetingTimezoneSuggestionsArray = new Boolean[countNumberTimezones];

    // the current meeting date and time
    long currentMeetingDateAndTime;

    // meeting status
    int meetingStatus;

    // meeting status
    int meetingPlace;

    // meeting place name
    String meetingPlaceName = "";

    // meeting suggestions author
    String meetingSuggestionsAuthor = "";

    // reference to MeetingFindMeetingCursorAdapter
    MeetingFindMeetingCursorAdapter dataAdapterListViewFindMeeting;

    // reference to MeetingWaitingForRequestCursorAdapter
    MeetingWaitingForRequestCursorAdapter dataAdapterListViewWaitingRequest;




    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingFind = layoutInflater.inflate(R.layout.fragment_meeting_meeting_find, null);

        return viewFragmentMeetingFind;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingFindContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentMeetingFind();

        // show actual meeting informations
        displayActualMeetingInformation();

    }



    private void initFragmentMeetingFind () {

        // init the DB
        myDb = new DBAdapter(fragmentMeetingFindContext);

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get current time and date
        currentMeetingDateAndTime = ((ActivityMeeting)getActivity()).getMeetingTimeAndDate();

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get meeting status
        meetingStatus = ((ActivityMeeting)getActivity()).getMeetingStatus();

        // call getter-methode getMeetingPlace in ActivityMeeting to get current place
        meetingPlace = ((ActivityMeeting)getActivity()).getMeetingPlace();

        // call getter-methode getMeetingPlaceName in ActivityMeeting to get current place name
        meetingPlaceName = ((ActivityMeeting)getActivity()).getMeetingPlaceName(meetingPlace);

        //call getter-methode getMeetingPlaceName in ActivityMeeting to get current place name
        meetingSuggestionsAuthor = ((ActivityMeeting)getActivity()).getAuthorMeetingSuggestion();



        // call getter-methode getMeetingPlace in ActivityMeeting to get current place
        makeMeetingTimezoneSuggestionsArray = ((ActivityMeeting)getActivity()).getMeetingTimezoneSuggestions();

    }




    private void displayActualMeetingInformation () {

        String tmpSubtitle = "";

        Boolean showMakeFindFirstMeeting = false;
        Boolean waitingForAnswerOfFindFirstMeeting = false;
        Boolean confirmationForMeetingFindMeeting = false;
       


        switch (meetingStatus) {

            case 5: // first find meeting -> no meeting so far, suggestion comes from coach over internet
                showMakeFindFirstMeeting = true;
                break;
            case 6: // find meeting -> wait for response
                waitingForAnswerOfFindFirstMeeting = true;
                break;
            case 7: // find meeting -> wait for response
                confirmationForMeetingFindMeeting = true;
                break;

        }


        // meeting status 5 -> find meeting
        if (showMakeFindFirstMeeting) {

            // unset all approval meeting in table
            myDb.unsetAllStatusApprovalMeetingFindMeeting();

            // get all suggeste meetings from database
            Cursor cursor = myDb.getAllRowsSuggesteMeetings();

            // find the listview for diesplaying suggestinons
            ListView listView = (ListView) viewFragmentMeetingFind.findViewById(R.id.listDateAndTimeSuggestions);

            if (cursor.getCount() > 0 && listView != null) {

                // set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeeting", "string", fragmentMeetingFindContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);

                // set no suggestions text visibility gone
                TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoDateAndTimeSuggestions);
                tmpNoSuggestionsText.setVisibility(View.GONE);

                // set listview vivibility visible
                listView.setVisibility(View.VISIBLE);

                // new dataadapter
                dataAdapterListViewFindMeeting = new MeetingFindMeetingCursorAdapter(
                        getActivity(),
                        cursor,
                        0,
                        meetingSuggestionsAuthor);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapterListViewFindMeeting);


            }
            else {

                // set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeetingNoSuggestions", "string", fragmentMeetingFindContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);

                // set no suggestions text visibility gone
                TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoDateAndTimeSuggestions);
                tmpNoSuggestionsText.setVisibility(View.VISIBLE);

            }

        }


        // meeting status 6 -> auf Antwort warten
        if(waitingForAnswerOfFindFirstMeeting) {

            // get all choosen suggeste meetings from database
            Cursor cursor = myDb.getRowsChoosenSuggesteMeetings();

            // find the listview for diesplaying suggestinons
            ListView listView = (ListView) viewFragmentMeetingFind.findViewById(R.id.listDateAndTimeSuggestions);

            if (cursor.getCount() > 0 && listView != null) {

                // set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstWaitingRequest", "string", fragmentMeetingFindContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);

                // set no suggestions text visibility gone
                TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoDateAndTimeSuggestions);
                tmpNoSuggestionsText.setVisibility(View.GONE);

                // set listview vivibility visible
                listView.setVisibility(View.VISIBLE);

                // new dataadapter
                dataAdapterListViewWaitingRequest = new MeetingWaitingForRequestCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapterListViewWaitingRequest);

            }
            else {

                /// set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeetingNoSuggestions", "string", fragmentMeetingFindContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);

                // set no suggestions text visibility visible
                TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoDateAndTimeSuggestions);
                tmpNoSuggestionsText.setVisibility(View.VISIBLE);

            }


        }




        // meeting status 7 -> Antwort da, zeige Termin
        if(confirmationForMeetingFindMeeting) {


            /// set correct subtitle
            tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeetingShowDateAndTime", "string", fragmentMeetingFindContext.getPackageName()));
            ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);


            // show meeting intro text
            TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setVisibility(View.VISIBLE);

            


            // show date text and set visible
            TextView tmpShowDateText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingDate);
            tmpShowDateText.setVisibility(View.VISIBLE);
            String tmpDate = EfbHelperClass.timestampToDateFormat(currentMeetingDateAndTime, "dd.MM.yyyy");
            tmpShowDateText.setText(tmpDate);

            // show time text and set visible
            TextView tmpShowTimeText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingTime);
            tmpShowTimeText.setVisibility(View.VISIBLE);
            String tmpTime = EfbHelperClass.timestampToTimeFormat(currentMeetingDateAndTime, "kk:MM") + " " + fragmentMeetingFindContext.getResources().getString(R.string.showClockWordAdditionalText);
            tmpShowTimeText.setText(tmpTime);

            if (meetingPlace == 1 || meetingPlace == 2) { // show meeting place name

                // show time place and set visible
                TextView tmpShowPlaceText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingPlace);
                tmpShowPlaceText.setVisibility(View.VISIBLE);
                tmpShowPlaceText.setText(meetingPlaceName);
            }
        }
            
            
            
            

        
        
        

    }

}
