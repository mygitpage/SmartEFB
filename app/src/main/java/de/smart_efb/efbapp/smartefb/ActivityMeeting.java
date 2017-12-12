package de.smart_efb.efbapp.smartefb;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ich on 15.08.16.
 */
public class ActivityMeeting extends AppCompatActivity {


    // Set subtitle first time
    Boolean setSubtitleFirstTime = false;

    // viewpager and tablayout for the view
    ViewPager viewPagerMeeting;
    TabLayout tabLayoutMeeting;

    // viewpager adapter
    MeetingViewPagerAdapter meetingViewPagerAdapter;


    // reference for the toolbar
    Toolbar toolbarMeeting;
    ActionBar actionBar;

    // shared prefs for the settings
    SharedPreferences prefs;

    // reference to the DB
    DBAdapter myDb;

    // what to show in tab zero
    String showCommandFragmentTabZero = "";

    // what to show in tab one (
    String showCommandFragmentTabOne = "";

    // Strings for subtitle ( )
    String [] arraySubTitleText = new String[ConstansClassMeeting.numberOfDifferentSubtitle];


    // info new entry on tab zero
    Boolean infoNewEntryOnTabZero = false;
    Boolean infoNewEntryOnTabOne = false;
    Boolean infoNewEntryOnTabTwo = true;
    String infoTextNewEntryPostFixTabZeroTitle = "";
    String infoTextNewEntryPostFixTabOneTitle = "";
    String infoTextNewEntryPostFixTabTwoTitle = "";
    String tabTitleTextTabZero = "";
    String tabTitleTextTabOne = "";
    String tabTitleTextTabTwo = "";

    // actual db id of meeting (for canceled, comment, etc.)
    Long actualDbIdOfMeeting = 0L;

    // message for successfull and not successfull sending (set and read by fragment)
    String notSuccessefullForSendingMessageString = "";
    String successefullForSendingMessageString = "";

    // reference to dialog settings
    AlertDialog alertDialogSettings;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_meeting);

        // init meeting
        initMeeting();

        // init view tabs
        initMeetingTabs();


    }


    private void initMeetingTabs () {

        // find viewpager in view
        viewPagerMeeting = (ViewPager) findViewById(R.id.viewPagerMeeting);

        // new pager adapter for OurArrangement
        meetingViewPagerAdapter = new MeetingViewPagerAdapter(getSupportFragmentManager(), this);

        // set pagerAdapter to viewpager
        viewPagerMeeting.setAdapter(meetingViewPagerAdapter);

        //find tablayout and set gravity
        tabLayoutMeeting = (TabLayout) findViewById(R.id.tabLayoutMeeting);
        tabLayoutMeeting.setTabGravity(TabLayout.GRAVITY_FILL);

        // and set tablayout with viewpager
        tabLayoutMeeting.setupWithViewPager(viewPagerMeeting);

        // set correct tab zero, one and two title with information new entry and color change -> FIRST TIME
        setTabZeroTitleAndColor();
        setTabOneTitleAndColor();
        setTabTwoTitleAndColor();

        // init listener for tab selected
        tabLayoutMeeting.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";


                Log.d("Meeting", "Tab Listener mit Position: "+ tab.getPosition());

                // Change the subtitle of the activity
                switch (tab.getPosition()) {
                    case 0: // title for tab zero
                        switch (showCommandFragmentTabZero) {
                            case "meeting_overview":
                                tmpSubtitleText = arraySubTitleText[0];
                                break;
                            case "meeting_client_canceled":
                                tmpSubtitleText = arraySubTitleText[5];
                                break;

                        }

                        // set correct tab zero title with information new entry and color change
                        setTabZeroTitleAndColor();
                        break;

                    case 1: // title for tab one
                        tmpSubtitleText = arraySubTitleText[2];
                        break;

                    case 2:
                        tmpSubtitleText = arraySubTitleText[3];
                        break;

                    case 3: // title for tab one
                        tmpSubtitleText = arraySubTitleText[4];
                        break;

                    default:
                        tmpSubtitleText = arraySubTitleText[0];
                        break;
                }

                // set toolbar text
                toolbarMeeting.setSubtitle(tmpSubtitleText);

                // call viewpager
                viewPagerMeeting.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

    }
    
    
    
    private void initMeeting() {

        // init the toolbarMeeting
        toolbarMeeting = (Toolbar) findViewById(R.id.toolbarMeeting);
        setSupportActionBar(toolbarMeeting);
        toolbarMeeting.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        catch (NullPointerException e) {
            // do nothing
        }

        // init DB
        myDb = new DBAdapter(getApplicationContext());



        // for developing
        //myDb.deleteAllRowsMeetingAndSuggestion();




        // init the prefs
        prefs = getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);

        for (int t=0; t<ConstansClassMeeting.numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
        }

        // enable setting subtitle for the first time
        setSubtitleFirstTime = true;

        // init show on tab zero meeting overview
        showCommandFragmentTabZero = "meeting_overview";

        // init show on tab one meeting overview
        showCommandFragmentTabOne = "suggestion_from_client";

        for (int t=0; t<ConstansClassOurArrangement.numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
        }

        // enable setting subtitle for the first time
        setSubtitleFirstTime = true;

        //set tab title string
        tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("meetingTabTitle_1", "string", getPackageName()));
        tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("meetingTabTitle_2", "string", getPackageName()));
        tabTitleTextTabTwo = getResources().getString(getResources().getIdentifier("meetingTabTitle_3", "string", getPackageName()));

        // create help dialog in Meeting
        createHelpDialog();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        // close db connection
        myDb.close();
    }



    // Look for new intents (with data from URI or putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // set actual meeting db id
        actualDbIdOfMeeting = 0L;

        // Extras from intent that holds data
        Bundle intentExtras = null;

        // meeting id for cancele meeting
        Long tmpMeetingId = 0L;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentExtras = intent.getExtras();

        if (intentExtras != null) {

            // get meeting id from external
            tmpMeetingId = intentExtras.getLong("meeting_id",0);

            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"), tmpMeetingId);
        }

    }



    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, Long meetingId) {

        if (command.equals("comment_from_client")) { // Show fragment client comment a meeting

            //set fragment in tab zero to comment
            meetingViewPagerAdapter.setFragmentTabZero("comment_from_client");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("meetingTabTitle_1a", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "comment_from_client";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarMeeting.setSubtitle(arraySubTitleText[1]);


        } else if (command.equals("suggestion_overview")) { // Show fragment overview of suggestions

            //set fragment in tab one to comment suggestions
            meetingViewPagerAdapter.setFragmentTabOne("suggestion_overview");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabTwo = getResources().getString(getResources().getIdentifier("meetingTabTitle_3", "string", getPackageName()));
            setTabTwoTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "suggestion_overview";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarMeeting.setSubtitle(arraySubTitleText[2]);

        } else if (command.equals("suggestion_from_client")) { // Show suggestion from client

            //set fragment in tab one to suggestion from client
            meetingViewPagerAdapter.setFragmentTabOne("suggestion_from_client");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("meetingTabTitle_2", "string", getPackageName()));
            setTabOneTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "suggestion_from_client";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarMeeting.setSubtitle(arraySubTitleText[3]);


        } else if (command.equals("meeting_client_canceled")) { // Show meeting canceled from client

            // set actual meeting db id
            actualDbIdOfMeeting = meetingId;

            //set fragment in tab zero to meeting canceled from client
            meetingViewPagerAdapter.setFragmentTabZero("meeting_client_canceled");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("meetingTabTitle_1a", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "meeting_client_canceled";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarMeeting.setSubtitle(arraySubTitleText[5]);


        } else if (command.equals("delete_canceled_meeting_by_client")) { // delete canceled meeting, clicked by client

            // delete selected meeting from db
            myDb.deleteSelectedMeetingOrSuggestionFromDb(meetingId);

            //set fragment in tab zero to meeting overview
            meetingViewPagerAdapter.setFragmentTabZero("meeting_overview");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("meetingTabTitle_1", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "meeting_overview";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarMeeting.setSubtitle(arraySubTitleText[0]);

        } else if (command.equals("client_delete_suggestion_entry")) { // delete suggestion entry by client, because meeting found from suggestion, cnaceneld suggestion or already vote

            // delete selected meeting from db
            myDb.deleteSelectedMeetingOrSuggestionFromDb(meetingId);

            //set fragment in tab zero to meeting overview
            //meetingViewPagerAdapter.setFragmentTabOne("suggestion_overview");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabTwo = getResources().getString(getResources().getIdentifier("meetingTabTitle_2", "string", getPackageName()));
            setTabTwoTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "suggestion_overview";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarMeeting.setSubtitle(arraySubTitleText[2]);

        }
        else { // Show fragment meeting overview on tab zero

            //set fragment in tab zero to meeting overview
            meetingViewPagerAdapter.setFragmentTabZero("meeting_overview");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("meetingTabTitle_1", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "meeting_overview";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarMeeting.setSubtitle(arraySubTitleText[0]);

        }
    }


    /*

    // get from prefs meeting date and time and place
    private void getDateAndTimeFromPrefs () {

        for (int t = 0; t < ConstansClassMeeting.numberSimultaneousMeetings; t++) {

            // get the current meeting date and time
            currentMeetingDateAndTime[t] = prefs.getLong(ConstansClassMeeting.namePrefsMeetingTimeAndDate + ConstansClassMeeting.prefsPraefixMeetings[t], 0);

            if (currentMeetingDateAndTime[t] > System.currentTimeMillis()) { // is meeting timestamp > current time?

                // get meeting place
                meetingPlace[t] = prefs.getInt(ConstansClassMeeting.namePrefsMeetingPlace + ConstansClassMeeting.prefsPraefixMeetings[t], 0);

                // get info new meeting date and time from prefs
                meetingNewDateAndTime[t] = prefs.getBoolean(ConstansClassMeeting.namePrefsNewMeetingDateAndTime  + ConstansClassMeeting.prefsPraefixMeetings[t], false);

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

            // replace fragment Old_MeetingFragmentMeetingChange
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingChange);
            fragmentTransaction.addToBackStack("change_meeting");
            fragmentTransaction.commit();


        } else if (command.equals("find_meeting")) { // Show fragment for finding meeting date and time

            // set new meeting status
            setMeetingStatus (tmpMeetingStatus);

            if (tmpUpdateFragement) {

                // refresh fragment Old_MeetingFragmentMeetingFind
                FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
                fragmentTransaction.detach(referenceFragmentMeetingFind);
                fragmentTransaction.attach(referenceFragmentMeetingFind);
                fragmentTransaction.commit();

            } else {

                if (tmpPopBackStack) {
                    fragmentManagerActivityMeeting.popBackStack("change_meeting", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                // replace fragment Old_MeetingFragmentMeetingFind
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

            // replace fragment Old_MeetingFragmentMeetingMake
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

            // replace fragment Old_MeetingFragmentMeetingMake
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

*/









    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonMeeting = (Button) findViewById(R.id.helpMeetingNow);


        // add button listener to question mark in activity Meeting (toolbar)
        tmpHelpButtonMeeting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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





    // getter for actual db id of meeting (for canceled, comment, etc.)
    public Long getActualMeetingDbId () {

        return actualDbIdOfMeeting;

    }


    // return a message that ist set by a fragment, when sending is successefull
    public String getSuccessefullMessageForSending() {

        return successefullForSendingMessageString;

    }


    // set message by a fragment, for returning, when sending is successefull
    public void setSuccessefullMessageForSending(String succssefullString) {

        successefullForSendingMessageString = succssefullString;

    }



    // return a message that ist set by a fragment, when sending is not successefull
    public String getNotSuccessefullMessageForSending() {

        return notSuccessefullForSendingMessageString;

    }


    // set message by a fragment, for returning, when sending is not successefull
    public void setNotSuccessefullMessageForSending(String notSuccssefullString) {

        notSuccessefullForSendingMessageString = notSuccssefullString;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    // set correct tab zero title with information new entry and color change
    private void setTabZeroTitleAndColor () {

        ActivityMeeting.this.lookNewEntryOnTabZero();

        String tmpTitleText = tabTitleTextTabZero + infoTextNewEntryPostFixTabZeroTitle;
        tabLayoutMeeting.getTabAt(0).setText(tmpTitleText);
        ActivityMeeting.this.setUnsetTextColorSignalNewTabZero(infoNewEntryOnTabZero);
    }



    // set correct tab one title with information new entry and color change
    private void setTabOneTitleAndColor () {

        ActivityMeeting.this.lookNewEntryOnTabOne();

        String tmpTitleText = tabTitleTextTabOne + infoTextNewEntryPostFixTabOneTitle;
        tabLayoutMeeting.getTabAt(1).setText(tmpTitleText);
        ActivityMeeting.this.setUnsetTextColorSignalNewTabOne(infoNewEntryOnTabOne);
    }

    // set correct tab two title with information new entry and color change
    private void setTabTwoTitleAndColor () {

        ActivityMeeting.this.lookNewEntryOnTabTwo();

        String tmpTitleText = tabTitleTextTabTwo + infoTextNewEntryPostFixTabTwoTitle;
        tabLayoutMeeting.getTabAt(2).setText(tmpTitleText);
        ActivityMeeting.this.setUnsetTextColorSignalNewTabTwo(infoNewEntryOnTabTwo);
    }


    // look for new entry on tab zero
    private void lookNewEntryOnTabZero () {

        // look for new entrys in db on tab zero
        if (myDb.getCountNewEntryMeetingAndSuggestion("meeting") > 0) {
            infoNewEntryOnTabZero = true;
            infoTextNewEntryPostFixTabZeroTitle = " " + this.getResources().getString(R.string.newEntryText);
        }
        else {
            infoNewEntryOnTabZero = false;
            infoTextNewEntryPostFixTabZeroTitle = "";
        }
    }



    // look for new entry on tab one
    private void lookNewEntryOnTabOne () {

        // look for new entrys in db on tab one
        if (myDb.getCountNewEntryMeetingAndSuggestion("suggestion") > 0) {
            infoNewEntryOnTabOne = true;
            infoTextNewEntryPostFixTabOneTitle = " "+ this.getResources().getString(R.string.newEntryText);
        }
        else {
            infoNewEntryOnTabOne = false;
            infoTextNewEntryPostFixTabOneTitle = "";
        }
    }


    // look for new entry on tab two
    private void lookNewEntryOnTabTwo () {

        Long nowTime = System.currentTimeMillis();
        Cursor c = myDb.getAllRowsMeetingsAndSuggestion("suggestion_for_show_attention", nowTime);

        // look for new entrys in db on tab two
        if (myDb.getCountNewEntryMeetingAndSuggestion("meeting") > 0) {
            infoNewEntryOnTabTwo = true;
            infoTextNewEntryPostFixTabTwoTitle = " " + this.getResources().getString(R.string.newEntryText);
        }
        else if (c != null && c.getCount() > 0 ) {
            infoNewEntryOnTabTwo = true;
            infoTextNewEntryPostFixTabTwoTitle = " "+ this.getResources().getString(R.string.newAttentionText);
        }else {
            infoNewEntryOnTabTwo = false;
            infoTextNewEntryPostFixTabTwoTitle = "";
        }
    }



    // set/ unset textcolor for tab title on tab zero
    private void setUnsetTextColorSignalNewTabZero (Boolean colorSet) {

        int tmpTextColor;

        if (colorSet) {
            tmpTextColor = ContextCompat.getColor(ActivityMeeting.this, R.color.text_accent_color);
        }
        else {
            tmpTextColor = ContextCompat.getColor(ActivityMeeting.this, R.color.colorAccent);
        }

        // Change tab text color on tab zero
        ViewGroup vg = (ViewGroup) tabLayoutMeeting.getChildAt(0);
        ViewGroup vgTab = (ViewGroup) vg.getChildAt(0); //Tab Zero
        int tabChildsCount = vgTab.getChildCount();
        for (int i=0; i<tabChildsCount; i++) {
            View tabViewCild = vgTab.getChildAt(i);
            if (tabViewCild instanceof TextView) {
                ((TextView) tabViewCild).setTextColor(tmpTextColor);
            }
        }

    }



    // set/ unset textcolor for tab title on tab one
    private void setUnsetTextColorSignalNewTabOne (Boolean colorSet) {

        int tmpTextColor;

        if (colorSet) {
            tmpTextColor = ContextCompat.getColor(ActivityMeeting.this, R.color.text_accent_color);
        }
        else {
            tmpTextColor = ContextCompat.getColor(ActivityMeeting.this, R.color.colorAccent);
        }

        // Change tab text color on tab one
        ViewGroup vg = (ViewGroup) tabLayoutMeeting.getChildAt(0);
        ViewGroup vgTab = (ViewGroup) vg.getChildAt(1); //Tab One
        int tabChildsCount = vgTab.getChildCount();
        for (int i=0; i<tabChildsCount; i++) {
            View tabViewCild = vgTab.getChildAt(i);
            if (tabViewCild instanceof TextView) {
                ((TextView) tabViewCild).setTextColor(tmpTextColor);
            }
        }

    }



    // set/ unset textcolor for tab title on tab two
    private void setUnsetTextColorSignalNewTabTwo (Boolean colorSet) {

        int tmpTextColor;

        if (colorSet) {
            tmpTextColor = ContextCompat.getColor(ActivityMeeting.this, R.color.text_accent_color);
        }
        else {
            tmpTextColor = ContextCompat.getColor(ActivityMeeting.this, R.color.colorAccent);
        }

        // Change tab text color on tab two
        ViewGroup vg = (ViewGroup) tabLayoutMeeting.getChildAt(0);
        ViewGroup vgTab = (ViewGroup) vg.getChildAt(2); //Tab Two
        int tabChildsCount = vgTab.getChildCount();
        for (int i=0; i<tabChildsCount; i++) {
            View tabViewCild = vgTab.getChildAt(i);
            if (tabViewCild instanceof TextView) {
                ((TextView) tabViewCild).setTextColor(tmpTextColor);
            }
        }

    }



    // setter for subtitle in meeting toolbar
    public void setMeetingToolbarSubtitle (String subtitleText, String subtitleChoose) {

        switch (subtitleChoose) {

            case "meeting_overview":
                arraySubTitleText[0] = subtitleText;
                break;
            case "comment_from_client":
                arraySubTitleText[1] = subtitleText;
                break;
            case "suggestion_overview":
                arraySubTitleText[3] = subtitleText;
                break;
            case "suggestion_from_client":
                arraySubTitleText[2] = subtitleText;
                break;
            case "meeting_suggestion_old":
                arraySubTitleText[4] = subtitleText;
                break;
            case "meeting_client_canceled":
                arraySubTitleText[5] = subtitleText;
                break;
        }

        // first time -> set initial subtitle
        if (setSubtitleFirstTime && subtitleChoose.equals("meeting_overview")) {
            toolbarMeeting.setSubtitle(subtitleText);
            setSubtitleFirstTime = false;
        }
    }




















}
