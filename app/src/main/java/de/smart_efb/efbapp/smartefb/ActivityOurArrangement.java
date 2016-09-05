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

    // Max number of comments <-> Over this number you can write infinitely comments
    final int commentLimitationBorder = 1000;


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

    // viewpager and tablayout for the view
    ViewPager viewPagerOurArrangement;
    TabLayout tabLayoutOurArrangement;

    // viewpager adapter
    OurArrangementViewPagerAdapter ourArrangementViewPagerAdapter;


    // Strings for subtitle ("Aktuelle vom...", "Älter als...", "Absprache kommentieren", "Kommentare zeigen", "Absprache bewerten" )
    String currentArrangementSubtitleText = "";
    String olderArrangementSubtitleText = "";
    String commentArrangementSubtitleText = "";
    String showCommentArrangementSubtitleText = "";
    String evaluateArrangementSubtitleText = "";

    // what to show in tab zero (like show_comment_for_arrangement, comment_an_arrangement, show_arrangement_now)
    String showCommandFragmentTabZero = "";

    // arrangement db-id - for comment or show comment
    int arrangementDbIdFromLink = 0;
    //arrangement number in listview
    int arrangementNumberInListView = 0;
    // evaluate next arrangement true -> yes, there is a next arrangement to evaluate; false -> there is nothing more
    boolean evaluateNextArrangement = false;


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


        // init listener for tab selected
        tabLayoutOurArrangement.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // Change the subtitle of the activity
                switch (tab.getPosition()) {

                    case 0:
                        switch (showCommandFragmentTabZero) {

                            case "show_arrangement_now":
                                toolbar.setSubtitle(currentArrangementSubtitleText);
                                break;
                            case "comment_an_arrangement":
                                toolbar.setSubtitle(commentArrangementSubtitleText);
                                break;
                            case "show_comment_for_arrangement":
                                toolbar.setSubtitle(showCommentArrangementSubtitleText);
                                break;
                            case "evaluate_an_arrangement":
                                toolbar.setSubtitle(evaluateArrangementSubtitleText);
                                break;
                        }
                        break;

                    case 1:

                        toolbar.setSubtitle(olderArrangementSubtitleText);

                        break;

                    default:
                        toolbar.setSubtitle(currentArrangementSubtitleText);
                        break;


                }

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

        // Uri from intent that holds data
        Uri intentLinkData = null;

        // Extras from intent that holds data
        Bundle intentExtras = null;

        arrangementDbIdFromLink = 0;
        arrangementNumberInListView = 0;
        evaluateNextArrangement = false;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentLinkData = intent.getData();
        intentExtras = intent.getExtras();

        // is there URI Data?
        if (intentLinkData != null) {
            // get data that comes with intent-link
            arrangementDbIdFromLink = Integer.parseInt(intentLinkData.getQueryParameter("db_id")); // arrangement DB-ID
            arrangementNumberInListView = Integer.parseInt(intentLinkData.getQueryParameter("arr_num"));
            evaluateNextArrangement = Boolean.parseBoolean(intentLinkData.getQueryParameter("eval_next"));
            // get command and execute it
            executeIntentCommand (intentLinkData.getQueryParameter("com"));

        } else if (intentExtras != null) {
           // get data that comes with extras
            arrangementDbIdFromLink = intentExtras.getInt("db_id",0);
            arrangementNumberInListView = intentExtras.getInt("arr_num",0);
            evaluateNextArrangement = intentExtras.getBoolean("eval_next");
            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"));
        }

    }



    public void executeIntentCommand (String command) {

        String tmpTabTitle = "";

        if (command.equals("show_comment_for_arrangement")) { // Show fragment all comments for arrangement


            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("show_comment_for_arrangement");

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(showCommentArrangementSubtitleText);

            // set correct tab zero titel
            try {
                tmpTabTitle = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1b", "string", getPackageName()));
                tabLayoutOurArrangement.getTabAt(0).setText(tmpTabTitle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // set command show variable
            showCommandFragmentTabZero = "show_comment_for_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();


        } else if (command.equals("comment_an_arrangement")) { // Show fragment comment arrangement

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("comment_an_arrangement");

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(commentArrangementSubtitleText);

            // set correct tab zero titel
            tabLayoutOurArrangement.getTabAt(0).setText(getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1a", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabZero = "comment_an_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();


        } else if (command.equals("evaluate_an_arrangement")) { // Show evaluate a arrangement

            //set fragment in tab zero to evaluate
            OurArrangementViewPagerAdapter.setFragmentTabZero("evaluate_an_arrangement");

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(evaluateArrangementSubtitleText);

            // set correct tab zero titel
            tabLayoutOurArrangement.getTabAt(0).setText(getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1c", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabZero = "evaluate_an_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

        } else { // Show fragment arrangement now

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("show_arrangement_now");

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(currentArrangementSubtitleText);

            // set correct tab zero titel
            tabLayoutOurArrangement.getTabAt(0).setText(getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1", "string", getPackageName())));

            // set command show variable
            showCommandFragmentTabZero = "show_arrangement_now";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

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
        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong("currentDateOfArrangement", System.currentTimeMillis());

        // init show on tab zero arrangemet now
        showCommandFragmentTabZero = "show_arrangement_now";

        // set variables for subtitle text string
        currentArrangementSubtitleText = getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
        olderArrangementSubtitleText = getResources().getString(getResources().getIdentifier("olderArrangementDateFrom", "string", getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
        commentArrangementSubtitleText = getResources().getString(getResources().getIdentifier("commentArrangementsubtitle", "string", getPackageName()));
        showCommentArrangementSubtitleText = getResources().getString(getResources().getIdentifier("showCommentArrangementsubtitle", "string", getPackageName()));
        evaluateArrangementSubtitleText = getResources().getString(getResources().getIdentifier("evaluateArrangementsubtitle", "string", getPackageName()));
        // init subtitle first time
        toolbar.setSubtitle(currentArrangementSubtitleText);

        createHelpDialog();

    }



    void createHelpDialog () {

        Button tmpHelpButtonOurArrangement = (Button) findViewById(R.id.helpOurArrangementNow);


        // add button listener
        tmpHelpButtonOurArrangement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView tmpdialogTextView;
                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOurArrangement.this);
                // Get the layout inflater


                dialogInflater = (LayoutInflater) ActivityOurArrangement.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                //LayoutInflater inflater = ActivityOurArrangement.this.getLayoutInflater();
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_our_arrangement, null);



                // show intro for settings
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsIntro);
                tmpdialogTextView.setText(ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsIntro));

                // show the settings for evaluation (like evaluation period, on/off-status, ...)
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsEvaluate);
                if (prefs.getBoolean("showArrangementEvaluate", false)) {

                    String tmpTxtEvaluate = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluateEnable);
                    String tmpTxtEvaluate1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluatePeriod);
                    String tmpCompleteTxtEvaluate1String = String.format(tmpTxtEvaluate1, EfbHelperClass.timestampToDateFormat(prefs.getLong("startDataEvaluationInMills", System.currentTimeMillis()), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(prefs.getLong("startDataEvaluationInMills", System.currentTimeMillis()), "kk.mm"), EfbHelperClass.timestampToDateFormat(prefs.getLong("endDataEvaluationInMills", System.currentTimeMillis()), "dd.MM.yyyy"),EfbHelperClass.timestampToDateFormat(prefs.getLong("endDataEvaluationInMills", System.currentTimeMillis()), "kk.mm"));
                    tmpdialogTextView.setText(tmpTxtEvaluate + " " + tmpCompleteTxtEvaluate1String);
                }
                else {
                    String tmpTxtEvaluate = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluateDisable);
                    tmpdialogTextView.setText(tmpTxtEvaluate);
                }



                // show the settings for comment (like on/off-status, count comment...)
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsComment);
                String tmpTxtComment, tmpTxtComment1, tmpTxtComment2, tmpTxtCommentSum;

                if (prefs.getBoolean("showArrangementComment", false)) {

                    tmpTxtComment = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentEnable);

                    if (prefs.getInt("commentOurArrangementMaxComment",0) < commentLimitationBorder) { // write infinitely comments?

                        if (prefs.getInt("commentOurArrangementMaxComment",0) == 1) {
                            tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountSingular);

                        }
                        else {
                            tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountPlural);
                            tmpTxtComment1 = String.format(tmpTxtComment1, prefs.getInt("commentOurArrangementMaxComment",0));
                        }


                    }
                    else {
                        tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountInfinitely);
                    }


                    if (prefs.getInt("commentOurArrangementCountComment",0) < prefs.getInt("commentOurArrangementMaxComment",0)) {


                        switch (prefs.getInt("commentOurArrangementCountComment", 0)) {
                            case 0:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCountCommentZero);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong("commentOurArrangementTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"));






                                break;
                            case 1:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberSingular);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong("commentOurArrangementTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"));

                                break;
                            default:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberPlural);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong("commentOurArrangementTimeSinceInMills", System.currentTimeMillis()), "dd.MM.yyyy"), prefs.getInt("commentOurArrangementCountComment",0));
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











                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(dialogSettings)

                        /*
                        // Add action buttons
                        .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // sign in the user ...
                            }
                        })
                        */
                        .setNegativeButton("Schließen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogSettings.cancel();
                            }
                        })

                        .setTitle("Einstellungen");


                alertDialogSettings = builder.create();

                builder.show();


            }
        });









    }



    // set alarmmaneger for evaluation time
    void setAlarmManagerOurArrangement () {

        PendingIntent pendingIntentOurArrangementEvaluate;

        // get reference to alarm manager
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // create intent for backcall to broadcast receiver
        Intent evaluateAlarmIntent = new Intent(ActivityOurArrangement.this, AlarmReceiverOurArrangement.class);

        // get evaluate pause time and active time
        evaluatePauseTime = prefs.getInt("evaluatePauseTimeInSeconds", 43200); // default value 43200 is 12 hours
        evaluateActivTime = prefs.getInt("evaluateActivTimeInSeconds", 43200); // default value 43200 is 12 hours

        // get start time and end time for evaluation
        Long startEvaluationDate = prefs.getLong("startDataEvaluationInMills", System.currentTimeMillis());
        Long endEvaluationDate = prefs.getLong("endDataEvaluationInMills", System.currentTimeMillis());

        Long tmpSystemTimeInMills = System.currentTimeMillis();
        int tmpEvalutePaAcTime = evaluateActivTime * 1000;
        String tmpIntentExtra = "evaluate";
        String tmpChangeDbEvaluationStatus = "set";

        // get calendar and init
        Calendar calendar = Calendar.getInstance();

        // set alarm manager when current time is between start date and end date and evaluation is enable
        if (prefs.getBoolean("showArrangementEvaluate", false) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

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
            myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()),tmpChangeDbEvaluationStatus);

            // put extras to intent -> "evaluate" or "delete"
            evaluateAlarmIntent.putExtra("evaluateState",tmpIntentExtra);

            // create call (pending intent) for alarm manager
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(ActivityOurArrangement.this, 0, evaluateAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // set alarm
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpEvalutePaAcTime, pendingIntentOurArrangementEvaluate);



            Log.d("End A - Alarm:"," Alarm SET!!!!!!! ");
        }
        else { // delete alarm - it is out of time

            // update table ourArrangement in db -> evaluation disable
            myDb.changeStatusEvaluationPossibleAllOurArrangement(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()),"delete");
            // crealte pending intent
            pendingIntentOurArrangementEvaluate = PendingIntent.getBroadcast(ActivityOurArrangement.this, 0, evaluateAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // delete alarm
            manager.cancel(pendingIntentOurArrangementEvaluate);


            Log.d("End A - Alarm:"," CANCELED!!!!!!! ");

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



    // geter for DB-Id of arrangement
    public int getArrangementDbIdFromLink () {

        return arrangementDbIdFromLink;

    }


    // geter for arrangement number in listview
    public int getArrangementNumberInListview () {

        return arrangementNumberInListView;

    }



    // geter for evaluate next arrangement
    public boolean getEvaluateNextArrangement () {

        return evaluateNextArrangement;

    }


}
