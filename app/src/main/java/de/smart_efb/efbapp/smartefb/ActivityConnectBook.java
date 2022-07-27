package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityConnectBook extends AppCompatActivity {

    // context of activity
    Context contextOfConnectBook;

    // reference to the DB
    DBAdapter myDb;

    // reference cursorAdapter for the listview
    ConnectBookCursorAdapter dataAdapter;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // reference for the toolbar
    Toolbar toolbar;
    ActionBar actionBar;

    // listview for connect book
    ListView listViewConnectBook;

    // role for the connect book (0= self messages on left side; 1= self messages on the right side;  2= self messages in the middle)
    int roleConnectBook = 1; // all self messages on the right side of the display

    // one day in milliseconds
    final Long oneDayInMills = 86400000L;

    // the users name of the connect book
    String userNameConnectBook;

    // reference to dialog settings
    AlertDialog alertDialogSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_efb_connect_book);

        contextOfConnectBook = this;

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(connectBookBroadcastReceiver, filter);

        // init the connect book
        initConnectBook();

        // init the view of activity
        initViewConnectBook();

        // create help dialog in Connect Book
        createHelpDialog();

        // init the ui
        displayMessageSet();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(contextOfConnectBook, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(contextOfConnectBook, startServiceIntent);
        }
    }


    // init activity
    private void initConnectBook() {

        // init the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarConnectBook);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        // set sub title
        String tmpSubTitle = getResources().getString(R.string.subTitleConnectBook);
        toolbar.setSubtitle(tmpSubTitle);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // init the connect book variables
        userNameConnectBook = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");

        // find the listview
        listViewConnectBook = (ListView) findViewById(R.id.list_view_messages);

        // check -> 24 h over? -> reset message counter
        Long startPointResetTimeMessageCounter = prefs.getLong(ConstansClassConnectBook.namePrefsConnectCountMessagesResetTime, 0);
        Long lastResetLocaleTimeMessageCounter = prefs.getLong(ConstansClassConnectBook.namePrefsConnectCountMessagesLastResetLocaleTime, 0);
        Long tmpNowTime = System.currentTimeMillis();
        if ( tmpNowTime > (startPointResetTimeMessageCounter + oneDayInMills) && tmpNowTime > lastResetLocaleTimeMessageCounter) {

            // set message counter to zero
            prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0);
            // set new start time for message count
            prefsEditor.putLong(ConstansClassConnectBook.namePrefsConnectCountMessagesResetTime, (startPointResetTimeMessageCounter + oneDayInMills));
            // set last reset locale time to prefs
            prefsEditor.putLong(ConstansClassConnectBook.namePrefsConnectCountMessagesLastResetLocaleTime, tmpNowTime);
            prefsEditor.apply();
        }
        else { // go back in time is not allowed
            if (tmpNowTime < lastResetLocaleTimeMessageCounter) {
                // set message counter to max messages
                int tmpMaxMessages = prefs.getInt(ConstansClassConnectBook.namePrefsConnectMaxMessages, 500);
                prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, tmpMaxMessages);
                prefsEditor.apply();
            }
        }
    }


    // init view of activity
    private void initViewConnectBook() {

        // get max letters for message
        final int maxLettersMessage = prefs.getInt(ConstansClassConnectBook.namePrefsConnectMaxLetters, 10);

        // check, sharing messages enable?
        if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectMessageShare, 0) == 0) {
            TextView textMessageSharingIsDisable = (TextView) findViewById(R.id.messageSharingIsDisable);
            textMessageSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // get textView to count input letters and init it
        final TextView textViewCountLettersMessageEditText = (TextView) findViewById(R.id.countLettersAndMessagesInfoText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", maxLettersMessage);

        // get current number of send messages and max numbers
        final int tmpMaxMessages = prefs.getInt(ConstansClassConnectBook.namePrefsConnectMaxMessages, 1);
        int tmpCountCurrentMessages = prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0);

        String tmpInfoTextCountCurrentMessages = getResources().getString(R.string.infoTextCountCurrentMessages);
        tmpInfoTextCountCurrentMessages = String.format(tmpInfoTextCountCurrentMessages, tmpCountCurrentMessages, tmpMaxMessages);
        textViewCountLettersMessageEditText.setText(tmpInfoTextCountLetters + " - " + tmpInfoTextCountCurrentMessages);

        // get edit text field for message
        final EditText txtInputMsg = (EditText) findViewById(R.id.inputMsg);

        // set text watcher to count letters in comment field
        final TextWatcher txtInputArrangementCommentTextWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // get count messages from prefs
                int tmpCountCurrentMessages = prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0);
                String tmpInfoTextCountCurrentMessages = getResources().getString(R.string.infoTextCountCurrentMessages);
                tmpInfoTextCountCurrentMessages = String.format(tmpInfoTextCountCurrentMessages, tmpCountCurrentMessages, tmpMaxMessages);
                // set count letters
                String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
                tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), maxLettersMessage);
                textViewCountLettersMessageEditText.setText(tmpInfoTextCountLetters + " - " + tmpInfoTextCountCurrentMessages);
            }
            public void afterTextChanged(Editable s) {
            }
        };

        // set text watcher to count input letters from message
        txtInputMsg.addTextChangedListener(txtInputArrangementCommentTextWatcher);

        // set input filter max length for message field
        txtInputMsg.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLettersMessage)});

        // send button init
        Button buttonSendConnectBook = (Button) findViewById(R.id.buttonSendMessage);

        // onClick send button
        buttonSendConnectBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tmpCountCurrentMessages = prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0);

                // check case close
                if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

                    // check number of send messages in 24h
                    if (tmpCountCurrentMessages < tmpMaxMessages) {

                        String inputMessage = txtInputMsg.getText().toString();

                        if (inputMessage.length() > 0) {

                            Long messageTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                            if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                                messageTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                            }
                            Long uploadTime = 0L;
                            Long localeTime = System.currentTimeMillis();
                            Boolean newEntry = false;
                            int messageStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                            int timerStatus = 0;

                            // put message into db (role: 0= left; 1= right; 2= center)
                            long tmpDbId = myDb.insertRowChatMessage(userNameConnectBook, localeTime, messageTime, inputMessage, roleConnectBook, messageStatus, newEntry, uploadTime, timerStatus);

                            // add current number of send messages and write to prefs
                            tmpCountCurrentMessages++;
                            prefsEditor.putInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, tmpCountCurrentMessages);
                            prefsEditor.apply();

                            // send intent to service to start the service
                            Intent startServiceIntent = new Intent(contextOfConnectBook, ExchangeJobIntentServiceEfb.class);
                            startServiceIntent.putExtra("com", "send_connectbook_message");
                            startServiceIntent.putExtra("dbid", tmpDbId);
                            startServiceIntent.putExtra("receiverBroadcast", "");
                            // start service
                            ExchangeJobIntentServiceEfb.enqueueWork(contextOfConnectBook, startServiceIntent);

                            // delete text in edittextfield
                            txtInputMsg.setText("");
                        } else {
                            // to less letters in message
                            String textCaseClose = ActivityConnectBook.this.getString(R.string.toastConnectBookToLessLettersMessageInput);
                            Toast toast = Toast.makeText(contextOfConnectBook, textCaseClose, Toast.LENGTH_LONG);
                            TextView viewMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) viewMessage.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                    } else {

                        // delete text in edittextfield
                        txtInputMsg.setText("");

                        // show dialog no more messages
                        createInfoDialogNoMoreMessages();
                    }
                }
                else {
                    // delete text in edittextfield
                    txtInputMsg.setText("");

                    // case is closed -> show toast
                    String textCaseClose = ActivityConnectBook.this.getString(R.string.toastConnectBookCaseCloseToastText);
                    Toast toast = Toast.makeText(contextOfConnectBook, textCaseClose, Toast.LENGTH_LONG);
                    TextView viewMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) viewMessage.setGravity(Gravity.CENTER);
                    toast.show();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        // update listview  on resume
        updateListView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // de-register broadcast receiver
        this.unregisterReceiver(connectBookBroadcastReceiver);

        // close db connection
        myDb.close();
    }

    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver connectBookBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with arrangements
            Boolean updateListView = false;

            // true-> update the view of activity connect book
            Boolean updateActivityView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order
                String tmpExtraConnectBook = intentExtras.getString("ConnectBook","0");
                String tmpExtraConnectBookSettings = intentExtras.getString("ConnectBookSettings","0");
                String tmpExtraConnectBookMessageNewOrSend = intentExtras.getString("ConnectBookMessageNewOrSend","0");
                String tmpExtraConnectBookMessageSharingEnable = intentExtras.getString ("ConnectBookSettingsMessageShareEnable","0");
                String tmpExtraConnectBookMessageSharingDisable = intentExtras.getString ("ConnectBookSettingsMessageShareDisable","0");
                String tmpExtraConnectBookMessageAndDelay = intentExtras.getString ("ConnectBookSettingsMessageAndDelay","0");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");
                // case is close
                String tmpSettings = intentExtras.getString("Settings","0");
                String tmpCaseClose = intentExtras.getString("Case_close","0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = ActivityConnectBook.this.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraConnectBook != null && tmpExtraConnectBook.equals("1") && tmpExtraConnectBookSettings != null && tmpExtraConnectBookSettings.equals("1") && tmpExtraConnectBookMessageSharingEnable != null && tmpExtraConnectBookMessageSharingEnable.equals("1")) {
                    // sharing enable
                    String updateConnectBookShare = getResources().getString(R.string.toastConnectBookMessageSettingsSharingEnable);
                    Toast toast = Toast.makeText(context,updateConnectBookShare , Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // connect book settings have change -> refresh activity view
                    updateActivityView = true;

                } else if (tmpExtraConnectBook != null && tmpExtraConnectBook.equals("1") && tmpExtraConnectBookSettings != null && tmpExtraConnectBookSettings.equals("1") && tmpExtraConnectBookMessageSharingDisable != null && tmpExtraConnectBookMessageSharingDisable.equals("1")) {
                    // sharing disable
                    String updateConnectBookShare = getResources().getString(R.string.toastConnectBookMessageSettingsSharingDisable);
                    Toast toast = Toast.makeText(context,updateConnectBookShare , Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // connect book settings have change -> refresh activity view
                    updateActivityView = true;

                } else if (tmpExtraConnectBook != null && tmpExtraConnectBook.equals("1") && tmpExtraConnectBookSettings != null && tmpExtraConnectBookSettings.equals("1") && tmpExtraConnectBookMessageAndDelay != null && tmpExtraConnectBookMessageAndDelay.equals("1")) {
                    // connect book settings have change
                    String updateConnectBookSettings = getResources().getString(R.string.toastConnectBookMessageSettingsChangeMessageAndDelay);
                    Toast toast = Toast.makeText(context,updateConnectBookSettings , Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // connect book settings have change -> refresh activity view
                    updateActivityView = true;

                } else if (tmpExtraConnectBookMessageNewOrSend != null && tmpExtraConnectBookMessageNewOrSend.equals("1")) {
                    // new message received or messages send
                    updateListView = true;

                } else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send successfull?
                    // message send succsessfull
                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update listview
                    updateListView = true;
                }
                else if (tmpSendNotSuccessefull != null && tmpSendNotSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send not successfull?
                    // message send not successfull
                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update listview
                    updateListView = true;
                }
            }

            // update the list view with messages
            if (updateListView) {
                updateListView();
            }

            // update the activity view
            if (updateActivityView) {
                updateActivityView();
            }
        }
    };


    // update the list view connect book
    public void updateListView () {

        if (listViewConnectBook != null) {
            listViewConnectBook.destroyDrawingCache();
            listViewConnectBook.setVisibility(ListView.INVISIBLE);
            listViewConnectBook.setVisibility(ListView.VISIBLE);

            // init the view of activity
            initViewConnectBook();

            // init listview for messages
            displayMessageSet ();
        }
    }

    
    public void updateActivityView () {

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    

    // display connect book messages in listview
    public void displayMessageSet () {

        Cursor cursor = myDb.getAllRowsChatMessage();

        if (cursor.getCount() > 0 && listViewConnectBook != null) {

            // new dataadapter
            dataAdapter = new ConnectBookCursorAdapter(
                    ActivityConnectBook.this,
                    cursor,
                    0);

            // Assign adapter to ListView
            listViewConnectBook.setAdapter(dataAdapter);
        }
    }


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonConnectBook = (Button) findViewById(R.id.helpConnectBook);

        // add button listener to question mark in activity Cconnect Book (toolbar)
        tmpHelpButtonConnectBook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView tmpdialogTextView;
                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityConnectBook.this, R.style.helpDialogStyle);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityConnectBook.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_connect_book, null);

                // show the settings for connect book
                tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogConnectBookSettings);
                String tmpTxtElement, tmpTxtElement1, tmpTxtElement2, tmpTxtElement2a, tmpTxtElement3;

                // generate text for max messages
                if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectMaxMessages, 0) > 1) {
                    tmpTxtElement = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsMaxMessagesPlural);
                }
                else {
                    tmpTxtElement = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsMaxMessagesSingular);
                }
                tmpTxtElement = String.format(tmpTxtElement, prefs.getInt(ConstansClassConnectBook.namePrefsConnectMaxMessages, 0));

                // generate text for count current messages
                if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0) > 1) {
                    tmpTxtElement1 = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsCountCurrentMessagesPlural);
                    tmpTxtElement1 = String.format(tmpTxtElement1, prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0));
                }
                else if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0) == 1) {
                    tmpTxtElement1 = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsCountCurrentMessagesSingular);
                    tmpTxtElement1 = String.format(tmpTxtElement1, prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0));
                }
                else {
                    tmpTxtElement1 = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsCountCurrentMessagesNothing);
                }

                tmpTxtElement2 = "";
                if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectCountCurrentMessages, 0) == prefs.getInt(ConstansClassConnectBook.namePrefsConnectMaxMessages, 0)) {
                    tmpTxtElement2 = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsCountCurrentMessagesNoMorePossible);

                }

                String tmpMaxLettersCount = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsMessageMaxLetters);
                tmpTxtElement2a = String.format(tmpMaxLettersCount, prefs.getInt(ConstansClassConnectBook.namePrefsConnectMaxLetters, 0));

                // generate text for delay time
                if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectSendDelayTime, 0) > 1) {
                    tmpTxtElement3 = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsDelayTimePlural);
                }
                else if (prefs.getInt(ConstansClassConnectBook.namePrefsConnectSendDelayTime, 0) == 0) {
                    tmpTxtElement3 = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsDelayTimeSingularZero);
                }
                else {
                    tmpTxtElement3 = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookSettingsDelayTimeSingular);
                }
                tmpTxtElement3 = String.format(tmpTxtElement3, prefs.getInt(ConstansClassConnectBook.namePrefsConnectSendDelayTime, 0));

                // set generate text to view
                tmpdialogTextView.setText(tmpTxtElement + " " + tmpTxtElement1 + " " + tmpTxtElement2 + " " + tmpTxtElement2a + " " + tmpTxtElement3);

                // generate link to involved person (activity settings tab 0)
                TextView tmpdialogTextViewLinkToInvolvedPerson = (TextView) dialogSettings.findViewById(R.id.textViewDialogConnectBookSettingsInvolvedPerson);
                tmpdialogTextViewLinkToInvolvedPerson.setMovementMethod(LinkMovementMethod.getInstance());

                // get string ressources
                String tmpTextCloseDialog = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookCloseDialog);
                String tmpTextTitleDialog = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookTitleDialog);

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

                alertDialogSettings.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // change background and text color of button
                        Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        // Change negative button text and background color
                        negativeButton.setTextColor(ContextCompat.getColor(ActivityConnectBook.this, R.color.white));
                        negativeButton.setBackgroundResource(R.drawable.help_dialog_custom_negativ_button_background);
                    }
                });

                // show dialog
                alertDialogSettings.show();
            }
        });
    }


    // Dialog for info no more messages possible
    void createInfoDialogNoMoreMessages () {

        LayoutInflater dialogInflater;

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityConnectBook.this);

        // Get the layout inflater
        dialogInflater = (LayoutInflater) ActivityConnectBook.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate and get the view
        View dialogNoMoreMessages = dialogInflater.inflate(R.layout.dialog_connect_book_no_more_messages, null);

        // get string ressources
        String tmpTextCloseDialog = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookCloseDialog);
        String tmpTextTitleDialog = ActivityConnectBook.this.getResources().getString(R.string.textDialogConnectBookTitleNoMoreMessages);

        // build the dialog
        builder.setView(dialogNoMoreMessages)

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
