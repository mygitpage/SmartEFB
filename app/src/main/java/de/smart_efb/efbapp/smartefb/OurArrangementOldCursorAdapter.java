package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by ich on 30.07.16.
 */
public class OurArrangementOldCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;






    // Default constructor
    public OurArrangementOldCursorAdapter (Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);


    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {




    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() ) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_old_first, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_old, parent, false);
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
