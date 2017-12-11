package de.smart_efb.efbapp.smartefb;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
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
        if (txtProgress != null && progressBar != null && txtAuthorAndDate != null && timeTableDate > 0 && author.length() > 0) {

            // build date and author string
            String timeTableChangeDate = EfbHelperClass.timestampToDateFormat(timeTableDate, "dd.MM.yyyy");;
            String timeTableChangeTime = EfbHelperClass.timestampToDateFormat(timeTableDate, "HH:mm");;
            String tmpTextAuthorNameAndDate = String.format(this.getResources().getString(R.string.timeTableChangeAuthorNameWithDate), author, timeTableChangeDate, timeTableChangeTime);
            txtAuthorAndDate.setText(Html.fromHtml(tmpTextAuthorNameAndDate));

            // set textfield
            txtProgress.setText(timeTableValue + "%\nerreicht");

            // set progress status
            progressBar.setProgress(timeTableValue);
        }
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver timeTableBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with arrangements
            Boolean updateListView = false;

            // true-> update the view of activity connect book
            Boolean updateActivityView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // check intent order
                String tmpExtraTimeTable = intentExtras.getString("TimeTable","0");
                String tmpExtraTimeTableNewValue = intentExtras.getString("TimeTableNewValue","0");

                if (tmpExtraTimeTable != null && tmpExtraTimeTable.equals("1") && tmpExtraTimeTableNewValue != null && tmpExtraTimeTableNewValue.equals("1")) {

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


    private void updateActivityView () {

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);

    }


    private void resetNewValueForTimeTable () {

        // set new value for time table to false
        prefsEditor.putBoolean(ConstansClassTimeTable.namePrefsTimeTableNewValue, false);
        prefsEditor.commit();

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
