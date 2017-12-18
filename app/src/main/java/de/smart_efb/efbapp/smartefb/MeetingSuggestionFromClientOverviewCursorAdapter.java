package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 12.12.2017.
 */

public class MeetingSuggestionFromClientOverviewCursorAdapter extends CursorAdapter {

    // code kontrollieren !!!!!!!!!!!!!!!!!!!


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingSuggestionOverviewCursorAdapterContext;


    // reference to the DB
    DBAdapter myDb;

    // array of meeting places names (only 3 possible-> 0=nothing; 1=Werder(Havel); 2=Bad Belzig)
    String  meetingPlaceNames[] = new String[3];

    // for prefs
    SharedPreferences prefs;


    // Own constructor
    public MeetingSuggestionFromClientOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewCursorAdapterContext = context;

        // init the DB
        myDb = new DBAdapter(context);

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        meetingPlaceNames = context.getResources().getStringArray(R.array.placesNameForMeetingArray);

    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


    }



    @Override
    public View newView(Context mContext, Cursor mCursor, ViewGroup parent) {

        final View inflatedView;

        final Context context = mContext;

        final Cursor cursor = mCursor;


        Long tmpNowTime = System.currentTimeMillis();

        Boolean tmpInTimezone = false;
        Boolean tmpOutTimezone = false;
        Boolean tmpSuggestionFromClientAlreadySend = false;
        Boolean tmpSuggestionFromClientCanceled = false;
        Boolean tmpSuggestionFromClientMeetingFound = false;

        // get the view
        inflatedView = cursorInflater.inflate(R.layout.list_meeting_suggestion_from_client_overview_normal, parent, false);


        // check suggestion from client canceled?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {
            tmpSuggestionFromClientCanceled = true;
        }
        else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND)) == 1) {
            // meeting found from suggestion
            tmpSuggestionFromClientMeetingFound = true;
        }
        else if (cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_AUTHOR)).length() > 0 && cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT)).length() > 0 && cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME)).length() > 0) {
            tmpSuggestionFromClientAlreadySend = true;
        }
        else if (cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)) < tmpNowTime && cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)) > tmpNowTime) {
            // in timezone
            tmpInTimezone = true;
        }
        else {
            // out timezone
            tmpOutTimezone = true;
        }




        // show intro text different in timezone/ out timezone
        if (tmpInTimezone || tmpOutTimezone) {

            // show intro text different in timezone/ out timezone
            String actualSuggestionFromClientHeadline = "";
            TextView textViewSuggestionFromClientIntroText = (TextView) inflatedView.findViewById(R.id.suggestionFromClientIntroInfoText);
            if (tmpInTimezone) {
                actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientIntroInfoTextInTimezone);
            }
            else {
                actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientIntroInfoTextOutTimezone);
            }
            textViewSuggestionFromClientIntroText.setText(actualSuggestionFromClientHeadline);
            textViewSuggestionFromClientIntroText.setVisibility(View.VISIBLE);
        }


        // set subtitle and info text+author for suggestion from client
        TextView textViewSuggestionFromClientTitleAndNumber = (TextView) inflatedView.findViewById(R.id.suggestionFromClientTitle);
        TextView textViewSuggestionFromClientAuthorAndDate = (TextView) inflatedView.findViewById(R.id.suggestionFromClientAuthorAndDate);
        String actualSuggestionFromClientHeadline = "";
        String tmpTextAuthorAndDate = "";
        String suggestionFromClientStartDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy");;
        String suggestionFromClientStartTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm");;
        String suggestionFromClientEndDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy");;
        String suggestionFromClientEndTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm");;
        String suggestionFromClientTimezoneAuthor = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR));
        String suggestionFromClientTimezoneCreationDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "dd.MM.yyyy");;
        String suggestionFromClientTimezoneCreationTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "HH:mm");;

        if (tmpSuggestionFromClientCanceled) { // suggestion from client canceled
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextCanceled);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientAuthorAndDateCanceled), suggestionFromClientStartDate, suggestionFromClientStartTime, suggestionFromClientEndDate, suggestionFromClientEndTime, suggestionFromClientTimezoneAuthor, suggestionFromClientTimezoneCreationDate, suggestionFromClientTimezoneCreationTime);

        }
        else if (tmpSuggestionFromClientMeetingFound) { // meeting found from suggestion from client
            String suggestionFromClientMeetingFoundDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE)), "dd.MM.yyyy");;
            String suggestionFromClientMeetingFoundTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE)), "HH:mm");;
            String suggestionFromClientMeetingFoundAuthor = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_AUTHOR));
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextFoundMeeting);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientAuthorAndDateMeetingFound), suggestionFromClientMeetingFoundAuthor, suggestionFromClientMeetingFoundDate, suggestionFromClientMeetingFoundTime, suggestionFromClientStartDate, suggestionFromClientStartTime, suggestionFromClientEndDate, suggestionFromClientEndTime);

        }
        else if (tmpSuggestionFromClientAlreadySend) {
            String suggestionFromClientMeetingSendDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME)), "dd.MM.yyyy");;
            String suggestionFromClientMeetingSendTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TIME)), "HH:mm");;
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextAlreadySend);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientAuthorAndDateAlreadySend), suggestionFromClientMeetingSendDate, suggestionFromClientMeetingSendTime);

        }
        else if (tmpInTimezone) { // suggestion from client in timezone
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextInTimezone);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientInTimezone), suggestionFromClientEndDate, suggestionFromClientEndTime, suggestionFromClientStartDate, suggestionFromClientStartTime, suggestionFromClientTimezoneAuthor, suggestionFromClientTimezoneCreationDate, suggestionFromClientTimezoneCreationTime);

        }
        else { // suggestion from client out timezone
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextOutTimezone);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientOutTimezone), suggestionFromClientStartDate, suggestionFromClientStartTime, suggestionFromClientEndDate, suggestionFromClientEndTime, suggestionFromClientTimezoneAuthor, suggestionFromClientTimezoneCreationDate, suggestionFromClientTimezoneCreationTime);

        }
        textViewSuggestionFromClientTitleAndNumber.setText(actualSuggestionFromClientHeadline);
        textViewSuggestionFromClientAuthorAndDate.setText(tmpTextAuthorAndDate);




        
        
        // we are in timezone -> show input field
        if (tmpInTimezone && cursor.isFirst()) {

            
            // Error Text view -> errorInputSuggestionFromClient


            // get max letters for edit text comment
            final int tmpMaxLength = ConstansClassMeeting.namePrefsSuggestionFromClientMaxLetters;

            // get textView to count input letters and init it
            final TextView textViewCountLettersSuggestionFromClientEditText = (TextView) inflatedView.findViewById(R.id.countLettersSuggestionFromClientEditText);
            String tmpInfoTextCountLetters = context.getResources().getString(R.string.infoTextCountLettersForComment);
            tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
            textViewCountLettersSuggestionFromClientEditText.setText(tmpInfoTextCountLetters);
            textViewCountLettersSuggestionFromClientEditText.setVisibility(View.VISIBLE);

            // suggestion from client textfield
            final EditText txtInputSuggestionFromClient = (EditText) inflatedView.findViewById(R.id.inputSuggestionFromClientText);

            // set text watcher to count letters in inputfield
            final TextWatcher txtInputTextWatcher = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String tmpInfoTextCountLetters =  context.getResources().getString(R.string.infoTextCountLettersForComment);
                    tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), tmpMaxLength);
                    textViewCountLettersSuggestionFromClientEditText.setText(tmpInfoTextCountLetters);
                }
                public void afterTextChanged(Editable s) {
                }
            };

            // set edit text field to visible
            txtInputSuggestionFromClient.setVisibility(View.VISIBLE);

            // set text watcher to count input letters
            txtInputSuggestionFromClient.addTextChangedListener(txtInputTextWatcher);

            // set input filter max length for comment field
            txtInputSuggestionFromClient.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

            // set count down timer
            final String tmpTextResponseTimeOver = context.getResources().getString(R.string.suggestionFromClientTimeIsOver);

            // get text view for timer place and set visible
            final TextView placeholderForTicTimer = (TextView) inflatedView.findViewById(R.id.suggestionFromClientEndDateTicTimer);
            placeholderForTicTimer.setVisibility(View.VISIBLE);

            // set intro text timer to visible
            TextView placeholderForTicTimerAskedFor = (TextView) inflatedView.findViewById(R.id.suggestionFromClientEndDateTicTimerIntroText);
            placeholderForTicTimerAskedFor.setVisibility(View.VISIBLE);

            // generate end date
            String tmpResponseDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy");;
            String tmpResponseTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm");
            String tmpDateAndTimeForResponse = String.format(context.getResources().getString(R.string.suggestionFromClientEndDateAndTimeText), tmpResponseDate, tmpResponseTime);
            TextView placeholderForTicTimerEndDateInText = (TextView) inflatedView.findViewById(R.id.suggestionFromClientEndDateTicTimerInText);
            placeholderForTicTimerEndDateInText.setText(tmpDateAndTimeForResponse);
            placeholderForTicTimerEndDateInText.setVisibility(View.VISIBLE);

            // show time until enddate is reach
            // calculate run time for timer in MILLISECONDS!!!
            Long nowTime = System.currentTimeMillis();
            Long runTimeForTimer = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)) - nowTime;
            // start the timer with the calculated milliseconds
            if (runTimeForTimer > 0) {
                new CountDownTimer(runTimeForTimer, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // gernate count down timer
                        String FORMAT = "%d Stunden %02d Minuten %02d Sekunden";
                        String tmpTime = String.format(FORMAT,
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                        // and set to textview
                        placeholderForTicTimer.setText(tmpTime);
                    }

                    public void onFinish() {
                        // change text to response time over
                        placeholderForTicTimer.setText(tmpTextResponseTimeOver);
                    }
                }.start();
            }


            // find send button "Terminvorschlaege senden"
            Button tmpSendButton = (Button) inflatedView.findViewById(R.id.buttonSendSuggestionFromClientToCoach);
            tmpSendButton.setVisibility(View.VISIBLE);

            // onClick listener make meeting
            tmpSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // get suggestion from client from inputfield
                    String suggestionFromClientText = "";
                    EditText txtInputSuggestionFromClient = (EditText) inflatedView.findViewById(R.id.inputSuggestionFromClientText);
                    suggestionFromClientText = txtInputSuggestionFromClient.getText().toString();


                    if (suggestionFromClientText.length() > 3) { // too few letters?

                        // canceled time
                        Long tmpSuggestionDate = System.currentTimeMillis();

                        // canceled status
                        int tmpStatus = 0; // not send to server

                        // insert  in DB
                        Long clientDbId = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
                        myDb.updateSuggestionFromClient(clientDbId, tmpSuggestionDate, prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"), suggestionFromClientText, tmpStatus);

                        // set successfull message in parent activity
                        //String tmpSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageMeetingCanceledMeetingByClientSuccessfullSend", "string", fragmentClientCanceledMeetingContext.getPackageName()));
                        //((ActivityMeeting) getActivity()).setSuccessefullMessageForSending (tmpSuccessfullMessage);

                        /*
                        // send intent to service to start the service and send vote suggestion to server!
                        Intent startServiceIntent = new Intent(meetingSuggestionOverviewCursorAdapterContext, ExchangeServiceEfb.class);
                        startServiceIntent.putExtra("com","send_suggestion_from_client_data");
                        startServiceIntent.putExtra("dbid",clientDbId);
                        meetingSuggestionOverviewCursorAdapterContext.startService(startServiceIntent);
                        */

                        // build intent to go back to suggestionFromClientOverview
                        Intent intent = new Intent(meetingSuggestionOverviewCursorAdapterContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com", "suggestion_from_client");
                        meetingSuggestionOverviewCursorAdapterContext.startActivity(intent);

                    }
                    else { // error too few suggestions!

                            TextView textViewToFewLettersInSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionFromClientAtLeastInfoText);
                            textViewToFewLettersInSuggestion.setVisibility(View.VISIBLE);

                    }
                }
            });






            
            
            
        }
        
        
        
        
        
        
        
        
        









        /*
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {
            actualMeetingHeadline = context.getResources().getString(R.string.meetingOverviewMeetingCanceledTitleAndNumberText);
        }
        textViewMeetingTitleAndNumber.setText(actualMeetingHeadline);
        */



        /*
        // check if meeting entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST)) == 1) {
            TextView newEntryOfMeeting = (TextView) inflatedView.findViewById(R.id.meetingNewMeetingText);
            String txtNewEntryOfMeeting = context.getResources().getString(R.string.newEntryText);
            newEntryOfMeeting.setText(txtNewEntryOfMeeting);

            // delete status new entry in db
            myDb.deleteStatusNewEntryMeetingAndSuggestion(cursor.getLong(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // textview for the author and date
        TextView tmpTextViewAuthorNameForMeeting = (TextView) inflatedView.findViewById(R.id.meetingAuthorAndDate);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR));
        String meetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "dd.MM.yyyy");;
        String meetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "HH:mm");;
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {

            String tmpCanceledAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR));
            String meetingCanceledDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "dd.MM.yyyy");;
            String meetingCanceledTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "HH:mm");;
            String tmpTextAuthorNameMeetingWithCancele = String.format(context.getResources().getString(R.string.meetingOverviewCreateMeetingAuthorAndDateWithCancele), tmpAuthorName, meetingDate, meetingTime, tmpCanceledAuthorName, meetingCanceledDate, meetingCanceledTime);
            tmpTextViewAuthorNameForMeeting.setText(Html.fromHtml(tmpTextAuthorNameMeetingWithCancele));

        } else {
            String tmpTextAuthorNameMeeting = String.format(context.getResources().getString(R.string.meetingOverviewCreateMeetingAuthorAndDate), tmpAuthorName, meetingDate, meetingTime);

            // check if meeting is canceled by client
            String tmpTextClientCanceledMeeting = "";
            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED)) == 1) {
                String tmpClientCanceledDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME)), "dd.MM.yyyy");
                String tmpClientCanceledTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME)), "HH:mm");
                tmpTextClientCanceledMeeting = String.format(context.getResources().getString(R.string.meetingOverviewClientCanceledMeetingAuthorAndDate), tmpClientCanceledDate, tmpClientCanceledTime);

                if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_MEETING_KEY_STATUS)) == 0) {
                    String tmpNotSendToServer = context.getResources().getString(R.string.meetingOverviewClientCanceledNotSendToServer);
                    tmpTextClientCanceledMeeting = tmpTextClientCanceledMeeting + " " + tmpNotSendToServer;
                }
            }
            tmpTextViewAuthorNameForMeeting.setText(Html.fromHtml(tmpTextAuthorNameMeeting+ " " + tmpTextClientCanceledMeeting));
        }

        // textview for meeting date
        TextView tmpTextViewMeetingDate = (TextView) inflatedView.findViewById(R.id.meetingDate);
        String tmpMeetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy");;
        tmpTextViewMeetingDate.setText(tmpMeetingDate);


        // textview for meeting time
        TextView tmpTextViewMeetingTime = (TextView) inflatedView.findViewById(R.id.meetingTime);
        String tmpMeetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm");;
        tmpTextViewMeetingTime.setText(tmpMeetingTime);

        // textview for meeting place
        TextView tmpTextViewMeetingPlace = (TextView) inflatedView.findViewById(R.id.meetingPlace);
        String tmpMeetingPlace = meetingPlaceNames[cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))];
        tmpTextViewMeetingPlace.setText(tmpMeetingPlace);

        // textview for meeting hint text
        TextView tmpTextViewMeetingHintText = (TextView) inflatedView.findViewById(R.id.meetingHintText);
        String tmpMeetingHintText = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT));
        tmpTextViewMeetingHintText.setText(tmpMeetingHintText);

        if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCanceleMeeting_OnOff, false) && cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED)) == 0 && cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 0) { // show cancele link for meeting

            TextView tmpTextViewClientCanceledLink = (TextView) inflatedView.findViewById(R.id.meetingCanceleLink);

            final Uri.Builder meetingClientCanceleLinkBuilder = new Uri.Builder();
            meetingClientCanceleLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("meeting")
                    .appendQueryParameter("meeting_id", Long.toString(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("com", "meeting_client_canceled");

            String tmpLinkClientCanceledMeeting = context.getResources().getString(context.getResources().getIdentifier("meetingOverviewClientCanceledLinkText", "string", context.getPackageName()));

            // generate link for output
            Spanned tmpMeetingCanceledLink = Html.fromHtml("<a href=\"" + meetingClientCanceleLinkBuilder.build().toString() + "\">" + tmpLinkClientCanceledMeeting + "</a>");

            // and set to textview
            tmpTextViewClientCanceledLink.setVisibility(View.VISIBLE);
            tmpTextViewClientCanceledLink.setText(tmpMeetingCanceledLink);
            tmpTextViewClientCanceledLink.setMovementMethod(LinkMovementMethod.getInstance());
        }

        // meeting is canceled by coach -> show hint
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter. MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {
            TextView tmpTextViewCanceledByCoach = (TextView) inflatedView.findViewById(R.id.meetingCanceledByCoachTextView);
            tmpTextViewCanceledByCoach.setVisibility(View.VISIBLE);

            TextView tmpTextViewDeleteCanceledMeetingLink = (TextView) inflatedView.findViewById(R.id.meetingDeleteCanceledMetingLink);
            // generate delete link for client -> delete meeting in db!
            final Uri.Builder meetingDeleteCanceledMeetingLink = new Uri.Builder();
            meetingDeleteCanceledMeetingLink.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("meeting")
                    .appendQueryParameter("meeting_id", Long.toString(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("com", "delete_canceled_meeting_by_client");

            String tmpLinkDeleteCanceledMeeting = context.getResources().getString(context.getResources().getIdentifier("meetingOverviewDeleteCanceledMeetingLinkText", "string", context.getPackageName()));

            // generate link for output
            Spanned tmpMeetingDeleteCanceledLink = Html.fromHtml("<a href=\"" + meetingDeleteCanceledMeetingLink.build().toString() + "\">" + tmpLinkDeleteCanceledMeeting + "</a>");

            tmpTextViewDeleteCanceledMeetingLink.setVisibility(View.VISIBLE);
            tmpTextViewDeleteCanceledMeetingLink.setText(tmpMeetingDeleteCanceledLink);
            tmpTextViewDeleteCanceledMeetingLink.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED)) == 1) {
            LinearLayout tmpLinearLayoutHintMeetingIsCanceled = (LinearLayout) inflatedView.findViewById(R.id.meetingCanceleByClientHint);
            tmpLinearLayoutHintMeetingIsCanceled.setVisibility(View.VISIBLE);
        }

        */


        return inflatedView;

    }



    // Turn off view recycling in listview, because there are different views (first, normal)
    // getViewTypeCount(), getItemViewType
    @Override
    public int getViewTypeCount () {

        return getCount();
    }

    @Override
    public int getItemViewType (int position) {

        return position;
    }

}
