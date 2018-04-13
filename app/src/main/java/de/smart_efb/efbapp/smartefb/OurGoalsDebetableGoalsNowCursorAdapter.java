package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ich on 13.11.2016.
 */
public class OurGoalsDebetableGoalsNowCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for debetable goals (12 numbers!)
    private String[] numberCountForAssessments = new String [12];


    // Default constructor
    public OurGoalsDebetableGoalsNowCursorAdapter (Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init array for count comments
        numberCountForAssessments = context.getResources().getStringArray(R.array.ourGoalsDebetableGoalsCountAssessments);

        commentLimitationBorder = ((ActivityOurGoals)context).isCommentLimitationBorderSet("debetableGoals");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor.isFirst() ) { // listview for first element? write intro text

            TextView tmpTextViewDebetableIntroText = (TextView) view.findViewById(R.id.ourGoalsDebetableGoalsIntroText);
            String tmpTextIntroText = "";

            if (cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_CHANGE_TO)).equals(ConstansClassOurGoals.jointlyGoalStatusNothing)) {
                tmpTextIntroText = String.format(context.getResources().getString(R.string.ourGoalsDebetableGoalsIntroTextPlural), EfbHelperClass.timestampToDateFormat(prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis()), "dd.MM.yyyy"));
                tmpTextViewDebetableIntroText.setText(tmpTextIntroText);
            }
            else {
                tmpTextIntroText = String.format(context.getResources().getString(R.string.ourGoalsDebetableGoalsIntroTextChangeTo), EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_WRITE_TIME)), "dd.MM.yyyy"));
                tmpTextViewDebetableIntroText.setText(tmpTextIntroText);
            }
        }

        // is cursor last?
        if (cursor.isLast() ) { // listview for last element -> set gap to bottom of display
            // show gap to bottom of display
            TextView tmpGapToBottom = (TextView) view.findViewById(R.id.borderToBottomOfDisplayWhenNeeded);
            tmpGapToBottom.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // init the DB
        final DBAdapter myDb = new DBAdapter(context);

        View inflatedView;

        // link to show comments
        Spanned showCommentsLinkTmp = null;
        // link to comment an debetable goal
        Spanned showCommentDebetableGoalLinkTmp = null;
        // text for new comment entry
        String tmpTextNewEntryComment = "";

        if (cursor.isFirst()) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_debetable_goals_now_first, parent, false);
        }
        else { // listview for next elements
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_debetable_goals_now, parent, false);
        }

        // put debetable goals number
        TextView tmpTextViewNumberOfGoals = (TextView) inflatedView.findViewById(R.id.listDebetableGoalNumberText);
        String tmpTextDebetableGoalsNumber = context.getResources().getString(R.string.showDebetableGoalNumberText)+ " " + Integer.toString(cursor.getPosition()+1);
        tmpTextViewNumberOfGoals.setText(tmpTextDebetableGoalsNumber);

        // put author name
        TextView tmpTextViewDebetableAuthorNameText = (TextView) inflatedView.findViewById(R.id.listTextDebetableAuthorName);
        String tmpTextAuthorNameText = String.format(context.getResources().getString(R.string.ourGoalsDebetableGoalsAuthorNameTextWithDate), cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewDebetableAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

        // check if debetable goal entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY)) == 1) {
            TextView newEntryOfDebetableGoal = (TextView) inflatedView.findViewById(R.id.listDebetableGoalNewText);
            String txtnewEntryOfDebetableGoal = context.getResources().getString(R.string.newEntryText);
            newEntryOfDebetableGoal.setText(txtnewEntryOfDebetableGoal);
            myDb.deleteStatusNewEntryOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // put debetable goal text
        TextView textViewDebetableGoal = (TextView) inflatedView.findViewById(R.id.listTextDebetableGoalNow);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewDebetableGoal.setText(title);

        // Show link for comment the debetable goal
        TextView linkDebetableCommentAGoal = (TextView) inflatedView.findViewById(R.id.linkDebetableCommentAGoal);
        TextView linkShowDebetableCommentOfGoal = (TextView) inflatedView.findViewById(R.id.linkToShowDebetableCommentsOfGoal);

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentDebetableGoals, false)) {

            // generate difference text for comment anymore
            String tmpNumberCommentsPossible;
            int tmpDifferenceComments = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0);
            if (commentLimitationBorder) {
                if (tmpDifferenceComments > 0) {
                    if (tmpDifferenceComments > 1) { //plural comments
                        tmpNumberCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfDebetableCommentsPossiblePlural), tmpDifferenceComments);
                    } else { // singular comments
                        tmpNumberCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfDebetableCommentsPossibleSingular), tmpDifferenceComments);
                    }
                } else {
                    tmpNumberCommentsPossible = context.getString(R.string.infoTextNumberOfDebetableCommentsPossibleNoMore);
                }
            } else {
                tmpNumberCommentsPossible = context.getString(R.string.infoTextNumberOfDebetableCommentsPossibleNoBorder);
            }

            // get from DB  all comments for choosen debetable goal (getCount)
            Cursor cursorDebetableGoalsAllComments = myDb.getAllRowsOurGoalsDebetableGoalsComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID)), "descending");
            // generate the number of comments to show
            String tmpCountAssessments;
            int tmpIntCountComments = cursorDebetableGoalsAllComments.getCount();
            if (tmpIntCountComments > 10) {
                tmpCountAssessments = numberCountForAssessments[11];

            } else {
                tmpCountAssessments = numberCountForAssessments[cursorDebetableGoalsAllComments.getCount()];
            }

            // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
            if (cursorDebetableGoalsAllComments.getCount() > 0) {
                cursorDebetableGoalsAllComments.moveToFirst();
                if (cursorDebetableGoalsAllComments.getInt(cursorDebetableGoalsAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
                    tmpTextNewEntryComment = "<font color='" + ContextCompat.getColor(context, R.color.text_accent_color) + "'>" + context.getResources().getString(R.string.newEntryText) + "</font>";
                }
            }

            // make link to comment debetable goal
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_debetable_goal");

            // make link to show comment for debetable goal
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_debetable_goal");


            if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment,0) < prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment,0) || !commentLimitationBorder) {
                showCommentDebetableGoalLinkTmp = Html.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourGoalsDebetableGoalsAssessmentsString", "string", context.getPackageName()))+ " " + tmpNumberCommentsPossible + "</a>");
            }
            else {
                showCommentDebetableGoalLinkTmp = Html.fromHtml(context.getResources().getString(context.getResources().getIdentifier("ourGoalsDebetableGoalsAssessmentsString", "string", context.getPackageName()))+ " " + tmpNumberCommentsPossible);
            }
            linkDebetableCommentAGoal.setText(showCommentDebetableGoalLinkTmp);
            linkDebetableCommentAGoal.setMovementMethod(LinkMovementMethod.getInstance());

            if (tmpIntCountComments == 0) {
                showCommentsLinkTmp = Html.fromHtml(tmpCountAssessments);
            }
            else {
                showCommentsLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountAssessments + "</a> " + tmpTextNewEntryComment);

            }
            linkShowDebetableCommentOfGoal.setText(showCommentsLinkTmp);
            linkShowDebetableCommentOfGoal.setMovementMethod(LinkMovementMethod.getInstance());
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
