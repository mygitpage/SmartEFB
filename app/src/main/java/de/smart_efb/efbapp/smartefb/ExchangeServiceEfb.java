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
import android.util.Log;
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

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                    Log.d("Exchange Service", "Network on in aks new data");

                    // get comment from db
                    Cursor commentData = myDb.getOneRowOurArrangementComment(dbId);

                    // get client id from prefs
                    String tmpClientId = prefs.getString(ConstansClassSettings.namePrefsClientId, "");

                    // generate xml output text
                    XmlSerializer xmlSerializer = Xml.newSerializer();
                    StringWriter writer = new StringWriter();


                    // get all comments with status = 0 -> ready to send
                    Cursor allCommentsReadyToSend = myDb.getAllReadyToSendComments(prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0"));




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


                        Log.d("Exchange", "Anzahl Kommentare to send: "+allCommentsReadyToSend.getCount());

                        // build xml for all now comments
                        if (allCommentsReadyToSend != null) {

                            while (allCommentsReadyToSend.moveToNext()) {
                                buildCommentNowXmlTagWithData(xmlSerializer, allCommentsReadyToSend);
                                send_now_comment_info = true;
                            }


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

                        if (allCommentsReadyToSend != null) {

                            if (returnMap.get("SendSuccessfull").equals("1") && send_now_comment_info) {



                                while (allCommentsReadyToSend.moveToNext()) {



                                    myDb.updateStatusOurArrangementComment (allCommentsReadyToSend.getLong(allCommentsReadyToSend.getColumnIndex(DBAdapter.KEY_ROWID)), 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)

                                }




                            }
                        }

                        // close input stream and disconnect
                        answerInputStream.close();
                        connection.disconnect();

                        /*
                        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageCommentSendSuccessfull));
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

                        if (returnMap.get("SendSuccessfull").equals("1")) {
                            myDb.updateStatusOurArrangementComment (dbId, 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                        }

                        // close input stream and disconnect
                        answerInputStream.close();
                        connection.disconnect();

                        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
                        Intent tmpIntent = translateMapToIntent (returnMap);
                        tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageCommentSendSuccessfull));
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
                else { // no network enable -> try to send comment to server later

                    // send intent to receiver in OurArrangementFragmentNow to show toast to user
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

                    if (returnMap.get("SendSuccessfull").equals("1")) {
                        myDb.updateStatusOurArrangementSketchComment (dbId, 1); // set status of sketch comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                    }




                    // close input stream and disconnect
                    answerInputStream.close();
                    connection.disconnect();

                    // send intent to receiver in OurArrangementFragmentSketch to update listView OurArrangement (when active)
                    Intent tmpIntent = translateMapToIntent (returnMap);
                    tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageCommentSendSuccessfull));
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
            else { // no network enable -> try to send comment to server later

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


                    // open evaluation result arrangement tag
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate);

                    // start tag evaluate arrangement order
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Order);
                    xmlSerializer.text(ConstansClassXmlParser.xmlNameForOrder_New);
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Order);

                    // start tag evalute remarks
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Remarks);
                    xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_REMARKS)));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_Remarks);

                    // start tag evaluate author name text
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_AuthorName);
                    xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_USERNAME)));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_AuthorName);

                    // start tag evaluate result time
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultTime);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_TIME))/1000)); // convert millis to timestamp
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultTime);

                    // start tag evaluate arrangement time
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_DateOfArrangement);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_TIME))/1000)); // convert millis to timestamp
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_DateOfArrangement);

                    // start tag evaluate result question A
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionA);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION1))));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionA);

                    // start tag evaluate result question B
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionB);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION2))));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionB);

                    // start tag evaluate result question C
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionC);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION3))));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionC);

                    // start tag evaluate result question D
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionD);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_RESULT_QUESTION4))));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ResultQuestionD);

                    // start tag evaluate start time
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_StartTime);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_START_EVALUATIONBLOCK_TIME))/1000)); // convert millis to timestamp
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_StartTime);

                    // start tag evaluate end time
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_EndTime);
                    xmlSerializer.text(String.valueOf(evaluationResultData.getLong(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_END_EVALUATIONBLOCK_TIME))/1000)); // convert millis to timestamp
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_EndTime);

                    // start tag server id arrangement
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ServerIdArrangement);
                    xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_SERVER_ID_ARRANGEMENT)));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_ServerIdArrangement);

                    // start tag block id of arrangements
                    xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_BlockId);
                    xmlSerializer.text(evaluationResultData.getString(evaluationResultData.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_EVALUATE_KEY_ARRANGEMENT_BLOCKID)));
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate_BlockId);

                    // end tag evaluation result arrangement
                    xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForOurArrangement_Evaluate);

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

                    // send intent to receiver in OurArrangementNowFragment and Fragment Evaluation to inform user (when active)
                    Intent tmpIntent = translateMapToIntent (returnMap);
                    tmpIntent.putExtra("Message",context.getResources().getString(R.string.toastMessageEvaluationSendSuccessfull));
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








    }
