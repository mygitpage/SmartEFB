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
import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 22.09.16.
 */
public class OurArrangementFragmentSketchComment extends Fragment {

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] evaluateSketchCommentScalesLevel = new String [countScalesLevel];

    // fragment view
    View viewFragmentSketchComment;

    // fragment context
    Context fragmentSketchCommentContext = null;

    // the fragment
    Fragment fragmentSketchCommentThisFragmentContext;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // Server DB-Id of arrangement to comment
    int sketchArrangementServerDbIdToComment = 0;

    // arrangement number in list view
    int sketchArrangementNumberInListView = 0;

    // cursor for the choosen sketch arrangement
    Cursor cursorChoosenSketchArrangement;

    // cursor for all comments to the choosen sketch arrangement
    Cursor cursorSketchArrangementAllComments;

    //number of radio buttons in struct question
    static final int numberOfRadioButtonsStructQuestion = 5;

    // result of struct question (1-5)
    int structQuestionResultSketchComment = 0;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentSketchComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_sketch_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentSketchCommentBrodcastReceiver, filter);

        return viewFragmentSketchComment;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentSketchCommentContext = getActivity().getApplicationContext();

        fragmentSketchCommentThisFragmentContext = this;

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init the fragment now only when an arrangement is choosen
        if (sketchArrangementServerDbIdToComment != 0) {

            // init the fragment now
            initFragmentSketchComment();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentSketchCommentContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentSketchCommentContext, startServiceIntent);
         }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentSketchCommentBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourArrangementFragmentSketchCommentBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurArrangementSketch = intentExtras.getString("OurArrangementSketch","0");
                String tmpExtraOurArrangementSketchComment = intentExtras.getString("OurArrangementSketchComment","0");
                String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");
                String tmpExtraOurArrangementSketchCommentShareEnable = intentExtras.getString("OurArrangementSettingsSketchCommentShareEnable","0");
                String tmpExtraOurArrangementSketchCommentShareDisable = intentExtras.getString("OurArrangementSettingsSketchCommentShareDisable","0");
                String tmpExtraOurArrangementResetSketchCommentCountComment = intentExtras.getString("OurArrangementSettingsSketchCommentCountComment","0");
                String tmpExtraOurArrangementSketchCommentSendInBackgroundRefreshView = intentExtras.getString("OurArrangementSketchCommentSendInBackgroundRefreshView","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentSketchCommentContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();

                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketchComment != null && tmpExtraOurArrangementSketchComment.equals("1")) {
                    // update now comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentSketchCommentContext.getString(R.string.toastMessageCommentSketchNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketch != null && tmpExtraOurArrangementSketch.equals("1")) {

                    // check sketch arrangement and sketch arrangement update and show dialog skezch arrangement and sketch arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("sketch");

                    // go back to fragment sketch arrangement -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurArrangement.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_sketch_arrangement");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetSketchCommentCountComment != null && tmpExtraOurArrangementResetSketchCommentCountComment.equals("1")) {
                    // reset sketch comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentSketchCommentContext.getString(R.string.toastMessageArrangementResetSketchCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareDisable  != null && tmpExtraOurArrangementSketchCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchCommentContext.getString(R.string.toastMessageArrangementSketchCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareEnable  != null && tmpExtraOurArrangementSketchCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchCommentContext.getString(R.string.toastMessageArrangementSketchCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings have change -> refresh view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangementSketchCommentSendInBackgroundRefreshView != null &&  tmpExtraOurArrangementSketchCommentSendInBackgroundRefreshView.equals("1")) {
                    // comment send in background -> refresh view
                    refreshView = true;
                }

                if (refreshView) {
                    refreshFragmentView();
                }
            }
        }
    };


    // refresh the fragments view
    private void refreshFragmentView () {
        // refresh fragments view
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(fragmentSketchCommentThisFragmentContext).attach(fragmentSketchCommentThisFragmentContext).commit();
    }


    // inits the fragment for use
    private void initFragmentSketchComment() {

        // init the DB
        myDb = new DBAdapter(fragmentSketchCommentContext);

        // init the prefs
        prefs = fragmentSketchCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentSketchCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // init array for text description of scales levels
        evaluateSketchCommentScalesLevel = getResources().getStringArray(R.array.evaluateSketchCommentScalesLevel);

        // get choosen arrangement
        cursorChoosenSketchArrangement = myDb.getRowSketchOurArrangement(sketchArrangementServerDbIdToComment);

        // get all comments for choosen sketch arrangement
        cursorSketchArrangementAllComments = myDb.getAllRowsOurArrangementSketchComment(sketchArrangementServerDbIdToComment, "descending", 0);

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = String.format(getResources().getString(getResources().getIdentifier("subtitleFragmentSketchCommentText", "string", fragmentSketchCommentContext.getPackageName())), sketchArrangementNumberInListView);
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketchComment");

        // set visibility of FAB for this fragment
        ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "sketchComment");

        // build the view
        // set intro text for comment debetable goal
        TextView textViewIntroTextForCommentSketchArrangement = viewFragmentSketchComment.findViewById(R.id.introStructQuestionForCommentSketchArrangement);
        String tmpIntroTextForCommentSketchArrangementField = this.getResources().getString(R.string.introStructQuestionForCommentSketchArrangement);
        tmpIntroTextForCommentSketchArrangementField = String.format(tmpIntroTextForCommentSketchArrangementField, sketchArrangementNumberInListView);
        textViewIntroTextForCommentSketchArrangement.setText(tmpIntroTextForCommentSketchArrangementField);

        //textview for the comment intro
        TextView textCommentNumberIntro = viewFragmentSketchComment.findViewById(R.id.sketchArrangementCommentNumberIntro);
        String tmpCommentNumberIntro = this.getResources().getString(R.string.showSketchArrangementIntroText) + " " + sketchArrangementNumberInListView;
        textCommentNumberIntro.setText(tmpCommentNumberIntro);

        // textview for the author of sketch arrangement
        TextView tmpTextViewAuthorNameText = viewFragmentSketchComment.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentAuthorNameTextWithDate), cursorChoosenSketchArrangement.getString(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // check, sharing sketch comments enable?
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) == 0) {
            TextView textSketchCommentSharingIsDisable = viewFragmentSketchComment.findViewById(R.id.sketchCommentSharingIsDisable);
            textSketchCommentSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // textview for the sketch arrangement
        TextView textViewArrangement = viewFragmentSketchComment.findViewById(R.id.choosenSketchArrangement);
        String arrangement = cursorChoosenSketchArrangement.getString(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);

        // some comments for sketch arrangement available?
        if (cursorSketchArrangementAllComments.getCount() > 0) {

            // set row id of comment from db for timer update
            final Long rowIdForUpdate = cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID));

            //textview for the last actual comment intro
            TextView textLastActualSketchCommentIntro = viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentInfoText);
            textLastActualSketchCommentIntro.setText(this.getResources().getString(R.string.lastActualSketchCommentText));

            // position one for comment cursor
            cursorSketchArrangementAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY)) == 1) {
                TextView newEntryOfComment = viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentNewInfoText);
                String txtNewEntryOfComment = fragmentSketchCommentContext.getResources().getString(R.string.newEntryText);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurArrangementSketchComment(cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualSketchComment = viewFragmentSketchComment.findViewById(R.id.textAuthorNameLastActualSketchComment);
            String tmpAuthorName = cursorSketchArrangementAllComments.getString(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME));

            if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                tmpAuthorName = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentPersonalAuthorName);
            }
            String commentDate = EfbHelperClass.timestampToDateFormat(cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME)), "HH:mm");
            String tmpTextAuthorNameLastActualComment = String.format(fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            if (cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(getResources().getString(R.string.ourArrangementSketchCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
            tmpTextViewAuthorNameLastActualSketchComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualSketchComment = viewFragmentSketchComment.findViewById(R.id.textSendInfoLastActualSketchComment);
            if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 0) {

                String tmpTextSendInfoLastActualComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendInfo);
                tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
            }
            else if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 1) {
                // textview for status 1 of the last actual comment

                // check, sharing of sketch comments enable?
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) == 1) {
                    // check system time is in past or future?
                    Long writeTimeComment = cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME)); // write time is from sever
                    Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) * 60000; // make milliseconds from miutes
                    Long maxTimerTime = writeTimeComment+delayTime;
                    if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_TIMER_STATUS)) == 0) {
                        // calculate run time for timer in MILLISECONDS!!!
                        Long nowTime = System.currentTimeMillis();
                        Long localeTimeComment = cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME));
                        Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                        // set textview visible
                        tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);

                        // start the timer with the calculated milliseconds
                        if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                            new CountDownTimer(runTimeForTimer, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    // gernate count down timer
                                    String FORMAT = "%02d:%02d:%02d";
                                    String tmpTextSendInfoLastActualComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendDelayInfo);
                                    String tmpTime = String.format(FORMAT,
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                    // put count down to string
                                    String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                    // and show
                                    tmpTextViewSendInfoLastActualSketchComment.setText(tmpCountdownTimerString);
                                }

                                public void onFinish() {
                                    // count down is over -> show
                                    String tmpTextSendInfoLastActualComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendSuccsessfullInfo);
                                    tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                                    myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                }
                            }.start();

                        } else {
                            // no count down anymore -> show send successfull
                            String tmpTextSendInfoLastActualComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendSuccsessfullInfo);
                            tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else {
                        // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                        tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                        String tmpTextSendInfoLastActualComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }
                else { // sharing of sketch comments is disable! -> show text
                    String tmpTextSendInfoLastActualSketchComment = "";
                    tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                    if (prefs.getLong(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShareChangeTime, 0) < cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME))) {
                        // show send successfull, but no sharing
                        tmpTextSendInfoLastActualSketchComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendInfoSharingDisable);
                    }
                    else {
                        // show send successfull
                        tmpTextSendInfoLastActualSketchComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendSuccsessfullInfo);
                    }
                    tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualSketchComment);
                    myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                }
            }

            // show actual result struct question only when result > 0
            TextView textViewShowResultStructQuestion = viewFragmentSketchComment.findViewById(R.id.assessementValueForSketchComment);
            if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1)) > 0) {
                String actualResultStructQuestion = fragmentSketchCommentContext.getResources().getString(R.string.textOurArrangementSketchCommentActualResultStructQuestion);
                actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateSketchCommentScalesLevel[cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1)) - 1]);
                textViewShowResultStructQuestion.setText(HtmlCompat.fromHtml(actualResultStructQuestion, HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else { // result is =0; comes from server/ coach
                String actualResultStructQuestionFromCoach = fragmentSketchCommentContext.getResources().getString(R.string.textOurArrangementSketchCommentActualResultStructQuestionFromCoach);
                textViewShowResultStructQuestion.setText(actualResultStructQuestionFromCoach);
            }

            // textview for the comment text
            TextView tmpTextViewCommentText = viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentText);
            String tmpCommentText = cursorSketchArrangementAllComments.getString(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);

            // button goto overview comment sketch arrangement
            Button buttonGoToSketchArrangementOverview = viewFragmentSketchComment.findViewById(R.id.buttonSketchCommentBackToShowComment);
            // onClick listener goto comment sketch arrangement overview
            buttonGoToSketchArrangementOverview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_comment_for_sketch_arrangement");
                    intent.putExtra("db_id", sketchArrangementServerDbIdToComment);
                    intent.putExtra("arr_num", sketchArrangementNumberInListView);
                    getActivity().startActivity(intent);

                }
            });

        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualSketchCommentIntro = viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentInfoText);
            textLastActualSketchCommentIntro.setText(this.getResources().getString(R.string.lastActualSketchCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorSketchArrangementAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualSketchComment = viewFragmentSketchComment.findViewById(R.id.textAuthorNameLastActualSketchComment);
            tmpTextViewAuthorNameLastActualSketchComment.setText(this.getResources().getString(R.string.lastActualSketchCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewSketchCommentText = viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentText);
            tmpTextViewSketchCommentText.setVisibility(View.GONE);

            // button for overview sketch comment
            Button tmpButtonShowOverviewSketchComment = viewFragmentSketchComment.findViewById(R.id.buttonSketchCommentBackToShowComment);
            tmpButtonShowOverviewSketchComment.setVisibility(View.GONE);

            // textview border no sketch comment and info text
            TextView tmpBorderBetweenNoCommentAndInfoText = viewFragmentSketchComment.findViewById(R.id.borderBetweenNoCommentInfoAndInfoText);
            tmpBorderBetweenNoCommentAndInfoText.setVisibility(View.VISIBLE);

        }

        // set onClickListener for radio button in radio group question 1
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;

        for (int numberOfButtons=0; numberOfButtons < numberOfRadioButtonsStructQuestion; numberOfButtons++) {
            tmpRessourceName ="structQuestionOne_Answer" + (numberOfButtons+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentSketchCommentContext.getPackageName());

                tmpRadioButtonQuestion = viewFragmentSketchComment.findViewById(resourceId);
                tmpRadioButtonQuestion.setOnClickListener(new sketchCommentRadioButtonListener(numberOfButtons));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // textview for max comments and count comments
        TextView textViewMaxAndCount = viewFragmentSketchComment.findViewById(R.id.infoSketchCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextSketchCommentMaxLetters;
        // build text element max sketch comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextSketchCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) > 1 && commentLimitationBorder){
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextSketchCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
        }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentUnlimitedText);
        }

        // build text element count sketch comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentCountZero);
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0));

        // build text element delay time
        String tmpInfoTextDelaytimeSingluarPluaral = "";
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) == 0) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentDelaytimeNoDelay);
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) == 1) {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentDelaytimeSingular);
        }
        else {
            tmpInfoTextDelaytimeSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentDelaytimePlural);
            tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0));
        }

        // generate text comment max letters
        tmpInfoTextSketchCommentMaxLetters =  this.getResources().getString(R.string.infoTextSketchCommentMaxLetters);
        tmpInfoTextSketchCommentMaxLetters = String.format(tmpInfoTextSketchCommentMaxLetters, prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchCommentLetters, 0));

        // show info text
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextSketchCommentMaxLetters + " " + tmpInfoTextDelaytimeSingluarPluaral);

        // get max letters for edit text sketch comment
        final int tmpMaxLength = prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchCommentLetters, 10);

        // get textView to count input letters and init it
        final TextView textViewCountLettersCommentEditText = viewFragmentSketchComment.findViewById(R.id.countLettersSketchCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // get edit text field for sketch comment
        final EditText txtInputSketchArrangementComment = viewFragmentSketchComment.findViewById(R.id.inputSketchArrangementComment);


        // set hint text in edit text field
        String tmpHintTextForCommentField = this.getResources().getString(R.string.introFreeQuestionForCommentSketchArrangementHint);
        tmpHintTextForCommentField = String.format(tmpHintTextForCommentField, sketchArrangementNumberInListView);
        txtInputSketchArrangementComment.setHint(tmpHintTextForCommentField);


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
        txtInputSketchArrangementComment.addTextChangedListener(txtInputArrangementCommentTextWatcher);

        // set input filter max length for sketch comment field
        txtInputSketchArrangementComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

        // set text max comment/ actual comment
        TextView infoTextCountComment = viewFragmentSketchComment.findViewById(R.id.infoTextCountSketchComment);
        String tmpInfoTextCountCommentSingluarPluaralNoLimit;
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourArrangementSketchCommentCountCommentTextSingular);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourArrangementSketchCommentCountCommentTextPlural);
            tmpInfoTextCountCommentSingluarPluaralNoLimit = String.format(tmpInfoTextCountCommentSingluarPluaralNoLimit, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));

        }
        else {
            tmpInfoTextCountCommentSingluarPluaralNoLimit = this.getResources().getString(R.string.ourArrangementSketchCommentCountCommentTextNoLimit);
        }
        infoTextCountComment.setText(tmpInfoTextCountCommentSingluarPluaralNoLimit);

        // button send sketch comment
        Button buttonSendSketchArrangementComment = viewFragmentSketchComment.findViewById(R.id.buttonSendSketchArrangementComment);

        // onClick listener send sketch arrangement comment
        buttonSendSketchArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean sketchCommentNoError = true;
                TextView tmpErrorTextView;

                if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

                    // check result struct question
                    tmpErrorTextView = viewFragmentSketchComment.findViewById(R.id.errorStructQuestionForCommentSketchArrangement);
                    if (structQuestionResultSketchComment == 0 && tmpErrorTextView != null) {
                        sketchCommentNoError = false;
                        tmpErrorTextView.setVisibility(View.VISIBLE);

                    } else if (tmpErrorTextView != null) {
                        tmpErrorTextView.setVisibility(View.GONE);
                    }

                    // comment textfield -> insert new comment
                    tmpErrorTextView = viewFragmentSketchComment.findViewById(R.id.errorFreeQuestionForCommentSketchArrangement);
                    if (txtInputSketchArrangementComment.getText().toString().length() < 3 && tmpErrorTextView != null) {
                        sketchCommentNoError = false;
                        tmpErrorTextView.setVisibility(View.VISIBLE);
                    } else if (tmpErrorTextView != null) {
                        tmpErrorTextView.setVisibility(View.GONE);
                    }

                    // check for errors?
                    if (sketchCommentNoError) {

                        String commentText = txtInputSketchArrangementComment.getText().toString();
                        String userName = prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt");
                        Long commentTime = System.currentTimeMillis(); // first insert with local system time; will be replace with server time!
                        if (prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L) > 0) {
                            commentTime = prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0L); // this is server time, but not actual!
                        }
                        Long uploadTime = 0L;
                        Long localeTime = System.currentTimeMillis();
                        String blockId = cursorChoosenSketchArrangement.getString(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_BLOCK_ID));
                        Boolean newEntry = false;
                        Long dateOfSketchArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis());
                        int commentStatus = 0; // 0= not send to sever; 1= send to server; 4= external comment
                        int sketchArrangementServerId = cursorChoosenSketchArrangement.getInt(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID));
                        int timerStatus = 0;

                        // insert comment for sketch arrangement in DB
                        Long tmpDbId = myDb.insertRowOurArrangementSketchComment(commentText, structQuestionResultSketchComment, 0, 0, userName, localeTime, commentTime, uploadTime, blockId, newEntry, dateOfSketchArrangement, commentStatus, sketchArrangementServerId, timerStatus);

                        // increment sketch comment count
                        int countSketchCommentSum = prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) + 1;
                        prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, countSketchCommentSum);
                        prefsEditor.commit();

                        // send intent to service to start the service and send comment to server!
                        Intent startServiceIntent = new Intent(fragmentSketchCommentContext, ExchangeJobIntentServiceEfb.class);
                        // set command = "ask new data" on server
                        startServiceIntent.putExtra("com", "send_sketch_comment_arrangement");
                        startServiceIntent.putExtra("dbid", tmpDbId);
                        startServiceIntent.putExtra("receiverBroadcast", "");
                        // start service
                        ExchangeJobIntentServiceEfb.enqueueWork(fragmentSketchCommentContext, startServiceIntent);

                        // build intent to get back to OurArrangementFragmentSketchArrangement
                        Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com", "show_sketch_arrangement");
                        intent.putExtra("db_id", 0);
                        intent.putExtra("arr_num", 0);
                        intent.putExtra("eval_next", false);
                        getActivity().startActivity(intent);
                    }
                }
                else {
                    // delete text in edittextfield
                    txtInputSketchArrangementComment.setText("");

                    // case is closed -> show toast
                    String textCaseClose = fragmentSketchCommentContext.getString(R.string.toastOurArrangementSketchCommentCaseCloseToastText);
                    Toast toast = Toast.makeText(fragmentSketchCommentContext, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        // button back to sketch arrangement overview
        Button buttonBackToSketchArrangementOverview = viewFragmentSketchComment.findViewById(R.id.buttonSketchCommentBackToSketchArrangement);
        // onClick listener back to sketch arrangement overview
        buttonBackToSketchArrangementOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_sketch_arrangement");
                intent.putExtra("db_id", 0);
                intent.putExtra("arr_num", 0);
                getActivity().startActivity(intent);

            }
        });


        // End build the view
    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getSketchArrangementDbIdFromLink();

        if (tmpArrangementDbIdToComment > 0) {
            sketchArrangementServerDbIdToComment = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            sketchArrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getSketchArrangementNumberInListview();
            if (sketchArrangementNumberInListView < 1) sketchArrangementNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("current");
        }
    }


    // onClickListener for radioButtons in fragment layout evaluate
    public class sketchCommentRadioButtonListener implements View.OnClickListener {

        int radioButtonNumber;

        public sketchCommentRadioButtonListener (int number) {

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

            structQuestionResultSketchComment = tmpResultQuestion;
        }
    }


}