package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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

    // for prefs
    SharedPreferences prefs;


    // Own constructor
    public MeetingOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewCursorAdapterContext = context;

        // init the DB
        myDb = new DBAdapter(context);

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

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

        // check if meeting is canceled by client
        String tmpTextClientCanceledMeeting = "";
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED)) == 1) {
            String tmpClientCanceledDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME)), "dd.MM.yyyy");
            String tmpClientCanceledTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED_TIME)), "HH:mm");
            tmpTextClientCanceledMeeting = String.format(context.getResources().getString(R.string.meetingOverviewClientCanceledMeetingAuthorAndDate), tmpClientCanceledDate, tmpClientCanceledTime);

            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_MEETING_KEY_STATUS)) == 0) {
                String tmpNotSendToServer = context.getResources().getString(R.string.meetingOverviewClientCanceledNotSendToServer);
                tmpTextClientCanceledMeeting = tmpTextClientCanceledMeeting + " " + tmpNotSendToServer;
            }



        }
        tmpTextViewAuthorNameForMeeting.setText(Html.fromHtml(tmpTextAuthorNameMeeting+ " " + tmpTextClientCanceledMeeting));



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



        if (prefs.getBoolean(ConstansClassMeeting.namePrefsMeeting_ClientCanceleMeeting_OnOff, false) && cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED)) == 0) { // show cancele link for meeting

            TextView tmpTextViewClientCanceledLink = (TextView) inflatedView.findViewById(R.id.meetingCanceleLink);

            final Uri.Builder meetingClientCanceleLinkBuilder = new Uri.Builder();
            meetingClientCanceleLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("meeting")
                    .appendQueryParameter("meeting_id", Long.toString(cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID))))
                    .appendQueryParameter("com", "meeting_client_canceled");

            String tmpLinkClientCanceledMeeting = context.getResources().getString(context.getResources().getIdentifier("meetingOverviewClientCanceledLinkText", "string", context.getPackageName()));

            // generate link for output
            Spanned tmpMeetingCanceledLink = Html.fromHtml("<a href=\"" + meetingClientCanceleLinkBuilder.build().toString() + "\">" + tmpLinkClientCanceledMeeting + "</a>");

            // and set to textview
            tmpTextViewClientCanceledLink.setVisibility(View.VISIBLE);
            tmpTextViewClientCanceledLink.setText(tmpMeetingCanceledLink);
            tmpTextViewClientCanceledLink.setMovementMethod(LinkMovementMethod.getInstance());

        }


        // meeting is canceled by client -> show hint
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_CANCELED)) == 1) {
            TextView tmpTextViewHintMeetingIsCanceled = (TextView) inflatedView.findViewById(R.id.meetingCanceleByClientHint);
            tmpTextViewHintMeetingIsCanceled.setVisibility(View.VISIBLE);

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
