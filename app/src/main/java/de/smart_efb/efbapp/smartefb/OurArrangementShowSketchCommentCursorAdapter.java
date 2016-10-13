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
 * Created by ich on 06.10.16.
 */
public class OurArrangementShowSketchCommentCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    final Context contextForActivity;

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] evaluateSketchCommentScalesLevel = new String [countScalesLevel];

    // DB-Id of arrangement
    int arrangementDbIdToShow = 0;

    // arrangement number in list view of fragment show arrangement now
    int arrangementNumberInListView = 0;

    String choosenArrangement = "";

    // reference to the DB
    private DBAdapter myDb;


    // own constructor!!!
    public OurArrangementShowSketchCommentCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInLIst, String arrangement) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // context of activity OurArrangement
        contextForActivity = context;

        // set db id for arrangement
        arrangementDbIdToShow = dbId;

        // set arrangement number in list view
        arrangementNumberInListView = numberInLIst;

        // set choosen arrangement
        choosenArrangement = arrangement;

        // init the DB
        myDb = new DBAdapter(context);

        // init array for text description of scales levels
        evaluateSketchCommentScalesLevel = context.getResources().getStringArray(R.array.evaluateSketchCommentScalesLevel);


    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // check if arrangement entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfArrangement = (TextView) view.findViewById(R.id.listActualTextNewSketchComment);
            String txtnewEntryOfArrangement = context.getResources().getString(R.string.newEntryText);
            newEntryOfArrangement.setText(txtnewEntryOfArrangement);

            // delete status new entry in db
            myDb.deleteStatusNewEntryOurArrangementSketchComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // show actual comment
        TextView textViewShowActualComment = (TextView) view.findViewById(R.id.listActualTextSketchComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // show actual result struct question
        TextView textViewShowResultStructQuestion = (TextView) view.findViewById(R.id.listActualResultStructQuestion);
        String actualResultStructQuestion = context.getResources().getString(R.string.textSketchCommentActualResultStructQuestion);
        actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateSketchCommentScalesLevel[cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1))-1]);
        textViewShowResultStructQuestion.setText(actualResultStructQuestion);

        // show author and date
        TextView textViewShowActualAuthorAndDate = (TextView) view.findViewById(R.id.listActualSketchCommentAuthorAndDate);
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME));
        String authorAndDate = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
        textViewShowActualAuthorAndDate.setText(authorAndDate);

        // generate link "zurueck zu den Entwuerfen" and set text intro "Die Einschaetzungen zum Entwurf ...", when cursor position is first
        if (cursor.isFirst()) {

            // set text intro "Entwurf ..."
            TextView textViewShowArrangementIntro = (TextView) view.findViewById(R.id.arrangementShowSketchArrangementIntro);
            String txtArrangementIntro = contextForActivity.getResources().getString(R.string.showSketchArrangementIntroText)+ " " + arrangementNumberInListView;
            textViewShowArrangementIntro.setText(txtArrangementIntro);

            // make link back to show arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_sketch_arrangement");
            TextView linkShowCommentBackLink = (TextView) view.findViewById(R.id.arrangementShowSketchCommentBackLink);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementBackLinkToSketchArrangement", "string", context.getPackageName()))+"</a>");
            linkShowCommentBackLink.setText(tmpBackLink);
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

            // show choosen arrangement
            TextView textViewShowChoosenArrangement = (TextView) view.findViewById(R.id.actualSketchCommentTextInShowComment);
            textViewShowChoosenArrangement.setText(choosenArrangement);

            // set text intro "Die Einschätzungen zum Entwurf ..."
            TextView textViewShowCommentIntro = (TextView) view.findViewById(R.id.arrangementShowCommentIntro);
            String txtCommentIntro = contextForActivity.getResources().getString(R.string.showSketchCommentIntroText)+ " " + arrangementNumberInListView;
            textViewShowCommentIntro.setText(txtCommentIntro);

        }

        // generate onclicklistener for Button "zurueck zu den Absprachen"
        if (cursor.isLast()) {

            // button abbort "zurueck zu den Entwuerfen"
            Button buttonBackToArrangement = (Button) view.findViewById(R.id.buttonAbortShowSketchComment);

            // onClick listener abbort button
            buttonBackToArrangement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_sketch_arrangement");
                    contextForActivity.startActivity(intent);

                }
            });

        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment_first, parent, false);
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment_firstandlast, parent, false);
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment_last, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment, parent, false);
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
