package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ich on 12.08.16.
 */
public class ActivityFaq extends AppCompatActivity {

    Toolbar toolbarFaq;
    ActionBar actionBar;

    ViewPager viewPagerFaq;
    TabLayout tabLayoutFaq;

    // reference to dialog settings
    AlertDialog alertDialogFaq;

    // context of activity
    Context contextFaq;

    // expand text list
    String expandTextList = "";

    // view pager adapter
    FaqViewPagerAdapter faqViewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_faq);

        contextFaq = this;

        // init activity faq
        initFaq();

        // set up viewpager
        viewPagerFaq = (ViewPager) findViewById(R.id.viewPagerFaq);
        faqViewPagerAdapter = new FaqViewPagerAdapter(getSupportFragmentManager(), this);
        viewPagerFaq.setAdapter(faqViewPagerAdapter);

        tabLayoutFaq = (TabLayout) findViewById(R.id.tabLayoutFaq);
        tabLayoutFaq.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayoutFaq.setupWithViewPager(viewPagerFaq);

        tabLayoutFaq.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";

                // Change the subtitle of the activity
                switch (tab.getPosition()) {
                    case 0: // title for tab zero
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionOne", "string", getPackageName()));
                        break;
                    case 1: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionTwo", "string", getPackageName()));
                        break;
                    case 2: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionThree", "string", getPackageName()));
                        break;
                    case 3: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionFour", "string", getPackageName()));
                        break;
                    case 4: // title for tab one
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionFive", "string", getPackageName()));
                        break;
                    default:
                        tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionOne", "string", getPackageName()));
                        break;
                }

                // set correct subtitle in toolbar
                toolbarFaq.setSubtitle(tmpSubtitleText);

                // set correct tab over viewpager
                viewPagerFaq.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // check for new intent at activity start
        // Extras from intent that holds data
        Bundle intentExtras = null;

        // intent
        Intent intent = getIntent();

        if (intent != null) {
            // get the link data from URI and from the extra
            intentExtras = intent.getExtras();
            if (intentExtras != null && intentExtras.getString("com") != null) {
                // get command and execute it
                executeIntentCommand(intentExtras.getString("com"));
            }
        }

    }


    // init the activity Faq
    private void initFaq() {

        // init the toolbar
        toolbarFaq = (Toolbar) findViewById(R.id.toolbarFaq);
        setSupportActionBar(toolbarFaq);
        toolbarFaq.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set correct subtitle for first call
        String tmpSubtitleText = getResources().getString(getResources().getIdentifier("faqSubtitleSectionOne", "string", getPackageName()));
        toolbarFaq.setSubtitle(tmpSubtitleText);

        // create help dialog
        createHelpDialog();
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
            executeIntentCommand (intentExtras.getString("com"));
        }
    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command) {

        if (command != null && command.equals("show_section1")) { // Show fragment for overview

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(0);
            tab.select();
        }
        else if (command != null && command.equals("show_section2")) { // Show fragment for Fragen zur App

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(1);
            tab.select();
        }
        else if (command != null && command.equals("show_section3")) { // Show fragment for Erziehungsberatung

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(2);
            tab.select();
        }
        else if (command != null && command.equals("show_section4")) { // Show fragment for Beratungsstellen

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(3);
            tab.select();
        }
        else if (command != null && command.equals("show_section5")) { // Show fragment for Erziehungsfragen

            TabLayout.Tab tab = tabLayoutFaq.getTabAt(4);
            tab.select();
        }
    }



    // generate hash value of string and check for sub or full string output
    Bundle checkAndGenerateMoreOrLessStringLink (String textString, String tmpExpandTextList) {

        Bundle bundle = new Bundle();

        final String MD5 = "MD5";

        String subString = "";

        if (textString.length() > ConstansClassMain.maxLessOrMoreStringFaqLetters) {
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
                int lastSpacePosition = textString.indexOf(" ", ConstansClassMain.maxLessOrMoreStringFaqLetters);
                if (lastSpacePosition < ConstansClassMain.maxLessOrMoreStringFaqLetters) {lastSpacePosition = ConstansClassMain.maxLessOrMoreStringFaqLetters;}
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
                .path("faq")
                .appendQueryParameter("expand_text_list", tmpExpandTextList)
                .appendQueryParameter("com", "less_or_more_text")
                .appendQueryParameter("link_text_hash", tmpLinkTextHash);

        String linkLessOrMoreTextString = "";
        String linkLessTextString = contextFaq.getResources().getString(R.string.preventionLinkTextLess);
        linkLessOrMoreTextString = contextFaq.getResources().getString(R.string.preventionLinkTextMore);
        if (tmpExpandTextList.contains(tmpLinkTextHash+";")) {
            linkLessOrMoreTextString = linkLessTextString;
        }

        // generate link for output
        return HtmlCompat.fromHtml(subText + "\n<a href=\"" + linkLessOrMoreTextLinkBuilder.build().toString() + "\"><b>" + linkLessOrMoreTextString + "</b></a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
    }


    // getter for expand text list
    public String getExpandTextList () {

        return expandTextList;
    }



    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonSettings = (Button) findViewById(R.id.helpFaq);

        // add button listener to question mark in activity settings efb (toolbar)
        tmpHelpButtonSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFaq.this, R.style.helpDialogStyle);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityFaq.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_faq, null);

                // get string ressources
                String tmpTextCloseDialog = ActivityFaq.this.getResources().getString(R.string.textDialogFaqCloseDialog);
                String tmpTextTitleDialog = ActivityFaq.this.getResources().getString(R.string.textDialogFaqTitleDialog);

                // build the dialog
                builder.setView(dialogSettings)

                        // Add close button
                        .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogFaq.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogFaq = builder.create();

                alertDialogFaq.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // change background and text color of button
                        Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        // Change negative button text and background color
                        negativeButton.setTextColor(ContextCompat.getColor(ActivityFaq.this, R.color.white));
                        negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
                    }
                });

                // and show the dialog
                alertDialogFaq.show();
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