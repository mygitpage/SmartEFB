package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ich on 22.11.2017.
 */

public class MeetingSuggestionOldOverviewCursorAdapter extends CursorAdapter {

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingSuggestionOverviewOldCursorAdapterContext;

    // array of meeting places names (only 3 possible-> 0=nothing; 1=Werder(Havel); 2=Bad Belzig)
    String  meetingPlaceNames[] = new String[3];


    // constructor
    public MeetingSuggestionOldOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewOldCursorAdapterContext = context;

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        meetingPlaceNames = context.getResources().getStringArray(R.array.placesNameForMeetingArray);

    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        inflatedView = cursorInflater.inflate(R.layout.list_meeting_suggestion_old_overview, parent, false);

        if (cursor.isFirst()) {
            // set info intro text to visible
            TextView textViewOldMeetingInfoText = (TextView) inflatedView.findViewById(R.id.oldMeetingIntroInfoText);
            textViewOldMeetingInfoText.setVisibility(View.VISIBLE);

            // set border to gone
            TextView textViewOldMeetingBorder = (TextView) inflatedView.findViewById(R.id.borderBetweenOldMeeting);
            textViewOldMeetingBorder.setVisibility(View.GONE);
        }

        // set title for meeting
        TextView textViewMeetingTitleAndNumber = (TextView) inflatedView.findViewById(R.id.meetingTitleAndNumber);
        String oldMeetingHeadline = context.getResources().getString(R.string.meetingOverviewOldMeetingTitleAndNumberText);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {
            oldMeetingHeadline = context.getResources().getString(R.string.meetingOverviewMeetingCanceledTitleAndNumberText);
        }
        textViewMeetingTitleAndNumber.setText(oldMeetingHeadline);

        // textview for the author and date
        TextView tmpTextViewAuthorNameForMeeting = (TextView) inflatedView.findViewById(R.id.meetingAuthorAndDate);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR));
        String meetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "dd.MM.yyyy");;
        String meetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "HH:mm");;
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {

            String tmpCanceledAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR));
            String meetingCanceledDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "dd.MM.yyyy");;
            String meetingCanceledTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "HH:mm");;
            String tmpTextAuthorNameMeetingWithCancele = String.format(context.getResources().getString(R.string.meetingOverviewCreateMeetingAuthorAndDateWithCancele), tmpAuthorName, meetingDate, meetingTime, tmpCanceledAuthorName, meetingCanceledDate, meetingCanceledTime);
            tmpTextViewAuthorNameForMeeting.setText(Html.fromHtml(tmpTextAuthorNameMeetingWithCancele));

        } else {
            String tmpTextAuthorNameMeeting = String.format(context.getResources().getString(R.string.meetingOverviewCreateMeetingAuthorAndDate), tmpAuthorName, meetingDate, meetingTime);

            // check if meeting is canceled by client
            String tmpTextClientCanceledMeeting = "";
            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED)) == 1) {
                String tmpClientCanceledDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME)), "dd.MM.yyyy");
                String tmpClientCanceledTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME)), "HH:mm");
                tmpTextClientCanceledMeeting = String.format(context.getResources().getString(R.string.meetingOverviewClientCanceledMeetingAuthorAndDate), tmpClientCanceledDate, tmpClientCanceledTime);
            }
            tmpTextViewAuthorNameForMeeting.setText(Html.fromHtml(tmpTextAuthorNameMeeting+ " " + tmpTextClientCanceledMeeting));
        }


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

        // textview for meeting hint text
        TextView tmpTextViewMeetingHintText = (TextView) inflatedView.findViewById(R.id.meetingHintText);
        String tmpMeetingHintText = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT));
        tmpTextViewMeetingHintText.setText(tmpMeetingHintText);

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
