package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * Created by ich on 20.12.2016.
 */
public class MeetingFragmentMeetingMake extends Fragment {


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

    // number of checkboxes for choosing timezones
    static final int countNumberTimezones = 15;

    // boolean status array checkbox
    Boolean [] makeMeetingCheckBoxListenerArray = new Boolean[countNumberTimezones];

    // count selected checkBoxes for border check
    int countSelectedCheckBoxesTimezone = 0;

    // number of radio buttons for choosing places
    static final int countNumberPlaces = 2;

    // result number of place (1 = Werder (Havel), 2 = Bad Belzig, 0 = no place selected)
    int resultNumberOfPlace = 0;

    // prefs name for meeting place
    static final String namePrefsMeetingPlace = "meetingPlace";

    // prefs name for timezone array
    static final String namePrefsArrayMeetingTimezoneArray = "meetingTimezone_";

    // prefs name for meeting problem
    static final String namePrefsMeetingProblem = "meetingProblem";




    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingMake = layoutInflater.inflate(R.layout.fragment_meeting_meeting_make, null);

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

        // get the current meeting date and time
        currentMeetingDateAndTime = prefs.getLong("meetingDateAndTime", System.currentTimeMillis());

        // get meeting status
        meetingStatus = prefs.getInt("meetingStatus", 0);

        // set onClickListener for checkboxes to choose timezone
        String tmpRessourceName ="";
        CheckBox tmpCheckBoxTimezone;
        for (int countTimezone = 0; countTimezone < countNumberTimezones; countTimezone++) {

            // init array checkbox status
            makeMeetingCheckBoxListenerArray[countTimezone] = false;

            tmpRessourceName ="makeMeetingTimezone_" + (countTimezone+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentMeetingMakeContext.getPackageName());

                tmpCheckBoxTimezone = (CheckBox) viewFragmentMeetingMake.findViewById(resourceId);
                tmpCheckBoxTimezone.setOnClickListener(new makeMeetingCheckBoxListenerTimezones(countTimezone));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // set onClickListener for radio button select place
        RadioButton tmpRadioButtonPlaces;
        for (int countPlaces = 0; countPlaces < countNumberPlaces; countPlaces++) {


            tmpRessourceName ="makeMeetingPlace_" + (countPlaces+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentMeetingMakeContext.getPackageName());

                tmpRadioButtonPlaces = (RadioButton) viewFragmentMeetingMake.findViewById(resourceId);
                tmpRadioButtonPlaces.setOnClickListener(new makeMeetingRadioButtonListenerPlaces(countPlaces));
                //tmpRadioButtonQuestion.setChecked(false);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

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
        ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, tmpSubtitleOrder);

        // status make first meeting
        if (btnVisibilitySendMakeFirstMeeting) {
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

                        // store checkboxes result in prefs
                        for (int i=0; i<countNumberTimezones; i++) {
                            prefsEditor.putBoolean(namePrefsArrayMeetingTimezoneArray+i,makeMeetingCheckBoxListenerArray[i]);
                        }

                        // store place in prefs
                        prefsEditor.putInt(namePrefsMeetingPlace,resultNumberOfPlace);

                        //store meeting problem
                        prefsEditor.putString(namePrefsMeetingProblem, tmpTextInputFirstMeetingProblem);

                        prefsEditor.commit();



                        // TODO ->
                        // Termin-ID erzeugen
                        // Netzwerk status pruefen
                        // Terminanfrage senden
                        // Ergebnis anzeigen


                        // meeting status -> send succesfull, waiting for response
                        // store meeting status in prefs
                        prefsEditor.putInt("meetingStatus",1);
                        prefsEditor.commit();

                        // Toast "Make first meeting send succesfull"
                        Toast.makeText(fragmentMeetingMakeContext, fragmentMeetingMakeContext.getResources().getString(R.string.makeFirstMeetingSendSuccesfullToastText), Toast.LENGTH_SHORT).show();

                        // send intent back to activity meeting
                        Intent intent = new Intent(fragmentMeetingMakeContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com", "now_meeting");
                        intent.putExtra("meet_status", 1);
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
                    fragmentMeetingMakeContext.startActivity(intent);

                }
            });
        }

    }

    //
    // onClickListener for checkboxes to choose timezone
    //
    public class makeMeetingCheckBoxListenerTimezones implements View.OnClickListener {

        int checkBoxNumber;

        public makeMeetingCheckBoxListenerTimezones (int checkBoxNr) {

            this.checkBoxNumber = checkBoxNr;

        }

        @Override
        public void onClick(View v) {


            if (makeMeetingCheckBoxListenerArray[checkBoxNumber]) {
                makeMeetingCheckBoxListenerArray[checkBoxNumber] = false;
                countSelectedCheckBoxesTimezone --;
            }
            else {
                makeMeetingCheckBoxListenerArray[checkBoxNumber] = true;
                countSelectedCheckBoxesTimezone ++;
            }


        }

    }


    //
    // onClickListener for checkboxes to choose timezone
    //
    public class makeMeetingRadioButtonListenerPlaces implements View.OnClickListener {

        int radioButtonNumber;

        public makeMeetingRadioButtonListenerPlaces (int radioButtonNr) {

            this.radioButtonNumber = radioButtonNr;

        }

        @Override
        public void onClick(View v) {


            // check button number and get result
            switch (radioButtonNumber) {

                case 0: // ever
                    resultNumberOfPlace = 1;
                    break;
                case 1:
                    resultNumberOfPlace = 2;
                    break;
                default:
                    resultNumberOfPlace = 0;
                    break;
            }


        }

    }

}