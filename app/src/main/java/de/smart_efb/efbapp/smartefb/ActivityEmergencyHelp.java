package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 20.01.2017.
 */
public class ActivityEmergencyHelp extends AppCompatActivity {


    // reference for the toolbar
    Toolbar toolbarEmergencyHelp;
    ActionBar actionBar;

    // reference to dialog settings
    AlertDialog alertDialogEmergencyHelp;

    // for prefs
    private SharedPreferences prefs;

    // view
    View emergencyHelpView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_emergencyhelp);

        emergencyHelpView = this.findViewById(android.R.id.content);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(emergencyHelpBrodcastReceiver, filter);

        // init meeting
        initEmergencyHelp();

        // create help dialog
        createHelpDialog();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(getApplicationContext(), ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(getApplicationContext(), startServiceIntent);
        }

        // show some view elements
        displaySomeViewElements();
    }


    private void initEmergencyHelp() {

        // init the toolbarEmergencyHelp
        toolbarEmergencyHelp = (Toolbar) findViewById(R.id.toolbarEmergencyHelp);
        setSupportActionBar(toolbarEmergencyHelp);
        toolbarEmergencyHelp.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // open sharedPrefs
        prefs =  getApplicationContext().getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, getApplicationContext().MODE_PRIVATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // de-register broadcast receiver
        this.unregisterReceiver(emergencyHelpBrodcastReceiver);
    }


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonEmergencyHelp = (Button) findViewById(R.id.helpEmergencyHelp);

        // add button listener to question mark in activity emergency help (toolbar)
        tmpHelpButtonEmergencyHelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEmergencyHelp.this);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityEmergencyHelp.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_emergencyhelp, null);

                // get string ressources
                String tmpTextCloseDialog = ActivityEmergencyHelp.this.getResources().getString(R.string.textDialogEmergencyHelpCloseDialog);
                String tmpTextTitleDialog = ActivityEmergencyHelp.this.getResources().getString(R.string.textDialogEmergencyHelpTitleDialog);

                // build the dialog
                builder.setView(dialogSettings)

                        // Add close button
                        .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogEmergencyHelp.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogEmergencyHelp = builder.create();

                // and show the dialog
                builder.show();
            }
        });
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver emergencyHelpBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                // case is close
                String tmpSettings = intentExtras.getString("Settings","0");
                String tmpCaseClose = intentExtras.getString("Case_close","0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = ActivityEmergencyHelp.this.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
            }
        }
    };


    private void displaySomeViewElements () {

        String tmpText = "";
        TextView textViewOnlineHint;
        Spanned linkToOnlineConsultation;

        // get link and text for efb online
        tmpText = ActivityEmergencyHelp.this.getString(R.string.emergencyHelpEmergencyEFBCallEmail);
        textViewOnlineHint = (TextView) emergencyHelpView.findViewById(R.id.emergencyHelpEmergencyEFBCallEmail);
        linkToOnlineConsultation = Html.fromHtml(tmpText);
        textViewOnlineHint.setText(linkToOnlineConsultation);
        textViewOnlineHint.setMovementMethod(LinkMovementMethod.getInstance());


        // get link and text for care our souls mail
        tmpText = ActivityEmergencyHelp.this.getString(R.string.emergencyHelpEmergencyCareOfSoulsEmail);
        textViewOnlineHint = (TextView) emergencyHelpView.findViewById(R.id.emergencyHelpEmergencyCareOfSoulsEmail);
        linkToOnlineConsultation = Html.fromHtml(tmpText);
        textViewOnlineHint.setText(linkToOnlineConsultation);
        textViewOnlineHint.setMovementMethod(LinkMovementMethod.getInstance());




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