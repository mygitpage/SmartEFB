package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by ich on 07.06.16.
 */
public class ActivityOurGoals extends AppCompatActivity {


    // Max number of comments (jointly and debetable goals) <-> Over this number you can write infinitely comments
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
    Toolbar toolbarOurGoals = null;
    ActionBar actionBarOurGoals = null;

    // the current date of jointly goals -> the other are old (look at tab old)
    long currentDateOfJointlyGoals;

    // the current date of debetable goals
    long getCurrentDateOfDebetableGoals;

    // viewpager and tablayout for the view
    ViewPager viewPagerOurGoals;
    TabLayout tabLayoutOurGoals;

    // viewpager adapter
    OurGoalsViewPagerAdapter ourGoalsViewPagerAdapter;

    // Strings for subtitle ()
    String [] arraySubTitleText = new String[numberOfDifferentSubtitle];

    // what to show in tab zero (like )
    String showCommandFragmentTabZero = "";
    // what to show in tab one (like )
    String showCommandFragmentTabOne = "";

    // jointly and debetable goal db-id - for
    int jointlyGoalDbIdFromLink = 0;
    int debetableGoalDbIdFromLink = 0;

    // jointly and debetable goal number in listview
    int jointlyGoalNumberInListView = 0;
    int debetableGoalNumberInListView = 0;

    // reference to the DB
    DBAdapter myDb;

    // reference to dialog settings
    AlertDialog alertDialogSettings;

    // evaluate next goal true -> yes, there is a next goal to evaluate; false -> there is nothing more
    Boolean evaluateNextJointlyGoal = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_goals);

        // init our goals
        initOurGoals();

        // init alarm manager
        setAlarmManagerOurGoals ();

        // find viewpager in view
        viewPagerOurGoals = (ViewPager) findViewById(R.id.viewPagerOurGoals);
        // new pager adapter for OurGoals
        ourGoalsViewPagerAdapter = new OurGoalsViewPagerAdapter(getSupportFragmentManager(), this);
        // set pagerAdapter to viewpager
        viewPagerOurGoals.setAdapter(ourGoalsViewPagerAdapter);

        //find tablayout and set gravity
        tabLayoutOurGoals = (TabLayout) findViewById(R.id.tabLayoutOurGoals);
        tabLayoutOurGoals.setTabGravity(TabLayout.GRAVITY_FILL);

        // and set tablayout with viewpager
        tabLayoutOurGoals.setupWithViewPager(viewPagerOurGoals);

        // init listener for tab selected
        tabLayoutOurGoals.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";

                // Change the subtitle of the activity
                switch (tab.getPosition()) {
                    case 0: // title for tab zero
                        switch (showCommandFragmentTabZero) {
                            case "show_jointly_goals_now":

                                Log.d("OurGoals","Sub-Selected:"+arraySubTitleText[0]);

                                tmpSubtitleText = arraySubTitleText[0];
                                break;
                            case "comment_an_jointly_goal":
                                tmpSubtitleText = arraySubTitleText[3];
                                break;
                            case "show_comment_for_jointly_goal":
                                tmpSubtitleText = arraySubTitleText[4];
                                break;
                            case "evaluate_an_jointly_goal":
                                tmpSubtitleText = arraySubTitleText[5];
                                break;
                        }
                        break;
                    case 1: // title for tab one
                        switch (showCommandFragmentTabOne) {
                            case "show_debetable_goals_now":
                                tmpSubtitleText = arraySubTitleText[1];
                                break;
                            case "comment_an_debetable_goal":
                                tmpSubtitleText = arraySubTitleText[6];
                                break;
                            case "show_comment_for_debetable_goal":
                                tmpSubtitleText = arraySubTitleText[7];
                                break;
                        }
                        break;
                    case 2: // title for tab two
                        tmpSubtitleText = arraySubTitleText[2];
                        break;
                    default:
                        tmpSubtitleText = arraySubTitleText[0];
                        break;
                }

                // set toolbar text
                toolbarOurGoals.setSubtitle(tmpSubtitleText);

                // call viewpager
                viewPagerOurGoals.setCurrentItem(tab.getPosition());

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

        // Uri from intent that holds data
        Uri intentLinkData = null;

        // Extras from intent that holds data
        Bundle intentExtras = null;

        jointlyGoalDbIdFromLink = 0;
        jointlyGoalNumberInListView = 0;
        evaluateNextJointlyGoal = false;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        /*intentLinkData = intent.getData();*/
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

        if (command.equals("show_comment_for_jointly_goal")) { // Show fragment all comments for jointly goal

            // set global varibales
            jointlyGoalDbIdFromLink = tmpDbId;
            jointlyGoalNumberInListView = tmpNumberinListView;
            evaluateNextJointlyGoal = tmpEvalNext;

            //set fragment in tab zero to comment
            OurGoalsViewPagerAdapter.setFragmentTabZero("show_comment_for_jointly_goal");

            // set correct tab zero titel
            try {
                tmpTabTitle = getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_1b", "string", getPackageName()));
                tabLayoutOurGoals.getTabAt(0).setText(tmpTabTitle);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // set command show variable
            showCommandFragmentTabZero = "show_comment_for_jointly_goal";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarOurGoals.setSubtitle(arraySubTitleText[4]);



        } else if (command.equals("comment_an_jointly_goal")) { // Show fragment comment jointly goal

            // set global varibales
            jointlyGoalDbIdFromLink = tmpDbId;
            jointlyGoalNumberInListView = tmpNumberinListView;
            evaluateNextJointlyGoal = tmpEvalNext;

            //set fragment in tab zero to comment
            OurGoalsViewPagerAdapter.setFragmentTabZero("comment_an_jointly_goal");

            // set correct tab zero titel
            tabLayoutOurGoals.getTabAt(0).setText(getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_1a", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabZero = "comment_an_jointly_goal";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarOurGoals.setSubtitle(arraySubTitleText[3]);

        } else if (command.equals("evaluate_an_jointly_goal")) { // Show evaluate a goal

            // set global varibales
            jointlyGoalDbIdFromLink = tmpDbId;
            jointlyGoalNumberInListView = tmpNumberinListView;
            evaluateNextJointlyGoal = tmpEvalNext;

            //set fragment in tab zero to evaluate
            OurGoalsViewPagerAdapter.setFragmentTabZero("evaluate_an_jointly_goal");

            // set correct tab zero titel
            tabLayoutOurGoals.getTabAt(0).setText(getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_1c", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabZero = "evaluate_an_jointly_goal";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbarOurGoals.setSubtitle(arraySubTitleText[5]);


        } else if (command.equals("comment_an_debetable_goal")) { // Comment debetable goal -> TAB ONE

            // set global varibales
            debetableGoalDbIdFromLink = tmpDbId;
            debetableGoalNumberInListView = tmpNumberinListView;

            //set fragment in tab one to comment an debetable goal
            OurGoalsViewPagerAdapter.setFragmentTabOne("comment_an_debetable_goal");

            // set correct tab one titel
            tabLayoutOurGoals.getTabAt(1).setText(getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_2a", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabOne = "comment_an_debetable_goal";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbarOurGoals.setSubtitle(arraySubTitleText[6]);

        } else if (command.equals("show_debetable_goals_now")) { // Show debetable goals -> TAB ONE

            // set global varibales
            debetableGoalDbIdFromLink = tmpDbId;
            debetableGoalNumberInListView = tmpNumberinListView;

            //set fragment in tab one to show debetable goals
            OurGoalsViewPagerAdapter.setFragmentTabOne("show_debetable_goals_now");

            // set correct tab one titel
            tabLayoutOurGoals.getTabAt(1).setText(getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_2", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabOne = "show_debetable_goals_now";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbarOurGoals.setSubtitle(arraySubTitleText[1]);

        } else if (command.equals("show_comment_for_debetable_goal")) { // Show comments for debetable goals -> TAB ONE

            // set global varibales
            debetableGoalDbIdFromLink = tmpDbId;
            debetableGoalNumberInListView = tmpNumberinListView;

            //set fragment in tab one to show comment debetable goals
            OurGoalsViewPagerAdapter.setFragmentTabOne("show_comment_for_debetable_goal");

            // set correct tab one titel
            tabLayoutOurGoals.getTabAt(1).setText(getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_2b", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabOne = "show_comment_for_debetable_goal";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbarOurGoals.setSubtitle(arraySubTitleText[7]);

        }
        else { // Show fragment jointly goals now -> Tab ZERO

            // set global varibales
            jointlyGoalDbIdFromLink = tmpDbId;
            jointlyGoalNumberInListView = tmpNumberinListView;
            evaluateNextJointlyGoal = tmpEvalNext;

            //set fragment in tab zero to comment
            OurGoalsViewPagerAdapter.setFragmentTabZero("show_jointly_goals_now");

            // set correct tab zero titel
            tabLayoutOurGoals.getTabAt(0).setText(getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_1", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabZero = "show_jointly_goals_now";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero

            Log.d("OurGoals","Sub-Else:"+arraySubTitleText[0]);

            toolbarOurGoals.setSubtitle(arraySubTitleText[0]);

        }

    }

    // init the activity Our Goals
    private void initOurGoals() {

        // init the toolbar
        toolbarOurGoals = (Toolbar) findViewById(R.id.toolbarOurGoals);
        setSupportActionBar(toolbarOurGoals);
        toolbarOurGoals.setTitleTextColor(Color.WHITE);
        actionBarOurGoals = getSupportActionBar();
        actionBarOurGoals.setDisplayHomeAsUpEnabled(true);

        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of jointly goals
        currentDateOfJointlyGoals = prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis());
        //get date of debetable goals
        getCurrentDateOfDebetableGoals = prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis());

        // init show on tab zero jointly goals now
        showCommandFragmentTabZero = "show_jointly_goals_now";
        // init show on tab one debetable goals now
        showCommandFragmentTabOne = "show_debetable_goals_now";


        for (int t=0; t<numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
        }

        // enable setting subtitle for the first time
        setSubtitleFirstTime = true;

        // create help dialog in OurGoals
        createHelpDialog();

    }




    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonOurGoals = (Button) findViewById(R.id.helpOurGoalsNow);


        // add button listener to question mark in activity OurGoals (toolbar)
        tmpHelpButtonOurGoals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView tmpdialogTextView;
                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOurGoals.this);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityOurGoals.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_our_goals, null);

                // show intro for settings
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurGoalsSettingsIntro);
                tmpdialogTextView.setText(ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsSettingsIntro));

                // show the settings for evaluation (like evaluation period, on/off-status, ...)
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurGoalsJointlyGoalsSettingsEvaluate);
                if (prefs.getBoolean("showEvaluateLinkJointlyGoals", false)) {

                    String tmpTxtEvaluate = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsEvaluateEnable);
                    String tmpTxtEvaluate1 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsEvaluatePeriod);
                    String tmpCompleteTxtEvaluate1String = String.format(tmpTxtEvaluate1, EfbHelperClass.timestampToDateFormat(prefs.getLong("startDataJointlyGoalsEvaluationInMills", System.currentTimeMillis()), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(prefs.getLong("startDataJointlyGoalsEvaluationInMills", System.currentTimeMillis()), "kk.mm"), EfbHelperClass.timestampToDateFormat(prefs.getLong("endDataJointlyGoalsEvaluationInMills", System.currentTimeMillis()), "dd.MM.yyyy"),EfbHelperClass.timestampToDateFormat(prefs.getLong("endDataJointlyGoalsEvaluationInMills", System.currentTimeMillis()), "kk.mm"));
                    tmpdialogTextView.setText(tmpTxtEvaluate + " " + tmpCompleteTxtEvaluate1String);
                }
                else {
                    String tmpTxtEvaluate = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsEvaluateDisable);
                    tmpdialogTextView.setText(tmpTxtEvaluate);
                }

                // show the settings for comment (like on/off-status, count comment...)
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurGoalsJointlyGoalsSettingsComment);
                String tmpTxtComment, tmpTxtComment1, tmpTxtComment2, tmpTxtCommentSum;

                if (prefs.getBoolean("showCommentLinkJointlyGoals", false)) {

                    tmpTxtComment = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentEnable);

                    if (prefs.getInt("commentJointlyGoalMaxCountComment",0) < commentLimitationBorder) { // write infinitely comments?

                        if (prefs.getInt("commentJointlyGoalMaxCountComment",0) == 1) {
                            tmpTxtComment1 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentCountSingular);
                        }
                        else {
                            tmpTxtComment1 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentCountPlural);
                            tmpTxtComment1 = String.format(tmpTxtComment1, prefs.getInt("commentJointlyGoalMaxCountComment",0));
                        }
                    }
                    else {
                        tmpTxtComment1 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentCountInfinitely);
                    }

                    // count comment - status
                    if (prefs.getInt("commentJointlyGoalCountComment",0) < prefs.getInt("commentJointlyGoalMaxCountComment",0)) {
                        switch (prefs.getInt("commentJointlyGoalCountComment", 0)) {
                            case 0:
                                tmpTxtComment2 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCountCommentZero);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong("jointlyGoalsCommentOurGoalsTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"));
                                break;
                            case 1:
                                tmpTxtComment2 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentCountNumberSingular);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong("jointlyGoalsCommentOurGoalsTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"));
                                break;
                            default:
                                tmpTxtComment2 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentCountNumberPlural);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong("jointlyGoalsCommentOurGoalsTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"), prefs.getInt("commentJointlyGoalCountComment",0));
                                break;
                        }
                    }
                    else {
                        tmpTxtComment2 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentCountNumberOff);
                    }

                    tmpTxtCommentSum = tmpTxtComment + " " + tmpTxtComment1 + " " + tmpTxtComment2;

                    tmpdialogTextView.setText(tmpTxtCommentSum);
                }
                else {
                    tmpTxtComment = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsCommentDisable);
                    tmpdialogTextView.setText(tmpTxtComment);
                }

                // show the settings for old jointly goals
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurGoalsJointlyGoalsSettingsOld);
                if (prefs.getBoolean("showOldGoals", false)) {

                    String tmpTxtOldJointlyGoals = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsOldEnable);
                    tmpdialogTextView.setText(tmpTxtOldJointlyGoals);
                }
                else {
                    String tmpTxtOldJointlyGoals = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsJointlyGoalsSettingsOldDisable);
                    tmpdialogTextView.setText(tmpTxtOldJointlyGoals);
                }

                // show the settings for debetable goals
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurGoalsDebetableGoalsSettings);
                String tmpTxtDebetablGoalSum, tmpTxtDebetableGoal, tmpTxtDebetableGoal1, tmpTxtDebetableGoal2, tmpTxtDebetableGoal3;
                if (prefs.getBoolean("showDebetableGoals", false)) {

                    tmpTxtDebetableGoal = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsEnable);


                    // comment debetable goals?
                    if (prefs.getBoolean("showCommentLinkDebetableGoals", false)) {

                        tmpTxtDebetableGoal1 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentEnable);




                        if (prefs.getInt("commentDebetableGoalsOurGoalsMaxComment",0) < commentLimitationBorder) { // write infinitely debetable goal comments?

                            if (prefs.getInt("commentDebetableGoalsOurGoalsMaxComment",0) == 1) {
                                tmpTxtDebetableGoal2 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentCountSingular);
                            }
                            else {
                                tmpTxtDebetableGoal2 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentCountPlural);
                                tmpTxtDebetableGoal2 = String.format(tmpTxtDebetableGoal2, prefs.getInt("commentDebetableGoalsOurGoalsMaxComment",0));
                            }
                        }
                        else {
                            tmpTxtDebetableGoal2 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentCountInfinitely);
                        }

                        // count debetable goal comment - status
                        if (prefs.getInt("commentDebetableGoalsOurGoalsCountComment",0) < prefs.getInt("commentDebetableGoalsOurGoalsMaxComment",0)) {
                            switch (prefs.getInt("commentDebetableGoalsOurGoalsCountComment", 0)) {
                                case 0:
                                    tmpTxtDebetableGoal3 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentNumberZero);
                                    tmpTxtDebetableGoal3 = String.format(tmpTxtDebetableGoal3, EfbHelperClass.timestampToDateFormat(prefs.getLong("debetableGoalsCommentOurGoalsTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"));
                                    break;
                                case 1:
                                    tmpTxtDebetableGoal3 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentNumberSingular);
                                    tmpTxtDebetableGoal3 = String.format(tmpTxtDebetableGoal3, EfbHelperClass.timestampToDateFormat(prefs.getLong("debetableGoalsCommentOurGoalsTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"));
                                    break;
                                default:
                                    tmpTxtDebetableGoal3 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentNumberNumberPlural);
                                    tmpTxtDebetableGoal3 = String.format(tmpTxtDebetableGoal3, EfbHelperClass.timestampToDateFormat(prefs.getLong("debetableGoalsCommentOurGoalsTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"), prefs.getInt("commentDebetableGoalsOurGoalsCountComment",0));
                                    break;
                            }
                        }
                        else {
                            tmpTxtDebetableGoal3 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentNumberOff);
                        }
                    }
                    else {
                        tmpTxtDebetableGoal1 = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsSettingsCommentDisable);
                        tmpTxtDebetableGoal2 = "";
                        tmpTxtDebetableGoal3 = "";
                    }
                    tmpTxtDebetablGoalSum = tmpTxtDebetableGoal + " " + tmpTxtDebetableGoal1 + " " + tmpTxtDebetableGoal2 + " " + tmpTxtDebetableGoal3;

                }
                else {
                    tmpTxtDebetableGoal = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsDebetableGoalsDisable);
                    tmpTxtDebetablGoalSum = tmpTxtDebetableGoal;

                }
                tmpdialogTextView.setText(tmpTxtDebetablGoalSum);

                // get string ressources
                String tmpTextCloseDialog = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsCloseDialog);
                String tmpTextTitleDialog = ActivityOurGoals.this.getResources().getString(R.string.textDialogOurGoalsTitleDialog);

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



    // getter for DB-Id of jointly goal
    public int getJointlyGoalDbIdFromLink () {

        return jointlyGoalDbIdFromLink;

    }



    // getter for DB-Id of debetable goal
    public int getDebetableGoalDbIdFromLink () {

        return debetableGoalDbIdFromLink;

    }


    // getter for jointly goal number in listview
    public int getJointlyGoalNumberInListview () {

        return jointlyGoalNumberInListView;

    }

    // getter for debetable goal number in listview
    public int getDebetableGoalNumberInListview () {

        return debetableGoalNumberInListView;

    }



    // geter for evaluate next jointly goal
    public boolean getEvaluateNextJointlyGoal () {

        return evaluateNextJointlyGoal;

    }



    // geter for border for comments
    public boolean isCommentLimitationBorderSet (String jointlyDebetable) {

        switch (jointlyDebetable) {
            case "jointlyGoals":
                if (prefs.getInt("commentJointlyGoalMaxCountComment",0) < commentLimitationBorder) { // is there a border for comments jointly goals
                    return true; // jointly goals comments are limited!
                }
                break;
            case "debetableGoals":
                if (prefs.getInt("commentDebetableGoalsOurGoalsMaxComment",0) < commentLimitationBorder) { // is there a border for comments debetable goals
                    return true; // debetable goals comments are limited!
                }
                break;
        }

        return  false; // write infinitely comments!

    }





    // setter for subtitle in OurGoals toolbar
    public void setOurGoalsToolbarSubtitle (String subtitleText, String subtitleChoose) {

        switch (subtitleChoose) {

            case "debetableNow":
                arraySubTitleText[1] = subtitleText;
                break;
            case "jointlyOld":
                arraySubTitleText[2] = subtitleText;
                break;
            case "jointlyNow":
                arraySubTitleText[0] = subtitleText;

                Log.d("OurGoals","Sub:"+arraySubTitleText[0]);

                break;
            case "jointlyNowComment":
                arraySubTitleText[3] = subtitleText;
                break;
            case "jointlyShowComment":
                arraySubTitleText[4] = subtitleText;
                break;
            case "jointlyEvaluate":
                arraySubTitleText[5] = subtitleText;
                break;
            case "debetableComment":
                arraySubTitleText[6] = subtitleText;
                break;
            case "debetableShowComment":
                arraySubTitleText[7] = subtitleText;
                break;

        }

        // first time -> set initial subtitle
        if (setSubtitleFirstTime) {
            toolbarOurGoals.setSubtitle(subtitleText);

            Log.d("OurGoals","SubOnce:"+subtitleText);


            setSubtitleFirstTime = false;
        }

    }


    // set alarmmanager for evaluation time
    void setAlarmManagerOurGoals () {

        PendingIntent pendingIntentOurGoalsEvaluate;

        // get reference to alarm manager
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // create intent for backcall to broadcast receiver
        Intent evaluateAlarmIntent = new Intent(ActivityOurGoals.this, AlarmReceiverOurGoals.class);

        // get start time and end time for evaluation
        Long startEvaluationDate = prefs.getLong("startDataJointlyGoalsEvaluationInMills", System.currentTimeMillis());
        Long endEvaluationDate = prefs.getLong("endDataJointlyGoalsEvaluationInMills", System.currentTimeMillis());

        // get evaluate pause time and active time in seconds
        evaluatePauseTime = prefs.getInt("evaluateJointlyGoalsPauseTimeInSeconds", 43200); // default value 43200 is 12 hours
        evaluateActivTime = prefs.getInt("evaluateJointlyGoalsActivTimeInSeconds", 43200); // default value 43200 is 12 hours

        Long tmpSystemTimeInMills = System.currentTimeMillis();
        int tmpEvalutePaAcTime = evaluateActivTime * 1000;
        String tmpIntentExtra = "evaluate";
        String tmpChangeDbEvaluationStatus = "set";

        // get calendar and init
        Calendar calendar = Calendar.getInstance();

        // set alarm manager when current time is between start date and end date and evaluation is enable
        if (prefs.getBoolean("showEvaluateLinkJointlyGoals", false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

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

            // update table ourGoals in db -> set or delete
            myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis()),tmpChangeDbEvaluationStatus);

            // put extras to intent -> "evaluate" or "delete"
            evaluateAlarmIntent.putExtra("evaluateState",tmpIntentExtra);

            // create call (pending intent) for alarm manager
            pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(ActivityOurGoals.this, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // set alarm
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpEvalutePaAcTime, pendingIntentOurGoalsEvaluate);

        }
        else { // delete alarm - it is out of time

            // update table ourGoals in db -> evaluation disable
            myDb.changeStatusEvaluationPossibleAllOurGoals(prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis()),"delete");
            // crealte pending intent
            pendingIntentOurGoalsEvaluate = PendingIntent.getBroadcast(ActivityOurGoals.this, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // delete alarm
            manager.cancel(pendingIntentOurGoalsEvaluate);
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




}
