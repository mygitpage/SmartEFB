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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 26.02.2018.
 */

public class ActivityMessage extends AppCompatActivity {

    // context of activity
    Context contextMessage;

    // reference to the DB
    DBAdapter myDb;

    // reference cursorAdapter for the listview
    MessageCursorAdapter dataAdapter;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // reference for the toolbar
    Toolbar toolbar;
    ActionBar actionBar;

    // listview for message
    ListView listViewMessage;

    Boolean contactModus = false; // true-> app is not associated with a case on server -> contact is anonymous
    Boolean clientModus = false; // true-> app is connect with server and associated with a csae

    // reference to dialog settings
    AlertDialog alertDialogMessage;

    // define one second for correct time in welcome message
    long oneSecondForWelcomeMessageCorrection = 60000; // in milli seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_efb_message);

        contextMessage = this;

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        this.registerReceiver(messageBroadcastReceiver, filter);

        // init message
        initMessage();

        // init the view of activity
        initViewMessage();

        // create help dialog in message
        createHelpDialog();

        // init the ui
        displayMessageSet();

        // first ask to server for new data, send intent to service to start the service
        Intent startServiceIntent = new Intent(contextMessage, ExchangeJobIntentServiceEfb.class);
        // set command = "ask new data" on server
        startServiceIntent.putExtra("com", "ask_new_data");
        startServiceIntent.putExtra("dbid",0L);
        startServiceIntent.putExtra("receiverBroadcast","");
        // start service
        ExchangeJobIntentServiceEfb.enqueueWork(contextMessage, startServiceIntent);
    }


    // init activity
    private void initMessage() {

        // init the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarMessage);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        // set subtitle
        String tmpSubtitle = getResources().getString(R.string.messageSubtitleActivity);
        toolbar.setSubtitle(tmpSubtitle);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // find the listview
        listViewMessage = (ListView) findViewById(R.id.list_view_messages);

        // check contact id and client id
        if (prefs.getString(ConstansClassSettings.namePrefsContactId, "Empty").length() == 0 && prefs.getString(ConstansClassSettings.namePrefsClientId, "Empty").length() == 0) {
            // generate contact id -> client id is not set -> no connection to server and case
            String tmpRandomContactId = EfbHelperClass.generateRandomString();
            prefsEditor.putString(ConstansClassSettings.namePrefsContactId, tmpRandomContactId);
            prefsEditor.apply();
        }
    }


    // init view of activity
    private void initViewMessage() {

        // max letters for a message
        int maxLettersMessage = 0;
        
        // max messages
        int tmpMaxMessages = 0;

        // current count messages
        int tmpCountCurrentMessages = 0;
        
        // check contact id set?
        if (prefs.getString(ConstansClassSettings.namePrefsContactId, "").length() > 0) {

            // app is not associated to a case
            contactModus = true;
            
            // insert welcome message in db? -> no connection, anonymous
            if (!prefs.getBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithoutConnection, false)) {

                // look for actual messages in db -> welcome message back must last - set write time correct!
                Cursor cursor = myDb.getAllRowsMessages("desc");

                if (prefs.getString(ConstansClassMessage.namePrefsMessageWelcomeMessageLast, "").equals("helloWithConnection")) {
                    // get author and text of welcome message not associated again
                    String tmpStoreAuthorName = getResources().getString(R.string.messageAutomaticGeneratedHelloTextAuthorNameNotAssociatedAgain);
                    String tmpStoreMessageText = getResources().getString(R.string.messageAutomaticGeneratedHelloTextMessageTextNotAssociatedAgain);
                    int tmpAnonymous = 0;
                    long writeTime = 0L;

                    // check for entry in db -> get last write time
                    if (cursor != null && cursor.getCount() > 0) {
                        writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));
                        // we add one minute (60 seconds) to last message write time to put last element
                        writeTime = writeTime + oneSecondForWelcomeMessageCorrection;
                    }
                    else {
                        writeTime = System.currentTimeMillis();
                    }

                    // insert welcome message in db
                    insertAutomaticHelloMessageByCoaches(tmpStoreAuthorName, tmpStoreMessageText, tmpAnonymous, writeTime);

                    // possible welcome text with new connection
                    prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithConnection, false);
                }
                else {
                    // get author and text of first welcome message not associated
                    String tmpStoreAuthorName = getResources().getString(R.string.messageAutomaticGeneratedHelloTextAuthorNameNotAssociated);
                    String tmpStoreMessageText = getResources().getString(R.string.messageAutomaticGeneratedHelloTextMessageTextNotAssociated);
                    int tmpAnonymous = 0;
                    long writeTime = 0L;

                    if (cursor != null && cursor.getCount() > 0) {
                        writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));
                        // check for negative timestamp -> because we sub one minute (60 seconds)
                        if (writeTime > 100) {writeTime = writeTime - oneSecondForWelcomeMessageCorrection;}
                        else {writeTime = System.currentTimeMillis();}
                    }
                    else {
                        writeTime = System.currentTimeMillis();
                    }

                    // insert welcome message in db
                    insertAutomaticHelloMessageByCoaches(tmpStoreAuthorName, tmpStoreMessageText, tmpAnonymous, writeTime);
                }
                prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithoutConnection, true);
                prefsEditor.putString(ConstansClassMessage.namePrefsMessageWelcomeMessageLast, "welcomeMessageConnectedShow");
                prefsEditor.apply();
            }

            // get max letters for message
            maxLettersMessage = prefs.getInt(ConstansClassMessage.namePrefsMessageMaxLettersNotAssociated, ConstansClassMessage.namePrefsMessageMaxLettersNotAssociatedStandard);

            // get max messages
            tmpMaxMessages = prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageNotAssociated, ConstansClassMessage.namePrefsMessageMaxMessageNotAssociatedStandard);

            // get current count messages
            tmpCountCurrentMessages = prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0);
        }

        // check client id
        if (prefs.getString(ConstansClassSettings.namePrefsClientId, "").length() > 0 ) {

            // app is associate with case
            clientModus = true;
            
            // insert welcome message in db? -> connected, associated with case
            if (!prefs.getBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithConnection, false)) {

                // look for actual messages in db -> welcome message must first - set write time correct!
                Cursor cursor = myDb.getAllRowsMessages("desc");

                if (prefs.getString(ConstansClassMessage.namePrefsMessageWelcomeMessageLast, "").equals("welcomeMessageConnectedShow")) {

                    // get author and text of welcome message associated
                    String tmpStoreAuthorName = getResources().getString(R.string.messageAutomaticGeneratedHelloTextAuthorNameAssociated);
                    String tmpStoreMessageText = getResources().getString(R.string.messageAutomaticGeneratedHelloTextMessageTextAssociated);
                    int tmpAnonymous = 1;
                    long writeTime = 0L;

                    if (cursor != null && cursor.getCount() > 0) {
                        writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));
                        // add one minute to last message write time
                        writeTime = writeTime + oneSecondForWelcomeMessageCorrection;
                    }
                    else {
                        writeTime = System.currentTimeMillis();
                    }

                    // insert welcome message in db
                    insertAutomaticHelloMessageByCoaches(tmpStoreAuthorName, tmpStoreMessageText, tmpAnonymous, writeTime);
                }
                else {
                    // get author and text of welcome message associated
                    String tmpStoreAuthorName = getResources().getString(R.string.messageAutomaticGeneratedHelloTextAuthorNameAssociated);
                    String tmpStoreMessageText = getResources().getString(R.string.messageAutomaticGeneratedHelloTextMessageTextAssociated);
                    int tmpAnonymous = 1;
                    long writeTime = 0L;

                    if (cursor != null && cursor.getCount() > 0) {
                        writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));
                        // check for negative timestamp -> because we sub one minute (60 seconds)
                        if (writeTime > 100) {writeTime = writeTime - oneSecondForWelcomeMessageCorrection;}
                        else {writeTime = System.currentTimeMillis();}
                    }
                    else {
                        writeTime = System.currentTimeMillis();
                    }

                    // insert welcome message in db
                    insertAutomaticHelloMessageByCoaches(tmpStoreAuthorName, tmpStoreMessageText, tmpAnonymous, writeTime);
                }

                prefsEditor.putBoolean(ConstansClassMessage.namePrefsMessageWelcomeMessageWithConnection, true);
                prefsEditor.putString(ConstansClassMessage.namePrefsMessageWelcomeMessageLast, "helloWithConnection");
                prefsEditor.apply();
            }

            // get max letters for message, associated with case
            maxLettersMessage = prefs.getInt(ConstansClassMessage.namePrefsMessageMaxLettersAssociated, ConstansClassMessage.namePrefsMessageMaxLettersAssociatedStandard);

            // get max messages
            tmpMaxMessages = prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageAssociated, ConstansClassMessage.namePrefsMessageMaxMessageAssociatedStandard);

            // get current count messages
            tmpCountCurrentMessages = prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0);
        }

        // check communication modus (associated or not associated)
        if ((clientModus && !contactModus) || (!clientModus && contactModus)) {

            // set max letters for a message
            final int maxLettersMessageFinal = maxLettersMessage;
            // set max messages
            final int tmpMaxMessagesFinal = tmpMaxMessages;
            // set current count of messages
            final int tmpCountCurrentMessagesFinal = tmpCountCurrentMessages;

            // check text stop sending messages
            TextView tmpTextViewStopSendingMessages = (TextView) findViewById(R.id.messageStopSending);
            if (prefs.getBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false)) {
                tmpTextViewStopSendingMessages.setVisibility(View.VISIBLE);
            }
            else {
                tmpTextViewStopSendingMessages.setVisibility(View.GONE);
            }

            // get textView to count input letters and init it
            final TextView textViewCountLettersMessageEditText = (TextView) findViewById(R.id.countLettersAndMessagesInfoText);
            String tmpInfoTextCountLetters = getResources().getString(R.string.infoTextCountLettersForComment);
            tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", maxLettersMessageFinal);

            // set text current messages/ max messages
            String tmpInfoTextCountCurrentMessages = getResources().getString(R.string.messageInfoTextCountCurrentMessages);
            tmpInfoTextCountCurrentMessages = String.format(tmpInfoTextCountCurrentMessages, tmpCountCurrentMessagesFinal, tmpMaxMessagesFinal);
            textViewCountLettersMessageEditText.setText(tmpInfoTextCountLetters + " - " + tmpInfoTextCountCurrentMessages);

            // get edit text field for message
            final EditText txtInputMsg = (EditText) findViewById(R.id.inputMsg);

            // set text watcher to count letters in comment field
            final TextWatcher txtInputArrangementCommentTextWatcher = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // get count messages 
                    String tmpInfoTextCountCurrentMessages = getResources().getString(R.string.messageInfoTextCountCurrentMessages);
                    tmpInfoTextCountCurrentMessages = String.format(tmpInfoTextCountCurrentMessages, tmpCountCurrentMessagesFinal, tmpMaxMessagesFinal);
                    // set count letters
                    String tmpInfoTextCountLetters = getResources().getString(R.string.infoTextCountLettersForComment);
                    tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), maxLettersMessageFinal);
                    textViewCountLettersMessageEditText.setText(tmpInfoTextCountLetters + " - " + tmpInfoTextCountCurrentMessages);
                }

                public void afterTextChanged(Editable s) {
                }
            };

            // set text watcher to count input letters from message
            txtInputMsg.addTextChangedListener(txtInputArrangementCommentTextWatcher);

            // set input filter max length for message field
            txtInputMsg.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLettersMessageFinal)});

            // send button init
            Button buttonSendMessage = (Button) findViewById(R.id.buttonSendMessage);

            // onClick send button
            buttonSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!prefs.getBoolean(ConstansClassMessage.namePrefsMessageStopCommunication, false)) {

                        // check number of send messages
                        if (tmpCountCurrentMessagesFinal < tmpMaxMessagesFinal) {

                            String inputMessage = txtInputMsg.getText().toString();

                            if (inputMessage.length() > 1) {

                                // author name of message
                                String tmpAuthorName;
                                // anonymous or not
                                int tmpAnonymous;
                                // add current number of send messages and write to prefs
                                int tmpCountCurrent = tmpCountCurrentMessagesFinal + 1;
                                if (clientModus) {
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, tmpCountCurrent);
                                    tmpAnonymous = 1; // not anonymous -> app is connected to server
                                    tmpAuthorName = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");
                                } else {
                                    prefsEditor.putInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, tmpCountCurrent);
                                    tmpAnonymous = 0; // anonymous -> app is not connected to server
                                    tmpAuthorName = getResources().getString(R.string.messageNotAssociatedAuthorName);
                                }
                                prefsEditor.apply();

                                Long messageTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                                if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                                    messageTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                                }
                                Long uploadTime = 0L;
                                Long localeTime = System.currentTimeMillis();
                                Boolean newEntry = false;
                                int messageStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment

                                // role for message (0= messages on left side; 1= messages on the right side)
                                int roleMessage = 1; // all messages on the right side of the display
                                String source = "message"; // used for future extension

                                // put message into db
                                long tmpDbId = myDb.insertRowMessage(tmpAuthorName, localeTime, messageTime, inputMessage, roleMessage, messageStatus, newEntry, uploadTime, tmpAnonymous, source);

                                // send intent to service to start the service and send message to server!
                                Intent startServiceIntent = new Intent(contextMessage, ExchangeJobIntentServiceEfb.class);
                                // set command = "ask new data" on server
                                startServiceIntent.putExtra("com", "send_current_message");
                                startServiceIntent.putExtra("dbid", tmpDbId);
                                startServiceIntent.putExtra("receiverBroadcast", "");
                                // start service
                                ExchangeJobIntentServiceEfb.enqueueWork(contextMessage, startServiceIntent);

                                // delete text in edittextfield
                                txtInputMsg.setText("");
                            } else {
                                // to less letters in message
                                String textCaseClose = ActivityMessage.this.getString(R.string.messageToastErrorMessageToShort);
                                Toast toast = Toast.makeText(contextMessage, textCaseClose, Toast.LENGTH_LONG);
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

                        String textCaseClose = ActivityMessage.this.getString(R.string.toastTextMessageStopSendingMessagesDisable);
                        Toast toast = Toast.makeText(contextMessage, textCaseClose, Toast.LENGTH_LONG);
                        TextView viewMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                        if (v != null) viewMessage.setGravity(Gravity.CENTER);
                        toast.show();
                    }
                }
            });
        }
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
        this.unregisterReceiver(messageBroadcastReceiver);

        // close db connection
        myDb.close();
    }

    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver messageBroadcastReceiver = new BroadcastReceiver() {

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
                String tmpMessagesMessage = intentExtras.getString("MessagesMessage");
                String tmpMessagesMessageSettings = intentExtras.getString("MessageSettings");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");
                String tmpExtraMessageNewOrSend = intentExtras.getString("MessageMessageNewOrSend","0");
                String tmpExtraMessageStopSendingEnable = intentExtras.getString ("MessageSettingsMessageStopSendingEnable","0");
                String tmpExtraMessageStopSendingDisable = intentExtras.getString ("MessageSettingsMessageStopSendingDisable","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings","0");
                String tmpCaseClose = intentExtras.getString("Case_close","0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = ActivityMessage.this.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpMessagesMessage != null && tmpMessagesMessage.equals("1") && tmpMessagesMessageSettings != null && tmpMessagesMessageSettings.equals("1") && tmpExtraMessageStopSendingEnable != null && tmpExtraMessageStopSendingEnable.equals("1")) {
                    // stop sending enable
                    String updateConnectBookShare = getResources().getString(R.string.toastTextMessageStopSendingMessagesEnable);
                    Toast toast = Toast.makeText(context,updateConnectBookShare , Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // connect book settings have change -> refresh activity view
                    updateActivityView = true;

                } else if (tmpMessagesMessage != null && tmpMessagesMessage.equals("1") && tmpMessagesMessageSettings != null && tmpMessagesMessageSettings.equals("1") && tmpExtraMessageStopSendingDisable != null && tmpExtraMessageStopSendingDisable.equals("1")) {
                    // stop sending disable
                    String updateConnectBookShare = getResources().getString(R.string.toastTextMessageStopSendingMessagesDisable);
                    Toast toast = Toast.makeText(context,updateConnectBookShare , Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // connect book settings have change -> refresh activity view
                    updateActivityView = true;

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
                else if (tmpExtraMessageNewOrSend != null && tmpExtraMessageNewOrSend.equals("1")) {

                    // new message received or messages send
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

        if (listViewMessage != null) {
            listViewMessage.destroyDrawingCache();
            listViewMessage.setVisibility(ListView.INVISIBLE);
            listViewMessage.setVisibility(ListView.VISIBLE);

            // init the view of activity
            initViewMessage();

            // init listview for messages
            displayMessageSet ();

        }
    }


    public void updateActivityView () {

        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }


    // display messages in listview
    public void displayMessageSet () {

        Cursor cursor = myDb.getAllRowsMessages("asc");

        if (cursor != null && cursor.getCount() > 0 && listViewMessage != null) {

            // new dataadapter
            dataAdapter = new MessageCursorAdapter(
                    ActivityMessage.this,
                    cursor,
                    0);

            // Assign adapter to ListView
            listViewMessage.setAdapter(dataAdapter);
        }
    }


    // help dialog
    void createHelpDialog () {

        Button tmpHelpButtonConnectBook = (Button) findViewById(R.id.helpMessage);

        // add button listener to question mark in activity message (toolbar)
        tmpHelpButtonConnectBook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView tmpdialogTextView;
                LayoutInflater dialogInflater;

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMessage.this);

                // Get the layout inflater
                dialogInflater = (LayoutInflater) ActivityMessage.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // inflate and get the view
                View dialogSettings = dialogInflater.inflate(R.layout.dialog_help_message, null);

                // show help dialog for client modus
                if (clientModus) {
                    // associated
                    TextView tmpDialogTextViewAssociatedOrNot = (TextView) dialogSettings.findViewById(R.id. textViewDialogMessageSettingsGeneralIntroAssociatedOrNot);
                    String tmpTextAssociatedOrNot = ActivityMessage.this.getResources().getString(R.string.textDialogMessageGeneralInfoTextAssociated);
                    tmpDialogTextViewAssociatedOrNot.setText(tmpTextAssociatedOrNot);

                    // show the settings for message
                    tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogMessageSettings);
                    String tmpTxtElement, tmpTxtElement1, tmpTxtElement2, tmpTxtElement2a;

                    // generate text for max messages
                    if (prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageAssociated, ConstansClassMessage.namePrefsMessageMaxMessageAssociatedStandard) > 1) {
                        tmpTxtElement = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsMaxMessagesPlural);
                    } else {
                        tmpTxtElement = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsMaxMessagesSingular);
                    }
                    tmpTxtElement = String.format(tmpTxtElement, prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageAssociated, ConstansClassMessage.namePrefsMessageMaxMessageAssociatedStandard));

                    // generate text for count current messages
                    if (prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0) > 1) {
                        tmpTxtElement1 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesPlural);
                        tmpTxtElement1 = String.format(tmpTxtElement1, prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0));
                    } else if (prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0) == 1) {
                        tmpTxtElement1 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesSingular);
                        tmpTxtElement1 = String.format(tmpTxtElement1, prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0));
                    } else {
                        tmpTxtElement1 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesNothing);
                    }

                    tmpTxtElement2 = "";
                    if (prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0) == prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageAssociated, 0)) {
                        tmpTxtElement2 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesNoMorePossible);

                    }

                    String tmpMaxLettersCount = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsMessageMaxLetters);
                    tmpTxtElement2a = String.format(tmpMaxLettersCount, prefs.getInt(ConstansClassMessage.namePrefsMessageMaxLettersAssociated, ConstansClassMessage.namePrefsMessageMaxLettersAssociatedStandard));

                    // set generate text to view
                    tmpdialogTextView.setText(tmpTxtElement + " " + tmpTxtElement1 + " " + tmpTxtElement2 + " " + tmpTxtElement2a);

                    TextView tmpDialogTextViewInfoTextAssociatedReadByCoach = (TextView) dialogSettings.findViewById(R.id.textViewDialogMessageSettingsReadByCoach);
                    tmpDialogTextViewInfoTextAssociatedReadByCoach.setVisibility(View.VISIBLE);

                    TextView tmpDialogTextViewInfoTextAssociatedContactCoach = (TextView) dialogSettings.findViewById(R.id.textViewDialogMessageSettingsContactCoach);
                    tmpDialogTextViewInfoTextAssociatedContactCoach.setVisibility(View.VISIBLE);

                }
                else {
                    //not associated dialog help
                    TextView tmpDialogTextViewAssociatedOrNot = (TextView) dialogSettings.findViewById(R.id. textViewDialogMessageSettingsGeneralIntroAssociatedOrNot);
                    String tmpTextAssociatedOrNot = ActivityMessage.this.getResources().getString(R.string.textDialogMessageGeneralInfoTextNotAssociated);
                    tmpDialogTextViewAssociatedOrNot.setText(tmpTextAssociatedOrNot);

                    // show the settings for message
                    tmpdialogTextView = (TextView) dialogSettings.findViewById(R.id.textViewDialogMessageSettings);
                    String tmpTxtElement, tmpTxtElement1, tmpTxtElement2, tmpTxtElement2a;

                    // generate text for max messages
                    if (prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageNotAssociated, ConstansClassMessage.namePrefsMessageMaxMessageNotAssociatedStandard) > 1) {
                        tmpTxtElement = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsMaxMessagesPlural);
                    } else {
                        tmpTxtElement = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsMaxMessagesSingular);
                    }
                    tmpTxtElement = String.format(tmpTxtElement, prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageNotAssociated, ConstansClassMessage.namePrefsMessageMaxMessageNotAssociatedStandard));

                    // generate text for count current messages
                    if (prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0) > 1) {
                        tmpTxtElement1 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesPlural);
                        tmpTxtElement1 = String.format(tmpTxtElement1, prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0));
                    } else if (prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0) == 1) {
                        tmpTxtElement1 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesSingular);
                        tmpTxtElement1 = String.format(tmpTxtElement1, prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentAssociated, 0));
                    } else {
                        tmpTxtElement1 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesNothing);
                    }

                    tmpTxtElement2 = "";
                    if (prefs.getInt(ConstansClassMessage.namePrefsMessageCountCurrentNotAssociated, 0) == prefs.getInt(ConstansClassMessage.namePrefsMessageMaxMessageNotAssociated, ConstansClassMessage.namePrefsMessageMaxMessageNotAssociatedStandard)) {
                        tmpTxtElement2 = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsCountCurrentMessagesNoMorePossible);

                    }

                    String tmpMaxLettersCount = ActivityMessage.this.getResources().getString(R.string.textDialogMessageSettingsMessageMaxLetters);
                    tmpTxtElement2a = String.format(tmpMaxLettersCount, prefs.getInt(ConstansClassMessage.namePrefsMessageMaxLettersNotAssociated, ConstansClassMessage.namePrefsMessageMaxLettersNotAssociatedStandard));

                    // set generate text to view
                    tmpdialogTextView.setText(tmpTxtElement + " " + tmpTxtElement1 + " " + tmpTxtElement2 + " " + tmpTxtElement2a);

                    TextView tmpDialogTextViewInfoTextNotAssociated = (TextView) dialogSettings.findViewById(R.id. textViewDialogMessageSettingsHintNotAssociated);
                    tmpDialogTextViewInfoTextNotAssociated.setVisibility(View.VISIBLE);
                }

                // get string ressources
                String tmpTextCloseDialog = ActivityMessage.this.getResources().getString(R.string.textDialogMessageCloseDialog);
                String tmpTextTitleDialog = ActivityMessage.this.getResources().getString(R.string.textDialogMessageTitleDialog);

                // build the dialog
                builder.setView(dialogSettings)

                        // Add close button
                        .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialogMessage.cancel();
                            }
                        })

                        // add title
                        .setTitle(tmpTextTitleDialog);

                // and create
                alertDialogMessage = builder.create();

                // and show the dialog
                builder.show();
            }
        });
    }


    // Dialog for info no more messages possible
    void createInfoDialogNoMoreMessages () {

        LayoutInflater dialogInflater;

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMessage.this);

        // Get the layout inflater
        dialogInflater = (LayoutInflater) ActivityMessage.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate and get the view
        View dialogNoMoreMessages = dialogInflater.inflate(R.layout.dialog_messages_no_more_messages, null);

        // get string ressources
        String tmpTextCloseDialog = ActivityMessage.this.getResources().getString(R.string.textDialogConnectBookCloseDialog);
        String tmpTextTitleDialog = ActivityMessage.this.getResources().getString(R.string.textDialogMessagesTitleNoMoreMessages);

        // build the dialog
        builder.setView(dialogNoMoreMessages)

                // Add close button
                .setNegativeButton(tmpTextCloseDialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialogMessage.cancel();
                    }
                })

                // add title
                .setTitle(tmpTextTitleDialog);

        // and create
        alertDialogMessage = builder.create();

        // and show the dialog
        builder.show();
    }


    void insertAutomaticHelloMessageByCoaches (String authorName, String messageText, int anonymous, Long messageWriteTime) {

        Long uploadTime = messageWriteTime;
        Long localeTime = messageWriteTime;
        Boolean newEntry = false;
        int messageStatus = 4; // 0= not send to sever; 1= send to server; 4= external comment

        // role for message (0= messages on left side; 1= messages on the right side)
        int roleMessage = 0; // all messages on the right side of the display
        String source = "message"; // used for future extension

        // put message into db
        long tmpDbId = myDb.insertRowMessage(authorName, localeTime, messageWriteTime, messageText, roleMessage, messageStatus, newEntry, uploadTime, anonymous, source);

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