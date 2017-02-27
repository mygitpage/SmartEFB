package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

/**
 * Created by ich on 28.10.2016.
 */
public class OurGoalsShowCommentJointlyGoalsCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    final Context contextForActivity;

    // DB-Id of jointly goal
    int jointlyGoalDbIdToShow = 0;

    // jointly goal number in list view of fragment show jointly goal now
    int jointlyGoalNumberInListView = 0;

    String choosenJointlyGoalText = "";

    // reference to the DB
    private DBAdapter myDb;


    // own constructor!!!
    public OurGoalsShowCommentJointlyGoalsCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInLIst, String goalText) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // context of activity OurGoals
        contextForActivity = context;

        // set db id for jointly goal
        jointlyGoalDbIdToShow = dbId;

        // set jointly goal number in list view
        jointlyGoalNumberInListView = numberInLIst;

        // set choosen jointly goal text
        choosenJointlyGoalText = goalText;

        // init the DB
        myDb = new DBAdapter(context);

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.d("CursorAdapter-Jointly","ID: "+cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));

        // check if jointly goal comment entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfJointlyGoalComment = (TextView) view.findViewById(R.id.listActualTextNewComment);
            String txtnewEntryOfJointlyGoalComment = context.getResources().getString(R.string.newEntryText);
            newEntryOfJointlyGoalComment.setText(txtnewEntryOfJointlyGoalComment);

            // delet status new entry in db
            myDb.deleteStatusNewEntryOurGoalsJointlyGoalComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // show actual comment for jointly goal
        TextView textViewShowActualComment = (TextView) view.findViewById(R.id.listActualTextComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // show author and date
        TextView textViewShowActualAuthorAndDate = (TextView) view.findViewById(R.id.listActualCommentAuthorAndDate);
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME));
        String authorAndDate = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
        textViewShowActualAuthorAndDate.setText(authorAndDate);

        // generate link "zurueck zu den zielen" and set text intro "Die kommentare zum ziel  ...", when cursor position is first
        if (cursor.isFirst()) {

            // set text intro "Ziel ..."
            TextView textViewShowArrangementIntro = (TextView) view.findViewById(R.id.goalsShowJointlyGoalIntro);
            String txtArrangementIntro = contextForActivity.getResources().getString(R.string.showJointlyGoalTextNumber)+ " " + jointlyGoalNumberInListView;
            textViewShowArrangementIntro.setText(txtArrangementIntro);

            // make link back to show jointly goals
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_jointly_goals_now");
            TextView linkShowCommentBackLink = (TextView) view.findViewById(R.id.jointlyGoalShowCommentBackLink);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourGoalsBackLinkToJointlyGoals", "string", context.getPackageName()))+"</a>");
            linkShowCommentBackLink.setText(tmpBackLink);
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

            // show choosen jointly goal
            TextView textViewShowChoosenArrangement = (TextView) view.findViewById(R.id.actualCommentTextInShowComment);
            textViewShowChoosenArrangement.setText(choosenJointlyGoalText);

            // set text intro "Die Kommentare zum Ziel ..."
            TextView textViewShowCommentIntro = (TextView) view.findViewById(R.id.jointlyGoalShowCommentIntro);
            String txtCommentIntro = contextForActivity.getResources().getString(R.string.showCommentIntroTextForJointlyGoal)+ " " + jointlyGoalNumberInListView;
            textViewShowCommentIntro.setText(txtCommentIntro);

        }

        // generate onclicklistener for Button "zurueck zu den gemeinsamen Zielen"
        if (cursor.isLast()) {

            // button abbort "zurueck zu den gemeinsamen Zielen"
            Button buttonBackToArrangement = (Button) view.findViewById(R.id.buttonAbortShowComment);

            // onClick listener abbort button
            buttonBackToArrangement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_jointly_goals_now");
                    contextForActivity.startActivity(intent);

                }
            });

        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_first, parent, false);
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_firstandlast, parent, false);
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_last, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_normal, parent, false);
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
