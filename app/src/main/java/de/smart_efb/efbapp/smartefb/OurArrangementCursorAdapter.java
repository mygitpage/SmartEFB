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

        // link to comment and show comment
        Spanned showAndCommentLinkTmp = null;
        // link to evaluate arrangement
        Spanned showEvaluateCommentLinkTmp = null;


        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);





        // create the arrangement

        TextView textViewArrangement = (TextView) view.findViewById(R.id.listTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);


        // create the number of arrangement
        TextView numberOfArrangement = (TextView) view.findViewById(R.id.listArrangementNumber);
        //countNumberOfCurrentArrangement = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
        numberOfArrangement.setText(Integer.toString(cursor.getPosition()+1));



        // Show link for evaluate an arrangement
        if (prefs.getBoolean("showArrangementEvaluate", false)) {

            // make link to evaluate arrangement
            Uri.Builder evaluateLinkBuilder = new Uri.Builder();
            evaluateLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "evaluate_an_arrangement");

            showEvaluateCommentLinkTmp = Html.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementEvaluateString", "string", context.getPackageName()))+"</a>");

        }



        // Show link for comment in our arrangement
        if (prefs.getBoolean("showArrangementComment", false)) {

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

            if (showEvaluateCommentLinkTmp != null) {
                showAndCommentLinkTmp = Html.fromHtml(" &middot; <a href=\"" + showCommentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentString", "string", context.getPackageName()))+"</a> &middot;" + " <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+"</a>");
            }
            else {
                showAndCommentLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentString", "string", context.getPackageName()))+"</a> &middot;" + " <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+"</a>");

            }


        }



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

                return cursorInflater.inflate(R.layout.list_our_arrangement_now, parent, false);

    }





}
