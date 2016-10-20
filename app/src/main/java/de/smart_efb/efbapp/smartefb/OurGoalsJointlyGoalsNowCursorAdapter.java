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
 * Created by ich on 16.10.2016.
 */
public class OurGoalsJointlyGoalsNowCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;


    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForComments = new String [12];


    // Default constructor
    public OurGoalsJointlyGoalsNowCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);

        // init array for count comments
        //numberCountForComments = context.getResources().getStringArray(R.array.ourArrangementCountComments);

        //commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("current");

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // link to show jointly goals comments
        Spanned showJointlyGoalsCommentsLinkTmp = null;
        // link to comment an jointly goal
        Spanned showJointlyGoalsCommentAnGoalLinkTmp = null;
        // link to evaluate jointly goal
        Spanned showJointlyGoalsEvaluateLinkTmp = null;

        // text for new comment entry
        String tmpTextNewEntryComment = "";

        // open sharedPrefs
        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);

        if (cursor.isFirst() ) { // listview for first element
            TextView introTextfJointlyGoals = (TextView) view.findViewById(R.id.ourGoalsJointlyGoalsIntroText);
            String txtArrangementNumber = context.getResources().getString(R.string.ourGoalsJointlyNowIntroText) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis()), "dd.MM.yyyy");
            introTextfJointlyGoals.setText(txtArrangementNumber);
        }


        // put text goal number
        TextView numberOfJointlyGoals = (TextView) view.findViewById(R.id.listGoalsNumberText);
        String txtJointlyGoalNumber = context.getResources().getString(R.string.showJointlyGoalTextNumber)+ " " + Integer.toString(cursor.getPosition()+1);
        numberOfJointlyGoals.setText(txtJointlyGoalNumber);

        // check if goal entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY)) == 1) {

            TextView newEntryOfGoal = (TextView) view.findViewById(R.id.listGoalsNewJointlyGoalText);
            String txtNewEntryOfGoal = context.getResources().getString(R.string.newEntryTextOurGoal);
            newEntryOfGoal.setText(txtNewEntryOfGoal);
            myDb.deleteStatusNewEntryOurGoals(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));

        }

        // put goal text
        TextView textViewGoalText = (TextView) view.findViewById(R.id.listTextGoal);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewGoalText.setText(title);





        // generate link for evaluate an jointly goal
        if (prefs.getBoolean("showEvaluateLinkJointlyGoals", false)) {

            // make link to evaluate jointly goal, when evaluation is possible for this goal
            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_EVALUATE_POSSIBLE)) == 1) {
                Uri.Builder evaluateLinkBuilder = new Uri.Builder();
                evaluateLinkBuilder.scheme("smart.efb.ilink_goal_comment")
                        .authority("www.smart-efb.de")
                        .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                        .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition() + 1))
                        .appendQueryParameter("com", "evaluate_an_jointly_goal");

                showJointlyGoalsEvaluateLinkTmp = Html.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">" + context.getResources().getString(context.getResources().getIdentifier("ourGoalsEvaluateJointlyGoalString", "string", context.getPackageName())) + "</a> &middot; ");
            } else { // link is not possible, so do it with text

                showJointlyGoalsEvaluateLinkTmp = Html.fromHtml("(" + context.getResources().getString(context.getResources().getIdentifier("ourGoalsEvaluateJointlyGoalString", "string", context.getPackageName())) + ") &middot; ");
            }
        }

        // Show link for comment in our goal
        if (prefs.getBoolean("showCommentLinkJointlyGoals", false)) {

            /********************************** TODO DB-Funktion erstellen ---------------------------------
            // get from DB  all comments for choosen goal (getCount)
            Cursor cursorGoalAllComments = myDb.getAllRowsOurArrangementComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
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

            */

            // make link to comment jointly goal
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.ilink_goal_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_jointly_goal");

            // make link to show comment for jointly goal
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.ilink_goal_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_jointly_goal");;


            if (prefs.getInt("commentJointlyGoalCountComment",0) < prefs.getInt("commentJointlyGoalMaxCountComment",0) || !commentLimitationBorder) {

                showJointlyGoalsCommentsLinkTmp = Html.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+"</a>");

            }
            else {
                showJointlyGoalsCommentsLinkTmp = Html.fromHtml(" ("+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+")");

            }


            if (tmpIntCountComments == 0) {
                showCommentsLinkTmp = Html.fromHtml(tmpCountComments + " &middot;");
            }
            else {
                showCommentsLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountComments + "</a> " + tmpTextNewEntryComment + " &middot;");

            }

        }


        // show genaerate links for evaluate or/and comment
        if (prefs.getBoolean("showArrangementComment", false) || prefs.getBoolean("showArrangementEvaluate", false) ) {

            // create the comment link
            TextView linkCommentAnArrangement = (TextView) view.findViewById(R.id.linkCommentAnArrangement);

            if (showEvaluateCommentLinkTmp != null && showCommentsLinkTmp != null && showCommentArrangementLinkTmp != null) {
                linkCommentAnArrangement.setText(TextUtils.concat(showEvaluateCommentLinkTmp, showCommentsLinkTmp, showCommentArrangementLinkTmp));
            }
            else if (showEvaluateCommentLinkTmp == null && showCommentsLinkTmp != null && showCommentArrangementLinkTmp != null) {
                linkCommentAnArrangement.setText(TextUtils.concat(showCommentsLinkTmp, showCommentArrangementLinkTmp));
            }
            else {
                linkCommentAnArrangement.setText(showEvaluateCommentLinkTmp);
            }

            linkCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());

        }








    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() ) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_jointly_goals_now_first, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_jointly_goals_now, parent, false);
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
