package de.smart_efb.efbapp.smartefb;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ich on 15.08.16.
 */
public class ActivityMeeting extends AppCompatActivity {


    // Number of different subtitles
    final int numberOfDifferentSubtitle = 8;

    // reference for the toolbar
    Toolbar toolbarMeeting;
    ActionBar actionBar;

    // reference to fragement manager
    FragmentManager fragmentManagerActivityMeeting;

    // reference to meeting fragments
    MeetingFragmentMeetingNow referenceFragmentMeetingNow;
    MeetingFragmentMeetingMake referenceFragmentMeetingMake;


    // Strings for subtitle ()
    String [] arraySubTitleText = new String[numberOfDifferentSubtitle];





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_meeting);

        // init meeting
        initMeeting();

    }


    private void initMeeting() {

        // init the toolbarMeeting
        toolbarMeeting = (Toolbar) findViewById(R.id.toolbarMeeting);
        setSupportActionBar(toolbarMeeting);
        toolbarMeeting.setTitleTextColor(Color.WHITE);
        //toolbarMeeting.setSubtitle("Bisher kein Termin vereinbart");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init array for subtitles
        for (int t=0; t<numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
        }

        // init reference fragment manager
        fragmentManagerActivityMeeting = getSupportFragmentManager();


        // init reference fragements
        referenceFragmentMeetingNow = new MeetingFragmentMeetingNow();
        referenceFragmentMeetingMake = new MeetingFragmentMeetingMake();


        // init start fragment MeetingFragmentMeetingNow
        FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, referenceFragmentMeetingNow);
        fragmentTransaction.commit();








    }







    // Look for new intents (with data from URI or putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // Uri from intent that holds data
        Uri intentLinkData = null;

        // Extras from intent that holds data
        Bundle intentExtras = null;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        /*intentLinkData = intent.getData();*/
        intentExtras = intent.getExtras();

        //int tmpDbId = 0;
        //int tmpNumberinListView = 0;
        //Boolean tmpEvalNext = false;

        if (intentExtras != null) {
            // get data that comes with extras
            //tmpDbId = intentExtras.getInt("db_id",0);
            //tmpNumberinListView = intentExtras.getInt("arr_num",0);
            //tmpEvalNext = intentExtras.getBoolean("eval_next");

            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"));
        }

    }




    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command) {

        if (command.equals("change_meeting")) { // Show fragment for changing meeting date and time



            Log.d("Activity Meeting","change_meeting");

            // set command show variable
            //showCommandFragmentTabZero = "show_comment_for_jointly_goal";



            // set correct subtitle in toolbar in tab zero
            //toolbarOurGoals.setSubtitle(arraySubTitleText[4]);


        } else if (command.equals("find_meeting")) { // Show fragment for finding meeting date and time


            Log.d("Activity Meeting","find_meeting");

            // set correct subtitle in toolbar in tab zero
            //toolbarOurGoals.setSubtitle(arraySubTitleText[3]);


        } else if (command.equals("make_meeting")) { // Show fragment for make first meeting date and time (make_meeting)


            Log.d("Activity Meeting","make_meeting");

            // replace fragment MeetingFragmentMeetingMake
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingMake);
            fragmentTransaction.commit();

            // set correct subtitle in toolbar in tab zero
            //toolbarOurGoals.setSubtitle(arraySubTitleText[3]);


        }
        else {


            Log.d("Activity Meeting","now_meeting");


            // replace fragment MeetingFragmentMeetingMake
            FragmentTransaction fragmentTransaction = fragmentManagerActivityMeeting.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, referenceFragmentMeetingNow);
            fragmentTransaction.commit();



            // set correct subtitle in toolbar in tab zero
            //toolbarOurGoals.setSubtitle(arraySubTitleText[5]);


        }

    }



    // setter for subtitle in ActivityMeeting toolbar
    public void setMeetingToolbarSubtitle (String subtitleText, String subtitleChoose) {

        switch (subtitleChoose) {

            case "noFirstMeeting":
                arraySubTitleText[0] = subtitleText;
                break;
            case "makeFirstMeeting":
                arraySubTitleText[1] = subtitleText;
                break;


            case "firstMeetingRequested":
                arraySubTitleText[2] = subtitleText;
                break;
            case "firstMeetingConfirmed":
                arraySubTitleText[3] = subtitleText;
                break;


        }


        toolbarMeeting.setSubtitle(subtitleText);

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
