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

        private int oldRole = -1;


        // Default constructor
        public ConnectBookCursorAdapter(Context context, Cursor cursor, int flags) {

            super(context, cursor, flags);

            cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {


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

            long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));

            String author = cursor.getString(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_AUTHOR_NAME)) + " - " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm");

            textViewAuthor.setText(author);


        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {





            int role = cursor.getInt(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_ROLE));
            long writeTime = cursor.getLong(cursor.getColumnIndex(DBAdapter.CHAT_MESSAGE_KEY_WRITE_TIME));


            if (cursor.isFirst()) {

                Log.d("ConnectBook","First Element ("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");

                oldRole = role;

                switch (role) {
                    case 0:


                            return cursorInflater.inflate(R.layout.list_item_message_left, parent, false);



                    case 1:



                            return cursorInflater.inflate(R.layout.list_item_message_right, parent, false);









                }


            }



            switch (role) {
                case 0:
                    if (role == oldRole) {
                        Log.d("ConnectBook","Next Left("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");
                        return cursorInflater.inflate(R.layout.list_item_message_nextleft, parent, false);
                    }
                    else {
                        Log.d("ConnectBook","From Left("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");
                        oldRole = role;
                        return cursorInflater.inflate(R.layout.list_item_message_left, parent, false);

                    }

                case 1:

                    if (role == oldRole) {
                        Log.d("ConnectBook","Next Right("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");
                        return cursorInflater.inflate(R.layout.list_item_message_right, parent, false);
                    }
                    else {
                        Log.d("ConnectBook","From Right("+EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy,HH:mm")+")");
                        oldRole = role;
                        return cursorInflater.inflate(R.layout.list_item_message_right, parent, false);

                    }







            }

            // default Role -> center
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
