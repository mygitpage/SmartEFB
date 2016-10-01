package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
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
 * Created by ich on 09.09.16.
 */
public class OurArrangementSketchCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;

    // actual arrangement date, which is "at work"
    long actualArrangementDate = 0;

    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForAssessments = new String [12];

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;




    // Default constructor
    public OurArrangementSketchCursorAdapter (Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);

        // init array for count comments
        numberCountForAssessments = context.getResources().getStringArray(R.array.ourArrangementCountAssessments);

        commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("sketch");

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // link to show comments
        Spanned showCommentsLinkTmp = null;
        // link to comment an arrangement
        Spanned showCommentSketchArrangementLinkTmp = null;


        // text for new comment entry
        String tmpTextNewEntryComment = "";

        // open sharedPrefs
        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);


        if (cursor.isFirst() ) { // listview for first element? write intro text
            TextView tmpTextViewSketchIntroText = (TextView) view.findViewById(R.id.ourArrangementSketchIntroText);
            String tmpTextIntroText = String.format(context.getResources().getString(R.string.ourArrangementSketchIntroText), prefs.getString("authorOfSketchArrangement", "John Dow"), EfbHelperClass.timestampToDateFormat(prefs.getLong("currentDateOfSketchArrangement", System.currentTimeMillis()), "dd.MM.yyyy"));
            tmpTextViewSketchIntroText.setText(tmpTextIntroText);
        }

        // put arrangement number
        TextView tmpTextViewNumberOfArrangement = (TextView) view.findViewById(R.id.listArrangementSketchNumberText);
        String tmpTextSketchArrangementNumber = context.getResources().getString(R.string.showSketchArrangementNumberText)+ " " + Integer.toString(cursor.getPosition()+1);
        tmpTextViewNumberOfArrangement.setText(tmpTextSketchArrangementNumber);

        // check if arrangement entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfSketchArrangement = (TextView) view.findViewById(R.id.listArrangementNewSketchText);
            String txtNewEntryOfSketchArrangement = context.getResources().getString(R.string.newEntryText);
            newEntryOfSketchArrangement.setText(txtNewEntryOfSketchArrangement);
            myDb.deleteStatusNewEntryOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // put arrangement text
        TextView textViewArrangement = (TextView) view.findViewById(R.id.listTextSketchArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);

        // Show link for comment the sketch arrangements
        if (prefs.getBoolean("showCommentLinkSketchArrangements", false)) {

            // get from DB  all comments for choosen sketch arrangement (getCount)
            Cursor cursorSketchArrangementAllComments = myDb.getAllRowsOurArrangementSketchComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
            // generate the number of comments to show
            String tmpCountAssessments;
            int tmpIntCountComments = cursorSketchArrangementAllComments.getCount();
            if (tmpIntCountComments > 10) {
                tmpCountAssessments = numberCountForAssessments[11];

            }
            else {
                tmpCountAssessments = numberCountForAssessments[cursorSketchArrangementAllComments.getCount()];
            }

            // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
            if (cursorSketchArrangementAllComments.getCount() > 0) {
                cursorSketchArrangementAllComments.moveToFirst();
                if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY)) == 1) {
                    tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(context, R.color.text_accent_color) + "'>"+ context.getResources().getString(R.string.newEntryText) + "</font>";
                }
            }


            // make link to comment arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_sketch_arrangement");

            // make link to show comment for arrangement
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_sketch_arrangement");;


            if (prefs.getInt("commentSketchOurArrangementCountComment",0) < prefs.getInt("commentSketchOurArrangementMaxComment",0) || !commentLimitationBorder) {

                showCommentSketchArrangementLinkTmp = Html.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementAssessmentsString", "string", context.getPackageName()))+"</a>");

            }
            else {
                showCommentSketchArrangementLinkTmp = Html.fromHtml(" ("+context.getResources().getString(context.getResources().getIdentifier("ourArrangementAssessmentsString", "string", context.getPackageName()))+")");

            }


            if (tmpIntCountComments == 0) {
                showCommentsLinkTmp = Html.fromHtml(tmpCountAssessments + " &middot;");
            }
            else {
                showCommentsLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountAssessments + "</a> " + tmpTextNewEntryComment + " &middot;");

            }


            // get textview from view
            TextView linkCommentAnSketchArrangement = (TextView) view.findViewById(R.id.linkCommentAnSketchArrangement);

            linkCommentAnSketchArrangement.setText(TextUtils.concat(showCommentsLinkTmp, showCommentSketchArrangementLinkTmp));
            linkCommentAnSketchArrangement.setMovementMethod(LinkMovementMethod.getInstance());

        }


    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst()) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_sketch_first, parent, false);


        }
        else { // listview for next elements
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_sketch, parent, false);

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