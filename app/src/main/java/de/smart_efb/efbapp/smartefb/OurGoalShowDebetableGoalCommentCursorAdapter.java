package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 22.11.2016.
 */
public class OurGoalShowDebetableGoalCommentCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    final Context contextForActivity;

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] evaluateDebetableGoalCommentScalesLevel = new String [countScalesLevel];

    // DB-Id of debetable goal
    int debetableGoalDbIdToShow = 0;

    // debetable goal number in list view of fragment show debetable goal
    int debetableGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // data for debetable goal
    Cursor choosenDebetableGoal;

    // shared prefs for the debetable comments
    SharedPreferences prefs;

    // count headline number for debetable comments
    int countCommentHeadlineNumber = 0;


    // own constructor!!!
    public OurGoalShowDebetableGoalCommentCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInLIst, Boolean commentsLimitation, Cursor currentGoal) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // context of activity OurGoals
        contextForActivity = context;

        // set db id for debetable goal
        debetableGoalDbIdToShow = dbId;

        // set debetable goal number in list view
        debetableGoalNumberInListView = numberInLIst;

        // set comments limitation
        commentLimitationBorder = commentsLimitation;

        // set choosen debetable goal
        choosenDebetableGoal = currentGoal;

        // init array for text description of scales levels
        evaluateDebetableGoalCommentScalesLevel = context.getResources().getStringArray(R.array.debetableGoalsCommentScalesLevel);

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // is cursor position first?
        if (cursor.isFirst()) {

            // set text debetable goal intro
            TextView textViewShowDebetableGoalsIntro = (TextView) view.findViewById(R.id.goalsShowDebetableGoalIntro);
            String txtGoalIntro = contextForActivity.getResources().getString(R.string.showDebetableGoalsIntroText)+ " " + debetableGoalNumberInListView;
            textViewShowDebetableGoalsIntro.setText(txtGoalIntro);

            // textview for the author of debetable goal
            TextView tmpTextViewAuthorNameText = (TextView) view.findViewById(R.id.textAuthorName);
            String tmpTextAuthorNameText = String.format(view.getResources().getString(R.string.ourGoalsDebetableGoalsAuthorNameTextWithDate), choosenDebetableGoal.getString(choosenDebetableGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
            tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

            // make link back to show debetable goals
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_debetable_goals_now");
            TextView linkShowCommentBackLink = (TextView) view.findViewById(R.id.goalsShowDebetableGoalCommentBackLink);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourGoalsBackLinkToDebetableGoals", "string", context.getPackageName()))+"</a>");
            linkShowCommentBackLink.setText(tmpBackLink);
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());
            
            // check, sharing debetable comments enable?
            if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentShare, 0) == 0) {
                TextView textDebetableCommentSharingIsDisable = (TextView) view.findViewById(R.id.debetableCommentSharingIsDisable);
                textDebetableCommentSharingIsDisable.setVisibility (View.VISIBLE);
            }
            
            // show choosen goal
            TextView textViewShowChoosenDebetableGoal = (TextView) view.findViewById(R.id.choosenDebetableGoal);
            textViewShowChoosenDebetableGoal.setText(choosenDebetableGoal.getString(choosenDebetableGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL)));
        }

        // generate onclicklistener for Button "zurueck zu den strittigen zielen"
        if (cursor.isLast()) {

            // button abbort "zurueck zu den strittigen zielen"
            Button buttonBackToDebetableGoals = (Button) view.findViewById(R.id.buttonAbortShowDebetableGoalComment);

            // onClick listener abbort button
            buttonBackToDebetableGoals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_debetable_goals_now");
                    contextForActivity.startActivity(intent);
                }
            });

            // generate link for sort sequence change
            TextView textViewChangeSortSequence = (TextView) view.findViewById(R.id.linkToChangeSortSequenceOfCommentList);
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(debetableGoalDbIdToShow))
                    .appendQueryParameter("arr_num", Integer.toString(debetableGoalNumberInListView))
                    .appendQueryParameter("eval_next", Boolean.toString(false))
                    .appendQueryParameter("com", "change_sort_sequence_debetable_goal_comment");

            String tmpLinkTextChangeSortSequence;
            if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "descending").equals("descending")) {
                tmpLinkTextChangeSortSequence = context.getResources().getString(context.getResources().getIdentifier("ourGoalsShowDebetableCommentLinkChangeSortSequenceOfDebetableCommentDescending", "string", context.getPackageName()));
            }
            else {
                tmpLinkTextChangeSortSequence = context.getResources().getString(context.getResources().getIdentifier("ourGoalsShowDebetableCommentLinkChangeSortSequenceOfDebetableCommentAscending", "string", context.getPackageName()));
            }
            Spanned tmpSortLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkTextChangeSortSequence + "</a>");
            textViewChangeSortSequence.setText(tmpSortLink);
            textViewChangeSortSequence.setMovementMethod(LinkMovementMethod.getInstance());

            // textview for max comments and count comments
            TextView textViewMaxAndCount = (TextView) view.findViewById(R.id.infoDebetableCommentMaxAndCount);
            String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
            // build text element max debetable goal comment
            if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) == 1 && commentLimitationBorder) {
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextDebetableGoalCommentMaxSingular), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));
            }
            else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) > 1 && commentLimitationBorder){
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextDebetableGoalCommentMaxPlural), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));
            }
            else {
                tmpInfoTextMaxSingluarPluaral = context.getString(R.string.infoTextDebetableGoalCommentUnlimitedText);
            }

            // build text element count debetable goal comment count
            if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0) == 0) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextDebetableGoalCommentCountZero);
            }
            else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0) == 1) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextDebetableGoalCommentCountSingular);
            }
            else {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextDebetableGoalCommentCountPlural);
            }
            tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0));

            // build text element delay time
            String tmpInfoTextDelaytimeSingluarPluaral = "";
            if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) == 0) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextDebetableCommentDelaytimeNoDelay);
            }
            else if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) == 1) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextDebetableCommentDelaytimeSingular);
            }
            else {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextDebetableCommentDelaytimePlural);
                tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0));
            }

            // generate text comment max letters
            tmpInfoTextCommentMaxLetters =  context.getString(R.string.infoTextDebetableGoalCommentMaxLetters);
            tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableLetters, 0));

            // show info text
            textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " + tmpInfoTextDelaytimeSingluarPluaral);
        }
    }


    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup parent) {

        // init the DB
        final DBAdapter myDb = new DBAdapter(mContext);

        View inflatedView;

        final Context context = mContext;

        // set row id of comment from db for timer update
        final Long rowIdForUpdate = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment_first, parent, false);
            countCommentHeadlineNumber = 1;
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment_firstandlast, parent, false);
            countCommentHeadlineNumber ++;
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment_last, parent, false);
            countCommentHeadlineNumber ++;
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment, parent, false);
            countCommentHeadlineNumber ++;
        }

        // set debetable comment information to the view
        // set debetable comment headline
        TextView textViewShowActualDebetableCommentHeadline = (TextView) inflatedView.findViewById(R.id.actualDebetableCommentInfoText);
        String actualCommentHeadline = context.getResources().getString(R.string.showDebetableCommentHeadlineWithNumber) + " " + countCommentHeadlineNumber;
        if (cursor.isFirst()) { // set text newest comment
            if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending").equals("descending")) {
                actualCommentHeadline = actualCommentHeadline + " " + context.getResources().getString(R.string.showDebetableCommentHeadlineWithNumberExtraNewest);
            }
            else {
                actualCommentHeadline = actualCommentHeadline + " " + context.getResources().getString(R.string.showDebetableCommentHeadlineWithNumberExtraOldest);
            }
        }
        textViewShowActualDebetableCommentHeadline.setText(actualCommentHeadline);

        // check if debetable comment entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfCommentGoal = (TextView) inflatedView.findViewById(R.id.actualDebetableCommentNewInfoText);
            String txtnewEntryOfCommentGoal = context.getResources().getString(R.string.newEntryText);
            newEntryOfCommentGoal.setText(txtnewEntryOfCommentGoal);

            // delet status new entry in db
            myDb.deleteStatusNewEntryOurGoalsDebetableGoalsComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // textview for the author and date
        TextView tmpTextViewAuthorNameLastActualComment = (TextView) inflatedView.findViewById(R.id.textAuthorNameActualDebetableComment);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME));
        if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
            tmpAuthorName = context.getResources().getString(R.string.ourGoalsDebetableCommentPersonalAuthorName);
        }
        String commentDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");;
        String commentTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME)), "HH:mm");;
        String tmpTextAuthorNameLastActualComment = String.format(context.getResources().getString(R.string.ourGoalsShowDebetableCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
        if (cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(context.getResources().getString(R.string.ourGoalsShowDebetableCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
        tmpTextViewAuthorNameLastActualComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

        // textview for status 0 of the last actual comment
        final TextView tmpTextViewSendInfoLastActualDebetableComment = (TextView) inflatedView.findViewById(R.id.textSendInfoActualDebetableComment);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS)) == 0) {

            String tmpTextSendInfoLastActualDebetableComment = context.getResources().getString(R.string.ourGoalsShowDebetableCommentSendInfo);
            tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
            tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualDebetableComment);

        } else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_STATUS)) == 1) {
            // textview for status 1 of the last actual comment

            // check, sharing of debetable comments enable?
            if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentShare, 0) == 1) {
                Long writeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME));
                Integer delayTime = prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                Long maxTimerTime = writeTimeComment+delayTime;
                if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_TIMER_STATUS)) == 0) { // check system time is in past and timer status is run!

                    // calculate run time for timer in MILLISECONDS!!!
                    Long nowTime = System.currentTimeMillis();
                    Long localeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_LOCAL_TIME));
                    Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                    // set textview visible
                    tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);

                    // start the timer with the calculated milliseconds
                    if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                        new CountDownTimer(runTimeForTimer, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // gernate count down timer
                                String FORMAT = "%02d:%02d:%02d";
                                String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourGoalsShowDebetableCommentSendDelayInfo);
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
                                String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                                tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                                myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                            }
                        }.start();

                    } else {
                        // no count down anymore -> show send successfull
                        String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }
                else { // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                    tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
                    String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                    tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                    myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                }
            }
            else { // sharing of debetable comments is disable! -> show text
                String tmpTextSendInfoLastActualDebetableComment = "";
                tmpTextViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);

                if (prefs.getLong(ConstansClassOurGoals.namePrefsDebetableCommentShareChangeTime, 0) < cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME))) {
                    // show send successfull, but no sharing
                    tmpTextSendInfoLastActualDebetableComment = context.getResources().getString(R.string.ourGoalsShowDebetableCommentSendInfoSharingDisable);
                }
                else {
                    // show send successfull
                    tmpTextSendInfoLastActualDebetableComment = context.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                }
                tmpTextViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualDebetableComment);
                myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
            }
        }

        // show actual result struct question only when result > 0
        TextView textViewShowResultStructQuestion = (TextView) inflatedView.findViewById(R.id.assessementValueForDebetableComment);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1)) > 0) {
            String actualResultStructQuestion = context.getResources().getString(R.string.textOurGoalsShowDebetableCommentActualResultStructQuestion);
            actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateDebetableGoalCommentScalesLevel[cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1)) - 1]);
            textViewShowResultStructQuestion.setText(Html.fromHtml(actualResultStructQuestion));
        } else { // result is =0; comes from server/ coach
            String actualResultStructQuestionFromCoach = context.getResources().getString(R.string.textOurGoalsShowDebetableCommentActualResultStructQuestionFromCoach);
            textViewShowResultStructQuestion.setText(actualResultStructQuestionFromCoach);
        }

        // show actual debetable comment
        TextView textViewShowActualComment = (TextView) inflatedView.findViewById(R.id.listActualTextDebetableComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // make link to comment a debetable goal only when comments possible
        TextView linkToCommentAGoal = (TextView) inflatedView.findViewById(R.id.linkToCommentADebetableGoal);
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment,0) < prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment,0) || !commentLimitationBorder) {

            // generate difference text for comment anymore
            String tmpNumberDebetableCommentsPossible;
            int tmpDifferenceComments = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0);
            if (commentLimitationBorder) {
                if (tmpDifferenceComments > 0) {
                    if (tmpDifferenceComments > 1) { //plural comments
                        tmpNumberDebetableCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfShowDebetableCommentsPossiblePlural), tmpDifferenceComments);
                    } else { // singular comments
                        tmpNumberDebetableCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfShowDebetableCommentsPossibleSingular), tmpDifferenceComments);
                    }
                }
                else {
                    tmpNumberDebetableCommentsPossible = context.getString(R.string.infoTextNumberOfShowDebetableCommentsPossibleNoMore);
                }
            }
            else {
                tmpNumberDebetableCommentsPossible = context.getString(R.string.infoTextNumberOfShowDebetableCommentsPossibleNoBorder);
            }

            Uri.Builder debetableCommentLinkBuilder = new Uri.Builder();
            debetableCommentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(debetableGoalDbIdToShow))
                    .appendQueryParameter("arr_num", Integer.toString(debetableGoalNumberInListView))
                    .appendQueryParameter("com", "comment_an_debetable_goal");
            String tmpLinkTextForCommentAGoal = String.format(context.getResources().getString(context.getResources().getIdentifier("ourGoalsShowDdebetableCommentLinkToCommentAGoal", "string", context.getPackageName())), debetableGoalNumberInListView);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + debetableCommentLinkBuilder.build().toString() + "\">" + tmpLinkTextForCommentAGoal + " " + tmpNumberDebetableCommentsPossible + "</a>");
            linkToCommentAGoal.setText(tmpBackLink);
            linkToCommentAGoal.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            linkToCommentAGoal.setVisibility(View.GONE);
        }

        // close DB connection
        myDb.close();

        return inflatedView;
    }


    // Turn off view recycling in listview, because there are different views (first, normal, last)
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
