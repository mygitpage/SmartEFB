package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


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

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(mainActivityBrodcastReceiver, filter);

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
                        case 3: // grid "zeitplan"
                            intent = new Intent(getApplicationContext(), ActivityTimeTable.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 4: // grid "praevention"
                            intent = new Intent(getApplicationContext(), ActivityPrevention.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 5: // grid "faq"
                            intent = new Intent(getApplicationContext(), ActivityFaq.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 6: // grid "termine"
                            intent = new Intent(getApplicationContext(), ActivityMeeting.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 7: // grid "hilfe"
                            intent = new Intent(getApplicationContext(), ActivityEmergencyHelp.class);
                            intent.putExtra("position", position);
                            intent.putExtra("title", mainMenueElementTitle[position]);
                            mainContext.startActivity(intent);
                            break;
                        case 8:
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
    }


    @Override
    public void onStart() {

        super.onStart();

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



    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver mainActivityBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the main view
            Boolean updateMainView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // check intent order
                // new connect book message
                String tmpExtraConnectBookMessageNewOrSend = intentExtras.getString("ConnectBookMessageNewOrSend","0");
                // new time table value
                String tmpExtraTimeTable = intentExtras.getString("TimeTable","0");
                String tmpExtraTimeTableNewValue = intentExtras.getString("TimeTableNewValue","0");

                if (tmpExtraConnectBookMessageNewOrSend != null && tmpExtraConnectBookMessageNewOrSend.equals("1")) {

                    // new message received
                    updateMainView = true;

                } else if (tmpExtraTimeTable != null && tmpExtraTimeTable.equals("1") && tmpExtraTimeTableNewValue != null && tmpExtraTimeTableNewValue.equals("1")) {

                    // time table has change -> refresh activity view
                    updateMainView = true;
                }
            }

            // update the main view
            if (updateMainView) {
                updateMainView();
            }
        }
    };



    public void updateMainView () {

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
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

        // start excahnge service with intent
        setAlarmForExchangeService();

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
                        if (myDb.getCountNewEntryOurGoals(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis())) > 0 || (subfunction_goals_comment && myDb.getCountAllNewEntryOurGoalsJointlyGoalsComment(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis())) > 0) || (subfunction_goals_debetable && myDb.getCountNewEntryOurGoals(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis())) > 0) || (subfunction_goals_debetablecomment && myDb.getCountAllNewEntryOurGoalsDebetableGoalsComment(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis())) > 0)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }
                    break;
                case 3: // menue item "Zeitplan"
                    if (showMainMenueElement[countElements]) { // is element aktiv?
                        if (prefs.getBoolean(ConstansClassTimeTable.namePrefsTimeTableNewValue, false)) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                        } else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                        tmpNew = true;
                    }

                    break;
                case 4: // menue item "Praevention"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 5: // menue item "FAQ"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;

                case 6: // menue item "Termine"
                    if (myDb.getCountNewEntryMeetingAndSuggestion("suggestion") > 0 || myDb.getCountNewEntryMeetingAndSuggestion("meeting") > 0 ) {
                        mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesNewEntry[countElements];
                    } else {
                        if (myDb.getCountNewEntryMeetingAndSuggestion("suggestion_for_show_attention") > 0) {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessourcesAttentionEntry[countElements];
                        }
                        else {
                            mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                        }
                    }
                    tmpNew = true;
                    break;
                case 7: // menue item "Notfallhilfe"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                case 8: // menue item "Einstellungen"
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
                default:
                    mainMenueShowElementBackgroundRessources[countElements] = mainMenueElementBackgroundRessources[countElements];
                    break;
            }
        }

        return tmpNew;
    }


    // set alarm manager to start every wakeUpTimeExchangeService seconds the service to check server for new data
    public void setAlarmForExchangeService () {

        // get calendar and init
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // set calendar object with seconds
        calendar.add(Calendar.SECOND, ConstansClassMain.wakeUpTimeExchangeService);
        int tmpAlarmTime = ConstansClassMain.wakeUpTimeExchangeService * 1000; // make mills-seconds

        // make intent for  alarm receiver
        Intent startIntentService = new Intent (getApplicationContext(), AlarmReceiverExchangeService.class);

        // make pending intent
        final PendingIntent pIntentService = PendingIntent.getBroadcast(this, 0, startIntentService, PendingIntent.FLAG_UPDATE_CURRENT );

        // get alarm manager service
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // set alarm manager to call exchange receiver
        try {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), tmpAlarmTime, pIntentService);
        }
        catch (NullPointerException e) {
            // do nothing
        }
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

                txtView.setText(mainMenueElementTitle[position]);
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_white));
                tmpLinearLayoutBackgroundColor = mainMenueElementColor[position];
            }
            else { //Element is inaktiv
                txtView.setText(getResources().getString(getResources().getIdentifier("main_menue_text_inactiv", "string", getPackageName())));
                txtView.setTextColor(ContextCompat.getColor(mContext, R.color.main_menue_text_inactiv_color));
                linearLayoutView.removeView(imageView);
            }

            linearLayoutView.setBackgroundColor(Color.parseColor(tmpLinearLayoutBackgroundColor));

            return grid;
        }
    }


    public void newOrUpdateInstallation () {

        if (prefs.getInt(ConstansClassMain.namePrefsMainNameAppVersion, 0) < ConstansClassMain.actualVersion || (prefs.getInt(ConstansClassMain.namePrefsMainNameAppVersion, 0) == ConstansClassMain.actualVersion && prefs.getInt(ConstansClassMain.namePrefsMainNameAppSubVersion, 0) < ConstansClassMain.actualSubVersion) ) {

            // complete new installation?
            if (prefs.getInt(ConstansClassMain.namePrefsMainNameAppVersion, 0) == 0) {

                Log.d ("Main Updat Func", "Im NEW!!! ZWEIG!");

                //app function switch off
                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, false); // switch off connect book

                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false); // turn function our arrangement off
                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false); // turn function our arrangement sketch off
                prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false); // turn function our arrangement old off

                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false); // turn function our goals off
                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false); // turn function our goals debetable off
                prefsEditor.putBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, false); // turn function our goals old off

                prefsEditor.putBoolean(ConstansClassMain.namePrefsMainMenueElementId_TimeTable, false); // turn function time table off

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
                prefsEditor.putInt(ConstansClassSettings.namePrefsRandomNumberForConnection, 0); // five digits for connetion to server
                prefsEditor.putString(ConstansClassSettings.namePrefsClientId, ""); // set smarthpone id to nothing

                // set app version / sub version
                prefsEditor.putInt(ConstansClassMain.namePrefsMainNameAppVersion, ConstansClassMain.actualVersion);
                prefsEditor.putInt(ConstansClassMain.namePrefsMainNameAppSubVersion, ConstansClassMain.actualSubVersion);

                prefsEditor.commit();

            }
            else { // update

                // do some update work!

                Log.d ("Main Updat Func", "Im UPDATE ZWEIG!");

            }
        }
    }

}








