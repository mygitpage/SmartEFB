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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 12.12.2017.
 */

public class MeetingSuggestionFromClientOverviewCursorAdapter extends CursorAdapter {

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingSuggestionOverviewCursorAdapterContext;

    // array of meeting places names (only 3 possible-> 0=nothing; 1=Werder(Havel); 2=Bad Belzig)
    String  meetingPlaceNames[] = new String[3];

    // for prefs
    SharedPreferences prefs;

    // count down timer for start of period
    CountDownTimer countDownEndTimeFormPeriod;

    
    // Own constructor
    public MeetingSuggestionFromClientOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewCursorAdapterContext = context;

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

        // init the DB
        final DBAdapter myDb = new DBAdapter(mContext);

        final View inflatedView;

        final Context context = mContext;

        final Cursor cursor = mCursor;

        Long tmpNowTime = System.currentTimeMillis();

        Boolean tmpInTimezone = false;
        Boolean tmpOutPostTimezone = false;
        Boolean tmpOutPreTimezone = false;
        Boolean tmpSuggestionFromClientAlreadySend = false;
        Boolean tmpSuggestionFromClientCanceled = false;
        Boolean tmpSuggestionFromClientMeetingFound = false;

        // set row id of comment from db for timer update
        final Long rowIdForUpdate = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
        
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
        else if (cursor.isFirst() && cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_TIMER_STATUS)) == 0 && cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)) < tmpNowTime && cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)) > tmpNowTime) {
            // in timezone
            tmpInTimezone = true;
        }
        else if (cursor.isFirst() && cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_TIMER_STATUS)) == 0 && cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)) > tmpNowTime && cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)) > tmpNowTime) {
            // out timezone (befor start date)
            tmpOutPreTimezone = true;
        }
        else {
            // out timezone (after end date)
            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_TIMER_STATUS)) == 0) {
                myDb.updateTimerStatusMeetingSuggestion(rowIdForUpdate, 1); // timer status to 1 -> stop timer!
            }
            tmpOutPostTimezone = true;
        }

        // check if entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST)) == 1) {
            TextView newEntryOfClientSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionFromClientNewEntryText);
            String txtNewEntryOfMeeting = context.getResources().getString(R.string.newEntryText);
            newEntryOfClientSuggestion.setText(txtNewEntryOfMeeting);

            // delete status new entry in db
            myDb.deleteStatusNewEntryMeetingAndSuggestion(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // show intro text different in timezone/ out pre/post timezone
        if (cursor.isFirst() && (tmpInTimezone || tmpOutPreTimezone || tmpOutPostTimezone)) {

            // show intro text different in timezone/ out timezone
            String actualSuggestionFromClientHeadline = "";
            TextView textViewSuggestionFromClientIntroText = (TextView) inflatedView.findViewById(R.id.suggestionFromClientIntroInfoText);
            if (tmpInTimezone) {
                actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientIntroInfoTextInTimezone);
            }
            else if (tmpOutPreTimezone) {
                actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientIntroInfoTextOutPreTimezone);
            }
            else {
                actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientIntroInfoTextOutPostTimezone);
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
            String suggestionFromClientMeetingSendDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME)), "dd.MM.yyyy");
            String suggestionFromClientMeetingSendTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_LOCALE_TIME)), "HH:mm");
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextAlreadySend);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientAuthorAndDateAlreadySend), suggestionFromClientMeetingSendDate, suggestionFromClientMeetingSendTime);
        }
        else if (tmpInTimezone) { // suggestion from client in timezone
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextInTimezone);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientInTimezone), suggestionFromClientEndDate, suggestionFromClientEndTime, suggestionFromClientStartDate, suggestionFromClientStartTime, suggestionFromClientTimezoneAuthor, suggestionFromClientTimezoneCreationDate, suggestionFromClientTimezoneCreationTime);
        }
        else if (tmpOutPreTimezone) { // suggestion from client out pre timezone
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextOutPreTimezone);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientOutPreTimezone), suggestionFromClientStartDate, suggestionFromClientStartTime, suggestionFromClientEndDate, suggestionFromClientEndTime, suggestionFromClientTimezoneAuthor, suggestionFromClientTimezoneCreationDate, suggestionFromClientTimezoneCreationTime);
        }
        else {
            // this is out post timezone
            actualSuggestionFromClientHeadline = context.getResources().getString(R.string.meetingOverviewSuggestionFromClientTitleAndNumberTextOutPostTimezone);
            tmpTextAuthorAndDate = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionFromClientOutPostTimezone), suggestionFromClientStartDate, suggestionFromClientStartTime, suggestionFromClientEndDate, suggestionFromClientEndTime);
        }
        textViewSuggestionFromClientTitleAndNumber.setText(actualSuggestionFromClientHeadline);
        textViewSuggestionFromClientAuthorAndDate.setText(Html.fromHtml(tmpTextAuthorAndDate));

        // we are in canceled suggestion from client
        if (tmpSuggestionFromClientCanceled) { // suggestion from client canceled

            TextView textViewSuggestionFromClientInvitationCanceled = (TextView) inflatedView.findViewById(R.id.suggestionFromClientCanceledInvitationText);
            textViewSuggestionFromClientInvitationCanceled.setVisibility(View.VISIBLE);

            if (!cursor.isFirst()) { // show border when not first
                TextView textViewSuggestionFromClientInvitationCanceledBorder = (TextView) inflatedView.findViewById(R.id.suggestionFromClientBorderBetween);
                textViewSuggestionFromClientInvitationCanceledBorder.setVisibility(View.VISIBLE);
            }

            // show input suggestion from client
            String inputClientSuggestion = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT));
            if (inputClientSuggestion.length() > 0) {
                TextView suggestionFromClientShowInputSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionFromClientInputSuggestionText);
                suggestionFromClientShowInputSuggestion.setText(inputClientSuggestion);
                suggestionFromClientShowInputSuggestion.setVisibility(View.VISIBLE);
            }
        }

        // we are post out off timezone
        if (tmpOutPostTimezone) {
            TextView textViewSuggestionFromClientPostOutoffTimezone = (TextView) inflatedView.findViewById(R.id.suggestionFromClientPostOutOffTimezone);
            textViewSuggestionFromClientPostOutoffTimezone.setVisibility(View.VISIBLE);

            if (!cursor.isFirst()) { // show border when not first
                TextView textViewSuggestionFromClientInvitationCanceledBorder = (TextView) inflatedView.findViewById(R.id.suggestionFromClientBorderBetween);
                textViewSuggestionFromClientInvitationCanceledBorder.setVisibility(View.VISIBLE);
            }
        }


        // we are in meeting found from client suggestion
        if (tmpSuggestionFromClientMeetingFound) { // suggestion from client canceled

            TextView textViewSuggestionFromClientMeetingFound = (TextView) inflatedView.findViewById(R.id.suggestionFromClientMeetingFoundText);
            textViewSuggestionFromClientMeetingFound.setVisibility(View.VISIBLE);

            if (!cursor.isFirst()) { // show border when not first
                TextView textViewSuggestionFromClientInvitationCanceledBorder = (TextView) inflatedView.findViewById(R.id.suggestionFromClientBorderBetween);
                textViewSuggestionFromClientInvitationCanceledBorder.setVisibility(View.VISIBLE);
            }

            // show input suggestion from client
            String inputClientSuggestion = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT));
            if (inputClientSuggestion.length() > 0) {
                TextView suggestionFromClientShowInputSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionFromClientInputSuggestionText);
                suggestionFromClientShowInputSuggestion.setText(inputClientSuggestion);
                suggestionFromClientShowInputSuggestion.setVisibility(View.VISIBLE);
            }
        }

        // we are in meeting found from suggestion, in canceled suggestion or suggestion send or timezone post out
        if (tmpSuggestionFromClientCanceled || tmpSuggestionFromClientMeetingFound || tmpOutPostTimezone) {

            // set link to delete suggestion entry, because canceled or meeting found from suggestion
            TextView tmpTextViewClientDeleteEntry = (TextView) inflatedView.findViewById(R.id.suggestionFromClientDeleteEntryLink);

            final Uri.Builder meetingClientDeleteEntryLinkBuilder = new Uri.Builder();
            meetingClientDeleteEntryLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("meeting")
                    .appendQueryParameter("meeting_id", Long.toString(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("com", "delete_suggestion_from_client_entry");

            String tmpLinkClientDeleteEntry = context.getResources().getString(context.getResources().getIdentifier("suggestionFromClientLinkTextDeleteSuggestionEntry", "string", context.getPackageName()));

            // generate link for output
            Spanned tmpDeleteEntrydLink = Html.fromHtml("<a href=\"" + meetingClientDeleteEntryLinkBuilder.build().toString() + "\">" + tmpLinkClientDeleteEntry + "</a>");

            // and set to textview
            tmpTextViewClientDeleteEntry.setVisibility(View.VISIBLE);
            tmpTextViewClientDeleteEntry.setText(tmpDeleteEntrydLink);
            tmpTextViewClientDeleteEntry.setMovementMethod(LinkMovementMethod.getInstance());
        }

        // we are in timezone -> show input field
        if (tmpInTimezone) {

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
            String tmpAskForText = context.getResources().getString(R.string.suggestionFromClientEndDateTimeAskedForIntroText);
            placeholderForTicTimerAskedFor.setVisibility(View.VISIBLE);
            placeholderForTicTimerAskedFor.setText(tmpAskForText);

            // generate end date
            String tmpResponseDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy");;
            String tmpResponseTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm");
            String tmpDateAndTimeForResponse = String.format(context.getResources().getString(R.string.suggestionFromClientEndDateAndTimeText), tmpResponseDate, tmpResponseTime);
            TextView placeholderForTicTimerEndDateInText = (TextView) inflatedView.findViewById(R.id.suggestionFromClientEndDateTicTimerInText);
            placeholderForTicTimerEndDateInText.setText(tmpDateAndTimeForResponse);
            placeholderForTicTimerEndDateInText.setVisibility(View.VISIBLE);

            // show timer until startdate is reach
            Long nowTime = System.currentTimeMillis();
            Long tmpUploadTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME)); // local upload of suggestion from client
            Long tmpEndSuggestionFromClientTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE));
            Long tmpStartSuggestionFromClientTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE));
            int tmpTimerStatus = cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_TIMER_STATUS)); // =0 timer can run; =1 timer stop

            // check for timer borders
            if (nowTime >= tmpUploadTime && nowTime <= tmpEndSuggestionFromClientTime && nowTime > tmpStartSuggestionFromClientTime && nowTime > prefs.getLong(ConstansClassMeeting.namePrefsMeeting_LastStartPointSuggestionFromClientTimer, System.currentTimeMillis()) && tmpTimerStatus == 0) {
                // calculate run time for timer in MILLISECONDS!!!
                Long runTimeForTimer = tmpEndSuggestionFromClientTime - nowTime;
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
                            // set timer stop!
                            myDb.updateTimerStatusMeetingSuggestion(rowIdForUpdate, 1); // timer status to 1 -> stop timer!

                            // change text to response time over
                            placeholderForTicTimer.setText(tmpTextResponseTimeOver);

                            // refresh view
                            refreshSuggestionFromClientView(context);
                        }
                    }.start();
                }
            }
            else {
                // set timer stop!
                myDb.updateTimerStatusMeetingSuggestion(rowIdForUpdate, 1); // timer status to 1 -> stop timer!
                // refresh view
                refreshSuggestionFromClientView(context);
            }

            // hint text from coach?
            if (cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT)).length() > 0) {

                TextView textViewShowHintTextFromCoach = (TextView) inflatedView.findViewById(R.id.suggestionFromClientCoachHintText);
                textViewShowHintTextFromCoach.setText(cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT)));
                textViewShowHintTextFromCoach.setVisibility(View.VISIBLE);

                TextView textViewShowHintTextFromCoachBorder = (TextView) inflatedView.findViewById(R.id.suggestionFromClientCoachHintTextBorder);
                textViewShowHintTextFromCoachBorder.setVisibility(View.VISIBLE);
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

                    // check case close
                    if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

                        if (suggestionFromClientText.length() > 3 && rowIdForUpdate != null && rowIdForUpdate > 0) { // too few letters AND vote db id  > 0?

                            // suggestion locale time
                            Long tmpSuggestionFromClientDate = System.currentTimeMillis();

                            // get server time from locale time or last contact time
                            Long tmpSuggestionDate = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                            if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                                tmpSuggestionDate = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                            }

                            // status of suggestion data -> 0= not send; 1=send; 4= external
                            int tmpStatus = 0; // not send to server
                            // timer status -> timer not run =1, because client already vote
                            int timerStatus = 1;

                            // generate update order
                            String updateOrder = "update_suggestion_from_client_server_time";
                            // get user name
                            String tmpSuggestionFromClientAuthor = prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt");

                            // insert  in DB
                            myDb.updateSuggestionFromClient(rowIdForUpdate, tmpSuggestionDate, tmpSuggestionFromClientDate, tmpSuggestionFromClientAuthor, suggestionFromClientText, tmpStatus, timerStatus, updateOrder);

                            // send intent to service to start the service and send vote suggestion to server!
                            Intent startServiceIntent = new Intent(meetingSuggestionOverviewCursorAdapterContext, ExchangeServiceEfb.class);
                            startServiceIntent.putExtra("com", "send_meeting_data");
                            startServiceIntent.putExtra("dbid", rowIdForUpdate);
                            startServiceIntent.putExtra("receiverBroadcast", "meetingFragmentSuggestionFromClient");
                            meetingSuggestionOverviewCursorAdapterContext.startService(startServiceIntent);
                        }
                        else { // error too few suggestions!
                            TextView textViewErrorToFewLettersInClientSuggestion = (TextView) inflatedView.findViewById(R.id.errorInputSuggestionFromClient);
                            textViewErrorToFewLettersInClientSuggestion.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        // delete text in edittextfield
                        txtInputSuggestionFromClient.setText("");

                        // case is closed -> show toast
                        String textCaseClose = context.getString(R.string.toastMessageSuggestionFromClientCaseCloseToastText);
                        Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                        TextView viewMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                        if (v != null) viewMessage.setGravity(Gravity.CENTER);
                        toast.show();
                    }
                }
            });

            // show info text for suggestion input for user
            TextView textViewClientSuggestionInfoTextForUser = (TextView) inflatedView.findViewById(R.id.suggestionFromClientAtLeastInfoText);
            textViewClientSuggestionInfoTextForUser.setVisibility(View.VISIBLE);
        }

        // we are in pre timezone (out but pre) -> show count down timer
        if (tmpOutPreTimezone && cursor.isFirst()) {

            // get text view for timer place and set visible
            final TextView placeholderForTicTimer = (TextView) inflatedView.findViewById(R.id.suggestionFromClientEndDateTicTimer);
            placeholderForTicTimer.setVisibility(View.VISIBLE);

            // set intro text timer to visible
            TextView placeholderForTicTimerAskedFor = (TextView) inflatedView.findViewById(R.id.suggestionFromClientEndDateTicTimerIntroText);
            String tmpAskForText = context.getResources().getString(R.string.suggestionFromClientStartDateTimeAskedForIntroText);
            placeholderForTicTimerAskedFor.setVisibility(View.VISIBLE);
            placeholderForTicTimerAskedFor.setText(tmpAskForText);

            // generate start date
            String tmpStartDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy");;
            String tmpStartTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm");
            String tmpDateAndTimeForResponse = String.format(context.getResources().getString(R.string.suggestionFromClientStartDateAndTimeText), tmpStartDate, tmpStartTime);
            TextView placeholderForTicTimerEndStartInText = (TextView) inflatedView.findViewById(R.id.suggestionFromClientEndDateTicTimerInText);
            placeholderForTicTimerEndStartInText.setText(tmpDateAndTimeForResponse);
            placeholderForTicTimerEndStartInText.setVisibility(View.VISIBLE);

            // show timer until startdate is reach
            Long nowTime = System.currentTimeMillis();
            Long tmpUploadTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_UPLOAD_TIME)); // local upload of suggestion from client
            Long tmpEndSuggestionFromClientTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE));
            Long tmpStartSuggestionFromClientTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE));
            int tmpTimerStatus = cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_TIMER_STATUS)); // =0 timer can run; =1 timer stop

            // check for timer borders
            if (nowTime >= tmpUploadTime && nowTime <= tmpEndSuggestionFromClientTime && nowTime <= tmpStartSuggestionFromClientTime && nowTime > prefs.getLong(ConstansClassMeeting.namePrefsMeeting_LastStartPointSuggestionFromClientTimer, System.currentTimeMillis()) && tmpTimerStatus == 0) {
                // calculate run time for timer in MILLISECONDS!!!
                Long runTimeForTimer = tmpStartSuggestionFromClientTime - nowTime;
                // start the timer with the calculated milliseconds
                if (runTimeForTimer > 0) {
                    countDownEndTimeFormPeriod = new CountDownTimer(runTimeForTimer, 1000) {
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
                            // refresh the view
                            refreshSuggestionFromClientView (context);
                        }
                    }.start();
                }
            }
            else {
                String timerProblemText = context.getResources().getString(R.string.suggestionFromClientTimerProblemText);
                placeholderForTicTimer.setText(timerProblemText);

            }
        }

        // client suggestion allready send -> show client suggestion and hint text
        if (tmpSuggestionFromClientAlreadySend) {

            // show input suggestion from client
            TextView suggestionFromClientShowInputSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionFromClientInputSuggestionText);
            String inputClientSuggestion = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_TEXT));
            suggestionFromClientShowInputSuggestion.setText(inputClientSuggestion);
            suggestionFromClientShowInputSuggestion.setVisibility(View.VISIBLE);

            // show input suggestion hint text for process information
            TextView suggestionFromClientShowInputSuggestionHintText = (TextView) inflatedView.findViewById(R.id.suggestionFromClientInputSuggestionHintText);
            suggestionFromClientShowInputSuggestionHintText.setVisibility(View.VISIBLE);
        }

        // close Db connection
        myDb.close();

        return inflatedView;
    }


    // send broadcast to refresh the view
    private void refreshSuggestionFromClientView (Context context) {

        Intent tmpIntent = new Intent();
        tmpIntent.putExtra("SuggestionFromClientUpdateListView", "update");
        tmpIntent.setAction("ACTIVITY_STATUS_UPDATE");
        context.sendBroadcast(tmpIntent);
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
