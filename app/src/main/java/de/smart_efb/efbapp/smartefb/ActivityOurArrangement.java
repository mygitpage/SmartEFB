package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewGroupCompat;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by ich on 25.05.16.
 */
public class ActivityOurArrangement extends AppCompatActivity {

    // Max number of comments (current and sketch) <-> Over this number you can write infinitely comments
    final int commentLimitationBorder = 1000;

    // Number of different subtitles
    final int numberOfDifferentSubtitle = 8;

    // Set subtitle first time
    Boolean setSubtitleFirstTime = false;

    // evaluate pause time and active time (get from prefs)
    int evaluatePauseTime = 0;
    int evaluateActivTime = 0;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // reference for the toolbar
    Toolbar toolbar = null;
    ActionBar actionBar = null;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // the date of sketch arrangement
    long currentDateOfSketchArrangement;

    // viewpager and tablayout for the view
    ViewPager viewPagerOurArrangement;
    TabLayout tabLayoutOurArrangement;

    // viewpager adapter
    OurArrangementViewPagerAdapter ourArrangementViewPagerAdapter;

    // Strings for subtitle ("Aktuelle vom...", "Älter als...", "Absprache kommentieren", "Kommentare zeigen", "Absprache bewerten", "Entwuerfe Absprachen" )
    String [] arraySubTitleText = new String[numberOfDifferentSubtitle];

    // what to show in tab zero (like show_comment_for_arrangement, comment_an_arrangement, show_arrangement_now, evaluate_an_arrangement)
    String showCommandFragmentTabZero = "";
    // what to show in tab one (like show_sketch_arrangement, comment_an_sketch_arrangement, show_comment_for_sketch_arrangement)
    String showCommandFragmentTabOne = "";

    // arrangement db-id - for comment, sketch comment, show comment or show sketch comment
    int arrangementDbIdFromLink = 0;
    int arrangementSketchDbIdFromLink = 0;
    //arrangement number and sketch number in listview
    int arrangementNumberInListView = 0;
    int arrangementSketchNumberInListView = 0;

    // evaluate next arrangement true -> yes, there is a next arrangement to evaluate; false -> there is nothing more
    boolean evaluateNextArrangement = false;

    // info new entry on tab zero or one
    Boolean infoNewEntryOnTabZero = false;
    Boolean infoNewEntryOnTabOne = false;
    String infoTextNewEntryPostFixTabZeroTitle = "";
    String infoTextNewEntryPostFixTabOneTitle = "";
    String tabTitleTextTabZero = "";
    String tabTitleTextTabOne = "";


    // reference to the DB
    DBAdapter myDb;

    // reference to dialog settings
    AlertDialog alertDialogSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_arrangement);

        // init our arragement
        initOurArrangement();

        // init alarm manager
        setAlarmManagerOurArrangement ();

         // find viewpager in view
        viewPagerOurArrangement = (ViewPager) findViewById(R.id.viewPagerOurArrangement);
        // new pager adapter for OurArrangement
        ourArrangementViewPagerAdapter = new OurArrangementViewPagerAdapter(getSupportFragmentManager(), this);
        // set pagerAdapter to viewpager
        viewPagerOurArrangement.setAdapter(ourArrangementViewPagerAdapter);

        //find tablayout and set gravity
        tabLayoutOurArrangement = (TabLayout) findViewById(R.id.tabLayoutOurArrangement);
        tabLayoutOurArrangement.setTabGravity(TabLayout.GRAVITY_FILL);

        // and set tablayout with viewpager
        tabLayoutOurArrangement.setupWithViewPager(viewPagerOurArrangement);


        // set correct tab zero and one title with information new entry and color change -> FIRST TIME
        setTabZeroTitleAndColor();
        setTabOneTitleAndColor();


        // init listener for tab selected
        tabLayoutOurArrangement.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";

                // Change the subtitle of the activity
                switch (tab.getPosition()) {
                    case 0: // title for tab zero
                        switch (showCommandFragmentTabZero) {
                            case "show_arrangement_now":
                                tmpSubtitleText = arraySubTitleText[0];
                                break;
                            case "comment_an_arrangement":
                                tmpSubtitleText = arraySubTitleText[3];
                                break;
                            case "show_comment_for_arrangement":
                                tmpSubtitleText = arraySubTitleText[4];
                                break;
                            case "evaluate_an_arrangement":
                                tmpSubtitleText = arraySubTitleText[5];
                                break;
                        }

                        // set correct tab zero title with information new entry and color change
                        setTabZeroTitleAndColor();

                        break;
                   case 1: // title for tab one
                        switch (showCommandFragmentTabOne) {
                            case "show_sketch_arrangement":
                                tmpSubtitleText = arraySubTitleText[1];
                                break;
                            case "comment_an_sketch_arrangement":
                                tmpSubtitleText = arraySubTitleText[6];
                                break;
                            case "show_comment_for_sketch_arrangement":
                                tmpSubtitleText = arraySubTitleText[7];
                                break;
                        }

                       // set correct tab one title with information new entry and color change
                       setTabOneTitleAndColor();

                       break;
                    case 2: // title for tab two
                        tmpSubtitleText = arraySubTitleText[2];
                        break;
                    default:
                        tmpSubtitleText = arraySubTitleText[0];
                        break;
                }

                // set toolbar text
                toolbar.setSubtitle(tmpSubtitleText);

                // call viewpager
                viewPagerOurArrangement.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

    }


    // Look for new intents (with data from URI or putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // Extras from intent that holds data
        Bundle intentExtras = null;

        arrangementDbIdFromLink = 0;
        arrangementNumberInListView = 0;
        evaluateNextArrangement = false;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentExtras = intent.getExtras();

        int tmpDbId = 0;
        int tmpNumberinListView = 0;
        Boolean tmpEvalNext = false;

        if (intentExtras != null) {
           // get data that comes with extras
            tmpDbId = intentExtras.getInt("db_id",0);
            tmpNumberinListView = intentExtras.getInt("arr_num",0);
            tmpEvalNext = intentExtras.getBoolean("eval_next");
            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"), tmpDbId, tmpNumberinListView, tmpEvalNext);
        }

    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, int tmpDbId, int tmpNumberinListView, Boolean tmpEvalNext) {

        String tmpTabTitle = "";

        if (command.equals("show_comment_for_arrangement")) { // Show fragment all comments for arrangement

            // set global varibales
            arrangementDbIdFromLink = tmpDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("show_comment_for_arrangement");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1b", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "show_comment_for_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[4]);



        } else if (command.equals("comment_an_arrangement")) { // Show fragment comment arrangement

            // set global varibales
            arrangementDbIdFromLink = tmpDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("comment_an_arrangement");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1a", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "comment_an_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[3]);

        } else if (command.equals("evaluate_an_arrangement")) { // Show evaluate a arrangement

            // set global varibales
            arrangementDbIdFromLink = tmpDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to evaluate
            OurArrangementViewPagerAdapter.setFragmentTabZero("evaluate_an_arrangement");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1c", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "evaluate_an_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[5]);


        } else if (command.equals("comment_an_sketch_arrangement")) { // Comment sketch arrangement -> TAB ONE

            // set global varibales
            arrangementSketchDbIdFromLink = tmpDbId;
            arrangementSketchNumberInListView = tmpNumberinListView;

            //set fragment in tab one to comment an sketch arrangement
            OurArrangementViewPagerAdapter.setFragmentTabOne("comment_an_sketch_arrangement");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2a", "string", getPackageName()));
            setTabOneTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "comment_an_sketch_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbar.setSubtitle(arraySubTitleText[6]);

        } else if (command.equals("show_sketch_arrangement")) { // Show sketch Arrangments -> TAB ONE

            // set global varibales
            arrangementSketchDbIdFromLink = tmpDbId;
            arrangementSketchNumberInListView = tmpNumberinListView;

            //set fragment in tab one to show sketch arrangement
            OurArrangementViewPagerAdapter.setFragmentTabOne("show_sketch_arrangement");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2", "string", getPackageName()));
            setTabOneTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "show_sketch_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbar.setSubtitle(arraySubTitleText[1]);

        } else if (command.equals("show_comment_for_sketch_arrangement")) { // Show comments for sketch Arrangments -> TAB ONE

            // set global varibales
            arrangementSketchDbIdFromLink = tmpDbId;
            arrangementSketchNumberInListView = tmpNumberinListView;

            //set fragment in tab one to show comment sketch arrangement
            OurArrangementViewPagerAdapter.setFragmentTabOne("show_comment_for_sketch_arrangement");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2b", "string", getPackageName()));
            setTabOneTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "show_comment_for_sketch_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbar.setSubtitle(arraySubTitleText[7]);

        }
        else { // Show fragment arrangement now -> Tab 0

            // set global varibales
            arrangementDbIdFromLink = tmpDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("show_arrangement_now");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "show_arrangement_now";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[0]);

        }

    }


    // init the activity
    private void initOurArrangement() {

        // init the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarOurArrangement);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());
        //get date of sketch arrangement
        currentDateOfSketchArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis());

        // init show on tab zero arrangemet now
        showCommandFragmentTabZero = "show_arrangement_now";
        // init show on tab one sketch arrangemet
        showCommandFragmentTabOne = "show_sketch_arrangement";


        for (int t=0; t<numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
        }

        // enable setting subtitle for the first time
        setSubtitleFirstTime = true;

        //set tab title string
        tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1", "string", getPackageName()));
        tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2", "string", getPackageName()));

        // create help dialog in OurArrangement
        createHelpDialog();



    }






    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonOurArrangement = (Button) findViewById(R.id.helpOurArrangementNow);


        // add button listener to question mark in activity OurArrangement (toolbar)
        tmpHelpButtonOurArrangement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView tmpdialogTextView;
                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOurArrangement.this);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityOurArrangement.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_our_arrangement, null);

                // show intro for settings
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsIntro);
                tmpdialogTextView.setText(ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsIntro));

                // show the settings for evaluation (like evaluation period, on/off-status, ...)
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsEvaluate);
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) {

                    String tmpTxtEvaluate = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluateEnable);
                    String tmpTxtEvaluate1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluatePeriod);
                    String tmpCompleteTxtEvaluate1String = String.format(tmpTxtEvaluate1, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "kk.mm"), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy"),EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "kk.mm"));
                    tmpdialogTextView.setText(tmpTxtEvaluate + " " + tmpCompleteTxtEvaluate1String);
                }
                else {
                    String tmpTxtEvaluate = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluateDisable);
                    tmpdialogTextView.setText(tmpTxtEvaluate);
                }

                // show the settings for comment (like on/off-status, count comment...)
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsComment);
                String tmpTxtComment, tmpTxtComment1, tmpTxtComment2, tmpTxtCommentSum;

                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false)) {

                    tmpTxtComment = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentEnable);

                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) < commentLimitationBorder) { // write infinitely comments?

                        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) == 1) {
                            tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountSingular);
                        }
                        else {
                            tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountPlural);
                            tmpTxtComment1 = String.format(tmpTxtComment1, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0));
                        }
                    }
                    else {
                        tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountInfinitely);
                    }

                    // count comment - status
                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0)) {
                        switch (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0)) {
                            case 0:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCountCommentZero);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                break;
                            case 1:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberSingular);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                break;
                            default:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberPlural);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0));
                                break;
                        }
                    }
                    else {
                        tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberOff);
                    }

                    tmpTxtCommentSum = tmpTxtComment + " " + tmpTxtComment1 + " " + tmpTxtComment2;

                    tmpdialogTextView.setText(tmpTxtCommentSum);
                }
                else {
                    tmpTxtComment = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentDisable);
                    tmpdialogTextView.setText(tmpTxtComment);
                }

                // show the settings for old arrangement
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsOldArrangement);
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false)) {

                    String tmpTxtOldArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsOldArrangementEnable);
                    tmpdialogTextView.setText(tmpTxtOldArrangement);
                }
                else {
                    String tmpTxtOldArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsOldArrangementDisable);
                    tmpdialogTextView.setText(tmpTxtOldArrangement);
                }

                // show the settings for sketch arrangement
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsSketchArrangement);
                String tmpTxtSketchArrangementSum, tmpTxtSketchArrangement, tmpTxtSketchArrangement1, tmpTxtSketchArrangement2, tmpTxtSketchArrangement3;
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false)) {

                    tmpTxtSketchArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchArrangementEnable);

                    // comment sketch arrangements?
                    if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, false)) {

                        tmpTxtSketchArrangement1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentArrangementEnable);

                        if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) < commentLimitationBorder) { // write infinitely sketch comments?

                            if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) == 1) {
                                tmpTxtSketchArrangement2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountSingular);
                            }
                            else {
                                tmpTxtSketchArrangement2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountPlural);
                                tmpTxtSketchArrangement2 = String.format(tmpTxtSketchArrangement2, prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0));
                            }
                        }
                        else {
                            tmpTxtSketchArrangement2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountInfinitely);
                        }

                        // count sketch comment - status
                        if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0)) {
                            switch (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0)) {
                                case 0:
                                    tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCountCommentZero);
                                    tmpTxtSketchArrangement3 = String.format(tmpTxtSketchArrangement3, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                    break;
                                case 1:
                                    tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountNumberSingular);
                                    tmpTxtSketchArrangement3 = String.format(tmpTxtSketchArrangement3, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                    break;
                                default:
                                    tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountNumberPlural);
                                    tmpTxtSketchArrangement3 = String.format(tmpTxtSketchArrangement3, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"), prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0));
                                    break;
                            }
                        }
                        else {
                            tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountNumberOff);
                        }
                    }
                    else {
                        tmpTxtSketchArrangement1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentArrangementDisable);
                        tmpTxtSketchArrangement2 = "";
                        tmpTxtSketchArrangement3 = "";
                    }
                    tmpTxtSketchArrangementSum = tmpTxtSketchArrangement + " " + tmpTxtSketchArrangement1 + " " + tmpTxtSketchArrangement2 + " " + tmpTxtSketchArrangement3;

                }
                else {
                    tmpTxtSketchArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchArrangementDisable);
                    tmpTxtSketchArrangementSum = tmpTxtSketchArrangement;

                }
                tmpdialogTextView.setText(tmpTxtSketchArrangementSum);

                // get string ressources
                String tmpTextCloseDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementCloseDialog);
                String tmpTextTitleDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementTitleDialog);

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


    // set alarmmanager for evaluation time
    void setAlarmManagerOurArrangement () {

        PendingIntent pendingIntentOurArrangementEvaluate;

        // get reference to alarm manager
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // create intent for backcall to broadcast receiver
        Intent evaluateAlarmIntent = new Intent(ActivityOurArrangement.this, AlarmReceiverOurArrangement.class);

        // get evaluate pause time and active time
        evaluatePauseTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, 43200); // default value 43200 is 12 hours
        evaluateActivTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, 43200); // default value 43200 is 12 hours

        // get start time and end time for evaluation
        Long startEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis());
        Long endEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis());

        Long tmpSystemTimeInMills = System.currentTimeMillis();
        int tmpEvalutePaAcTime = evaluateActivTime * 1000;
        String tmpIntentExtra = "evaluate";
        String tmpChangeDbEvaluationStatus = "set";

        // get calendar and init
        Calendar calendar = Calendar.getInstance();

        // set alarm manager when current time is between start date and end date and evaluation is enable
        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

            calendar.setTimeInMillis(startEvaluationDate);

            do {
                calendar.add(Calendar.SECOND, evaluateActivTime);
                tmpIntentExtra = "evaluate";
                tmpChangeDbEvaluationStatus = "set";
                tmpEvalutePaAcTime = evaluateActivTime * 1000; // make mills-seconds
                if (calendar.getTimeInMillis() < tmpSystemTimeInMills) {
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpIntentExtra = "pause";
                    tmpChangeDbEvaluationStatus = "delete";
                    tmpEvalutePaAcTime = evaluatePauseTime * 1000; // make mills-seconds
                }
            } while (calendar.getTimeInMillis() < tmpSystemTimeInMills);

            // update table ourArrangement in db -> set or delete
            myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()),tmpChangeDbEvaluationStatus);

            // put extras to intent -> "evaluate" or "delete"
            evaluateAlarmIntent.putExtra("evaluateState",tmpIntentExtra);

            // create call (pending intent) for alarm manager
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(ActivityOurArrangement.this, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // set alarm
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpEvalutePaAcTime, pendingIntentOurArrangementEvaluate);
        }
        else { // delete alarm - it is out of time

            // update table ourArrangement in db -> evaluation disable
            myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()),"delete");
            // crealte pending intent
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(ActivityOurArrangement.this, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // delete alarm
            manager.cancel(pendingIntentOurArrangementEvaluate);
        }

    }


    // set correct tab zero title with information new entry and color change
    private void setTabZeroTitleAndColor () {

        ActivityOurArrangement.this.lookNewEntryOnTabZero();

        tabLayoutOurArrangement.getTabAt(0).setText(tabTitleTextTabZero + infoTextNewEntryPostFixTabZeroTitle);
        ActivityOurArrangement.this.setUnsetTextColorSignalNewTabZero(infoNewEntryOnTabZero);

    }


    // set correct tab one title with information new entry and color change
    private void setTabOneTitleAndColor () {

        ActivityOurArrangement.this.lookNewEntryOnTabOne();

        tabLayoutOurArrangement.getTabAt(1).setText(tabTitleTextTabOne + infoTextNewEntryPostFixTabOneTitle);
        ActivityOurArrangement.this.setUnsetTextColorSignalNewTabOne(infoNewEntryOnTabOne);

    }


    // look for new entry on tab zero
    private void lookNewEntryOnTabZero () {

        // look for new entrys in db on tab zero
        if (myDb.getCountAllNewEntryOurArrangementComment(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis())) > 0 || myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "current") > 0 ) {
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
        if (myDb.getCountAllNewEntryOurArrangementSketchComment(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis())) > 0 || myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis()), "sketch") > 0) {
            infoNewEntryOnTabOne = true;
            infoTextNewEntryPostFixTabOneTitle = " "+ this.getResources().getString(R.string.newEntryText);
        }
        else {
            infoNewEntryOnTabOne = false;
            infoTextNewEntryPostFixTabOneTitle = "";
        }

    }

    // set/ unset textcolor for tab title on tab zero
    private void setUnsetTextColorSignalNewTabZero (Boolean colorSet) {

        int tmpTextColor;

        if (colorSet) {
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.text_accent_color);
                    //parseColor("#F330F0");
        }
        else {
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.colorAccent);
        }

        // Change tab text color on tab zero
        ViewGroup vg = (ViewGroup) tabLayoutOurArrangement.getChildAt(0);
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
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.text_accent_color);
            //parseColor("#F330F0");
        }
        else {
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.colorAccent);
        }

        // Change tab text color on tab zero
        ViewGroup vg = (ViewGroup) tabLayoutOurArrangement.getChildAt(0);
        ViewGroup vgTab = (ViewGroup) vg.getChildAt(1); //Tab One
        int tabChildsCount = vgTab.getChildCount();
        for (int i=0; i<tabChildsCount; i++) {
            View tabViewCild = vgTab.getChildAt(i);
            if (tabViewCild instanceof TextView) {
                ((TextView) tabViewCild).setTextColor(tmpTextColor);
            }
        }

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



    // getter for DB-Id of arrangement
    public int getArrangementDbIdFromLink () {

        return arrangementDbIdFromLink;

    }

    // getter for DB-Id of sketch arrangement
    public int getSketchArrangementDbIdFromLink () {

        return arrangementSketchDbIdFromLink;

    }


    // getter for arrangement number in listview
    public int getArrangementNumberInListview () {

        return arrangementNumberInListView;

    }

    // getter for sketch arrangement number in listview
    public int getSketchArrangementNumberInListview () {

        return arrangementSketchNumberInListView;

    }



    // geter for evaluate next arrangement
    public boolean getEvaluateNextArrangement () {

        return evaluateNextArrangement;

    }





    // geter for border for comments
    public boolean isCommentLimitationBorderSet (String currentSketch) {

        switch (currentSketch) {
            case "current":
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) < commentLimitationBorder) { // is there a border for comments
                    return true; // comments are limited!
                }
                break;
            case "sketch":
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) < commentLimitationBorder) { // is there a border for sketch comments
                    return true; // sketch comments are limited!
                }
                break;
        }

        return  false; // write infinitely comments!

    }


    // setter for subtitle in OurArrangement toolbar
    public void setOurArrangementToolbarSubtitle (String subtitleText, String subtitleChoose) {

        switch (subtitleChoose) {

           case "sketch":
               arraySubTitleText[1] = subtitleText;
               break;
           case "old":
               arraySubTitleText[2] = subtitleText;
               break;
           case "now":
               arraySubTitleText[0] = subtitleText;
               break;
            case "nowComment":
                arraySubTitleText[3] = subtitleText;
                break;
            case "showComment":
                arraySubTitleText[4] = subtitleText;
                break;
            case "evaluate":
                arraySubTitleText[5] = subtitleText;
                break;
            case "sketchComment":
                arraySubTitleText[6] = subtitleText;
                break;
            case "showSketchComment":
                arraySubTitleText[7] = subtitleText;
                break;

        }

        // first time -> set initial subtitle
        if (setSubtitleFirstTime && subtitleChoose.equals("now")) {
            toolbar.setSubtitle(subtitleText);
            setSubtitleFirstTime = false;
        }

    }

}
