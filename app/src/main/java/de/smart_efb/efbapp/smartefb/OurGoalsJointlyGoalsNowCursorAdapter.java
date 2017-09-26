package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 16.10.2016.
 */
public class OurGoalsJointlyGoalsNowCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder = true;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for goal (12 numbers!)
    private String[] numberCountForComments = new String [12];


    // Default constructor
    public OurGoalsJointlyGoalsNowCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);

        // init array for count comments
        numberCountForComments = context.getResources().getStringArray(R.array.ourGoalsCountComments);

        commentLimitationBorder = ((ActivityOurGoals)context).isCommentLimitationBorderSet("jointlyGoals");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // is cursor first?
        if (cursor.isFirst() ) { // listview for first element
            TextView introTextfJointlyGoals = (TextView) view.findViewById(R.id.ourGoalsJointlyGoalsIntroText);
            String txtGoalNumber = context.getResources().getString(R.string.ourGoalsJointlyNowIntroText) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy");
            introTextfJointlyGoals.setText(txtGoalNumber);
        }

        // is cursor last?
        if (cursor.isLast() ) { // listview for last element -> set gap to bottom of display

            if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false)) { // show info of evaluation period when activated
                // set info text evaluation period
                TextView textViewEvaluationPeriodGapToTop = (TextView) view.findViewById(R.id.borderBetweenLastElementAndEvaluationInfo);
                textViewEvaluationPeriodGapToTop.setVisibility(View.VISIBLE);
                TextView textViewEvaluationPeriod = (TextView) view.findViewById(R.id.infoEvaluationTimePeriod);
                textViewEvaluationPeriod.setVisibility(View.VISIBLE);
                // make time and date variables
                String tmpBeginEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
                String tmpBeginEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "HH:mm");
                String tmpEndEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
                String tmpEndEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "HH:mm");
                int tmpEvaluationPeriodActive = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, 3600) / 3600; // make hours from seconds
                int tmpEvaluationPeriodPassiv = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, 3600) / 3600; // make hours from seconds
                String textEvaluationPeriod = String.format(context.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriod), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv );
                textViewEvaluationPeriod.setText(textEvaluationPeriod);
            }
            else { // show gap to bottom of display
                TextView tmpGapToBottom = (TextView) view.findViewById(R.id.borderToBottomOfDisplayWhenNeeded);
                tmpGapToBottom.setVisibility(View.VISIBLE);
            }
        }
     }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // link to show jointly goals comments
        Spanned showJointlyGoalsCommentsLinkTmp = null;
        // link to comment an jointly goal
        Spanned showJointlyGoalsCommentAGoalLinkTmp = null;
        
        // text for new comment entry
        String tmpTextNewEntryComment = "";
        
        View inflatedView;

        if (cursor.isFirst() ) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_jointly_goals_now_first, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_jointly_goals_now, parent, false);
        }

        // put text goal number
        TextView numberOfJointlyGoals = (TextView) inflatedView.findViewById(R.id.listGoalsNumberText);
        String txtJointlyGoalNumber = context.getResources().getString(R.string.showJointlyGoalTextNumber)+ " " + Integer.toString(cursor.getPosition()+1);
        numberOfJointlyGoals.setText(txtJointlyGoalNumber);

        // put author name
        TextView tmpTextViewAuthorNameText = (TextView) inflatedView.findViewById(R.id.listTextAuthorName);
        String tmpTextAuthorNameText = String.format(context.getResources().getString(R.string.ourGoalsAuthorNameTextWithDate), cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

        // check if goal entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY)) == 1) {
            TextView newEntryOfGoals = (TextView) inflatedView.findViewById(R.id.listGoalsNewGoalsText);
            String txtNewEntryOfGoals = context.getResources().getString(R.string.newEntryText);
            newEntryOfGoals.setText(txtNewEntryOfGoals);
            myDb.deleteStatusNewEntryOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // put goal text
        TextView textViewGoalText = (TextView) inflatedView.findViewById(R.id.listTextGoal);
        String goalText = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewGoalText.setText(goalText);

        // generate link for evaluate a goal
        final TextView linkEvaluateAnGoal = (TextView) inflatedView.findViewById(R.id.linkToEvaluateAnGoal);

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false)) { // evaluation on/off?

            // evaluation timezone expired?
            if (System.currentTimeMillis() < prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis())) {

                // make link to evaluate goals, when evaluation is possible for this goal
                if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE)) == 1) {

                    final Uri.Builder evaluateLinkBuilder = new Uri.Builder();
                    evaluateLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourgoals")
                            .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID))))
                            .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition() + 1))
                            .appendQueryParameter("com", "evaluate_an_jointly_goal");

                    final String tmpLinkTextForEvaluationActive = context.getResources().getString(context.getResources().getIdentifier("ourGoalsEvaluateStringNextPassivPeriod", "string", context.getPackageName()));
                    final String tmpTextEvaluationModulSwitchOnOff = context.getResources().getString(R.string.evaluateJointlyGoalEvaluateTextEvaluationModulSwitchOff);

                    // show time until next evaluation period
                    // calculate run time for timer in MILLISECONDS!!!
                    Long nowTime = System.currentTimeMillis();
                    Integer pausePeriod = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, 0) * 1000; // make milliseconds from seconds
                    Long runTimeForTimer = pausePeriod - (nowTime - prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, System.currentTimeMillis()));
                    // start the timer with the calculated milliseconds
                    if (runTimeForTimer > 0) {
                        new CountDownTimer(runTimeForTimer, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // generate count down timer
                                String FORMAT = "%02d:%02d:%02d";
                                String tmpTime = String.format(FORMAT,
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                // put count down to string
                                String tmpCountdownTimerString = String.format(tmpLinkTextForEvaluationActive, tmpTime);
                                // generate link for output
                                Spanned tmpCountdownTimerLink = Html.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">" + tmpCountdownTimerString + "</a>");

                                // and set to textview
                                linkEvaluateAnGoal.setText(tmpCountdownTimerLink);
                                linkEvaluateAnGoal.setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            public void onFinish() {
                                // change text to evaluation modul will switch off!
                                linkEvaluateAnGoal.setText(tmpTextEvaluationModulSwitchOnOff);
                            }

                        }.start();
                    }

                } else { // link is not possible, pause period, so do it with text

                    final String tmpTextNextEvaluationPeriod = context.getResources().getString(R.string.ourGoalsEvaluateStringNextActivePeriod);
                    final String tmpTextEvaluationModulSwitchOnOff = context.getResources().getString(R.string.evaluateJointlyGoalEvaluateTextEvaluationModulSwitchOn);

                    // show time until next evaluation period
                    // calculate run time for timer in MILLISECONDS!!!
                    Long nowTime = System.currentTimeMillis();
                    Integer pausePeriod = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, 0) * 1000; // make milliseconds from seconds
                    Long runTimeForTimer = pausePeriod - (nowTime - prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, System.currentTimeMillis()));
                    // start the timer with the calculated milliseconds
                    if (runTimeForTimer > 0) {
                        new CountDownTimer(runTimeForTimer, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // gernate count down timer
                                String FORMAT = "%02d:%02d:%02d";
                                String tmpTime = String.format(FORMAT,
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                // put count down to string
                                Spanned tmpCountdownTimerString = Html.fromHtml(String.format(tmpTextNextEvaluationPeriod, tmpTime));

                                // and set to textview
                                linkEvaluateAnGoal.setText(tmpCountdownTimerString);
                                linkEvaluateAnGoal.setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            public void onFinish() {
                                // change text to evaluation modul will switch on!
                                linkEvaluateAnGoal.setText(tmpTextEvaluationModulSwitchOnOff);
                            }
                        }.start();
                    }
                }
            }
            else { // evaluation time expired!
                String tmpEvaluationPeriodExpired = context.getResources().getString(R.string.evaluateJointlyGoalEvaluatePeriodExpired);
                linkEvaluateAnGoal.setText(tmpEvaluationPeriodExpired);
            }
        }
        else { // evaluation not possible/ deactivated
            linkEvaluateAnGoal.setVisibility(View.GONE);
        }


        // Show link for comment a goal and to show all comments for a goal
        TextView linkCommentAGoal = (TextView) inflatedView.findViewById(R.id.linkCommentAGoal);
        TextView linkShowCommentOfGoal = (TextView) inflatedView.findViewById(R.id.linkToShowCommentsOfGoals);

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, false)) {

           
            // get from DB  all comments for choosen goal (getCount)
            Cursor cursorGoalAllComments = myDb.getAllRowsOurGoalsJointlyGoalsComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)));



            Log.d("+++ Now Jointly Goal", "Server ID Goal:" + cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)) );

            Log.d("+++ Now Jointly Goal", "Anzahl Kommentare zum Goal:" + cursorGoalAllComments.getCount() );



            // generate the number of comments to show
            String tmpCountComments;
            int tmpIntCountComments = cursorGoalAllComments.getCount();
            if (cursorGoalAllComments.getCount() > 10) {
                tmpCountComments = numberCountForComments[11];
            }
            else {
                tmpCountComments = numberCountForComments[cursorGoalAllComments.getCount()];
            }

            // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
            if (cursorGoalAllComments.getCount() > 0) {
                cursorGoalAllComments.moveToFirst();
                if (cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY)) == 1) {
                    tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(context, R.color.text_accent_color) + "'>"+ context.getResources().getString(R.string.newEntryText) + "</font>";
                }
            }

            // generate difference text for comment anymore
            String tmpNumberCommentsPossible;
            int tmpDifferenceComments = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0);
            if (commentLimitationBorder) {
                if (tmpDifferenceComments > 0) {
                    if (tmpDifferenceComments > 1) { //plural comments
                        tmpNumberCommentsPossible = String.format(context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossiblePlural), tmpDifferenceComments);
                    } else { // singular comments
                        tmpNumberCommentsPossible = String.format(context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleSingular), tmpDifferenceComments);
                    }
                }
                else {
                    tmpNumberCommentsPossible = context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleNoMore);
                }
            }
            else {
                tmpNumberCommentsPossible = context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleNoBorder);
            }

            // make link to comment a goal
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_jointly_goal");

            // make link to show comment for goals
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_jointly_goal");;


            if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment,0) < prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment,0) || !commentLimitationBorder) {
                showJointlyGoalsCommentsLinkTmp = Html.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourGoalsCommentString", "string", context.getPackageName())) + " " + tmpNumberCommentsPossible + "</a>");
            }
            else {
                showJointlyGoalsCommentsLinkTmp = Html.fromHtml(context.getResources().getString(context.getResources().getIdentifier("ourGoalsCommentString", "string", context.getPackageName()))+ " " + tmpNumberCommentsPossible);
            }
            linkCommentAGoal.setText(showJointlyGoalsCommentsLinkTmp);
            linkCommentAGoal.setMovementMethod(LinkMovementMethod.getInstance());


            if (tmpIntCountComments == 0) {
                showJointlyGoalsCommentAGoalLinkTmp = Html.fromHtml(tmpCountComments);
            }
            else {
                showJointlyGoalsCommentAGoalLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountComments + "</a> " + tmpTextNewEntryComment);
            }
            linkShowCommentOfGoal.setText(showJointlyGoalsCommentAGoalLinkTmp);
            linkShowCommentOfGoal.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else { // comment and show comment are deactivated -> hide them
            linkCommentAGoal.setVisibility(View.GONE);
            linkShowCommentOfGoal.setVisibility(View.GONE);
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


}
