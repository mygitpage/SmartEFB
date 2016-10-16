package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    // goal db-id - for
    int goalDbIdFromLink = 0;

    // goal number in listview
    int goaltNumberInListView = 0;

    // reference to the DB
    DBAdapter myDb;

    // reference to dialog settings
    AlertDialog alertDialogSettings;








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





    // init the activity
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
