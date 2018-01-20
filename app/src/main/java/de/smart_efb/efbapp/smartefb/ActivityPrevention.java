package de.smart_efb.efbapp.smartefb;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 13.03.2017.
 */
public class ActivityPrevention extends AppCompatActivity {


    // reference for the toolbar
    Toolbar toolbarPrevention;
    ActionBar actionBar;

    // reference to dialog settings
    AlertDialog alertDialogPrevention;

    View preventionView;

    // for prefs
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_prevention);

        preventionView = this.findViewById(android.R.id.content);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(preventionBrodcastReceiver, filter);

        // init meeting
        initPrevention();

        // create help dialog
        createHelpDialog();

        // show prevention view
        displayPreventionView();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            // send intent to service to start the service
            Intent startServiceIntent = new Intent(getApplicationContext(), ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            // start service
            getApplicationContext().startService(startServiceIntent);
        }

        // check for intent on start time
        // Extras from intent that holds data
        Bundle intentExtras = null;
        // intent
        Intent intent = getIntent();

        if (intent != null) { // intent set?
            // get the link data from the extra
            intentExtras = intent.getExtras();
            if (intentExtras != null && intentExtras.getString("com") != null) { // extra data set?
                // get command and execute it
                executeIntentCommand(intentExtras.getString("com"));
            }
        }



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // de-register broadcast receiver
        this.unregisterReceiver(preventionBrodcastReceiver);
    }


    private void initPrevention() {

        // init the toolbarPrevention
        toolbarPrevention = (Toolbar) findViewById(R.id.toolbarPrevention);
        setSupportActionBar(toolbarPrevention);
        toolbarPrevention.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // open sharedPrefs
        prefs =  getApplicationContext().getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, getApplicationContext().MODE_PRIVATE);
    }




    void displayPreventionView () {

        TextView tmpLinkToVideo;

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get button show video depression
        Button buttonShowVideoDepression = (Button) preventionView.findViewById(R.id.buttonShowVideoDepression);

        // set onClick listener send intent to youtube app or browser
        buttonShowVideoDepression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // play video
                String idVideo = "1UiA32Qv4yE";
                sendIntentForYouTubeVideo(idVideo);
            }
        });

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // get button show video pain
        Button buttonShowVideoPain = (Button) preventionView.findViewById(R.id.buttonShowVideoPain);

        // set onClick listener send intent to youtube app or browser
        buttonShowVideoPain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // play video
                String idVideo = "KpJfixYgBrw";
                sendIntentForYouTubeVideo(idVideo);
            }
        });



        // get textview for Link to media competence medienquiz schaun hin
        tmpLinkToVideo = (TextView) preventionView.findViewById(R.id.preventionMediaCompetenceLinkToVideo1);
        tmpLinkToVideo.setMovementMethod(LinkMovementMethod.getInstance());

        // get textview for Link to media competence klicksafe
        tmpLinkToVideo = (TextView) preventionView.findViewById(R.id.preventionMediaCompetenceLinkToVideo2);
        tmpLinkToVideo.setMovementMethod(LinkMovementMethod.getInstance());







    }




    void sendIntentForYouTubeVideo(String videoId) {

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }






    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver preventionBrodcastReceiver = new BroadcastReceiver() {

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
                    String textCaseClose = ActivityPrevention.this.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
            }
        }
    };




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

        if (command.equals("open_link_medienquiz_schau_hin")) {

            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://medienquiz.schau-hin.info"));
            try {
                startActivity(webIntent);
            }
            catch (ActivityNotFoundException ex) {
                String textCaseClose = ActivityPrevention.this.getString(R.string.toastNoLinkGoalFound);
                Toast toast = Toast.makeText(ActivityPrevention.this, textCaseClose, Toast.LENGTH_LONG);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }

        }
        else if (command.equals("open_link_klicksafe")) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.klicksafe.de"));
            try {
                startActivity(webIntent);
            }
            catch (ActivityNotFoundException ex) {
                String textCaseClose = ActivityPrevention.this.getString(R.string.toastNoLinkGoalFound);
                Toast toast = Toast.makeText(ActivityPrevention.this, textCaseClose, Toast.LENGTH_LONG);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        }




    }













    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonSettings = (Button) findViewById(R.id.helpPrevention);

        // add button listener to question mark in activity settings efb (toolbar)
        tmpHelpButtonSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPrevention.this);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityPrevention.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_prevention, null);

                // get string ressources
                String tmpTextCloseDialog = ActivityPrevention.this.getResources().getString(R.string.textDialogPreventionCloseDialog);
                String tmpTextTitleDialog = ActivityPrevention.this.getResources().getString(R.string.textDialogPreventionTitleDialog);

                // build the dialog
                builder.setView(dialogSettings)

                        // Add close button
                        .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogPrevention.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogPrevention = builder.create();

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
