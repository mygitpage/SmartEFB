package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ich on 25.05.16.
 */


// Android File browser example
// https://www.christophbrill.de/en/posts/how-to-create-a-android-file-browser-in-15-minutes/

// https://mobikul.com/how-file-chooser-works-in-android-studio/


public class ActivityOurArrangement extends AppCompatActivity {

    // Set subtitle first time
    Boolean setSubtitleFirstTime = false;

    // set visibility of fab for first time
    Boolean setFabFirstTime = false;

    // set visibility of fab after refresh
    Boolean setFabClickListenerRefreshTimeForNow = false;
    Boolean setFabClickListenerRefreshTimeForSketch = false;

    // set click listener for fab for first time
    Boolean setFabClickListenerFirstTime = false;

    // evaluate pause time and active time (get from prefs)
    int evaluatePauseTime = 0;
    int evaluateActivTime = 0;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // reference for the toolbar
    Toolbar toolbar = null;
    ActionBar actionBar = null;

    // reference fab
    FloatingActionButton ourArrangementFabView = null;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // block id of current arrangement
    String currentBlockIdOfArrangement = "";

    // the date of sketch arrangement
    long currentDateOfSketchArrangement;

    // viewpager and tablayout for the view
    ViewPager viewPagerOurArrangement;
    TabLayout tabLayoutOurArrangement;

    // viewpager adapter
    OurArrangementViewPagerAdapter ourArrangementViewPagerAdapter;

    // Strings for subtitle ("Aktuelle vom...", "Älter als...", "Absprache kommentieren", "Kommentare zeigen", "Absprache bewerten", "Entwuerfe Absprachen" )
    String [] arraySubTitleText = new String[ConstansClassOurArrangement.numberOfDifferentSubtitle];

    // Show or hide FAB in different fragments
    String [] arrayShowOrHideFAB = new String[ConstansClassOurArrangement.numberOfDifferentSubtitle];

    // array list of object smart efb arrangement
    ArrayList<ObjectSmartEFBArrangement> arrayListOfArrangementNow = new ArrayList<ObjectSmartEFBArrangement>();
    ArrayList<ObjectSmartEFBArrangement> arrayListOfArrangementSketch = new ArrayList<ObjectSmartEFBArrangement>();
    ArrayList<ObjectSmartEFBArrangement> arrayListOfArrangementShowNowComment = new ArrayList<ObjectSmartEFBArrangement>();
    ArrayList<ObjectSmartEFBArrangement> arrayListOfArrangementShowSketchComment = new ArrayList<ObjectSmartEFBArrangement>();
    // fragment name for click listener in different fragments
    String [] arrayFragmentNameForClickListener = new String[ConstansClassOurArrangement.numberOfDifferentSubtitle];
    String [] arrayFragmentIntentOrderForClickListener = new String[ConstansClassOurArrangement.numberOfDifferentSubtitle];

    // what to show in tab zero (like show_comment_for_arrangement, comment_an_arrangement, show_arrangement_now, evaluate_an_arrangement)
    String showCommandFragmentTabZero = "";
    // what to show in tab one (like show_sketch_arrangement, comment_an_sketch_arrangement, show_comment_for_sketch_arrangement)
    String showCommandFragmentTabOne = "";

    // arrangement db-id - for comment, sketch comment, show comment or show sketch comment
    int arrangementServerDbIdFromLink = 0;
    int arrangementSketchDbIdFromLink = 0;
    //arrangement number and sketch number in listview
    int arrangementNumberInListView = 0;
    int arrangementSketchNumberInListView = 0;

    // evaluate next arrangement true -> yes, there is a next arrangement to evaluate; false -> there is nothing more
    boolean evaluateNextArrangement = false;

    // info new entry on tab zero or one
    Boolean infoNewEntryOnTabZero = false;
    Boolean infoNewEntryOnTabOne = false;
    String infoTextNewEntryPostFixTabZeroTitle = "";
    String infoTextNewEntryPostFixTabOneTitle = "";
    String tabTitleTextTabZero = "";
    String tabTitleTextTabOne = "";

    // reference to the DB
    DBAdapter myDb;

    // reference to dialog settings and Arrangement change
    AlertDialog alertDialogSettings;
    AlertDialog alertDialogArrangementChange;

    // activ/inactiv sub-functions
    // our arrangements sub functions activ/ inactiv
    Boolean subfunction_arrangement_comment = false;
    Boolean subfunction_arrangement_evaluation = false;
    Boolean subfunction_arrangement_sketch = false;
    Boolean subfunction_arrangement_sketchcomment = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_arrangement);

        // init our arragement
        initOurArrangement();

        // init evaluation start point/ alarm manager for evaluation is set in main activity!
        setOurArrangementEvaluationStartPoint();

        // find viewpager in view
        viewPagerOurArrangement = findViewById(R.id.viewPagerOurArrangement);
        // new pager adapter for OurArrangement
        ourArrangementViewPagerAdapter = new OurArrangementViewPagerAdapter(getSupportFragmentManager(), this);
        // set pagerAdapter to viewpager
        viewPagerOurArrangement.setAdapter(ourArrangementViewPagerAdapter);

        //find tablayout and set gravity
        tabLayoutOurArrangement = findViewById(R.id.tabLayoutOurArrangement);
        tabLayoutOurArrangement.setTabGravity(TabLayout.GRAVITY_FILL);

        // and set tablayout with viewpager
        tabLayoutOurArrangement.setupWithViewPager(viewPagerOurArrangement);

        // set correct tab zero and one title with information new entry and color change -> FIRST TIME
        setTabZeroTitleAndColor();
        setTabOneTitleAndColor();

        // init listener for tab selected
        tabLayoutOurArrangement.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tmpSubtitleText = "";
                String tmpShowOrHideFAB = "";

                // Change the subtitle of the activity
                switch (tab.getPosition()) {
                    case 0: // title for tab zero
                        switch (showCommandFragmentTabZero) {
                            case "show_arrangement_now":
                                tmpSubtitleText = arraySubTitleText[0];
                                tmpShowOrHideFAB = arrayShowOrHideFAB[0];
                                break;
                            case "comment_an_arrangement":
                                tmpSubtitleText = arraySubTitleText[3];
                                tmpShowOrHideFAB = arrayShowOrHideFAB[3];
                                break;
                            case "show_comment_for_arrangement":
                                tmpSubtitleText = arraySubTitleText[4];
                                tmpShowOrHideFAB = arrayShowOrHideFAB[4];
                                break;
                            case "evaluate_an_arrangement":
                                tmpSubtitleText = arraySubTitleText[5];
                                tmpShowOrHideFAB = arrayShowOrHideFAB[5];
                                break;
                        }

                        // set correct tab zero title with information new entry and color change
                        setTabZeroTitleAndColor();

                        break;
                   case 1: // title for tab one
                        switch (showCommandFragmentTabOne) {
                            case "show_sketch_arrangement":
                                tmpSubtitleText = arraySubTitleText[1];
                                tmpShowOrHideFAB = arrayShowOrHideFAB[1];
                                break;
                            case "comment_an_sketch_arrangement":
                                tmpSubtitleText = arraySubTitleText[6];
                                tmpShowOrHideFAB = arrayShowOrHideFAB[6];
                                break;
                            case "show_comment_for_sketch_arrangement":
                                tmpSubtitleText = arraySubTitleText[7];
                                tmpShowOrHideFAB = arrayShowOrHideFAB[7];
                                break;
                        }

                       // set correct tab one title with information new entry and color change
                       setTabOneTitleAndColor();

                       break;
                    case 2: // title for tab two
                        tmpSubtitleText = arraySubTitleText[2];
                        tmpShowOrHideFAB = arrayShowOrHideFAB[2];
                        break;
                    default:
                        tmpSubtitleText = arraySubTitleText[0];
                        tmpShowOrHideFAB = arrayShowOrHideFAB[0];
                        break;
                }

                // set toolbar text
                toolbar.setSubtitle(tmpSubtitleText);

                // show or hide fab in fragment
                showOrHideFAB(tmpShowOrHideFAB);
                // set on click listener for FAB
                if (tmpShowOrHideFAB.equals("show")) {
                    if (tab.getPosition() == 0 && showCommandFragmentTabZero.equals("show_arrangement_now")) {
                        addOnClickListenerToFABForFragmentNow(arrayListOfArrangementNow, arrayFragmentNameForClickListener[0], arrayFragmentIntentOrderForClickListener[0]);
                    }
                    else if (tab.getPosition() == 0 && showCommandFragmentTabZero.equals("show_comment_for_arrangement")) {
                        addOnClickListenerToFABForFragmentNow(arrayListOfArrangementNow, arrayFragmentNameForClickListener[4], arrayFragmentIntentOrderForClickListener[4]);
                    }
                    if (tab.getPosition() == 1 && showCommandFragmentTabOne.equals("show_sketch_arrangement")) {
                        addOnClickListenerToFABForFragmentNow(arrayListOfArrangementSketch, arrayFragmentNameForClickListener[1], arrayFragmentIntentOrderForClickListener[1]);
                    }
                    else if (tab.getPosition() == 1 && showCommandFragmentTabOne.equals("show_comment_for_sketch_arrangement")) {
                        addOnClickListenerToFABForFragmentNow(arrayListOfArrangementSketch, arrayFragmentNameForClickListener[7], arrayFragmentIntentOrderForClickListener[7]);
                    }
                }

                // call viewpager
                viewPagerOurArrangement.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // close db connection
        myDb.close();
    }



    // Look for new intents (with data from URI or putExtra)
    @Override
    protected void onNewIntent(Intent intent) {

        // Extras from intent that holds data
        Bundle intentExtras = null;

        arrangementServerDbIdFromLink = 0;
        arrangementNumberInListView = 0;
        evaluateNextArrangement = false;

        // call super
        super.onNewIntent(intent);

        // get the link data from URI and from the extra
        intentExtras = intent.getExtras();

        int tmpServerDbId = 0;
        int tmpNumberinListView = 0;
        Boolean tmpEvalNext = false;
        String tmpCommand = "";

        if (intentExtras != null) {
           // get data that comes with extras
            tmpServerDbId = intentExtras.getInt("db_id",0);
            tmpNumberinListView = intentExtras.getInt("arr_num",0);
            tmpEvalNext = intentExtras.getBoolean("eval_next");
            // get command and execute it
            tmpCommand = intentExtras.getString("com");
            if (tmpCommand == null) {tmpCommand="";}
            executeIntentCommand (tmpCommand, tmpServerDbId, tmpNumberinListView, tmpEvalNext);
        }
    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, int tmpServerDbId, int tmpNumberinListView, Boolean tmpEvalNext) {

        if (command.equals("show_comment_for_arrangement")) { // Show fragment all comments for arrangement

            // set global varibales
            arrangementServerDbIdFromLink = tmpServerDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("show_comment_for_arrangement");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1b", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "show_comment_for_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[4]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[4]);

        }  else if (command.equals("comment_an_arrangement")) { // Show fragment comment arrangement

            // set global varibales
            arrangementServerDbIdFromLink = tmpServerDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("comment_an_arrangement");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1a", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "comment_an_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[3]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[3]);

        } else if (command.equals("evaluate_an_arrangement")) { // Show evaluate a arrangement

            // set global varibales
            arrangementServerDbIdFromLink = tmpServerDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to evaluate
            OurArrangementViewPagerAdapter.setFragmentTabZero("evaluate_an_arrangement");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1c", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "evaluate_an_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[5]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[5]);

        } else if (command.equals("comment_an_sketch_arrangement")) { // Comment sketch arrangement -> TAB ONE

            // set global varibales
            arrangementSketchDbIdFromLink = tmpServerDbId;
            arrangementSketchNumberInListView = tmpNumberinListView;

            //set fragment in tab one to comment an sketch arrangement
            OurArrangementViewPagerAdapter.setFragmentTabOne("comment_an_sketch_arrangement");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2a", "string", getPackageName()));
            setTabOneTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "comment_an_sketch_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbar.setSubtitle(arraySubTitleText[6]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[6]);

        } else if (command.equals("show_sketch_arrangement")) { // Show sketch Arrangements -> TAB ONE

            // set global varibales
            arrangementSketchDbIdFromLink = tmpServerDbId;
            arrangementSketchNumberInListView = tmpNumberinListView;

            //set fragment in tab one to show sketch arrangement
            OurArrangementViewPagerAdapter.setFragmentTabOne("show_sketch_arrangement");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2", "string", getPackageName()));
            setTabOneTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "show_sketch_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbar.setSubtitle(arraySubTitleText[1]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[1]);

        } else if (command.equals("show_comment_for_sketch_arrangement")) { // Show comments for sketch Arrangments -> TAB ONE

            // set global variables
            arrangementSketchDbIdFromLink = tmpServerDbId;
            arrangementSketchNumberInListView = tmpNumberinListView;

            //set fragment in tab one to show comment sketch arrangement
            OurArrangementViewPagerAdapter.setFragmentTabOne("show_comment_for_sketch_arrangement");

            // set correct tab one title with information new entry and color change
            tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2b", "string", getPackageName()));
            setTabOneTitleAndColor();

            // set command show variable
            showCommandFragmentTabOne = "show_comment_for_sketch_arrangement";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab one
            toolbar.setSubtitle(arraySubTitleText[7]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[7]);
        }
        else if (command.equals("show_arrangement_now_with_tab_change")) { // Change to Tab 0 and show arrangement now

            // set global varibales
            arrangementServerDbIdFromLink = tmpServerDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            // change to Tab 0!!!!
            //viewPagerOurArrangement.setCurrentItem(0);

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("show_arrangement_now");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "show_arrangement_now";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[0]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[0]);

        }
        else { // Show fragment arrangement now -> Tab 0

            // set global varibales
            arrangementServerDbIdFromLink = tmpServerDbId;
            arrangementNumberInListView = tmpNumberinListView;
            evaluateNextArrangement = tmpEvalNext;

            //set fragment in tab zero to comment
            OurArrangementViewPagerAdapter.setFragmentTabZero("show_arrangement_now");

            // set correct tab zero title with information new entry and color change
            tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1", "string", getPackageName()));
            setTabZeroTitleAndColor();

            // set command show variable
            showCommandFragmentTabZero = "show_arrangement_now";

            // call notify data change
            ourArrangementViewPagerAdapter.notifyDataSetChanged();

            // set correct subtitle in toolbar in tab zero
            toolbar.setSubtitle(arraySubTitleText[0]);

            // show or hide fab for this fragment
            showOrHideFAB(arrayShowOrHideFAB[0]);

        }
    }


    // init the activity
    private void initOurArrangement() {

        // init the toolbar
        toolbar = findViewById(R.id.toolbarOurArrangement);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        catch (NullPointerException e){
            // do nothing
        }

        // find fab
        ourArrangementFabView = findViewById(R.id.fabOurArrangement);

        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // our arrangements sub functions activ/ inactiv
        subfunction_arrangement_comment = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false);
        subfunction_arrangement_evaluation = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false);
        subfunction_arrangement_sketch = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false);
        subfunction_arrangement_sketchcomment = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, false);

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());
        // get current block id of arrangement
        currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "");
        //get date of sketch arrangement
        currentDateOfSketchArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis());

        // init show on tab zero arrangemet now
        showCommandFragmentTabZero = "show_arrangement_now";
        // init show on tab one sketch arrangemet
        showCommandFragmentTabOne = "show_sketch_arrangement";

        for (int t=0; t<ConstansClassOurArrangement.numberOfDifferentSubtitle; t++) {
            arraySubTitleText[t] = "";
            arrayShowOrHideFAB[t] = "hide"; // init show/hide FAB with "hide"
            arrayFragmentNameForClickListener[t] = ""; // init fragment name for FAB
            arrayFragmentIntentOrderForClickListener[t] = ""; // init order for intent for FAB
        }

        // enable setting subtitle for the first time
        setSubtitleFirstTime = true;

        // enable setting visibility of fab for first time
        setFabFirstTime = true;

        // enable setting click listener for fab for first time
        setFabClickListenerFirstTime = true;

        //set tab title string
        tabTitleTextTabZero = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_1", "string", getPackageName()));
        tabTitleTextTabOne = getResources().getString(getResources().getIdentifier("ourArrangementTabTitle_2", "string", getPackageName()));

        // create help dialog in OurArrangement
        createHelpDialog();
    }


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonOurArrangement = findViewById(R.id.helpOurArrangementNow);

        // add button listener to question mark in activity OurArrangement (toolbar)
        tmpHelpButtonOurArrangement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView tmpdialogTextView;
                LayoutInflater dialogInflater;

                // get alert dialog builder with custom style
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOurArrangement.this, R.style.helpDialogStyle);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityOurArrangement.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_our_arrangement, null);

                // show intro for settings
                tmpdialogTextView = dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsIntro);
                tmpdialogTextView.setText(ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsIntro));

                // show the settings for evaluation (like evaluation period, on/off-status, ...)
                tmpdialogTextView = dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsEvaluate);
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) {

                    String tmpTxtEvaluate = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluateEnable);
                    String tmpTxtEvaluate1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluatePeriod);
                    String tmpCompleteTxtEvaluate1String = tmpTxtEvaluate + " " + String.format(tmpTxtEvaluate1, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "kk.mm"), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy"),EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "kk.mm"));
                    tmpdialogTextView.setText(tmpCompleteTxtEvaluate1String);
                }
                else {
                    String tmpTxtEvaluate = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsEvaluateDisable);
                    tmpdialogTextView.setText(tmpTxtEvaluate);
                }

                // show the settings for comment (like on/off-status, count comment...)
                tmpdialogTextView = dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsComment);
                String tmpTxtComment, tmpTxtComment1, tmpTxtComment2, tmpTxtComment3, tmpTxtComment4, tmpTxtCommentSum;

                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false)) {

                    tmpTxtComment = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentEnable);

                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) < ConstansClassOurArrangement.commentLimitationBorder) { // write infinitely comments?

                        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) == 1) {
                            tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountSingular);
                        }
                        else {
                            tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountPlural);
                            tmpTxtComment1 = String.format(tmpTxtComment1, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0));
                        }
                    }
                    else {
                        tmpTxtComment1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountInfinitely);
                    }

                    // count comment - status
                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0)) {
                        switch (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0)) {
                            case 0:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCountCommentZero);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                break;
                            case 1:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberSingular);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                break;
                            default:
                                tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberPlural);
                                tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0));
                                break;
                        }

                        // set text max letters for comment
                        tmpTxtComment3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentMaxLetters);
                        tmpTxtComment3 = String.format(tmpTxtComment3, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters,0));

                        // show delaytime for comments
                        switch (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0)) {
                            case 0:
                                tmpTxtComment4 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentDelaytimeNoDelay);
                                break;
                            case 1:
                                tmpTxtComment4 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentDelaytimeSingular);
                                break;
                            default:
                                tmpTxtComment4 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentDelaytimePlural);
                                tmpTxtComment4 = String.format(tmpTxtComment4, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime,0));
                                break;
                        }
                    }
                    else {
                        tmpTxtComment2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentCountNumberOff);
                        tmpTxtComment2 = String.format(tmpTxtComment2, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCommentTimeSinceCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                        tmpTxtComment3 = "";
                        tmpTxtComment4 = "";
                    }

                    tmpTxtCommentSum = tmpTxtComment + " " + tmpTxtComment1 + " " + tmpTxtComment2 + tmpTxtComment3 + tmpTxtComment4;

                    tmpdialogTextView.setText(tmpTxtCommentSum);

                    // check comment sharing disable/ enable -> in case of disable -> show text
                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) != 1) {
                        TextView tmpdialogTextViewNoSharing = dialogSettings.findViewById(R.id.textViewDialogOurArrangementCommentSharingDisable);
                        tmpdialogTextViewNoSharing.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    tmpTxtComment = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsCommentDisable);
                    tmpdialogTextView.setText(tmpTxtComment);
                }

                // show the settings for old arrangement
                tmpdialogTextView = dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsOldArrangement);
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false)) {

                    String tmpTxtOldArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsOldArrangementEnable);
                    tmpdialogTextView.setText(tmpTxtOldArrangement);
                }
                else {
                    String tmpTxtOldArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsOldArrangementDisable);
                    tmpdialogTextView.setText(tmpTxtOldArrangement);
                }

                // show the settings for sketch arrangement
                tmpdialogTextView = dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsSketchArrangement);
                String tmpTxtSketchArrangementSum, tmpTxtSketchArrangement, tmpTxtSketchArrangement1, tmpTxtSketchArrangement2, tmpTxtSketchArrangement3, tmpTxtSketchArrangement4, tmpTxtSketchArrangement5;
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false)) {

                    tmpTxtSketchArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchArrangementEnable);

                    // comment sketch arrangements?
                    if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, false)) {

                        tmpTxtSketchArrangement1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentArrangementEnable);

                        if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) < ConstansClassOurArrangement.commentLimitationBorder) { // write infinitely sketch comments?

                            if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) == 1) {
                                tmpTxtSketchArrangement2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountSingular);
                            }
                            else {
                                tmpTxtSketchArrangement2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountPlural);
                                tmpTxtSketchArrangement2 = String.format(tmpTxtSketchArrangement2, prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0));
                            }
                        }
                        else {
                            tmpTxtSketchArrangement2 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountInfinitely);
                        }

                        // count sketch comment - status
                        if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0)) {
                            switch (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0)) {
                                case 0:
                                    tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCountCommentZero);
                                    tmpTxtSketchArrangement3 = String.format(tmpTxtSketchArrangement3, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                    break;
                                case 1:
                                    tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountNumberSingular);
                                    tmpTxtSketchArrangement3 = String.format(tmpTxtSketchArrangement3, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                                    break;
                                default:
                                    tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountNumberPlural);
                                    tmpTxtSketchArrangement3 = String.format(tmpTxtSketchArrangement3, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"), prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0));
                                    break;
                            }

                            // set text max letters for sketch comment
                            tmpTxtSketchArrangement4 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentMaxLetters);
                            tmpTxtSketchArrangement4 = String.format(tmpTxtSketchArrangement4, prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchCommentLetters,0));

                            // show delaytime for comments
                            switch (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0)) {
                                case 0:
                                    tmpTxtSketchArrangement5 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentDelaytimeNoDelay);
                                    break;
                                case 1:
                                    tmpTxtSketchArrangement5 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentDelaytimeSingular);
                                    break;
                                default:
                                    tmpTxtSketchArrangement5 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentDelaytimePlural);
                                    tmpTxtSketchArrangement5 = String.format(tmpTxtSketchArrangement5, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime,0));
                                    break;
                            }
                        }
                        else {
                            tmpTxtSketchArrangement3 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentCountNumberOff);
                            tmpTxtSketchArrangement3 = String.format(tmpTxtSketchArrangement3, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsSketchCommentTimeSinceSketchCommentStartInMills, System.currentTimeMillis()), "dd.MM.yyyy"));
                            tmpTxtSketchArrangement4 = "";
                            tmpTxtSketchArrangement5 = "";
                        }

                        // check sketch comment sharing disable/ enable -> in case of disable -> show text
                        if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) != 1) {
                            TextView tmpdialogTextViewNoSharing = dialogSettings.findViewById(R.id.textViewDialogOurArrangementSketchCommentSharingDisable);
                            tmpdialogTextViewNoSharing.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        tmpTxtSketchArrangement1 = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchCommentArrangementDisable);
                        tmpTxtSketchArrangement2 = "";
                        tmpTxtSketchArrangement3 = "";
                        tmpTxtSketchArrangement4 = "";
                        tmpTxtSketchArrangement5 = "";
                    }
                    tmpTxtSketchArrangementSum = tmpTxtSketchArrangement + " " + tmpTxtSketchArrangement1 + " " + tmpTxtSketchArrangement2 + " " + tmpTxtSketchArrangement3  + tmpTxtSketchArrangement4 + tmpTxtSketchArrangement5;
                }
                else {
                    tmpTxtSketchArrangement = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSettingsSketchArrangementDisable);
                    tmpTxtSketchArrangementSum = tmpTxtSketchArrangement;

                }
                tmpdialogTextView.setText(tmpTxtSketchArrangementSum);

                // generate link to involved person (activity settings tab 0)
                TextView tmpdialogTextViewLinkToInvolvedPerson = dialogSettings.findViewById(R.id.textViewDialogOurArrangementSettingsInvolvedPerson);
                tmpdialogTextViewLinkToInvolvedPerson.setMovementMethod(LinkMovementMethod.getInstance());

                // get string ressources
                String tmpTextCloseDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementCloseDialog);
                String tmpTextTitleDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementTitleDialog);

                // build the dialog
                builder.setView(dialogSettings);

                // Add close button
                builder.setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogSettings.cancel();
                            }
                        });

                // add title
                builder.setTitle(tmpTextTitleDialog);

                // and create
                alertDialogSettings = builder.create();

                alertDialogSettings.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // change background and text color of button
                        Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        // Change negative button text and background color
                        negativeButton.setTextColor(ContextCompat.getColor(ActivityOurArrangement.this, R.color.white));
                        negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
                    }
                });

                // show dialog
                alertDialogSettings.show();

            }
        });
    } 


    // set evaluation start point
    void setOurArrangementEvaluationStartPoint () {

        // get evaluate pause time and active time
        evaluatePauseTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours
        evaluateActivTime = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, ConstansClassOurArrangement.defaultTimeForActiveAndPauseEvaluationArrangement); // default value 43200 is 12 hours

        // get start time and end time for evaluation
        Long startEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis());
        Long endEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis());

        Long tmpSystemTimeInMills = System.currentTimeMillis();
        int tmpEvalutePaAcTime = evaluateActivTime * 1000;

        // get calendar and init
        Calendar calendar = Calendar.getInstance();

        // set alarm manager when current time is between start date and end date and evaluation is enable
        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false) && System.currentTimeMillis() >= prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, 0) && System.currentTimeMillis() > startEvaluationDate && System.currentTimeMillis() < endEvaluationDate) {
            calendar.setTimeInMillis(startEvaluationDate);
            do {
                calendar.add(Calendar.SECOND, evaluateActivTime);
                tmpEvalutePaAcTime = evaluateActivTime * 1000; // make mills-seconds
                if (calendar.getTimeInMillis() < tmpSystemTimeInMills) {
                    calendar.add(Calendar.SECOND, evaluatePauseTime);
                    tmpEvalutePaAcTime = evaluatePauseTime * 1000; // make mills-seconds
                }
            } while (calendar.getTimeInMillis() < tmpSystemTimeInMills);

            // set new start point for evaluation timer in view fragment now for evaluation link
            prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, (calendar.getTimeInMillis() - tmpEvalutePaAcTime));
            prefsEditor.apply();
        }
    }


    // set correct tab zero title with information new entry and color change
    private void setTabZeroTitleAndColor () {

        ActivityOurArrangement.this.lookNewEntryOnTabZero();

        tabLayoutOurArrangement.getTabAt(0).setText(tabTitleTextTabZero + infoTextNewEntryPostFixTabZeroTitle);
        ActivityOurArrangement.this.setUnsetTextColorSignalNewTabZero(infoNewEntryOnTabZero);
    }


    // set correct tab one title with information new entry and color change
    private void setTabOneTitleAndColor () {

        ActivityOurArrangement.this.lookNewEntryOnTabOne();

        tabLayoutOurArrangement.getTabAt(1).setText(tabTitleTextTabOne + infoTextNewEntryPostFixTabOneTitle);
        ActivityOurArrangement.this.setUnsetTextColorSignalNewTabOne(infoNewEntryOnTabOne);
    }


    // look for new entry on tab zero
    private void lookNewEntryOnTabZero () {

        // look for new entrys in db on tab zero
        if ((subfunction_arrangement_comment && myDb.getCountAllNewEntryOurArrangementComment(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0")) > 0) || myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "current") > 0 ) {
            infoNewEntryOnTabZero = true;
            infoTextNewEntryPostFixTabZeroTitle = " " + this.getResources().getString(R.string.newEntryText);
        }
        else {
            infoNewEntryOnTabZero = false;
            infoTextNewEntryPostFixTabZeroTitle = "";
        }
    }


    // look for new entry on tab one
    private void lookNewEntryOnTabOne () {

        // look for new entrys in db on tab one
        if ((subfunction_arrangement_sketchcomment && myDb.getCountAllNewEntryOurArrangementSketchComment(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0")) > 0) || (subfunction_arrangement_sketch && myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis()), "sketch") > 0)) {
            infoNewEntryOnTabOne = true;
            infoTextNewEntryPostFixTabOneTitle = " "+ this.getResources().getString(R.string.newEntryText);
        }
        else {
            infoNewEntryOnTabOne = false;
            infoTextNewEntryPostFixTabOneTitle = "";
        }
    }

    // set/ unset textcolor for tab title on tab zero
    private void setUnsetTextColorSignalNewTabZero (Boolean colorSet) {

        int tmpTextColor;

        if (colorSet) {
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.text_accent_color);
        }
        else {
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.colorAccent);
        }

        // Change tab text color on tab zero
        ViewGroup vg = (ViewGroup) tabLayoutOurArrangement.getChildAt(0);
        ViewGroup vgTab = (ViewGroup) vg.getChildAt(0); //Tab Zero
        int tabChildsCount = vgTab.getChildCount();
        for (int i=0; i<tabChildsCount; i++) {
            View tabViewCild = vgTab.getChildAt(i);
            if (tabViewCild instanceof TextView) {
                ((TextView) tabViewCild).setTextColor(tmpTextColor);
            }
        }
    }


    // set/ unset textcolor for tab title on tab one
    private void setUnsetTextColorSignalNewTabOne (Boolean colorSet) {

        int tmpTextColor;

        if (colorSet) {
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.text_accent_color);
        }
        else {
            tmpTextColor = ContextCompat.getColor(ActivityOurArrangement.this, R.color.colorAccent);
        }

        // Change tab text color on tab zero
        ViewGroup vg = (ViewGroup) tabLayoutOurArrangement.getChildAt(0);
        ViewGroup vgTab = (ViewGroup) vg.getChildAt(1); //Tab One
        int tabChildsCount = vgTab.getChildCount();
        for (int i=0; i<tabChildsCount; i++) {
            View tabViewCild = vgTab.getChildAt(i);
            if (tabViewCild instanceof TextView) {
                ((TextView) tabViewCild).setTextColor(tmpTextColor);
            }
        }
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


    // getter for DB-Id of arrangement
    public int getArrangementDbIdFromLink () {

        return arrangementServerDbIdFromLink;
    }


    // getter for DB-Id of sketch arrangement
    public int getSketchArrangementDbIdFromLink () {

        return arrangementSketchDbIdFromLink;
    }


    // getter for arrangement number in listview
    public int getArrangementNumberInListview () {

        return arrangementNumberInListView;
    }


    // getter for sketch arrangement number in listview
    public int getSketchArrangementNumberInListview () {

        return arrangementSketchNumberInListView;
    }


    // getter for evaluate next arrangement
    public boolean getEvaluateNextArrangement () {

        return evaluateNextArrangement;
    }


    // getter for fab view
    public FloatingActionButton getFabViewOurArrangement () {

        return ourArrangementFabView;
    }


    // getter for border for comments
    public boolean isCommentLimitationBorderSet (String currentSketch) {

        switch (currentSketch) {
            case "current":
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) < ConstansClassOurArrangement.commentLimitationBorder) { // is there a border for comments
                    return true; // comments are limited!
                }
                break;
            case "sketch":
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) < ConstansClassOurArrangement.commentLimitationBorder) { // is there a border for sketch comments
                    return true; // sketch comments are limited!
                }
                break;
        }

        return  false; // write infinitely comments!
    }


    // setter for subtitle in OurArrangement toolbar
    public void setOurArrangementToolbarSubtitle (String subtitleText, String subtitleChoose) {

        switch (subtitleChoose) {

            case "sketch":
               arraySubTitleText[1] = subtitleText;
               break;
            case "old":
               arraySubTitleText[2] = subtitleText;
               break;
            case "now":
               arraySubTitleText[0] = subtitleText;
               break;
            case "nowComment":
                arraySubTitleText[3] = subtitleText;
                break;
            case "showComment":
                arraySubTitleText[4] = subtitleText;
                break;
            case "evaluate":
                arraySubTitleText[5] = subtitleText;
                break;
            case "sketchComment":
                arraySubTitleText[6] = subtitleText;
                break;
            case "showSketchComment":
                arraySubTitleText[7] = subtitleText;
                break;
        }

        // first time -> set initial subtitle
        if (setSubtitleFirstTime && subtitleChoose.equals("now")) {
            toolbar.setSubtitle(subtitleText);
            setSubtitleFirstTime = false;
        }
    }


    // setter for visibility in OurArrangement FAB in fragment
    public void setOurArrangementFABVisibility (String visibility, String fragment) {

        switch (fragment) {

            case "sketch":
                arrayShowOrHideFAB[1] = visibility;
                break;
            case "old":
                arrayShowOrHideFAB[2] = visibility;
                break;
            case "now":
                arrayShowOrHideFAB[0] = visibility;
                break;
            case "nowComment":
                arrayShowOrHideFAB[3] = visibility;
                break;
            case "showComment":
                arrayShowOrHideFAB[4] = visibility;
                break;
            case "evaluate":
                arrayShowOrHideFAB[5] = visibility;
                break;
            case "sketchComment":
                arrayShowOrHideFAB[6] = visibility;
                break;
            case "showSketchComment":
                arrayShowOrHideFAB[7] = visibility;
                break;
        }

        // first time -> set initial visibility of fab
        if (setFabFirstTime && fragment.equals("now")) {
            showOrHideFAB(visibility);
            setFabFirstTime = false;
        }

    }


    // setter for click listener in OurArrangement FAB in fragment
    public void setOurArrangementFABClickListener (ArrayList<ObjectSmartEFBArrangement> objectArrayArrangement, String fragment, String intentOrder) {

        switch (fragment) {

            case "sketch":
                arrayListOfArrangementSketch = objectArrayArrangement;
                arrayFragmentNameForClickListener[1] = fragment;
                arrayFragmentIntentOrderForClickListener[1] = intentOrder;
                break;
            case "old":
                arrayFragmentNameForClickListener[2] = fragment;
                arrayFragmentIntentOrderForClickListener[2] = intentOrder;
                break;
            case "now":
                arrayListOfArrangementNow = objectArrayArrangement;
                arrayFragmentNameForClickListener[0] = fragment;
                arrayFragmentIntentOrderForClickListener[0] = intentOrder;
                break;
            case "nowComment":
                arrayFragmentNameForClickListener[3] = fragment;
                arrayFragmentIntentOrderForClickListener[3] = intentOrder;
                break;
            case "showComment":
                arrayListOfArrangementShowNowComment = objectArrayArrangement;
                arrayFragmentNameForClickListener[4] = fragment;
                arrayFragmentIntentOrderForClickListener[4] = intentOrder;
                break;
            case "evaluate":
                arrayFragmentNameForClickListener[5] = fragment;
                arrayFragmentIntentOrderForClickListener[5] = intentOrder;
                break;
            case "sketchComment":
                arrayFragmentNameForClickListener[6] = fragment;
                arrayFragmentIntentOrderForClickListener[6] = intentOrder;
                break;
            case "showSketchComment":
                arrayListOfArrangementShowSketchComment = objectArrayArrangement;
                arrayFragmentNameForClickListener[7] = fragment;
                arrayFragmentIntentOrderForClickListener[7] = intentOrder;
                break;
        }

        // first time -> set initial click listener of fab
        if (setFabClickListenerFirstTime && fragment.equals("now")) {
            addOnClickListenerToFABForFragmentNow(objectArrayArrangement, "now", "comment_an_arrangement");
            setFabClickListenerFirstTime = false;
        }

        // new data for fragment -> refresh fab click listener
        if (setFabClickListenerRefreshTimeForNow && fragment.equals("now")) {
            addOnClickListenerToFABForFragmentNow(objectArrayArrangement, "now", "comment_an_arrangement");
            setFabClickListenerRefreshTimeForNow = false;
        }
        if (setFabClickListenerRefreshTimeForSketch && fragment.equals("sketch")) {
            addOnClickListenerToFABForFragmentNow(objectArrayArrangement, "sketch", "comment_an_sketch_arrangement");
            setFabClickListenerRefreshTimeForSketch = false;
        }

    }


    // check prefs for update now and sketch arrangement or only now arrangements or only sketch?
    public void checkUpdateForShowDialog (String fragmentName) {

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsSignalNowArrangementUpdate, false) && prefs.getBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false)) {

            // set signal arrangements and sketch arrangements are update to false; because user is informed by dialog!
            prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalNowArrangementUpdate, false);
            prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false);
            prefsEditor.apply();

            // update fab listener
            refreshFABOnClickListenerAndVisibility("now");
            refreshFABOnClickListenerAndVisibility("sketch");

            // set correct tab 0 and tab 1 color and text
            setTabZeroTitleAndColor ();
            setTabOneTitleAndColor();

            // show dialog arrangement and sketch arrangement change
            alertDialogArrangementChange("currentSketch");

        }
        else if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsSignalNowArrangementUpdate, false) && fragmentName.equals("now")) {
            // set signal arrangements are update to false; because user is informed by dialog!
            prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalNowArrangementUpdate, false);
            prefsEditor.apply();

            // update fab listener
            refreshFABOnClickListenerAndVisibility("now");

            // set correct tab 0 color and text
            setTabZeroTitleAndColor ();

            // show dialog arrangement change
            alertDialogArrangementChange("current");

        } else if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false) && fragmentName.equals("sketch")) {
            // set signal sketch arrangements are update to false; because user is informed by dialog!
            prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false);
            prefsEditor.apply();

            // update fab listener
            refreshFABOnClickListenerAndVisibility("sketch");

            // set correct tab 1 color and text
            setTabOneTitleAndColor();

            // show dialog arrangement change
            alertDialogArrangementChange("sketch");
        }
    }


    public void alertDialogArrangementChange (String whatDialog) {

        LayoutInflater dialogInflater;

        String tmpTextCloseDialog = "";
        String tmpTextTitleDialog = "";
        String infoTextForChange = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOurArrangement.this);

        // Get the layout inflater
        dialogInflater = (LayoutInflater) ActivityOurArrangement.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate and get the view
        View dialogSettings = dialogInflater.inflate(R.layout.dialog_info_arrangement_change, null);

        // get textview for info-text from view
        TextView textViewArrangement = dialogSettings.findViewById(R.id.dialogOurArrangementArrangementChangeInfoText);

        switch (whatDialog) {

            case "current": // dialog for update now arrangement
                // get string ressources
                tmpTextCloseDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementArrangementChangeCloseButton);
                tmpTextTitleDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementArrangementChangeHeadline);
                // textview for the dialog text -> arrangement change!
                infoTextForChange = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementArrangementChangeInfoText);
                textViewArrangement.setText(infoTextForChange);
                break;

            case "sketch": // dialog for update sketch arrangement
                // get string ressources
                tmpTextCloseDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSketchArrangementChangeCloseButton);
                tmpTextTitleDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSketchArrangementChangeHeadline);
                // textview for the dialog text -> arrangement change!
                infoTextForChange = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSketchArrangementChangeInfoText);
                textViewArrangement.setText(infoTextForChange);
                break;

            case "currentSketch": // // dialog for update now and sketch arrangement
                // get string ressources
                tmpTextCloseDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSketchArrangementChangeCloseButton);
                tmpTextTitleDialog = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementCurrentSketchArrangementChangeHeadline);
                // textview for the dialog text -> arrangement change!
                infoTextForChange = ActivityOurArrangement.this.getResources().getString(R.string.textDialogOurArrangementSketchAndCurrentArrangementChangeInfoText);
                textViewArrangement.setText(infoTextForChange);
                break;
        }

        // build the dialog
        builder.setView(dialogSettings);

        // add title
        builder.setTitle(tmpTextTitleDialog);

        // Add close button
        builder.setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialogArrangementChange.cancel();
            }
        });

        // and create
        alertDialogArrangementChange = builder.create();

        // change color and style of negativ button
        alertDialogArrangementChange.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // change background and text color of button
                Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                // Change negative button text and background color
                negativeButton.setTextColor(ContextCompat.getColor(ActivityOurArrangement.this, R.color.white));
                negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
            }
        });

        // show dialog
        alertDialogArrangementChange.show();
    }


    public void refreshFABOnClickListenerAndVisibility (String fragmentName) {

        switch (fragmentName) {

            case "sketch":
                arrayListOfArrangementSketch = null;
                arrayFragmentNameForClickListener[1] = "";
                arrayFragmentIntentOrderForClickListener[1] = "";
                arrayFragmentNameForClickListener[6] = ""; // array index of sketchComment
                arrayFragmentIntentOrderForClickListener[6] = ""; // array index of sketchComment
                arrayListOfArrangementShowSketchComment = null; // array index of showSketchComment
                arrayFragmentNameForClickListener[7] = ""; // array index of showSketchComment
                arrayFragmentIntentOrderForClickListener[7] = ""; // array index of showSketchComment
                setFabClickListenerRefreshTimeForSketch = true;

            break;

            case "now":
                arrayListOfArrangementNow = null;
                arrayFragmentNameForClickListener[0] = "";
                arrayFragmentIntentOrderForClickListener[0] = "";
                arrayFragmentNameForClickListener[3] = ""; // array index of nowComment
                arrayFragmentIntentOrderForClickListener[3] = ""; // array index of nowComment
                arrayListOfArrangementShowNowComment = null;
                arrayFragmentNameForClickListener[4] = ""; // array index of showComment
                arrayFragmentIntentOrderForClickListener[4] = ""; // array index of showComment
                arrayFragmentNameForClickListener[5] = ""; // array index of evaluate
                arrayFragmentIntentOrderForClickListener[5] = ""; // array index of evaluate
                setFabClickListenerRefreshTimeForNow = true;

            break;
        }
    }


    // setter for visibility fab (hide or show)
    public void showOrHideFAB (String hideOrShow) {

        switch (hideOrShow) {
            case "show":
                ourArrangementFabView.show();
                break;
            case "hide":
                ourArrangementFabView.hide();
                break;
            default:
                ourArrangementFabView.hide();
                break;
        }

    }

    // add correct onclicklistener for fab in fragment
    public void addOnClickListenerToFABForFragmentNow (final ArrayList<ObjectSmartEFBArrangement> arrayList, final String fragmentName, final String intentOrder) {

        // add on click listener to fab
        ourArrangementFabView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (arrayList.size() > 1) {

                    // create popup menu for fab
                    PopupMenu popupFabCommentArrangement = new PopupMenu(ActivityOurArrangement.this, view);

                    // inflate popup menu for fab
                    if (fragmentName.equals("sketch") || fragmentName.equals("showSketchComment")) {
                        popupFabCommentArrangement.getMenuInflater().inflate(R.menu.popup_efb_our_arrangement_sketch_arrangement_fab, popupFabCommentArrangement.getMenu());
                    }
                    else {
                        popupFabCommentArrangement.getMenuInflater().inflate(R.menu.popup_efb_our_arrangement_now_arrangement_fab, popupFabCommentArrangement.getMenu());
                    }

                    // set on click listener for popup menu item
                    popupFabCommentArrangement.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        Intent intent = new Intent(ActivityOurArrangement.this, ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com", intentOrder);

                        // generate correct db id value for intent
                        int countIndexArrayList = 0;
                        int offsetIndexArrayList = 1;
                        int [] listOfDbId = {0,0,0,0,0,0,0,0,0,0,0,0};
                        if (fragmentName.equals("sketch")) {
                            if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchList, "descending").equals("descending")) {
                                countIndexArrayList = arrayList.size() - 1;
                                offsetIndexArrayList = -1;
                            } else {
                                countIndexArrayList = 0;
                                offsetIndexArrayList = 1;
                            }
                        }
                        else {
                            if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "descending").equals("descending")) {
                                countIndexArrayList = arrayList.size() - 1;
                                offsetIndexArrayList = -1;
                            } else {
                                countIndexArrayList = 0;
                                offsetIndexArrayList = 1;
                            }
                        }
                        for (int c = 0; c < arrayList.size(); c++) {
                            listOfDbId[c] = arrayList.get(countIndexArrayList).getServerIdArrangement();
                            countIndexArrayList = countIndexArrayList + offsetIndexArrayList;
                        }

                        switch (item.getItemId()) {
                            case R.id.arrangement1:
                                intent.putExtra("db_id", listOfDbId[0]);
                                intent.putExtra("arr_num", 1);
                                break;
                            case R.id.arrangement2:
                                if (1 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[1]);
                                    intent.putExtra("arr_num", 2);
                                }
                                break;
                            case R.id.arrangement3:
                                if (2 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[2]);
                                    intent.putExtra("arr_num", 3);
                                }
                                break;
                            case R.id.arrangement4:
                                if (3 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[3]);
                                    intent.putExtra("arr_num", 4);
                                }
                                break;

                            case R.id.arrangement5:
                                if (4 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[4]);
                                    intent.putExtra("arr_num", 5);
                                }
                                break;
                            case R.id.arrangement6:
                                if (5 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[5]);
                                    intent.putExtra("arr_num", 6);
                                }
                                break;
                            case R.id.arrangement7:
                                if (6 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[6]);
                                    intent.putExtra("arr_num", 7);
                                }
                                break;
                            case R.id.arrangement8:
                                if (7 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[7]);
                                    intent.putExtra("arr_num", 8);
                                }
                                break;
                            case R.id.arrangement9:
                                if (8 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[8]);
                                    intent.putExtra("arr_num", 9);
                                }
                                break;
                            case R.id.arrangement10:
                                if (9 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[9]);
                                    intent.putExtra("arr_num", 10);
                                }
                                break;
                            case R.id.arrangement11:
                                if (10 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[10]);
                                    intent.putExtra("arr_num", 11);
                                }
                                break;
                            case R.id.arrangement12:
                                if (11 < arrayList.size()) {
                                    intent.putExtra("db_id", listOfDbId[11]);
                                    intent.putExtra("arr_num", 12);
                                }
                                break;
                            }

                            // start comment choosen arrangement
                            ActivityOurArrangement.this.startActivity(intent);

                            return true;
                        }
                    });

                    // disable menu entry not used
                    int resId;
                    String ressourceArrangementString = "arrangement";

                    for (int t = arrayList.size()+1; t <= 12; t++) {
                        resId = getResources().getIdentifier(ressourceArrangementString + t, "id", ActivityOurArrangement.this.getPackageName());
                        // set popup menu entrys gone, when not needed
                        popupFabCommentArrangement.getMenu().findItem(resId).setVisible(false);
                    }

                    // show popup menu
                    popupFabCommentArrangement.show();

                }
                else {
                    // only one arrangement now/sketch in db

                    Intent intent = new Intent(ActivityOurArrangement.this, ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com", intentOrder);
                    intent.putExtra("db_id", arrayList.get(0).getServerIdArrangement());
                    intent.putExtra("arr_num", 1);
                    // start comment for this arrangement (now/ sketch)
                    ActivityOurArrangement.this.startActivity(intent);
                }
            }
        });
    }

}