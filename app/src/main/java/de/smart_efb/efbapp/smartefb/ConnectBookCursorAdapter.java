package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ich on 31.03.16.
 */
public class ConnectBookCursorAdapter extends CursorAdapter {



        private LayoutInflater cursorInflater;

        // Default constructor
        public ConnectBookCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            cursorInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

        }

        public void bindView(View view, Context context, Cursor cursor) {
            TextView textViewMessage = (TextView) view.findViewById(R.id.message);
            String title = cursor.getString( cursor.getColumnIndex( DBAdapter.KEY_MESSAGE ) );
            textViewMessage.setText(title);

        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // R.layout.list_item_message_complet is the xml layout for each row
            return cursorInflater.inflate(R.layout.list_item_message_complet, parent, false);
        }






}
