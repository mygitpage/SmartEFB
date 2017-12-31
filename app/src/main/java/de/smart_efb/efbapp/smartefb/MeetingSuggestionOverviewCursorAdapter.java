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
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 22.11.2017.
 */

public class MeetingSuggestionOverviewCursorAdapter extends CursorAdapter {

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    private Context meetingSuggestionOverviewCursorAdapterContext;

    // reference to the DB
    private DBAdapter myDb;

    // for prefs
    private SharedPreferences prefs;

    // max number of simultaneous meeting checkboxes
    private static final int maxSimultaneousMeetingCheckBoxes = ConstansClassMeeting.maxNumbersOfSuggestion;

    // int array for checkbox values (DbId)
    private int [] checkBoxSuggestionsValues = new int [maxSimultaneousMeetingCheckBoxes+1];

    // array of meeting places names (only 3 possible-> 0=nothing; 1=Werder(Havel); 2=Bad Belzig)
    private String  meetingPlaceNames[] = new String[3];

    // number of min votes for suggestion
    private int minNumberOfVotes = 1;
    
    // init view elements in array
    private int [] linearLayoutSuggestionHolder = new int[] {R.id.listSuggestionLine1, R.id.listSuggestionLine2, R.id.listSuggestionLine3, R.id.listSuggestionLine4, R.id.listSuggestionLine5, R.id.listSuggestionLine6 };
    private int[] textViewSuggestionDateAndTime = new int[] {R.id.listActualDateAndTimeSuggestion1, R.id.listActualDateAndTimeSuggestion2, R.id.listActualDateAndTimeSuggestion3, R.id.listActualDateAndTimeSuggestion4, R.id.listActualDateAndTimeSuggestion5, R.id.listActualDateAndTimeSuggestion6 };
    private int[] textViewSuggestionPlace = new int[] {R.id.listActualPlaceSuggestion1, R.id.listActualPlaceSuggestion2, R.id.listActualPlaceSuggestion3, R.id.listActualPlaceSuggestion4, R.id.listActualPlaceSuggestion5, R.id.listActualPlaceSuggestion6 };
    private String [] nameDbColumNamePlace = new String[ConstansClassMeeting.maxNumbersOfSuggestion];
    private String [] nameDbColumNameDate = new String[ConstansClassMeeting.maxNumbersOfSuggestion];
    private String []  nameDbColumNameVote = new String[ConstansClassMeeting.maxNumbersOfSuggestion];
    private int [] checkboxVoteSuggestion = new int[] {R.id.suggestionCheck1, R.id.suggestionCheck2, R.id.suggestionCheck3, R.id.suggestionCheck4, R.id.suggestionCheck5, R.id.suggestionCheck6 };

    // count list view elements vote for
    private int countListViewElementVote = 0;

    // vote db id for send button
    Long clientVoteDbId;


    // constructor
    MeetingSuggestionOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewCursorAdapterContext = context;

        // init the DB
        myDb = new DBAdapter(context);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // init meeting plce array
        meetingPlaceNames = context.getResources().getStringArray(R.array.placesNameForMeetingArray);

        // init checkbox value array
        for (int i=0; i<maxSimultaneousMeetingCheckBoxes+1; i++) {
            checkBoxSuggestionsValues[i] = 2; // 0=checkbox not selected; 1=checkbox selected; 2=checkbox not present, not in view)
        }

        // init db colum names for use in view
        nameDbColumNameDate[0] = DBAdapter.MEETING_SUGGESTION_KEY_DATE1;
        nameDbColumNameDate[1] = DBAdapter.MEETING_SUGGESTION_KEY_DATE2;
        nameDbColumNameDate[2] = DBAdapter.MEETING_SUGGESTION_KEY_DATE3;
        nameDbColumNameDate[3] = DBAdapter.MEETING_SUGGESTION_KEY_DATE4;
        nameDbColumNameDate[4] = DBAdapter.MEETING_SUGGESTION_KEY_DATE5;
        nameDbColumNameDate[5] = DBAdapter.MEETING_SUGGESTION_KEY_DATE6;

        nameDbColumNamePlace[0] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE1;
        nameDbColumNamePlace[1] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE2;
        nameDbColumNamePlace[2] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE3;
        nameDbColumNamePlace[3] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE4;
        nameDbColumNamePlace[4] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE5;
        nameDbColumNamePlace[5] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE6;

        nameDbColumNameVote[0] = DBAdapter.MEETING_SUGGESTION_KEY_VOTE1;
        nameDbColumNameVote[1] = DBAdapter.MEETING_SUGGESTION_KEY_VOTE2;
        nameDbColumNameVote[2] = DBAdapter.MEETING_SUGGESTION_KEY_VOTE3;
        nameDbColumNameVote[3] = DBAdapter.MEETING_SUGGESTION_KEY_VOTE4;
        nameDbColumNameVote[4] = DBAdapter.MEETING_SUGGESTION_KEY_VOTE5;
        nameDbColumNameVote[5] = DBAdapter.MEETING_SUGGESTION_KEY_VOTE6;
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        if (cursor.isFirst() ) { // look for button in first element and normal suggestion
            //set border between suggestion to invisible -> it is first suggestion
            TextView tmpBorderBetween = (TextView) view.findViewById(R.id.borderBetweenMeetingSuggestion);
            tmpBorderBetween.setVisibility(View.GONE);
        }
    }


    @Override
    public View newView(Context mContext, Cursor mCursor, ViewGroup parent) {

        final View inflatedView;

        Boolean tmpStatusSuggestionCanceled = false;
        Boolean tmpStatusVoteSuggestion = false;
        Boolean tmpStatusMeetingFoundFromSuggestion = false;
        Boolean tmpStatusSuggestion = false;

        final Context context = mContext;

        final Cursor cursor = mCursor;

        inflatedView = cursorInflater.inflate(R.layout.list_meeting_suggestion_overview_normal, parent, false);


        // canceled suggestion
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {
            tmpStatusSuggestionCanceled = true;

        } else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND)) == 1) {
            // check for meeting found from suggestion
            tmpStatusMeetingFoundFromSuggestion = true;
        } else if ((cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTEAUTHOR)).length() > 0 && cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTEDATE)) > 0) || (cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_AUTHOR)).length() > 0 && cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_DATE)) > 0)) { // suggestion vote or comment send
            // check for vote (vote and/or comment
            tmpStatusVoteSuggestion = true;
        } else if (cursor.isFirst()) {
            // check for normal suggestion
            tmpStatusSuggestion = true;
            // init vote db id for button send
            clientVoteDbId = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
        }

        // check for normal suggestion -> set intro text to visible
        if (tmpStatusSuggestion) {
            TextView tmpIntroText = (TextView) inflatedView.findViewById(R.id.meetingIntroInfoText);
            tmpIntroText.setVisibility(View.VISIBLE);
        }

        // page title
        TextView tmpTextViewTitleForSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionTitleAndNumber);
        // set info text
        String tmpTitleForSuggestion = "";
        TextView tmpTextViewAuthorNameForSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionAuthorAndDate);
        if (tmpStatusSuggestion) { // for normal suggestion
            String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR));
            String meetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "dd.MM.yyyy");
            String meetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "HH:mm");

            // set info text
            String tmpTextAuthorNameMeeting = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionAuthorAndDate), tmpAuthorName, meetingDate, meetingTime);
            tmpTextViewAuthorNameForSuggestion.setText(Html.fromHtml(tmpTextAuthorNameMeeting));

            // set title "actual suggestion"
            tmpTitleForSuggestion = context.getResources().getString(R.string.suggestionOverviewActualSuggestionTitle);
            tmpTextViewTitleForSuggestion.setText(tmpTitleForSuggestion);
        }
        if (tmpStatusSuggestionCanceled) {
            // set info text with canceled info
            String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR));
            String meetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "dd.MM.yyyy");
            String meetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "HH:mm");

            String tmpCanceledAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR));
            String meetingCanceledDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "dd.MM.yyyy");
            String meetingCanceledTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "HH:mm");
            String tmpTextAuthorNameSuggestionWithCancele = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionAuthorAndDateWithCancele), tmpAuthorName, meetingDate, meetingTime, tmpCanceledAuthorName, meetingCanceledDate, meetingCanceledTime);
            tmpTextViewAuthorNameForSuggestion.setText(Html.fromHtml(tmpTextAuthorNameSuggestionWithCancele));

            // set title "suggestion canceled"
            tmpTitleForSuggestion = context.getResources().getString(R.string.suggestionOverviewCanceledSuggestionTitle);
            tmpTextViewTitleForSuggestion.setText(tmpTitleForSuggestion);

            TextView tmpTextViewBigHintSuggestionCanceled = (TextView) inflatedView.findViewById(R.id.suggestionCanceledByCoachTextView);
            tmpTextViewBigHintSuggestionCanceled.setVisibility(View.VISIBLE);
        }

        if (tmpStatusVoteSuggestion) { // vote already send
            // set info text with vote info
            String meetingVoteDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTEDATE)), "dd.MM.yyyy");
            String meetingVoteTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTEDATE)), "HH:mm");

            String tmpTextAuthorNameSuggestionWithVote;
            if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) {
                tmpTextAuthorNameSuggestionWithVote = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionAuthorAndDateVoteAndOrComment), meetingVoteDate, meetingVoteTime);
            }
            else {
                tmpTextAuthorNameSuggestionWithVote = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionAuthorAndDateVote), meetingVoteDate, meetingVoteTime);
            }
            tmpTextViewAuthorNameForSuggestion.setText(Html.fromHtml(tmpTextAuthorNameSuggestionWithVote));

            // set title "vote for suggestion send"
            tmpTitleForSuggestion = context.getResources().getString(R.string.suggestionOverviewVoteSuggestionTitle);
            tmpTextViewTitleForSuggestion.setText(tmpTitleForSuggestion);

            TextView tmpTextViewBigHintMeetingFound = (TextView) inflatedView.findViewById(R.id.meetingSuggestionAlreadyVoteTextView);
            tmpTextViewBigHintMeetingFound.setVisibility(View.VISIBLE);
        }

        if (tmpStatusMeetingFoundFromSuggestion) {
            // set info text with meeting found info
            String tmpFoundAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_AUTHOR));
            String meetingFoundDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE)), "dd.MM.yyyy");
            String meetingFoundTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_SUGGESTION_FOUND_DATE)), "HH:mm");
            String tmpTextAuthorNameMeetingFoundFromSuggestion = String.format(context.getResources().getString(R.string.meetingOverviewMeetingFoundFromSuggestionAuthorAndDate), tmpFoundAuthorName, meetingFoundDate, meetingFoundTime);
            tmpTextViewAuthorNameForSuggestion.setText(Html.fromHtml(tmpTextAuthorNameMeetingFoundFromSuggestion));

            // set title "meeting found"
            tmpTitleForSuggestion = context.getResources().getString(R.string.suggestionOverviewFoundMeetingFromSuggestionTitle);
            tmpTextViewTitleForSuggestion.setText(tmpTitleForSuggestion);

            TextView tmpTextViewBigHintMeetingFound = (TextView) inflatedView.findViewById(R.id.meetingFoundFromSuggestionByCoachTextView);
            tmpTextViewBigHintMeetingFound.setVisibility(View.VISIBLE);
        }

        // check if suggestion entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST)) == 1) {
            TextView newEntryOfMeeting = (TextView) inflatedView.findViewById(R.id.meetingNewSuggestionText);
            String txtNewEntryOfMeeting = context.getResources().getString(R.string.newEntryText);
            newEntryOfMeeting.setText(txtNewEntryOfMeeting);

            // delete status new entry in db
            myDb.deleteStatusNewEntryMeetingAndSuggestion(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // set "maxNumbersOfSuggestion" suggestion to view
        for (int t=1; t<ConstansClassMeeting.maxNumbersOfSuggestion; t++) {

            if (linearLayoutSuggestionHolder[t] > 0 && cursor.getLong(cursor.getColumnIndex(nameDbColumNameDate[t-1])) > 0) { // check LayoutHolder and date of suggestion > 0
                // linear layout for suggetion
                LinearLayout tmpSuggestionHolder = (LinearLayout) inflatedView.findViewById(linearLayoutSuggestionHolder[t-1]);
                tmpSuggestionHolder.setVisibility(View.VISIBLE);

                // text view for date
                TextView tmpTextViewMeetingDateAndTime = (TextView) inflatedView.findViewById(textViewSuggestionDateAndTime[t-1]);
                String tmpMeetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(nameDbColumNameDate[t-1])), "dd.MM.yyyy");
                String tmpMeetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(nameDbColumNameDate[t-1])), "HH:mm");
                String tmpDateAndTimeForSuggestion = String.format(context.getResources().getString(R.string.meetingOverviewSuggestiondateAndTimeText), tmpMeetingDate, tmpMeetingTime);
                tmpTextViewMeetingDateAndTime.setText(tmpDateAndTimeForSuggestion);

                // textview for meeting place
                TextView tmpTextViewMeetingPlace = (TextView) inflatedView.findViewById(textViewSuggestionPlace[t-1]);
                String tmpMeetingPlace = meetingPlaceNames[cursor.getInt(cursor.getColumnIndex(nameDbColumNamePlace[t-1]))];
                tmpTextViewMeetingPlace.setText(tmpMeetingPlace);

                // set checkbox selected array to not selected (the other are not present, set to 2)
                checkBoxSuggestionsValues[t] = 0;

                // find checkboxes to vote
                CheckBox tmpSuggestionCheckBox = (CheckBox) inflatedView.findViewById(checkboxVoteSuggestion[t-1]);

                //check if suggestions votes or canceled meeting or found meeting!
                if (tmpStatusMeetingFoundFromSuggestion || tmpStatusVoteSuggestion || tmpStatusSuggestionCanceled) {

                    // change of checkbox is not possible
                    tmpSuggestionCheckBox.setClickable(false);

                    if (cursor.getInt(cursor.getColumnIndex(nameDbColumNameVote[t-1])) == 1) {
                        tmpSuggestionCheckBox.setChecked(true);
                    } else {
                        tmpSuggestionCheckBox.setChecked(false);
                    }
                }
                else {
                    tmpSuggestionCheckBox.setOnClickListener(new onClickListenerCheckBoxSuggestionVote(t));
                }
            }
        }

        // set hint text for suggestion
        String tmpSuggestionHintText = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT));
        if (tmpSuggestionHintText.length() > 0) {
            TextView tmpTextViewSuggestionHintText = (TextView) inflatedView.findViewById(R.id.suggestionHintText);
            tmpTextViewSuggestionHintText.setText(tmpSuggestionHintText);
            tmpTextViewSuggestionHintText.setVisibility(View.VISIBLE);
        }

        // check for suggestion vote or found meeting or suggestion canceled -> hint send button
        if (tmpStatusMeetingFoundFromSuggestion || tmpStatusVoteSuggestion || tmpStatusSuggestionCanceled) {
            Button tmpButtonSendSuggestion = (Button) inflatedView.findViewById(R.id.buttonSendSuggestionToCoach);
            tmpButtonSendSuggestion.setVisibility(View.GONE);

            // check for suggestion comment text -> show text or hint no text available
            if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) {
                String tmpSuggestionCommentText = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_COMMENT_TEXT));
                TextView tmpTextViewSuggestionCommentText = (TextView) inflatedView.findViewById(R.id.suggestionCommentTextByClient);
                TextView tmpTextViewSuggestionCommentHeadline = (TextView) inflatedView.findViewById(R.id.suggestionCommentTextByClientHeadline);
                if (tmpSuggestionCommentText.length() > 0) {
                    tmpTextViewSuggestionCommentText.setText(tmpSuggestionCommentText);
                    tmpTextViewSuggestionCommentText.setVisibility(View.VISIBLE);
                    tmpTextViewSuggestionCommentHeadline.setVisibility(View.VISIBLE);
                }
                else {
                    String tmpSuggestionCommentTextNotAvailable = context.getResources().getString(R.string.meetingOverviewSuggestionNoCommentTextAvailable);
                    tmpTextViewSuggestionCommentText.setText(tmpSuggestionCommentTextNotAvailable);
                    tmpTextViewSuggestionCommentText.setVisibility(View.VISIBLE);
                    tmpTextViewSuggestionCommentHeadline.setVisibility(View.VISIBLE);
                }
            }

            // set link to delete suggestion entry, because already vote, canceled or meeting found from suggestion
            TextView tmpTextViewClientDeleteEntry = (TextView) inflatedView.findViewById(R.id.suggestionDeleteEntryLink);
            TextView tmpTextViewClientDeleteRegularyEntry = (TextView) inflatedView.findViewById(R.id.suggestionDeleteRegularyInfoText);

            final Uri.Builder meetingClientDeleteEntryLinkBuilder = new Uri.Builder();
            meetingClientDeleteEntryLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("meeting")
                    .appendQueryParameter("meeting_id", Long.toString(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("com", "client_delete_suggestion_entry");

            String tmpLinkClientDeleteEntry = context.getResources().getString(context.getResources().getIdentifier("suggestionOverviewLinkTextDeleteSuggestionEntry", "string", context.getPackageName()));

            // generate link for output
            Spanned tmpDeleteEntrydLink = Html.fromHtml("<a href=\"" + meetingClientDeleteEntryLinkBuilder.build().toString() + "\">" + tmpLinkClientDeleteEntry + "</a>");

            // and set to textview
            tmpTextViewClientDeleteEntry.setVisibility(View.VISIBLE);
            tmpTextViewClientDeleteEntry.setText(tmpDeleteEntrydLink);
            tmpTextViewClientDeleteEntry.setMovementMethod(LinkMovementMethod.getInstance());

            String tmpSuggestionDeleteRegularyInfoText = context.getResources().getString(context.getResources().getIdentifier("suggestionOverviewLinkNormalDeleteDate", "string", context.getPackageName()));
            String suggestionDeleteEntryDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy");
            String suggestionDeleteEntryTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm");
            tmpSuggestionDeleteRegularyInfoText = String.format(tmpSuggestionDeleteRegularyInfoText,suggestionDeleteEntryDate, suggestionDeleteEntryTime);
            tmpTextViewClientDeleteRegularyEntry.setVisibility(View.VISIBLE);
            tmpTextViewClientDeleteRegularyEntry.setText(tmpSuggestionDeleteRegularyInfoText);
        }

        // get text view for info text at least
        TextView tmpTextViewInfoTextAtLeast = (TextView) inflatedView.findViewById(R.id.suggestionAtLeastInfoText);
        String tmpAtLastMessage = "";
        // vote -> show info text at least
        if (tmpStatusVoteSuggestion) {
            if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) {
                tmpAtLastMessage = context.getResources().getString(R.string.meetingOverviewSuggestionVoteInfoOrSuggestionCommentTextAtLeast);
            }
            else {
                tmpAtLastMessage = context.getResources().getString(R.string.meetingOverviewSuggestionVoteInfoTextAtLeast);
            }
            tmpTextViewInfoTextAtLeast.setText(tmpAtLastMessage);
            tmpTextViewInfoTextAtLeast.setVisibility(View.VISIBLE);
        }

        // meeting found from suggestion -> show info text at least
        if (tmpStatusMeetingFoundFromSuggestion) {
            tmpAtLastMessage = context.getResources().getString(R.string.meetingOverviewSuggestionFoundMeetingInfoTextAtLeast);
            tmpTextViewInfoTextAtLeast.setText(tmpAtLastMessage);
            tmpTextViewInfoTextAtLeast.setVisibility(View.VISIBLE);
        }

        // suggestion is canceled by coach
        if (tmpStatusSuggestionCanceled) {
            tmpAtLastMessage = context.getResources().getString(R.string.meetingOverviewSuggestionCanceledInfoTextAtLeast);
            tmpTextViewInfoTextAtLeast.setText(tmpAtLastMessage);
            tmpTextViewInfoTextAtLeast.setVisibility(View.VISIBLE);
        }

        // count down timer for response time -> only normal suggestion
        if (tmpStatusSuggestion) {

            final String tmpTextResponseTimeOver = context.getResources().getString(R.string.suggestionOverviewResponseTimeIsOver);

            // get text view for timer place and set visible
            final TextView placeholderForTicTimer = (TextView) inflatedView.findViewById(R.id.suggestionResponseTicTimer);
            placeholderForTicTimer.setVisibility(View.VISIBLE);

            // set intro text timer to visible
            TextView placeholderForTicTimerAskedFor = (TextView) inflatedView.findViewById(R.id.suggestionResponseTicTimerIntroText);
            placeholderForTicTimerAskedFor.setVisibility(View.VISIBLE);

            // generate end date
            String tmpResponseDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy");;
            String tmpResponseTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm");
            String tmpDateAndTimeForResponse = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionResponseDateAndTimeText), tmpResponseDate, tmpResponseTime);
            TextView placeholderForTicTimerEndDateInText = (TextView) inflatedView.findViewById(R.id.suggestionResponseTicTimerEndDateInText);
            placeholderForTicTimerEndDateInText.setText(tmpDateAndTimeForResponse);
            placeholderForTicTimerEndDateInText.setVisibility(View.VISIBLE);

            // show time until next evaluation period
            // calculate run time for timer in MILLISECONDS!!!
            Long nowTime = System.currentTimeMillis();
            Long runTimeForTimer = cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)) - nowTime;
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
        }

        // check if comment suggestion is possible -> show comment input field
        if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false) && tmpStatusSuggestion) {

            // get max letters for edit text comment
            final int tmpMaxLength = ConstansClassMeeting.namePrefsSuggestionCommentMaxLetters;

            // get textView to count input letters and init it
            final TextView textViewCountLettersSuggestionCommentEditText = (TextView) inflatedView.findViewById(R.id.countLettersCommentSuggestionEditText);
            String tmpInfoTextCountLetters = context.getResources().getString(R.string.infoTextCountLettersForComment);
            tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
            textViewCountLettersSuggestionCommentEditText.setText(tmpInfoTextCountLetters);
            textViewCountLettersSuggestionCommentEditText.setVisibility(View.VISIBLE);

            // comment suggestion textfield
            final EditText txtInputSuggestionComment = (EditText) inflatedView.findViewById(R.id.inputSuggestionComment);

            // set text watcher to count letters in comment field
            final TextWatcher txtInputArrangementCommentTextWatcher = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String tmpInfoTextCountLetters =  context.getResources().getString(R.string.infoTextCountLettersForComment);
                    tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), tmpMaxLength);
                    textViewCountLettersSuggestionCommentEditText.setText(tmpInfoTextCountLetters);
                }
                public void afterTextChanged(Editable s) {
                }
            };

            // set edit text field to visible
            txtInputSuggestionComment.setVisibility(View.VISIBLE);

            // set text watcher to count input letters
            txtInputSuggestionComment.addTextChangedListener(txtInputArrangementCommentTextWatcher);

            // set input filter max length for comment field
            txtInputSuggestionComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

            // set border top and bottom of edittext field visible
            TextView tmpBorderBetweenCommentSuggestionTop = (TextView) inflatedView.findViewById(R.id.borderBetweenCommentSuggestionTop);
            tmpBorderBetweenCommentSuggestionTop.setVisibility(View.VISIBLE);
            TextView tmpBorderBetweenCommentSuggestionBottom = (TextView) inflatedView.findViewById(R.id.borderBetweenCommentSuggestionBottom);
            tmpBorderBetweenCommentSuggestionBottom.setVisibility(View.VISIBLE);
        }

        if (tmpStatusSuggestion ) { // look for button in first element and normal suggestion

            // find send button "verbindich senden"
            Button tmpSendButton = (Button) inflatedView.findViewById(R.id.buttonSendSuggestionToCoach);

            // check if comment suggestion is possible -> show comment input field
            String tmpSendButtonText;
            if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) {
                tmpSendButtonText =  context.getResources().getString(R.string.btnTextSendSuggestionAndCommentToCoach);
            }
            else {
                tmpSendButtonText =  context.getResources().getString(R.string.btnTextSendSuggestionToCoach);
            }
            tmpSendButton.setText(tmpSendButtonText);

            // onClick listener make meeting
            tmpSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // get comment from inputfield when on
                    String tmpClientCommentText = "";
                    if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) {
                        EditText txtInputSuggestionComment = (EditText) inflatedView.findViewById(R.id.inputSuggestionComment);
                        tmpClientCommentText = txtInputSuggestionComment.getText().toString();
                    }

                    if ((countListViewElementVote >= minNumberOfVotes || tmpClientCommentText.length() > 3) && clientVoteDbId != null && clientVoteDbId > 0) { // too little suggestions or to few comment letters AND vote db id  > 0?

                        // status of suggestion data -> 0= not send; 1=send; 4= external
                        int tmpStatus = 0; // not send to server

                        // set comment author and date
                        String tmpClientCommentAuthor = "";
                        Long tmpClientCommentDate = 0L;
                        if (tmpClientCommentText.length() > 3) {
                            tmpClientCommentAuthor = prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt");
                            tmpClientCommentDate = System.currentTimeMillis();
                        }

                        // set vote author and date
                        String tmpVoteAuthor = "";
                        Long tmpVoteDate = 0L;
                        if (countListViewElementVote >= minNumberOfVotes) {
                            tmpVoteDate = System.currentTimeMillis();
                            tmpVoteAuthor = prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt");
                        }

                        // insert  in DB
                        myDb.updateSuggestionVoteAndCommentByClient(checkBoxSuggestionsValues[1], checkBoxSuggestionsValues[2], checkBoxSuggestionsValues[3], checkBoxSuggestionsValues[4], checkBoxSuggestionsValues[5], checkBoxSuggestionsValues[6], tmpVoteDate, tmpVoteAuthor, tmpClientCommentAuthor, tmpClientCommentDate, tmpClientCommentText, clientVoteDbId, tmpStatus);

                        // send intent to service to start the service and send vote suggestion to server!
                        Intent startServiceIntent = new Intent(meetingSuggestionOverviewCursorAdapterContext, ExchangeServiceEfb.class);
                        startServiceIntent.putExtra("com","send_meeting_data");
                        startServiceIntent.putExtra("dbid",clientVoteDbId);
                        startServiceIntent.putExtra("receiverBroadcast", "meetingFragmentSuggestionOverview");
                        meetingSuggestionOverviewCursorAdapterContext.startService(startServiceIntent);
                    }
                    else { // error too little suggestions!

                        // show error message in view
                        if (countListViewElementVote < minNumberOfVotes) {
                            TextView textViewTooLittleSuggestionChoosen = (TextView) inflatedView.findViewById(R.id.suggestionErrorToFewSuggestionsChoosen);
                            textViewTooLittleSuggestionChoosen.setVisibility(View.VISIBLE);
                        }
                        if (tmpClientCommentText.length() < 4 && prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCommentSuggestion_OnOff, false)) {
                            TextView textViewToFewLettersInComment = (TextView) inflatedView.findViewById(R.id.errorInputSuggestionComment);
                            textViewToFewLettersInComment.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }

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


    // onClickListener for vote for timezone
    private class onClickListenerCheckBoxSuggestionVote implements View.OnClickListener {

        int listPosition = 0;

        private onClickListenerCheckBoxSuggestionVote (int tmpListPosition) {

            this.listPosition = tmpListPosition;
        }

        @Override
        public void onClick(View v) {

            // Is the checkBox checked?
            boolean checked = ((CheckBox) v).isChecked();

            if (checked) {
                checkBoxSuggestionsValues[listPosition] = 1;
                countListViewElementVote++; //inc count checkboxes
            }
            else {
                checkBoxSuggestionsValues[listPosition] = 0;
                countListViewElementVote--; //dec count checkboxes
            }
        }
    }

}
