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
    View viewFragmentMeetingMake;

    // fragment context
    Context fragmentMeetingMakeContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // shared prefs for storing
    SharedPreferences.Editor prefsEditor;

    // the current meeting date and time
    long currentMeetingDateAndTime;

    // meeting status
    int meetingStatus;

    // number of checkboxes for choosing timezones (look fragment meetingNow)
    static final int countNumberTimezones = 15;

    // boolean status array checkbox
    Boolean [] makeMeetingCheckBoxListenerArray = new Boolean[countNumberTimezones];

    // count selected checkBoxes for border check
    int countSelectedCheckBoxesTimezone = 0;

    // number of radio buttons for choosing places
    static final int countNumberPlaces = 2;

    // result number of place (1 = Werder (Havel), 2 = Bad Belzig, 0 = no place selected)
    int resultNumberOfPlace = 0;



    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingMake = layoutInflater.inflate(R.layout.fragment_meeting_meeting_change, null);

        Log.d("Meeting","onCreateView");

        return viewFragmentMeetingMake;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingMakeContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentMeetingNow();

        // show actual meeting informations
        displayActualMeetingInformation();

    }



    private void initFragmentMeetingNow () {

        // init the prefs
        prefs = fragmentMeetingMakeContext.getSharedPreferences("smartEfbSettings", fragmentMeetingMakeContext.MODE_PRIVATE);

        // init prefs editor
        prefsEditor = prefs.edit();

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get current time and date
        currentMeetingDateAndTime = ((ActivityMeeting)getActivity()).getMeetingTimeAndDate();

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get meeting status
        meetingStatus = ((ActivityMeeting)getActivity()).getMeetingStatus();






    }

    // show fragment ressources
    private void displayActualMeetingInformation () {

        String tmpSubtitle = "";
        String tmpSubtitleOrder = "";

        Button tmpButton;

        Boolean btnVisibilitySendMakeFirstMeeting = false;



        switch (meetingStatus) {


            case 0:
            default: // no time and date for meeting -> first meeting
                btnVisibilitySendMakeFirstMeeting = true;
                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMakeFirstMeeting", "string", fragmentMeetingMakeContext.getPackageName()));
                tmpSubtitleOrder = "makeFirstMeeting";
                //tmpSubtitle = String.format(tmpSubtitle, jointlyGoalNumberInListView);
                break;

        }

        // Set correct subtitle in Activity Meeting Fragment make first meeting
        ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle);

        // status make first meeting
        if (btnVisibilitySendMakeFirstMeeting) {

            // set movement methode for telephone link in intro text
            TextView tmpShowMeetingExplainText = (TextView) viewFragmentMeetingMake.findViewById(R.id.makeFirstMeetingExplainText);
            tmpShowMeetingExplainText.setMovementMethod(LinkMovementMethod.getInstance());

            // set movement methode for telephone link in info text
            TextView tmpShowMeetingProcedureText = (TextView) viewFragmentMeetingMake.findViewById(R.id.infoMakeMeetingProcedure);
            tmpShowMeetingProcedureText.setMovementMethod(LinkMovementMethod.getInstance());

            // find send button "Anfrage senden"
            tmpButton = (Button) viewFragmentMeetingMake.findViewById(R.id.buttonSendSuggestionFirstMeeting);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Boolean makeMeetingNoError = true;

                    TextView tmpErrorTextView;

                    // check check boxes result (border <2)
                    tmpErrorTextView = (TextView) viewFragmentMeetingMake.findViewById(R.id.makeFirstMeetingChooseTimezoneError);
                    if ( countSelectedCheckBoxesTimezone < 3 && tmpErrorTextView != null) {
                        makeMeetingNoError = false;
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    } else if (tmpErrorTextView != null) {
                        tmpErrorTextView.setVisibility(View.GONE);
                    }


                    // check radio buttons result
                    tmpErrorTextView = (TextView) viewFragmentMeetingMake.findViewById(R.id.makeFirstMeetingChoosePlaceError);
                    if ( resultNumberOfPlace <= 0 && tmpErrorTextView != null) {
                        makeMeetingNoError = false;
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    } else if (tmpErrorTextView != null) {
                        tmpErrorTextView.setVisibility(View.GONE);
                    }


                    // check edit text border (<3 and >500)
                    EditText tmpInputFirstMeetingProblem = (EditText) viewFragmentMeetingMake.findViewById(R.id.inputFirstMeetingProblemText);
                    String tmpTextInputFirstMeetingProblem = "";
                    if (tmpInputFirstMeetingProblem != null) {
                        tmpTextInputFirstMeetingProblem = tmpInputFirstMeetingProblem.getText().toString();

                        tmpErrorTextView = (TextView) viewFragmentMeetingMake.findViewById(R.id.makeFirstMeetingProblemError);
                        if ((tmpTextInputFirstMeetingProblem.length() < 3 || tmpTextInputFirstMeetingProblem.length() > 500) && tmpErrorTextView != null ) {
                            makeMeetingNoError = false;
                            tmpErrorTextView.setVisibility(View.VISIBLE);
                        }
                        else if (tmpErrorTextView != null){
                            tmpErrorTextView.setVisibility(View.VISIBLE);
                        }

                    }

                    // input error?
                    if (makeMeetingNoError) {


                        // call setter-methode setMeetingTimezoneSuggestions in ActivityMeeting to set timezone suggestion results
                        ((ActivityMeeting)getActivity()).setMeetingTimezoneSuggestions(makeMeetingCheckBoxListenerArray);


                        // call setter-methode setMeetingPlace in ActivityMeeting to set place
                        ((ActivityMeeting)getActivity()).setMeetingPlace(resultNumberOfPlace);

                        // call setter-methode setMeetingProblem in ActivityMeeting to problem
                        ((ActivityMeeting)getActivity()).setMeetingProblem(tmpTextInputFirstMeetingProblem);

                        // call setter-methode setMeetingStatus in ActivityMeeting to Meeting suggested
                        ((ActivityMeeting)getActivity()).setMeetingStatus(1);





                        // TODO ->
                        // Termin-ID erzeugen
                        // Netzwerk status pruefen
                        // Terminanfrage senden
                        // Ergebnis anzeigen




                        // Toast "Make first meeting send succesfull"
                        Toast.makeText(fragmentMeetingMakeContext, fragmentMeetingMakeContext.getResources().getString(R.string.makeFirstMeetingSendSuccesfullToastText), Toast.LENGTH_SHORT).show();

                        // send intent back to activity meeting
                        Intent intent = new Intent(fragmentMeetingMakeContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com", "now_meeting");
                        intent.putExtra("pop_stack", true);
                        fragmentMeetingMakeContext.startActivity(intent);

                    }
                    else {

                        // Toast "Make first Meeting not completly"
                        Toast.makeText(fragmentMeetingMakeContext, fragmentMeetingMakeContext.getResources().getString(R.string.makeFirstMeetingCompletErrorToastText), Toast.LENGTH_SHORT).show();


                    }

                }
            });


            // find abbort button "Zurueck zur Terminuebersicht"
            tmpButton = (Button) viewFragmentMeetingMake.findViewById(R.id.buttonAbbortSuggestionFirstMeeting);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(fragmentMeetingMakeContext, ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","now_meeting");
                    intent.putExtra("pop_stack", true);
                    fragmentMeetingMakeContext.startActivity(intent);

                }
            });
        }

    }


}
