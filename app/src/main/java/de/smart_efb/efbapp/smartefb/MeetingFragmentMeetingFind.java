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

    // reference to MeetingFindMeetingCursorAdapter
    MeetingFindMeetingCursorAdapter dataAdapterListViewFindMeeting;




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

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get current time and date
        currentMeetingDateAndTime = ((ActivityMeeting)getActivity()).getMeetingTimeAndDate();

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get meeting status
        meetingStatus = ((ActivityMeeting)getActivity()).getMeetingStatus();

        // call getter-methode getMeetingPlace in ActivityMeeting to get current place
        meetingPlace = ((ActivityMeeting)getActivity()).getMeetingPlace();

        // call getter-methode getMeetingPlaceName in ActivityMeeting to get current place name
        meetingPlaceName = ((ActivityMeeting)getActivity()).getMeetingPlaceName(meetingPlace);

        // call getter-methode getMeetingPlace in ActivityMeeting to get current place
        makeMeetingTimezoneSuggestionsArray = ((ActivityMeeting)getActivity()).getMeetingTimezoneSuggestions();

    }




    private void displayActualMeetingInformation () {

        String txtFindFirstMeetingIntro = "";
        String tmpSubtitle = "";
        String tmpSubtitleOrder = "";

        Button tmpButton;

        Boolean btnVisibilitySendMakeFirstMeeting = false;
        Boolean btnVisibilitySendFindMeetingDate = false;
        Boolean btnVisibilitySendChangeMeetingDate = false;
        Boolean showMakeFindFirstMeeting = false;
       


        switch (meetingStatus) {


            case 5: // first find meeting -> no meeting so far, suggestion comes from coach over internet
                txtFindFirstMeetingIntro  = fragmentMeetingFindContext.getResources().getString(R.string.findMeetingIntroTextNoMeetingSoFar);
                showMakeFindFirstMeeting = true;


                break;



        }


        // meeting status 5 -> find meeting
        if (showMakeFindFirstMeeting) {

            // get all suggeste meetings from database
            Cursor cursor = myDb.getAllRowsSuggesteMeetings();

            // show meeting intro text
            TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setText(txtFindFirstMeetingIntro);

            // set movement methode for explain text find first + meeting telephone link
            TextView tmpTextFindFirstMeetingExplainText = (TextView) viewFragmentMeetingFind.findViewById(R.id.showExplainTextForFindFirstMeeting);
            tmpTextFindFirstMeetingExplainText.setVisibility(View.VISIBLE);
            tmpTextFindFirstMeetingExplainText.setMovementMethod(LinkMovementMethod.getInstance());




            // find the listview for diesplaying suggestinons
            ListView listView = (ListView) viewFragmentMeetingFind.findViewById(R.id.listDateAndTimeSuggestions);



            if (cursor.getCount() > 0 && listView != null) {

                // set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeeting", "string", fragmentMeetingFindContext.getPackageName()));
                tmpSubtitleOrder = "findFirstMeeting";
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, tmpSubtitleOrder);


                // set no suggestions text visibility gone
                TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoDateAndTimeSuggestions);
                tmpNoSuggestionsText.setVisibility(View.GONE);

                // set listview vivibility visible
                listView.setVisibility(View.VISIBLE);




                // new dataadapter
                dataAdapterListViewFindMeeting = new MeetingFindMeetingCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapterListViewFindMeeting);

            }
            else {

                /// set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeetingNoSuggestions", "string", fragmentMeetingFindContext.getPackageName()));
                tmpSubtitleOrder = "findFirstMeeting";
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, tmpSubtitleOrder);

                // set no suggestions text visibility gone
                TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoDateAndTimeSuggestions);
                tmpNoSuggestionsText.setVisibility(View.VISIBLE);





            }











        }



        






    }

}
