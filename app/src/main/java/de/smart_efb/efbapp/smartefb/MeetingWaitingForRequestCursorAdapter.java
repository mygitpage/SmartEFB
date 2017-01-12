package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 12.01.2017.
 */
public class MeetingWaitingForRequestCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingWaitingRequestCursorAdapterContext;

    // meeting suggestions author
    String meetingSuggestionsAuthor = "";

    

    // constructor
    public MeetingWaitingForRequestCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingWaitingRequestCursorAdapterContext = context;

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }





    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        if (cursor.isFirst() ) { // listview for first element

            // show meeting intro text
            String txtFindFirstMeetingIntro  = context.getResources().getString(R.string.findMeetingIntroTextWaitingForRequest);
            TextView textViewNextMeetingIntroText = (TextView) view.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setText(txtFindFirstMeetingIntro);

            // set movement methode for explain text find first + meeting telephone link
            TextView tmpTextFindFirstMeetingExplainText = (TextView) view.findViewById(R.id.showExplainTextForWaitingForRequest);
            tmpTextFindFirstMeetingExplainText.setVisibility(View.VISIBLE);
            tmpTextFindFirstMeetingExplainText.setMovementMethod(LinkMovementMethod.getInstance());


            // set visible of text meeting send suggestions
            TextView tmpTextSendMeetingSuggestions = (TextView) view.findViewById(R.id.showExplainTextSendMeetingSuggestions);
            tmpTextSendMeetingSuggestions.setVisibility(View.VISIBLE);





        }


        // put date text
        TextView tmpDateMeetingSuggestion = (TextView) view.findViewById(R.id.listActualDateMeetingSuggestion);
        String tmpTextMeetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_FIND_MEETING_KEY_DATE_TIME)), "dd.MM.yyyy");
        tmpDateMeetingSuggestion.setText(tmpTextMeetingDate);

        // put time text
        TextView tmpTimeMeetingSuggestion = (TextView) view.findViewById(R.id.listActualTimeMeetingSuggestion);
        String tmpTextMeetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_FIND_MEETING_KEY_DATE_TIME)), "HH:mm");
        String tmpTextMeetingTimeAdditionalWordClock = context.getResources().getString(R.string.showClockWordAdditionalText);
        tmpTimeMeetingSuggestion.setText(", " + tmpTextMeetingTime +" " + tmpTextMeetingTimeAdditionalWordClock );

        // put place text
        TextView tmpPlaceMeetingSuggestion = (TextView) view.findViewById(R.id.listActualPlaceMeetingSuggestion);
        String tmpTextMeetingPlace = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_FIND_MEETING_KEY_MEETING_PLACE));
        tmpPlaceMeetingSuggestion.setText(tmpTextMeetingPlace);


        if (cursor.isFirst() ) { // last element -> set separate line gone

            View tmpSeparateLine = (View) view.findViewById(R.id.separateLine);
            tmpSeparateLine.setVisibility(View.GONE);
        }


    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isFirst() ) { // listview for first element

            inflatedView = cursorInflater.inflate(R.layout.list_waiting_meeting_request_first, parent, false);
        }
        else {

            inflatedView = cursorInflater.inflate(R.layout.list_waiting_meeting_request, parent, false);
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