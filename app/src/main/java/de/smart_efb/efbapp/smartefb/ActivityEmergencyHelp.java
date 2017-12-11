package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ich on 20.01.2017.
 */
public class ActivityEmergencyHelp extends AppCompatActivity {


    // reference for the toolbar
    Toolbar toolbarEmergencyHelp;
    ActionBar actionBar;

    // reference to dialog settings
    AlertDialog alertDialogSettings;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_emergencyhelp);

        // init meeting
        initEmergencyHelp();

        // create help dialog
        createHelpDialog();

    }


    private void initEmergencyHelp() {

        // init the toolbarEmergencyHelp
        toolbarEmergencyHelp = (Toolbar) findViewById(R.id.toolbarEmergencyHelp);
        setSupportActionBar(toolbarEmergencyHelp);
        toolbarEmergencyHelp.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
    }

    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonEmergencyHelp = (Button) findViewById(R.id.helpEmergencyHelp);


        // add button listener to question mark in activity OurGoals (toolbar)
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
                                alertDialogSettings.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogSettings = builder.create();

                // and show the dialog
                builder.show();

            }
        });

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


