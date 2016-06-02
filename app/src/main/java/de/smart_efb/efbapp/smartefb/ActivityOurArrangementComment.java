package de.smart_efb.efbapp.smartefb;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ich on 02.06.16.
 */
public class ActivityOurArrangementComment extends AppCompatActivity {



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
        setContentView(R.layout.activity_efb_our_arrangement_comment);

        // init our arragement
        initOurArrangementComment();


    }


    private void initOurArrangementComment() {

        // init the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarOurArrangementComment);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);

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

