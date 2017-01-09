package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayout;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by ich on 12.12.2016.
 */
public class MeetingFragmentMeetingNow extends Fragment {


    // fragment view
    View viewFragmentMeetingNow;

    // fragment context
    Context fragmentMeetingNowContext = null;

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




    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingNow = layoutInflater.inflate(R.layout.fragment_meeting_meeting_now, null);

        return viewFragmentMeetingNow;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingNowContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentMeetingNow();

        // show actual meeting informations
        displayActualMeetingInformation();

    }



    private void initFragmentMeetingNow () {

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

        String txtNextMeetingIntro = "";
        String tmpSubtitle = "";
        String tmpSubtitleOrder = "";

        Button tmpButton;

        Boolean btnVisibilitySendMakeFirstMeeting = false;
        Boolean btnVisibilitySendFindMeetingDate = false;
        Boolean btnVisibilitySendChangeMeetingDate = false;
        Boolean showMakeFirstMeeting = false;
        Boolean showTimezoneAndPlaceSuggestion = false;
        Boolean showTimeAndDateConfirmed = false;
        Boolean showNextMeetingNotPossible = false;


        switch (meetingStatus) {


            case 0: // no time and date for meeting -> first meeting
                    txtNextMeetingIntro  = fragmentMeetingNowContext.getResources().getString(R.string.nextMeetingIntroTextNoMeeting);
                    btnVisibilitySendMakeFirstMeeting = true;
                    showMakeFirstMeeting = true;

                    tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleNoFirstMeeting", "string", fragmentMeetingNowContext.getPackageName()));
                    tmpSubtitleOrder = "noFirstMeeting";
                    break;

            case 1: // first meeting requested
                    txtNextMeetingIntro  = fragmentMeetingNowContext.getResources().getString(R.string.nextMeetingIntroTextFirstMeetingRequested);

                    showTimezoneAndPlaceSuggestion = true;
                    btnVisibilitySendChangeMeetingDate = true;

                    tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFirstMeetingRequested", "string", fragmentMeetingNowContext.getPackageName()));
                    tmpSubtitleOrder = "firstMeetingRequested";
                    break;

            case 2: // first meeting confirmed

                if (meetingPlace == 1) { // meeting is in Werder (Havel)
                    txtNextMeetingIntro = fragmentMeetingNowContext.getResources().getString(R.string.nextMeetingIntroTextFirstMeetingConfirmedTownWerder);
                }
                else if (meetingPlace == 2) { // meeting is in Bad Belzig
                    txtNextMeetingIntro = fragmentMeetingNowContext.getResources().getString(R.string.nextMeetingIntroTextFirstMeetingConfirmedTownBadBelzig);
                }
                else {
                    txtNextMeetingIntro = fragmentMeetingNowContext.getResources().getString(R.string.nextMeetingIntroTextFirstMeetingConfirmedTownNotSelected);
                }


                showTimeAndDateConfirmed = true;
                btnVisibilitySendChangeMeetingDate = true;

                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFirstMeetingConfirmed", "string", fragmentMeetingNowContext.getPackageName()));
                tmpSubtitleOrder = "firstMeetingConfirmed";
                break;

            case 3: // first meeting suggestion not possible -> please call us

                txtNextMeetingIntro  = fragmentMeetingNowContext.getResources().getString(R.string.nextMeetingNotPossiblePleaseCall);

                showNextMeetingNotPossible = true;

                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleFirstMeetingNotPossible", "string", fragmentMeetingNowContext.getPackageName()));
                tmpSubtitleOrder = "firstMeetingNotPossible";
                break;

        }

        // Set correct subtitle in Activity Meeting
        ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, tmpSubtitleOrder);


        // meeting status 0 -> ersttermin vereinbaren
        if (showMakeFirstMeeting) {

            // show meeting intro text
            TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setText(txtNextMeetingIntro);

            // set movement methode for explain text make meeting telephone link
            TextView tmpShowMeetingMakeExplainText = (TextView) viewFragmentMeetingNow.findViewById(R.id.showExplainTextForMeetingMake);
            tmpShowMeetingMakeExplainText.setVisibility(View.VISIBLE);
            tmpShowMeetingMakeExplainText.setMovementMethod(LinkMovementMethod.getInstance());


        }



        // meeting status 1 -> terminanfrage erstellen
        if (showTimezoneAndPlaceSuggestion) {

            // show meeting intro text
            TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setText(txtNextMeetingIntro);

            // show timezone suggestion intro text
            TextView tmpShowTimezoneSuggestionExplainText = (TextView) viewFragmentMeetingNow.findViewById(R.id.showTimezoneSuggestionExplainText);
            tmpShowTimezoneSuggestionExplainText.setVisibility(View.VISIBLE);

            // find gridView for timezone suggestion and set visible
            GridLayout tmpGridLayoutTimezoneSuggestion = (GridLayout) viewFragmentMeetingNow.findViewById(R.id.showMeetingTimezoneSuggestionGrid);
            tmpGridLayoutTimezoneSuggestion.setVisibility(View.VISIBLE);

            // set checked/unchecked for checkboxes timezone
            String tmpRessourceName ="";
            CheckBox tmpCheckBoxTimezone;
            for (int countTimezone = 0; countTimezone < countNumberTimezones; countTimezone++) {

                tmpRessourceName ="makeMeetingTimezone_" + (countTimezone+1);
                try {
                    int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentMeetingNowContext.getPackageName());

                    tmpCheckBoxTimezone = (CheckBox) viewFragmentMeetingNow.findViewById(resourceId);
                    if (!makeMeetingTimezoneSuggestionsArray[countTimezone]) {
                        tmpCheckBoxTimezone.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // show place suggestion intro text
            TextView tmpShowPlaceSuggestionExplainText = (TextView) viewFragmentMeetingNow.findViewById(R.id.showPlaceSuggestionExplainText);
            String tmpPlaceSuggestion = fragmentMeetingNowContext.getResources().getString(R.string.showPlaceSuggestionExplainText);
            tmpPlaceSuggestion = String.format(tmpPlaceSuggestion, meetingPlaceName);
            tmpShowPlaceSuggestionExplainText.setText(tmpPlaceSuggestion);
            tmpShowPlaceSuggestionExplainText.setVisibility(View.VISIBLE);

        }





        // meeting status 2 -> termin vorschlag eingegangen
        if (showTimeAndDateConfirmed) {

            // show meeting intro text
            TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setText(txtNextMeetingIntro);

            // show timezone suggestion intro text and set visiblity GONE
            TextView tmpShowTimezoneSuggestionExplainText = (TextView) viewFragmentMeetingNow.findViewById(R.id.showTimezoneSuggestionExplainText);
            tmpShowTimezoneSuggestionExplainText.setVisibility(View.GONE);

            // find gridView for timezone suggestion and set GONE
            GridLayout tmpGridLayoutTimezoneSuggestion = (GridLayout) viewFragmentMeetingNow.findViewById(R.id.showMeetingTimezoneSuggestionGrid);
            tmpGridLayoutTimezoneSuggestion.setVisibility(View.GONE);

            // find place suggestion intro text and set GONE
            TextView tmpShowPlaceSuggestionExplainText = (TextView) viewFragmentMeetingNow.findViewById(R.id.showPlaceSuggestionExplainText);
            tmpShowPlaceSuggestionExplainText.setVisibility(View.GONE);


            // show date text and set visible
            TextView tmpShowDateText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingDate);
            tmpShowDateText.setVisibility(View.VISIBLE);
            String tmpDate = EfbHelperClass.timestampToDateFormat(currentMeetingDateAndTime, "dd.MM.yyyy");
            tmpShowDateText.setText(tmpDate);

            // show time text and set visible
            TextView tmpShowTimeText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingTime);
            tmpShowTimeText.setVisibility(View.VISIBLE);
            String tmpTime = EfbHelperClass.timestampToTimeFormat(currentMeetingDateAndTime, "kk:MM") + " " + fragmentMeetingNowContext.getResources().getString(R.string.showClockWordAdditionalText);
            tmpShowTimeText.setText(tmpTime);

            if (meetingPlace == 1 || meetingPlace == 2) { // show meeting place name

                // show time place and set visible
                TextView tmpShowPlaceText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingPlace);
                tmpShowPlaceText.setVisibility(View.VISIBLE);
                tmpShowPlaceText.setText(meetingPlaceName);
            }
        }


        // meeting status 3 -> termin vorschlag nicht moeglich
        if (showNextMeetingNotPossible) {

            // show meeting intro text
            TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setText(txtNextMeetingIntro);

            // show meeting intro text
            TextView textExplainTextFirstMeetingNotPossible = (TextView) viewFragmentMeetingNow.findViewById(R.id.showExplainTextFirstMeetingNotPossible);
            textExplainTextFirstMeetingNotPossible.setVisibility(View.VISIBLE);
            textExplainTextFirstMeetingNotPossible.setMovementMethod(LinkMovementMethod.getInstance());

        }







        // set visibility of SendMakeFirstMeeting button to visible
        if (btnVisibilitySendMakeFirstMeeting) {

            tmpButton = (Button) viewFragmentMeetingNow.findViewById(R.id.buttonSendMakeFirstMeeting);
            tmpButton.setVisibility(View.VISIBLE);


            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(fragmentMeetingNowContext, ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","make_meeting");
                    fragmentMeetingNowContext.startActivity(intent);

                }
            });

        }

        // set visibility of SendFindMeetingDate button to visible
        if (btnVisibilitySendFindMeetingDate) {
            tmpButton = (Button) viewFragmentMeetingNow.findViewById(R.id.buttonSendFindMeetingDate);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(fragmentMeetingNowContext, ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","find_meeting");
                    fragmentMeetingNowContext.startActivity(intent);

                }
            });
        }

        // set visibility of SendChangeMeetingDate button to visible
        if (btnVisibilitySendChangeMeetingDate) {
            tmpButton = (Button) viewFragmentMeetingNow.findViewById(R.id.buttonSendChangeMeetingDate);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(fragmentMeetingNowContext, ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","change_meeting");
                    fragmentMeetingNowContext.startActivity(intent);

                }
            });
        }


    }




}
