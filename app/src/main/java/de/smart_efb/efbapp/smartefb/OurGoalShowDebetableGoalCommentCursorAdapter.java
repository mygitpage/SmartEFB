package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

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

    String choosenDebetableGoal = "";

    // reference to the DB
    private DBAdapter myDb;


    // own constructor!!!
    public OurGoalShowDebetableGoalCommentCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInLIst, String debetableGoal) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // context of activity OurGoals
        contextForActivity = context;

        // set db id for debetable goal
        debetableGoalDbIdToShow = dbId;

        // set debetable goal number in list view
        debetableGoalNumberInListView = numberInLIst;

        // set choosen debetable goal
        choosenDebetableGoal = debetableGoal;

        // init the DB
        myDb = new DBAdapter(context);

        // init array for text description of scales levels
        evaluateDebetableGoalCommentScalesLevel = context.getResources().getStringArray(R.array.debetableGoalsCommentScalesLevel);


    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // check if debetable goal comment entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfDebetableGoal = (TextView) view.findViewById(R.id.listActualTextNewDebetableGoalComment);
            String txtnewEntryOfDebetableGoal = context.getResources().getString(R.string.newEntryText);
            newEntryOfDebetableGoal.setText(txtnewEntryOfDebetableGoal);

            // delete status new entry in db
            myDb.deleteStatusNewEntryOurGoalsDebetableGoalsComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // show actual comment
        TextView textViewShowActualComment = (TextView) view.findViewById(R.id.listActualTextDebetableGoalComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // show actual result struct question
        TextView textViewShowResultStructQuestion = (TextView) view.findViewById(R.id.listActualResultStructQuestion);
        String actualResultStructQuestion = context.getResources().getString(R.string.textDebetableGoalCommentActualResultStructQuestion);
        actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateDebetableGoalCommentScalesLevel[cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1))-1]);
        textViewShowResultStructQuestion.setText(actualResultStructQuestion);

        // show author and date
        TextView textViewShowActualAuthorAndDate = (TextView) view.findViewById(R.id.listActualDebetableGoalCommentAuthorAndDate);
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME));
        String authorAndDate = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
        textViewShowActualAuthorAndDate.setText(authorAndDate);

        // generate link "zurueck zu den Entwuerfen" and set text intro "Die Einschaetzungen zum Entwurf ...", when cursor position is first
        if (cursor.isFirst()) {

            // set text intro "Strittiges Ziel ..."
            TextView textViewShowDebetableGoalIntro = (TextView) view.findViewById(R.id.goalsShowDebetableGoalIntro);
            String txtDebetableGoalIntro = contextForActivity.getResources().getString(R.string.showDebetableGoalsIntroText)+ " " + debetableGoalNumberInListView;
            textViewShowDebetableGoalIntro.setText(txtDebetableGoalIntro);

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

            // show choosen debetable goal
            TextView textViewShowchoosenDebetableGoal = (TextView) view.findViewById(R.id.actualDebetableGoalCommentTextInShowComment);
            textViewShowchoosenDebetableGoal.setText(choosenDebetableGoal);

            // set text intro "Die Einschätzungen zum strittigen Ziel ..."
            TextView textViewShowCommentIntro = (TextView) view.findViewById(R.id.debetableGoalShowCommentIntro);
            String txtCommentIntro = contextForActivity.getResources().getString(R.string.showDebetableGoalCommentIntroText)+ " " + debetableGoalNumberInListView;
            textViewShowCommentIntro.setText(txtCommentIntro);

        }

        // generate onclicklistener for Button "zurueck zu strittigen Zielen"
        if (cursor.isLast()) {

            // button abbort "zurueck zu den Entwuerfen"
            Button buttonBackToDebetableGoalsNow = (Button) view.findViewById(R.id.buttonAbortShowDebetableGoalComment);

            // onClick listener abbort button
            buttonBackToDebetableGoalsNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_debetable_goals_now");
                    contextForActivity.startActivity(intent);

                }
            });

        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment_first, parent, false);
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment_firstandlast, parent, false);
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment_last, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_debetable_goals_comment, parent, false);
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
