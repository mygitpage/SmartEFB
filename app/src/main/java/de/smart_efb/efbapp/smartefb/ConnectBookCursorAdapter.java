package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * Created by ich on 31.03.16.
 */
public class ConnectBookCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    private Context connectBookCursorAdapterContext;





    // previous date string of cursor element
    private String previousDateString = "";

    // show message group date at begin -> true
    Boolean showMessageGroupFirstDateChange = false;
    // show message group date at end -> true
    Boolean showMessageGroupLastDateChange = false;





    // Default constructor
    public ConnectBookCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        connectBookCursorAdapterContext = context;

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        long writeTimeNext = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));

        int role = cursor.getInt(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_ROLE));

        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));


        // show date group at end
        if (showMessageGroupLastDateChange) {

            // go to next element only when it is not last
            if (!cursor.isLast()) {
                cursor.moveToNext();

                writeTimeNext = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));
                cursor.moveToPrevious();


            }


            LinearLayout dateZoneLast = (LinearLayout) view.findViewById(R.id.connectBookDateParentLast);

            if (dateZoneLast != null) {
                dateZoneLast.setVisibility(View.VISIBLE);
                TextView textViewMessageLast = (TextView) view.findViewById(R.id.connectBookDateTextLast);
                textViewMessageLast.setText(EfbHelperClass.timestampToDateFormat(writeTimeNext, "dd.MM.yyyy"));
            }

            showMessageGroupLastDateChange = false;

            //((ActivityConnectBook) connectBookCursorAdapterContext).setShowMessageGroupDateChange(false);
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

            //((ActivityConnectBook) connectBookCursorAdapterContext).setShowMessageGroupDateChange(false);
        }




            /*
            int role = cursor.getInt(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_ROLE));

            Log.d("ConnectBook","Number: "+ cursor.getPosition());

            if (cursor.isFirst()) {
                oldRole = -1;
                Log.d("ConnectBook","RESET!!!!!");
            }



            //LinearLayout parentlayout = (LinearLayout) view.findViewById(R.id.parentBubble);
            LinearLayout childParent = (LinearLayout) view.findViewById(R.id.childBubble);

            Log.d ("ConnectBook","Role: "+role+" OldRole: "+oldRole);

            if (role == 0) {
                if (role == oldRole) {
                    Log.d("ConnectBook","Next Left");
                    childParent.setBackgroundResource(R.drawable.bubblenextleft);
                }
                else {
                    Log.d("ConnectBook","From Left");
                    childParent.setBackgroundResource(R.drawable.bubblefromleft);
                    oldRole = role;
                }

            }

            if (role == 1) {
                if (role == oldRole) {
                    childParent.setBackgroundResource(R.drawable.bubblefromright);
                }
                else {
                    childParent.setBackgroundResource(R.drawable.bubblefromright);
                    oldRole = role;
                }

            }

            /*
            if (message.isMine) {
            layout.setBackgroundResource(R.drawable.bubble2);
            parent_layout.setGravity(Gravity.RIGHT);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.bubble1);
            parent_layout.setGravity(Gravity.LEFT);
        }

             */



        TextView textViewMessage = (TextView) view.findViewById(R.id.txtMsg);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_MESSAGE));
        textViewMessage.setText(title);


        TextView textViewAuthor = (TextView) view.findViewById(R.id.lblMsgFrom);
        String author = cursor.getString(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_AUTHOR_NAME)) + " - " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm");
        textViewAuthor.setText(author);


    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        // role and write time of current element of cursor
        int role = cursor.getInt(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_ROLE));
        long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));

        // role and write time of previous element of cursor
        int rolePrevoius = -1;
        long writeTimePrevoius = 0;

        // go to previous element only when it is not first
        if (!cursor.isFirst()) {
            cursor.moveToPrevious();
            rolePrevoius = cursor.getInt(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_ROLE));
            writeTimePrevoius = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));
            cursor.moveToNext();
            previousDateString = EfbHelperClass.timestampToDateFormat(writeTimePrevoius, "dd.MM.yyyy");
            //Log.d("Connect Book","rolePrev: "+rolePrevoius+" writePrev: "+EfbHelperClass.timestampToDateFormat(writeTimePrevoius, "dd.MM.yyyy,HH:mm"));
        }



        // last element of cursor?
        if (cursor.isLast()) {

            Log.d("ConnectBook","Last Element ("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");



            showMessageGroupFirstDateChange = false;
            showMessageGroupLastDateChange = false;

            switch (role) {
                case 0:
                    if (role == rolePrevoius) {
                        return cursorInflater.inflate(R.layout.list_item_message_nextleft, parent, false);
                    }
                    else {
                        return cursorInflater.inflate(R.layout.list_item_message_left, parent, false);
                    }

                case 1:
                    if (role == rolePrevoius) {
                        return cursorInflater.inflate(R.layout.list_item_message_nextright, parent, false);
                    }
                    else {
                        return cursorInflater.inflate(R.layout.list_item_message_right, parent, false);
                    }

            }

            return cursorInflater.inflate(R.layout.list_item_message_center, parent, false);

        }


        Log.d("ConnectBook","Prev DATE STRING "+ previousDateString);

        // the other elements of cursor
        switch (role) {
            case 0:
                if (role == rolePrevoius) {

                    if (previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {

                        Log.d("ConnectBook","Next Left keine Änderung: ("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");

                        return cursorInflater.inflate(R.layout.list_item_message_nextleft, parent, false);
                    }
                    else {
                        Log.d("ConnectBook","From Left Änderung!: ("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");

                        showMessageGroupFirstDateChange = true;


                        return cursorInflater.inflate(R.layout.list_item_message_left, parent, false);
                    }
                }
                else {
                    Log.d("ConnectBook","From Left("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");

                    if (!previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {

                        Log.d("Connect Book","Mit Änderung!!");
                        showMessageGroupFirstDateChange = true;


                    }

                    return cursorInflater.inflate(R.layout.list_item_message_left, parent, false);

                }

            case 1:

                if (role == rolePrevoius) {

                    if (previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {

                        Log.d("ConnectBook","Next Right Keine Änderung ("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");

                        return cursorInflater.inflate(R.layout.list_item_message_nextright, parent, false);
                    }
                    else {

                        Log.d("ConnectBook","From Right Änderung!: ("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");

                        showMessageGroupFirstDateChange = true;


                        return cursorInflater.inflate(R.layout.list_item_message_right, parent, false);
                    }




                }
                else {
                    Log.d("ConnectBook","From Right("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");

                    if (!previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {

                        Log.d("Connect Book","Mit Änderung right!!");
                        showMessageGroupFirstDateChange = true;


                    }

                    return cursorInflater.inflate(R.layout.list_item_message_right, parent, false);

                }

        }

        // default Role -> center
        Log.d("ConnectBook","Center ("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");
        if (!previousDateString.equals(EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy"))) {

            Log.d("Connect Book","Mit Änderung center!!");
            showMessageGroupFirstDateChange = true;


        }

        return cursorInflater.inflate(R.layout.list_item_message_center, parent, false);
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
