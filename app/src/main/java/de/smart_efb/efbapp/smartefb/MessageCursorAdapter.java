package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 26.02.2018.
 */

public class MessageCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;

    private Context messageCursorAdapterContext;

    // previous date string of cursor element
    private String previousDateString = "";

    // show message group date at begin -> true
    Boolean showMessageGroupFirstDateChange = false;
    // show message group date at end -> true
    Boolean showMessageGroupLastDateChange = false;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;


    // Default constructor
    public MessageCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        messageCursorAdapterContext = context;

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init the prefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        long writeTimeNext = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));

        // show date group at end
        if (showMessageGroupLastDateChange) {

            // go to next element only when it is not last
            if (!cursor.isLast()) {
                cursor.moveToNext();
                writeTimeNext = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));
                cursor.moveToPrevious();
            }

            LinearLayout dateZoneLast = (LinearLayout) view.findViewById(R.id.connectBookDateParentLast);

            if (dateZoneLast != null) {
                dateZoneLast.setVisibility(View.VISIBLE);
                TextView textViewMessageLast = (TextView) view.findViewById(R.id.connectBookDateTextLast);
                textViewMessageLast.setText(EfbHelperClass.timestampToDateFormat(writeTimeNext, "dd.MM.yyyy"));
            }
            showMessageGroupLastDateChange = false;
        }

        // show date group at begin
        if (showMessageGroupFirstDateChange || cursor.isFirst()) {

            LinearLayout dateZoneFirst = (LinearLayout) view.findViewById(R.id.connectBookDateParentFirst);

            if (dateZoneFirst != null) {
                dateZoneFirst.setVisibility(View.VISIBLE);
                TextView textViewMessageFirst = (TextView) view.findViewById(R.id.connectBookDateTextFirst);
                textViewMessageFirst.setText(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"));
            }
            showMessageGroupFirstDateChange = false;
        }
    }


    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup parent) {

        // init the DB
        final DBAdapter myDb = new DBAdapter(mContext);

        // inflate view
        View inflatedView;

        // context for cursor adapter
        final Context context = mContext;

        // set row id of comment from db for timer update
        final Long rowIdForUpdate = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));

        // rightViewCurrent: true -> current right view is new View; false-> other view ist new View
        Boolean rightViewCurrent = false;

        // role and write time of current element of cursor
        int role = cursor.getInt(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_ROLE));
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));

        // role and write time of previous element of cursor
        int rolePrevoius = -1;
        long writeTimePrevoius = 0;

        // go to previous element only when it is not first
        if (!cursor.isFirst()) {
            cursor.moveToPrevious();
            rolePrevoius = cursor.getInt(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_ROLE));
            writeTimePrevoius = cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_WRITE_TIME));
            cursor.moveToNext();
            previousDateString = EfbHelperClass.timestampToDateFormat(writeTimePrevoius, "dd.MM.yyyy");
        }

        // last element of cursor?
        if (cursor.isLast()) {

            // init show signals
            showMessageGroupFirstDateChange = false;
            showMessageGroupLastDateChange = false;

            if (!previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {
                showMessageGroupFirstDateChange = true;
            }

            switch (role) {
                case 0:
                    if (role == rolePrevoius) {
                        inflatedView = cursorInflater.inflate(R.layout.list_item_message_nextleft, parent, false);
                    }
                    else {
                        inflatedView = cursorInflater.inflate(R.layout.list_item_message_left, parent, false);
                    }
                    break;
                case 1:
                    if (role == rolePrevoius) {
                        inflatedView = cursorInflater.inflate(R.layout.list_item_message_nextright, parent, false);
                    }
                    else {
                        inflatedView = cursorInflater.inflate(R.layout.list_item_message_right, parent, false);
                    }
                    rightViewCurrent = true;
                    break;
                default:
                    // set default view
                    inflatedView = cursorInflater.inflate(R.layout.list_item_message_center, parent, false);
                    break;
            }

        } else { // cursor is not last! -> the other elements of cursor

            switch (role) {
                case 0:
                    if (role == rolePrevoius) {

                        if (previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {
                            inflatedView = cursorInflater.inflate(R.layout.list_item_connect_book_nextleft, parent, false);
                        } else {
                            showMessageGroupFirstDateChange = true;
                            inflatedView = cursorInflater.inflate(R.layout.list_item_connect_book_left, parent, false);
                        }
                    } else {
                        if (!previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {
                            showMessageGroupFirstDateChange = true;
                        }
                        inflatedView = cursorInflater.inflate(R.layout.list_item_connect_book_left, parent, false);
                    }
                    break;
                case 1:
                    if (role == rolePrevoius) {

                        if (previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {
                            inflatedView = cursorInflater.inflate(R.layout.list_item_connect_book_nextright, parent, false);
                        } else {
                            showMessageGroupFirstDateChange = true;
                            inflatedView = cursorInflater.inflate(R.layout.list_item_connect_book_right, parent, false);
                        }
                    } else {
                        if (!previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {
                            showMessageGroupFirstDateChange = true;
                        }
                        inflatedView = cursorInflater.inflate(R.layout.list_item_connect_book_right, parent, false);
                    }
                    rightViewCurrent = true;
                    break;
                default:
                    // set default view
                    inflatedView = cursorInflater.inflate(R.layout.list_item_connect_book_center, parent, false);
                    break;
            }
        }

        // default Role -> center
        if (!previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {
            showMessageGroupFirstDateChange = true;
        }

        // set timer only, when right view is current view
        if (rightViewCurrent ) {

            // textview for status 0 of the last actual message -> message not send yet!
            final TextView tmpTextViewSendInfoLastActualMessage = (TextView) inflatedView.findViewById(R.id.textSendInfoActualMessage);
            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_STATUS)) == 0) {
                String tmpTextSendInfoLastActualMessage = context.getResources().getString(R.string.textConnectBookMessageNotSendYet);
                tmpTextViewSendInfoLastActualMessage.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualMessage.setText(tmpTextSendInfoLastActualMessage);

            } else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_STATUS)) == 1) {
                // textview for status 1 of the last actual message -> message send to server 


            }
        }

        // show message text
        TextView textViewMessage = (TextView) inflatedView.findViewById(R.id.txtMsg);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_MESSAGE));
        textViewMessage.setText(title);

        // show message author and date with new message
        TextView textViewAuthor = (TextView) inflatedView.findViewById(R.id.lblMsgFrom);
        String tmpNewMessage = "";
        // check if message entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_NEW_ENTRY)) == 1) {
            tmpNewMessage = context.getResources().getString(R.string.newEntryText);
            myDb.deleteStatusNewEntryConnectBookMessage(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }
        String tmpAuthorandDate = context.getResources().getString(R.string.textConnectBookMessageAuthorAndDateLocale);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_STATUS)) == 4) {tmpAuthorandDate = context.getResources().getString(R.string.textConnectBookMessageAuthorAndDate);}
        tmpAuthorandDate = String.format(tmpAuthorandDate, cursor.getString(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MESSAGE_KEY_LOCAL_TIME)), "dd.MM.yyyy - HH:mm"), tmpNewMessage);
        textViewAuthor.setText(Html.fromHtml(tmpAuthorandDate));

        // close DB connection
        myDb.close();

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
