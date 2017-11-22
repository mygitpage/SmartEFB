package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by ich on 22.11.2017.
 */

public class MeetingSuggestionOverviewCursorAdapter extends CursorAdapter {




    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingSuggestionOverviewCursorAdapterContext;


    // reference to the DB
    DBAdapter myDb;




    // max number of simultaneous meeting checkboxes
    static final int maxSimultaneousMeetingCheckBoxes = 20;

    // int array for checkbox values (DbId)
    int [] checkBoxMeetingSuggestionsValues = new int [maxSimultaneousMeetingCheckBoxes];



    // Own constructor
    public MeetingSuggestionOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewCursorAdapterContext = context;

        // init the DB
        myDb = new DBAdapter(context);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init checkbox value array
        for (int i=0; i<maxSimultaneousMeetingCheckBoxes; i++) {

            checkBoxMeetingSuggestionsValues[i] = -1;
        }

    }




    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        inflatedView = cursorInflater.inflate(R.layout.list_make_meeting_show_meeting_last, parent, false);


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
