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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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
            Intent startServiceIntent = new Intent(contextPrevention, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(contextPrevention, startServiceIntent);
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
        toolbarPrevention.setSubtitle(contextPrevention.getString(R.string.preventionSubtitleActivity));

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
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
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
        // media competence
        tmpText = contextPrevention.getString(R.string.preventionMediaCompetenceExplainText1);
        textViewExplain = (TextView) preventionView.findViewById(R.id.preventionMediaCompetenceExplain1);
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = makeLinkForMoreOrLessText (returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewExplain.setText(tmpLinkText);
            textViewExplain.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            textViewExplain.setText(tmpText);
        }

        tmpText = contextPrevention.getString(R.string.preventionMediaCompetenceExplainText2);
        textViewExplain = (TextView) preventionView.findViewById(R.id.preventionMediaCompetenceExplain2);
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = makeLinkForMoreOrLessText (returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewExplain.setText(tmpLinkText);
            textViewExplain.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            textViewExplain.setText(tmpText);
        }

        // get textview for Link to media competence medienquiz schaun hin
        tmpLinkToVideo = (TextView) preventionView.findViewById(R.id.preventionMediaCompetenceLinkToVideo1);
        tmpLinkToVideo.setMovementMethod(LinkMovementMethod.getInstance());

        // get textview for Link to media competence klicksafe
        tmpLinkToVideo = (TextView) preventionView.findViewById(R.id.preventionMediaCompetenceLinkToVideo2);
        tmpLinkToVideo.setMovementMethod(LinkMovementMethod.getInstance());

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // explain pain
        tmpText = contextPrevention.getString(R.string.preventionPainExplainText);
        textViewExplain = (TextView) preventionView.findViewById(R.id.preventionPainExplain);
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
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

        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // finance help for familiy
        tmpText = contextPrevention.getString(R.string.preventionFinanceForFamilyExplainText);
        textViewExplain = (TextView) preventionView.findViewById(R.id.preventionFinanceForFamilyExplain);
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = makeLinkForMoreOrLessText (returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewExplain.setText(tmpLinkText);
            textViewExplain.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            textViewExplain.setText(tmpText);
        }
        // get textview for Link to finance help
        tmpLinkToVideo = (TextView) preventionView.findViewById(R.id.preventionFinanceForFamilyLinkToVideo);
        tmpLinkToVideo.setMovementMethod(LinkMovementMethod.getInstance());

        // mobbing
        tmpText = contextPrevention.getString(R.string.preventionMobbingExplainText1);
        textViewExplain = (TextView) preventionView.findViewById(R.id.preventionMobbingExplain);
        returnBundle = checkAndGenerateMoreOrLessStringLink(tmpText, tmpExpandTextList);
        if (returnBundle.getBoolean("generate")) {
            Spanned tmpLinkText = makeLinkForMoreOrLessText (returnBundle.getString("substring"), expandTextList, returnBundle.getString("hash_value"));
            textViewExplain.setText(tmpLinkText);
            textViewExplain.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            textViewExplain.setText(tmpText);
        }
        // get textview for Link to finance help
        tmpLinkToVideo = (TextView) preventionView.findViewById(R.id.preventionMobbingLink1ToVideo);
        tmpLinkToVideo.setMovementMethod(LinkMovementMethod.getInstance());




    }



    // generate hash value of string and check for sub or full string output
    Bundle checkAndGenerateMoreOrLessStringLink (String textString, String tmpExpandTextList) {

        Bundle bundle = new Bundle();

        final String MD5 = "MD5";

        String subString = "";

        if (textString.length() > ConstansClassMain.maxLessOrMoreStringPreventionLetters) {
            bundle.putBoolean("generate", true);

            // generate hash value of string
            bundle.putString("hash_value", "");
            String hashValue = "";
            try {
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
                digest.update(textString.getBytes());
                byte[] messageDigest = digest.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    String h = Integer.toHexString(0xFF & aMessageDigest);
                    while (h.length() < 2)
                        h = "0" + h;
                    hexString.append(h);
                }

                hashValue = hexString.toString();
                bundle.putString("hash_value", hashValue);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            // generate sub string for output
            String tmpHashValue = hashValue + ";";
            if (tmpExpandTextList.contains(tmpHashValue)) {
                subString = textString;
            }
            else {
                int lastSpacePosition = textString.indexOf(" ", ConstansClassMain.maxLessOrMoreStringPreventionLetters);
                if (lastSpacePosition < ConstansClassMain.maxLessOrMoreStringPreventionLetters) {lastSpacePosition = ConstansClassMain.maxLessOrMoreStringPreventionLetters;}
                subString = textString.substring(0, lastSpacePosition);
                subString = subString + "...";
            }
            bundle.putString("substring", subString);
        }
        else {
            bundle.putBoolean("generate", false);
        }

        return bundle;
    }


    // generate more or less link text
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
        Spanned linkMoreOrLess = HtmlCompat.fromHtml(subText + "\n<a href=\"" + linkLessOrMoreTextLinkBuilder.build().toString() + "\"><b>" + linkLessOrMoreTextString + "</b></a>", HtmlCompat.FROM_HTML_MODE_LEGACY);

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


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver preventionBrodcastReceiver = new BroadcastReceiver() {

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
            String tmpCommand = intentExtras.getString("com");
            if (tmpCommand == null) {tmpCommand = "";}
            executeIntentCommand (tmpCommand, tmpExpandTextList, tmpLinkTextHash);
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
        else if (command.equals("open_link_infotool_family")) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.infotool-familie.de/"));
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
        else if (command.equals("open_link_mobbing_schluss")) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mobbing-schluss-damit.de/kartoffelfilm/cool-faktor-zero"));
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
            }
            else {
                tmpExpandTextList = tmpExpandTextList.concat(tmpLinkTextHash+";");
            }

            expandTextList = tmpExpandTextList;

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

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPrevention.this, R.style.helpDialogStyle);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityPrevention.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_prevention, null);

                // get string ressources
                String tmpTextCloseDialog = ActivityPrevention.this.getResources().getString(R.string.textDialogPreventionCloseDialog);
                String tmpTextTitleDialog = ActivityPrevention.this.getResources().getString(R.string.textDialogPreventionTitleDialog);

                TextView tmpLinkMail = (TextView) dialogSettings.findViewById(R.id.textViewDialogPreventionIntro);
                tmpLinkMail.setMovementMethod(LinkMovementMethod.getInstance());

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

                alertDialogPrevention.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // change background and text color of button
                        Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        // Change negative button text and background color
                        negativeButton.setTextColor(ContextCompat.getColor(ActivityPrevention.this, R.color.white));
                        negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
                    }
                });

                // and show the dialog
                alertDialogPrevention.show();
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
