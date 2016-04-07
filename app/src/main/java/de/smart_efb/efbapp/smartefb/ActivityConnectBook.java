package de.smart_efb.efbapp.smartefb;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ActivityConnectBook extends AppCompatActivity {

    DBAdapter myDb;


    ConnectBookCursorAdapter dataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_connect_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        myDb = new DBAdapter(getApplicationContext());


        Button buttonSendConnectBook = (Button) findViewById(R.id.btnSend);

        buttonSendConnectBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText txtInputMsg = (EditText) findViewById(R.id.inputMsg);


                long newID = myDb.insertRowChatMessage("ich", txtInputMsg.getText().toString(), 1, 2);

                txtInputMsg.setText("");

                displayMessageSet();

                displayToast();
            }
        });

        displayMessageSet();




    }

    private void displayToast() {
        Toast.makeText(this," Nachricht eingetragen ", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }







    public void displayMessageSet () {
        //displayText("Dispaly all resords");

        Cursor cursor = myDb.getAllRowsChatMessage();

        //
        // LOOK AT: http://www.mysamplecode.com/2012/07/android-listview-cursoradapter-sqlite.html
        //


        /*

        Override View
        http://stackoverflow.com/questions/4777272/android-listview-with-different-layout-for-each-row?rq=1

        http://android.amberfog.com/?p=296




         */


        //------------------------------------
        // The desired columns to be bound
        /*
        String[] columns = new String[] {
                DBAdapter.KEY_WRITE_TIME,
                DBAdapter.KEY_AUTHOR_NAME,
                DBAdapter.KEY_MESSAGE,
                DBAdapter.KEY_ROLE
        };

        // the XML defined views which the data will be bound to

                int[] to = new int[] {
                R.id.writetime,
                R.id.name,
                R.id.message,
                R.id.role
        };
        */

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        /*
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.list_item_message_complet,
                cursor,
                columns,
                to,
                0);
        */
        ListView listView = (ListView) findViewById(R.id.list_view_messages);
        // Assign adapter to ListView


        dataAdapter = new ConnectBookCursorAdapter(
                ActivityConnectBook.this,
                cursor,
                0);





        listView.setAdapter(dataAdapter);



        //-------------------------------------









    }


}
