package de.smart_efb.efbapp.smartefb;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentA extends Fragment {

    // fragment view
    View viewFragmentConnectToServer;

     // fragment context
    Context fragmentConnectToServerContext = null;

    // minimum and maximum for random number to connect to server
    static int randomNumberForConnectionMin = 10000;
    static int randomNumberForConnectionMax = 99999;

    // the connecting status (0=not connected, 1=no network, try again, 2= connection error, 3=connected)
    int connectingStatus = 0;

    // actual random number for connetion to server
    int randomNumberForConnection = 0;

    // Connection helper object
    EfbHelperConnectionClass efbHelperConnectionClass;

    // Progress Dialog
    private ProgressDialog pDialog;

    // return information for change
    Map<String, String> returnMap;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentConnectToServer = layoutInflater.inflate(R.layout.fragment_settings_efb_a, null);

        return viewFragmentConnectToServer;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentConnectToServerContext = getActivity().getApplicationContext();

        efbHelperConnectionClass = new EfbHelperConnectionClass(fragmentConnectToServerContext);

        // init the fragment connect to server
        initFragmentConnectToServer();

        // show actual connecting informations
        displayActualConnectingInformation();


    }


    private void initFragmentConnectToServer () {

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get meeting status
        connectingStatus = ((ActivitySettingsEfb)getActivity()).getConnectingStatus();

    }


    // show actual process data of connecting to server
    private void displayActualConnectingInformation () {

        // connecting status 0 -> not connected to server
        if (connectingStatus == 0) {

            Log.d("Settings A","ConnectinStatus == 0");

            // replace headline connect to server
            TextView textViewConnectedWithServerHeadlineText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // replace text and show connecting intro text
            TextView textViewConnectToServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerIntro);
            String tmpTextIntroText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroText);
            textViewConnectToServerIntroText.setText(tmpTextIntroText);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);

            // generate and show random number for connecting
            randomNumberForConnection = EfbHelperClass.randomNumber(randomNumberForConnectionMin, randomNumberForConnectionMax);

            TextView textViewRandomNumberText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingConnectToServerKeyNumber);
            textViewRandomNumberText.setVisibility(View.VISIBLE);
            textViewRandomNumberText.setText(""+randomNumberForConnection);

            // show remark for clicking button
            TextView textViewConnectToServerRemarkClickButtonText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerRemarkClickButton);
            String tmpTextRemarkClickButton = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerRemarkClickButtonText);
            textViewConnectToServerRemarkClickButtonText.setText(tmpTextRemarkClickButton);
            textViewConnectToServerRemarkClickButtonText.setVisibility(View.VISIBLE);


            // send button
            Button tmpButton = (Button) viewFragmentConnectToServer.findViewById(R.id.buttonSendConnectToServerKeyNumber);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener send pin number
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // call setter-methode setRandomNumberForConnection in ActivitySettingsEfb to set random number
                    ((ActivitySettingsEfb)getActivity()).setRandomNumberForConnection(randomNumberForConnection);

                    if (efbHelperConnectionClass.internetAvailable()) {

                        // new thread -> send pin to server and get answer (xml data with arrangement, goals, meeting, settings, etc.)
                        new sendPinToServer().execute(Integer.toString(randomNumberForConnection));



                    } else { // no network connection!


                        // call setter-methode setConnectionStatus in ActivitySettingsEfb
                        ((ActivitySettingsEfb)getActivity()).setConnectionStatus(1); // 1 -> no internet

                        connectingStatus = 1;

                        dialogNoInternetAvailable ();

                        Intent intent = new Intent(fragmentConnectToServerContext, ActivitySettingsEfb.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com","show_no_network_try_again");
                        fragmentConnectToServerContext.startActivity(intent);

                    }

                }
            });

        }


        // connecting status 1 -> no connection, no network, try again
        if (connectingStatus == 1) {

            // replace headline no network available
            TextView textViewConnectedWithServerHeadlineText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerHeadingNoInternetOrError);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // replace text and show info text try again
            TextView textViewConnectToServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerIntro);
            String tmpTextIntroText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroTextNoInternetOrError);
            textViewConnectToServerIntroText.setText(tmpTextIntroText);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);

            // get random number from prefs and show
            randomNumberForConnection = ((ActivitySettingsEfb)getActivity()).getRandomNumberForConnection();

            TextView textViewRandomNumberText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingConnectToServerKeyNumber);
            textViewRandomNumberText.setVisibility(View.VISIBLE);
            textViewRandomNumberText.setText(""+randomNumberForConnection);

            // show remark for clicking button
            TextView textViewConnectToServerRemarkClickButtonText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerRemarkClickButton);
            String tmpTextRemarkClickButton = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerRemarkNoInternetClickButtonText);
            textViewConnectToServerRemarkClickButtonText.setText(tmpTextRemarkClickButton);
            textViewConnectToServerRemarkClickButtonText.setVisibility(View.VISIBLE);

            Button tmpButton = (Button) viewFragmentConnectToServer.findViewById(R.id.buttonSendConnectToServerKeyNumber);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener send pin number
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // check internet available
                    if (efbHelperConnectionClass.internetAvailable()) {

                        // new thread -> send pin to server and get answer (xml data with arrangement, goals, meeting, settings, etc.)
                        new sendPinToServer().execute(Integer.toString(randomNumberForConnection));




                    } else { // no network connection!
                        
                        // show dialog "no imternet"
                        dialogNoInternetAvailable ();
                    
                    }

                }
            });

        }

        // connecting status 2 -> connection error
        if (connectingStatus == 2) {

            // replace headline connection error
            TextView textViewConnectedWithServerHeadlineText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectedWithServerErrorIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // show error pre intro text
            TextView textViewConnectedWithServerErrorPreIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerConnectionErrorPreIntro);
            textViewConnectedWithServerErrorPreIntroText.setVisibility(View.VISIBLE);

            // show error text
            TextView textViewConnectedWithServerErrorTextError = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerConnectionErrorTextError);
            // get last error text from prefs (ActivitySettingsEfb) and show
            String lastErrorText = ((ActivitySettingsEfb)getActivity()).getLastErrorText();
            textViewConnectedWithServerErrorTextError.setText(lastErrorText);
            textViewConnectedWithServerErrorTextError.setVisibility(View.VISIBLE);

            // show error post intro text
            TextView textViewConnectedWithServerErrorPostIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerConnectionErrorPostIntro);
            textViewConnectedWithServerErrorPostIntroText.setVisibility(View.VISIBLE);

            // set connection status to not connected so far
            ((ActivitySettingsEfb)getActivity()).setConnectionStatus(1); // 0 -> not connected so far

            connectingStatus = 1;

        }

        // connecting status 3 -> sucsessfull connected with server
        if (connectingStatus == 3) {

            // replace headline connected with server
            TextView textViewConnectedWithServerHeadlineText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectedWithServerIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // show connected to server intro text
            TextView textViewConnectedWithServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerSucsessfullIntro);
            textViewConnectedWithServerIntroText.setVisibility(View.VISIBLE);
            textViewConnectedWithServerIntroText.setMovementMethod(LinkMovementMethod.getInstance());

        }

    }


    // dialog no internet available
    public void dialogNoInternetAvailable () {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerDialogNoInternetHeadline));
        alertDialog.setMessage(fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerDialogNoInternetInfoText));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerDialogOkButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


    // dialog waiting for response
    private void dialogWaitingForResponse () {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(fragmentConnectToServerContext.getResources().getString(R.string.settingsSendingToServerDialogInfoText));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

    }




    // Background Async Task
    class sendPinToServer extends AsyncTask<String, String, String> {

        // Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // call dialog waiting for connection
            dialogWaitingForResponse();
        }

        // do in background -> Asyn task
        protected String doInBackground(String... args) {

            try {

                // prepair data to send
                String textparam = "xmlcode=" + URLEncoder.encode(makeXMLRequestForFirstConnection (args[0]), "UTF-8");
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
                    while ((currentRow = reader.readLine()) != null){
                        stringBuilder.append(currentRow);
                        stringBuilder.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // call xml parser with input
                EfbXmlParser xmlparser = new EfbXmlParser(fragmentConnectToServerContext);
                returnMap = xmlparser.parseXmlInput(stringBuilder.toString().trim());


                // close input stream and disconnect
                answerInputStream.close();
                connection.disconnect();


                // connection status correct and no error occured?
                if (returnMap.get("ConnectionStatus") == "3" && returnMap.get("Error") == "0" ) { // connection status 3 -> connection sucsessfull; 2 -> connection error; 1 -> no internet; 0 -> no try to connect so far

                    // set connection status to connect
                    ((ActivitySettingsEfb)getActivity()).setConnectionStatus(3); // 3 -> Connect with server

                    connectingStatus = 3;


                    // prepair data to send
                    String xmlCodeEstablished = "xmlcode=" + URLEncoder.encode(makeXMLRequestForConnectionEstablished (returnMap.get("ClientId")), "UTF-8");
                    // set url and parameters
                    URL scriptEstablishedUrl = new URL(ConstansClassSettings.urlConnectionEstablishedToServer);
                    HttpURLConnection connectionEstablished = (HttpURLConnection) scriptEstablishedUrl.openConnection();
                    connectionEstablished.setDoOutput(true);
                    connectionEstablished.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connectionEstablished.setRequestMethod("POST");
                    connectionEstablished.setFixedLengthStreamingMode(xmlCodeEstablished.getBytes().length);

                    // generate output stream and send
                    OutputStreamWriter contentWriterEstablished = new OutputStreamWriter(connectionEstablished.getOutputStream());
                    contentWriterEstablished.write(xmlCodeEstablished);
                    contentWriterEstablished.flush();

                    contentWriterEstablished.close();



                    //tmp

                    // get answer from input
                    InputStream answerInputStream1 = connectionEstablished.getInputStream();
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(answerInputStream1));
                    StringBuilder stringBuilder1 = new StringBuilder();

                    // convert input stream to string
                    String currentRow1;
                    try {
                        while ((currentRow1 = reader1.readLine()) != null){
                            stringBuilder1.append(currentRow1);
                            stringBuilder1.append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("AsynTask","Fehler:"+stringBuilder1.toString().trim());


                    // tmp



                    connectionEstablished.disconnect();


                    // show text connect sucsessfull
                    Intent intent = new Intent(fragmentConnectToServerContext, ActivitySettingsEfb.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","show_connect_sucsessfull");
                    fragmentConnectToServerContext.startActivity(intent);

                } else { // connection error

                    // set connection status to connect error
                    ((ActivitySettingsEfb)getActivity()).setConnectionStatus(2); // 2 -> Connect error

                    connectingStatus = 2;


                    // show error text
                    Intent intent = new Intent(fragmentConnectToServerContext, ActivitySettingsEfb.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","show_connect_error");
                    fragmentConnectToServerContext.startActivity(intent);

                }






                Log.d("AsynTask","Empfangen: "+stringBuilder.toString().trim());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        // After completing background task
        @Override
        protected void onPostExecute(String result) {

            // dismiss the dialog after getting clientID and configuration
            pDialog.dismiss();

        }

    }








    private String makeXMLRequestForFirstConnection (String clientpin) {

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

            // start tag main order -> first connection, send pin
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_Order);
            xmlSerializer.text("first");
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_Order);

            // start tag client pin
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMainClientPin);
            xmlSerializer.text(clientpin);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMainClientPin);

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





    private String makeXMLRequestForConnectionEstablished (String clientId) {

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




