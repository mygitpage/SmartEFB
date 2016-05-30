package de.smart_efb.efbapp.smartefb;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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
    Toolbar toolbar;
    ActionBar actionBar;

    // the current date of arrangement -> the other are history
    long currentDateOfArrangement;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_arrangement);

        // init our arragement
        initOurArrangement();

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
        getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", getPackageName()));


        toolbar.setSubtitle(getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy"));


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
