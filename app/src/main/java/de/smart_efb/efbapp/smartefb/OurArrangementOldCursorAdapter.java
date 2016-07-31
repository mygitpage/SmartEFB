package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ich on 30.07.16.
 */
public class OurArrangementOldCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;

    // actual arrangement date, which is "at work"
    long actualArrangementDate = 0;

    // old arrangement change true -> change, false -> no change, same Date!
    Boolean oldArrangementDateChange = false;


    // Default constructor
    public OurArrangementOldCursorAdapter (Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);


    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        if (oldArrangementDateChange) { // listview for first element or date change
            TextView tmpOldArrangementDate = (TextView) view.findViewById(R.id.listOldArrangementDate);
            String txtArrangementNumber = context.getResources().getString(R.string.ourArrangementOldArrangementDateIntro) + " " + EfbHelperClass.timestampToDateFormat(actualArrangementDate, "dd.MM.yyyy");
            tmpOldArrangementDate.setText(txtArrangementNumber);
        }


        // set old arrangement text
        TextView textViewArrangement = (TextView) view.findViewById(R.id.listOldTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);


    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() || actualArrangementDate != cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_WRITE_TIME))) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_old_start, parent, false);
            actualArrangementDate = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_WRITE_TIME));
            oldArrangementDateChange = true;
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_old, parent, false);
            oldArrangementDateChange = false;
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
