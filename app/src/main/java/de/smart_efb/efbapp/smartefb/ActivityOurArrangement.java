package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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



    // reference to the DB
    DBAdapter myDb;



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
            // get command and execute it
            executeIntentCommand (intentLinkData.getQueryParameter("com"));

        } else if (intentExtras != null) {
           // get data that comes with extras
            arrangementDbIdFromLink = intentExtras.getInt("db_id",0);
            arrangementNumberInListView = intentExtras.getInt("arr_num",0);
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

            //set fragment in tab zero to comment
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

    }




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

        // set alarm manager when current time is between start date and end date
        if (System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {

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


}
