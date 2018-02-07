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
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
            Intent startServiceIntent = new Intent(fragmentCommentContextJointlyGoals, ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            fragmentCommentContextJointlyGoals.startService(startServiceIntent);
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


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourGoalsFragmentCommentJointlyGoalsBrodcastReceiver = new BroadcastReceiver() {

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
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentCommentContextJointlyGoals.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
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
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentCommentContextJointlyGoals.getString(R.string.toastMessageJointlyGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentCommentContextJointlyGoals.getString(R.string.toastMessageJointlyGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
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

        // init the prefs
        prefs = fragmentCommentContextJointlyGoals.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentCommentContextJointlyGoals.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // get choosen jointly goal
        cursorChoosenGoal = myDb.getJointlyRowOurGoals(goalServerDbIdToComment);

        // get all comments for choosen jointly goals
        cursorGoalAllComments = myDb.getAllRowsOurGoalsJointlyGoalsComment(goalServerDbIdToComment);

        // Set correct subtitle in Activity -> "Ziel ... kommentieren"
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsComment", "string", fragmentCommentContextJointlyGoals.getPackageName()));
        tmpSubtitle = String.format(tmpSubtitle, goalNumberInListView);
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyNowComment");
    }


    // build the view for the fragment
    private void buildFragmentJointlyGoalsCommentView () {

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.goalCommentNumberIntro);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showJointlyGoalCommentIntroText) + " " + goalNumberInListView);

        // textview for the author of goal
        TextView tmpTextViewAuthorNameText = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsAuthorNameTextWithDate), cursorChoosenGoal.getString(cursorChoosenGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

        // textview for the goal
        TextView textViewGoal = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.choosenGoal);
        String goal = cursorChoosenGoal.getString(cursorChoosenGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewGoal.setText(goal);

        // generate back link "zurueck zu gemeinsamen zielen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourgoals")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_jointly_goals_now");
        TextView linkShowCommentBackLink = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.goalShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+viewFragmentCommentJointlyGoals.getResources().getString(fragmentCommentContextJointlyGoals.getResources().getIdentifier("ourGoalsBackLinkToJointlyGoals", "string", fragmentCommentContextJointlyGoals.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

        // check, sharing comments enable?
        if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, 0) == 0) {
            TextView textCommentSharingIsDisable = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.commentSharingIsDisable);
            textCommentSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // some comments for goal available?
        if (cursorGoalAllComments.getCount() > 0) {

            // set row id of jointly comment from db for timer update
            final Long rowIdForUpdate = cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID));

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualJointlyCommentText));

            // position one for comment cursor
            cursorGoalAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
                TextView newEntryOfComment = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentNewInfoText);
                String txtNewEntryOfComment = fragmentCommentContextJointlyGoals.getResources().getString(R.string.newEntryTextOurGoal);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurGoalsJointlyGoalComment(cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.textAuthorNameLastActualComment);
            String tmpAuthorName = cursorGoalAllComments.getString(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME));

            if (tmpAuthorName.equals(prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"))) {
                tmpAuthorName = fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentPersonalAuthorName);
            }

            String commentDate = EfbHelperClass.timestampToDateFormat(cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");;
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_LOCAL_TIME)), "HH:mm");;
            String tmpTextAuthorNameLastActualComment = String.format(fragmentCommentContextJointlyGoals.getResources().getString(R.string.ourGoalsJointlyCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            if (cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(getResources().getString(R.string.ourGoalsJointlyCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
            tmpTextViewAuthorNameLastActualComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualComment = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.textSendInfoLastActualComment);
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
                    String tmpTextSendInfoLastActualComment = "";
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
            TextView tmpTextViewCommentText = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentText);
            String tmpCommentText = cursorGoalAllComments.getString(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);

            // get textview for Link to Show all comments
            TextView tmpTextViewLInkToShowAllComment = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.commentLinkToShowAllComments);

            // more than one comment available?
            if (cursorGoalAllComments.getCount() > 1) {

                // generate link to show all comments
                Uri.Builder showCommentLinkBuilder = new Uri.Builder();
                showCommentLinkBuilder.scheme("smart.efb.deeplink")
                        .authority("linkin")
                        .path("ourgoals")
                        .appendQueryParameter("db_id", Integer.toString(goalServerDbIdToComment))
                        .appendQueryParameter("arr_num", Integer.toString(goalNumberInListView))
                        .appendQueryParameter("com", "show_comment_for_jointly_goal");

                if (cursorGoalAllComments.getCount() == 2) {
                    String tmpLinkStringShowAllComments = String.format(fragmentCommentContextJointlyGoals.getResources().getString(fragmentCommentContextJointlyGoals.getResources().getIdentifier("ourGoalsJointlyCommentLinkToShowAllCommentsSingular", "string", fragmentCommentContextJointlyGoals.getPackageName())),cursorGoalAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllComment.setText(Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllComments + "</a>"));
                }
                else {
                    String tmpLinkStringShowAllComments = String.format(fragmentCommentContextJointlyGoals.getResources().getString(fragmentCommentContextJointlyGoals.getResources().getIdentifier("ourGoalsJointlyCommentLinkToShowAllCommentsPlural", "string", fragmentCommentContextJointlyGoals.getPackageName())),cursorGoalAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllComment.setText(Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllComments + "</a>"));
                }
                tmpTextViewLInkToShowAllComment.setMovementMethod(LinkMovementMethod.getInstance());
            }
            else {
                // no comment anymore
                String tmpLinkStringShowAllComments = fragmentCommentContextJointlyGoals.getResources().getString(fragmentCommentContextJointlyGoals.getResources().getIdentifier("ourGoalsJointlyCommentLinkToShowAllCommentsNotAvailable", "string", fragmentCommentContextJointlyGoals.getPackageName()));
                tmpTextViewLInkToShowAllComment.setText(tmpLinkStringShowAllComments);
            }
        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualJointlyCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorGoalAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.textAuthorNameLastActualComment);
            tmpTextViewAuthorNameLastActualComment.setText(this.getResources().getString(R.string.lastActualJointlyCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewCommentText = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.lastActualCommentText);
            tmpTextViewCommentText.setVisibility(View.GONE);
        }

        // textview for max comments, count comments and max letters
        TextView textViewMaxAndCount = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.infoJointlyCommentMaxAndCount);
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
        String tmpInfoTextDelaytimeSingluarPluaral = "";
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
        final TextView textViewCountLettersCommentEditText = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.countLettersCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment textfield -> set hint text
        final EditText txtInputGoalComment = (EditText) viewFragmentCommentJointlyGoals.findViewById(R.id.inputGoalComment);

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

        // get button send comment
        Button buttonSendGoalComment = (Button) viewFragmentCommentJointlyGoals.findViewById(R.id.buttonSendGoalComment);

        // set onClick listener send goal comment
        buttonSendGoalComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtInputGoalComment.getText().toString().length() > 3) {

                    String commentText = txtInputGoalComment.getText().toString();
                    String userName = prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt");
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
                    prefsEditor.commit();

                    // send intent to service to start the service and send comment to server!
                    Intent startServiceIntent = new Intent(fragmentCommentContextJointlyGoals, ExchangeServiceEfb.class);
                    startServiceIntent.putExtra("com","send_jointly_comment_goal");
                    startServiceIntent.putExtra("dbid",tmpDbId);
                    startServiceIntent.putExtra("receiverBroadcast","");
                    fragmentCommentContextJointlyGoals.startService(startServiceIntent);

                    // build intent to get back to OurGoalsFragmentJointlyGoals
                    Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_jointly_goals_now");
                    getActivity().startActivity(intent);

                } else {

                    TextView tmpErrorTextView = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.errorInputJointlyGoalComment);
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                }

            }
        });

        // button abbort
        Button buttonAbbortGoalComment = (Button) viewFragmentCommentJointlyGoals.findViewById(R.id.buttonAbortComment);
        // onClick listener button abbort
        buttonAbbortGoalComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_jointly_goals_now");
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
