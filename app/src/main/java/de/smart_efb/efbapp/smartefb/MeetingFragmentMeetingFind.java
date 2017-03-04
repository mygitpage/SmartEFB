package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

    // number of simultaneous meetings
    static final int numberSimultaneousMeetings = 2;

    // boolean status array checkbox
    Boolean [] makeMeetingTimezoneSuggestionsArray = new Boolean[countNumberTimezones];

    // the current meeting date and time
    long [] currentMeetingDateAndTime = new long[numberSimultaneousMeetings];

    // meeting status
    int [] meetingPlace = new int[numberSimultaneousMeetings];

    // meeting place name
    String [] meetingPlaceName = new String [numberSimultaneousMeetings];

    // meeting status
    int meetingStatus;

    // meeting suggestions author
    String meetingSuggestionsAuthor = "";

    // deadline for responding of meeting suggestions
    long meetingSuggestionsResponeseDeadline = 0;

    // reference to MeetingFindMeetingCursorAdapter
    MeetingFindMeetingCursorAdapter dataAdapterListViewFindMeeting;

    // reference to MeetingWaitingForRequestCursorAdapter
    MeetingWaitingForRequestCursorAdapter dataAdapterListViewWaitingRequest;

    // reference to MeetingMakeMeetingAndShowMeetingCursorAdapter
    MeetingMakeMeetingAndShowMeetingCursorAdapter dataAdapterListViewMakeAndShow;
    
    // 
    int indexNumberFirstMeetingToShow = 0;
    int indexNumberSecondMeetingToShow = 0;




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

        // call getter-methode getSuggestionsResponeseDeadline in ActivityMeeting to get deadline for responding of meeting suggestions
        meetingSuggestionsResponeseDeadline = ((ActivityMeeting)getActivity()).getSuggestionsResponeseDeadline();

        // call getter-methode getMeetingPlaceName in ActivityMeeting to get current place name
        for (int t=0; t<numberSimultaneousMeetings; t++) {
            meetingPlaceName[t] = ((ActivityMeeting)getActivity()).getMeetingPlaceName(meetingPlace[t]);
        }

        //call getter-methode getMeetingPlaceName in ActivityMeeting to get current place name
        meetingSuggestionsAuthor = ((ActivityMeeting)getActivity()).getAuthorMeetingSuggestion();

        // call getter-methode getMeetingPlace in ActivityMeeting to get current place
        makeMeetingTimezoneSuggestionsArray = ((ActivityMeeting)getActivity()).getMeetingTimezoneSuggestions();

    }


    // look for meeting status and show current data
    private void displayActualMeetingInformation () {

        String tmpSubtitle = "";

        // meeting status 5 -> find meeting
        if (meetingStatus == 5) {

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
                        meetingSuggestionsAuthor,
                        meetingSuggestionsResponeseDeadline);

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
        if(meetingStatus == 6) {

            int tmpIndexNumber = 0;

            // which meeting data to parse to dataAdapter, default is 0
            if ((currentMeetingDateAndTime[0] > currentMeetingDateAndTime[1] ) && currentMeetingDateAndTime[1] != 0) {
                tmpIndexNumber = 1;
            }

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
                        0,
                        currentMeetingDateAndTime[tmpIndexNumber],
                        meetingPlaceName[tmpIndexNumber],
                        tmpIndexNumber);

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

        // meeting status 7 -> Antwort da, zeige Termine (maximal zwei Termine koennen angezeigt werden)
        if(meetingStatus == 7) {

            // call delete-methode unsetNewStatusMeeting in ActivityMeeting to unset new status meetings (both new status for meeting is unset)
            ((ActivityMeeting)getActivity()).unsetNewStatusMeeting();

            if (currentMeetingDateAndTime[0] != 0 || currentMeetingDateAndTime[1] != 0) { //is min. one meeting set?

                if (currentMeetingDateAndTime[0] > 0 && currentMeetingDateAndTime[1] > 0) {

                    // set correct subtitle
                    tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeetingShowDateAndTimePlural", "string", fragmentMeetingFindContext.getPackageName()));
                    ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle(tmpSubtitle);


                    // show linear layout for date and time for first meeting
                    LinearLayout linearLayoutViewNextMeeting_A = (LinearLayout) viewFragmentMeetingFind.findViewById(R.id.containerShowNextMeetingDateAndTime_A);
                    linearLayoutViewNextMeeting_A.setVisibility(View.VISIBLE);


                    // show linear layout for date and time for second meeting
                    LinearLayout linearLayoutViewNextMeeting_B = (LinearLayout) viewFragmentMeetingFind.findViewById(R.id.containerShowNextMeetingDateAndTime_B);
                    linearLayoutViewNextMeeting_B.setVisibility(View.VISIBLE);

                    // show meeting intro text for first meeting
                    TextView textViewNextMeetingIntroText_A = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingIntroText_A);
                    String tmpTextViewNextMeetingIntroText = fragmentMeetingFindContext.getResources().getString(R.string.textShowDateAndTimeForMeetingConfirmationPlural);
                    textViewNextMeetingIntroText_A.setText(tmpTextViewNextMeetingIntroText);
                    textViewNextMeetingIntroText_A.setVisibility(View.VISIBLE);

                    // select order to show meetings
                    if (currentMeetingDateAndTime[0] >= currentMeetingDateAndTime[1]) {
                        indexNumberFirstMeetingToShow = 1;
                        indexNumberSecondMeetingToShow = 0;
                    }
                    else  {
                        indexNumberFirstMeetingToShow = 0;
                        indexNumberSecondMeetingToShow = 1;
                    }

                    // show meeting A ----------------------------
                    // show date text and set visible
                    TextView tmpShowDateText_A = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingDate_A);
                    tmpShowDateText_A.setVisibility(View.VISIBLE);
                    String tmpDate_A = EfbHelperClass.timestampToDateFormat(currentMeetingDateAndTime[indexNumberFirstMeetingToShow], "dd.MM.yyyy");
                    tmpShowDateText_A.setText(tmpDate_A);

                    // show time text and set visible
                    TextView tmpShowTimeText_A = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingTime_A);
                    tmpShowTimeText_A.setVisibility(View.VISIBLE);
                    String tmpTime_A = EfbHelperClass.timestampToTimeFormat(currentMeetingDateAndTime[indexNumberFirstMeetingToShow], "HH:mm") + " " + fragmentMeetingFindContext.getResources().getString(R.string.showClockWordAdditionalText);
                    tmpShowTimeText_A.setText(tmpTime_A);

                    // show time place and set visible
                    TextView tmpShowPlaceText_A = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingPlace_A);
                    tmpShowPlaceText_A.setVisibility(View.VISIBLE);
                    if (meetingPlace[indexNumberFirstMeetingToShow] == 1 || meetingPlace[indexNumberFirstMeetingToShow] == 2) { // show meeting place name
                        tmpShowPlaceText_A.setText(meetingPlaceName[indexNumberFirstMeetingToShow]);
                    } else {
                        tmpShowPlaceText_A.setText(meetingPlaceName[0]); // show place "Kein Ort gewaehlt"
                    }

                    // show button abbort meeting
                    Button btnAbbortMeeting_A = (Button) viewFragmentMeetingFind.findViewById(R.id.buttonSendChangeMeetingDate_A);
                    btnAbbortMeeting_A.setVisibility(View.VISIBLE);
                    
                    btnAbbortMeeting_A.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(fragmentMeetingFindContext, ActivityMeeting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("com","change_meeting");
                            intent.putExtra("met_index",indexNumberFirstMeetingToShow);
                            intent.putExtra("met_backto","find_meeting");
                            fragmentMeetingFindContext.startActivity(intent);

                        }
                    });

                    // show meeting B ------------------------
                    // show date text and set visible
                    TextView tmpShowDateText_B = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingDate_B);
                    tmpShowDateText_B.setVisibility(View.VISIBLE);
                    String tmpDate_B = EfbHelperClass.timestampToDateFormat(currentMeetingDateAndTime[indexNumberSecondMeetingToShow], "dd.MM.yyyy");
                    tmpShowDateText_B.setText(tmpDate_B);

                    // show time text and set visible
                    TextView tmpShowTimeText_B = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingTime_B);
                    tmpShowTimeText_B.setVisibility(View.VISIBLE);
                    String tmpTime_B = EfbHelperClass.timestampToTimeFormat(currentMeetingDateAndTime[indexNumberSecondMeetingToShow], "HH:mm") + " " + fragmentMeetingFindContext.getResources().getString(R.string.showClockWordAdditionalText);
                    tmpShowTimeText_B.setText(tmpTime_B);

                    // show time place and set visible
                    TextView tmpShowPlaceText_B = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingPlace_B);
                    tmpShowPlaceText_B.setVisibility(View.VISIBLE);
                    if (meetingPlace[indexNumberSecondMeetingToShow ] == 1 || meetingPlace[indexNumberSecondMeetingToShow ] == 2) { // show meeting place name
                        tmpShowPlaceText_B.setText(meetingPlaceName[indexNumberSecondMeetingToShow ]);
                    } else {
                        tmpShowPlaceText_B.setText(meetingPlaceName[0]); // show place "Kein Ort gewaehlt"
                    }

                    // show button abbort meeting
                    Button btnAbbortMeeting_B = (Button) viewFragmentMeetingFind.findViewById(R.id.buttonSendChangeMeetingDate_B);
                    btnAbbortMeeting_B.setVisibility(View.VISIBLE);

                    btnAbbortMeeting_B.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(fragmentMeetingFindContext, ActivityMeeting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("com","change_meeting");
                            intent.putExtra("met_index",indexNumberSecondMeetingToShow);
                            intent.putExtra("met_backto","find_meeting");
                            fragmentMeetingFindContext.startActivity(intent);

                        }
                    });

                } else { // only one meeting is set

                    indexNumberFirstMeetingToShow = 0;

                    // set correct subtitle
                    tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindFirstMeetingShowDateAndTimeSingular", "string", fragmentMeetingFindContext.getPackageName()));
                    ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle(tmpSubtitle);

                    // show meeting intro text for first meeting
                    TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingIntroText_A);
                    textViewNextMeetingIntroText.setVisibility(View.VISIBLE);

                    // show linear layout for date and time for first meeting
                    LinearLayout linearLayoutViewNextMeeting = (LinearLayout) viewFragmentMeetingFind.findViewById(R.id.containerShowNextMeetingDateAndTime_A);
                    linearLayoutViewNextMeeting.setVisibility(View.VISIBLE);

                    if (currentMeetingDateAndTime[0] == 0 && currentMeetingDateAndTime[1] != 0) {
                        indexNumberFirstMeetingToShow = 1;
                    } else if (currentMeetingDateAndTime[0] != 0 && currentMeetingDateAndTime[1] == 0) {
                        indexNumberFirstMeetingToShow = 0;
                    }

                    // show date text and set visible
                    TextView tmpShowDateText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingDate_A);
                    tmpShowDateText.setVisibility(View.VISIBLE);
                    String tmpDate = EfbHelperClass.timestampToDateFormat(currentMeetingDateAndTime[indexNumberFirstMeetingToShow], "dd.MM.yyyy");
                    tmpShowDateText.setText(tmpDate);

                    // show time text and set visible
                    TextView tmpShowTimeText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingTime_A);
                    tmpShowTimeText.setVisibility(View.VISIBLE);
                    String tmpTime = EfbHelperClass.timestampToTimeFormat(currentMeetingDateAndTime[indexNumberFirstMeetingToShow], "HH:mm") + " " + fragmentMeetingFindContext.getResources().getString(R.string.showClockWordAdditionalText);
                    tmpShowTimeText.setText(tmpTime);

                    // show time place and set visible
                    TextView tmpShowPlaceText = (TextView) viewFragmentMeetingFind.findViewById(R.id.nextMeetingPlace_A);
                    tmpShowPlaceText.setVisibility(View.VISIBLE);
                    if (meetingPlace[indexNumberFirstMeetingToShow] == 1 || meetingPlace[indexNumberFirstMeetingToShow] == 2) { // show meeting place name
                        tmpShowPlaceText.setText(meetingPlaceName[indexNumberFirstMeetingToShow]);
                    } else {
                        tmpShowPlaceText.setText(meetingPlaceName[0]); // show place "Kein Ort gewaehlt"
                    }

                    // show button abbort meeting
                    Button btnAbbortMeeting = (Button) viewFragmentMeetingFind.findViewById(R.id.buttonSendChangeMeetingDate_A);
                    btnAbbortMeeting.setVisibility(View.VISIBLE);

                    btnAbbortMeeting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(fragmentMeetingFindContext, ActivityMeeting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("com","change_meeting");
                            intent.putExtra("met_index",indexNumberFirstMeetingToShow);
                            intent.putExtra("met_backto","find_meeting");
                            fragmentMeetingFindContext.startActivity(intent);

                        }
                    });

                }


            }
            else { // no meeting set (old or something else)

                /// set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFindMeetingNoMoreMeetingSet", "string", fragmentMeetingFindContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle(tmpSubtitle);

                // set movement methode for telephone link in info text
                TextView tmpShowMeetingNoMoreMeetingAvailable = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoActualMeetingAvailable);
                tmpShowMeetingNoMoreMeetingAvailable.setVisibility(View.VISIBLE);
                tmpShowMeetingNoMoreMeetingAvailable.setMovementMethod(LinkMovementMethod.getInstance());

            }
        }


        // meeting status 8 -> Termin steht fest und neue Terminvorschläge sind da
        if(meetingStatus == 8) {

            // call delete-methode unsetNewStatusMeeting in ActivityMeeting to unset new status meetings (both new status for meeting is unset)
            ((ActivityMeeting)getActivity()).unsetNewStatusMeeting();

            int tmpIndexNumber = 0;

            // which meeting data to parse to dataAdapter, default is 0
            if ((currentMeetingDateAndTime[0] > currentMeetingDateAndTime[1] ) && currentMeetingDateAndTime[1] != 0) {
                tmpIndexNumber = 1;
            }


            // unset all approval meeting in table
            myDb.unsetAllStatusApprovalMeetingFindMeeting();

            // get all suggeste meetings from database
            Cursor cursor = myDb.getAllRowsSuggesteMeetings();

            // find the listview for diesplaying suggestinons
            ListView listView = (ListView) viewFragmentMeetingFind.findViewById(R.id.listDateAndTimeSuggestions);

            if (cursor.getCount() > 0 && listView != null) {

                // set correct subtitle
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMakeMeetingAndShowMeeting", "string", fragmentMeetingFindContext.getPackageName()));
                ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);

                // set no suggestions text visibility gone
                TextView tmpNoSuggestionsText = (TextView) viewFragmentMeetingFind.findViewById(R.id.meetingFindMeetingNoDateAndTimeSuggestions);
                tmpNoSuggestionsText.setVisibility(View.GONE);

                // set listview vivibility visible
                listView.setVisibility(View.VISIBLE);

                // new dataadapter with own constructor
                dataAdapterListViewMakeAndShow = new MeetingMakeMeetingAndShowMeetingCursorAdapter(
                        getActivity(),
                        cursor,
                        0,
                        meetingSuggestionsAuthor,
                        currentMeetingDateAndTime[tmpIndexNumber],
                        meetingPlaceName[tmpIndexNumber],
                        meetingSuggestionsResponeseDeadline);


                // Assign adapter to ListView
                listView.setAdapter(dataAdapterListViewMakeAndShow);

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

    }

}
