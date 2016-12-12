package de.smart_efb.efbapp.smartefb;


import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ich on 15.08.16.
 */
public class ActivityMeeting extends AppCompatActivity {


    // reference for the toolbar
    Toolbar toolbar;
    ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_meeting);

        // init meeting
        initMeeting();



        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        MeetingFragmentMeetingNow f1 = new MeetingFragmentMeetingNow();
        fragmentTransaction.add(R.id.fragment_container, f1);

        fragmentTransaction.commit();



        // for example look
        // http://android.tutorialhorizon.com/fragments-add-fragments-dynamically-at-runtime/




    }


    private void initMeeting() {

        // init the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarMeeting);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("Bisher kein Termin vereinbart");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
