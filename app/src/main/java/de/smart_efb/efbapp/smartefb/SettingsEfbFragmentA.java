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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentA extends Fragment {

    // fragment view
    View viewFragmentConnectToServer;

     // fragment context
    Context fragmentConnectToServerContext = null;

    // minimum and maximum for random number to connect to server
    static int randomNumverForConnectionMin = 10000;
    static int randomNumverForConnectionMax = 99999;

    // the connecting status (0=not connected, 1=no network, try again, 2= pin out of time, 3=connected)
    int connectingStatus = 0;

    // actual random number for connetion to server
    int randomNumverForConnection = 0;

    // Connection helper object
    EfbHelperConnectionClass efbHelperConnectionClass;

    // Progress Dialog
    private ProgressDialog pDialog;


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
            randomNumverForConnection = EfbHelperClass.randomNumber(randomNumverForConnectionMin, randomNumverForConnectionMax);

            TextView textViewRandomNumberText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingConnectToServerKeyNumber);
            textViewRandomNumberText.setVisibility(View.VISIBLE);
            textViewRandomNumberText.setText(""+randomNumverForConnection);

            // show remark for clicking button
            TextView textViewConnectToServerRemarkClickButtonText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerRemarkClickButton);
            String tmpTextRemarkClickButton = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerRemarkClickButtonText);
            textViewConnectToServerRemarkClickButtonText.setText(tmpTextRemarkClickButton);
            textViewConnectToServerRemarkClickButtonText.setVisibility(View.VISIBLE);


            // send button
            Button tmpButton = (Button) viewFragmentConnectToServer.findViewById(R.id.buttonSendConnectToServerKeyNumber);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    if (efbHelperConnectionClass.internetAvailable()) {

                        Log.d ("MAIN","Before new AsynTask!");


                        new sendPinToServer().execute(Integer.toString(randomNumverForConnection));



                        Log.d ("MAIN","After new AsynTask!");

                    } else { // no network connection!

                        // call setter-methode setRandomNumberForConnection in ActivitySettingsEfb to set random number
                        ((ActivitySettingsEfb)getActivity()).setRandomNumberForConnection(randomNumverForConnection);

                        // call setter-methode setConnectionStatus in ActivitySettingsEfb to set connection status waiting for response
                        ((ActivitySettingsEfb)getActivity()).setConnectionStatus(1);

                        connectingStatus = 1;


                        dialogNoInternetAvailable ();


                        Intent intent = new Intent(fragmentConnectToServerContext, ActivitySettingsEfb.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("com","show_no_network_try_again");
                        fragmentConnectToServerContext.startActivity(intent);


                    }


                    // show toast number succsessfull send
                    //Toast.makeText(fragmentConnectToServerContext, fragmentConnectToServerContext.getResources().getString(R.string.sendConnectToServerKeyNumberSuccsesfulyText), Toast.LENGTH_SHORT).show();

                    /*
                    Intent intent = new Intent(fragmentConnectToServerContext, ActivitySettingsEfb.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","show_waiting_response");
                    fragmentConnectToServerContext.startActivity(intent);
                    */

                }
            });






        }


        // connecting status 1 -> no connection, no network, try again
        if (connectingStatus == 1) {


            Log.d ("MAIN","Status=1");

            // replace headline no network available
            TextView textViewConnectedWithServerHeadlineText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerHeadingNoInternet);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // replace text and show info text try again
            TextView textViewConnectToServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerIntro);
            String tmpTextIntroText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroTextNoInternet);
            textViewConnectToServerIntroText.setText(tmpTextIntroText);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);

            // get random number from prefs and show
            randomNumverForConnection = ((ActivitySettingsEfb)getActivity()).getRandomNumberForConnection();


            TextView textViewRandomNumberText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingConnectToServerKeyNumber);
            textViewRandomNumberText.setVisibility(View.VISIBLE);
            textViewRandomNumberText.setText(""+randomNumverForConnection);

            // show remark for clicking button
            TextView textViewConnectToServerRemarkClickButtonText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerRemarkClickButton);
            String tmpTextRemarkClickButton = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerRemarkNoInternetClickButtonText);
            textViewConnectToServerRemarkClickButtonText.setText(tmpTextRemarkClickButton);
            textViewConnectToServerRemarkClickButtonText.setVisibility(View.VISIBLE);

            Button tmpButton = (Button) viewFragmentConnectToServer.findViewById(R.id.buttonSendConnectToServerKeyNumber);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {




                    if (efbHelperConnectionClass.internetAvailable()) {

                        Log.d ("MAIN","Before new AsynTask!");


                        new sendPinToServer().execute(Integer.toString(randomNumverForConnection));



                        Log.d ("MAIN","After new AsynTask!");



                    } else { // no network connection!




                        dialogNoInternetAvailable ();




                    }


                    // show toast number succsessfull send
                    //Toast.makeText(fragmentConnectToServerContext, fragmentConnectToServerContext.getResources().getString(R.string.sendConnectToServerKeyNumberSuccsesfulyText), Toast.LENGTH_SHORT).show();

                    /*
                    Intent intent = new Intent(fragmentConnectToServerContext, ActivitySettingsEfb.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","show_waiting_response");
                    fragmentConnectToServerContext.startActivity(intent);
                    */

                }
            });




            /*

            // replace headline connect to server
            TextView textViewConnectedWithServerHeadlineText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // show waiting for response intro text
            TextView textViewConnectToServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsWaitingForResponseIntro);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);

            */
        }

        // connecting status 2 -> connected with server
        if (connectingStatus == 2) {

            // replace headline connected with server
            TextView textViewConnectedWithServerHeadlineText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectedWithServerIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            /*
            // show connected to server intro text
            TextView textViewConnectedWithServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerSuccessful);
            textViewConnectedWithServerIntroText.setVisibility(View.VISIBLE);
            textViewConnectedWithServerIntroText.setMovementMethod(LinkMovementMethod.getInstance());
            */
        }





    }



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



    private void dialogWaitingForResponse () {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(fragmentConnectToServerContext.getResources().getString(R.string.settingsSendingToServerDialogInfoText));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

    }








    //+++++++++++++++++++++++++++++

    /**
     * Background Async Task to send pin to server
     * */
    class sendPinToServer extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("AsynTask","Vor Pre execut!");
            dialogWaitingForResponse();
        }

        /**
         * getting clientID and configuration from server
         */
        protected String doInBackground(String... args) {

            Log.d("AsynTask","Vor try Gesendet");

            try {
                String textparam = "clientpin=" + URLEncoder.encode(args[0], "UTF-8");

                URL scripturl = new URL(ConstansClassSettings.urlFirstConnectToServer);
                HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                contentWriter.write(textparam);
                contentWriter.flush();

                contentWriter.close();


                InputStream answerInputStream = connection.getInputStream();


                BufferedReader reader = new BufferedReader(new InputStreamReader(answerInputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String aktuelleZeile;
                try {
                    while ((aktuelleZeile = reader.readLine()) != null){
                        stringBuilder.append(aktuelleZeile);
                        stringBuilder.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //return stringBuilder.toString().trim();



                //final String answer = getTextFromInputStream(answerInputStream);


                answerInputStream.close();
                connection.disconnect();


                Log.d("AsynTask","Empfangen:"+stringBuilder.toString().trim());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute() {

            Log.d ("AsynTask","OnPostExecute!t");

            // dismiss the dialog after getting clientID and configuration
            pDialog.dismiss();


        }


        //+++++++++++++++++++++++++++++++++

    }








}




