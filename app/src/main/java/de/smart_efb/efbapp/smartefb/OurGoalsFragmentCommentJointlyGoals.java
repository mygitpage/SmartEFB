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
 * Created by ich on 24.10.2016.
 */
public class OurGoalsFragmentCommentJointlyGoals extends Fragment {

    // fragment view
    View viewFragmentCommentJointlyGoals;

    // fragment context
    Context fragmentCommentContextJointlyGoals = null;

    // the fragment
    Fragment fragmentCommentContextJointlyGoalsThisFragment;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // fab view
    FloatingActionButton fabFragmentJointlyComment = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment jointly goals
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // DB-Id of jointly goal to comment
    int goalServerDbIdToComment = 0;

    // jointly goal number in list view
    int goalNumberInListView = 0;

    // cursor for the choosen jointly goal
    Cursor cursorChoosenGoal;

    // cursor for all comments to the choosen jointly goal
    Cursor cursorGoalAllComments;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentCommentJointlyGoals = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentCommentJointlyGoalsBrodcastReceiver, filter);
        
        return viewFragmentCommentJointlyGoals;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentCommentContextJointlyGoals = getActivity().getApplicationContext();

        fragmentCommentContextJointlyGoalsThisFragment = this;

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init the fragment jointly goals comment only when an goal is choosen
        if (goalServerDbIdToComment != 0) {
            initFragmentCommentJointlyGoals();
            buildFragmentJointlyGoalsCommentView();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentCommentContextJointlyGoals, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentCommentContextJointlyGoals, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentCommentJointlyGoalsBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourGoalsFragmentCommentJointlyGoalsBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                Boolean refreshView = false;

                String tmpExtraOurGoals = intentExtras.getString("OurGoals","0");
                String tmpExtraOurGoalsNow = intentExtras.getString("OurGoalsJointlyNow","0");
                String tmpExtraOurGoalsNowComment = intentExtras.getString("OurGoalsJointlyComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsCommentShareDisable","0");
                String tmpExtraOurGoalsCommentShareDisable = intentExtras.getString("OurGoalsSettingsCommentShareEnable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsCommentCountComment","0");
                String tmpExtraOurGoalsJointlyCommentSendInBackgroundRefreshView = intentExtras.getString("OurGoalsJointlyCommentSendInBackgroundRefreshView","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentCommentContextJointlyGoals.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();

                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNowComment != null && tmpExtraOurGoalsNowComment.equals("1")) {
                    // update now comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentCommentContextJointlyGoals.getString(R.string.toastMessageCommentJointlyGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNow != null && tmpExtraOurGoalsNow.equals("1")) {
                    // update jointly goals! -> go back to fragment now goals and show dialog

                    // check goals and goals update and show dialog goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("jointly");

                    // go back to fragment jointly goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_jointly_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentCommentContextJointlyGoals.getString(R.string.toastMessageJointlyGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentCommentContextJointlyGoals.getString(R.string.toastMessageJointlyGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentCommentContextJointlyGoals.getString(R.string.toastMessageJointlyGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    refreshView = true;
                }
                else if (tmpExtraOurGoalsJointlyCommentSendInBackgroundRefreshView != null &&  tmpExtraOurGoalsJointlyCommentSendInBackgroundRefreshView.equals("1")) {
                    // debetable comment send in background -> refresh view
                    refreshView = true;
                }

                if (refreshView) {
                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentCommentContextJointlyGoalsThisFragment).attach(fragmentCommentContextJointlyGoalsThisFragment).commit();
                }
            }
        }
    };
    

    // inits the fragment for use
    private void initFragmentCommentJointlyGoals() {

        // init the DB
        myDb = new DBAdapter(fragmentCommentContextJointlyGoals);

        // hide fab
        // show fab and set on click listener
        if (fabFragmentJointlyComment != null) {
            fabFragmentJointlyComment.hide();
        }

        // init the prefs
        prefs = fragmentCommentContextJointlyGoals.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentCommentContextJointlyGoals.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // get choosen jointly goal
        cursorChoosenGoal = myDb.getJointlyRowOurGoals(goalServerDbIdToComment);

        // get all comments for choosen jointly goals
        cursorGoalAllComments = myDb.getAllRowsOurGoalsJointlyGoalsComment(goalServerDbIdToComment, "descending", 0);

        // Set correct subtitle in Activity -> "Ziel ... kommentieren"
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsComment", "string", fragmentCommentContextJointlyGoals.getPackageName()));
        tmpSubtitle = String.format(tmpSubtitle, goalNumberInListView);
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyNowComment");

        // set visibility of FAB for this fragment
        ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility ("hide", "jointlyNowComment");
    }


    // build the view for the fragment
    private void buildFragmentJointlyGoalsCommentView () {

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = viewFragmentCommentJointlyGoals.findViewById(R.id.goalCommentNumberIntro);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showJointlyGoalCommentIntroText) + " " + goalNumberInListView);

        // textview for the author of goal
        TextView tmpTextViewAuthorNameText = viewFragmentCommentJointlyGoals.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsAuthorNameTextWithDate), cursorChoosenGoal.getString(cursorChoosenGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // textview for the goal
        TextView textViewGoal = viewFragmentCommentJointlyGoals.findViewById(R.id.choosenGoal);
        String goal = cursorChoosenGoal.getString(cursorChoosenGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewGoal.setText(goal);

        // check, sharing comments enable?
        if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, 0) == 0) {
            TextView textCommentSharingIsDisable = viewFragmentCommentJointlyGoals.findViewById(R.id.commentSharingIsDisable);
            textCommentSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // some comments for goal available?
        if (cursorGoalAllComments.getCount() > 0) {

            // set row id of jointly comment from db for timer update
            final Long rowIdForUpdate = cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID));

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualJointlyCommentText));

            // position one for comment cursor
            cursorGoalAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
                TextView newEntryOfComment = viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentNewInfoText);
                String txtNewEntryOfComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.newEntryTextOurGoal);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurGoalsJointlyGoalComment(cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = viewFragmentCommentJointlyGoals.findViewById(R.id.textAuthorNameLastActualComment);
            String tmpAuthorName = cursorGoalAllComments.getString(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME));

            if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                tmpAuthorName = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentPersonalAuthorName);
            }

            String commentDate = EfbHelperClass.timestampToDateFormat(cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME)), "HH:mm");
            String tmpTextAuthorNameLastActualComment = String.format(fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            if (cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(getResources().getString(R.string.ourGoalsJointlyCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
            tmpTextViewAuthorNameLastActualComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualComment = viewFragmentCommentJointlyGoals.findViewById(R.id.textSendInfoLastActualComment);
            if (cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS)) == 0) {

                String tmpTextSendInfoLastActualComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentSendInfo);
                tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
            }
            else if (cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS)) == 1) {
                // textview for status 1 of the last actual comment

                // check, sharing of comments enable?
                if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, 0) == 1) {
                    // check system time is in past or future?
                    Long writeTimeComment = cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME)); // write time is from sever
                    Integer delayTime = prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                    Long maxTimerTime = writeTimeComment+delayTime;
                    if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_TIMER_STATUS)) == 0) { // check system time is in past and timer status is run!
                        // calculate run time for timer in MILLISECONDS!!!
                        Long nowTime = System.currentTimeMillis();
                        Long localeTimeComment = cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME));
                        Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                        // set textview visible
                        tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);

                        // start the timer with the calculated milliseconds
                        if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                            new CountDownTimer(runTimeForTimer, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    // gernate count down timer
                                    String FORMAT = "%02d:%02d:%02d";
                                    String tmpTextSendInfoLastActualComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentSendDelayInfo);
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
                                    String tmpTextSendInfoLastActualComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentSendSuccsessfullInfo);
                                    tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                                    myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                }
                            }.start();

                        } else {
                            // no count down anymore -> show send successfull
                            String tmpTextSendInfoLastActualComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentSendSuccsessfullInfo);
                            tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else {
                        // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                        tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                        String tmpTextSendInfoLastActualComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }
                else { // sharing of comments is disable! -> show text
                    String tmpTextSendInfoLastActualComment;
                    tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                    if (prefs.getLong(ConstansClassOurGoals.namePrefsJointlyCommentShareChangeTime, 0) < cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME))) {
                        // show send successfull, but no sharing
                        tmpTextSendInfoLastActualComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentSendInfoSharingDisable);
                    }
                    else {
                        // show send successfull
                        tmpTextSendInfoLastActualComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentSendSuccsessfullInfo);
                    }
                    tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                    myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                }
            }

            // textview for the comment text
            TextView tmpTextViewCommentText = viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentText);
            String tmpCommentText = cursorGoalAllComments.getString(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);

        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualJointlyCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorGoalAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = viewFragmentCommentJointlyGoals.findViewById(R.id.textAuthorNameLastActualComment);
            tmpTextViewAuthorNameLastActualComment.setText(this.getResources().getString(R.string.lastActualJointlyCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewCommentText = viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentText);
            tmpTextViewCommentText.setVisibility(View.GONE);

            // button for overview comment
            Button tmpButtonShowOverviewComment = viewFragmentCommentJointlyGoals.findViewById(R.id.buttonJointlyCommentBackToShowJointlyComment);
            tmpButtonShowOverviewComment.setVisibility(View.GONE);

            // textview border no comment and info text
            TextView tmpBorderBetweenNoCommentAndInfoText = viewFragmentCommentJointlyGoals.findViewById(R.id.borderBetweenNoCommentInfoAndInfoText);
            tmpBorderBetweenNoCommentAndInfoText.setVisibility(View.VISIBLE);
        }

        // textview for max comments, count comments and max letters
        TextView textViewMaxAndCount = viewFragmentCommentJointlyGoals.findViewById(R.id.infoJointlyCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
        // build text element max comment
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextJointlyGoalsCommentMaxSingular), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextJointlyGoalsCommentMaxPlural), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
        }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentUnlimitedText);
        }

        // build text element count comment
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentCountZero);
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0));

        // build text element delay time
        String tmpInfoTextDelaytimeSingluarPluaral;
        if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) == 0) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyCommentDelaytimeNoDelay);
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) == 1) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyCommentDelaytimeSingular);
        }
        else {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyCommentDelaytimePlural);
            tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0));
        }

        // generate text comment max letters
        tmpInfoTextCommentMaxLetters =  this.getResources().getString(R.string.infoTextNowCommentCommentMaxLettersAndDelaytime);
        tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyLetters, 0));

        // show info text
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " +tmpInfoTextDelaytimeSingluarPluaral);

        // get max letters for edit text comment
        final int tmpMaxLength = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyLetters, 10);

        // get textView to count input letters and init it
        final TextView textViewCountLettersCommentEditText = viewFragmentCommentJointlyGoals.findViewById(R.id.countLettersCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment textfield
        final EditText txtInputGoalComment = viewFragmentCommentJointlyGoals.findViewById(R.id.inputGoalComment);

        // set hint text in edit text field
        String tmpHintTextForCommentField = this.getResources().getString(R.string.goalJointlyCommentHintText);
        tmpHintTextForCommentField = String.format(tmpHintTextForCommentField, goalNumberInListView);
        txtInputGoalComment.setHint(tmpHintTextForCommentField);

        // set text watcher to count letters in comment field
        final TextWatcher txtInputGoalCommentTextWatcher = new TextWatcher() {
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
        txtInputGoalComment.addTextChangedListener(txtInputGoalCommentTextWatcher);

        // set input filter max length for comment field
        txtInputGoalComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

        // set text max comment/ actual comment
        TextView infoTextCountComment = viewFragmentCommentJointlyGoals.findViewById(R.id.infoTextCountComment);
        String tmpInfoTextCountCommentSingluarPluaralNoLimit;
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourGoalsJointlyCommentCountCommentTextSingular);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment,0), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourGoalsJointlyCommentCountCommentTextPlural);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment,0), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));

        }
        else {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourGoalsJointlyCommentCountCommentTextNoLimit);
        }
        infoTextCountComment.setText(tmpInfoTextCountCommentSingluarPluaralNoLimit);


        // get button send comment
        Button buttonSendGoalComment = viewFragmentCommentJointlyGoals.findViewById(R.id.buttonSendGoalComment);

        // set onClick listener send goal comment
        buttonSendGoalComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

                    if (txtInputGoalComment.getText().toString().length() > 3) {

                        String commentText = txtInputGoalComment.getText().toString();
                        String userName = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");
                        Long commentTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                        if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                            commentTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                        }
                        Long uploadTime = 0L;
                        Long localeTime = System.currentTimeMillis();
                        String blockId = cursorChoosenGoal.getString(cursorChoosenGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID));
                        Boolean newEntry = false;
                        Long dateOfJointlyGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());
                        int commentStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                        int jointlyGoalsServerId = cursorChoosenGoal.getInt(cursorChoosenGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID));
                        int timerStatus = 0;

                        // insert comment in DB
                        Long tmpDbId = myDb.insertRowOurGoalJointlyGoalComment(commentText, userName, commentTime, localeTime, uploadTime, blockId, newEntry, dateOfJointlyGoals, commentStatus, jointlyGoalsServerId, timerStatus);

                        // increment comment count
                        int countCommentSum = prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) + 1;
                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, countCommentSum);
                        prefsEditor.apply();

                        // send intent to service to start the service and send comment to server!
                        // send intent to service to start the service
                        Intent startServiceIntent = new Intent(fragmentCommentContextJointlyGoals, ExchangeJobIntentServiceEfb.class);
                        startServiceIntent.putExtra("com", "send_jointly_comment_goal");
                        startServiceIntent.putExtra("dbid", tmpDbId);
                        startServiceIntent.putExtra("receiverBroadcast", "");
                        // start service
                        ExchangeJobIntentServiceEfb.enqueueWork(fragmentCommentContextJointlyGoals, startServiceIntent);

                        // build intent to get back to OurGoalsFragmentJointlyGoals
                        Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com", "show_jointly_goals_now");
                        intent.putExtra("db_id", 0);
                        intent.putExtra("arr_num", 0);
                        intent.putExtra("eval_next", false);
                        getActivity().startActivity(intent);

                    } else {

                        TextView tmpErrorTextView = viewFragmentCommentJointlyGoals.findViewById(R.id.errorInputJointlyGoalComment);
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    // delete text in edittextfield
                    txtInputGoalComment.setText("");

                    // case is closed -> show toast
                    String textCaseClose = fragmentCommentContextJointlyGoals.getString(R.string.toastJointlyGoalsCommentCaseCloseToastText);
                    Toast toast = Toast.makeText(fragmentCommentContextJointlyGoals, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        // button back to goal overview
        Button buttonBackToGoalOverview = viewFragmentCommentJointlyGoals.findViewById(R.id.buttonJointlyCommentBackToGoal);
        // onClick listener button back to goal overview
        buttonBackToGoalOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_jointly_goals_now");
                intent.putExtra("db_id", 0);
                intent.putExtra("arr_num", 0);
                getActivity().startActivity(intent);

            }
        });

        // button back to show comment overview for jointly goals
        Button buttonBackToCommentOverviewForJointlyGoals = viewFragmentCommentJointlyGoals.findViewById(R.id.buttonJointlyCommentBackToShowJointlyComment);
        // onClick listener back to comment overview for arrangement
        buttonBackToCommentOverviewForJointlyGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_comment_for_jointly_goal");
                intent.putExtra("db_id", goalServerDbIdToComment );
                intent.putExtra("arr_num", goalNumberInListView);
                getActivity().startActivity(intent);

            }
        });
        // End build the view
    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpJointlyGoalServerDbIdToComment = 0;

        // call getter-methode getJointlyGoalDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale goal
        tmpJointlyGoalServerDbIdToComment = ((ActivityOurGoals)getActivity()).getJointlyGoalDbIdFromLink();

        // call getter-methode getFabViewOurGoals() in ActivityOurGoals to get view for fab
        fabFragmentJointlyComment = ((ActivityOurGoals)getActivity()).getFabViewOurGoals();

        if (tmpJointlyGoalServerDbIdToComment > 0) {
            goalServerDbIdToComment = tmpJointlyGoalServerDbIdToComment;

            // call getter-methode getJointlyGoalNumberInListview() in ActivityOurGoals to get listView-number for the actuale jointly goal
            goalNumberInListView = ((ActivityOurGoals)getActivity()).getJointlyGoalNumberInListview();
            if (goalNumberInListView < 1) goalNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("jointlyGoals");
        }
    }


}
