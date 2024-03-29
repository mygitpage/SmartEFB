package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 20.06.16.
 */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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

    // the connecting status (0=not connected, 1=try to connect, 2=connected, 3=error)
    int connectingStatus = 0;

    // actual random number for connetion to server
    int randomNumberForConnection = 0;

    // reference to dialog settings
    AlertDialog alertDialogSettings;


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
                    case 3: // title for tab three
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("settingsSubtitleAppSettingsChange", "string", getPackageName()));
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
                if (intentExtras.getString("com").equals("show_contact") || intentExtras.getString("com").equals("data_protection")) { // execute only command show_contact (comes from activity: settings) or data_protection (comes from settings)
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

        // get meeting status (0=connect to server; 1=no network available; 2=connection error; 3=connected)
        connectingStatus = prefs.getInt(ConstansClassSettings.namePrefsConnectingStatus, 0);

        //get random Number for connection
        randomNumberForConnection = prefs.getInt(ConstansClassSettings.namePrefsRandomNumberForConnection, 0);

        // set correct subtitle
        String tmpSubtitleText = getSubtitleForTabZero();
        setSettingsToolbarSubtitle(tmpSubtitleText);

        // create help dialog
        createHelpDialog();
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
            String tmpComman = intentExtras.getString("com");
            if (tmpComman == null) {tmpComman = "";}
            executeIntentCommand (tmpComman);
        }
    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command) {

        if (command.equals("show_contact")) { // Show tab 2 'ueber' -> used to show contact information from other activitys
            // set tab 2
            TabLayout.Tab tab = tabLayoutSettingsEfb.getTabAt(1);
            tab.select();

        } else if (command.equals("data_protection")) { // Show tab 3 'hilfe' -> used to show data protection information from other activitys
            // set tab 3
            TabLayout.Tab tab = tabLayoutSettingsEfb.getTabAt(2);
            tab.select();

        } else if (command.equals("show_no_network_try_again")) { // Show tab 0 -> no network available, try again

            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();

        } else if (command.equals("show_involved_person")) { // Show tab 0 -> involved person

            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();

        }
        else if (command.equals("show_connect_sucsessfull")) { // Show tab 0 -> connection sucsessfull, connect with server

            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();

        } else if (command.equals("show_connect_error")) { // Show tab 0 -> connection error

            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();

        } else if (command.equals("generate_new_connection_number")) { // Show tab 0 -> new connection number (Pin-Number new)

            // set correct subtitle
            String tmpSubtitleText = ActivitySettingsEfb.this.getSubtitleForTabZero();
            setSettingsToolbarSubtitle(tmpSubtitleText);

            // generate new random number and set
            int tmpNumber = EfbHelperClass.randomNumber(ConstansClassSettings.randomNumberForConnectionMin, ConstansClassSettings.randomNumberForConnectionMax);
            setRandomNumberForConnection(tmpNumber);

            // notify view pager adapter that data change
            settingsEfbViewPagerAdapter.notifyDataSetChanged();

        } else if (command.equals("open_link_dataprivacy_external")) {

            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.smart-efb.de/datenschutz.htm"));
            try {
                startActivity(webIntent);
            }
            catch (ActivityNotFoundException ex) {
                String textCaseClose = ActivitySettingsEfb.this.getString(R.string.toastNoLinkGoalFound);
                Toast toast = Toast.makeText(ActivitySettingsEfb.this, textCaseClose, Toast.LENGTH_LONG);
                toast.show();
            }

        }

    }


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonSettings = (Button) findViewById(R.id.helpSettings);

        // add button listener to question mark in activity settings efb (toolbar)
        tmpHelpButtonSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySettingsEfb.this, R.style.helpDialogStyle);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivitySettingsEfb.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_settings, null);

                // get string ressources
                String tmpTextCloseDialog = ActivitySettingsEfb.this.getResources().getString(R.string.textDialogSettingsEfbCloseDialog);
                String tmpTextTitleDialog = ActivitySettingsEfb.this.getResources().getString(R.string.textDialogSettingsEfbTitleDialog);

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

                alertDialogSettings.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // change background and text color of button
                        Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        // Change negative button text and background color
                        negativeButton.setTextColor(ContextCompat.getColor(ActivitySettingsEfb.this, R.color.white));
                        negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
                    }
                });

                // and show the dialog
                alertDialogSettings.show();
            }
        });
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

        prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus,tmpConnectionStatus); // 0=connect to server; 1=no network available; 2=connection error; 3=connected
        prefsEditor.apply();
    }


    // getter for random number for connection to server
    public int getRandomNumberForConnection() {

        return randomNumberForConnection;
    }

    // setter for random number for connection to server
    public void setRandomNumberForConnection(int tmpRandomNumber) {

        randomNumberForConnection = tmpRandomNumber;

        prefsEditor.putInt(ConstansClassSettings.namePrefsRandomNumberForConnection,tmpRandomNumber);
        prefsEditor.apply();
    }


    public String getLastErrorText () {

        String tmp_errortext = prefs.getString(ConstansClassSettings.namePrefsLastErrorMessages,"");

        // check if erorrtext is set
        if (tmp_errortext.length() == 0 ) {tmp_errortext = "Leider kein Fehlertext übermittelt!";}

        return tmp_errortext;
    }


    public void deleteLastErrorText () {

        prefsEditor.putString(ConstansClassSettings.namePrefsLastErrorMessages, "");
        prefsEditor.apply();
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







