package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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


        TextView textViewArrangement = (TextView) view.findViewById(R.id.listTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);

        TextView numberOfArrangement = (TextView) view.findViewById(R.id.listArrangementNumber);
        int number = cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID));

        textViewArrangement.setText("1");



        TextView linkCommentAnArrangement = (TextView) view.findViewById(R.id.linkCommentAnArrangement);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("smart.efb.ilink")
                .authority("www.myawesomesite.com")
                .appendPath("turtles")
                .appendPath("types")
                .appendQueryParameter("type", "1")
                .appendQueryParameter("sort", "relevance")
                .fragment("section-name");

        linkCommentAnArrangement.setText(Html.fromHtml("<a href=\"" + builder.build().toString() + "\">Kommentieren</a>"));
        linkCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());



    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

                return cursorInflater.inflate(R.layout.list_our_arrangement_left, parent, false);

    }





}