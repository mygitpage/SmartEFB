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
        int number = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));
        numberOfArrangement.setText(Integer.toString(number));




        if (prefs.getBoolean("showArrangementComment", false)) {
            // create the comment link
            TextView linkCommentAnArrangement = (TextView) view.findViewById(R.id.linkCommentAnArrangement);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("smart.efb.ilink")
                    .authority("www.smart-efb.de")
                    .appendQueryParameter("id", Integer.toString(number));
            linkCommentAnArrangement.setText(Html.fromHtml("<a href=\"" + builder.build().toString() + "\">Kommentieren</a>"));
            linkCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());
        }



    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

                return cursorInflater.inflate(R.layout.list_our_arrangement_left, parent, false);

    }





}
