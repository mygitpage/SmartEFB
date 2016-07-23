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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by ich on 23.07.16.
 */
public class OurArrangementShowCommentCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;




    // Default constructor
    public OurArrangementShowCommentCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {




        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);


        // create the comment show
        TextView textViewShowActualNewMarker = (TextView) view.findViewById(R.id.listActualTextNewComment);
        String newMarker = cursor.getString(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
        textViewShowActualNewMarker.setText(newMarker);


        TextView textViewShowActualComment = (TextView) view.findViewById(R.id.listActualTextComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);


        TextView textViewShowActualAuthorAndDate = (TextView) view.findViewById(R.id.listActualCommentAuthorAndDate);
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
        String authorAndDate = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
        textViewShowActualAuthorAndDate.setText(authorAndDate);



    }




    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView = null;

        if (cursor.getPosition() == 0) {
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_first, parent, false);

            // make link to comment arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_arrangement_now");

            // create the comment link
            TextView linkShowCommentBackLink = (TextView) inflatedView.findViewById(R.id.arrangementShowCommentBackLink);
            linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentBackLink", "string", context.getPackageName()))+"</a>"));
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else if (cursor.getPosition() == cursor.getCount()-1) {
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_last, parent, false);
        }
        else {
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment, parent, false);
        }

        return inflatedView;

    }





}








