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
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentSketchCommentBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourArrangementFragmentSketchCommentBrodcastReceiver = new BroadcastReceiver() {

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
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentSketchCommentContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
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
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareDisable  != null && tmpExtraOurArrangementSketchCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchCommentContext.getString(R.string.toastMessageArrangementSketchCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareEnable  != null && tmpExtraOurArrangementSketchCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchCommentContext.getString(R.string.toastMessageArrangementSketchCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }

                if (refreshView) {
                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentSketchCommentThisFragmentContext).attach(fragmentSketchCommentThisFragmentContext).commit();
                }
            }
        }
    };


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
        cursorSketchArrangementAllComments = myDb.getAllRowsOurArrangementSketchComment(sketchArrangementServerDbIdToComment);

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = String.format(getResources().getString(getResources().getIdentifier("subtitleFragmentSketchCommentText", "string", fragmentSketchCommentContext.getPackageName())), sketchArrangementNumberInListView);
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketchComment");

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.sketchArrangementCommentNumberIntro);
        String tmpCommentNumberIntro = this.getResources().getString(R.string.showSketchArrangementIntroText) + " " + sketchArrangementNumberInListView;
        textCommentNumberIntro.setText(tmpCommentNumberIntro);

        // textview for the author of sketch arrangement
        TextView tmpTextViewAuthorNameText = (TextView) viewFragmentSketchComment.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentAuthorNameTextWithDate), cursorChoosenSketchArrangement.getString(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

        // generate back link "zurueck zu allen Entwuerfen"
        Uri.Builder backLinkBuilder = new Uri.Builder();
        backLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourarrangement")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_sketch_arrangement");
        TextView linkShowCommentBackLink = (TextView) viewFragmentSketchComment.findViewById(R.id.arrangementShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + backLinkBuilder.build().toString() + "\">"+fragmentSketchCommentContext.getResources().getString(fragmentSketchCommentContext.getResources().getIdentifier("ourArrangementBackLinkToSketchArrangement", "string", fragmentSketchCommentContext.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

        // check, sharing sketch comments enable?
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) == 0) {
            TextView textSketchCommentSharingIsDisable = (TextView) viewFragmentSketchComment.findViewById(R.id.sketchCommentSharingIsDisable);
            textSketchCommentSharingIsDisable.setVisibility (View.VISIBLE);
        }

        // textview for the sketch arrangement
        TextView textViewArrangement = (TextView) viewFragmentSketchComment.findViewById(R.id.choosenSketchArrangement);
        String arrangement = cursorChoosenSketchArrangement.getString(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);

        // some comments for arrangement available?
        if (cursorSketchArrangementAllComments.getCount() > 0) {

            //textview for the last actual comment intro
            TextView textLastActualSketchCommentIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentInfoText);
            textLastActualSketchCommentIntro.setText(this.getResources().getString(R.string.lastActualSketchCommentText));

            // position one for comment cursor
            cursorSketchArrangementAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY)) == 1) {
                TextView newEntryOfComment = (TextView) viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentNewInfoText);
                String txtNewEntryOfComment = fragmentSketchCommentContext.getResources().getString(R.string.newEntryText);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurArrangementSketchComment(cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualSketchComment = (TextView) viewFragmentSketchComment.findViewById(R.id.textAuthorNameLastActualSketchComment);
            String tmpAuthorName = cursorSketchArrangementAllComments.getString(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME));

            if (tmpAuthorName.equals(prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"))) {
                tmpAuthorName = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentPersonalAuthorName);
            }
            String commentDate = EfbHelperClass.timestampToDateFormat(cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME)), "dd.MM.yyyy");;
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME)), "HH:mm");;
            String tmpTextAuthorNameLastActualComment = String.format(fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            tmpTextViewAuthorNameLastActualSketchComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

            // textview for status 0 of the last actual comment
            final TextView tmpTextViewSendInfoLastActualSketchComment = (TextView) viewFragmentSketchComment.findViewById(R.id.textSendInfoLastActualSketchComment);
            if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 0) {

                String tmpTextSendInfoLastActualComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendInfo);
                tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
            }
            else if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 1) {
                // textview for status 1 of the last actual comment

                // check, sharing of sketch comments enable?
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) == 1) {

                    // set textview visible
                    tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);

                    // calculate run time for timer in MILLISECONDS!!!
                    Long nowTime = System.currentTimeMillis();
                    Long writeTimeComment = cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME));
                    Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) * 60000; // make milliseconds from miutes
                    Long runTimeForTimer = delayTime - (nowTime - writeTimeComment);
                    // start the timer with the calculated milliseconds
                    if (runTimeForTimer > 0) {
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
                            }
                        }.start();

                    } else {
                        // no count down anymore -> show send successfull
                        String tmpTextSendInfoLastActualComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementSketchCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                    }
                }
                else { // sharing of sketch comments is disable! -> show text
                    String tmpTextSendInfoLastActualSketchComment = "";
                    tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                    if (prefs.getLong(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShareChangeTime, 0) < cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME))) {
                        // show send successfull, but no sharing
                        tmpTextSendInfoLastActualSketchComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementCommentSendInfoSharingDisable);
                    }
                    else {
                        // show send successfull
                        tmpTextSendInfoLastActualSketchComment = fragmentSketchCommentContext.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                    }
                    tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualSketchComment);
                }
            }

            // show actual result struct question only when result > 0
            TextView textViewShowResultStructQuestion = (TextView) viewFragmentSketchComment.findViewById(R.id.assessementValueForSketchComment);
            if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1)) > 0) {
                String actualResultStructQuestion = fragmentSketchCommentContext.getResources().getString(R.string.textOurArrangementSketchCommentActualResultStructQuestion);
                actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateSketchCommentScalesLevel[cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1)) - 1]);
                textViewShowResultStructQuestion.setText(Html.fromHtml(actualResultStructQuestion));
            } else { // result is =0; comes from server/ coach
                String actualResultStructQuestionFromCoach = fragmentSketchCommentContext.getResources().getString(R.string.textOurArrangementSketchCommentActualResultStructQuestionFromCoach);
                textViewShowResultStructQuestion.setText(actualResultStructQuestionFromCoach);
            }

            // textview for the comment text
            TextView tmpTextViewCommentText = (TextView) viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentText);
            String tmpCommentText = cursorSketchArrangementAllComments.getString(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);

            // get textview for Link to Show all comments
            TextView tmpTextViewLInkToShowAllSketchComment = (TextView) viewFragmentSketchComment.findViewById(R.id.commentLinkToShowAllSketchComments);

            // more than one comment available?
            if (cursorSketchArrangementAllComments.getCount() > 1) {

                // generate link to show all comments
                Uri.Builder sketchCommentLinkBuilder = new Uri.Builder();
                sketchCommentLinkBuilder.scheme("smart.efb.deeplink")
                        .authority("linkin")
                        .path("ourarrangement")
                        .appendQueryParameter("db_id", Integer.toString(sketchArrangementServerDbIdToComment))
                        .appendQueryParameter("arr_num", Integer.toString(sketchArrangementNumberInListView))
                        .appendQueryParameter("com", "show_comment_for_sketch_arrangement");

                if (cursorSketchArrangementAllComments.getCount() == 2) {
                    String tmpLinkStringShowAllSketchComments = String.format(fragmentSketchCommentContext.getResources().getString(fragmentSketchCommentContext.getResources().getIdentifier("ourArrangementSketchCommentLinkToShowAllCommentsSingular", "string", fragmentSketchCommentContext.getPackageName())),cursorSketchArrangementAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllSketchComment.setText(Html.fromHtml("<a href=\"" + sketchCommentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllSketchComments + "</a>"));
                }
                else {
                    String tmpLinkStringShowAllSketchComments = String.format(fragmentSketchCommentContext.getResources().getString(fragmentSketchCommentContext.getResources().getIdentifier("ourArrangementSketchCommentLinkToShowAllCommentsPlural", "string", fragmentSketchCommentContext.getPackageName())),cursorSketchArrangementAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllSketchComment.setText(Html.fromHtml("<a href=\"" + sketchCommentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllSketchComments + "</a>"));
                }
                tmpTextViewLInkToShowAllSketchComment.setMovementMethod(LinkMovementMethod.getInstance());

            }
            else {
                // no comment anymore
                String tmpLinkStringShowAllSketchComments = fragmentSketchCommentContext.getResources().getString(fragmentSketchCommentContext.getResources().getIdentifier("ourArrangementSketchCommentLinkToShowAllCommentsNotAvailable", "string", fragmentSketchCommentContext.getPackageName()));
                tmpTextViewLInkToShowAllSketchComment.setText(tmpLinkStringShowAllSketchComments);
            }
        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualSketchCommentIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentInfoText);
            textLastActualSketchCommentIntro.setText(this.getResources().getString(R.string.lastActualSketchCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorSketchArrangementAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualSketchComment = (TextView) viewFragmentSketchComment.findViewById(R.id.textAuthorNameLastActualSketchComment);
            tmpTextViewAuthorNameLastActualSketchComment.setText(this.getResources().getString(R.string.lastActualSketchCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewSketchCommentText = (TextView) viewFragmentSketchComment.findViewById(R.id.lastActualSketchCommentText);
            tmpTextViewSketchCommentText.setVisibility(View.GONE);
        }

        // set onClickListener for radio button in radio group question 1
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;

        for (int numberOfButtons=0; numberOfButtons < numberOfRadioButtonsStructQuestion; numberOfButtons++) {
            tmpRessourceName ="structQuestionOne_Answer" + (numberOfButtons+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentSketchCommentContext.getPackageName());

                tmpRadioButtonQuestion = (RadioButton) viewFragmentSketchComment.findViewById(resourceId);
                tmpRadioButtonQuestion.setOnClickListener(new sketchCommentRadioButtonListener(numberOfButtons));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // textview for max comments and count comments
        TextView textViewMaxAndCount = (TextView) viewFragmentSketchComment.findViewById(R.id.infoSketchCommentMaxAndCount);
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
        final TextView textViewCountLettersCommentEditText = (TextView) viewFragmentSketchComment.findViewById(R.id.countLettersSketchCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // get edit text field for sketch comment
        final EditText txtInputSketchArrangementComment = (EditText) viewFragmentSketchComment.findViewById(R.id.inputSketchArrangementComment);

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

        // button send comment
        Button buttonSendSketchArrangementComment = (Button) viewFragmentSketchComment.findViewById(R.id.buttonSendSketchArrangementComment);

        // onClick listener send sketch arrangement comment
        buttonSendSketchArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean sketchCommentNoError = true;
                TextView tmpErrorTextView;

                // check result struct question
                tmpErrorTextView = (TextView) viewFragmentSketchComment.findViewById(R.id.errorStructQuestionForCommentSketchArrangement);
                if ( structQuestionResultSketchComment == 0 && tmpErrorTextView != null) {
                    sketchCommentNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);

                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // comment textfield -> insert new comment
                tmpErrorTextView = (TextView) viewFragmentSketchComment.findViewById(R.id.errorFreeQuestionForCommentSketchArrangement);
                if (txtInputSketchArrangementComment.getText().toString().length() < 3 && tmpErrorTextView != null) {
                    sketchCommentNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check for errors?
                if (sketchCommentNoError) {

                    // insert comment for sketch arrangement in DB
                    Long tmpDbId = myDb.insertRowOurArrangementSketchComment(txtInputSketchArrangementComment.getText().toString(), structQuestionResultSketchComment, 0, 0, prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"), System.currentTimeMillis(), 0, cursorChoosenSketchArrangement.getString(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_BLOCK_ID)), true, prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement , System.currentTimeMillis()), 0, cursorChoosenSketchArrangement.getInt(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));

                    // increment sketch comment count
                    int countSketchCommentSum = prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0) + 1;
                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, countSketchCommentSum);
                    prefsEditor.commit();


                    // send intent to service to start the service and send comment to server!
                    Intent startServiceIntent = new Intent(fragmentSketchCommentContext, ExchangeServiceEfb.class);
                    startServiceIntent.putExtra("com","send_sketch_comment_arrangement");
                    startServiceIntent.putExtra("dbid",tmpDbId);
                    fragmentSketchCommentContext.startService(startServiceIntent);

                    // build intent to get back to OurArrangementFragmentSketchArrangement
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_sketch_arrangement");
                    getActivity().startActivity(intent);
                }
            }
        });

        // button abbort
        Button buttonAbbortArrangementComment = (Button) viewFragmentSketchComment.findViewById(R.id.buttonAbortSketchComment);
        // onClick listener button abbort
        buttonAbbortArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_sketch_arrangement");
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