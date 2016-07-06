package de.smart_efb.efbapp.smartefb;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 25.05.16.
 */
public class ActivityOurArrangement extends AppCompatActivity {



    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

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


    // Strings for subtitle ("Aktuelle vom...", "Älter als...", "Absprache kommentieren")
    String currentArrangementSubtitleText = "";
    String olderArrangementSubtitleText = "";
    String commentArrangementSubtitleText = "";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_arrangement);

        // init our arragement
        initOurArrangement();

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
                        toolbar.setSubtitle(currentArrangementSubtitleText);
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


    // Look for new intents (like link to comment or show comments)
    @Override
    protected void onNewIntent(Intent intent) {

        // uri to handle the data from the link
        Uri commentLinkData;
        // id of the given arrangement
        //int commentArrangementIdFromLink = 0;
        // link command
        String commandFromLink = "";


        super.onNewIntent(intent);

        // get the link data
        commentLinkData = intent.getData();

        // commentLinkData correct?
        if (commentLinkData != null) {

            //commentArrangementIdFromLink = Integer.parseInt(commentLinkData.getQueryParameter("id"));

            commandFromLink = commentLinkData.getQueryParameter("com");
            executeIntentCommand (commandFromLink);


        }

    }


    private void executeIntentCommand (String command) {

        if (command.equals("show_comment")) {


            //OurArrangementViewPagerAdapter.setFragmentTabZero("show_comment_for_arrangement");
            //ourArrangementViewPagerAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Kommentare zeigen", Toast.LENGTH_SHORT).show();


        } else if (command.equals("comment")) {

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("comment_an_arrangement");
            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(commentArrangementSubtitleText);
            // set correct tab zero titel


            // call notofy data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();
            //Toast.makeText(this, "Kommentieren für", Toast.LENGTH_SHORT).show();


        } else {



            Toast.makeText(this, "Keinen Befehl erkannt mit ID", Toast.LENGTH_SHORT).show();
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

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong("currentDateOfArrangement", System.currentTimeMillis());
        // and set undertitle of activity
        //getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", getPackageName()));


        // set current and older subtitle text string
        currentArrangementSubtitleText = getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
        olderArrangementSubtitleText = getResources().getString(getResources().getIdentifier("olderArrangementDateFrom", "string", getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
        commentArrangementSubtitleText = getResources().getString(getResources().getIdentifier("commentArrangementsubtitle", "string", getPackageName()));

        // init subtitle first time
        toolbar.setSubtitle(currentArrangementSubtitleText);


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
