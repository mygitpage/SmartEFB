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

        // Look at:
        // http://www.truiton.com/2014/09/android-service-broadcastreceiver-example/


        // Look at:
        // https://stackoverflow.com/questions/3385106/android-sending-data-from-an-activity-to-a-service


        // Look at
        // https://stackoverflow.com/questions/3293243/pass-data-from-activity-to-service-using-an-intent

        // Look at:
        // https://xjaphx.wordpress.com/2012/07/07/create-a-service-that-does-a-schedule-task/

        // Look at:
        // https://www.thepolyglotdeveloper.com/2014/10/use-broadcast-receiver-background-services-android/




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



        // Ask server for new data and get answer from server
        public class ExchangeTaskCheckNewContent implements Runnable {


            public void run() {


                Log.d("Exchange Service", "Exchange Task");

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                    Log.d("Exchange Service", "Network on");


                    try {

                        // prepair data to send
                        String textparam = "xmlcode=" + URLEncoder.encode(makeXMLRequestForConnectionAskNew("a123456789bcdefghi"), "UTF-8");
                        // set url and parameters
                        URL scripturl = new URL(ConstansClassSettings.urlConnectionAskForNewDataToServer);
                        HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setRequestMethod("POST");
                        connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                        // set timeout for connection
                        connection.setConnectTimeout(ConstansClassSettings.connectionEstablishedTimeOut);
                        connection.setReadTimeout(ConstansClassSettings.connectionReadTimeOut);

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
                        //EfbXmlParser xmlparser = new EfbXmlParser(fragmentConnectToServerContext);
                        //returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());


                        // close input stream and disconnect
                        answerInputStream.close();
                        connection.disconnect();
                    }
                    catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } /*catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }*/










                }



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


                Log.d ("Exchange Service", "in SendNowComment dbid="+dbId);

            }

            // the task
            public void run() {


                Log.d("Exchange Service", "Exchange Task Send Now Comment RUN!");

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

                        // call xml parser with input
                        EfbXmlParser xmlparser = new EfbXmlParser(context);
                        returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());


                        if (returnMap.get("SendSucsessfull").equals("1")) {
                            myDb.updateStatusOurArrangementComment (dbId, 1); // set status of comment to 1 -> sucsessfull send! (=0-> ready to send, =4->comes from external)
                            Log.d("SEND RESULT","UPDATE IN DB OK!");
                        }

                        Log.d("SEND RESULT","DATA SEND: "+returnMap.get("SendSucsessfull"));

                        // close input stream and disconnect
                        answerInputStream.close();
                        connection.disconnect();


                        // send intent to receiver in OurArrangementFragmentNow to update listView OurArrangement (when active)
                        Intent tmpIntent = new Intent();
                        tmpIntent.putExtra("Success","yes");
                        tmpIntent.putExtra("Message","Der Kommentar wurde gesendet!");
                        tmpIntent.setAction("ARRANGEMENT_FRAGMENT_NOW_STATUS_UPDATE");
                        context.sendBroadcast(tmpIntent);


                    }


                    catch (MalformedURLException e) {
                     //   e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }










                }
                else { // no network enable -> try to send comment to server later

                    // send intent to receiver in OurArrangementFragmentNow to show toast to user
                    Intent tmpIntent = new Intent();
                    tmpIntent.setAction("ARRANGEMENT_FRAGMENT_NOW_STATUS_UPDATE");
                    tmpIntent.putExtra("Success","no");
                    tmpIntent.putExtra("Message","Senden niicht möglich. Kein Netzwerk!");
                    context.sendBroadcast(tmpIntent);
                }


                stopSelf();




            }

        }







        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("Exchange Service", "In onDestroy");

            this.isRunning = false;

        }



        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            Log.d("Exchange Service", "In onStartCommand");


            // Extras from intent that holds data
            Bundle intentExtras = null;

            // get data from intent
            intentExtras = intent.getExtras();


            if (intentExtras != null && !this.isRunning) {
                // get data that comes with extras
                //tmpServerDbId = intentExtras.getInt("db_id",0);
                //tmpNumberinListView = intentExtras.getInt("arr_num",0);
                //tmpEvalNext = intentExtras.getBoolean("eval_next");
                // get command and execute it
                //executeIntentCommand (intentExtras.getString("com"), tmpServerDbId, tmpNumberinListView, tmpEvalNext);

                Log.d("Exchange Service", "In IntentExtras");

                // get command from intent extras
                String command = intentExtras.getString("com");

                // get db id from intent extras
                Long dbId = intentExtras.getLong("dbid", 0);


                Log.d ("Exchange Service", "in OnStartCommand dbid="+dbId);




                if (command.equals("ask_new_data")) { // Ask server for new data


                    this.backgroundThread = new Thread(new ExchangeTaskCheckNewContent());

                    // task is running
                    this.isRunning = true;

                    // start task
                    this.backgroundThread.start();


                    Log.d("Exchange Service", "In ask new data");

                } else if (command.equals("send_now_comment_arrangement") && dbId > 0) { // send new arrangement comment to server

                    // generate new send task
                    this.backgroundThread = new Thread(new ExchangeTaskSendNowCommentArrangement (context, dbId));

                    // task is running
                    this.isRunning = true;

                    // start task
                    this.backgroundThread.start();


                    Log.d("Exchange Service", "send_now_comment_arrangement");

                }





            }




            /*


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

        if (intentExtras != null) {
           // get data that comes with extras
            tmpServerDbId = intentExtras.getInt("db_id",0);
            tmpNumberinListView = intentExtras.getInt("arr_num",0);
            tmpEvalNext = intentExtras.getBoolean("eval_next");
            // get command and execute it
            executeIntentCommand (intentExtras.getString("com"), tmpServerDbId, tmpNumberinListView, tmpEvalNext);
        }

    }


    // execute the commands that comes from link or intend
    public void executeIntentCommand (String command, int tmpServerDbId, int tmpNumberinListView, Boolean tmpEvalNext) {

        String tmpTabTitle = "";

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



        } else if (command.equals("comment_an_arrangement")) { // Show fragment comment arrangement

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

        }



             */










            return START_STICKY;

        }












        private String makeXMLRequestForConnectionAskNew (String clientId) {

            XmlSerializer xmlSerializer = Xml.newSerializer();

            StringWriter writer = new StringWriter();

            try {

                xmlSerializer.setOutput(writer);

                //Start Document
                xmlSerializer.startDocument("UTF-8", true);
                xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                // Open Tag
                xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMasterElement);
                xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain);

                // start tag main order -> connection established, send client ID
                xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
                xmlSerializer.text("sendnowcomment");
                xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

                // start tag client id
                xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);
                xmlSerializer.text(clientId);
                xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ClientID);

                // end tag main
                xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain);

                // end tag smartEfb
                xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMasterElement);

                xmlSerializer.endDocument();

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return writer.toString();

        }










    }
