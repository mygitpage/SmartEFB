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
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    // context of activity
    Context contextPrevention;

    // for prefs
    private SharedPreferences prefs;

    // expand text list
    String expandTextList = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_prevention);

        preventionView = this.findViewById(android.R.id.content);

        contextPrevention = this;

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(preventionBrodcastReceiver, filter);

        // init meeting
        initPrevention();

        // create help dialog
        createHelpDialog();

        // show prevention view
        displayPreventionView("", "");

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            // send intent to service to start the service
            Intent startServiceIntent = new Intent(contextPrevention, ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            contextPrevention.startService(startServiceIntent);
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

                String tmpExpandTextList = "";
                String tmpLinkTextHash = "";
                if (intentExtras.getString("expand_text_list") != null && intentExtras.getString("expand_text_list").length() > 0 && intentExtras.getString("link_text_hash") != null && intentExtras.getString("link_text_hash").length() > 0) {
                    tmpExpandTextList = intentExtras.getString("expand_text_list");
                    tmpLinkTextHash = intentExtras.getString("link_text_hash");
                }

                // get command and execute it
                executeIntentCommand (intentExtras.getString("com"), tmpExpandTextList, tmpLinkTextHash);
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
        prefs =  getApplicationContext().getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextPrevention.MODE_PRIVATE);
    }




    void displayPreventionView (String tmpExpandTextList, String tmpLinkTextHash) {

        TextView tmpLinkToVideo;

        String tmpText;

        TextView textViewExplain;

        Bundle returnBundle;

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // get depression intro text
        tmpText = contextPrevention.getString(R.string.preventionDepressionExplainText);
        textViewExplain = (TextView) preventionView.findViewById(R.id.preventionDepressionExplain);
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList, tmpLinkTextHash);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = makeLinkForMoreOrLessText (returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewExplain.setText(tmpLinkText);
            textViewExplain.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            textViewExplain.setText(tmpText);
        }

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


        tmpText = contextPrevention.getString(R.string.preventionMediaCompetenceExplainText1);
        textViewExplain = (TextView) preventionView.findViewById(R.id.preventionMediaCompetenceExplain1);
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList, tmpLinkTextHash);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = makeLinkForMoreOrLessText (returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewExplain.setText(tmpLinkText);
            textViewExplain.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            textViewExplain.setText(tmpText);
        }



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







    Bundle checkAndGenerateMoreOrLessStringLink (String textString, String tmpExpandTextList, String tmpLinkTextHash) {

        Bundle bundle = new Bundle();

        final String MD5 = "MD5";

        String subString = "";

        if (textString.length() > ConstansClassMain.maxLessOrMoreStringLetters) {
            bundle.putBoolean("generate", true);



            Log.d("CheckMoreOrLess---->", "List:"+tmpExpandTextList+" +++ Hash:"+tmpLinkTextHash);



            // generate hash value of string
            bundle.putString("hash_value", "");
            String hashValue = "";
            try {
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
                digest.update(textString.getBytes());
                byte messageDigest[] = digest.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    String h = Integer.toHexString(0xFF & aMessageDigest);
                    while (h.length() < 2)
                        h = "0" + h;
                    hexString.append(h);
                }

                Log.d("Generate HASH -->", "Value:"+hexString.toString());

                hashValue = hexString.toString();
                bundle.putString("hash_value", hashValue);


            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }




            // generate sub string for output
            if (tmpExpandTextList.contains(tmpLinkTextHash+";") && hashValue.equals(tmpLinkTextHash+";")) {
                subString = textString;

            }
            else {
                subString = textString.substring(0, textString.indexOf(" ", ConstansClassMain.maxLessOrMoreStringLetters));
                subString = subString + "...";
            }
            bundle.putString("substring", subString);







        }
        else {
            bundle.putBoolean("generate", false);
        }

        return bundle;
    }




    Spanned makeLinkForMoreOrLessText (String subText, String tmpExpandTextList, String tmpLinkTextHash) {

        final Uri.Builder linkLessOrMoreTextLinkBuilder = new Uri.Builder();
        linkLessOrMoreTextLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("prevention")
                .appendQueryParameter("expand_text_list", tmpExpandTextList)
                .appendQueryParameter("com", "less_or_more_text")
                .appendQueryParameter("link_text_hash", tmpLinkTextHash);

        String linkLessOrMoreTextString = "";
        String linkLessTextString = contextPrevention.getResources().getString(R.string.preventionLinkTextLess);
        String linkMoreTextString = contextPrevention.getResources().getString(R.string.preventionLinkTextMore);

        linkLessOrMoreTextString = linkMoreTextString;
        if (tmpExpandTextList.contains(tmpLinkTextHash+";")) {
            linkLessOrMoreTextString = linkLessTextString;
        }

        // generate link for output
        Spanned linkMoreOrLess = Html.fromHtml(subText + "\n<a href=\"" + linkLessOrMoreTextLinkBuilder.build().toString() + "\"><b>" + linkLessOrMoreTextString + "</b></a>");

        return linkMoreOrLess;
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

            String tmpExpandTextList = "";
            String tmpLinkTextHash = "";
            if (intentExtras.getString("expand_text_list") != null && intentExtras.getString("expand_text_list").length() >= 0 && intentExtras.getString("link_text_hash") != null && intentExtras.getString("link_text_hash").length() > 0) {
                tmpExpandTextList = intentExtras.getString("expand_text_list");
                tmpLinkTextHash = intentExtras.getString("link_text_hash");
            }

            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"), tmpExpandTextList, tmpLinkTextHash);
        }
    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, String tmpExpandTextList, String tmpLinkTextHash) {

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
        else if (command.equals("less_or_more_text")) {

            if (tmpExpandTextList.contains(tmpLinkTextHash+";")) {
                tmpExpandTextList = tmpExpandTextList.replace(tmpLinkTextHash+";", "");

                Log.d("NewIntentHashList", "REPLACE!!!!");
            }
            else {

                Log.d("NewIntentHashList", "CONCAT!!!!");

                tmpExpandTextList = tmpExpandTextList.concat(tmpLinkTextHash+";");
            }

            expandTextList = tmpExpandTextList;


            Log.d("Prevention####>", "List:"+tmpExpandTextList);


            // show prevention view
            displayPreventionView(tmpExpandTextList, tmpLinkTextHash);

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
