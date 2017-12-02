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

        String tmpSubtitle = "";

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
        tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleClientCanceledMeeting", "string", fragmentClientCanceledMeetingContext.getPackageName()));
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

    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeServiceEfb
    private BroadcastReceiver meetingFragmentMeetingClientCanceledBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                String tmpExtraMeeting = intentExtras.getString("Meeting","0");
                String tmpExtraMeetingNewMeeting = intentExtras.getString("MeetingNewMeeting","0");
                String tmpExtraMeetingCanceledMeetingByCoach = intentExtras.getString("MeetingCanceledMeetingByCoach","0");
                String tmpCommand = intentExtras.getString("Command");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");

                if (tmpExtraMeeting != null && tmpExtraMeeting.equals("1") && tmpExtraMeetingNewMeeting != null && tmpExtraMeetingNewMeeting.equals("1")) {
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

                        // set successfull message in parent activity
                        String tmpSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageMeetingCanceledMeetingByClientSuccessfullSend", "string", fragmentClientCanceledMeetingContext.getPackageName()));
                        ((ActivityMeeting) getActivity()).setSuccessefullMessageForSending (tmpSuccessfullMessage);

                        // set not successfull message in parent activity
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



        /*
        // some comments for arrangement available?
        if (cursorArrangementAllComments.getCount() > 0) {

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualCommentText));

            // position one for comment cursor
            cursorArrangementAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                TextView newEntryOfComment = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentNewInfoText);
                String txtNewEntryOfComment = fragmentNowCommentContext.getResources().getString(R.string.newEntryText);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurArrangementComment(cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = (TextView) viewFragmentNowComment.findViewById(R.id.textAuthorNameLastActualComment);
            String tmpAuthorName = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME));

            if (tmpAuthorName.equals(prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"))) {
                tmpAuthorName = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentPersonalAuthorName);
            }

            String commentDate = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME)), "dd.MM.yyyy");;
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME)), "HH:mm");;
            String tmpTextAuthorNameLastActualComment = String.format(fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            tmpTextViewAuthorNameLastActualComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualComment = (TextView) viewFragmentNowComment.findViewById(R.id.textSendInfoLastActualComment);
            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 0) {

                String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendInfo);
                tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);

            } else if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 1) {
                // textview for status 1 of the last actual comment

                // check, sharing of comments enable?
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 1) {

                    // set textview visible
                    tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);

                    // calculate run time for timer in MILLISECONDS!!!
                    Long nowTime = System.currentTimeMillis();
                    Long writeTimeComment = cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
                    Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) * 60000; // make milliseconds from miutes
                    Long runTimeForTimer = delayTime - (nowTime - writeTimeComment);
                    // start the timer with the calculated milliseconds
                    if (runTimeForTimer > 0) {
                        new CountDownTimer(runTimeForTimer, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // gernate count down timer
                                String FORMAT = "%02d:%02d:%02d";
                                String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendDelayInfo);
                                String tmpTime = String.format(FORMAT,
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                // put count down to string
                                String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                // and show
                                tmpTextViewSendInfoLastActualComment.setText(tmpCountdownTimerString);
                            }

                            public void onFinish() {
                                // count down is over -> show
                                String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                            }
                        }.start();

                    } else {
                        // no count down anymore -> show send successfull
                        String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                    }
                }
                else { // sharing of comments is disable! -> show text
                    String tmpTextSendInfoLastActualComment = "";
                    tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                    if (prefs.getLong(ConstansClassOurArrangement.namePrefsArrangementCommentShareChangeTime, 0) < cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME))) {
                        // show send successfull, but no sharing
                        tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendInfoSharingDisable);
                    }
                    else {
                        // show send successfull
                        tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                    }
                    tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                }
            }

            // textview for the comment text
            TextView tmpTextViewCommentText = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentText);
            String tmpCommentText = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);

            // get textview for Link to Show all comments
            TextView tmpTextViewLInkToShowAllComment = (TextView) viewFragmentNowComment.findViewById(R.id.commentLInkToShowAllComments);

            // more than one comment available?
            if (cursorArrangementAllComments.getCount() > 1) {

                // generate link to show all comments
                Uri.Builder commentLinkBuilder = new Uri.Builder();
                commentLinkBuilder.scheme("smart.efb.deeplink")
                        .authority("linkin")
                        .path("ourarrangement")
                        .appendQueryParameter("db_id", Integer.toString(arrangementServerDbIdToComment))
                        .appendQueryParameter("arr_num", Integer.toString(arrangementNumberInListView))
                        .appendQueryParameter("com", "show_comment_for_arrangement");

                if (cursorArrangementAllComments.getCount() == 2) {
                    String tmpLinkStringShowAllComments = String.format(fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementCommentLinkToShowAllCommentsSingular", "string", fragmentNowCommentContext.getPackageName())),cursorArrangementAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllComment.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllComments + "</a>"));
                }
                else {
                    String tmpLinkStringShowAllComments = String.format(fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementCommentLinkToShowAllCommentsPlural", "string", fragmentNowCommentContext.getPackageName())),cursorArrangementAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllComment.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllComments + "</a>"));
                }
                tmpTextViewLInkToShowAllComment.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else {
                // no comment anymore
                String tmpLinkStringShowAllComments = fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementCommentLinkToShowAllCommentsNotAvailable", "string", fragmentNowCommentContext.getPackageName()));
                tmpTextViewLInkToShowAllComment.setText(tmpLinkStringShowAllComments);
            }
        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorArrangementAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = (TextView) viewFragmentNowComment.findViewById(R.id.textAuthorNameLastActualComment);
            tmpTextViewAuthorNameLastActualComment.setText(this.getResources().getString(R.string.lastActualCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewCommentText = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentText);
            tmpTextViewCommentText.setVisibility(View.GONE);
        }

        // textview for max comments, count comments and max letters
        TextView textViewMaxAndCount = (TextView) viewFragmentNowComment.findViewById(R.id.infoNowCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
        // build text element max comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextNowCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextNowCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
        }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentUnlimitedText);
        }

        // build text element count comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentCountZero);
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0));

        // build text element delay time
        String tmpInfoTextDelaytimeSingluarPluaral = "";
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) == 0) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentDelaytimeNoDelay);
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) == 1) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentDelaytimeSingular);
        }
        else {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentDelaytimePlural);
            tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0));

        }

        // generate text comment max letters
        tmpInfoTextCommentMaxLetters =  this.getResources().getString(R.string.infoTextNowCommentCommentMaxLettersAndDelaytime);
        tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, 0));

        // show info text
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " +tmpInfoTextDelaytimeSingluarPluaral);


        // get max letters for edit text comment
        final int tmpMaxLength = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, 10);

        // get textView to count input letters and init it
        final TextView textViewCountLettersCommentEditText = (TextView) viewFragmentNowComment.findViewById(R.id.countLettersCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment textfield -> set hint text
        final EditText txtInputArrangementComment = (EditText) viewFragmentNowComment.findViewById(R.id.inputArrangementComment);

        // set text watcher to count letters in comment field
        final TextWatcher txtInputArrangementCommentTextWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
                String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
                tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), tmpMaxLength);
                textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);
            }
            public void afterTextChanged(Editable s) {
            }
        };

        // set text watcher to count input letters
        txtInputArrangementComment.addTextChangedListener(txtInputArrangementCommentTextWatcher);

        // set input filter max length for comment field
        txtInputArrangementComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

        // get button send comment
        Button buttonSendArrangementComment = (Button) viewFragmentNowComment.findViewById(R.id.buttonSendArrangementComment);

        // set onClick listener send arrangement comment
        buttonSendArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtInputArrangementComment.getText().toString().length() > 3) {

                    // insert comment in DB
                    Long tmpDbId = myDb.insertRowOurArrangementComment(txtInputArrangementComment.getText().toString(), prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"), System.currentTimeMillis(), 0, cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_BLOCK_ID)), true, prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), 0, cursorChoosenArrangement.getInt(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));

                    // increment comment count
                    int countCommentSum = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) + 1;
                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, countCommentSum);
                    prefsEditor.commit();

                    // send intent to service to start the service and send comment to server!
                    Intent startServiceIntent = new Intent(fragmentNowCommentContext, ExchangeServiceEfb.class);
                    startServiceIntent.putExtra("com","send_now_comment_arrangement");
                    startServiceIntent.putExtra("dbid",tmpDbId);
                    fragmentNowCommentContext.startService(startServiceIntent);

                    // build intent to get back to OurArrangementFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com", "show_arrangement_now");
                    getActivity().startActivity(intent);

                } else {

                    TextView tmpErrorTextView = (TextView) viewFragmentNowComment.findViewById(R.id.errorInputArrangementComment);
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                }

            }
        });

        // button abbort
        Button buttonAbbortArrangementComment = (Button) viewFragmentNowComment.findViewById(R.id.buttonAbortComment);
        // onClick listener button abbort
        buttonAbbortArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_arrangement_now");
                getActivity().startActivity(intent);

            }
        });



    */


    }















}
