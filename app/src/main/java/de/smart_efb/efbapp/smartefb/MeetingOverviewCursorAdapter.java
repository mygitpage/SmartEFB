package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ich on 21.11.2017.
 */

public class MeetingOverviewCursorAdapter extends CursorAdapter {




    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingSuggestionOverviewCursorAdapterContext;


    // reference to the DB
    DBAdapter myDb;

    // count headline number for meetings
    int countMeetingHeadlineNumber = 0;


    // array of meeting places names (only 3 possible-> 0=nothing; 1=Werder(Havel); 2=Bad Belzig)
    String  meetingPlaceNames[] = new String[3];



    // Own constructor
    public MeetingOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewCursorAdapterContext = context;

        // init the DB
        myDb = new DBAdapter(context);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        meetingPlaceNames = context.getResources().getStringArray(R.array.placesNameForMeetingArray);

    }




    @Override
    public void bindView(View view, final Context context, Cursor cursor) {






    }



    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        final Context context = mContext;

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            Log.d("Meeting Cursor Adapter", "In der 1");
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_overview_first, parent, false);
            countMeetingHeadlineNumber = 1;
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_overview_firstandlast, parent, false);
            countMeetingHeadlineNumber++;
            Log.d("Meeting Cursor Adapter", "In der 2");
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_overview_last, parent, false);
            countMeetingHeadlineNumber++;
            Log.d("Meeting Cursor Adapter", "In der 3");
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_overview_normal, parent, false);
            countMeetingHeadlineNumber++;
            Log.d("Meeting Cursor Adapter", "In der 4");
        }





        TextView textViewMeetingTitleAndNumber = (TextView) inflatedView.findViewById(R.id.meetingTitleAndNumber);
        String actualMeetingHeadline = context.getResources().getString(R.string.meetingOverviewMeetingTitleAndNumberText) + " " + countMeetingHeadlineNumber;
        textViewMeetingTitleAndNumber.setText(actualMeetingHeadline);

        // check if meeting entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST)) == 1) {
            TextView newEntryOfMeeting = (TextView) inflatedView.findViewById(R.id.meetingNewMeetingText);
            String txtNewEntryOfMeeting = context.getResources().getString(R.string.newEntryText);
            newEntryOfMeeting.setText(txtNewEntryOfMeeting);

            // delet status new entry in db
            //myDb.deleteStatusNewEntryOurArrangementComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // textview for the author and date
        TextView tmpTextViewAuthorNameForMeeting = (TextView) inflatedView.findViewById(R.id.meetingAuthorAndDate);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR));
        String meetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "dd.MM.yyyy");;
        String meetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "HH:mm");;
        String tmpTextAuthorNameMeeting = String.format(context.getResources().getString(R.string.meetingOverviewCreateMeetingAuthorAndDate), tmpAuthorName, meetingDate, meetingTime);
        tmpTextViewAuthorNameForMeeting.setText(Html.fromHtml(tmpTextAuthorNameMeeting));



        // textview for meeting date
        TextView tmpTextViewMeetingDate = (TextView) inflatedView.findViewById(R.id.meetingDate);
        String tmpMeetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy");;
        tmpTextViewMeetingDate.setText(tmpMeetingDate);


        // textview for meeting time
        TextView tmpTextViewMeetingTime = (TextView) inflatedView.findViewById(R.id.meetingTime);
        String tmpMeetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm");;
        tmpTextViewMeetingTime.setText(tmpMeetingTime);

        // textview for meeting place
        TextView tmpTextViewMeetingPlace = (TextView) inflatedView.findViewById(R.id.meetingPlace);
        String tmpMeetingPlace = meetingPlaceNames[cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))];
        tmpTextViewMeetingPlace.setText(tmpMeetingPlace);


        Log.d("Meeting Cursor Adapter", "place1: " + cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))+", "+tmpMeetingPlace);







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
