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
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 01.07.16.
 */
public class OurArrangementFragmentNowComment extends Fragment {

    // fragment view
    View viewFragmentNowComment;

    // fragment context
    Context fragmentNowCommentContext = null;

    // the fragment
    Fragment fragmentNowCommentThisFragmentContext;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // Server DB-Id of arrangement to comment (the server id of arrangement is anchor)
    int arrangementServerDbIdToComment = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

    // cursor for the choosen arrangement
    Cursor cursorChoosenArrangement;

    // cursor for all comments to the choosen arrangement
    Cursor cursorArrangementAllComments;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentNowComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_now_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentNowCommentBrodcastReceiver, filter);

        return viewFragmentNowComment;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentNowCommentContext = getActivity().getApplicationContext();

        fragmentNowCommentThisFragmentContext = this;

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init the fragment now only when an arrangement is choosen
        if (arrangementServerDbIdToComment != 0) {
            initFragmentNowComment();
            buildFragmentNowCommentView();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentNowCommentContext, ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            fragmentNowCommentContext.startService(startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentNowCommentBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourArrangementFragmentNowCommentBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                Boolean refreshView = false;

                String tmpExtraOurArrangement = intentExtras.getString("OurArrangement","0");
                String tmpExtraOurArrangementNow = intentExtras.getString("OurArrangementNow","0");
                String tmpExtraOurArrangementNowComment = intentExtras.getString("OurArrangementNowComment","0");
                String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");
                String tmpExtraOurArrangementCommentShareEnable = intentExtras.getString("OurArrangementSettingsCommentShareEnable","0");
                String tmpExtraOurArrangementCommentShareDisable = intentExtras.getString("OurArrangementSettingsCommentShareDisable","0");
                String tmpExtraOurArrangementResetCommentCountComment = intentExtras.getString("OurArrangementSettingsCommentCountComment","0");
                String tmpExtraOurArrangementCommentSendInBackgroundRefreshView = intentExtras.getString("OurArrangementCommentSendInBackgroundRefreshView","0");

                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentNowCommentContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNowComment != null && tmpExtraOurArrangementNowComment.equals("1")) {
                    // update now comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentNowCommentContext.getString(R.string.toastMessageCommentNowNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNow != null && tmpExtraOurArrangementNow.equals("1")) {
                    // update now arrangement! -> go back to fragment now arrangement and show dialog

                    // check arrangement and now arrangement update and show dialog arrangement and now arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("now");

                    // go back to fragment now arrangement -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurArrangement.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_arrangement_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetCommentCountComment != null && tmpExtraOurArrangementResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentNowCommentContext.getString(R.string.toastMessageArrangementResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareDisable  != null && tmpExtraOurArrangementCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentNowCommentContext.getString(R.string.toastMessageArrangementCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareEnable  != null && tmpExtraOurArrangementCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentNowCommentContext.getString(R.string.toastMessageArrangementCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings has change -> refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangementCommentSendInBackgroundRefreshView != null &&  tmpExtraOurArrangementCommentSendInBackgroundRefreshView.equals("1")) {
                    // comment send in background -> refresh view
                    refreshView = true;
                }

                if (refreshView) {
                    refreshFragmentView ();
                }
            }
        }
    };


    // refresh the fragments view
    private void refreshFragmentView () {
        // refresh fragments view
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(fragmentNowCommentThisFragmentContext).attach(fragmentNowCommentThisFragmentContext).commit();
    }


    // inits the fragment for use
    private void initFragmentNowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentNowCommentContext);

        // init the prefs
        prefs = fragmentNowCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentNowCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementServerDbIdToComment);

        // get all comments for choosen arrangement
        cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementServerDbIdToComment, "descending");

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentNowCommentText", "string", fragmentNowCommentContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "nowComment");
    }


    // build the view for the fragment
    private void buildFragmentNowCommentView () {

        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementCommentNumberIntro);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showArrangementIntroText) + " " + arrangementNumberInListView);

        // textview for the author of arrangement
        TextView tmpTextViewAuthorNameText = (TextView) viewFragmentNowComment.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentNowCommentContext.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

        // textview for the arrangement
        TextView textViewArrangement = (TextView) viewFragmentNowComment.findViewById(R.id.choosenArrangement);
        String arrangement = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);

        // generate back link "zurueck zu allen Absprachen"
        Uri.Builder backArrangementLinkBuilder = new Uri.Builder();
        backArrangementLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourarrangement")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_arrangement_now");
        TextView linkShowCommentBackLink = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + backArrangementLinkBuilder.build().toString() + "\">"+fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementBackLinkToArrangementFromComment", "string", fragmentNowCommentContext.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

        // check, sharing comments enable?
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 0) {
            TextView textCommentSharingIsDisable = (TextView) viewFragmentNowComment.findViewById(R.id.commentSharingIsDisable);
            textCommentSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // some comments for arrangement available?
        if (cursorArrangementAllComments.getCount() > 0) {

            // set row id of comment from db for timer update
            final Long rowIdForUpdate = cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID));

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

            String commentDate = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME)), "HH:mm");
            String tmpTextAuthorNameLastActualComment = String.format(fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            if (cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
            tmpTextViewAuthorNameLastActualComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualComment = (TextView) viewFragmentNowComment.findViewById(R.id.textSendInfoLastActualComment);
            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 0) {

                String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendInfo);
                tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);

            } else if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 1) { // textview for status 1 of the last actual comment

                // check, sharing of comments enable and timer for comment possible, not finish?
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 1) {
                    // check system time is in past or future?
                    Long writeTimeComment = cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME)); // write time is from sever
                    Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                    Long maxTimerTime = writeTimeComment+delayTime;
                    if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_TIMER_STATUS)) == 0) { // check system time is in past and timer status is run!
                        // calculate run time for timer in MILLISECONDS!!!
                        Long nowTime = System.currentTimeMillis();
                        Long localeTimeComment = cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME));
                        Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                        // set textview visible
                        tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);

                        // start the timer with the calculated milliseconds
                        if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
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
                                    // count down is over -> show send successfull
                                    String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                                    tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                                    myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                }
                            }.start();

                        } else {
                            // no count down anymore -> show send successfull
                            String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                            tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else {
                        // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                        tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                        String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }
                else { // sharing of comments is disable! -> show text
                    String tmpTextSendInfoLastActualComment;
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
                    myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
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
        String tmpInfoTextDelaytimeSingluarPluaral;
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

                    String commentText = txtInputArrangementComment.getText().toString();
                    String userName = prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt");
                    Long commentTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                    if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                        commentTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                    }
                    Long uploadTime = 0L;
                    Long localeTime = System.currentTimeMillis();
                    String blockId = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_BLOCK_ID));
                    Boolean newEntry = false;
                    Long dateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());
                    int commentStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                    int arrangementServerId = cursorChoosenArrangement.getInt(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID));
                    int timerStatus = 0;

                    // insert comment in DB
                    Long tmpDbId = myDb.insertRowOurArrangementComment(commentText, userName , commentTime, uploadTime, localeTime, blockId, newEntry, dateOfArrangement, commentStatus, arrangementServerId, timerStatus);

                    // increment comment count
                    int countCommentSum = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) + 1;
                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, countCommentSum);
                    prefsEditor.apply();

                    // send intent to service to start the service and send comment to server!
                    Intent startServiceIntent = new Intent(fragmentNowCommentContext, ExchangeServiceEfb.class);
                    startServiceIntent.putExtra("com","send_now_comment_arrangement");
                    startServiceIntent.putExtra("dbid",tmpDbId);
                    startServiceIntent.putExtra("receiverBroadcast","");
                    fragmentNowCommentContext.startService(startServiceIntent);

                    // build intent to get back to OurArrangementFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com", "show_arrangement_now");
                    intent.putExtra("db_id", 0);
                    intent.putExtra("arr_num", 0);
                    intent.putExtra("eval_next", false);
                    getActivity().startActivity(intent);

                }
                else {
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
                intent.putExtra("db_id", 0);
                intent.putExtra("arr_num", 0);
                getActivity().startActivity(intent);

            }
        });
    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmparrangementServerDbIdToComment;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmparrangementServerDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();

        if (tmparrangementServerDbIdToComment > 0) {
            arrangementServerDbIdToComment = tmparrangementServerDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
            if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("current");
        }
    }

}