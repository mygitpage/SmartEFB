package de.smart_efb.efbapp.smartefb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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




        public class ExchangeTaskCheckNewContent implements Runnable {

            private int parameter;

            public ExchangeTaskCheckNewContent (int parameter) {

                this.parameter = parameter;

            }

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
                        URL scripturl = new URL(ConstansClassSettings.urlFirstConnectToServer);
                        HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
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






        public class ExchangeTaskSendContent implements Runnable {

            private int parameter;

            public ExchangeTaskSendContent (int parameter) {

                this.parameter = parameter;

            }

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
                        URL scripturl = new URL(ConstansClassSettings.urlFirstConnectToServer);
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







        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("Exchange Service", "In onDestroy");

            this.isRunning = false;

        }



        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d("Exchange Service", "In onStartCommand");

            this.backgroundThread = new Thread(new ExchangeTaskCheckNewContent(10));

            if (!this.isRunning) {
                this.isRunning = true;


                this.backgroundThread.start();


                Log.d("Exchange Service", "In IF STATTEMANT ENDE");
            }




            return START_NOT_STICKY;

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
                xmlSerializer.text("established");
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
