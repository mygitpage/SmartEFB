package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 20.06.16.
 */

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ich on 07.06.16.
 */
public class ActivitySettingsEfb extends AppCompatActivity {

    // defenition toolbar and actionbar
    Toolbar toolbarSettingsEfb;
    ActionBar actionBar;

    ViewPager viewPagerSettingsEfb;
    TabLayout tabLayoutSettingsEfb;

    // reference to viewpageradapter
    SettingsEfbViewPagerAdapter settingsEfbViewPagerAdapter;


    // shared prefs for the app
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // reference to the DB
    DBAdapter myDb;

    // the connecting status (0=not connected, 1=try to connect, 2=connected, 3=error)
    int connectingStatus = 0;

    // actual random number for connetion to server
    int randomNumberForConnection = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_efb);

        // init settings
        initSettingsEfb();

        viewPagerSettingsEfb = (ViewPager) findViewById(R.id.viewPagerSettingsEfb);
        settingsEfbViewPagerAdapter = new SettingsEfbViewPagerAdapter(getSupportFragmentManager(), this);
        viewPagerSettingsEfb.setAdapter(settingsEfbViewPagerAdapter);

        tabLayoutSettingsEfb = (TabLayout) findViewById(R.id.tabLayoutSettingsEfb);
        tabLayoutSettingsEfb.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayoutSettingsEfb.setupWithViewPager(viewPagerSettingsEfb);

        tabLayoutSettingsEfb.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";

                // Change the subtitle of the activity
                switch (tab.getPosition()) {
                    case 0: // title for tab zero
                        tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
                        break;
                    case 1: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleContactdetails", "string", getPackageName()));
                        break;
                    case 2: // title for tab two
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleHelpForApp", "string", getPackageName()));
                        break;

                    default:
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleConnectToServer", "string", getPackageName()));
                        break;

                }

                // set correct subtitle
                setSettingsToolbarSubtitle(tmpSubtitleText);

                viewPagerSettingsEfb.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // check for intent on start time
        // Extras from intent that holds data
        Bundle intentExtras = null;
        // intent
        Intent intent = getIntent();

        if (intent != null) { // intent set?
            // get the link data from the extra
            intentExtras = intent.getExtras();
            if (intentExtras != null && intentExtras.getString("com") != null) { // extra data set?
                if (intentExtras.getString("com").equals("show_contact")) { // execute only command show_contact (comes from activity: meeting, faq)
                    // get command and execute it
                    executeIntentCommand(intentExtras.getString("com"));
                }
            }
        }


    }


    private void initSettingsEfb() {

        // init the toolbarSettings
        toolbarSettingsEfb = (Toolbar) findViewById(R.id.toolbarSettingsEfb);
        setSupportActionBar(toolbarSettingsEfb);
        toolbarSettingsEfb.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init the prefs
        prefs = getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);

        // init prefs editor
        prefsEditor = prefs.edit();

        // get meeting status
        connectingStatus = prefs.getInt(ConstansClassSettings.namePrefsConnectingStatus, 0);

        //get random Number for connection
        randomNumberForConnection = prefs.getInt(ConstansClassSettings.namePrefsRandomNumberForConnection, 0);

        // set correct subtitle
        String tmpSubtitleText = getSubtitleForTabZero();
        setSettingsToolbarSubtitle(tmpSubtitleText);

    }


    private String getSubtitleForTabZero () {

        String tmpSubtitleText = "";

        switch(connectingStatus)

        {
            case 0:
                tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleConnectToServer", "string", getPackageName()));
                break;
            case 1:
                tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleNoNetworkAvailable", "string", getPackageName()));
                break;

            case 2:
                tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleConnectionError", "string", getPackageName()));
                break;

            case 3:
                tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleSucsessfullConnected", "string", getPackageName()));
                break;

            default:
                tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleConnectToServer", "string", getPackageName()));
                break;
        }

        return tmpSubtitleText;

    }


    // Look for new intents (with data from putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // Extras from intent that holds data
        Bundle intentExtras = null;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentExtras = intent.getExtras();

        if (intentExtras != null) {
            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"));
        }

    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command) {

        if (command.equals("show_contact")) { // Show tab 2 'ueber' -> used to show contact information from other activitys
            // set tab 2
            TabLayout.Tab tab = tabLayoutSettingsEfb.getTabAt(1);
            tab.select();

        } if (command.equals("show_no_network_try_again")) { // Show tab 0 -> no network available, try again


            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();

        } if (command.equals("show_connect_sucsessfull")) { // Show tab 0 -> connection sucsessfull, connect with server


            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();

        } if (command.equals("show_connect_error")) { // Show tab 0 -> connection error

            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();


        }




        else {



        }

    }



    // setter for subtitle in ActivitySettingsEfb toolbar
    public void setSettingsToolbarSubtitle (String subtitleText) {

        toolbarSettingsEfb.setSubtitle(subtitleText);

    }




    // getter for connecting status
    public int getConnectingStatus () {

        return connectingStatus;

    }

    // setter for connecting status
    public void setConnectionStatus (int tmpConnectionStatus) {

        connectingStatus = tmpConnectionStatus;

        prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus,tmpConnectionStatus);

        prefsEditor.commit();

    }





    // getter for random number for connection to server
    public int getRandomNumberForConnection() {


        return randomNumberForConnection;


    }

    // setter for random number for connection to server
    public void setRandomNumberForConnection(int tmpRandomNumber) {

        randomNumberForConnection = tmpRandomNumber;

        prefsEditor.putInt(ConstansClassSettings.namePrefsRandomNumberForConnection,tmpRandomNumber);

        prefsEditor.commit();

    }



    public String getLastErrorText () {

        return prefs.getString(ConstansClassSettings.namePrefsLastErrorMessages,"");

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



    // all following finctions are for fragment d (app settings) -> will be deleted!!!!

    public void onClick_showDateChooserForCurrentArrangement (View v) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new saveDateForCurrentArrangement(), mYear, mMonth, mDay);
        dialog.show();

    }



    private class saveDateForCurrentArrangement implements DatePickerDialog.OnDateSetListener {


        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            int mYear = year;
            int mMonth = monthOfYear+1;
            int mDay = dayOfMonth;
            Date date = null;

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            try {
                date = formatter.parse(mDay+"-"+mMonth+"-"+year);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            prefs = getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
            prefsEditor = prefs.edit();

            prefsEditor.putLong("currentDateOfArrangement", date.getTime());
            prefsEditor.commit();

            Toast.makeText(ActivitySettingsEfb.this, "Absprachen Stamp:" + date.getTime(), Toast.LENGTH_SHORT).show();

        }

    }





    /* Datepicker for Jointly Goals */
    public void onClick_showDateChooserForJointlyGoals (View v) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new saveDateForJointlyGoals(), mYear, mMonth, mDay);
        dialog.show();

    }



    private class saveDateForJointlyGoals implements DatePickerDialog.OnDateSetListener {


        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            int mYear = year;
            int mMonth = monthOfYear+1;
            int mDay = dayOfMonth;
            Date date = null;

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            try {
                date = formatter.parse(mDay+"-"+mMonth+"-"+year);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            prefs = getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
            prefsEditor = prefs.edit();

            prefsEditor.putLong("currentDateOfJointlyGoals", date.getTime());
            prefsEditor.commit();

            Toast.makeText(ActivitySettingsEfb.this, "Gemeinsame Ziele Stamp:" + date.getTime(), Toast.LENGTH_SHORT).show();

        }

    }






    public void onClick_showDateChooserForMeetingSuggestions (View v) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new saveDateForMeetingSuggestions(), mYear, mMonth, mDay);
        dialog.show();

    }





    public void onClick_readXMLConfigurationFile (View v) {


        /*
        // read configuration xml file ------- TEST
        EfbXmlParser test = new EfbXmlParser(getApplicationContext());
        try {
            test.parseXmlInput();
        }
        catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        */

    }




    private class saveDateForMeetingSuggestions implements DatePickerDialog.OnDateSetListener {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            int mYear = year;
            int mMonth = monthOfYear+1;
            int mDay = dayOfMonth;
            Date date = null;

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            try {
                date = formatter.parse(mDay+"-"+mMonth+"-"+year);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            myDb.insertNewMeetingDateAndTime(date.getTime(),"Werder (Havel)", true, 0);

            Toast.makeText(ActivitySettingsEfb.this, "Terminvorschlag Timestamp " + date.getTime(), Toast.LENGTH_SHORT).show();

        }

    }








}







