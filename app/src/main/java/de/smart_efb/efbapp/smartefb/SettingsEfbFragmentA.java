package de.smart_efb.efbapp.smartefb;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

    // the fragment
    Fragment fragmentSettingsEfbAThisFragment;

    // shared prefs for settings
    SharedPreferences prefs;

    // reference to the DB
    DBAdapter myDb;

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

    // communication error text
    String errorCommunicationText = "";


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentConnectToServer = layoutInflater.inflate(R.layout.fragment_settings_efb_a, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(settingsFragmentABrodcastReceiver, filter);

        return viewFragmentConnectToServer;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentConnectToServerContext = getActivity().getApplicationContext();

        fragmentSettingsEfbAThisFragment = this;

        efbHelperConnectionClass = new EfbHelperConnectionClass(fragmentConnectToServerContext);

        // init the fragment connect to server
        initFragmentConnectToServer();

        // show actual connecting informations
        displayActualConnectingInformation();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentConnectToServerContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentConnectToServerContext, startServiceIntent);
         }

    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(settingsFragmentABrodcastReceiver);

        // close db connection
        myDb.close();
    }


    private void initFragmentConnectToServer () {

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get connection status
        connectingStatus = ((ActivitySettingsEfb)getActivity()).getConnectingStatus();

        // init the prefs
        prefs = fragmentConnectToServerContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentConnectToServerContext.MODE_PRIVATE);

        // init the DB
        myDb = new DBAdapter(fragmentConnectToServerContext);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver settingsFragmentABrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // set global error text communication
            errorCommunicationText = "";

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                Boolean refreshView = false;

                // get communcation error, when given
                String tmpErrorCommunication = intentExtras.getString("ErrorCommunication", "0");
                String tmpErrorCommunicationText = intentExtras.getString("ErrorText", "Test");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentConnectToServerContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    refreshView = true;

                } else if (tmpErrorCommunication != null && tmpErrorCommunication.equals("1") && tmpErrorCommunicationText != null && tmpErrorCommunicationText.length() > 0) {

                    // copy error communication text to global
                    errorCommunicationText = tmpErrorCommunicationText;

                    // update the view
                    displayActualConnectingInformation ();
                }

                if (refreshView) {
                    // call getter-methode getConnectionStatus in ActivitySettingsEfb to get connection status
                    connectingStatus = ((ActivitySettingsEfb)getActivity()).getConnectingStatus();

                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentSettingsEfbAThisFragment).attach(fragmentSettingsEfbAThisFragment).commit();
                }
            }
        }
    };


    // show actual process data of connecting to server
    private void displayActualConnectingInformation () {

        // show hint text case close
        if (prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            LinearLayout linearLayoutCaseCloseTextHolder =  viewFragmentConnectToServer.findViewById(R.id.layoutHolderHintTextCaseClose);
            linearLayoutCaseCloseTextHolder.setVisibility(View.VISIBLE);

        }

        // connecting status 0 -> not connected to server
        if (connectingStatus == 0) {

            // replace headline connect to server
            TextView textViewConnectedWithServerHeadlineText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // replace text and show connecting intro text
            TextView textViewConnectToServerIntroText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerIntro);
            String tmpTextIntroText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroText);
            textViewConnectToServerIntroText.setText(tmpTextIntroText);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);


            // generate new connection number (5 digits)
            TextView tmpTextViewGenerateNewConnectionNumber = viewFragmentConnectToServer.findViewById(R.id.settingsEfbALinkGenerateNewConnectionNumber);

            final Uri.Builder newConnectionNumberLinkBuilder = new Uri.Builder();
            newConnectionNumberLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("settings")
                    .appendQueryParameter("com", "generate_new_connection_number");

            String tmpLinkTextNewConnectionNumber = fragmentConnectToServerContext.getResources().getString(R.string.settingsGenerateNewConnectionNumberText);

            // generate link for output
            Spanned tmpLinkNewConnectionNumber = HtmlCompat.fromHtml("<a href=\"" + newConnectionNumberLinkBuilder.build().toString() + "\">" + tmpLinkTextNewConnectionNumber + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);

            // and set to textview
            tmpTextViewGenerateNewConnectionNumber.setVisibility(View.VISIBLE);
            tmpTextViewGenerateNewConnectionNumber.setText(tmpLinkNewConnectionNumber);
            tmpTextViewGenerateNewConnectionNumber.setMovementMethod(LinkMovementMethod.getInstance());

            // get random connection number from parent
            randomNumberForConnection = ((ActivitySettingsEfb)getActivity()).getRandomNumberForConnection();
            // check random number == 0?

            if (randomNumberForConnection == 0) {
                // first generate random number
                int tmpNumber = EfbHelperClass.randomNumber(ConstansClassSettings.randomNumberForConnectionMin, ConstansClassSettings.randomNumberForConnectionMax);
                ((ActivitySettingsEfb)getActivity()).setRandomNumberForConnection(tmpNumber);
                randomNumberForConnection = tmpNumber;
            }

            TextView textViewRandomNumberText = viewFragmentConnectToServer.findViewById(R.id.settingConnectToServerKeyNumber);
            textViewRandomNumberText.setVisibility(View.VISIBLE);
            textViewRandomNumberText.setText(""+randomNumberForConnection);

            // show remark for clicking button
            TextView textViewConnectToServerRemarkClickButtonText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerRemarkClickButton);
            String tmpTextRemarkClickButton = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerRemarkClickButtonText);
            textViewConnectToServerRemarkClickButtonText.setText(tmpTextRemarkClickButton);
            textViewConnectToServerRemarkClickButtonText.setVisibility(View.VISIBLE);

            // send button
            Button tmpButton = viewFragmentConnectToServer.findViewById(R.id.buttonSendConnectToServerKeyNumber);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener send pin number
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            TextView textViewConnectedWithServerHeadlineText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerHeadingNoInternetOrError);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // replace text and show info text try again
            TextView textViewConnectToServerIntroText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerIntro);
            String tmpTextIntroText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerIntroTextNoInternetOrError);
            textViewConnectToServerIntroText.setText(tmpTextIntroText);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);

            // get random number from prefs and show
            randomNumberForConnection = ((ActivitySettingsEfb)getActivity()).getRandomNumberForConnection();

            TextView textViewRandomNumberText = viewFragmentConnectToServer.findViewById(R.id.settingConnectToServerKeyNumber);
            textViewRandomNumberText.setVisibility(View.VISIBLE);
            textViewRandomNumberText.setText(""+randomNumberForConnection);

            // show remark for clicking button
            TextView textViewConnectToServerRemarkClickButtonText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerRemarkClickButton);
            String tmpTextRemarkClickButton = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerRemarkNoInternetClickButtonText);
            textViewConnectToServerRemarkClickButtonText.setText(tmpTextRemarkClickButton);
            textViewConnectToServerRemarkClickButtonText.setVisibility(View.VISIBLE);

            Button tmpButton = viewFragmentConnectToServer.findViewById(R.id.buttonSendConnectToServerKeyNumber);
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
            TextView textViewConnectedWithServerHeadlineText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectedWithServerErrorIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // show error pre intro text
            TextView textViewConnectedWithServerErrorPreIntroText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerConnectionErrorPreIntro);
            textViewConnectedWithServerErrorPreIntroText.setVisibility(View.VISIBLE);

            // show error text
            TextView textViewConnectedWithServerErrorTextError = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerConnectionErrorTextError);
            // get last error text from prefs (ActivitySettingsEfb) and show
            String lastErrorText = ((ActivitySettingsEfb)getActivity()).getLastErrorText();
            textViewConnectedWithServerErrorTextError.setText(lastErrorText);
            textViewConnectedWithServerErrorTextError.setVisibility(View.VISIBLE);
            ((ActivitySettingsEfb)getActivity()).deleteLastErrorText(); // delete last error text!!!

            // show error post intro text
            TextView textViewConnectedWithServerErrorPostIntroText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerConnectionErrorPostIntro);
            textViewConnectedWithServerErrorPostIntroText.setVisibility(View.VISIBLE);

            // set connection status to not connected so far
            ((ActivitySettingsEfb)getActivity()).setConnectionStatus(1); // 0 -> not connected so far

            connectingStatus = 1;
        }

        // connecting status 3 -> sucsessfull connected with server
        if (connectingStatus == 3) {

            // replace headline connected with server
            TextView textViewConnectedWithServerHeadlineText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerHeadingIntro);
            String tmpTextHeadline = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectedWithServerIntroHeadingText);
            textViewConnectedWithServerHeadlineText.setText(tmpTextHeadline);

            // show connected to server intro text
            TextView textViewConnectedWithServerIntroText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerSucsessfullIntro);

            String tmpTextIntroHeadingText;
            if (prefs.getLong(ConstansClassSettings.namePrefsFirstInitTimeInMills, 0L) > 0) {
                tmpTextIntroHeadingText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerSuccessfulTextWithDate);
                tmpTextIntroHeadingText = String.format(tmpTextIntroHeadingText, EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassSettings.namePrefsFirstInitTimeInMills, 0L), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassSettings.namePrefsFirstInitTimeInMills, 0L), "HH:mm"));
            }
            else {
                tmpTextIntroHeadingText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerSuccessfulTextWithoutDate);
            }
            textViewConnectedWithServerIntroText.setText(HtmlCompat.fromHtml(tmpTextIntroHeadingText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            textViewConnectedWithServerIntroText.setVisibility(View.VISIBLE);
            textViewConnectedWithServerIntroText.setMovementMethod(LinkMovementMethod.getInstance());

            // get all case involved persons like coach or clients
            Cursor case_coaches = myDb.getInvolvedPerson("coach");
            Cursor case_clients = myDb.getInvolvedPerson("client");

            Boolean first_set = true;
            String modifiedTime = "";
            String tmpAllCoachesForView = "";

            if (case_coaches != null && case_coaches.getCount() > 0) {

                String tmpFunctionCoachString = fragmentConnectToServerContext.getResources().getString(R.string.functionStringForCoach);
                case_coaches.moveToFirst();
                do {
                    tmpAllCoachesForView += case_coaches.getString(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_NAME)) + " " + tmpFunctionCoachString + "<br />";
                    if (first_set) {
                        modifiedTime = EfbHelperClass.timestampToDateFormat(case_coaches.getLong(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_MODIFIED_TIME)), "dd.MM.yyyy - HH:mm");
                        first_set = false;
                    }
                } while (case_coaches.moveToNext());

                // add client names when possible
                if (case_clients != null && case_clients.getCount() > 0) {
                    case_clients.moveToFirst();
                    do {
                        tmpAllCoachesForView += case_clients.getString(case_clients.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_NAME)) + "<br />";
                    } while (case_clients.moveToNext());
                }

                // put involved person to view
                LinearLayout linearLayoutInvolvedPersonCoaches =  viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerInvolvedPersonHeadline);
                linearLayoutInvolvedPersonCoaches.setVisibility(View.VISIBLE);
                // explain text with date
                String tmpExplainText = fragmentConnectToServerContext.getResources().getString(R.string.settingsConnectToServerInvolvedPersonHeadlineExplainText);
                String tmpExplainTextWithTime = String.format(tmpExplainText, modifiedTime);
                TextView textViewHeadlineExplainText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerInvolvedPersonHeadlineExplainText);
                textViewHeadlineExplainText.setText(tmpExplainTextWithTime);
                // set border visible
                TextView textViewBorder = viewFragmentConnectToServer.findViewById(R.id.borderBetweenMessageAndInvolvedPerson);
                textViewBorder.setVisibility(View.VISIBLE);
                // set coach and client names to view
                TextView textViewInvolvedPersonCoaches = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerInvolvedPersonCoachesAndClients);
                textViewInvolvedPersonCoaches.setText(HtmlCompat.fromHtml(tmpAllCoachesForView, HtmlCompat.FROM_HTML_MODE_LEGACY));
                textViewInvolvedPersonCoaches.setVisibility(View.VISIBLE);
            }

            // look for precense text from coaches
            if (case_coaches != null && case_coaches.getCount() > 0) {

                String tmpAllPresenceTextCoaches = "";
                Boolean precense_text_found = false;

                case_coaches.moveToFirst();
                do {
                    if  (case_coaches.getString(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_PRESENCE_TEXT_ONE)).length() > 0) {

                        Long tmpActualTime = System.currentTimeMillis();

                        if (case_coaches.getString(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_PRESENCE_TEXT_TWO)).length() > 0 && case_coaches.getLong(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_PRESENCE_TWO_START)) < tmpActualTime && case_coaches.getLong(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_PRESENCE_TWO_END)) > tmpActualTime) {
                            tmpAllPresenceTextCoaches += "<b>" + case_coaches.getString(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_NAME)) + "</b><br />";
                            tmpAllPresenceTextCoaches += case_coaches.getString(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_PRESENCE_TEXT_TWO)) + "<br /><br />";
                            precense_text_found = true;
                        }
                        else {
                            tmpAllPresenceTextCoaches += "<b>" + case_coaches.getString(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_NAME)) + "</b><br />";
                            tmpAllPresenceTextCoaches += case_coaches.getString(case_coaches.getColumnIndex(DBAdapter.INVOLVED_PERSON_KEY_PRESENCE_TEXT_ONE)) + "<br /><br />";
                            precense_text_found = true;
                        }
                   }
                } while (case_coaches.moveToNext());

                if (precense_text_found) {

                    // put involved person precense text to view
                    LinearLayout linearLayoutInvolvedPersonPresenceContainer =  viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerInvolvedPersonPresenceContainer);
                    linearLayoutInvolvedPersonPresenceContainer.setVisibility(View.VISIBLE);

                    // set border visible
                    TextView textViewBorder = viewFragmentConnectToServer.findViewById(R.id.borderBetweenInvolvedPersonAndPresenceText);
                    textViewBorder.setVisibility(View.VISIBLE);
                    // set presence text to view
                    TextView textViewInvolvedPersonPrecenseText = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerInvolvedPersonPresenceText);
                    textViewInvolvedPersonPrecenseText.setText(HtmlCompat.fromHtml(tmpAllPresenceTextCoaches, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    textViewInvolvedPersonPrecenseText.setVisibility(View.VISIBLE);
                }
            }

            // show last contact time to server, when set! and connection status, like communication error
            if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                LinearLayout linearLayoutLastContactContainer = viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerLastContactTimeToServer);
                linearLayoutLastContactContainer.setVisibility(View.VISIBLE);
                TextView textViewBorderLastContactTime =  viewFragmentConnectToServer.findViewById(R.id.borderBetweenInvolvedPersonAndLastContactTime);
                textViewBorderLastContactTime.setVisibility(View.VISIBLE);
                TextView textViewLastContactTime =  viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerLastContactTimeToServerTimeText);
                String lastContactString = String.format(viewFragmentConnectToServer.getResources().getString(R.string.settingsSendingToServerLastContactTimeTimeText), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, System.currentTimeMillis()), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, System.currentTimeMillis()), "HH:mm"));
                textViewLastContactTime.setText(lastContactString);
                textViewLastContactTime.setVisibility(View.VISIBLE);

                // show communication error
                String lastCommunicationStatusServer;
                TextView textViewCommunicationStatusServer =  viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerLastCommunicationStatusServer);
                if (errorCommunicationText.length() > 0) {
                    String preErrorCommunicationText = viewFragmentConnectToServer.getResources().getString(R.string.settingsSendingToServerLastCommunicationStatusTextPreErrorText);
                    lastCommunicationStatusServer = preErrorCommunicationText + " " +errorCommunicationText;
                }
                else {
                    lastCommunicationStatusServer = viewFragmentConnectToServer.getResources().getString(R.string.settingsSendingToServerLastCommunicationStatusTextOk);
                }
                textViewCommunicationStatusServer.setText(lastCommunicationStatusServer );
                textViewCommunicationStatusServer.setVisibility(View.VISIBLE);
            }
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

                    // prepair data to send -> send all data correct received to server
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
            xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

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

            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
            xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

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
            xmlSerializer.setFeature(ConstansClassXmlParser.xmlFeatureLink, true);

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
            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ContactId);

            xmlSerializer.startTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);
            xmlSerializer.text(ConstansClassMain.localeAppVersionAsString);
            xmlSerializer.endTag("", ConstansClassXmlParser.xmlNameForMain_ThisAppVersion);

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


