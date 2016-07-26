package de.smart_efb.efbapp.smartefb;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by ich on 23.07.16.
 */
public class OurArrangementShowCommentCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    final Context contextForActivity;

    // DB-Id of arrangement
    int arrangementDbIdToShow = 0;

    // arrangement number in list view of fragment show arrangement now
    int arrangementNumberInListView = 0;

    String choosenArrangement = "";


    // Default constructor
    public OurArrangementShowCommentCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInLIst, String arrangement) {

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

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        SharedPreferences prefs = context.getSharedPreferences("smartEfbSettings", context.MODE_PRIVATE);


        // show "NEW" when comment is new
        TextView textViewShowActualNewMarker = (TextView) view.findViewById(R.id.listActualTextNewComment);
        textViewShowActualNewMarker.setText("Neu");

        // show actual comment
        TextView textViewShowActualComment = (TextView) view.findViewById(R.id.listActualTextComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // show author and date
        TextView textViewShowActualAuthorAndDate = (TextView) view.findViewById(R.id.listActualCommentAuthorAndDate);
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
        String authorAndDate = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
        textViewShowActualAuthorAndDate.setText(authorAndDate);


        // generate link "zurueck zu den Absprachen" and set text intro "Die kommentare zur Absprache ...", when cursor position == zero
        if (cursor.getPosition() == 0) {



            // set text intro "Absprache ..."
            TextView textViewShowArrangementIntro = (TextView) view.findViewById(R.id.arrangementShowArrangementIntro);
            String txtArrangementIntro = contextForActivity.getResources().getString(R.string.showArrangementIntroText)+ " " + arrangementNumberInListView;
            textViewShowArrangementIntro.setText(txtArrangementIntro);
            textViewShowArrangementIntro.setMovementMethod(LinkMovementMethod.getInstance());


            // make link back to show arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.ilink_comment")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_arrangement_now");
            TextView linkShowCommentBackLink = (TextView) view.findViewById(R.id.arrangementShowCommentBackLink);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentBackLink", "string", context.getPackageName()))+"</a>");
            linkShowCommentBackLink.setText(tmpBackLink);
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());




            // show choosen arrangement
            TextView textViewShowChoosenArrangement = (TextView) view.findViewById(R.id.actualCommentTextInShowComment);
            textViewShowChoosenArrangement.setText(actualComment);


            // set text intro "Die Kommentare zur Absprache ..."
            TextView textViewShowCommentIntro = (TextView) view.findViewById(R.id.arrangementShowCommentIntro);
            String txtCommentIntro = contextForActivity.getResources().getString(R.string.showCommentIntroText)+ " " + arrangementNumberInListView;
            textViewShowCommentIntro.setText(txtCommentIntro);





        }

        // generate onclicklistener for Button "zurueck zu den Absprachen"
        if (cursor.getPosition() == cursor.getCount()-1) {

            // button abbort "zurueck zu den Absprachen"
            Button buttonBackToArrangement = (Button) view.findViewById(R.id.buttonAbortShowComment);

            // onClick listener abbort button
            buttonBackToArrangement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_arrangement_now");
                    contextForActivity.startActivity(intent);

                }
            });

        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView = null;


        if (cursor.getPosition() == 0 && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_first, parent, false);
        }
        else if (cursor.getPosition() == 0 && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_firstandlast, parent, false);
        }
        else if (cursor.getPosition() == cursor.getCount()-1) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_last, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment, parent, false);
        }

        return inflatedView;

    }

}








