package de.smart_efb.efbapp.smartefb;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ExchangeJobIntentServiceEfb extends JobIntentService {

    private static final int JOB_ID = 22222;
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    // point to shared preferences
    SharedPreferences prefs;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, ExchangeJobIntentServiceEfb.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.isRunning = false;
        this.context = this;

        // get the shared preferences
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);
    }

    //++++++++++++++++++ TASK AREA ++++++++++++++++++++++++++++++++++++++++++++++

    // Check the DB for unsend comments, evaluation results, meetings, etc. and send to sever.
    // Ask server for new data and get answer from server
    public class ExchangeTaskCheckNewContent implements Runnable {

        // reference to the DB
        private DBAdapter myDb;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskCheckNewContent (Context context) {

            // context of task
            this.context = context;

            // init the DB
            myDb = new DBAdapter(context);

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            Boolean send_now_comment_info = false;
            Boolean send_sketch_comment_info = false;
            Boolean send_jointly_goals_comment_info = false;
            Boolean send_arrangement_evaluation_result_info = false;
            Boolean send_goals_evaluation_result_info = false;
            Boolean send_debetable_goals_comment_info = false;
            Boolean send_connect_book_messages_result_info = false;
            Boolean send_meeting_data_result_info = false;
            Boolean send_message_data_result_info = false;

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                Boolean clientIdSet = false;
                Boolean contactIdSet = false;

                // all cursors for data
                Cursor allCommentsReadyToSend = null;
                Cursor allSketchCommentsReadyToSend = null;
                Cursor allArrangementEvaluationResultsReadyToSend = null;
                Cursor allJointlyGoalsCommentsReadyToSend = null;
                Cursor allGoalsEvaluationResultsReadyToSend = null;
                Cursor allDebetableCommentsReadyToSend = null;
                Cursor allConnectBookMessagesReadyToSend = null;
                Cursor allMeetingsReadyToSend = null;
                Cursor allMessagesReadyToSend = null;

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // get contact id from prefs
                String tmpContactId = prefs.getString(ConstansClassSettings.namePrefsContactId, "");

                // check for client or contact id
                if (tmpClientId.length() > 0) {clientIdSet = true;}
                if (tmpContactId.length() > 0 && !clientIdSet) {contactIdSet = true;}

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();

                // check association -> app connect to server -> check all data
                if (clientIdSet) {

                    // get all comments with status = 0 -> ready to send
                    allCommentsReadyToSend = myDb.getAllReadyToSendComments(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0"));

                    // get all sketch comments with status = 0 -> ready to send
                    allSketchCommentsReadyToSend = myDb.getAllReadyToSendSketchComments(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0"));

                    // get all now arrangement evaluation results with status = 0 -> ready to send
                    allArrangementEvaluationResultsReadyToSend = myDb.getAllReadyToSendArrangementEvaluationResults();

                    // get all jointly goals comments with status = 0 -> ready to send
                    allJointlyGoalsCommentsReadyToSend = myDb.getAllReadyToSendJointlyGoalsComments(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0"));

                    // get all jointly goals evaluation results with status = 0 -> ready to send
                    allGoalsEvaluationResultsReadyToSend = myDb.getAllReadyToSendGoalsEvaluationResults();

                    // get all debetable comments with status = 0 -> ready to send
                    allDebetableCommentsReadyToSend = myDb.getAllReadyToSendDebetableComments(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfDebetableGoals, "0"));

                    // get all connect book messages with status = 0 and role = 1 (own messages) -> ready to send
                    allConnectBookMessagesReadyToSend = myDb.getAllReadyToSendConnectBookMessages();

                    // get all meetings data (meetings, suggestions from client, answers, comments, etc.) with status = 0 -> ready to send
                    allMeetingsReadyToSend = myDb.getAllRowsMeetingsAndSuggestion("ready_to_send", 0L); // time is not needed for db-question, so set to zero
                }

                // in every case (associated or not associated) -> get all messages with status = 0 and role = 1 (own messages) -> ready to send
                allMessagesReadyToSend = myDb.getAllReadyToSendMessages();

                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send "ask new data" and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_AskNewData);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // check client or contact id set?
                    if (clientIdSet) {
                        // start tag client id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        xmlSerializer.text(tmpClientId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        // start empty tag contact id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    }
                    else {
                        // start empty tag client id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        // start tag contact id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                        xmlSerializer.text(tmpContactId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    }

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // check association -> app connect to server -> check all data
                    if (clientIdSet) {

                        // build xml for all now comments
                        if (allCommentsReadyToSend != null && allCommentsReadyToSend.getCount() > 0) {

                            do {
                                buildCommentNowXmlTagWithData(xmlSerializer, allCommentsReadyToSend);
                                send_now_comment_info = true;
                            } while (allCommentsReadyToSend.moveToNext());
                        }

                        // build xml for all sketch comments
                        if (allSketchCommentsReadyToSend != null && allSketchCommentsReadyToSend.getCount() > 0) {

                            do {
                                buildCommentSketchXmlTagWithData(xmlSerializer, allSketchCommentsReadyToSend);
                                send_sketch_comment_info = true;
                            } while (allSketchCommentsReadyToSend.moveToNext());
                        }

                        // build xml for all arrangement evaluation result
                        if (allArrangementEvaluationResultsReadyToSend != null && allArrangementEvaluationResultsReadyToSend.getCount() > 0) {

                            do {
                                buildArrangementEvaluationResultXmlTagWithData(xmlSerializer, allArrangementEvaluationResultsReadyToSend);
                                send_arrangement_evaluation_result_info = true;
                            }
                            while (allArrangementEvaluationResultsReadyToSend.moveToNext());
                        }

                        // build xml for all jointly goals comments
                        if (allJointlyGoalsCommentsReadyToSend != null && allJointlyGoalsCommentsReadyToSend.getCount() > 0) {

                            do {
                                buildJointlyCommentXmlTagWithData(xmlSerializer, allJointlyGoalsCommentsReadyToSend);
                                send_jointly_goals_comment_info = true;
                            } while (allJointlyGoalsCommentsReadyToSend.moveToNext());
                        }

                        // build xml for all goals evaluation result
                        if (allGoalsEvaluationResultsReadyToSend != null && allGoalsEvaluationResultsReadyToSend.getCount() > 0) {

                            do {
                                buildJointlyGoalsEvaluationResultXmlTagWithData(xmlSerializer, allGoalsEvaluationResultsReadyToSend);
                                send_goals_evaluation_result_info = true;
                            } while (allGoalsEvaluationResultsReadyToSend.moveToNext());
                        }

                        // build xml for all debetable goals comments
                        if (allDebetableCommentsReadyToSend != null && allDebetableCommentsReadyToSend.getCount() > 0) {

                            do {
                                buildCommentDebetableXmlTagWithData(xmlSerializer, allDebetableCommentsReadyToSend);
                                send_debetable_goals_comment_info = true;
                            } while (allDebetableCommentsReadyToSend.moveToNext());
                        }

                        // build xml for all connect book messages result
                        if (allConnectBookMessagesReadyToSend != null && allConnectBookMessagesReadyToSend.getCount() > 0) {

                            do {
                                buildConnectBookMessageXmlTagWithData(xmlSerializer, allConnectBookMessagesReadyToSend);
                                send_connect_book_messages_result_info = true;
                            } while (allConnectBookMessagesReadyToSend.moveToNext());
                        }

                        // build xml for all meetings data set result
                        if (allMeetingsReadyToSend != null && allMeetingsReadyToSend.getCount() > 0) {

                            do {
                                buildMeetingDataXmlTagWithData(xmlSerializer, allMeetingsReadyToSend);
                                send_meeting_data_result_info = true;
                            } while (allMeetingsReadyToSend.moveToNext());
                        }
                    }

                    // build in every case (associated or not associated) xml for all messages result
                    if (allMessagesReadyToSend != null && allMessagesReadyToSend.getCount() > 0) {
                        do {
                            buildMessageXmlTagWithData(xmlSerializer, allMessagesReadyToSend, clientIdSet, contactIdSet);
                            send_message_data_result_info = true;
                        } while (allMessagesReadyToSend.moveToNext());
                    }

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    //Log.d("Send", "XMLCODE=" + textparam);

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionAskForNewDataToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Log.d("+++++++++ New Data Send", "Antwort:" + stringBuilder.toString().trim());

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    //++++++++++++++++ db status update section +++++++++++++++++++++++++++++++++

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    // build intent to update ui for user
                    Intent tmpIntentUpdateUiForUser = new Intent();
                    tmpIntentUpdateUiForUser.setAction("ACTIVITY_STATUS_UPDATE");

                    // check association -> app is connected to server
                    if (clientIdSet) {

                        // set status of now comment to 1 -> send successfull
                        if (allCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_now_comment_info) {
                                allCommentsReadyToSend.moveToFirst();
                                do {
                                    Long rowId = allCommentsReadyToSend.getLong(allCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                    myDb.updateStatusOurArrangementComment(rowId, 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        myDb.updateWriteTimeOurArrangementComment(rowId, globalServerTime);
                                    } // update write time for comment with server time
                                } while (allCommentsReadyToSend.moveToNext());
                                // intent for our arrangement comment -> refresh list view
                                tmpIntentUpdateUiForUser.putExtra("OurArrangementCommentSendInBackgroundRefreshView", "1"); // refresh list view in our arrangement comment
                            }
                        }

                        // set status of sketch comment to 1 -> send successfull
                        if (allSketchCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_sketch_comment_info) {
                                allSketchCommentsReadyToSend.moveToFirst();
                                do {
                                    Long rowId = allSketchCommentsReadyToSend.getLong(allSketchCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                    myDb.updateStatusOurArrangementSketchComment(allSketchCommentsReadyToSend.getLong(allSketchCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of sketch comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        myDb.updateWriteTimeOurArrangementSketchComment(rowId, globalServerTime);
                                    } // update write time for sketch comment with server time
                                } while (allSketchCommentsReadyToSend.moveToNext());
                                // intent for our arrangement sketch comment -> refresh list view
                                tmpIntentUpdateUiForUser.putExtra("OurArrangementSketchCommentSendInBackgroundRefreshView", "1"); // refresh list view in our arrangement sketch comment
                            }
                        }

                        // set status of evaluation result for arrangement to 1 -> send successfull
                        if (allArrangementEvaluationResultsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_arrangement_evaluation_result_info) {
                                allArrangementEvaluationResultsReadyToSend.moveToFirst();
                                do {
                                    Long rowId = allArrangementEvaluationResultsReadyToSend.getLong(allArrangementEvaluationResultsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                    myDb.updateStatusOurArrangementEvaluation(allArrangementEvaluationResultsReadyToSend.getLong(allArrangementEvaluationResultsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of sketch comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        myDb.updateWriteTimeOurArrangementEvaluationResult(rowId, globalServerTime);
                                    } // update write time for sketch comment with server time
                                }
                                while (allArrangementEvaluationResultsReadyToSend.moveToNext());
                            }
                        }

                        // set status of evaluation jointly goal to 1 -> send successfull
                        if (allGoalsEvaluationResultsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_goals_evaluation_result_info) {
                                allGoalsEvaluationResultsReadyToSend.moveToFirst();
                                do {
                                    Long rowId = allGoalsEvaluationResultsReadyToSend.getLong(allGoalsEvaluationResultsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                    myDb.updateStatusOurGoalsEvaluation(allGoalsEvaluationResultsReadyToSend.getLong(allGoalsEvaluationResultsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of evaluation result to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        myDb.updateWriteTimeOurGoalsEvaluationResult(rowId, globalServerTime);
                                    } // update write time for sketch comment with server time
                                } while (allGoalsEvaluationResultsReadyToSend.moveToNext());
                            }
                        }

                        // set status of jointly goal comment to 1 -> send successfull
                        if (allJointlyGoalsCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_jointly_goals_comment_info) {
                                allJointlyGoalsCommentsReadyToSend.moveToFirst();
                                do {
                                    Long rowId = allJointlyGoalsCommentsReadyToSend.getLong(allJointlyGoalsCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                    myDb.updateStatusOurGoalsJointlyGoalsComment(allJointlyGoalsCommentsReadyToSend.getLong(allJointlyGoalsCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        myDb.updateWriteTimeOurGoalsJointlyComment(rowId, globalServerTime);
                                    } // update write time for jointly comment with server time
                                } while (allJointlyGoalsCommentsReadyToSend.moveToNext());
                                // intent for our goals jointly comment -> refresh list view
                                tmpIntentUpdateUiForUser.putExtra("OurGoalsJointlyCommentSendInBackgroundRefreshView", "1"); // refresh list view in our goals jointly comment
                            }
                        }

                        // set status of debetable goal comment to 1 -> send successfull
                        if (allDebetableCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_debetable_goals_comment_info) {
                                allDebetableCommentsReadyToSend.moveToFirst();
                                do {
                                    Long rowId = allDebetableCommentsReadyToSend.getLong(allDebetableCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                    myDb.updateStatusOurGoalsDebetableComment(allDebetableCommentsReadyToSend.getLong(allDebetableCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of debetable comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        myDb.updateWriteTimeOurGoalsDebetableComment(rowId, globalServerTime);
                                    } // update write time for debetable comment with server time
                                } while (allDebetableCommentsReadyToSend.moveToNext());
                                // intent for our goals debetable comment -> refresh list view
                                tmpIntentUpdateUiForUser.putExtra("OurGoalsDebetableCommentSendInBackgroundRefreshView", "1"); // refresh list view in our goals debetable comment
                            }
                        }

                        // set status of connect book messages to 1 -> send successfull
                        if (allConnectBookMessagesReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_connect_book_messages_result_info) {
                                allConnectBookMessagesReadyToSend.moveToFirst();
                                do {
                                    Long rowId = allConnectBookMessagesReadyToSend.getLong(allConnectBookMessagesReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                    myDb.updateStatusConnectBookMessage(allConnectBookMessagesReadyToSend.getLong(allConnectBookMessagesReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of message to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        myDb.updateWriteTimeConnectBookMessage(rowId, globalServerTime);
                                    } // update write time for connect book messages with server time
                                } while (allConnectBookMessagesReadyToSend.moveToNext());

                                // intent for connect book activity -> refresh list view
                                tmpIntentUpdateUiForUser.putExtra("ConnectBookMessageNewOrSend", "1"); // refresh list view in connect book messages
                            }
                        }

                        // set status of meeting data to 1 -> send successfull
                        if (allMeetingsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_meeting_data_result_info) {
                                allMeetingsReadyToSend.moveToFirst();
                                do {
                                    myDb.updateStatusMeetingAndSuggestion(allMeetingsReadyToSend.getLong(allMeetingsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of meeting data to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                    if (globalServerTime > 0) {
                                        postUpdateMeetingAndSuggestionDataInDB(allMeetingsReadyToSend, globalServerTime, myDb);
                                    }
                                } while (allMeetingsReadyToSend.moveToNext());
                                // intent for meeting activity -> refresh view
                                tmpIntentUpdateUiForUser.putExtra("MeetingSendInBackgroundRefreshView", "1"); // refresh view in any meeting fragement
                            }
                        }
                    }

                    // set status of messages to 1 -> send successfull -> in every case (associated or not associated)
                    if (allMessagesReadyToSend != null) {
                        if (returnMap.get("SendSuccessfull").equals("1") && send_message_data_result_info) {
                            allMessagesReadyToSend.moveToFirst();
                            do {
                                Long rowId = allMessagesReadyToSend.getLong(allMessagesReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID));
                                myDb.updateStatusMessage(allMessagesReadyToSend.getLong(allMessagesReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of message to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                if (globalServerTime > 0) {myDb.updateWriteTimeMessage(rowId, globalServerTime); } // update write time for messages with server time
                            } while (allMessagesReadyToSend.moveToNext());

                            // intent for message activity -> refresh list view
                            tmpIntentUpdateUiForUser.putExtra("MessageMessageNewOrSend", "1"); // refresh list view in message activity
                        }
                    }

                    //++++++++++++++++ end db status update section +++++++++++++++++++++++++++++++++

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    // check is app visible and in foreground -> only then send broadcast to receiver!
                    if (EfbLifecycle.isApplicationVisible() && EfbLifecycle.isApplicationInForeground()) {

                        // send broadcast for update ui for user
                        context.sendBroadcast(tmpIntentUpdateUiForUser);

                        // send intent to broadcast receiver -> the receiver looks for relevant data in intent
                        Intent tmpIntent;
                        tmpIntent = translateMapToIntent(returnMap);
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }

                    // show notification when needed
                    setNotificationToScreen(returnMap);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }


    private void setNotificationToScreen (Map<String, String> returnMap) {

        String notificationContentTitle;
        Intent notificationIntent;
        Intent  mainActivityIntent;
        PendingIntent contentPendingIntent;
        TaskStackBuilder stackBuilder;

        // check is app visible and in foreground -> only then set notification
        if (!EfbLifecycle.isApplicationVisible() && !EfbLifecycle.isApplicationInForeground()) {

            // get notification manager
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // new notification builder without sound
            NotificationCompat.Builder mBuilderNoSound = new NotificationCompat.Builder(context, ConstansClassMain.uniqueNotificationChannelIdNoSound);

            // new notification builder with sound
            NotificationCompat.Builder mBuilderSound = new NotificationCompat.Builder(context, ConstansClassMain.uniqueNotificationChannelIdSound);

            // set basic things to all notifications (channel with or without sound)
            mBuilderNoSound.setSmallIcon(R.drawable.notification_smile);
            mBuilderNoSound.setAutoCancel(true);
            mBuilderSound.setSmallIcon(R.drawable.notification_smile);
            mBuilderSound.setAutoCancel(true);

            // needed for back stack -> start main activity after pressing back
            mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // notification for arrangement
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, true) && returnMap.get("OurArrangement").equals("1") && (returnMap.get("OurArrangementNow").equals("1") || returnMap.get("OurArrangementSketch").equals("1") || returnMap.get("OurArrangementNowComment").equals("1") || returnMap.get("OurArrangementSketchComment").equals("1"))) {

                // get our arrangement notification string
                notificationContentTitle = this.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurArrangement);

                // set intent/ pending intent to start connect book
                notificationIntent = new Intent(getApplicationContext(), ActivityOurArrangement.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // generate back stack for pending intent and add main activity
                stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(mainActivityIntent);

                // add intent for arrangement
                stackBuilder.addNextIntent(notificationIntent);

                // generate pending intent
                contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                // set notification attributes (without or with sound)
                mBuilderNoSound.setContentTitle(notificationContentTitle);
                mBuilderNoSound.setContentIntent(contentPendingIntent);
                mBuilderSound.setContentTitle(notificationContentTitle);
                mBuilderSound.setContentIntent(contentPendingIntent);

                // sound on/off for arrangement?
                if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, true)) {
                    // show notification with sound
                    mNotificationManager.notify(001, mBuilderSound.build());
                }
                else {
                    // show notification without sound
                    mNotificationManager.notify(002, mBuilderNoSound.build());
                }
            }

            // notification for connect book
            if (returnMap.get("ConnectBook").equals("1") && returnMap.get("ConnectBookMessageNewOrSend").equals("1") && prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, true)) {

                // get connect book notification string
                notificationContentTitle = this.getResources().getString(R.string.exchangeServiceNotificationTextNewConnectBookMessage);

                // set intent/ pending intent to start connect book
                notificationIntent = new Intent(getApplicationContext(), ActivityConnectBook.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // generate back stack for pending intent and add main activity
                stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(mainActivityIntent);

                // add intent for connect book
                stackBuilder.addNextIntent(notificationIntent);

                // generate pending intent
                contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                // set notification attributes (without or with sound)
                mBuilderNoSound.setContentTitle(notificationContentTitle);
                mBuilderNoSound.setContentIntent(contentPendingIntent);
                mBuilderSound.setContentTitle(notificationContentTitle);
                mBuilderSound.setContentIntent(contentPendingIntent);

                // sound on/off for connect book?
                if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, true)) {
                    // show notification with sound
                    mNotificationManager.notify(001, mBuilderSound.build());
                }
                else {
                    // show notification without sound
                    mNotificationManager.notify(002, mBuilderNoSound.build());
                }
            }

            // notification for goal
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, true) && returnMap.get("OurGoals").equals("1") && (returnMap.get("OurGoalsJointlyNow").equals("1") || returnMap.get("OurGoalsDebetableNow").equals("1") || returnMap.get("OurGoalsJointlyComment").equals("1") || returnMap.get("OurGoalsDebetableComment").equals("1"))) {

                // get our goals notification string
                notificationContentTitle = this.getResources().getString(R.string.exchangeServiceNotificationTextNewEventOurGoals);

                // set intent/ pending intent to start goals
                notificationIntent = new Intent(getApplicationContext(), ActivityOurGoals.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // generate back stack for pending intent and add main activity
                stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(mainActivityIntent);

                // add intent for goals
                stackBuilder.addNextIntent(notificationIntent);

                // generate pending intent
                contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                // set notification attributes (without or with sound)
                mBuilderNoSound.setContentTitle(notificationContentTitle);
                mBuilderNoSound.setContentIntent(contentPendingIntent);
                mBuilderSound.setContentTitle(notificationContentTitle);
                mBuilderSound.setContentIntent(contentPendingIntent);

                // sound on/off for our goal?
                if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, true)) {
                    // show notification with sound
                    mNotificationManager.notify(001, mBuilderSound.build());
                }
                else {
                    // show notification without sound
                    mNotificationManager.notify(002, mBuilderNoSound.build());
                }
            }

            // notification for message
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_Message, true) && returnMap.get("MessagesMessage").equals("1") && returnMap.get("MessageMessageNewOrSend").equals("1") ) {

                // get new message notification string
                notificationContentTitle = this.getResources().getString(R.string.exchangeServiceNotificationTextNewEventMessage);

                // set intent/ pending intent to start message
                notificationIntent = new Intent(getApplicationContext(), ActivityMessage.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // generate back stack for pending intent and add main activity
                stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(mainActivityIntent);

                // add intent for message
                stackBuilder.addNextIntent(notificationIntent);

                // generate pending intent
                contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                // set notification attributes (without or with sound)
                mBuilderNoSound.setContentTitle(notificationContentTitle);
                mBuilderNoSound.setContentIntent(contentPendingIntent);
                mBuilderSound.setContentTitle(notificationContentTitle);
                mBuilderSound.setContentIntent(contentPendingIntent);

                // sound on/off for message?
                if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_Message, true)) {
                    // show notification with sound
                    mNotificationManager.notify(001, mBuilderSound.build());
                }
                else {
                    // show notification without sound
                    mNotificationManager.notify(002, mBuilderNoSound.build());
                }
            }
        }
    }

    // send now comment arrangement to server and get answer from server
    public class ExchangeTaskSendNowCommentArrangement implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendNowCommentArrangement (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // get comment from db
                Cursor commentData = myDb.getOneRowOurArrangementComment(dbId);

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send comment and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_CommentArrangement);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for comment with data
                    buildCommentNowXmlTagWithData(xmlSerializer, commentData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendNewCommentArrangementToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull with server time?
                        myDb.updateStatusOurArrangementComment(dbId, 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // update write time of comment with server time
                        if (globalServerTime > 0) {myDb.updateWriteTimeOurArrangementComment(dbId, globalServerTime); } // update write time for comment with server time

                        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
                        Intent tmpIntent = translateMapToIntent(returnMap);
                        tmpIntent.putExtra("Message", context.getResources().getString(R.string.toastMessageCommentSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    } else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageCommentSendNotSuccessfull);
                        String command = "";
                        sendIntentBroadcastSendingNotSuccessefull(message, command);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull(message, command);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull(message, command);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageCommentNotSendSuccessfullNoNetwork);
                String command = "";
                sendIntentBroadcastSendingNotSuccessefull (message, command);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }


    // send sketch comment arrangement to server and get answer from server
    public class ExchangeTaskSendSketchCommentArrangement implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendSketchCommentArrangement (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();

        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get comment from db
                Cursor commentData = myDb.getOneRowOurArrangementSketchComment(dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send sketch comment and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_CommentSketchArrangement);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for sketch comment with data
                    buildCommentSketchXmlTagWithData (xmlSerializer, commentData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendNewSketchCommentArrangementToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusOurArrangementSketchComment (dbId, 1); // set status of sketch comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // update write time of sketch comment in db
                        if (globalServerTime > 0) {myDb.updateWriteTimeOurArrangementSketchComment(dbId, globalServerTime); } // update write time for sketch comment with server time

                        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageSketchCommentSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageSketchCommentSendNotSuccessfull);
                        String command = "";
                        sendIntentBroadcastSendingNotSuccessefull (message, command);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageSketchCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageSketchCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageCommentNotSendSuccessfullNoNetwork);
                String command = "";
                sendIntentBroadcastSendingNotSuccessefull (message, command);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }

    // send evaluation result arrangement to server and get answer from server
    public class ExchangeTaskSendEvaluationResultArrangement implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendEvaluationResultArrangement (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get evaluation result from db
                Cursor evaluationResultData = myDb.getOneRowEvaluationResultArrangement (dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send sketch comment and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_EvaluationResultArrangement);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for evaluation result with data
                    buildArrangementEvaluationResultXmlTagWithData (xmlSerializer, evaluationResultData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendEvaluationResultArrangementToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    if (returnMap.get("SendSuccessfull").equals("1")) {
                        myDb.updateStatusOurArrangementEvaluation (dbId, 1); // set status of evaluation to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // set result time of evaluationresult to server time
                        if (globalServerTime > 0) {myDb.updateWriteTimeOurArrangementEvaluationResult(dbId, globalServerTime);
                        } // update evaluation result time with server time
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send evaluation result to server later

                // send intent to receiver in OurArrangementFragmentSketch to show toast to user
                Intent tmpIntent = new Intent();
                tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                tmpIntent.putExtra("SendSuccessfull","0");
                tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageCommentNotSendSuccessfullNoNetwork));
                context.sendBroadcast(tmpIntent);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }


    // send message to activity that sending was not successfull
    public void sendIntentBroadcastSendingNotSuccessefull (String message, String command) {

        SystemClock.sleep(2000); // wait two second because fragment change and with in the broadcast receiver

        // send intent to receiver that sending not successfull
        Intent tmpIntent = new Intent();
        tmpIntent.putExtra("Message", message);
        tmpIntent.putExtra("Command", command);
        tmpIntent.putExtra("SendNotSuccessfull","1");
        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
        context.sendBroadcast(tmpIntent);
    }
    // +++++++++++++++++++++++++ task exchange goals +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    // send jointly comment goals to server and get answer from server
    public class ExchangeTaskSendJointlyCommentGoals implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendJointlyCommentGoals (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get comment from db
                Cursor commentData = myDb.getOneRowOurGoalsJointlyComment(dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send comment and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_JointlyGoalsComment);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for jointly comment with data
                    buildJointlyCommentXmlTagWithData (xmlSerializer, commentData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendNewCommentJointlyGoalsToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusOurGoalsJointlyGoalsComment (dbId, 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // update write time of jointly comment in db
                        if (globalServerTime > 0) {myDb.updateWriteTimeOurGoalsJointlyComment(dbId, globalServerTime); } // update write time for jointly comment with server time

                        // send intent to receiver in OurGoalsFragmentJointlyGoals to update listView OurGoals (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendNotSuccessfull);
                        String command = "";
                        sendIntentBroadcastSendingNotSuccessefull (message, command);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentNotSendSuccessfullNoNetwork);
                String command = "";
                sendIntentBroadcastSendingNotSuccessefull (message, command);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }

    // send evaluation result jointly goal to server and get answer from server
    public class ExchangeTaskSendJointlyGoalsEvaluationResult implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendJointlyGoalsEvaluationResult (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get evaluation result from db
                Cursor evaluationResultData = myDb.getOneRowEvaluationResultGoals (dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send evaluation result and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_JointlyGoalsEvaluationResult);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for jointly evaluation with data
                    buildJointlyGoalsEvaluationResultXmlTagWithData (xmlSerializer, evaluationResultData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendEvaluationResultJointlyGoalsToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    if (returnMap.get("SendSuccessfull").equals("1")) {
                        myDb.updateStatusOurGoalsEvaluation (dbId, 1); // set status of evaluation to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send evaluation result to server later

                // send intent to receiver in OurArrangementFragmentSketch to show toast to user
                Intent tmpIntent = new Intent();
                tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                tmpIntent.putExtra("SendSuccessfull","0");
                tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageJointlyGoalsCommentNotSendSuccessfullNoNetwork));
                context.sendBroadcast(tmpIntent);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }


    // send debetable comment goal to server and get answer from server
    public class ExchangeTaskSendDebetableGoalCommentResult implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;


        // Constructor
        public ExchangeTaskSendDebetableGoalCommentResult (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get comment from db
                Cursor commentData = myDb.getOneRowOurGoalsDebetableComment (dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send sketch comment and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_DebetableGoalsComment);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for sketch comment with data
                    buildCommentDebetableXmlTagWithData (xmlSerializer, commentData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendNewCommentDebetableGoalsToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusOurGoalsDebetableComment (dbId, 1); // set status of debetable comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // update write time of debetable comment in db
                        if (globalServerTime > 0) {myDb.updateWriteTimeOurGoalsDebetableComment(dbId, globalServerTime); } // update write time for debetable comment with server time

                        // send intent to receiver in OurGoalsFragmentDebetableComment to update listView OurGoals (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageDebetableCommentSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageeDebetableCommentSendNotSuccessfull);
                        String command = "";
                        sendIntentBroadcastSendingNotSuccessefull (message, command);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageeDebetableCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageeDebetableCommentSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageeDebetableCommentNotSendSuccessfullNoNetwork);
                String command = "";
                sendIntentBroadcastSendingNotSuccessefull (message, command);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }
    // +++++++++++++++++++++++++ end task exchange goals +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



    // +++++++++++++++++++++++++ task exchange connect book +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // send connect book message to server and get answer from server
    public class ExchangeTaskSendConnectBookMessage implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendConnectBookMessage (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get comment from db
                Cursor messageData = myDb.getOneRowChatMessage (dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send connect book message and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_ConnectBookMessage);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for connect book message with data
                    buildConnectBookMessageXmlTagWithData (xmlSerializer, messageData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendConnectBookMessageToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusConnectBookMessage (dbId, 1); // set status of message to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // update write time of debetable comment in db
                        if (globalServerTime > 0) {myDb.updateWriteTimeConnectBookMessage (dbId, globalServerTime); } // update write time for connect book message with server time

                        // send intent to receiver in OurGoalsFragmentDebetableComment to update listView OurGoals (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastConnectBookMessageSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastConnectBookMessageSendNotSuccessfull);
                        String command = "";
                        sendIntentBroadcastSendingNotSuccessefull (message, command);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastConnectBookMessageSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);

                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastConnectBookMessageSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);

                } catch (XmlPullParserException e) {
                    e.printStackTrace();

                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastConnectBookMessageSendNoNetwork);
                String command = "";
                sendIntentBroadcastSendingNotSuccessefull (message, command);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }
    // +++++++++++++++++++++++++ end task exchange connect book +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    // +++++++++++++++++++++++++ task exchange meeting +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // send complete meeting data set to server and get answer from server
    public class ExchangeTaskSendMeetingData implements Runnable {

        // id of the data row in db
        private Long dbId;

        // receiver broadcast -> to cancele double show of messages in class meeting
        private String receiverBroadcast;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendMeetingData (Context context, Long dbid, String mReceiverBroadcast) {

            // id of the data row in db
            this.dbId = dbid;

            // receiver broadcast -> to cancele double show of messages in class meeting
            this.receiverBroadcast = mReceiverBroadcast;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get meeting from db
                Cursor meetingData = myDb.getOneRowMeetingsOrSuggestion(dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send meeting data and client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_MeetingData);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // start tag client id
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.text(tmpClientId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for meeting with data
                    buildMeetingDataXmlTagWithData (xmlSerializer, meetingData);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendMeetingDataToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull

                        // update meeting status -> separate from other changes
                        myDb.updateStatusMeetingAndSuggestion (dbId, 1); // set status of meeting to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // update some data after sending in meeting db table (like write time,etc.)
                        if (globalServerTime > 0) {
                            postUpdateMeetingAndSuggestionDataInDB(meetingData, globalServerTime, myDb);
                        }

                        // send intent to receiver in Meeting to update listView Meeting (when active)
                        String command = "ask_parent_activity";
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Command", command);
                        tmpIntent.putExtra("receiverBroadcast", receiverBroadcast);
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = "";
                        String command = "ask_parent_activity";
                        sendIntentBroadcastSendingNotSuccessefull (message, command);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = "";
                    String command = "ask_parent_activity";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = "";
                    String command = "ask_parent_activity";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send meeting data to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageMeetingDataNotSendSuccessfullNoNetwork);
                String command = "look_message";
                sendIntentBroadcastSendingNotSuccessefull (message, command);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }


    // do some update work in meeting suggestion db table (like write time, etc.)
    void postUpdateMeetingAndSuggestionDataInDB(Cursor meetingData, Long globalServerTime, DBAdapter myDb) {

        String updateOrder = meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_UPDATE_ORDER));

        Long dbId = meetingData.getLong(meetingData.getColumnIndex(DBAdapter.KEY_ROWID));

        if (updateOrder != null && updateOrder.length() > 0) {

            myDb.postUpdateWriteTimeMeetingSuggestion(dbId, updateOrder, globalServerTime);

        }
    }

    // +++++++++++++++++++++++++ end task exchange meeting +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    // +++++++++++++++++++++++++ task exchange message +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // send current message to server and get answer from server
    public class ExchangeTaskSendMessageData implements Runnable {

        // id of the data row in db
        private Long dbId;

        // context of task
        Context context;

        // shared prefs
        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;

        // return information for change
        Map<String, String> returnMap;

        // Constructor
        public ExchangeTaskSendMessageData (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

            // context of task
            this.context = context;

            // init the prefs
            prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
            prefsEditor = prefs.edit();
        }

        // the task
        public void run() {

            // init the DB
            DBAdapter myDb = new DBAdapter(context);

            Boolean clientIdSet = false;
            Boolean contactIdSet = false;

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                // get message from db
                Cursor messageData = myDb.getOneRowMessage (dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // get contact id from prefs
                String tmpContactId = prefs.getString(ConstansClassSettings.namePrefsContactId, "");

                // check for client or contact id
                if (tmpClientId.length() > 0) {clientIdSet = true;}
                if (tmpContactId.length() > 0 && !clientIdSet) {contactIdSet = true;}

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    xmlSerializer.setOutput(writer);

                    //Start Document
                    xmlSerializer.startDocument("UTF-8", true);
                    xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

                    // Open Tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                    // start tag main order -> send associated or not associated message
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                    if (clientIdSet) {
                        xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_MessageAssociated);
                    }
                    else {
                        xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_MessageNotAssociated);
                    }
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                    // check client or contact id set?
                    if (clientIdSet) {
                        // start tag client id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        xmlSerializer.text(tmpClientId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        // start empty tag contact id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                    }
                    else {
                        // start empty tag client id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        // start tag contact id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
                        xmlSerializer.text(tmpContactId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

                    }

                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
                    xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

                    // end tag main
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                    // build xml tag for message with data
                    buildMessageXmlTagWithData (xmlSerializer, messageData, clientIdSet, contactIdSet);

                    // end tag smartEfb
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                    xmlSerializer.endDocument();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                // and send xml text to server
                try {
                    // prepair data to send
                    String textparam = "xmlcode=" + URLEncoder.encode(writer.toString(), "UTF-8");

                    // set url and parameters
                    URL scripturl = new URL(ConstansClassSettings.urlConnectionSendMessageToServer);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();

                    // set timeout for connection
                    connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                    connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();

                    contentWriter.close();

                    // get answer from input
                    InputStream answerInputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    // convert input stream to string
                    String currentRow;
                    try {
                        while ((currentRow = reader.readLine()) != null) {
                            stringBuilder.append(currentRow);
                            stringBuilder.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    // set global server time for comments, messages, etc.
                    Long globalServerTime = 0L;
                    if (returnMap.get("AskForTimeSuccessfull").equals("1") && returnMap.get("ServerTimeInMills").length() > 0) {
                        globalServerTime = Long.valueOf(returnMap.get("ServerTimeInMills")) * 1000; // make global server time in mills
                    }

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusMessage (dbId, 1); // set status of message to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // update write time of message in db
                        if (globalServerTime > 0) {myDb.updateWriteTimeMessage (dbId, globalServerTime); } // update write time for message with server time

                        // send intent to receiver in Activity message to update listView (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageSendNotSuccessfull);
                        String command = "";
                        sendIntentBroadcastSendingNotSuccessefull (message, command);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastConnectBookMessageSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);

                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastConnectBookMessageSendNotSuccessfull);
                    String command = "";
                    sendIntentBroadcastSendingNotSuccessefull (message, command);

                } catch (XmlPullParserException e) {
                    e.printStackTrace();

                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastConnectBookMessageSendNoNetwork);
                String command = "";
                sendIntentBroadcastSendingNotSuccessefull (message, command);
            }

            // close db connection
            myDb.close();

            // stop the task with service
            stopSelf();
        }
    }
    // +++++++++++++++++++++++++ end task exchange message +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //++++++++++++++++++ END TASK AREA ++++++++++++++++++++++++++++++++++++++++++++++


    // convert the returnMap from the EfbXMLParser to extras in Intents for the fragments
    public Intent translateMapToIntent (Map<String, String> returnMap) {

        Intent intent = new Intent();

        for( Map.Entry <String, String> entry : returnMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // put to intent when value = "1"
            if (value.equals("1")) {
                intent.putExtra(key,"1");
            }
        }

        // translate "ErrorText" to intent, when given
        String tmpErrorTextForTranslate = returnMap.get("ErrorText");
        if (tmpErrorTextForTranslate != null && tmpErrorTextForTranslate.length() > 0) {
            intent.putExtra("ErrorText",tmpErrorTextForTranslate);
        }


        return intent;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        this.isRunning = false;
    }


    @Override
    public boolean onStopCurrentWork() {

        return super.onStopCurrentWork();
    }


    @Override
    // check the commands for service and start the task to handle the command
    protected void onHandleWork(@NonNull Intent intent) {

        // Extras from intent that holds data
        Bundle intentExtras;

        // get data from intent
        intentExtras = intent.getExtras();

        if (intentExtras != null && !this.isRunning) {// && !prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            int tmpCommectionStatus = prefs.getInt(ConstansClassSettings.namePrefsConnectingStatus, 0);

            // get command from intent extras
            String command = intentExtras.getString("com");
            if (command == null) {command="";}

            // get db id from intent extras
            Long dbId = intentExtras.getLong("dbid", 0);

            // get receiver broadcast
            String receiverBroadcast = intentExtras.getString("receiverBroadcast", ""); // this is needed for double "mystic" messages receives in view pager like suggestion and client suggestion
            if (receiverBroadcast == null) {receiverBroadcast = "";}

            // check commands
            if (command.equals("ask_new_data")) { // Ask server for new data

                // generate new background task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskCheckNewContent(context));
                // set task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_now_comment_arrangement") && dbId > 0 && tmpCommectionStatus == 3) { // send new arrangement comment to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendNowCommentArrangement(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_sketch_comment_arrangement") && dbId > 0 && tmpCommectionStatus == 3) { // send new sketch arrangement comment to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendSketchCommentArrangement(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_evaluation_result_arrangement") && dbId > 0 && tmpCommectionStatus == 3) { // send new evaluation result to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendEvaluationResultArrangement(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_jointly_comment_goal") && dbId > 0 && tmpCommectionStatus == 3) { // send jointly goal comment to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendJointlyCommentGoals(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_evaluation_result_goal") && dbId > 0 && tmpCommectionStatus == 3) { // send jointly goal evaluation result to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendJointlyGoalsEvaluationResult(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            }  else if (command.equals("send_debetable_comment_goal") && dbId > 0 && tmpCommectionStatus == 3) { // send debetable comment result to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendDebetableGoalCommentResult(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_connectbook_message") && dbId > 0 && tmpCommectionStatus == 3) { // send connect book message to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendConnectBookMessage(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_meeting_data") && dbId > 0 && tmpCommectionStatus == 3) { // send meeting data to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendMeetingData(context, dbId, receiverBroadcast));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();

            } else if (command.equals("send_current_message") && dbId > 0) { // send current message data to server

                // generate new send task
                this.backgroundThread = new Thread(new ExchangeJobIntentServiceEfb.ExchangeTaskSendMessageData(context, dbId));
                // task is running
                this.isRunning = true;
                // start task
                this.backgroundThread.start();
            }
        }

    }


    public void buildCommentNowXmlTagWithData(XmlSerializer xmlSerializer, Cursor commentData) {

        try {
            // open comment now arrangement tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment);

            // start tag comment now arrangement order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Order );
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Order );

            // start tag comment text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Comment);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_Comment);

            // start tag author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_AuthorName);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_AuthorName);

            // start tag comment time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_CommentLocaleTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME))/1000)); // this is the local smartphone time; server puts his time self! // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_CommentLocaleTime);

            // start tag arrangement time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_DateOfArrangement);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_ARRANGEMENT_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_DateOfArrangement);

            // start tag block number
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_BlockId);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_BLOCK_ID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_BlockId);

            // start tag server id arrangement
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_ServerIdArrangement);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_SERVER_ID_ARRANGEMENT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_ServerIdArrangement);

            // end tag comment now arrangement
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildCommentSketchXmlTagWithData(XmlSerializer xmlSerializer, Cursor commentData) {

        try {

            // open comment sketch arrangement tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment);

            // start tag comment sketch arrangement order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_Order );
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_Order );

            // start tag comment text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_Comment);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_Comment);

            // start tag author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_AuthorName);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_AuthorName);

            // start tag comment time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_CommentLocaleTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_CommentLocaleTime);

            // start tag sketch arrangement time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_DateOfArrangement);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_ARRANGEMENT_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_DateOfArrangement);

            // start tag sketch comment result question A
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionA);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionA);

            // start tag sketch comment result question B
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionB);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION2))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionB);

            // start tag sketch comment result question C
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionC);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION3))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ResultQuestionC);

            // start tag block number of sketch arrangement
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_BlockId);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_BLOCK_ID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_BlockId);

            // start tag server id sketch arrangement
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ServerIdArrangement);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_SERVER_ID_ARRANGEMENT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_ServerIdArrangement);

            // end tag sketch comment
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildArrangementEvaluationResultXmlTagWithData (XmlSerializer xmlSerializer, Cursor evaluationData) {

        try {

            // open evaluation result arrangement tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate);

            // start tag evaluate arrangement order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Order);
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Order);

            // start tag evalute remarks
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Remarks);
            xmlSerializer.text(evaluationData.getString(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Remarks);

            // start tag evaluate author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_AuthorName);
            xmlSerializer.text(evaluationData.getString(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_AuthorName);

            // start tag evaluate result time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_LocaleTime);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_LOCAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_LocaleTime);

            // start tag evaluate arrangement time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_DateOfArrangement);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_DateOfArrangement);

            // start tag evaluate result question A
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionA);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionA);

            // start tag evaluate result question B
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionB);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionB);

            // start tag evaluate result question C
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionC);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionC);

            // start tag evaluate result question D
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionD);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionD);

            // start tag evaluate start time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_StartTime);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_StartTime);

            // start tag evaluate end time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_EndTime);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_EndTime);

            // start tag server id arrangement
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ServerIdArrangement);
            xmlSerializer.text(evaluationData.getString(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ServerIdArrangement);

            // start tag block id of arrangements
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_BlockId);
            xmlSerializer.text(evaluationData.getString(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_BlockId);

            // end tag evaluation result arrangement
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildJointlyGoalsEvaluationResultXmlTagWithData (XmlSerializer xmlSerializer, Cursor evaluationResultData) {

        try {

            // open evaluation result jointly goals tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate);

            // start tag evaluate jointly goals order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_Order);
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_Order);

            // start tag evalute remarks
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_Remarks);
            xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_REMARKS)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_Remarks);

            // start tag evaluate author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_AuthorName);
            xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_USERNAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_AuthorName);

            // start tag evaluate result time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_LocaleTime);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_LOCAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_LocaleTime);

            // start tag evaluate arrangement time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_DateOfGoal);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_GOAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_DateOfGoal);

            // start tag evaluate result question A
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionA);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION1))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionA);

            // start tag evaluate result question B
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionB);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION2))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionB);

            // start tag evaluate result question C
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionC);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION3))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionC);

            // start tag evaluate result question D
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionD);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_QUESTION4))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultQuestionD);

            // start tag evaluate start time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_StartTime);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_StartTime);

            // start tag evaluate end time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_EndTime);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_EndTime);

            // start tag server id arrangement
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ServerIdGoal);
            xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_SERVER_ID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ServerIdGoal);

            // start tag block id of arrangements
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_BlockId);
            xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_BLOCKID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_BlockId);

            // end tag evaluation result arrangement
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildJointlyCommentXmlTagWithData (XmlSerializer xmlSerializer, Cursor commentData) {

        try {
            // open comment jointly goals tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment);

            // start tag comment jointly goals order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_Order );
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_Order );

            // start tag comment text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_Comment);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_Comment);

            // start tag author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_AuthorName);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_AuthorName);

            // start tag comment time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_CommentLocaleTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_CommentLocaleTime);

            // start tag goal time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_DateOfJointlyGoal);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_GOAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_DateOfJointlyGoal);

            // start tag block number
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_BlockId);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_BLOCK_ID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_BlockId);

            // start tag server id goal
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_ServerGoalId);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_SERVER_ID_GOAL)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_ServerGoalId);

            // end tag comment jointly goal
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildCommentDebetableXmlTagWithData (XmlSerializer xmlSerializer, Cursor commentData) {

        try {

            // open comment debetable comment tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment);

            // start tag comment debetable goal order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_Order);
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_Order);

            // start tag comment text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_Comment);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_Comment);

            // start tag author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_AuthorName);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_AuthorName);

            // start tag comment time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_CommentLocaleTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_CommentLocaleTime);

            // start tag debetable goal time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_DateOfDebetableGoal);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_GOAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_DateOfDebetableGoal);

            // start tag debetable comment result question A
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionA);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionA);

            // start tag debetable comment result question B
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionB);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION2))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionB);

            // start tag debetable comment result question C
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionC);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION3))));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ResultQuestionC);

            // start tag block number of debetable comment
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_BlockId);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_BLOCK_ID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_BlockId);

            // start tag server id debetable goal
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ServerIdGoal);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_SERVER_ID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_ServerIdGoal);

            // end tag debetable comment
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildConnectBookMessageXmlTagWithData (XmlSerializer xmlSerializer, Cursor messageData) {

        try {

            // open connect book message tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_Messages);

            // start tag connect book message order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_Order);
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_Order);

            // start tag message text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_Message);
            xmlSerializer.text(messageData.getString(messageData.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_MESSAGE)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_Message);

            // start tag author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_AuthorName);
            xmlSerializer.text(messageData.getString(messageData.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_AUTHOR_NAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_AuthorName);

            // start tag message locale time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_MessageLocaleTime);
            xmlSerializer.text(String.valueOf(messageData.getLong(messageData.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_LOCAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_MessageLocaleTime);

            // end tag connect book message
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_Messages);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildMeetingDataXmlTagWithData(XmlSerializer xmlSerializer, Cursor meetingData) {

        try {
            // open meeting data tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_And_Suggestions);

            // start tag meeting/ suggestion order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_Order);
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_Update);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_Order);

            // start tag meeting/suggestion timestamp 1
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate1);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate1);

            // start tag meeting/suggestion timestamp 2
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate2);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE2))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate2);

            // start tag meeting/suggestion timestamp 3
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate3);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE3))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate3);

            // start tag meeting/suggestion timestamp 4
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate4);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE4))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate4);

            // start tag meeting/suggestion timestamp 5
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate5);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE5))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate5);

            // start tag meeting/suggestion timestamp 6
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate6);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE6))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingDate6);

            // start tag meeting/suggestion place 1
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace1);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace1);

            // start tag meeting/suggestion place 2
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace2);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE2)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace2);

            // start tag meeting/suggestion place 3
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace3);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE3)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace3);

            // start tag meeting/suggestion place 4
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace4);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE4)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace4);

            // start tag meeting/suggestion place 5
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace5);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE5)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace5);

            // start tag meeting/suggestion place 6
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace6);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE6)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingPlace6);

            // start tag meeting/suggestion vote 1
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote1);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE1)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote1);

            // start tag meeting/suggestion vote 2
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote2);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE2)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote2);

            // start tag meeting/suggestion vote 3
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote3);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE3)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote3);

            // start tag meeting/suggestion vote 4
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote4);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE4)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote4);

            // start tag meeting/suggestion vote 5
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote5);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE5)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote5);

            // start tag meeting/suggestion vote 6
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote6);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE6)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_MettingVote6);

            // start tag meeting/suggestion vote author
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_VoteAuthor);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTEAUTHOR)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_VoteAuthor);

            // start tag meeting/suggestio vote date
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_VoteLocaleDate);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTELOCALEDATE))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_VoteLocaleDate);

            // start tag meeting/suggestion creation time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_CreationTime);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_CreationTime);

            // start tag meeting/suggestion kategorie
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_Kategorie);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_KATEGORIE)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_Kategorie);

            // start tag meeting/suggestion client suggestion text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_Text);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_Text);

            // start tag meeting/suggestion client suggestion time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_LocaleTime);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_LocaleTime);

            // start tag meeting/suggestion client suggestion author
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_Author);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_AUTHOR)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_SuggestionFromClient_Author);

            // start tag meeting/suggestion client comment author
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCommentAuthorName);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCommentAuthorName);

            // start tag meeting/suggestion client comment time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCommentLocaleTime);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_LOCALE_DATE))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCommentLocaleTime);

            // start tag meeting/suggestion client comment text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCommentText);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_TEXT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCommentText);

            // start tag meeting/suggestion client canceled time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCanceledLocaleTime);
            xmlSerializer.text(String.valueOf(meetingData.getLong(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_LOCALE_TIME))/1000)); // convert millis to times
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCanceledLocaleTime);

            // start tag meeting/suggestion client canceled author name
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCanceledAuthorName);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_AUTHOR)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCanceledAuthorName);

            // start tag meeting/suggestion client canceled text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCanceledText);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TEXT)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_ClientCanceledText);

            // start tag meeting/suggestion server id
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_DataServerId);
            xmlSerializer.text(meetingData.getString(meetingData.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_SERVER_ID)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_Suggestion_DataServerId);

            // end tag meeting/suggestion
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMeeting_And_Suggestions);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buildMessageXmlTagWithData (XmlSerializer xmlSerializer, Cursor messageData, Boolean clientIdSet, Boolean contactIdSet) {

        try {

            // open message tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMessage_Messages);

            // start tag message order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMessage_Order);
            // check associate or not associated message

            if (clientIdSet) {
                xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_NewAssociatedMessage);
            }
            else {
                xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_NewNotAssociatedMessage);
            }
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMessage_Order);

            // start tag message text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMessage_MessageText);
            xmlSerializer.text(messageData.getString(messageData.getColumnIndex(DBAdapter.MESSAGE_KEY_MESSAGE)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMessage_MessageText);

            // start tag author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMessage_AuthorName);
            xmlSerializer.text(messageData.getString(messageData.getColumnIndex(DBAdapter.MESSAGE_KEY_AUTHOR_NAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMessage_AuthorName);

            // start tag message locale time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMessage_MessageLocaleTime);
            xmlSerializer.text(String.valueOf(messageData.getLong(messageData.getColumnIndex(DBAdapter.MESSAGE_KEY_LOCAL_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMessage_MessageLocaleTime);

            // end tag message
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMessage_Messages);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
