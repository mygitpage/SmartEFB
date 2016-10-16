package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by ich on 16.10.2016.
 */
public class OurGoalsJointlyGoalsNowCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // reference to the DB
    private DBAdapter myDb;

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;


    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForComments = new String [12];


    // Default constructor
    public OurGoalsJointlyGoalsNowCursorAdapter (Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the DB
        myDb = new DBAdapter(context);

        // init array for count comments
        //numberCountForComments = context.getResources().getStringArray(R.array.ourArrangementCountComments);

        //commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("current");

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() ) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_jointly_goals_now_first, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_jointly_goals_now, parent, false);
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
