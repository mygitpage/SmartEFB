package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


// https://coderwall.com/p/fmavhg/android-cursoradapter-with-custom-layout-and-how-to-use-it



/**
 * Created by ich on 31.03.16.
 */
public class ConnectBookCursorAdapter extends CursorAdapter {



        private LayoutInflater cursorInflater;

        private int switschLeftRight;

        // Default constructor
        public ConnectBookCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switschLeftRight = 0;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textViewMessage = (TextView) view.findViewById(R.id.txtMsg);
            String title = cursor.getString(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_MESSAGE));
            textViewMessage.setText(title);


            TextView textViewAuthor = (TextView) view.findViewById(R.id.lblMsgFrom);

            long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));

            String author = cursor.getString(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_AUTHOR_NAME)) + " " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy HH:mm:ss");

            textViewAuthor.setText(author);


        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {


            int role = cursor.getInt(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_ROLE));

            switch (role) {
                case 0: return cursorInflater.inflate(R.layout.list_item_message_left, parent, false);

                case 1: return cursorInflater.inflate(R.layout.list_item_message_right, parent, false);


            }

            // default Role -> center
            return cursorInflater.inflate(R.layout.list_item_message_center, parent, false);

        }






}
