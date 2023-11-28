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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 14.11.2016.
 */
public class OurGoalsFragmentDebetableGoalsComment extends Fragment {

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] debetableGoalsCommentScalesLevel = new String [countScalesLevel];

    // fragment view
    View viewFragmentDebetableGoalsComment;

    // fragment context
    Context fragmentDebetableGoalsContext = null;

    // the fragment
    Fragment fragmentCommentContextDebetableGoalsThisFragment;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // fab view
    FloatingActionButton fabFragmentDebetableComment = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment debetable goals
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // Server DB-Id of debetable goal to comment
    int debetableGoalsServerDbIdToComment = 0;

    // debetable goal number in list view
    int debetableGoalNumberInListView = 0;

    // cursor for the choosen debetable goal
    Cursor cursorChoosenDebetableGoals;

    // cursor for all comments to the choosen debetable goal
    Cursor cursorDebetableGoalAllComments;

    //number of radio buttons in struct question
    static final int numberOfRadioButtonsStructQuestion = 5;

    // result of struct question (1-5)
    int structQuestionResultDebetableGoalComment = 0;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentDebetableGoalsComment = layoutInflater.inflate(R.layout.fragment_our_goals_debetable_goals_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentCommentDebetableGoalsBrodcastReceiver, filter);

        return viewFragmentDebetableGoalsComment;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {
        
        super.onViewCreated(view, saveInstanceState);

        fragmentDebetableGoalsContext = getActivity().getApplicationContext();

        fragmentCommentContextDebetableGoalsThisFragment = this;

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init the fragment only when an debetable goal is choosen
        if (debetableGoalsServerDbIdToComment != 0) {

            // init the fragment debetable comment and build the view
            initFragmentDebetableGoalComment();
            buildFragmentDebetableGoalsCommentView();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentDebetableGoalsContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentDebetableGoalsContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentCommentDebetableGoalsBrodcastReceiver);

        // close db connection
        myDb.close();

    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourGoalsFragmentCommentDebetableGoalsBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurGoalsDebetableNow = intentExtras.getString("OurGoalsDebetableNow","0");
                String tmpExtraOurGoalsDebetableNowComment = intentExtras.getString("OurGoalsDebetableComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareDisable = intentExtras.getString("OurGoalsSettingsDebetableCommentShareDisable","0");
                String tmpExtraOurGoalsCommentShareEnable= intentExtras.getString("OurGoalsSettingsDebetableCommentShareEnable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsDebetableCommentCountComment","0");
                String tmpExtraOurGoalsDebetableCommentSendInBackgroundRefreshView = intentExtras.getString("OurGoalsDebetableCommentSendInBackgroundRefreshView","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentDebetableGoalsContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();

                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNowComment != null && tmpExtraOurGoalsDebetableNowComment.equals("1")) {
                    // update debetable comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalsContext.getString(R.string.toastMessageCommentDebetableGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNow != null && tmpExtraOurGoalsDebetableNow.equals("1")) {
                    // update debetable goals! -> go back to fragment now goals and show dialog

                    // check goals and goals update and show dialog goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("debetable");

                    // go back to fragment debetable goals goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_debetable_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset debetable comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalsContext.getString(R.string.toastMessageDebetableGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalsContext.getString(R.string.toastMessageDebetableGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalsContext.getString(R.string.toastMessageDebetableGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {
                    // goal settings change
                    refreshView = true;
                }
                else if (tmpExtraOurGoalsDebetableCommentSendInBackgroundRefreshView != null &&  tmpExtraOurGoalsDebetableCommentSendInBackgroundRefreshView.equals("1")) {
                    // debetable comment send in background -> refresh view
                    refreshView = true;
                }

                if (refreshView) {
                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentCommentContextDebetableGoalsThisFragment).attach(fragmentCommentContextDebetableGoalsThisFragment).commit();
                }
            }
        }
    };


    // inits the fragment for use
    private void initFragmentDebetableGoalComment() {

        // init the DB
        myDb = new DBAdapter(fragmentDebetableGoalsContext);

        // hide fab
        // show fab and set on click listener
        if (fabFragmentDebetableComment != null) {
            fabFragmentDebetableComment.hide();
        }

        // init the prefs
        prefs = fragmentDebetableGoalsContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentDebetableGoalsContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // init array for text description of scales levels
        debetableGoalsCommentScalesLevel = getResources().getStringArray(R.array.debetableGoalsCommentScalesLevel);

        // get choosen debetable goal
        cursorChoosenDebetableGoals = myDb.getDebetableRowOurGoals(debetableGoalsServerDbIdToComment);

        // get all comments for choosen debetable goal
        cursorDebetableGoalAllComments = myDb.getAllRowsOurGoalsDebetableGoalsComment(debetableGoalsServerDbIdToComment, "descending", 0);
        
        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = String.format(getResources().getString(getResources().getIdentifier("ourGoalsSubtitleDebetableGoalsComment", "string", fragmentDebetableGoalsContext.getPackageName())), debetableGoalNumberInListView);
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle(tmpSubtitle, "debetableComment");

        // set visibility of FAB for this fragment
        ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility ("hide", "debetableComment");
    }


    // build the view for the fragment
    private void buildFragmentDebetableGoalsCommentView () {

        // set intro text for comment debetable goal
        TextView textViewIntroTextForCommentDebetableGoal = viewFragmentDebetableGoalsComment.findViewById(R.id.introStructQuestionForCommentDebetableGoal);
        String tmpIntroTextForCommentDebetableGoalField = this.getResources().getString(R.string.introStructQuestionForCommentDebetableGoal);
        tmpIntroTextForCommentDebetableGoalField = String.format(tmpIntroTextForCommentDebetableGoalField, debetableGoalNumberInListView);
        textViewIntroTextForCommentDebetableGoal.setText(tmpIntroTextForCommentDebetableGoalField);

        //textview for the comment intro
        TextView textCommentNumberIntro = viewFragmentDebetableGoalsComment.findViewById(R.id.debetableGoalCommentNumberIntro);
        String tmpCommentNumberIntro = this.getResources().getString(R.string.showDebetableGoalsIntroText) + " " + debetableGoalNumberInListView;
        textCommentNumberIntro.setText(tmpCommentNumberIntro);

        // textview for the author of debetable goal
        TextView tmpTextViewAuthorNameText = viewFragmentDebetableGoalsComment.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentAuthorNameTextWithDate), cursorChoosenDebetableGoals.getString(cursorChoosenDebetableGoals.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));
        
        // check, sharing debetable comments enable?
        if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentShare, 0) == 0) {
            TextView textDebetableCommentSharingIsDisable = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.commentSharingIsDisable);
            textDebetableCommentSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // textview for the debetable goal
        TextView textViewGoal = viewFragmentDebetableGoalsComment.findViewById(R.id.choosenDebetableGoal);
        String debetableGoal = cursorChoosenDebetableGoals.getString(cursorChoosenDebetableGoals.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewGoal.setText(debetableGoal);

        // some comments for goals available?
        if (cursorDebetableGoalAllComments.getCount() > 0) {

            // set row id of jointly comment from db for timer update
            final Long rowIdForUpdate = cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID));

            //textview for the last actual comment intro
            TextView textLastActualDebetableCommentIntro = viewFragmentDebetableGoalsComment.findViewById(R.id.lastActualDebetableCommentInfoText);
            textLastActualDebetableCommentIntro.setText(this.getResources().getString(R.string.showDebetableGoalCommentIntroText));

            // position one for comment cursor
            cursorDebetableGoalAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
                TextView newEntryOfComment = viewFragmentDebetableGoalsComment.findViewById(R.id.lastActualDebetableCommentNewInfoText);
                String txtNewEntryOfComment = fragmentDebetableGoalsContext.getResources().getString(R.string.newEntryText);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurGoalsDebetableGoalsComment(cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualDebetableComment = viewFragmentDebetableGoalsComment.findViewById(R.id.textAuthorNameLastActualDebetableComment);
            String tmpAuthorName = cursorDebetableGoalAllComments.getString(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME));

            if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                tmpAuthorName = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentPersonalAuthorName);
            }
            String commentDate = EfbHelperClass.timestampToDateFormat(cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME)), "HH:mm");
            String tmpTextAuthorNameLastActualComment = String.format(fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            if (cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(getResources().getString(R.string.ourGoalsDebetableCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
            tmpTextViewAuthorNameLastActualDebetableComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));
            
            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualDebetableComment = viewFragmentDebetableGoalsComment.findViewById(R.id.textSendInfoLastActualDebetableComment);
            if (cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS)) == 0) {

                String tmpTextSendInfoLastActualComment = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentSendInfo);
                tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
            }
            else if (cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS)) == 1) {
                // textview for status 1 of the last actual comment

                // check, sharing of debetable comments enable?
                if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentShare, 0) == 1) {

                    // check system time is in past or future?
                    Long writeTimeComment = cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME)); // write time is from sever
                    Integer delayTime = prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                    Long maxTimerTime = writeTimeComment+delayTime;
                    if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_TIMER_STATUS)) == 0) { // check system time is in past and timer status is run!
                        // calculate run time for timer in MILLISECONDS!!!
                        Long nowTime = System.currentTimeMillis();
                        Long localeTimeComment = cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME));
                        Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                        // set textview visible
                        tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);

                        // start the timer with the calculated milliseconds
                        if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                            new CountDownTimer(runTimeForTimer, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    // gernate count down timer
                                    String FORMAT = "%02d:%02d:%02d";
                                    String tmpTextSendInfoLastActualComment = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentSendDelayInfo);
                                    String tmpTime = String.format(FORMAT,
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                    // put count down to string
                                    String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                    // and show
                                    tmpTextViewSendInfoLastActualDebetableComment.setText(tmpCountdownTimerString);
                                }

                                public void onFinish() {
                                    // count down is over -> show
                                    String tmpTextSendInfoLastActualComment = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentSendSuccsessfullInfo);
                                    tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                                    myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                }
                            }.start();

                        } else {
                            // no count down anymore -> show send successfull
                            String tmpTextSendInfoLastActualComment = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentSendSuccsessfullInfo);
                            tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else {
                        // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                        tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
                        String tmpTextSendInfoLastActualComment = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }
                else { // sharing of debetable comments is disable! -> show text
                    String tmpTextSendInfoLastActualDebetableComment = "";
                    tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
                    if (prefs.getLong(ConstansClassOurGoals.namePrefsDebetableCommentShareChangeTime, 0) < cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME))) {
                        // show send successfull, but no sharing
                        tmpTextSendInfoLastActualDebetableComment = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentSendInfoSharingDisable);
                    }
                    else {
                        // show send successfull
                        tmpTextSendInfoLastActualDebetableComment = fragmentDebetableGoalsContext.getResources().getString(R.string.ourGoalsDebetableCommentSendSuccsessfullInfo);
                    }
                    tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualDebetableComment);
                    myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                }
            }

            // show actual result struct question only when result > 0
            TextView textViewShowResultStructQuestion = viewFragmentDebetableGoalsComment.findViewById(R.id.assessementValueForDebetablComment);
            if (cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1)) > 0) {
                String actualResultStructQuestion = fragmentDebetableGoalsContext.getResources().getString(R.string.textOurGoalsDebetableCommentActualResultStructQuestion);
                actualResultStructQuestion = String.format(actualResultStructQuestion, debetableGoalsCommentScalesLevel[cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1)) - 1]);
                textViewShowResultStructQuestion.setText(HtmlCompat.fromHtml(actualResultStructQuestion, HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else { // result is =0; comes from server/ coach
                String actualResultStructQuestionFromCoach = fragmentDebetableGoalsContext.getResources().getString(R.string.textOurGoalsDebetableCommentActualResultStructQuestionFromCoach);
                textViewShowResultStructQuestion.setText(actualResultStructQuestionFromCoach);
            }

            // textview for the comment text
            TextView tmpTextViewCommentText = viewFragmentDebetableGoalsComment.findViewById(R.id.lastActualDebetableCommentText);
            String tmpCommentText = cursorDebetableGoalAllComments.getString(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);

            // button goto overview comment debetable goal
            Button buttonGoToDebetableGoalOverview = viewFragmentDebetableGoalsComment.findViewById(R.id.buttonDebetableCommentBackToShowComment);
            // onClick listener goto comment debetable goals overview
            buttonGoToDebetableGoalOverview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_comment_for_debetable_goal");
                    intent.putExtra("db_id", debetableGoalsServerDbIdToComment);
                    intent.putExtra("arr_num", debetableGoalNumberInListView);
                    getActivity().startActivity(intent);

                }
            });
            
        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualDebetableCommentIntro = viewFragmentDebetableGoalsComment.findViewById(R.id.lastActualDebetableCommentInfoText);
            textLastActualDebetableCommentIntro.setText(this.getResources().getString(R.string.ourGoalsDebetableCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorDebetableGoalAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualDebetableComment = viewFragmentDebetableGoalsComment.findViewById(R.id.textAuthorNameLastActualDebetableComment);
            tmpTextViewAuthorNameLastActualDebetableComment.setText(this.getResources().getString(R.string.ourGoalsDebetableCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewDebetableCommentText = viewFragmentDebetableGoalsComment.findViewById(R.id.lastActualDebetableCommentText);
            tmpTextViewDebetableCommentText.setVisibility(View.GONE);

            // button for overview sketch comment
            Button tmpButtonShowOverviewDebetableComment = viewFragmentDebetableGoalsComment.findViewById(R.id.buttonDebetableCommentBackToShowComment);
            tmpButtonShowOverviewDebetableComment.setVisibility(View.GONE);

            // textview border no sketch comment and info text
            TextView tmpBorderBetweenNoCommentAndInfoText = viewFragmentDebetableGoalsComment.findViewById(R.id.borderBetweenNoCommentInfoAndInfoText);
            tmpBorderBetweenNoCommentAndInfoText.setVisibility(View.VISIBLE);
        }

        // set onClickListener for radio button in radio group question 1-4
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;

        for (int numberOfButtons=0; numberOfButtons < numberOfRadioButtonsStructQuestion; numberOfButtons++) {
            tmpRessourceName ="structQuestionOne_Answer" + (numberOfButtons+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentDebetableGoalsContext.getPackageName());

                tmpRadioButtonQuestion = viewFragmentDebetableGoalsComment.findViewById(resourceId);
                tmpRadioButtonQuestion.setOnClickListener(new debetableGoalCommentRadioButtonListener(numberOfButtons));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // textview for max comments and count comments
        TextView textViewMaxAndCount = viewFragmentDebetableGoalsComment.findViewById(R.id.infoDebetableGoalCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
        // build text element max debetable goal comment
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextDebetableGoalCommentMaxSingular), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) > 1 && commentLimitationBorder){
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextDebetableGoalCommentMaxPlural), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));
        }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentUnlimitedText);
        }

        // build text element count debetable goal comment count
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentCountZero);
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0));

        // build text element delay time
        String tmpInfoTextDelaytimeSingluarPluaral = "";
        if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) == 0) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableCommentDelaytimeNoDelay);
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) == 1) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableCommentDelaytimeSingular);
        }
        else {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableCommentDelaytimePlural);
            tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0));
        }

        // generate text comment max letters
        tmpInfoTextCommentMaxLetters =  this.getResources().getString(R.string.infoTextDebetableGoalCommentMaxLetters);
        tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableLetters, 0));

        // show info text
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " +tmpInfoTextDelaytimeSingluarPluaral);

        // get max letters for edit text comment
        final int tmpMaxLength = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableLetters, 10);

        // get textView to count input letters and init it
        final TextView textViewCountLettersCommentEditText = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.countLettersCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment textfield -> insert new comment
        final EditText txtInputDebetableCommentComment = viewFragmentDebetableGoalsComment.findViewById(R.id.inputDebetableGoalComment);

        // set hint text in edit text field
        String tmpHintTextForCommentField = this.getResources().getString(R.string.commentDebetableGoalTextFieldHintText);
        tmpHintTextForCommentField = String.format(tmpHintTextForCommentField, debetableGoalNumberInListView);
        txtInputDebetableCommentComment.setHint(tmpHintTextForCommentField);

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
        txtInputDebetableCommentComment.addTextChangedListener(txtInputGoalCommentTextWatcher);

        // set input filter max length for comment field
        txtInputDebetableCommentComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

        // set text max comment/ actual comment
        TextView infoTextCountComment = viewFragmentDebetableGoalsComment.findViewById(R.id.infoTextCountDebetableComment);
        String tmpInfoTextCountCommentSingluarPluaralNoLimit;
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourGoalsDebetableCommentCountCommentTextSingular);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment,0), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourGoalsDebetableCommentCountCommentTextPlural);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment,0), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));

        }
        else {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourGoalsDebetableCommentCountCommentTextNoLimit);
        }
        infoTextCountComment.setText(tmpInfoTextCountCommentSingluarPluaralNoLimit);

        // button send comment
        Button buttonSendDebetableCommentComment = viewFragmentDebetableGoalsComment.findViewById(R.id.buttonSendDebetableGoalComment);

        // onClick listener send debetable goal comment
        buttonSendDebetableCommentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean debetableGoalCommentNoError = true;
                TextView tmpErrorTextView;

                if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

                    // check result struct question
                    tmpErrorTextView = viewFragmentDebetableGoalsComment.findViewById(R.id.errorStructQuestionForCommentDebetableGoal);
                    if (structQuestionResultDebetableGoalComment == 0 && tmpErrorTextView != null) {
                        debetableGoalCommentNoError = false;
                        tmpErrorTextView.setVisibility(View.VISIBLE);

                    } else if (tmpErrorTextView != null) {
                        tmpErrorTextView.setVisibility(View.GONE);
                    }

                    // check comment textfield -> insert new comment
                    tmpErrorTextView = viewFragmentDebetableGoalsComment.findViewById(R.id.errorFreeQuestionForCommentDebeableGoal);
                    if (txtInputDebetableCommentComment.getText().toString().length() < 3 && tmpErrorTextView != null) {
                        debetableGoalCommentNoError = false;
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    } else if (tmpErrorTextView != null) {
                        tmpErrorTextView.setVisibility(View.GONE);
                    }

                    if (debetableGoalCommentNoError) {

                        String commentText = txtInputDebetableCommentComment.getText().toString();
                        String userName = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");
                        Long commentTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                        if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                            commentTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                        }
                        Long uploadTime = 0L;
                        Long localeTime = System.currentTimeMillis();
                        String blockId = cursorChoosenDebetableGoals.getString(cursorChoosenDebetableGoals.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_BLOCK_ID));
                        Boolean newEntry = false;
                        Long dateOfDebetableGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis());
                        int commentStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                        int debetableGoalsServerId = cursorChoosenDebetableGoals.getInt(cursorChoosenDebetableGoals.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID));
                        int timerStatus = 0;

                        // insert comment in DB
                        Long tmpDbId = myDb.insertRowOurGoalsDebetableGoalsComment(commentText, structQuestionResultDebetableGoalComment, 0, 0, userName, commentTime, localeTime, uploadTime, blockId, newEntry, dateOfDebetableGoals, commentStatus, debetableGoalsServerId, timerStatus);

                        // increment debetable goal comment count
                        int countDebetableGoalsCommentSum = prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0) + 1;
                        prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, countDebetableGoalsCommentSum);
                        prefsEditor.apply();

                        // send intent to service to start the service
                        Intent startServiceIntent = new Intent(fragmentDebetableGoalsContext, ExchangeJobIntentServiceEfb.class);
                        startServiceIntent.putExtra("com", "send_debetable_comment_goal");
                        startServiceIntent.putExtra("dbid", tmpDbId);
                        startServiceIntent.putExtra("receiverBroadcast", "");
                        // start service
                        ExchangeJobIntentServiceEfb.enqueueWork(fragmentDebetableGoalsContext, startServiceIntent);

                        // build intent to get back to OurGoalsFragmentDebetableNow
                        Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com", "show_debetable_goals_now");
                        intent.putExtra("db_id", 0);
                        intent.putExtra("arr_num", 0);
                        intent.putExtra("eval_next", false);
                        getActivity().startActivity(intent);
                    }
                }
                else {
                    // delete text in edittextfield
                    txtInputDebetableCommentComment.setText("");

                    // case is closed -> show toast
                    String textCaseClose = fragmentDebetableGoalsContext.getString(R.string.toastDebetableGoalsCommentCaseCloseToastText);
                    Toast toast = Toast.makeText(fragmentDebetableGoalsContext, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        // button back to debetable goals
        Button buttonBackTodebetableGoalOverview = viewFragmentDebetableGoalsComment.findViewById(R.id.buttonDebetableCommentBackToDebetableGoal);
        // onClick listener button abbort
        buttonBackTodebetableGoalOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_debetable_goals_now");
                intent.putExtra("db_id", 0);
                intent.putExtra("arr_num", 0);
                getActivity().startActivity(intent);
            }
        });
        // End build the view
    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpdebetableGoalsServerDbIdToComment = 0;

        // call getter-methode getDebetableGoalsDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale debetable goal
        tmpdebetableGoalsServerDbIdToComment = ((ActivityOurGoals)getActivity()).getDebetableGoalDbIdFromLink();

        // call getter-methode getFabViewOurGoals() in ActivityOurGoals to get view for fab
        fabFragmentDebetableComment = ((ActivityOurGoals)getActivity()).getFabViewOurGoals();

        if (tmpdebetableGoalsServerDbIdToComment > 0) {
            debetableGoalsServerDbIdToComment = tmpdebetableGoalsServerDbIdToComment;

            // call getter-methode getDebetableGoalsNumberInListview() in ActivityOurGoals to get listView-number for the actuale debetable goal
            debetableGoalNumberInListView = ((ActivityOurGoals)getActivity()).getDebetableGoalNumberInListview();
            if (debetableGoalNumberInListView < 1) debetableGoalNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("debetableGoals");
        }
    }


    // onClickListener for radioButtons in fragment layout debetable goal comment
    public class debetableGoalCommentRadioButtonListener implements View.OnClickListener {

        int radioButtonNumber;

        public debetableGoalCommentRadioButtonListener (int number) {

            this.radioButtonNumber = number;
        }

        @Override
        public void onClick(View v) {

            int tmpResultQuestion;

            // check button number and get result
            switch (radioButtonNumber) {

                case 0: // ever
                    tmpResultQuestion = 1;
                    break;
                case 1:
                    tmpResultQuestion = 2;
                    break;
                case 2:
                    tmpResultQuestion = 3;
                    break;
                case 3:
                    tmpResultQuestion = 4;
                    break;
                case 4: // radioButton never
                    tmpResultQuestion = 5;
                    break;
                default:
                    tmpResultQuestion = 0;
                    break;
            }

            structQuestionResultDebetableGoalComment = tmpResultQuestion;
        }
    }


}