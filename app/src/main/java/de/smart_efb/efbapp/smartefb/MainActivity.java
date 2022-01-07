package de.smart_efb.efbapp.smartefb;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    // grid view adapter
    mainMenueGridViewApdapter mainMenueGridViewApdapter;

    // title of main Menue Elements
    private String[] mainMenueElementTitle = new String [ConstansClassMain.mainMenueNumberOfElements];
    // color of active grid element
    private String[] mainMenueElementColor = new String [ConstansClassMain.mainMenueNumberOfElements];
    // color of inactive element
    private String[] mainMenueElementColorLight = new String [ConstansClassMain.mainMenueNumberOfElements];

    // background ressource of normal elements (image icon)
    private int[] mainMenueElementBackgroundRessources = new int[ConstansClassMain.mainMenueNumberOfElements];
    // background ressource of new entry elements (image icon)
    private int[] mainMenueElementBackgroundRessourcesNewEntry = new int[ConstansClassMain.mainMenueNumberOfElements];
    // background ressource of attention entry elements (image icon)
    private int[] mainMenueElementBackgroundRessourcesAttentionEntry = new int[ConstansClassMain.mainMenueNumberOfElements];
    // background ressource of elemts to show!
    private int[] mainMenueShowElementBackgroundRessources = new int[ConstansClassMain.mainMenueNumberOfElements];

    // show the menue element
    private boolean[] showMainMenueElement = new boolean[ConstansClassMain.mainMenueNumberOfElements];

    // context of main
    Context mainContext;

    // point to shared preferences
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // reference to the DB
    DBAdapter myDb;

    // activ/inactiv sub-functions
    // our arrangements sub functions activ/ inactiv
    Boolean subfunction_arrangement_comment = false;
    Boolean subfunction_arrangement_evaluation = false;
    Boolean subfunction_arrangement_sketch = false;
    Boolean subfunction_arrangement_sketchcomment = false;
    // our goals sub functions activ/ inactiv
    Boolean subfunction_goals_comment = false;
    Boolean subfunction_goals_evaluation = false;
    Boolean subfunction_goals_debetable = false;
    Boolean subfunction_goals_debetablecomment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_main);

        // register lifecycle counter for Application
        getApplication().registerActivityLifecycleCallbacks(new EfbLifecycle());

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(mainActivityBrodcastReceiver, filter);

        // init notification channel and importance
        initNotificationChannelForApp();

        // init the elements arrays (title, color, colorLight, backgroundImage)
        initMainMenueElementsArrays();

        // create background ressources to show in grid
        createMainMenueElementBackgroundRessources();

        GridView gridview = (GridView) findViewById(R.id.mainMenueGridView);

        mainMenueGridViewApdapter = new mainMenueGridViewApdapter(this);

        gridview.setAdapter(mainMenueGridViewApdapter);
        gridview.setNumColumns(ConstansClassMain.numberOfGridColumns);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (showMainMenueElement[position]) {

                    Intent intent;

                    switch (position) {

                        case 0: // grid "uebergabe"
                            intent = new Intent(mainContext, ActivityConnectBook.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 1: // grid "absprachen"
                            intent = new Intent(mainContext, ActivityOurArrangement.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 2: // grid "ziele"
                            intent = new Intent(mainContext, ActivityOurGoals.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 3: // grid "nachrichten"
                            intent = new Intent(getApplicationContext(), ActivityMessage.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 4: // grid "termine"
                            intent = new Intent(getApplicationContext(), ActivityMeeting.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 5: // grid "zeitplan"
                            intent = new Intent(getApplicationContext(), ActivityTimeTable.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 6: // grid "praevention"
                            intent = new Intent(getApplicationContext(), ActivityPrevention.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 7: // grid "faq"
                            intent = new Intent(getApplicationContext(), ActivityFaq.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 8: // grid "hilfe"
                            intent = new Intent(getApplicationContext(), ActivityEmergencyHelp.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 9:
                            // grid "einstellungen"
                            intent = new Intent(getApplicationContext(), ActivitySettingsEfb.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

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


    @Override
    public void onStart() {

        super.onStart();

        // new alarm manager service for start all needed alarms
        EfbSetAlarmManager efbSetAlarmManager = new EfbSetAlarmManager(this);

        // start exchange service with intent
        efbSetAlarmManager.setAlarmForExchangeService();

        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // start check meeting remember alarm manager, when function meeting is on and case is not closed
            if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, false)) {
                efbSetAlarmManager.setAlarmManagerForRememberMeeting();
            }
            // start check our arrangement alarm manager, when function our arrangement is on and case is not closed
            if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false)) {
                efbSetAlarmManager.setAlarmManagerForOurArrangementEvaluation();
            }
            // start check our goals alarm manager, when function our goals is on and case is not closed
            if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false)) {
                efbSetAlarmManager.setAlarmManagerForOurGoalsEvaluation();
            }
        }

        // init array show elements
        initShowElementArray();

        // create background ressources to show in grid
        if (createMainMenueElementBackgroundRessources()) { // new things in grid?

            mainMenueGridViewApdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // de-register broadcast receiver
        this.unregisterReceiver(mainActivityBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver mainActivityBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the main view
            Boolean updateMainView = false;

            // true-> update the main menue background array
            Boolean updateMainMenueArray = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // check intent order
                // new connect book message
                String tmpExtraConnectBook = intentExtras.getString("ConnectBook","0");
                String tmpExtraConnectBookMainActivityUpdateView = intentExtras.getString("ConnectBookMainActivityUpdateView","0");
                String tmpExtraConnectBookSettingsMainActivityUpdateView = intentExtras.getString("ConnectBookSettingsMainActivityUpdateView","0");

                // new time table value
                String tmpExtraTimeTable = intentExtras.getString("TimeTable","0");
                String tmpExtraTimeTableSettingMainActivityUpdateView = intentExtras.getString("TimeTableSettingMainActivityUpdateView","0");
                String tmpExtraTimeTableNewValueMainActivityUpdateView = intentExtras.getString("TimeTableNewValueMainActivityUpdateView","0");

                // new meeting/ suggestion
                String tmpExtraMeeting = intentExtras.getString("Meeting","0");
                String tmpExtraMeetingNewMeetingMainActivityUpdateView = intentExtras.getString("MeetingNewMeetingMainActivityUpdateView","0");
                String tmpExtraMeetingCancelMeetingMainActivityUpdateView = intentExtras.getString("MeetingCancelMeetingMainActivityUpdateView","0");
                String tmpExtraMeetingNewSuggestionMainActivityUpdateView = intentExtras.getString("MeetingNewSuggestionMeetingMainActivityUpdateView","0");
                String tmpExtraMeetingCancelSuggestionMainActivityUpdateView = intentExtras.getString("MeetingCancelSuggestionMainActivityUpdateView","0");
                String tmpExtraMeetingFoundSuggestionMainActivityUpdateView = intentExtras.getString("MeetingFoundSuggestionMainActivityUpdateView","0");
                String tmpExtraMeetingNewInvitationMainActivityUpdateView = intentExtras.getString("MeetingNewInvitationMainActivityUpdateView","0");
                String tmpExtraMeetingInvitationFoundClientMainActivityUpdateView = intentExtras.getString("MeetingInvitationFoundClientMainActivityUpdateView","0");
                String tmpExtraMeetingInvitationCancelMainActivityUpdateView = intentExtras.getString("MeetingInvitationCancelMainActivityUpdateView","0");
                String tmpExtraMeetingSettingMainActivityUpdateView = intentExtras.getString("MeetingSettingMainActivityUpdateView","0");

                // case is close or other menue items chnage
                String tmpExtraSettings = intentExtras.getString("Settings","0");
                //String tmpExtraSettingsOtherMenueItems = intentExtras.getString("SettingsOtherMenueItems","0");
                String tmpExtraCaseClose = intentExtras.getString("Case_close","0");

                // Settings arrangement or new arrangement change
                String tmpExtraOurArrangement = intentExtras.getString("OurArrangement","0");
                String tmpExtraOurArrangementNowMainActivityUpdateView = intentExtras.getString("OurArrangementNowMainActivityUpdateView","0");
                String tmpExtraOurArrangementNowCommentMainActivityUpdateView = intentExtras.getString("OurArrangementNowCommentMainActivityUpdateView","0");
                String tmpExtraOurArrangementSketchMainActivityUpdateView = intentExtras.getString("OurArrangementSketchMainActivityUpdateView","0");
                String tmpExtraOurArrangementSketchCommentMainActivityUpdateView = intentExtras.getString("OurArrangementSketchCommentMainActivityUpdateView","0");
                String tmpExtraOurArrangementSettingsMainActivityUpdateView = intentExtras.getString("OurArrangementSettingsMainActivityUpdateView","0");

                // Settings goal or new goals change
                String tmpExtraOurGoal = intentExtras.getString("OurGoals","0");
                String tmpExtraOurGoalJointlyMainActivityUpdateView = intentExtras.getString("OurGoalsJointlyMainActivityUpdateView","0");
                String tmpExtraOurGoalJointlyCommentMainActivityUpdateView = intentExtras.getString("OurGoalsJointlyCommentMainActivityUpdateView","0");
                String tmpExtraOurGoalDebetableMainActivityUpdateView = intentExtras.getString("OurGoalsDebetableMainActivityUpdateView","0");
                String tmpExtraOurGoalDebetableCommentMainActivityUpdateView = intentExtras.getString("OurGoalsDebetableCommentMainActivityUpdateView","0");
                String tmpExtraOurGoalSettingsMainActivityUpdateView = intentExtras.getString("OurGoalsSettingsMainActivityUpdateView","0");

                // New Message or Settings in Messages
                String tmpExtraMessages = intentExtras.getString("MessagesMessage","0");
                String tmpExtraMessagesMainActivityUpdateView = intentExtras.getString("MessagesMessageMainActivityUpdateView","0");
                String tmpExtraMessagesSettingsMainActivityUpdateView = intentExtras.getString("MessagesSettingsMainActivityUpdateView","0");

                // prevention set on or off
                String tmpExtraPrevention = intentExtras.getString("PreventionPrevention","0");
                String tmpExtraPreventionSettingsMainActivityUpdateView = intentExtras.getString("PreventionSettingMainActivityUpdateView","0");

                // faq set on or off
                String tmpExtraFaq = intentExtras.getString("FaqFaq","0");
                String tmpExtraFaqSettingsMainActivityUpdateView = intentExtras.getString("FaqSettingMainActivityUpdateView","0");

                // emergency set on or off
                String tmpExtraEmergency = intentExtras.getString("EmergencyEmergency","0");
                String tmpExtraEmergencySettingsMainActivityUpdateView = intentExtras.getString("EmergencySettingMainActivityUpdateView","0");

                // setting set on or off
                String tmpExtraSetting = intentExtras.getString("SettingSetting","0");
                String tmpExtraSettingSettingsMainActivityUpdateView = intentExtras.getString("SettingSettingMainActivityUpdateView","0");

                if (tmpExtraSettings != null && tmpExtraSettings.equals("1") && tmpExtraCaseClose != null && tmpExtraCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = MainActivity.this.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for arrangement changes
                if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettingsMainActivityUpdateView != null && tmpExtraOurArrangementSettingsMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNowMainActivityUpdateView != null && tmpExtraOurArrangementNowMainActivityUpdateView.equals("1")) {
                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNowCommentMainActivityUpdateView != null && tmpExtraOurArrangementNowCommentMainActivityUpdateView.equals("1")) {
                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketchMainActivityUpdateView != null && tmpExtraOurArrangementSketchMainActivityUpdateView.equals("1")) {
                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketchCommentMainActivityUpdateView != null && tmpExtraOurArrangementSketchCommentMainActivityUpdateView.equals("1")) {
                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for goals changes
                if (tmpExtraOurGoal != null && tmpExtraOurGoal.equals("1") && tmpExtraOurGoalSettingsMainActivityUpdateView != null && tmpExtraOurGoalSettingsMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurGoal != null && tmpExtraOurGoal.equals("1") && tmpExtraOurGoalJointlyMainActivityUpdateView != null && tmpExtraOurGoalJointlyMainActivityUpdateView.equals("1")) {
                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurGoal != null && tmpExtraOurGoal.equals("1") && tmpExtraOurGoalJointlyCommentMainActivityUpdateView != null && tmpExtraOurGoalJointlyCommentMainActivityUpdateView.equals("1")) {
                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurGoal != null && tmpExtraOurGoal.equals("1") && tmpExtraOurGoalDebetableMainActivityUpdateView != null && tmpExtraOurGoalDebetableMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraOurGoal != null && tmpExtraOurGoal.equals("1") && tmpExtraOurGoalDebetableCommentMainActivityUpdateView != null && tmpExtraOurGoalDebetableCommentMainActivityUpdateView.equals("1")) {
                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for messages changes
                if (tmpExtraMessages != null && tmpExtraMessages.equals("1") && tmpExtraMessagesSettingsMainActivityUpdateView != null && tmpExtraMessagesSettingsMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMessages != null && tmpExtraMessages.equals("1") && tmpExtraMessagesMainActivityUpdateView != null && tmpExtraMessagesMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for connect book changes
                if (tmpExtraConnectBook != null && tmpExtraConnectBook.equals("1") && tmpExtraConnectBookSettingsMainActivityUpdateView != null && tmpExtraConnectBookSettingsMainActivityUpdateView.equals("1")) {

                    // update show elements for menu
                    initShowElementArray ();

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraConnectBook != null && tmpExtraConnectBook.equals("1") && tmpExtraConnectBookMainActivityUpdateView != null && tmpExtraConnectBookMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for meeting changes
                if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingSettingMainActivityUpdateView != null && tmpExtraMeetingSettingMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingNewMeetingMainActivityUpdateView != null && tmpExtraMeetingNewMeetingMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingCancelMeetingMainActivityUpdateView != null && tmpExtraMeetingCancelMeetingMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingNewSuggestionMainActivityUpdateView != null && tmpExtraMeetingNewSuggestionMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingCancelSuggestionMainActivityUpdateView != null && tmpExtraMeetingCancelSuggestionMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingFoundSuggestionMainActivityUpdateView != null && tmpExtraMeetingFoundSuggestionMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingNewInvitationMainActivityUpdateView != null && tmpExtraMeetingNewInvitationMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingInvitationFoundClientMainActivityUpdateView != null && tmpExtraMeetingInvitationFoundClientMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingInvitationCancelMainActivityUpdateView != null && tmpExtraMeetingInvitationCancelMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for time table
                if (tmpExtraTimeTable != null && tmpExtraTimeTable.equals("1") && tmpExtraTimeTableSettingMainActivityUpdateView != null && tmpExtraTimeTableSettingMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }
                else if (tmpExtraTimeTable != null && tmpExtraTimeTable.equals("1") && tmpExtraTimeTableNewValueMainActivityUpdateView != null && tmpExtraTimeTableNewValueMainActivityUpdateView.equals("1")) {

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for prevention
                if (tmpExtraPrevention != null && tmpExtraPrevention.equals("1") && tmpExtraPreventionSettingsMainActivityUpdateView != null && tmpExtraPreventionSettingsMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for faq
                if (tmpExtraFaq != null && tmpExtraFaq.equals("1") && tmpExtraFaqSettingsMainActivityUpdateView != null && tmpExtraFaqSettingsMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for emergency
                if (tmpExtraEmergency != null && tmpExtraEmergency.equals("1") && tmpExtraEmergencySettingsMainActivityUpdateView != null && tmpExtraEmergencySettingsMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }

                // update main activity view for emergency
                if (tmpExtraSetting != null && tmpExtraSetting.equals("1") && tmpExtraSettingSettingsMainActivityUpdateView != null && tmpExtraSettingSettingsMainActivityUpdateView.equals("1")) {

                    // update main menue background array
                    updateMainMenueArray = true;

                    // refresh view
                    updateMainView = true;
                }
            }

            // update show elements for menu
            if  (updateMainMenueArray) {
                initShowElementArray ();
            }

            // update the main view
            if (updateMainView) {
                updateMainView();
            }
        }
    };


    public void updateMainView () {

        // check for grid updates
        if (createMainMenueElementBackgroundRessources()) { // new things in grid?
            mainMenueGridViewApdapter.notifyDataSetChanged();
        }

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    // init the elements arrays (title, color, colorLight, backgroundImage)
    private void initMainMenueElementsArrays() {

        String[] tmpBackgroundRessources, tmpBackgroundRessourcesNewEntry, tmpBackgroundRessourcesAttentionEntry;

        // init the context
        mainContext = this;

        // get the shared preferences
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // init the DB
        myDb = new DBAdapter(this);

        // check installation status (new or update)
        newOrUpdateInstallation();

        mainMenueElementTitle = getResources().getStringArray(R.array.mainMenueElementTitle);

        mainMenueElementColor = getResources().getStringArray(R.array.mainMenueElementColor);

        mainMenueElementColorLight = getResources().getStringArray(R.array.mainMenueElementColorLight);

        tmpBackgroundRessources = getResources().getStringArray(R.array.mainMenueElementImage);
        tmpBackgroundRessourcesNewEntry = getResources().getStringArray(R.array.mainMenueElementImageNewEntry);
        tmpBackgroundRessourcesAttentionEntry = getResources().getStringArray(R.array.mainMenueElementImageAttentionEntry);

        for (int i=0; i<ConstansClassMain.mainMenueNumberOfElements; i++) {
            mainMenueElementBackgroundRessources[i] = getResources().getIdentifier(tmpBackgroundRessources[i], "drawable", "de.smart_efb.efbapp.smartefb");
            mainMenueElementBackgroundRessourcesNewEntry[i] = getResources().getIdentifier(tmpBackgroundRessourcesNewEntry[i], "drawable", "de.smart_efb.efbapp.smartefb");
            mainMenueElementBackgroundRessourcesAttentionEntry[i] = getResources().getIdentifier(tmpBackgroundRessourcesAttentionEntry[i], "drawable", "de.smart_efb.efbapp.smartefb");
        }

        // init array show elements and activ/inactiv sub-functions
        initShowElementArray();
    }


    // init array show elements
    private void initShowElementArray () {


        /*
        // set for debuging -> change time for evaluation arrangement and/ or goals
        // arrangement evaluation
        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, 360);
        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, 360);
        // goals evaluation
        prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, 360);
        prefsEditor.putInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, 360);
        prefsEditor.apply();
        */

        for (int i=0; i<ConstansClassMain.mainMenueNumberOfElements; i++) {

            String tmpMainMenueElementName = ConstansClassMain.namePrefsSubstringMainMenueElementId + i;

            showMainMenueElement[i] = false;
            if (prefs.getBoolean(tmpMainMenueElementName, false)) {
                showMainMenueElement[i] = true;
            }
        }

        // our arrangements sub functions activ/ inactiv
        subfunction_arrangement_comment = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false);
        subfunction_arrangement_evaluation = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false);
        subfunction_arrangement_sketch = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false);
        subfunction_arrangement_sketchcomment = prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, false);

        // our goals sub functions activ/ inactiv
        subfunction_goals_comment = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, false);
        subfunction_goals_evaluation = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false);
        subfunction_goals_debetable = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false);
        subfunction_goals_debetablecomment = prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, false);
    }


    // creates the background ressources for the grid (like new entry or normal image)
    private boolean createMainMenueElementBackgroundRessources () {

        boolean tmpNew = false;

        for (int countElements=0; countElements < ConstansClassMain.mainMenueNumberOfElements; countElements++) {
            switch (countElements) {

                case 0: // menue item "Uebergabe"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (myDb.getCountNewEntryConnectBookMessage() > 0) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 1: // menue item "Absprachen"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if ((subfunction_arrangement_sketchcomment && myDb.getCountAllNewEntryOurArrangementSketchComment(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0")) > 0) || ( subfunction_arrangement_comment && myDb.getCountAllNewEntryOurArrangementComment(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0")) > 0) || myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "current") > 0 || (subfunction_arrangement_sketch && myDb.getCountNewEntryOurArrangement(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis()), "sketch") > 0)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 2: // menue item "Ziele"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (myDb.getCountNewEntryOurGoals(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "jointly") > 0 || (subfunction_goals_comment && myDb.getCountAllNewEntryOurGoalsJointlyGoalsComment(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis())) > 0) || (subfunction_goals_debetable && myDb.getCountNewEntryOurGoals(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis()), "debetable") > 0) || (subfunction_goals_debetablecomment && myDb.getCountAllNewEntryOurGoalsDebetableGoalsComment(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis())) > 0)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 3: // menue item "Nachrichten"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (myDb.getCountNewEntryMessage() > 0) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                            ;
                        }
                        tmpNew = true;
                    }
                    break;

                case 4: // menue item "Termine"
                    // delete all meeting/ suggestion mark with new but never show (time expired, etc.)
                    Long nowTime = System.currentTimeMillis();
                    myDb.deleteStatusNewEntryAllOldMeetingAndSuggestion (nowTime);
                    if (myDb.getCountNewEntryMeetingAndSuggestion("all") > 0 ) {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                    } else {
                        Cursor cSuggest = myDb.getAllRowsMeetingsAndSuggestion("suggestion_for_show_attention", nowTime);
                        Cursor cClientSuggest = myDb.getAllRowsMeetingsAndSuggestion("client_suggestion_for_show_attention", nowTime);
                        if ( (cSuggest != null && cSuggest.getCount() > 0) || (cClientSuggest != null && cClientSuggest.getCount() > 0) ) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesAttentionEntry[countElements];
                        }
                        else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                    }
                    tmpNew = true;
                    break;

                case 5: // menue item "Zeitplan"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (prefs.getBoolean(ConstansClassTimeTable.namePrefsTimeTableNewValue, false)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;

                case 6: // menue item "Praevention"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 7: // menue item "FAQ"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 8: // menue item "Notfallhilfe"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 9: // menue item "Einstellungen"
                    // check case close?
                    if (prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesAttentionEntry[countElements];
                    }
                    else {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    }
                    break;

                default:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
            }
        }

        return tmpNew;
    }


    // inner class grid view adapter
    public class mainMenueGridViewApdapter extends BaseAdapter {

        private Context mContext;

        public mainMenueGridViewApdapter(Context c) {

            mContext = c;
        }

        @Override
        public int getCount() {

            return ConstansClassMain.mainMenueNumberOfElements;
        }

        @Override
        public Object getItem(int item) {

            return mainMenueElementBackgroundRessources[item];
        }

        @Override
        public long getItemId(int itemId) {

            return itemId;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {

            View grid;

            // init the layout color with light color
            String tmpLinearLayoutBackgroundColor = mainMenueElementColorLight[position];

            if(convertView==null){
                LayoutInflater inflater = getLayoutInflater();
                grid = inflater.inflate (R.layout.gridview_main_layout, parent, false);
            }
            else {

                grid = convertView;
            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_item_image);
            LinearLayout linearLayoutView = (LinearLayout) grid.findViewById(R.id.grid_linear_layout);
            TextView txtView = (TextView) grid.findViewById(R.id.grid_item_label);

            // Element aktiv?
            if (showMainMenueElement[position]) {

                if (imageView != null) {
                    imageView.setImageResource(mainMenueShowElementBackgroundRessources[position]);
                }
                else {
                    imageView = new ImageView(mContext);
                    imageView.setId(R.id.grid_item_image);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageResource(mainMenueShowElementBackgroundRessources[position]);
                    linearLayoutView.addView(imageView,0);
                }

                // show menue name
                txtView.setText(mainMenueElementTitle[position]);
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_white));
                tmpLinearLayoutBackgroundColor = mainMenueElementColor[position];
            }
            else { //Element is inaktiv

                // set normal background ressource (picture)
                if (imageView != null) {
                    imageView.setImageResource(mainMenueElementBackgroundRessources[position]);
                }
                else {
                    imageView = new ImageView(mContext);
                    imageView.setId(R.id.grid_item_image);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setImageResource(mainMenueElementBackgroundRessources[position]);
                    linearLayoutView.addView(imageView,0);
                }

                // show text "inactiv"
                txtView.setText(getResources().getString(getResources().getIdentifier("main_menue_text_inactiv", "string", getPackageName())));
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.main_menue_text_inactiv_color));
            }

            linearLayoutView.setBackgroundColor(Color.parseColor(tmpLinearLayoutBackgroundColor));

            return grid;
        }
    }


    public void newOrUpdateInstallation () {

        // check for version change
        int localAppVersionNumber = prefs.getInt(ConstansClassMain.namePrefsNumberAppVersion, 0);

        if (localAppVersionNumber < ConstansClassMain.actualAppVersionNumber ) {

            switch (localAppVersionNumber) {

                case 0: // installation of app -> first time

                    // set case close to true
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsCaseClose, false);

                    //app function switch off
                    // set function connect book off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, false); // switch off connect book

                    // set function our arrangement off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false); // turn function our arrangement off
                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false); // turn function our arrangement sketch off
                    prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false); // turn function our arrangement old off

                    // set function our goals off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false); // turn function our goals off
                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false); // turn function our goals debetable off
                    prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, false); // turn function our goals old off

                    // set function message on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, true); // turn function message on
                    prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithConnection, false); // give welcome message when connected to server
                    prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithoutConnection, false); // give welcome message when not connected to server
                    prefsEditor.putString(ConstansClassMessage.namePrefsMessageWelcomeMessageLast, ""); // last welcome message given to user

                    // set function time table off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_TimeTable, false); // turn function time table off

                    // set meeting function and subfunction off
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Meeting, false); // turn function meeting off
                    prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientSuggestion_OnOff, false); // turn function meeting client suggestion off
                    prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCanceleMeeting_OnOff, false); // turn function meeting client canceled meeting off
                    prefsEditor.putBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false); // turn function meeting client comment suggestion off

                    // function to switch on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Prevention, true); // turn function prevention on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Faq, true); // turn function faq on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_EmergencyHelp, true); // turn function emergency help on
                    prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_Settings, true); // turn function settings on

                    // set connection parameter and status
                    prefsEditor.putInt(ConstansClassSettings.namePrefsConnectingStatus, 0); // 0=connect to server; 1=no network available; 2=connection error; 3=connected
                    prefsEditor.putInt(ConstansClassSettings.namePrefsRandomNumberForConnection, 0); // five digits for connection to server
                    prefsEditor.putString(ConstansClassSettings.namePrefsClientId, ""); // set client id to nothing
                    prefsEditor.putString(ConstansClassSettings.namePrefsContactId, ""); // set contact id to nothing
                    prefsEditor.putLong(ConstansClassSettings.namePrefsFirstInitTimeInMills, 0L); // set first init time to zero

                    // set visual notification
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_Message, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_RememberMeeting, false);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_RememberSuggestion, false);
                    
                    // set acoustics notification
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_Message, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberMeeting, true);
                    prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true);

                    // write init to prefs
                    prefsEditor.apply();

                    // no break between case!
                case 1: // update one -> put new inits here
                    // nothing to do!
                case 2: // update two
                    // nothing to do!
                case 3: // update three
                    // nothing to do!
                case 4: // update four
                    // nothing to do!
                case 5: // update five
                    // nothing to do!
                case 6: // update six
                    // nothing to do!
                case 7: // update seven
                    // nothing to do!
                case 8: // update eight
                    // nothing to do!
                case 9: // update nine
                    // nothing to do!
                case 10: // update ten

                    prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "ascending");  // init sort of now arrangement
                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementNowShowComment, 20); // init the number of comments for list in our arrangement show comment

                    prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchList, "ascending");  // init sort of sketch arrangement
                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementSketchShowComment, 20); // init the number of comments for list in our arrangement sketch show comment


                    prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementOldList, "ascending");  // init sort of old arrangement



                case 11: // update ten
                    // nothing to do!
                case 12: // update ten
                    // nothing to do!
                case 13: // update ten
                    // nothing to do!
                case 14: // update ten
                    // nothing to do!
            }

            // write app version to prefs
            prefsEditor.putInt(ConstansClassMain.namePrefsNumberAppVersion, ConstansClassMain.actualAppVersionNumber);
            prefsEditor.apply();
        }
    }


    // init notification channel for app
    void initNotificationChannelForApp () {

        int importanceNoSound = NotificationManager.IMPORTANCE_LOW;
        int importanceSound = NotificationManager.IMPORTANCE_DEFAULT;

        // get notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // create new notification channel object without sound
        NotificationChannel notificationChannelNoSound = new NotificationChannel(ConstansClassMain.uniqueNotificationChannelIdNoSound, ConstansClassMain.uniqueNotificationChannelNameNoSound, importanceNoSound);

        // set sound off
        notificationChannelNoSound.setSound(null, null);

        // create notification channel
        notificationManager.createNotificationChannel(notificationChannelNoSound);

        // create new notification channel object without sound
        NotificationChannel notificationChannelSound = new NotificationChannel(ConstansClassMain.uniqueNotificationChannelIdSound, ConstansClassMain.uniqueNotificationChannelNameSound, importanceSound);

        // set sound on
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationChannelSound.setSound(alarmSound, null);

        // create notification channel
        notificationManager.createNotificationChannel(notificationChannelSound);
    }

}