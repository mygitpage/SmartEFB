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
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 09.03.2017.
 */
public class ActivityTimeTable extends AppCompatActivity {

    // reference for the toolbar
    Toolbar toolbarMeeting;
    ActionBar actionBar;

    // shared prefs
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    private TextView txtProgress;
    private TextView txtAuthorAndDate;
    private ProgressBar progressBar;

    // reference to dialog time table information
    AlertDialog alertDialogTimeTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_timetable);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(timeTableBrodcastReceiver, filter);

        // init tiemtable
        initTimeTable();

        // set progress value
        setTimeTableValue ();

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
    }


    private void initTimeTable() {

        // init the toolbarMeeting
        toolbarMeeting = (Toolbar) findViewById(R.id.toolbarTimeTable);
        setSupportActionBar(toolbarMeeting);
        toolbarMeeting.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtProgress = (TextView) findViewById(R.id.txtProgress);
        txtAuthorAndDate = (TextView) findViewById(R.id.textAuthorNameAndDate);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // init the prefs
        prefs = getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // set new value for time table to false
        resetNewValueForTimeTable();
    }


    private void setTimeTableValue () {

        int timeTableValue = prefs.getInt(ConstansClassTimeTable.namePrefsTimeTableValue, 0); // get value for time table
        String author = prefs.getString(ConstansClassTimeTable.namePrefsTimeTableModifiedAuthor, ""); // get author name for time table
        Long timeTableDate = prefs.getLong(ConstansClassTimeTable.namePrefsTimeTableModifiedDate, 0); // get change date for time table

        // check variables
        if (txtProgress != null && progressBar != null && txtAuthorAndDate != null) {

            String timeTableChangeDate;
            String timeTableChangeTime;
            String tmpTextAuthorNameAndDate;

            if (timeTableDate > 0 && author.length() > 0) {
                // build date and author string
                timeTableChangeDate = EfbHelperClass.timestampToDateFormat(timeTableDate, "dd.MM.yyyy");
                timeTableChangeTime = EfbHelperClass.timestampToDateFormat(timeTableDate, "HH:mm");
                tmpTextAuthorNameAndDate = String.format(this.getResources().getString(R.string.timeTableChangeAuthorNameWithDate), author, timeTableChangeDate, timeTableChangeTime);
            }
            else { // no timetable value available
                timeTableValue = 0;
                tmpTextAuthorNameAndDate = this.getResources().getString(R.string.timeTableChangeNoValuetoday);
            }

            txtAuthorAndDate.setText(HtmlCompat.fromHtml(tmpTextAuthorNameAndDate, HtmlCompat.FROM_HTML_MODE_LEGACY));
            // set textfield
            txtProgress.setText(timeTableValue + "%\nerreicht");

            // set progress status
            progressBar.setProgress(timeTableValue);
        }
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver timeTableBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the view of activity connect book
            Boolean updateActivityView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // check intent order
                String tmpExtraTimeTable = intentExtras.getString("TimeTable","0");
                String tmpExtraTimeTableNewValue = intentExtras.getString("TimeTableNewValue","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings","0");
                String tmpCaseClose = intentExtras.getString("Case_close","0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = ActivityTimeTable.this.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraTimeTable != null && tmpExtraTimeTable.equals("1") && tmpExtraTimeTableNewValue != null && tmpExtraTimeTableNewValue.equals("1")) {

                    // set new value for time table to false
                    resetNewValueForTimeTable();

                    // time table has change -> refresh activity view
                    updateActivityView = true;

                }
            }

            // update the activity view
            if (updateActivityView) {
                updateActivityView();
            }
        }
    };


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonSettings = (Button) findViewById(R.id.helpTimeTable);

        // add button listener to question mark in activity settings efb (toolbar)
        tmpHelpButtonSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTimeTable.this, R.style.helpDialogStyle);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityTimeTable.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_timetable, null);

                // get string ressources
                String tmpTextCloseDialog = ActivityTimeTable.this.getResources().getString(R.string.textDialogTimeTableCloseDialog);
                String tmpTextTitleDialog = ActivityTimeTable.this.getResources().getString(R.string.textDialogTimeTableTitleDialog);

                // build the dialog
                builder.setView(dialogSettings)

                        // Add close button
                        .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogTimeTable.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogTimeTable = builder.create();

                alertDialogTimeTable.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // change background and text color of button
                        Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        // Change negative button text and background color
                        negativeButton.setTextColor(ContextCompat.getColor(ActivityTimeTable.this, R.color.white));
                        negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
                    }
                });

                // and show the dialog
                alertDialogTimeTable.show();
            }
        });
    }

    private void updateActivityView () {

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //finish();
        startActivity(intent);
    }


    private void resetNewValueForTimeTable () {

        // set new value for time table to false
        prefsEditor.putBoolean(ConstansClassTimeTable.namePrefsTimeTableNewValue, false);
        prefsEditor.apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // de-register broadcast receiver
        this.unregisterReceiver(timeTableBrodcastReceiver);
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
