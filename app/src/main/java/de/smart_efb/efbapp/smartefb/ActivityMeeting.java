package de.smart_efb.efbapp.smartefb;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    // what to show in tab one
    String showCommandFragmentTabOne = "";

    // what to show in tab two
    String showCommandFragmentTabTwo = "";

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
        viewPagerMeeting = findViewById(R.id.viewPagerMeeting);

        // new pager adapter for OurArrangement
        meetingViewPagerAdapter = new MeetingViewPagerAdapter(getSupportFragmentManager(), this);

        // set pagerAdapter to viewpager
        viewPagerMeeting.setAdapter(meetingViewPagerAdapter);

        //find tablayout and set gravity
        tabLayoutMeeting = findViewById(R.id.tabLayoutMeeting);
        tabLayoutMeeting.setTabGravity(TabLayout.GRAVITY_FILL);

        // and set tablayout with viewpager
        tabLayoutMeeting.setupWithViewPager(viewPagerMeeting);

        // set correct tab zero, one and two title with information new entry and color change -> FIRST TIME
        setTabZeroTitleAndColor();
        setTabOneTitleAndColor();
        setTabTwoTitleAndColor();

        // init listener for tab selected
        tabLayoutMeeting.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";

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

        // check for intent on start time
        // Extras from intent that holds data
        Bundle intentExtras = null;
        // intent
        Intent intent = getIntent();

        if (intent != null) { // intent set?
            // get the link data from the extra
            intentExtras = intent.getExtras();
            if (intentExtras != null && intentExtras.getString("com") != null) { // extra data set?
                if (intentExtras.getString("com").equals("show_meeting") || intentExtras.getString("com").equals("show_client_suggestion") || intentExtras.getString("com").equals("show_suggestion")) { // execute only command show_meeting, show_client_suggestion or show_suggestion (comes from notification
                    // get command and execute it
                    executeIntentCommand(intentExtras.getString("com"), 0L);
                }
            }
        }
    }

    
    private void initMeeting() {

        // init the toolbarMeeting
        toolbarMeeting = (Toolbar) findViewById(R.id.toolbarMeeting);
        setSupportActionBar(toolbarMeeting);
        toolbarMeeting.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // init DB
        myDb = new DBAdapter(getApplicationContext());

        // delete all meeting/ suggestion mark with new but never show (time expired, etc.)
        Long nowTime = System.currentTimeMillis();
        myDb.deleteStatusNewEntryAllOldMeetingAndSuggestion (nowTime);

        // init the prefs
        prefs = getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);

        for (int t=0; t<ConstansClassMeeting.numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
        }

        // enable setting subtitle for the first time
        setSubtitleFirstTime = true;

        // init show on tab zero meeting overview
        showCommandFragmentTabZero = "meeting_overview";

        // init show on tab one suggestion from client
        showCommandFragmentTabOne = "suggestion_from_client";

        // init show on tab two suggestion from coach
        showCommandFragmentTabTwo = "suggestion_overview";

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

        // meeting id for cancle meeting
        Long tmpMeetingId = 0L;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentExtras = intent.getExtras();

        if (intentExtras != null) {

            // get meeting id from external
            tmpMeetingId = intentExtras.getLong("meeting_id",0);

            // get command and execute it
            String tmpCommand = intentExtras.getString("com");
            if (tmpCommand == null) {tmpCommand="";}
            executeIntentCommand (tmpCommand, tmpMeetingId);
        }
    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, Long meetingId) {

        if (command.equals("show_meeting")) {
            // set tab 1 (Meeting)
            TabLayout.Tab tab = tabLayoutMeeting.getTabAt(0);
            if (tab != null) {tab.select();}
        }
        else if (command.equals("show_client_suggestion")) {
            // set tab 1 (client suggestion)
            TabLayout.Tab tab = tabLayoutMeeting.getTabAt(1);
            if (tab != null) {tab.select();}
        }
        else if (command.equals("show_suggestion")) {
            // set tab 2 (client suggestion)
            TabLayout.Tab tab = tabLayoutMeeting.getTabAt(2);
            if (tab != null) {tab.select();}
        }
        else if (command.equals("comment_from_client")) { // Show fragment client comment a meeting

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

            //set fragment in tab two to suggestion from coach
            meetingViewPagerAdapter.setFragmentTabTwo("suggestion_overview");

            // set correct tab two title with information new entry and color change
            tabTitleTextTabTwo = getResources().getString(getResources().getIdentifier("meetingTabTitle_3", "string", getPackageName()));
            setTabTwoTitleAndColor();

            // set command show variable
            showCommandFragmentTabTwo = "suggestion_overview";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab two
            toolbarMeeting.setSubtitle(arraySubTitleText[3]);

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
            tabTitleTextTabTwo = getResources().getString(getResources().getIdentifier("meetingTabTitle_3", "string", getPackageName()));
            setTabTwoTitleAndColor();

            // set command show variable
            showCommandFragmentTabTwo = "suggestion_overview";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab two
            toolbarMeeting.setSubtitle(arraySubTitleText[3]);

        } else if (command.equals("delete_suggestion_from_client_entry")) { // delete suggestion from client entry by client, because meeting found from suggestion or canceneld suggestion

            // delete selected meeting from db
            myDb.deleteSelectedMeetingOrSuggestionFromDb(meetingId);

            //set fragment in tab zero to meeting overview
            //meetingViewPagerAdapter.setFragmentTabOne("suggestion_overview");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabTwo = getResources().getString(getResources().getIdentifier("meetingTabTitle_2", "string", getPackageName()));
            setTabTwoTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "suggestion_from_client";

            // call notify data change
            meetingViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbarMeeting.setSubtitle(arraySubTitleText[2]);

        } else { // Show fragment meeting overview on tab zero

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


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonMeeting = (Button) findViewById(R.id.helpMeetingNow);

        // add button listener to question mark in activity Meeting (toolbar)
        tmpHelpButtonMeeting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMeeting.this, R.style.helpDialogStyle);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityMeeting.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view

                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_meeting, null);

                // show text meeting cancele by client on/off
                TextView tmpFunctionMeetingCanceleOnOff = (TextView) dialogSettings.findViewById(R.id.textViewDialogMeetingSettingsIntroMeetingFunctionCanceleOnOff);
                String tmpTextFunctionMeetingCanceleOnOff;
                if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCanceleMeeting_OnOff, false)) {
                    tmpTextFunctionMeetingCanceleOnOff = ActivityMeeting.this.getResources().getString(R.string.textDialogMeetingSettingsIntroMeetingFunctionCanceleOn);
                }
                else {
                    tmpTextFunctionMeetingCanceleOnOff = ActivityMeeting.this.getResources().getString(R.string.textDialogMeetingSettingsIntroMeetingFunctionCanceleOff);
                }
                tmpFunctionMeetingCanceleOnOff.setText(tmpTextFunctionMeetingCanceleOnOff);

                // show text client suggestion on/off
                TextView tmpFunctionClientSuggestionOnOff = (TextView) dialogSettings.findViewById(R.id.textViewDialogMeetingSettingsIntroClientSuggestionFunctionOnOff);
                String tmpTextFunctionClientSuggestionOnOff;
                if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientSuggestion_OnOff, false)) {
                    tmpTextFunctionClientSuggestionOnOff = ActivityMeeting.this.getResources().getString(R.string.textDialogMeetingSettingsIntroClientSuggestionFunctionOn);
                }
                else {
                    tmpTextFunctionClientSuggestionOnOff = ActivityMeeting.this.getResources().getString(R.string.textDialogMeetingSettingsIntroClientSuggestionFunctionOff);
                }
                tmpFunctionClientSuggestionOnOff.setText(tmpTextFunctionClientSuggestionOnOff);

                // set text client comment for suggestion on/off
                TextView tmpFunctionClientCommentCoachSuggestionOnOff = (TextView) dialogSettings.findViewById(R.id.textViewDialogMeetingSettingsIntroCoachSuggestionFunctionCommentOnOff);
                String tmpTextFunctionClientCommentCoachSuggestionOnOff;
                if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) {
                    tmpTextFunctionClientCommentCoachSuggestionOnOff = ActivityMeeting.this.getResources().getString(R.string.textViewDialogMeetingSettingsIntroCoachSuggestionFunctionCommentOn);
                }
                else {
                    tmpTextFunctionClientCommentCoachSuggestionOnOff = ActivityMeeting.this.getResources().getString(R.string.textViewDialogMeetingSettingsIntroCoachSuggestionFunctionCommentOff);
                }
                tmpFunctionClientCommentCoachSuggestionOnOff.setText(tmpTextFunctionClientCommentCoachSuggestionOnOff);

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

                alertDialogSettings.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // change background and text color of button
                        Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        // Change negative button text and background color
                        negativeButton.setTextColor(ContextCompat.getColor(ActivityMeeting.this, R.color.white));
                        negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
                    }
                });

                // and show the dialog
                alertDialogSettings.show();

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
        if (tabLayoutMeeting.getTabAt(0) != null) {
            tabLayoutMeeting.getTabAt(0).setText(tmpTitleText);
        }
        ActivityMeeting.this.setUnsetTextColorSignalNewTabZero(infoNewEntryOnTabZero);
    }



    // set correct tab one title with information new entry and color change
    private void setTabOneTitleAndColor () {

        ActivityMeeting.this.lookNewEntryOnTabOne();

        String tmpTitleText = tabTitleTextTabOne + infoTextNewEntryPostFixTabOneTitle;
        if (tabLayoutMeeting.getTabAt(1) != null) {
            tabLayoutMeeting.getTabAt(1).setText(tmpTitleText);
        }
        ActivityMeeting.this.setUnsetTextColorSignalNewTabOne(infoNewEntryOnTabOne);
    }

    // set correct tab two title with information new entry and color change
    // call from MeetingFragmentSuggestionOverview!
    void setTabTwoTitleAndColor () {

        ActivityMeeting.this.lookNewEntryOnTabTwo();

        String tmpTitleText = tabTitleTextTabTwo + infoTextNewEntryPostFixTabTwoTitle;
        if (tabLayoutMeeting.getTabAt(2) != null) {
            tabLayoutMeeting.getTabAt(2).setText(tmpTitleText);
        }
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

        Long nowTime = System.currentTimeMillis();
        Cursor c = myDb.getAllRowsMeetingsAndSuggestion("client_suggestion_for_show_attention", nowTime);

        // look for new entrys in db on tab one
        if (myDb.getCountNewEntryMeetingAndSuggestion("suggestion_from_client") > 0) {
            infoNewEntryOnTabOne = true;
            infoTextNewEntryPostFixTabOneTitle = " "+ this.getResources().getString(R.string.newEntryText);
        }
        else if (c != null && c.getCount() > 0 ) {
            infoNewEntryOnTabOne = true;
            infoTextNewEntryPostFixTabOneTitle = " "+ this.getResources().getString(R.string.newAttentionText);
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
        if (myDb.getCountNewEntryMeetingAndSuggestion("suggestion") > 0) {
            infoNewEntryOnTabTwo = true;
            infoTextNewEntryPostFixTabTwoTitle = " " + this.getResources().getString(R.string.newEntryText);
        }
        else if (c != null && c.getCount() > 0 ) {
            infoNewEntryOnTabTwo = true;
            infoTextNewEntryPostFixTabTwoTitle = " "+ this.getResources().getString(R.string.newAttentionText);
        }
        else {
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
