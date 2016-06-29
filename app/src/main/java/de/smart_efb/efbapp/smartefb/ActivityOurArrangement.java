package de.smart_efb.efbapp.smartefb;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.Toast;

/**
 * Created by ich on 25.05.16.
 */
public class ActivityOurArrangement extends AppCompatActivity {



    // reference to the DB
    DBAdapter myDb;

    // reference cursorAdapter for the listview
    OurArrangementCursorAdapter dataAdapter;

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


    // Strings for subtitle ("Aktuelle vom...", "Älter als...")
    String currentArrangementSubtitleText = "";
    String olderArrangementSubtitleText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_arrangement);

        // init our arragement
        initOurArrangement();



        // Aufruf eines Fragmentes durch ein anderes
        // http://stackoverflow.com/questions/14627829/call-fragment-from-fragment
        //
        // Link: Austausch ein Fragment gegen ein anderes mit ViewPager
        // http://stackoverflow.com/questions/18588944/replace-one-fragment-with-another-in-viewpager

        // Fragmentaufruf durch Intent
        // http://stackoverflow.com/questions/9831728/start-a-fragment-via-intent-within-a-fragment




        viewPagerOurArrangement = (ViewPager) findViewById(R.id.viewPagerOurArrangement);
        OurArrangementViewPagerAdapter ourArrangementViewPagerAdapter = new OurArrangementViewPagerAdapter(getSupportFragmentManager(), this);
        viewPagerOurArrangement.setAdapter(ourArrangementViewPagerAdapter);

        tabLayoutOurArrangement = (TabLayout) findViewById(R.id.tabLayoutOurArrangement);
        tabLayoutOurArrangement.setTabGravity(TabLayout.GRAVITY_FILL);


        tabLayoutOurArrangement.setupWithViewPager(viewPagerOurArrangement);

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

                    case 2: // Change to subtitle for Fragment Comment - when needed
                        toolbar.setSubtitle(currentArrangementSubtitleText);
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








        /*
        // show the arrangements
        displayArrangementSet();


        // produces test date and write to the db
        final EditText txtInputArrangement = (EditText) findViewById(R.id.arrangementText);
        Button buttonSendArrangement = (Button) findViewById(R.id.arrangementTextSend);
        // onClick send button
        buttonSendArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long newID = myDb.insertRowOurArrangement(txtInputArrangement.getText().toString(), "testuser", currentDateOfArrangement);

                txtInputArrangement.setText("");

                displayArrangementSet();

                Toast.makeText(ActivityOurArrangement.this, " Vereinbarung eingetragen ", Toast.LENGTH_SHORT).show();

            }
        });
        // end test input
        */



    }



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



    public void displayArrangementSet () {

        Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentDateOfArrangement);

        // find the listview
        ListView listView = (ListView) findViewById(R.id.listOurArrangement);


        // new dataadapter
        dataAdapter = new OurArrangementCursorAdapter(
                ActivityOurArrangement.this,
                cursor,
                0);

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

    }




}
