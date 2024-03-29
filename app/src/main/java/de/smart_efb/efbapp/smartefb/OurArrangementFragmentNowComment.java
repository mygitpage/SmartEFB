package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    // fab view
    FloatingActionButton fabFragmentNowComment = null;

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
    Cursor cursorChooseArrangement;

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
            Intent startServiceIntent = new Intent(fragmentNowCommentContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentNowCommentContext, startServiceIntent);
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


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourArrangementFragmentNowCommentBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras;

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
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareDisable  != null && tmpExtraOurArrangementCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentNowCommentContext.getString(R.string.toastMessageArrangementCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareEnable  != null && tmpExtraOurArrangementCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentNowCommentContext.getString(R.string.toastMessageArrangementCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
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

        // hide fab
        // show fab and set on click listener
        if (fabFragmentNowComment != null) {
            fabFragmentNowComment.hide();
        }

        // get choosen arrangement
        cursorChooseArrangement = myDb.getRowOurArrangement(arrangementServerDbIdToComment);

        // get all comments for choose arrangement
        cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementServerDbIdToComment, "descending", 0);

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentNowCommentText", "string", fragmentNowCommentContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "nowComment");

        // set visibility of FAB for this fragment
        ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "nowComment");
    }


    // build the view for the fragment
    private void buildFragmentNowCommentView () {

        //textview for the comment intro
        TextView textCommentNumberIntro = viewFragmentNowComment.findViewById(R.id.arrangementCommentNumberIntro);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showArrangementIntroText) + " " + arrangementNumberInListView);

        // textview for the author of arrangement
        TextView tmpTextViewAuthorNameText = viewFragmentNowComment.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentNowCommentContext.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), cursorChooseArrangement.getString(cursorChooseArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // textview for the arrangement
        TextView textViewArrangement = viewFragmentNowComment.findViewById(R.id.chooseArrangement);
        String arrangement = cursorChooseArrangement.getString(cursorChooseArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);

        // check, sharing comments enable?
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 0) {
            TextView textCommentSharingIsDisable = viewFragmentNowComment.findViewById(R.id.commentSharingIsDisable);
            textCommentSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // some comments for arrangement available?
        if (cursorArrangementAllComments.getCount() > 0) {

            // set row id of comment from db for timer update
            final Long rowIdForUpdate = cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID));

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = viewFragmentNowComment.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualCommentText));

            // position one for comment cursor
            cursorArrangementAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                TextView newEntryOfComment = viewFragmentNowComment.findViewById(R.id.lastActualCommentNewInfoText);
                String txtNewEntryOfComment = fragmentNowCommentContext.getResources().getString(R.string.newEntryText);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurArrangementComment(cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = viewFragmentNowComment.findViewById(R.id.textAuthorNameLastActualComment);
            String tmpAuthorName = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME));

            if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                tmpAuthorName = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentPersonalAuthorName);
            }

            String commentDate = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME)), "HH:mm");
            String tmpTextAuthorNameLastActualComment = String.format(fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            if (cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
            tmpTextViewAuthorNameLastActualComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualComment = viewFragmentNowComment.findViewById(R.id.textSendInfoLastActualComment);
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
            TextView tmpTextViewCommentText = viewFragmentNowComment.findViewById(R.id.lastActualCommentText);
            String tmpCommentText = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);

        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = viewFragmentNowComment.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorArrangementAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = viewFragmentNowComment.findViewById(R.id.textAuthorNameLastActualComment);
            tmpTextViewAuthorNameLastActualComment.setText(this.getResources().getString(R.string.lastActualCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewCommentText = viewFragmentNowComment.findViewById(R.id.lastActualCommentText);
            tmpTextViewCommentText.setVisibility(View.GONE);

            // button for overview comment
            Button tmpButtonShowOverviewComment = viewFragmentNowComment.findViewById(R.id.buttonNowCommentBackToShowComment);
            tmpButtonShowOverviewComment.setVisibility(View.GONE);

            // textview border no comment and info text
            TextView tmpBorderBetweenNoCommentAndInfoText = viewFragmentNowComment.findViewById(R.id.borderBetweenNoCommentInfoAndInfoText);
            tmpBorderBetweenNoCommentAndInfoText.setVisibility(View.VISIBLE);

        }

        // textview for max comments, count comments and max letters
        TextView textViewMaxAndCount = viewFragmentNowComment.findViewById(R.id.infoNowCommentMaxAndCount);
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
        final TextView textViewCountLettersCommentEditText = viewFragmentNowComment.findViewById(R.id.countLettersCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment textfield
        final EditText txtInputArrangementComment = viewFragmentNowComment.findViewById(R.id.inputArrangementComment);

        // set hint text in edit text field
        String tmpHintTextForCommentField = this.getResources().getString(R.string.arrangementCommentHintText);
        tmpHintTextForCommentField = String.format(tmpHintTextForCommentField, arrangementNumberInListView);
        txtInputArrangementComment.setHint(tmpHintTextForCommentField);


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

        // set text max comment/ actual comment
        TextView infoTextCountComment = viewFragmentNowComment.findViewById(R.id.infoTextCountComment);
        String tmpInfoTextCountCommentSingluarPluaralNoLimit;
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourArrangementNowCommentCountCommentTextSingular);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourArrangementNowCommentCountCommentTextPlural);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));

        }
        else {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourArrangementNowCommentCountCommentTextNoLimit);
        }
        infoTextCountComment.setText(tmpInfoTextCountCommentSingluarPluaralNoLimit);

        // get button send comment
        Button buttonSendArrangementComment = viewFragmentNowComment.findViewById(R.id.buttonSendArrangementComment);

        // set onClick listener send arrangement comment
        buttonSendArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check case close
                if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

                    if (txtInputArrangementComment.getText().toString().length() > 3) {

                        String commentText = txtInputArrangementComment.getText().toString();
                        String userName = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");
                        Long commentTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                        if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                            commentTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                        }
                        Long uploadTime = 0L;
                        Long localeTime = System.currentTimeMillis();
                        String blockId = cursorChooseArrangement.getString(cursorChooseArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_BLOCK_ID));
                        Boolean newEntry = false;
                        Long dateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());
                        int commentStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                        int arrangementServerId = cursorChooseArrangement.getInt(cursorChooseArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID));
                        int timerStatus = 0;

                        // insert comment in DB
                        Long tmpDbId = myDb.insertRowOurArrangementComment(commentText, userName, commentTime, uploadTime, localeTime, blockId, newEntry, dateOfArrangement, commentStatus, arrangementServerId, timerStatus);

                        // increment comment count
                        int countCommentSum = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) + 1;
                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, countCommentSum);
                        prefsEditor.apply();

                        // send intent to service to start the service
                        Intent startServiceIntent = new Intent(fragmentNowCommentContext, ExchangeJobIntentServiceEfb.class);
                        startServiceIntent.putExtra("com", "send_now_comment_arrangement");
                        startServiceIntent.putExtra("dbid", tmpDbId);
                        startServiceIntent.putExtra("receiverBroadcast", "");
                        // start service
                        ExchangeJobIntentServiceEfb.enqueueWork(fragmentNowCommentContext, startServiceIntent);

                        // build intent to get back to OurArrangementFragmentNow
                        Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com", "show_arrangement_now");
                        intent.putExtra("db_id", 0);
                        intent.putExtra("arr_num", 0);
                        intent.putExtra("eval_next", false);
                        getActivity().startActivity(intent);

                    } else {
                        TextView tmpErrorTextView = viewFragmentNowComment.findViewById(R.id.errorInputArrangementComment);
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    // delete text in edittextfield
                    txtInputArrangementComment.setText("");

                    // case is closed -> show toast
                    String textCaseClose = fragmentNowCommentContext.getString(R.string.toastOurArrangementCommentCaseCloseToastText);
                    Toast toast = Toast.makeText(fragmentNowCommentContext, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        // button back to arrangement overview
        Button buttonBackToArrangementOverview = viewFragmentNowComment.findViewById(R.id.buttonNowCommentBackToArrangement);
        // onClick listener back to arrangement overview
        buttonBackToArrangementOverview.setOnClickListener(new View.OnClickListener() {
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

        // button back to show comment overview for arrangement
        Button buttonBackToCommentOverviewForArrangement = viewFragmentNowComment.findViewById(R.id.buttonNowCommentBackToShowComment);
        // onClick listener back to comment overview for arrangement
        buttonBackToCommentOverviewForArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_comment_for_arrangement");
                intent.putExtra("db_id", arrangementServerDbIdToComment );
                intent.putExtra("arr_num", arrangementNumberInListView);
                getActivity().startActivity(intent);

            }
        });

    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementServerDbIdToComment;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementServerDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();

        // call getter-methode getFabViewOurArrangement() in ActivityOurArrangement to get view for fab
        fabFragmentNowComment = ((ActivityOurArrangement)getActivity()).getFabViewOurArrangement();

        if (tmpArrangementServerDbIdToComment > 0) {
            arrangementServerDbIdToComment = tmpArrangementServerDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
            if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("current");
        }
    }

}