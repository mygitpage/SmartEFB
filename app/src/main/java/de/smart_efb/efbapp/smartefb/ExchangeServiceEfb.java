package de.smart_efb.efbapp.smartefb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;

import org.apache.http.conn.ConnectTimeoutException;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
     * Created by ich on 27.07.2017.
     */
    public class ExchangeServiceEfb extends Service {

        private boolean isRunning;
        private Context context;
        private Thread backgroundThread;

        @Override
        public IBinder onBind(Intent intent) {
            // Wont be called as service is not bound
            Log.d("Exchange Service", "In onBind");
            return null;
        }


        @Override
        public void onCreate() {
            super.onCreate();
            Log.d("Exchange Service", "In onCreate");

            this.isRunning = false;
            this.context = this;

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

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                    Log.d("Exchange Service", "Network on in aks new data");

                    // get client id from prefs
                    String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                    // generate xml output text
                    XmlSerializer xmlSerializer = Xml.newSerializer();
                    StringWriter writer = new StringWriter();


                    // get all comments with status = 0 -> ready to send
                    Cursor allCommentsReadyToSend = myDb.getAllReadyToSendComments(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0"));

                    // get all sketch comments with status = 0 -> ready to send
                    Cursor allSketchCommentsReadyToSend = myDb.getAllReadyToSendSketchComments(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0"));


                    // get all now arrangement evaluation results with status = 0 -> ready to send
                    Cursor allArrangementEvaluationResultsReadyToSend = myDb.getAllReadyToSendArrangementEvaluationResults();


                    // get all jointly goals comments with status = 0 -> ready to send
                    Cursor allJointlyGoalsCommentsReadyToSend = myDb.getAllReadyToSendJointlyGoalsComments(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0"));


                    // get all jointly goals evaluation results with status = 0 -> ready to send
                    Cursor allGoalsEvaluationResultsReadyToSend = myDb.getAllReadyToSendGoalsEvaluationResults();


                    // get all debetable comments with status = 0 -> ready to send
                    Cursor allDebetableCommentsReadyToSend = myDb.getAllReadyToSendDebetableComments(prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfDebetableGoals, "0"));




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

                        // start tag client id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        xmlSerializer.text(tmpClientId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);

                        // end tag main
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);


                        Log.d("Exchange Arr Comment", "Anzahl Kommentare to send: "+allCommentsReadyToSend.getCount());

                        // build xml for all now comments
                        if (allCommentsReadyToSend != null && allCommentsReadyToSend.getCount() > 0) {

                            do {
                                buildCommentNowXmlTagWithData(xmlSerializer, allCommentsReadyToSend);
                                send_now_comment_info = true;
                            } while (allCommentsReadyToSend.moveToNext());
                        }

                        Log.d("Exchange", "Anzahl Entwürfe Komm to send: "+allSketchCommentsReadyToSend.getCount());

                        // build xml for all sketch comments
                        if (allSketchCommentsReadyToSend != null && allSketchCommentsReadyToSend.getCount() > 0) {

                            do {
                                buildCommentSketchXmlTagWithData(xmlSerializer, allSketchCommentsReadyToSend);
                                send_sketch_comment_info = true;
                            } while (allSketchCommentsReadyToSend.moveToNext());
                        }


                        Log.d ("EXCHANGE --->", "Anzahl Arrangement Evaluation: "+allArrangementEvaluationResultsReadyToSend.getCount());

                        // build xml for all arrangement evaluation result
                        if (allArrangementEvaluationResultsReadyToSend != null && allArrangementEvaluationResultsReadyToSend.getCount() > 0) {

                            do {
                                buildArrangementEvaluationResultXmlTagWithData(xmlSerializer, allArrangementEvaluationResultsReadyToSend);
                                send_arrangement_evaluation_result_info = true;
                            } while (allArrangementEvaluationResultsReadyToSend.moveToNext());
                        }

                        // build xml for all jointly goals comments
                        if (allJointlyGoalsCommentsReadyToSend != null && allJointlyGoalsCommentsReadyToSend.getCount() > 0) {

                            do {
                                buildJointlyCommentXmlTagWithData(xmlSerializer, allJointlyGoalsCommentsReadyToSend);
                                send_jointly_goals_comment_info = true;
                            } while (allJointlyGoalsCommentsReadyToSend.moveToNext());
                        }



                        Log.d ("EXCHANGE --->", "Anzahl Goals Evaluation: "+allGoalsEvaluationResultsReadyToSend.getCount());

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

                        Log.d("Send","XMLCODE="+textparam);

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

                        Log.d("New Data Send", "Antwort:"+stringBuilder.toString().trim());



                        // call xml parser with input
                        EfbXmlParser xmlparser = new EfbXmlParser(context);
                        returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                        //++++++++++++++++ db status update section +++++++++++++++++++++++++++++++++

                        // set status of now comment to 1 -> send successfull
                        if (allCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_now_comment_info) {
                                allCommentsReadyToSend.moveToFirst();
                                do {
                                    myDb.updateStatusOurArrangementComment (allCommentsReadyToSend.getLong(allCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                } while (allCommentsReadyToSend.moveToNext());
                            }
                        }

                        // set status of sketch comment to 1 -> send successfull
                        if (allSketchCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_sketch_comment_info) {
                                allSketchCommentsReadyToSend.moveToFirst();
                                do {
                                    myDb.updateStatusOurArrangementSketchComment (allSketchCommentsReadyToSend.getLong(allSketchCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of sketch comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                } while (allSketchCommentsReadyToSend.moveToNext());
                            }
                        }


                        // set status of evaluation result for arrangement to 1 -> send successfull
                        if (allArrangementEvaluationResultsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_arrangement_evaluation_result_info) {
                                allArrangementEvaluationResultsReadyToSend.moveToFirst();
                                do {
                                    myDb.updateStatusOurArrangementEvaluation (allArrangementEvaluationResultsReadyToSend.getLong(allArrangementEvaluationResultsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of sketch comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                } while (allArrangementEvaluationResultsReadyToSend.moveToNext());
                            }
                        }




                        // set status of evaluation jointly goal to 1 -> send successfull
                        if (allGoalsEvaluationResultsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_goals_evaluation_result_info) {
                                allGoalsEvaluationResultsReadyToSend.moveToFirst();
                                do {
                                    myDb.updateStatusOurGoalsEvaluation (allGoalsEvaluationResultsReadyToSend.getLong(allGoalsEvaluationResultsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of evaluation result to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                } while (allGoalsEvaluationResultsReadyToSend.moveToNext());
                            }
                        }




                        // set status of jointly goal comment to 1 -> send successfull
                        if (allJointlyGoalsCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_jointly_goals_comment_info) {
                                allJointlyGoalsCommentsReadyToSend.moveToFirst();
                                do {
                                    myDb.updateStatusOurGoalsJointlyGoalsComment (allJointlyGoalsCommentsReadyToSend.getLong(allJointlyGoalsCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                } while (allJointlyGoalsCommentsReadyToSend.moveToNext());
                            }
                        }


                        // set status of debetable goal comment to 1 -> send successfull
                        if (allDebetableCommentsReadyToSend != null) {
                            if (returnMap.get("SendSuccessfull").equals("1") && send_debetable_goals_comment_info) {
                                allDebetableCommentsReadyToSend.moveToFirst();
                                do {
                                    myDb.updateStatusOurGoalsDebetableComment (allDebetableCommentsReadyToSend.getLong(allDebetableCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of debetable comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                                } while (allDebetableCommentsReadyToSend.moveToNext());
                            }
                        }





                        //++++++++++++++++ end db status update section +++++++++++++++++++++++++++++++++

                        // close input stream and disconnect
                        answerInputStream.close();
                        connection.disconnect();


                        // send intent to broadcast receiver -> the receiver looks for relevant data in intent
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }


                }


                // stop the task with service
                stopSelf();

            }

        }








        // send now comment arrangement to server and get answer from server
        public class ExchangeTaskSendNowCommentArrangement implements Runnable {

            // id of the data row in db
            private Long dbId;

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
            public ExchangeTaskSendNowCommentArrangement (Context context, Long dbid) {

                // id of the data row in db
                this.dbId = dbid;

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

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                    Log.d("Exchange Service", "Network on in send now");

                    // get comment from db
                    Cursor commentData = myDb.getOneRowOurArrangementComment(dbId);

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
                        xmlSerializer.text(ConstansClassXmlParser.xmlNameForSendToServer_CommentArrangement);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                        // start tag client id
                        xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                        xmlSerializer.text(tmpClientId);
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);

                        // end tag main
                        xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                        // build xml tag for comment with data
                        buildCommentNowXmlTagWithData (xmlSerializer, commentData);

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

                        Log.d("Send","XMLCODE="+textparam);

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

                        Log.d("Comment Send", "Antwort:"+stringBuilder.toString().trim());

                        // call xml parser with input
                        EfbXmlParser xmlparser = new EfbXmlParser(context);
                        returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                        // close input stream and disconnect
                        answerInputStream.close();
                        connection.disconnect();


                        if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                            myDb.updateStatusOurArrangementComment (dbId, 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                            // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
                            Intent tmpIntent = translateMapToIntent (returnMap);
                            tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageCommentSendSuccessfull));
                            tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                            context.sendBroadcast(tmpIntent);
                        }
                        else { // send not successfull
                            // send information broadcast to receiver that sending was not successefull
                            String message = context.getResources().getString(R.string.toastMessageCommentSendNotSuccessfull);
                            sendIntentBroadcastSendingNotSuccessefull (message);
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        // send information broadcast to receiver that sending not successfull
                        String message = context.getResources().getString(R.string.toastMessageCommentSendNotSuccessfull);
                        sendIntentBroadcastSendingNotSuccessefull (message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // send information broadcast to receiver that sending not successfull
                        String message = context.getResources().getString(R.string.toastMessageCommentSendNotSuccessfull);
                        sendIntentBroadcastSendingNotSuccessefull (message);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }
                else { // no network enable -> try to send comment to server later
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageCommentNotSendSuccessfullNoNetwork);
                    sendIntentBroadcastSendingNotSuccessefull (message);
                }

                // stop the task with service
                stopSelf();

            }

        }



    // send sketch comment arrangement to server and get answer from server
    public class ExchangeTaskSendSketchCommentArrangement implements Runnable {

        // id of the data row in db
        private Long dbId;

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
        public ExchangeTaskSendSketchCommentArrangement (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

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

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                Log.d("Exchange Service Sketch", "Network on in send sketch");

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

                    Log.d("Sketch Comment XML", "Content:"+stringBuilder.toString().trim());


                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusOurArrangementSketchComment (dbId, 1); // set status of sketch comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageSketchCommentSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageSketchCommentSendNotSuccessfull);
                        sendIntentBroadcastSendingNotSuccessefull (message);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageSketchCommentSendNotSuccessfull);
                    sendIntentBroadcastSendingNotSuccessefull (message);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageSketchCommentSendNotSuccessfull);
                    sendIntentBroadcastSendingNotSuccessefull (message);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageCommentNotSendSuccessfullNoNetwork);
                sendIntentBroadcastSendingNotSuccessefull (message);
            }

            // stop the task with service
            stopSelf();

        }

    }






    //
    // send evaluation result arrangement to server and get answer from server
    public class ExchangeTaskSendEvaluationResultArrangement implements Runnable {

        // id of the data row in db
        private Long dbId;

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
        public ExchangeTaskSendEvaluationResultArrangement (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

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

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                Log.d("Exch Service Evaluation", "Network on in send evaluation");

                // get evaluation result from db
                Cursor evaluationResultData = myDb.getOneRowEvaluationResultArrangement (dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    Log.d("Exch Service Evaluation", "Try to send");

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

                    Log.d("Evaluation REc. XML", "Content:"+stringBuilder.toString().trim());


                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    if (returnMap.get("SendSuccessfull").equals("1")) {
                        myDb.updateStatusOurArrangementEvaluation (dbId, 1); // set status of evaluation to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    // send intent to receiver in OurArrangementNowFragment to inform user (when active)
                    /* Bei erfolgreichem Senden der Evaluationsergebnisse wird keine Nachricht ausgegeben!!!!!!
                    Intent tmpIntent = translateMapToIntent (returnMap);
                    tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageEvaluationSendSuccessfull));
                    tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                    context.sendBroadcast(tmpIntent);
                    */

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

            // stop the task with service
            stopSelf();

        }

    }



    // send message to activity that sending was not successfull
    public void sendIntentBroadcastSendingNotSuccessefull (String message) {

        Log.d("Exchange Service", "SENDE FEHLER BRAODCAST TO all Listener!!!");

        SystemClock.sleep(2000); // wait two second because fragment change and with in the broadcast receiver

        Log.d("Exchange Service", "SENDE FEHLER BRAODCAST TO all Listener 1s later!!!");

        // send intent to receiver that sending not successfull
        Intent tmpIntent = new Intent();
        tmpIntent.putExtra("Message", message);
        tmpIntent.putExtra("SendNotSuccessfull","1");
        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
        context.sendBroadcast(tmpIntent);
    }





    // +++++++++++++++++++++++++ task exchange goals +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



    // send jointly comment goals to server and get answer from server
    public class ExchangeTaskSendJointlyCommentGoals implements Runnable {

        // id of the data row in db
        private Long dbId;

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
        public ExchangeTaskSendJointlyCommentGoals (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

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

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                Log.d("Exchange Service", "Network on in send jointly com");

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

                    Log.d("Send","XMLCODE="+textparam);

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

                    Log.d("Comment Send", "Antwort:"+stringBuilder.toString().trim());

                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();


                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusOurGoalsJointlyGoalsComment (dbId, 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // send intent to receiver in OurGoalsFragmentJointlyGoals to update listView OurGoals (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendNotSuccessfull);
                        sendIntentBroadcastSendingNotSuccessefull (message);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendNotSuccessfull);
                    sendIntentBroadcastSendingNotSuccessefull (message);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentSendNotSuccessfull);
                    sendIntentBroadcastSendingNotSuccessefull (message);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageJointlyGoalsCommentNotSendSuccessfullNoNetwork);
                sendIntentBroadcastSendingNotSuccessefull (message);
            }

            // stop the task with service
            stopSelf();

        }

    }





    //
    // send evaluation result jointly goal to server and get answer from server
    public class ExchangeTaskSendJointlyGoalsEvaluationResult implements Runnable {

        // id of the data row in db
        private Long dbId;

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
        public ExchangeTaskSendJointlyGoalsEvaluationResult (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

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

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                Log.d("Exch Jointly Evaluation", "Network on in send evaluation");

                // get evaluation result from db
                Cursor evaluationResultData = myDb.getOneRowEvaluationResultGoals (dbId);

                // get client id from prefs
                String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                // generate xml output text
                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                try {

                    Log.d("Exch Jointly Evaluation", "Try to send");

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

                    Log.d("Evaluation Jointly XML", "Content:"+stringBuilder.toString().trim());


                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    if (returnMap.get("SendSuccessfull").equals("1")) {
                        myDb.updateStatusOurGoalsEvaluation (dbId, 1); // set status of evaluation to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                    }

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    // send intent to receiver in OurGoalsFragmentJointlyGoalsNow and Fragment Evaluation to inform user (when active)
                    /* Keine Nachricht bei erfolgrechem Senden der Evaluationsergebnisse
                    Intent tmpIntent = translateMapToIntent (returnMap);
                    tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageJointlyGoalsEvaluationSendSuccessfull));
                    tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                    context.sendBroadcast(tmpIntent);
                    */

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

            // stop the task with service
            stopSelf();
        }
    }








    // send debetable comment goal to server and get answer from server
    public class ExchangeTaskSendDebetableGoalCommentResult implements Runnable {

        // id of the data row in db
        private Long dbId;

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
        public ExchangeTaskSendDebetableGoalCommentResult (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

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

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                Log.d("Ex Service Debetable", "Network on in send sketch");

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

                    Log.d("Debetable Comment XML", "Content:"+stringBuilder.toString().trim());


                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull
                        myDb.updateStatusOurGoalsDebetableComment (dbId, 1); // set status of debetable comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                        // send intent to receiver in OurGoalsFragmentDebetableComment to update listView OurGoals (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageDebetableCommentSendSuccessfull));
                        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);
                    }
                    else { // send not successfull
                        // send information broadcast to receiver that sending was not successefull
                        String message = context.getResources().getString(R.string.toastMessageeDebetableCommentSendNotSuccessfull);
                        sendIntentBroadcastSendingNotSuccessefull (message);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageeDebetableCommentSendNotSuccessfull);
                    sendIntentBroadcastSendingNotSuccessefull (message);
                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull
                    String message = context.getResources().getString(R.string.toastMessageeDebetableCommentSendNotSuccessfull);
                    sendIntentBroadcastSendingNotSuccessefull (message);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            else { // no network enable -> try to send comment to server later
                // send information broadcast to receiver that sending not successfull
                String message = context.getResources().getString(R.string.toastMessageeDebetableCommentNotSendSuccessfullNoNetwork);
                sendIntentBroadcastSendingNotSuccessefull (message);
            }

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
        public ExchangeTaskSendConnectBookMessage (Context context, Long dbid) {

            // id of the data row in db
            this.dbId = dbid;

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

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                Log.d("Ex Service Connect Book", "Network on in send message!");

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

                    Log.d("Connect Book XML", "Content:"+stringBuilder.toString().trim());


                    // call xml parser with input
                    EfbXmlParser xmlparser = new EfbXmlParser(context);
                    returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());

                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    if (returnMap.get("SendSuccessfull").equals("1")) { // send successfull

                        myDb.updateStatusConnectBookMessage (dbId, 1); // set status of message to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                    }




                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                    // send information broadcast to receiver that sending not successfull

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }


            // stop the task with service
            stopSelf();
        }
    }
    
    


    







    // +++++++++++++++++++++++++ end task exchange connect book +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    
    
    
    



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

            return intent;
        }



        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("Exchange Service", "In onDestroy");

            this.isRunning = false;

        }



        @Override
        // check the commands for service and start the task to handle the command
        public int onStartCommand(Intent intent, int flags, int startId) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // get data from intent
            intentExtras = intent.getExtras();

            if (intentExtras != null && !this.isRunning) {

                // get command from intent extras
                String command = intentExtras.getString("com");

                // get db id from intent extras
                Long dbId = intentExtras.getLong("dbid", 0);

                if (command.equals("ask_new_data")) { // Ask server for new data

                    // generate new background task
                    this.backgroundThread = new Thread(new ExchangeTaskCheckNewContent(context));
                    // set task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();

                } else if (command.equals("send_now_comment_arrangement") && dbId > 0) { // send new arrangement comment to server

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendNowCommentArrangement (context, dbId));
                    // task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();

                } else if (command.equals("send_sketch_comment_arrangement") && dbId > 0) { // send new sketch arrangement comment to server

                    Log.d("Excange Service", "Command erhalten !!!!!!!!");

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendSketchCommentArrangement (context, dbId));
                    // task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();

                } else if (command.equals("send_evaluation_result_arrangement") && dbId > 0) { // send new evaluation result to server

                    Log.d("Excange Service", "Evaluation REsult erhalten !!!!!!!!");

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendEvaluationResultArrangement(context, dbId));
                    // task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();

                } else if (command.equals("send_jointly_comment_goal") && dbId > 0) { // send jointly goal comment to server

                    Log.d("Excange Service", "Jointly comment !!!!!!!!");

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendJointlyCommentGoals (context, dbId));
                    // task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();

                } else if (command.equals("send_evaluation_result_goal") && dbId > 0) { // send jointly goal evaluation result to server

                    Log.d("Excange Service", "Jointly Goal Evaluation REsult !!!!!!!!");

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendJointlyGoalsEvaluationResult (context, dbId));
                    // task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();

                }  else if (command.equals("send_debetable_comment_goal") && dbId > 0) { // send debetable comment result to server

                    Log.d("Excange Service", "Debetable Comment REsult !!!!!!!!");

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendDebetableGoalCommentResult (context, dbId));
                    // task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();
                
                } else if (command.equals("send_connectbook_message") && dbId > 0) { // send connect book message to server

                    Log.d("Excange Service", "Connect Book message !!!!!!!!");

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendConnectBookMessage (context, dbId));
                    // task is running
                    this.isRunning = true;
                    // start task
                    this.backgroundThread.start();
                } 




                










            }

            return START_STICKY;

        }







        public void buildCommentNowXmlTagWithData(XmlSerializer xmlSerializer, Cursor commentData) {


            Log.d("Exchange Build", "Kommentartext: " + commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));

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
                xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_CommentTime);
                xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME))/1000)); // convert millis to timestamp
                xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_NowComment_CommentTime);

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


        Log.d("Exchange Build Sketch", "Kommentartext: " + commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));

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
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_CommentTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_SketchComment_CommentTime);

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

            // end tag comment now arrangement
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
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultTime);
            xmlSerializer.text(String.valueOf(evaluationData.getLong(evaluationData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultTime);
    
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


        //Log.d("Ex Build EVAL GOALS", "Kommentartext: " + commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT));

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
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultTime);
            xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_EVALUATE_KEY_RESULT_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyEvaluate_ResultTime);

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


        Log.d("Exchange Build", "Kommentartext: " + commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT));

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
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_CommentTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_JointlyComment_CommentTime);

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


        Log.d("Ex Build Debetable", "Kommentartext: " + commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT));

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
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_CommentTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment_CommentTime);

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

            // end tag comment now arrangement
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurGoals_DebetableComment);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }







    public void buildConnectBookMessageXmlTagWithData (XmlSerializer xmlSerializer, Cursor commentData) {


        Log.d("Ex Build Connect Book", "Message: " + commentData.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_MESSAGE));

        try {

            // open connect book message tag
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_Messages);

            // start tag connect book message order
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_Order);
            xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_Order);

            // start tag message text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_Message);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_MESSAGE)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_Message);

            // start tag author name text
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_AuthorName);
            xmlSerializer.text(commentData.getString(commentData.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_AUTHOR_NAME)));
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_AuthorName);

            // start tag comment time
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForConnectBook_MessageTime);
            xmlSerializer.text(String.valueOf(commentData.getLong(commentData.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME))/1000)); // convert millis to timestamp
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_MessageTime);

            // end tag connect book message
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForConnectBook_Messages);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }




}
