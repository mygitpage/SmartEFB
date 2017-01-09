package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
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
 * Created by ich on 09.01.2017.
 */
public class MeetingFindMeetingCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;


    // Default constructor
    public MeetingFindMeetingCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);



    }





    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        /*
        if (cursor.isFirst() ) { // listview for first element
            TextView numberOfArrangement = (TextView) view.findViewById(R.id.ourArrangementIntroText);
            String txtArrangementNumber = context.getResources().getString(R.string.ourArrangementIntroText) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()), "dd.MM.yyyy");;
            numberOfArrangement.setText(txtArrangementNumber);
        }
        */

        /*
        // put arrangement number
        TextView numberOfArrangement = (TextView) view.findViewById(R.id.listArrangementNumberText);
        String txtArrangementNumber = context.getResources().getString(R.string.showArrangementIntroText)+ " " + Integer.toString(cursor.getPosition()+1);
        numberOfArrangement.setText(txtArrangementNumber);


        // put arrangement text
        TextView textViewArrangement = (TextView) view.findViewById(R.id.listTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);
        */


    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() ) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_find_meeting_suggestion, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_find_meeting_last_suggestion, parent, false);
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
