package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 24.11.2017.
 */

public class MeetingFragmentMeetingClientCanceled extends Fragment {

    // fragment view
    View viewFragmentClientCanceledMeeting;

    // fragment context
    Context fragmentClientCanceledMeetingContext = null;

    // the fragment
    Fragment fragmentThisFragmentContext;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // cursor for the canceled meeting
    Cursor cursorCanceledMeeting = null;

    // db id of the actual canceled meeting
    Long clientCanceledMeetingId = 0L;

    // array of meeting places names (only 3 possible-> 0=nothing; 1=Werder(Havel); 2=Bad Belzig)
    String  meetingPlaceNames[] = new String[3];


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentClientCanceledMeeting = layoutInflater.inflate(R.layout.fragment_meeting_client_canceled, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(meetingFragmentMeetingClientCanceledBrodcastReceiver, filter);

        return viewFragmentClientCanceledMeeting;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentClientCanceledMeetingContext = getActivity().getApplicationContext();

        fragmentThisFragmentContext = this;

        // init fragment
        initFragmentMeetingClientCanceled();

        // build view
        buildFragmentClientCanceledMeetingView();
    }


    private void initFragmentMeetingClientCanceled () {

        callGetterFunctionInSuper();

        // init the DB
        myDb = new DBAdapter(fragmentClientCanceledMeetingContext);

        // get canceled meeting data
        cursorCanceledMeeting = myDb.getOneRowMeetingsOrSuggestion(clientCanceledMeetingId);

        // get all possible meeting places
        meetingPlaceNames = fragmentClientCanceledMeetingContext.getResources().getStringArray(R.array.placesNameForMeetingArray);

        // init the prefs
        prefs = fragmentClientCanceledMeetingContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentClientCanceledMeetingContext.MODE_PRIVATE);

        // set correct subtitle
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleClientCanceledMeeting", "string", fragmentClientCanceledMeetingContext.getPackageName()));
        ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, "meeting_client_canceled");
    }


    // call getter Functions in ActivityMeeting for some data
    private void callGetterFunctionInSuper () {

        // call getter-methode getActualMeetingDbId() in ActivityMeeting to get DB ID for the actuale meeting
        clientCanceledMeetingId = ((ActivityMeeting)getActivity()).getActualMeetingDbId();
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(meetingFragmentMeetingClientCanceledBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeServiceEfb
    private BroadcastReceiver meetingFragmentMeetingClientCanceledBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                Boolean refreshView = false;

                String tmpExtraMeeting = intentExtras.getString("Meeting","0");
                String tmpExtraMeetingNewMeeting = intentExtras.getString("MeetingNewMeeting","0");
                String tmpExtraMeetingCanceledMeetingByCoach = intentExtras.getString("MeetingCanceledMeetingByCoach","0");
                String tmpExtraMeetingSettings = intentExtras.getString("MeetingSettings","0");
                String tmpCommand = intentExtras.getString("Command");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentClientCanceledMeetingContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingNewMeeting != null && tmpExtraMeetingNewMeeting.equals("1")) {
                    // new meeting on smartphone -> show toast
                    String updateNewMeeting = fragmentClientCanceledMeetingContext.getString(R.string.toastMessageMeetingNewMeeting);
                    Toast toast = Toast.makeText(context, updateNewMeeting, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingCanceledMeetingByCoach != null && tmpExtraMeetingCanceledMeetingByCoach.equals("1")) {
                    // meeting canceled by coach -> show toast
                    String updateNewMeeting = fragmentClientCanceledMeetingContext.getString(R.string.toastMessageMeetingCanceledMeetingByCoach);
                    Toast toast = Toast.makeText(context, updateNewMeeting, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpCommand != null && tmpCommand.length() > 0) { // send successfull?

                    if (tmpCommand.equals("ask_parent_activity")) {

                        // get successfull message from parent
                        String successfullMessage = ((ActivityMeeting) getActivity()).getSuccessefullMessageForSending ();
                        if (successfullMessage.length() > 0) {
                            Toast toast = Toast.makeText(context, successfullMessage, Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                    }
                }
                else if (tmpSendNotSuccessefull != null && tmpSendNotSuccessefull.equals("1") && tmpCommand != null && tmpCommand.length() > 0) { // send not successfull?

                    if (tmpCommand.equals("ask_parent_activity")) {

                        // get not successfull message from parent
                        String notSuccessfullMessage = ((ActivityMeeting) getActivity()).getNotSuccessefullMessageForSending ();
                        if (notSuccessfullMessage.length() > 0) {
                            Toast toast = Toast.makeText(context, notSuccessfullMessage, Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                    }
                    else if (tmpCommand.equals("look_message") && tmpMessage != null && tmpMessage.length() > 0) {

                        Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        if( v != null) v.setGravity(Gravity.CENTER);
                        toast.show();
                    }
                }
                else if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingSettings != null && tmpExtraMeetingSettings.equals("1")) {

                    // meeting settings change
                    refreshView = true;
                }

                if (refreshView) {
                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentThisFragmentContext).attach(fragmentThisFragmentContext).commit();
                }

            }
        }
    };


    // build the view for the fragment
    private void buildFragmentClientCanceledMeetingView () {

        if (cursorCanceledMeeting != null && cursorCanceledMeeting.getCount() > 0) {

            // textview for the intro text, like meeting
            TextView textViewInfoCanceledMeeting = (TextView) viewFragmentClientCanceledMeeting.findViewById(R.id.meetingCanceledInfoText);
            String meetingDate = EfbHelperClass.timestampToDateFormat(cursorCanceledMeeting.getLong(cursorCanceledMeeting.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy");
            String meetingTime = EfbHelperClass.timestampToDateFormat(cursorCanceledMeeting.getLong(cursorCanceledMeeting.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm");
            String meetingPLace = meetingPlaceNames[cursorCanceledMeeting.getInt(cursorCanceledMeeting.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))];
            String tmpInfoCanceledMeetingText = String.format(fragmentClientCanceledMeetingContext.getResources().getString(R.string.meetingClientCanceledIntroText), meetingDate, meetingTime, meetingPLace );
            textViewInfoCanceledMeeting.setText(tmpInfoCanceledMeetingText);

            // generate back link "zurueck zu den Terminen"
            Uri.Builder backMeetingLinkBuilder = new Uri.Builder();
            backMeetingLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("meeting")
                    .appendQueryParameter("meeting_id", "0")
                    .appendQueryParameter("com", "meeting_overview");
            TextView backLinkToMeetingOverview = (TextView) viewFragmentClientCanceledMeeting.findViewById(R.id.meetingBackLinkToOverview);
            backLinkToMeetingOverview.setText(Html.fromHtml("<a href=\"" + backMeetingLinkBuilder.build().toString() + "\">" + fragmentClientCanceledMeetingContext.getResources().getString(fragmentClientCanceledMeetingContext.getResources().getIdentifier("meetingBackLinkToMeetingOverview", "string", fragmentClientCanceledMeetingContext.getPackageName())) + "</a>"));
            backLinkToMeetingOverview.setMovementMethod(LinkMovementMethod.getInstance());

            // get max letters for edit text comment
            final int tmpMaxLength = ConstansClassMeeting.namePrefsMaxLettersCanceledMeetingReason;

            // get textView to count input letters and init it
            final TextView textViewCountLettersReasonEditText = (TextView) viewFragmentClientCanceledMeeting.findViewById(R.id.countLettersCanceledEditText);
            String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
            tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
            textViewCountLettersReasonEditText.setText(tmpInfoTextCountLetters);

            // comment textfield -> set hint text
            final EditText txtInputCanceledReason = (EditText) viewFragmentClientCanceledMeeting.findViewById(R.id.inputCanceledReason);

            // set text watcher to count letters in comment field
            final TextWatcher txtInputArrangementCommentTextWatcher = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //
                    String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
                    tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), tmpMaxLength);
                    textViewCountLettersReasonEditText.setText(tmpInfoTextCountLetters);
                }
                public void afterTextChanged(Editable s) {
                }
            };

            // set text watcher to count input letters
            txtInputCanceledReason.addTextChangedListener(txtInputArrangementCommentTextWatcher);

            // set input filter max length for canceled reason field
            txtInputCanceledReason.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

            // get button send comment
            Button buttonSendCanceledReason = (Button) viewFragmentClientCanceledMeeting.findViewById(R.id.buttonSendCanceledReason);

            // set onClick listener send canceled reason
            buttonSendCanceledReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (txtInputCanceledReason.getText().toString().length() > 3) {

                        // canceled time
                        Long tmpCanceledTime = System.currentTimeMillis();

                        // canceled status
                        int tmpStatus = 0; // not send to server

                        // insert  in DB
                        myDb.updateMeetingCanceledByClient(clientCanceledMeetingId, tmpCanceledTime, prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"), txtInputCanceledReason.getText().toString(), tmpStatus);

                        // set successfull message in parent activity -> show in toast, when canceled message is send successfull
                        String tmpSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageMeetingCanceledMeetingByClientSuccessfullSend", "string", fragmentClientCanceledMeetingContext.getPackageName()));
                        ((ActivityMeeting) getActivity()).setSuccessefullMessageForSending (tmpSuccessfullMessage);

                        // set not successfull message in parent activity -> show in toast, when canceled message is send not successfull
                        String tmpNotSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageMeetingCanceledMeetingByClientNotSuccessfullSend", "string", fragmentClientCanceledMeetingContext.getPackageName()));
                        ((ActivityMeeting) getActivity()).setNotSuccessefullMessageForSending (tmpNotSuccessfullMessage);

                        // send intent to service to start the service and send canceled meeting to server!
                        Intent startServiceIntent = new Intent(fragmentClientCanceledMeetingContext, ExchangeServiceEfb.class);
                        startServiceIntent.putExtra("com","send_meeting_data");
                        startServiceIntent.putExtra("dbid",clientCanceledMeetingId);
                        fragmentClientCanceledMeetingContext.startService(startServiceIntent);

                        // build intent to go back to meetingOverview
                        Intent intent = new Intent(getActivity(), ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com", "meeting_overview");
                        getActivity().startActivity(intent);

                    } else {

                        TextView tmpErrorTextView = (TextView) viewFragmentClientCanceledMeeting.findViewById(R.id.errorInputMeetingCanceled);
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            });

            // button abbort
            Button buttonAbbortCanceledReason = (Button) viewFragmentClientCanceledMeeting.findViewById(R.id.buttonAbortCanceled);
            // onClick listener button abbort
            buttonAbbortCanceledReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com", "meeting_overview");
                    getActivity().startActivity(intent);

                }
            });
        }
    }

}
