package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
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
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by ich on 30.05.16.
 */
public class OurArrangementNowCursorAdapter extends CursorAdapter {

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;


    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForComments = new String [12];


    // Default constructor
    public OurArrangementNowCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);

        // init array for count comments
        numberCountForComments = context.getResources().getStringArray(R.array.ourArrangementCountComments);

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // link to comment and show comment
        Spanned showAndCommentLinkTmp = null;
        // link to evaluate arrangement
        Spanned showEvaluateCommentLinkTmp = null;

        // open sharedPrefs
        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);

        if (cursor.isFirst() ) { // listview for first element
            TextView numberOfArrangement = (TextView) view.findViewById(R.id.ourArrangementIntroText);
            String txtArrangementNumber = context.getResources().getString(R.string.ourArrangementIntroText) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()), "dd.MM.yyyy");;
            numberOfArrangement.setText(txtArrangementNumber);
        }

        // put arrangement number
        TextView numberOfArrangement = (TextView) view.findViewById(R.id.listArrangementNumberText);
        String txtArrangementNumber = context.getResources().getString(R.string.showArrangementIntroText)+ " " + Integer.toString(cursor.getPosition()+1);
        numberOfArrangement.setText(txtArrangementNumber);

        // check if arrangement entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfArrangement = (TextView) view.findViewById(R.id.listArrangementNewArrangementText);
            String txtnewEntryOfArrangement = context.getResources().getString(R.string.newEntryText);
            newEntryOfArrangement.setText(txtnewEntryOfArrangement);
            myDb.deleteStatusNewEntryOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // put arrangement text
        TextView textViewArrangement = (TextView) view.findViewById(R.id.listTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);

        // generate link for evaluate an arrangement
        if (prefs.getBoolean("showArrangementEvaluate", false)) {

            // make link to evaluate arrangement
            Uri.Builder evaluateLinkBuilder = new Uri.Builder();
            evaluateLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "evaluate_an_arrangement");

            showEvaluateCommentLinkTmp = Html.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementEvaluateString", "string", context.getPackageName()))+"</a> &middot; ");

        }

        // Show link for comment in our arrangement
        if (prefs.getBoolean("showArrangementComment", false)) {

            // get all comments for choosen arrangement (getCount)
            Cursor cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
            // generate the number of comments to show
            String tmpCountComments;
            int tmpIntCountComments = cursorArrangementAllComments.getCount();
            if (cursorArrangementAllComments.getCount() > 10) {
                tmpCountComments = numberCountForComments[11];

            }
            else {
                tmpCountComments = numberCountForComments[cursorArrangementAllComments.getCount()];
            }

            // make link to comment arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_arrangement");

            // make link to show comment for arrangement
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_arrangement");;

            if (tmpIntCountComments == 0) {
                showAndCommentLinkTmp = Html.fromHtml(tmpCountComments + " &middot;" + " <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+"</a>");
            }
            else {
                showAndCommentLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountComments + "</a> &middot;" + " <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+"</a>");

            }

        }


        // show genaerate links for evaluate or/and comment
        if (prefs.getBoolean("showArrangementComment", false) || prefs.getBoolean("showArrangementEvaluate", false) ) {

            // create the comment link
            TextView linkCommentAnArrangement = (TextView) view.findViewById(R.id.linkCommentAnArrangement);

            if (showEvaluateCommentLinkTmp != null && showAndCommentLinkTmp != null) {
                linkCommentAnArrangement.setText(TextUtils.concat(showEvaluateCommentLinkTmp, showAndCommentLinkTmp));
            }
            else if (showEvaluateCommentLinkTmp == null && showAndCommentLinkTmp != null) {
                linkCommentAnArrangement.setText(showAndCommentLinkTmp);
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
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_now_first, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_now, parent, false);
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
