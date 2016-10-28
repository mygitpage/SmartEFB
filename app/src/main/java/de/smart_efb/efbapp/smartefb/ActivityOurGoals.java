package de.smart_efb.efbapp.smartefb;

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

/**
 * Created by ich on 07.06.16.
 */
public class ActivityOurGoals extends AppCompatActivity {


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
    Boolean evaluateNextGoal = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_goals);

        // init our goals
        initOurGoals();

        // init alarm manager
        //setAlarmManagerOurGoal ();

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
        evaluateNextGoal = false;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        /*intentLinkData = intent.getData();*/
        intentExtras = intent.getExtras();

        int tmpDbId = 0;
        int tmpNumberinListView = 0;
        Boolean tmpEvalNext = false;

        // is there URI Data?
        /*
        if (intentLinkData != null) {
            // get data that comes with intent-link
            tmpDbId = Integer.parseInt(intentLinkData.getQueryParameter("db_id")); // arrangement DB-ID
            tmpNumberinListView = Integer.parseInt(intentLinkData.getQueryParameter("arr_num"));
            tmpEvalNext = Boolean.parseBoolean(intentLinkData.getQueryParameter("eval_next"));
            // get command and execute it
            executeIntentCommand (intentLinkData.getQueryParameter("com"), tmpDbId, tmpNumberinListView, tmpEvalNext);

        } else*/
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
            evaluateNextGoal = tmpEvalNext;

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
            evaluateNextGoal = tmpEvalNext;

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
            evaluateNextGoal = tmpEvalNext;

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

            //set fragment in tab one to show sketch arrangement
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

            //set fragment in tab one to show comment sketch arrangement
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
            evaluateNextGoal = tmpEvalNext;

            //set fragment in tab zero to comment
            OurGoalsViewPagerAdapter.setFragmentTabZero("show_jointly_goals_now");

            // set correct tab zero titel
            tabLayoutOurGoals.getTabAt(0).setText(getResources().getString(getResources().getIdentifier("ourGoalsTabTitle_1", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabZero = "show_jointly_goals_now";

            // call notify data change
            ourGoalsViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
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

        //get current date of arrangement
        currentDateOfJointlyGoals = prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis());
        //get date of sketch arrangement
        getCurrentDateOfDebetableGoals = prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis());

        // init show on tab zero arrangemet now
        showCommandFragmentTabZero = "show_jointly_goals_now";
        // init show on tab one sketch arrangemet
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

        Button tmpHelpButtonOurArrangement = (Button) findViewById(R.id.helpOurGoalsNow);


        // add button listener to question mark in activity OurArrangement (toolbar)
        tmpHelpButtonOurArrangement.setOnClickListener(new View.OnClickListener() {

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
    public int getdebetableGoalDbIdFromLink () {

        return debetableGoalDbIdFromLink;

    }


    // getter for jointly goal number in listview
    public int getJointlyGoalNumberInListview () {

        return jointlyGoalNumberInListView;

    }

    // getter for sketch arrangement number in listview
    public int getDebetableGoalNumberInListview () {

        return debetableGoalNumberInListView;

    }


    /*
    // geter for evaluate next jointly goal
    public boolean getEvaluateNextJointlyGoal () {

        return evaluateNextArrangement;

    }
    */


    // geter for border for comments
    public boolean isCommentLimitationBorderSet (String jointlyDebetable) {

        switch (jointlyDebetable) {
            case "jointlyGoals":
                if (prefs.getInt("commentJointlyGoalMaxCountComment",0) < commentLimitationBorder) { // is there a border for comments jointly goals
                    return true; // comments are limited!
                }
                break;
            case "debetableGoals":

                // TODO Konstante an Max Count DEBETABLE GOALS anpassen
                if (prefs.getInt("commentDebetableGoalMaxCountComment",0) < commentLimitationBorder) { // is there a border for comments debetable goals
                    return true; // sketch comments are limited!
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
            setSubtitleFirstTime = false;
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
