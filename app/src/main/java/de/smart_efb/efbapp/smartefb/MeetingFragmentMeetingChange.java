package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 04.01.2017.
 */
public class MeetingFragmentMeetingChange extends Fragment {


    // fragment view
    View viewFragmentMeetingChange;

    // fragment context
    Context fragmentMeetingChangeContext = null;

    // number of simultaneous meetings
    static final int numberSimultaneousMeetings = 2;


    

    /// the current meeting date and time
    long [] currentMeetingDateAndTime = new long [numberSimultaneousMeetings];

    // meeting place
    int [] meetingPlace = new int[numberSimultaneousMeetings];

    // meeting place name
    String [] meetingPlaceName = new String [numberSimultaneousMeetings];

    // meeting status
    int meetingStatus = 0;


    // index of meeting to change/delete (0,1 is possible)
    int indexNumberMeetingToShow = 0;
      


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingChange = layoutInflater.inflate(R.layout.fragment_meeting_meeting_change, null);

        return viewFragmentMeetingChange;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingChangeContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        //initFragmentMeetingNow();

        // show actual meeting informations
        //displayActualMeetingInformation();

    }


    /*

    private void initFragmentMeetingNow () {

        
        // call getter-methode getMeetingIndexToChange in ActivityMeeting to get index of meeting to change/delete
        indexNumberMeetingToShow = ((ActivityMeeting)getActivity()).getMeetingIndexToChange();

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get current time and date array (2 simultaneous meetings are possible)
        currentMeetingDateAndTime = ((ActivityMeeting)getActivity()).getMeetingTimeAndDate();

        // call getter-methode getMeetingPlace in ActivityMeeting to get current place
        meetingPlace = ((ActivityMeeting)getActivity()).getMeetingPlace();

        // call getter-methode getMeetingPlaceName in ActivityMeeting to get current place name
        for (int t=0; t<numberSimultaneousMeetings; t++) {
            meetingPlaceName[t] = ((ActivityMeeting)getActivity()).getMeetingPlaceName(meetingPlace[t]);
        }

        // get meeting status
        meetingStatus = ((ActivityMeeting)getActivity()).getMeetingStatus();





    }

    // show fragment ressources
    private void displayActualMeetingInformation () {

        

        // Set correct subtitle in Activity Meeting Fragment make first meeting
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleChangeMeeting", "string", fragmentMeetingChangeContext.getPackageName()));
        ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);


        // set detail intro text movement
        TextView tmpChangeMeetingIntroDetailDescripton = (TextView) viewFragmentMeetingChange.findViewById(R.id.changeMeetingIntroDetailDescripton);
        tmpChangeMeetingIntroDetailDescripton.setMovementMethod(LinkMovementMethod.getInstance());

        // show date text of meeting to change/delete
        TextView tmpShowDateText = (TextView) viewFragmentMeetingChange.findViewById(R.id.changeMeetingDate);
        String tmpDate = EfbHelperClass.timestampToDateFormat(currentMeetingDateAndTime[indexNumberMeetingToShow], "dd.MM.yyyy");
        tmpShowDateText.setText(tmpDate);

        // show time text of meeting to change/delete
        TextView tmpShowTimeText = (TextView) viewFragmentMeetingChange.findViewById(R.id.changeMeetingTime);
        String tmpTime = EfbHelperClass.timestampToTimeFormat(currentMeetingDateAndTime[indexNumberMeetingToShow], "HH:mm") + " " + fragmentMeetingChangeContext.getResources().getString(R.string.showClockWordAdditionalText);
        tmpShowTimeText.setText(tmpTime);

        // show time place and set visible
        TextView tmpShowPlaceText = (TextView) viewFragmentMeetingChange.findViewById(R.id.changeMeetingPlace);
        if (meetingPlace[indexNumberMeetingToShow] == 1 || meetingPlace[indexNumberMeetingToShow] == 2) { // show meeting place name
            tmpShowPlaceText.setText(meetingPlaceName[indexNumberMeetingToShow]);
        } else {
            tmpShowPlaceText.setText(meetingPlaceName[0]); // show place "Kein Ort gewaehlt"
        }

        // find send button "Absage senden"
        Button tmpButton;
        tmpButton = (Button) viewFragmentMeetingChange.findViewById(R.id.buttonConfirmChangeMeeting);

        // onClick listener make meeting
        tmpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean changeMeetingNoError = true;

                TextView tmpErrorTextView;

                // check edit text border (<3 and >500)
                EditText tmpInputChangeMeetingReason = (EditText) viewFragmentMeetingChange.findViewById(R.id.inputChangeMeetingReason);
                String tmpTextInputChangeMeetingReason = "";
                if (tmpInputChangeMeetingReason != null) {
                    tmpTextInputChangeMeetingReason = tmpInputChangeMeetingReason.getText().toString();

                    // get text view for error
                    tmpErrorTextView = (TextView) viewFragmentMeetingChange.findViewById(R.id.errorInputChangeMeetingReason);
                    // check for errors
                    if ((tmpTextInputChangeMeetingReason.length() < 3 || tmpTextInputChangeMeetingReason.length() > 500) && tmpErrorTextView != null ) {
                        changeMeetingNoError = false;
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    }
                    else if (tmpErrorTextView != null){
                        tmpErrorTextView.setVisibility(View.GONE);
                    }

                }

                // input error?
                if (changeMeetingNoError) {


                    // TODO ->
                    // Termin-ID erzeugen
                    // Netzwerk status pruefen
                    // Terminabsage senden

                    // delete text from input field
                    if (tmpInputChangeMeetingReason != null) {
                       tmpInputChangeMeetingReason.setText("");
                    }

                        // Delete meeting information in smartphone
                    ((ActivityMeeting)getActivity()).deleteMeetingTimestampAndPlace(indexNumberMeetingToShow);


                    // Toast "change (delete) meeting sucessfully send"
                    Toast.makeText(fragmentMeetingChangeContext, fragmentMeetingChangeContext.getResources().getString(R.string.changeDeleteMeetingSendSuccesfullToastText), Toast.LENGTH_SHORT).show();

                    // call getter for info back to fragment
                    String tmpBackTo = ((ActivityMeeting)getActivity()).getMeetingBackToFragment ();

                    if (tmpBackTo.equals("find_meeting")) {

                        // send intent back to fragment find meeting
                        Intent intent = new Intent(fragmentMeetingChangeContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com", "find_meeting");
                        intent.putExtra("update", false);
                        intent.putExtra("met_status", meetingStatus);
                        intent.putExtra("pop_stack", true);
                        fragmentMeetingChangeContext.startActivity(intent);

                    } else {

                        // send intent back to fragment now meeting
                        Intent intent = new Intent(fragmentMeetingChangeContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com", "now_meeting");
                        intent.putExtra("met_status", meetingStatus);
                        intent.putExtra("pop_stack", true);
                        fragmentMeetingChangeContext.startActivity(intent);
                    }

                }
                else {

                    // Toast "change (delete) meeting not sucessfully send"
                    Toast.makeText(fragmentMeetingChangeContext, fragmentMeetingChangeContext.getResources().getString(R.string.changeDeleteMeetingNotSendSuccesfullToastText), Toast.LENGTH_SHORT).show();


                }
            }
        });
        



        // Button Abbort change meeting
        // find button Abbort
        Button tmpButtonAbbort;
        tmpButtonAbbort = (Button) viewFragmentMeetingChange.findViewById(R.id.buttonAbbortChangeMeeting);

        // onClick listener make meeting
        tmpButtonAbbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // delete text from input field
                EditText tmpInputChangeMeetingReason = (EditText) viewFragmentMeetingChange.findViewById(R.id.inputChangeMeetingReason);
                if (tmpInputChangeMeetingReason != null) {
                    tmpInputChangeMeetingReason.setText("");
                }

                // call getter for info back to fragment
                String tmpBackTo = ((ActivityMeeting)getActivity()).getMeetingBackToFragment ();

                if (tmpBackTo.equals("find_meeting")) {

                        // send intent back to fragment find meeting
                        Intent intent = new Intent(fragmentMeetingChangeContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com", "find_meeting");
                        intent.putExtra("update", false);
                        intent.putExtra("met_status", meetingStatus);
                        intent.putExtra("pop_stack", true);
                        fragmentMeetingChangeContext.startActivity(intent);

                } else {
                        // send intent back to fragment now meeting
                        Intent intent = new Intent(fragmentMeetingChangeContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com", "now_meeting");
                        intent.putExtra("met_status", meetingStatus);
                        intent.putExtra("pop_stack", true);
                        fragmentMeetingChangeContext.startActivity(intent);
                }

            }


        });



        
        
        

    }

    */


}
