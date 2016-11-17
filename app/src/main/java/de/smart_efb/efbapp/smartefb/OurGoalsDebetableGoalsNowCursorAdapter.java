package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
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

    // reference to the DB
    private DBAdapter myDb;

    // actual debetable goal date, which is "at work"
    long actualDebetableGoalDate = 0;

    // number for count comments for debetable goals (12 numbers!)
    private String[] numberCountForAssessments = new String [12];

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;




    // Default constructor
    public OurGoalsDebetableGoalsNowCursorAdapter (Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);

        // init array for count comments
        numberCountForAssessments = context.getResources().getStringArray(R.array.ourGoalsDebetableGoalsCountAssessments);

        commentLimitationBorder = ((ActivityOurGoals)context).isCommentLimitationBorderSet("debetableGoals");

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // link to show comments
        Spanned showCommentsLinkTmp = null;
        // link to comment an debetable goal
        Spanned showCommentDebetableGoalLinkTmp = null;


        // text for new comment entry
        String tmpTextNewEntryComment = "";

        // open sharedPrefs
        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);


        if (cursor.isFirst() ) { // listview for first element? write intro text
            TextView tmpTextViewSketchIntroText = (TextView) view.findViewById(R.id.ourGoalsDebetableGoalsIntroText);
            String tmpTextIntroText = String.format(context.getResources().getString(R.string.ourGoalsDebetableGoalsIntroText), prefs.getString("authorOfSketchArrangement", "John Dow"), EfbHelperClass.timestampToDateFormat(prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis()), "dd.MM.yyyy"));
            tmpTextViewSketchIntroText.setText(tmpTextIntroText);
        }

        // put debetable goals number
        TextView tmpTextViewNumberOfDebetableGoal = (TextView) view.findViewById(R.id.listDebetableGoalNumberText);
        String tmpTextDebetableGoalNumber = context.getResources().getString(R.string.showDebetableGoalNumberText)+ " " + Integer.toString(cursor.getPosition()+1);
        tmpTextViewNumberOfDebetableGoal.setText(tmpTextDebetableGoalNumber);

        // check if debetable goal entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY)) == 1) {
            TextView newEntryOfDebetableGoal = (TextView) view.findViewById(R.id.listDebetableGoalNewText);
            String txtnewEntryOfDebetableGoal = context.getResources().getString(R.string.newEntryText);
            newEntryOfDebetableGoal.setText(txtnewEntryOfDebetableGoal);
            myDb.deleteStatusNewEntryOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // put debetable goal text
        TextView textViewDebetableGoal = (TextView) view.findViewById(R.id.listTextDebetableGoalNow);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewDebetableGoal.setText(title);

        // Show link for comment the debetable goals
        if (prefs.getBoolean("showCommentLinkDebetableGoals", false)) {

            // get from DB  all comments for choosen debetable goal (getCount)
            Cursor cursorDebetableGoalsAllComments = myDb.getAllRowsOurGoalsDebetableGoalsComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
            // generate the number of comments to show
            String tmpCountAssessments;
            int tmpIntCountComments = cursorDebetableGoalsAllComments.getCount();
            if (tmpIntCountComments > 10) {
                tmpCountAssessments = numberCountForAssessments[11];

            }
            else {
                tmpCountAssessments = numberCountForAssessments[cursorDebetableGoalsAllComments.getCount()];
            }

            // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
            if (cursorDebetableGoalsAllComments.getCount() > 0) {
                cursorDebetableGoalsAllComments.moveToFirst();
                if (cursorDebetableGoalsAllComments.getInt(cursorDebetableGoalsAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
                    tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(context, R.color.text_accent_color) + "'>"+ context.getResources().getString(R.string.newEntryText) + "</font>";
                }
            }


            // make link to comment arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_debetable_goal");

            // make link to show comment for arrangement
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_debetable_goal");;


            if (prefs.getInt("commentDebetableGoalsOurGoalsCountComment",0) < prefs.getInt("commentDebetableGoalsOurGoalsMaxComment",0) || !commentLimitationBorder) {

                showCommentDebetableGoalLinkTmp = Html.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourGoalsDebetableGoalsAssessmentsString", "string", context.getPackageName()))+"</a>");

            }
            else {
                showCommentDebetableGoalLinkTmp = Html.fromHtml(" ("+context.getResources().getString(context.getResources().getIdentifier("ourGoalsDebetableGoalsAssessmentsString", "string", context.getPackageName()))+")");

            }


            if (tmpIntCountComments == 0) {
                showCommentsLinkTmp = Html.fromHtml(tmpCountAssessments + " &middot;");
            }
            else {
                showCommentsLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountAssessments + "</a> " + tmpTextNewEntryComment + " &middot;");

            }


            // get textview from view
            TextView linkCommentAnDdebetableGoal = (TextView) view.findViewById(R.id.linkCommentAnDdebetableGoal);
            linkCommentAnDdebetableGoal.setText(TextUtils.concat(showCommentsLinkTmp, showCommentDebetableGoalLinkTmp));
            linkCommentAnDdebetableGoal.setMovementMethod(LinkMovementMethod.getInstance());

        }


    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst()) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_debetable_goals_now_first, parent, false);


        }
        else { // listview for next elements
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_debetable_goals_now, parent, false);

        }

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
