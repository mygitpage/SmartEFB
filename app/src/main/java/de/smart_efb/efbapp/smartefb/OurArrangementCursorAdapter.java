package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ich on 30.05.16.
 */
public class OurArrangementCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;




    // Default constructor
    public OurArrangementCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);


        // create the arrangement

        TextView textViewArrangement = (TextView) view.findViewById(R.id.listTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);


        // create the number of arrangement
        TextView numberOfArrangement = (TextView) view.findViewById(R.id.listArrangementNumber);
        //countNumberOfCurrentArrangement = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
        numberOfArrangement.setText(Integer.toString(cursor.getPosition()+1));



        // Show link for comment in our arrangement
        if (prefs.getBoolean("showArrangementComment", false)) {
            // create the comment link
            TextView linkCommentAnArrangement = (TextView) view.findViewById(R.id.linkCommentAnArrangement);

            // make link to comment arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("com", "comment_an_arrangement");


            // make link to show comment for arrangement
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("com", "show_comment_for_arrangement");;




            linkCommentAnArrangement.setText(Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentString", "string", context.getPackageName()))+"</a> &middot;" + " <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+"</a>"));
            linkCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());






        }



    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

                return cursorInflater.inflate(R.layout.list_our_arrangement_left, parent, false);

    }





}
