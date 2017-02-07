package de.smart_efb.efbapp.smartefb;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ich on 15.08.16.
 */
public class ActivityMeeting extends AppCompatActivity {



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
    MeetingFragmentMeetingFind referenceFragmentMeetingFind;
    MeetingFragmentMeetingChange referenceFragmentMeetingChange;


    // number of checkboxes for choosing timezones
    //static final int countNumberTimezones = 15;

    // number of simultaneous meetings
    //static final int numberSimultaneousMeetings = 2;



    // prefs name for meeting status
    //static final String namePrefsMeetingStatus = "meetingStatus";

    // prefs name for meeting place
    //static final String namePrefsMeetingPlace = "meetingPlace";

    // prefs name for timezone array
    //static final String namePrefsArrayMeetingTimezoneArray = "meetingTimezone_";

    // prefs name for meeting problem
    //static final String namePrefsMeetingProblem = "meetingProblem";

    // prefs name for meeting time and date
    //static final String namePrefsMeetingTimeAndDate = "meetingDateAndTime";

    // prefs name for author meeting suggestion
    //static final String namePrefsAuthorMeetingSuggestion = "authorMeetingSuggestions";

    // prefs name for info new meeting date and time (in mainActivity also!!!!!!!!!)
    //static final String namePrefsNewMeetingDateAndTime = "meetingNewDateAndTime";

    // prefs name for deadline for response of meeting suggestions
    //static final String namePrefsMeetingSuggestionsResponseDeadline = "meetingSuggestionsResponseDeadline";

    // prefs praefix for  (in mainActivity also!!!!!!!!!)
    //String [] prefsPraefixMeetings = {"_A","_B"};

    // boolean status array checkbox
    Boolean [] makeMeetingCheckBoxListenerArray = new Boolean[ConstantsClassMeeting.countNumberTimezones];

    // meeting status
    int meetingStatus = 0;

    // name for places array (2 places)
    private String[] placesNameForMeetingArray = new String [4];

    // meeting problem
    String meetingProblem = "";

    // author meeting suggestions
    String meetingSuggestionsAuthor = "";

    // meeting place
    int [] meetingPlace = new int[ConstantsClassMeeting.numberSimultaneousMeetings];

    // the current meeting date and time
    long [] currentMeetingDateAndTime = new long [ConstantsClassMeeting.numberSimultaneousMeetings];

    // info new meeting date and time
    Boolean [] meetingNewDateAndTime = new Boolean[ConstantsClassMeeting.numberSimultaneousMeetings];

    // deadline for responding of meeting suggestions
    long meetingSuggestionsResponeseDeadline = 0;

    // index number for meeting to change
    int indexNumberForMeetingToChange = 0;

    // String info back to fragment when back from change meeting
    String meetingBackToFragment = "";

    // reference to dialog settings
    AlertDialog alertDialogSettings;



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
        prefs = getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);

        // init prefs editor
        prefsEditor = prefs.edit();

        // get meeting status
        meetingStatus = prefs.getInt(ConstantsClassMeeting.namePrefsMeetingStatus, 0);

        // get meeting problem
        meetingProblem = prefs.getString(ConstantsClassMeeting.namePrefsMeetingProblem, "");

        // get author meeting suggestions
        meetingSuggestionsAuthor = prefs.getString(ConstantsClassMeeting.namePrefsAuthorMeetingSuggestion, "Herr Terminmann");

        // get response deadline for meeting suggestions
        meetingSuggestionsResponeseDeadline = prefs.getLong(ConstantsClassMeeting.namePrefsMeetingSuggestionsResponseDeadline, 0);

        // get from prefs meeting date and time and place
        getDateAndTimeFromPrefs ();

        //load timezone array for meeting
        for (int i=0; i<ConstantsClassMeeting.countNumberTimezones; i++) {
            makeMeetingCheckBoxListenerArray[i] = prefs.getBoolean(ConstantsClassMeeting.namePrefsArrayMeetingTimezoneArray+i, false);
        }

        // init array for places name
        placesNameForMeetingArray = getResources().getStringArray(R.array.placesNameForMeetingArray);

        // init reference fragment manager
        fragmentManagerActivityMeeting = getSupportFragmentManager();

        // init reference fragments
        referenceFragmentMeetingNow = new MeetingFragmentMeetingNow();
        referenceFragmentMeetingMake = new MeetingFragmentMeetingMake();
        referenceFragmentMeetingFind = new MeetingFragmentMeetingFind();
        referenceFragmentMeetingChange = new MeetingFragmentMeetingChange();

        if (meetingStatus >= 0 && meetingStatus <= 3) { // init start fragment MeetingFragmentMeetingNow

            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, referenceFragmentMeetingNow, "now_meeting");
            fragmentTransaction.commit();
        }
        else {// init start fragment MeetingFragmentFindMeeting

            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, referenceFragmentMeetingFind, "find_meeting");
            fragmentTransaction.commit();

        }

        // create help dialog in Meeting
        createHelpDialog();

    }


    // get from prefs meeting date and time and place
    private void getDateAndTimeFromPrefs () {

        for (int t=0; t < ConstantsClassMeeting.numberSimultaneousMeetings; t++) {

            // get the current meeting date and time
            currentMeetingDateAndTime[t] = prefs.getLong(ConstantsClassMeeting.namePrefsMeetingTimeAndDate + ConstantsClassMeeting.prefsPraefixMeetings[t], 0);

            if (currentMeetingDateAndTime[t] > System.currentTimeMillis()) { // is meeting timestamp > current time?

                // get meeting place
                meetingPlace[t] = prefs.getInt(ConstantsClassMeeting.namePrefsMeetingPlace + ConstantsClassMeeting.prefsPraefixMeetings[t], 0);

                // get info new meeting date and time from prefs
                meetingNewDateAndTime[t] = prefs.getBoolean(ConstantsClassMeeting.namePrefsNewMeetingDateAndTime  + ConstantsClassMeeting.prefsPraefixMeetings[t], false);

            }
            else { // no -> init with zero
                currentMeetingDateAndTime[t] = 0;
                meetingPlace[t] = 0;
                meetingNewDateAndTime[t] = false;
            }
        }
    }


    // Look for new intents (with data from putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // Extras from intent that holds data
        Bundle intentExtras = null;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentExtras = intent.getExtras();

        Boolean tmpPopBackStack = false;
        Boolean  tmpUpdateFragement = false;
        int tmpMeetingStatus = 0;
        int tmpMeetingIndexToChange = 0;
        String tmpMeetingBackToFragment = "";

        if (intentExtras != null) {

            // get data that comes with extras -> pop_stack
            tmpPopBackStack = intentExtras.getBoolean("pop_stack",false);

            // get data that comes with extras -> pop_stack
            tmpMeetingStatus = intentExtras.getInt("met_status",0);

            // get the meeting indexnumber for deleting or changing meeting date
            tmpMeetingIndexToChange = intentExtras.getInt("met_index",0);

            // get info back to fragement
            tmpMeetingBackToFragment = intentExtras.getString("met_backto","");

            // get info to update fragement or replace (find_meeting)
            tmpUpdateFragement = intentExtras.getBoolean("update", false);


            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"), tmpPopBackStack, tmpMeetingStatus, tmpMeetingIndexToChange, tmpMeetingBackToFragment, tmpUpdateFragement);
        }

    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, Boolean tmpPopBackStack, int tmpMeetingStatus, int tmpMeetingIndexToChange, String tmpMeetingBackToFragment, Boolean tmpUpdateFragement) {

        if (command.equals("change_meeting")) { // Show fragment for changing meeting date and time

            // set index of meeting to delete
            setMeetingIndexToChange (tmpMeetingIndexToChange);

            // set back to fragment info
            setMeetingBackToFragment (tmpMeetingBackToFragment);

            // replace fragment MeetingFragmentMeetingChange
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingChange);
            fragmentTransaction.addToBackStack("change_meeting");
            fragmentTransaction.commit();


        } else if (command.equals("find_meeting")) { // Show fragment for finding meeting date and time

            // set new meeting status
            setMeetingStatus (tmpMeetingStatus);

            if (tmpUpdateFragement) {

                // refresh fragment MeetingFragmentMeetingFind
                FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
                fragmentTransaction.detach(referenceFragmentMeetingFind);
                fragmentTransaction.attach(referenceFragmentMeetingFind);
                fragmentTransaction.commit();

            } else {

                if (tmpPopBackStack) {
                    fragmentManagerActivityMeeting.popBackStack("change_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                // replace fragment MeetingFragmentMeetingFind
                FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingFind);
                fragmentTransaction.addToBackStack("find_meeting");
                fragmentTransaction.commit();

                if (tmpPopBackStack) {
                    fragmentManagerActivityMeeting.popBackStack("find_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                // delete back to fragment info
                setMeetingBackToFragment ("");

            }




        } else if (command.equals("make_meeting")) { // Show fragment for make first meeting date and time (make_meeting)

            // replace fragment MeetingFragmentMeetingMake
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingMake);
            fragmentTransaction.addToBackStack("make_meeting");
            fragmentTransaction.commit();

        } else {

            if (tmpPopBackStack && getMeetingBackToFragment().equals("") ) {

                fragmentManagerActivityMeeting.popBackStack("make_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            } else if (tmpPopBackStack && getMeetingBackToFragment().equals("now_meeting") ) {

                fragmentManagerActivityMeeting.popBackStack("change_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }

            // replace fragment MeetingFragmentMeetingMake
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingNow);
            fragmentTransaction.addToBackStack("now_meeting");
            fragmentTransaction.commit();

            if (tmpPopBackStack) {
                fragmentManagerActivityMeeting.popBackStack("now_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }

            // delete back to fragment info
            setMeetingBackToFragment ("");

        }

    }


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonMeeting = (Button) findViewById(R.id.helpMeetingNow);


        // add button listener to question mark in activity Meeting (toolbar)
        tmpHelpButtonMeeting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView tmpdialogTextView;
                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMeeting.this);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityMeeting.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_meeting, null);



                // get string ressources
                String tmpTextCloseDialog = ActivityMeeting.this.getResources().getString(R.string.textDialogMeetingCloseDialog);
                String tmpTextTitleDialog = ActivityMeeting.this.getResources().getString(R.string.textDialogMeetingTitleDialog);

                // build the dialog
                builder.setView(dialogSettings)

                        // Add close button
                        .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogSettings.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogSettings = builder.create();

                // and show the dialog
                builder.show();

            }
        });

    }


    // setter for subtitle in ActivityMeeting toolbar
    public void setMeetingToolbarSubtitle (String subtitleText) {

       toolbarMeeting.setSubtitle(subtitleText);

    }


    // getter for actual meeting timestamp (max 2 meetings)
    public  long[] getMeetingTimeAndDate () {

        return currentMeetingDateAndTime;
    }


    // delete meeting timestamp and place with index
    public void deleteMeetingTimestampAndPlace (int index) {

        // Delete timestamp from meeting _A or _B (look index)
        prefsEditor.putLong(ConstantsClassMeeting.namePrefsMeetingTimeAndDate + ConstantsClassMeeting.prefsPraefixMeetings[index], 0);
        // Delete meeting place
        prefsEditor.putInt(ConstantsClassMeeting.namePrefsMeetingPlace + ConstantsClassMeeting.prefsPraefixMeetings[index], 0);
        // Delete new info for meeting
        prefsEditor.putBoolean(ConstantsClassMeeting.namePrefsNewMeetingDateAndTime + ConstantsClassMeeting.prefsPraefixMeetings[index], false);

        prefsEditor.commit();

        // refresh from prefs meeting date and time and place
        getDateAndTimeFromPrefs ();


    }


    // getter for timezone suggestions array
    public Boolean[] getMeetingTimezoneSuggestions () {

        return makeMeetingCheckBoxListenerArray;
    }


    // setter for timezone suggestions array
    public void setMeetingTimezoneSuggestions (Boolean [] tmpTimezoneSuggestion) {

        // store timezone suggestions result in prefs
        for (int i=0; i<ConstantsClassMeeting.countNumberTimezones; i++) {
            prefsEditor.putBoolean(ConstantsClassMeeting.namePrefsArrayMeetingTimezoneArray+i,tmpTimezoneSuggestion[i]);
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

        prefsEditor.putInt(ConstantsClassMeeting.namePrefsMeetingStatus,tmpMeetingStatus);

        prefsEditor.commit();

    }


    // getter for meeting place (max 2 places for meetings)
    public int[] getMeetingPlace () {

        return meetingPlace;

    }


    // getter for meeting place name
    public String getMeetingPlaceName (int tmpMeetingPlace) {

        return placesNameForMeetingArray[tmpMeetingPlace];

    }


    // setter for meeting place
    public void setMeetingPlace (int tmpMeetingPlace, int placeIndex) {

        meetingPlace[placeIndex] = tmpMeetingPlace;

        prefsEditor.putInt(ConstantsClassMeeting.namePrefsMeetingPlace + ConstantsClassMeeting.prefsPraefixMeetings[placeIndex],tmpMeetingPlace);

        prefsEditor.commit();

    }




    // getter for meeting problem
    public String getMeetingProblem () {

        return meetingProblem;

    }


    // setter for meeting problem
    public void setMeetingProblem (String tmpMeetingProblem) {

        meetingProblem = tmpMeetingProblem;

        prefsEditor.putString(ConstantsClassMeeting.namePrefsMeetingProblem,tmpMeetingProblem);

        prefsEditor.commit();

    }


    // getter for author meeting suggestion
    public String getAuthorMeetingSuggestion () {

        return meetingSuggestionsAuthor;

    }


    // getter for info new meeting date and time
    public Boolean[] getInfoNewMeetingDateAndTime () {

        return meetingNewDateAndTime;

    }

    // unset new status meetings (both new status for meeting is unset)
    public void unsetNewStatusMeeting () {

        prefsEditor.putBoolean(ConstantsClassMeeting.namePrefsNewMeetingDateAndTime + ConstantsClassMeeting.prefsPraefixMeetings[0],false);
        prefsEditor.putBoolean(ConstantsClassMeeting.namePrefsNewMeetingDateAndTime + ConstantsClassMeeting.prefsPraefixMeetings[1],false);

        prefsEditor.commit();

    }


    // getter for deadline for suggestion response
    public long getSuggestionsResponeseDeadline () {

        return meetingSuggestionsResponeseDeadline;

    }


    // setter for index to change/delete an meeting
    public void setMeetingIndexToChange (int tmpMeetingIndexToChange) {

        indexNumberForMeetingToChange = tmpMeetingIndexToChange;

    }

    // getter for index to change/delete an meeting
    public int getMeetingIndexToChange () {

        return indexNumberForMeetingToChange;

    }

    // setter for info back to fragment
    public void setMeetingBackToFragment (String tmpMeetingBackToFragment) {

        meetingBackToFragment = tmpMeetingBackToFragment;

    }


    // getter for info back to fragment
    public String getMeetingBackToFragment () {

        return meetingBackToFragment;

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
