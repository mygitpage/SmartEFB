package de.smart_efb.efbapp.smartefb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ich on 15.08.16.
 */
public class ActivityMeeting extends AppCompatActivity {


    // Number of different subtitles
    final int numberOfDifferentSubtitle = 8;

    // reference for the toolbar
    Toolbar toolbarMeeting;
    ActionBar actionBar;

    // shared prefs for the settings
    SharedPreferences prefs;

    // shared prefs for storing
    SharedPreferences.Editor prefsEditor;

    // reference to fragement manager
    FragmentManager fragmentManagerActivityMeeting;

    // reference to meeting fragments
    MeetingFragmentMeetingNow referenceFragmentMeetingNow;
    MeetingFragmentMeetingMake referenceFragmentMeetingMake;


    // Strings for subtitle ()
    String [] arraySubTitleText = new String[numberOfDifferentSubtitle];

    // number of checkboxes for choosing timezones
    static final int countNumberTimezones = 15;

    // boolean status array checkbox
    Boolean [] makeMeetingCheckBoxListenerArray = new Boolean[countNumberTimezones];

    // prefs name for timezone array
    static final String namePrefsMeetingStatus = "meetingStatus";

    // prefs name for meeting place
    static final String namePrefsMeetingPlace = "meetingPlace";

    // prefs name for timezone array
    static final String namePrefsArrayMeetingTimezoneArray = "meetingTimezone_";

    // prefs name for meeting problem
    static final String namePrefsMeetingProblem = "meetingProblem";

    // prefs name for meeting time and date
    static final String namePrefsMeetingTimeAndDate = "meetingDateAndTime";

    // meeting status
    int meetingStatus = 0;

    // meeting place
    int meetingPlace = 0;

    // name for places array (2 places)
    private String[] placesNameForMeetingArray = new String [4];


    // meeting problem
    String meetingProblem = "";

    // the current meeting date and time
    long currentMeetingDateAndTime;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_meeting);

        // init meeting
        initMeeting();

    }


    private void initMeeting() {

        // init the toolbarMeeting
        toolbarMeeting = (Toolbar) findViewById(R.id.toolbarMeeting);
        setSupportActionBar(toolbarMeeting);
        toolbarMeeting.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init the prefs
        prefs = getSharedPreferences("smartEfbSettings", MODE_PRIVATE);

        // init prefs editor
        prefsEditor = prefs.edit();

        // get meeting status
        meetingStatus = prefs.getInt(namePrefsMeetingStatus, 0);

        // get meeting place
        meetingPlace = prefs.getInt(namePrefsMeetingPlace, 0);

        // get meeting problem
        meetingProblem = prefs.getString(namePrefsMeetingProblem, "");

        // get the current meeting date and time
        currentMeetingDateAndTime = prefs.getLong(namePrefsMeetingTimeAndDate, System.currentTimeMillis());

        //load timezone array for meeting
        for (int i=0; i<countNumberTimezones; i++) {
            makeMeetingCheckBoxListenerArray[i] = prefs.getBoolean(namePrefsArrayMeetingTimezoneArray+i, false);
        }

        // init array for places name
        placesNameForMeetingArray = getResources().getStringArray(R.array.placesNameForMeetingArray);


        // init array for subtitles
        for (int t=0; t<numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
        }

        // init reference fragment manager
        fragmentManagerActivityMeeting = getSupportFragmentManager();


        // init reference fragements
        referenceFragmentMeetingNow = new MeetingFragmentMeetingNow();
        referenceFragmentMeetingMake = new MeetingFragmentMeetingMake();


        Log.d("MEETING","C0:"+fragmentManagerActivityMeeting.getBackStackEntryCount());


        // init start fragment MeetingFragmentMeetingNow
        FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, referenceFragmentMeetingNow, "now_meeting");
        //fragmentTransaction.addToBackStack("now_meeting");
        fragmentTransaction.commit();


        Log.d("MEETING","C1:"+fragmentManagerActivityMeeting.getBackStackEntryCount());






    }







    // Look for new intents (with data from URI or putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // Uri from intent that holds data
        Uri intentLinkData = null;

        // Extras from intent that holds data
        Bundle intentExtras = null;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        /*intentLinkData = intent.getData();*/
        intentExtras = intent.getExtras();

        Boolean tmpPopBackStack = false;
        //int tmpNumberinListView = 0;
        //Boolean tmpEvalNext = false;

        if (intentExtras != null) {

            // get data that comes with extras
            tmpPopBackStack = intentExtras.getBoolean("pop_stack",false);

            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"), tmpPopBackStack);
        }

    }




    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, Boolean tmpPopBackStack) {

        if (command.equals("change_meeting")) { // Show fragment for changing meeting date and time

            Log.d("Activity Meeting","change_meeting");


        } else if (command.equals("find_meeting")) { // Show fragment for finding meeting date and time


            Log.d("Activity Meeting","find_meeting");


        } else if (command.equals("make_meeting")) { // Show fragment for make first meeting date and time (make_meeting)

            // replace fragment MeetingFragmentMeetingMake
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingMake);
            fragmentTransaction.addToBackStack("make_meeting");
            fragmentTransaction.commit();

        }
        else {

            if (tmpPopBackStack) {
                fragmentManagerActivityMeeting.popBackStack("make_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }


            // replace fragment MeetingFragmentMeetingMake
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingNow);
            fragmentTransaction.addToBackStack("now_meeting");
            fragmentTransaction.commit();

            if (tmpPopBackStack) {
                fragmentManagerActivityMeeting.popBackStack("now_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }


        }

    }



    // setter for subtitle in ActivityMeeting toolbar
    public void setMeetingToolbarSubtitle (String subtitleText, String subtitleChoose) {

        switch (subtitleChoose) {

            case "noFirstMeeting":
                arraySubTitleText[0] = subtitleText;
                break;
            case "makeFirstMeeting":
                arraySubTitleText[1] = subtitleText;
                break;


            case "firstMeetingRequested":
                arraySubTitleText[2] = subtitleText;
                break;
            case "firstMeetingConfirmed":
                arraySubTitleText[3] = subtitleText;
                break;


        }


        toolbarMeeting.setSubtitle(subtitleText);

    }




    // getter for timezone suggestions array
    public  Long getMeetingTimeAndDate () {

        return currentMeetingDateAndTime;
    }


    // getter for timezone suggestions array
    public Boolean[] getMeetingTimezoneSuggestions () {

        Log.d("ActivityMeeting","getMeetingTimezoneArray");

        if (makeMeetingCheckBoxListenerArray[0] == true) {
            Log.d("ArrayElement 0 ","TRUE");
        }
        else {
            Log.d("ArrayElement 0 ","FALSE");
        }


        return makeMeetingCheckBoxListenerArray;
    }


    // setter for timezone suggestions array
    public void setMeetingTimezoneSuggestions (Boolean [] tmpTimezoneSuggestion) {

        // store timezone suggestions result in prefs
        for (int i=0; i<countNumberTimezones; i++) {
            prefsEditor.putBoolean(namePrefsArrayMeetingTimezoneArray+i,tmpTimezoneSuggestion[i]);
            makeMeetingCheckBoxListenerArray[i] = tmpTimezoneSuggestion[i];
        }

        prefsEditor.commit();

    }



    // getter for meeting status
    public int getMeetingStatus () {

        return meetingStatus;

    }

    // setter for meeting status
    public void setMeetingStatus (int tmpMeetingStatus) {

        meetingStatus = tmpMeetingStatus;

        prefsEditor.putInt(namePrefsMeetingStatus,tmpMeetingStatus);

        prefsEditor.commit();

    }



    // getter for meeting place
    public int getMeetingPlace () {

        return meetingPlace;

    }


    // getter for meeting place name
    public String getMeetingPlaceName (int tmpMeetingPlace) {

        return placesNameForMeetingArray[tmpMeetingPlace];

    }




    // setter for meeting place
    public void setMeetingPlace (int tmpMeetingPlace) {

        meetingStatus = tmpMeetingPlace;

        prefsEditor.putInt(namePrefsMeetingPlace,tmpMeetingPlace);

        prefsEditor.commit();

    }





    // getter for meeting problem
    public String getMeetingProblem () {

        return meetingProblem;

    }

    // setter for meeting problem
    public void setMeetingProblem (String tmpMeetingProblem) {

        meetingProblem = tmpMeetingProblem;

        prefsEditor.putString(namePrefsMeetingProblem,tmpMeetingProblem);

        prefsEditor.commit();

    }
    
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:

                int count = fragmentManagerActivityMeeting.getBackStackEntryCount();

                if (count == 0) {
                    super.onBackPressed();
                    return true;
                    //additional code
                } else {
                    fragmentManagerActivityMeeting.popBackStack();
                }



                //onBackPressed();

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
