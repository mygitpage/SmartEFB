package de.smart_efb.efbapp.smartefb;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityConnectBook extends AppCompatActivity {


    // reference to the DB
    DBAdapter myDb;

    // reference cursorAdapter for the listview
    ConnectBookCursorAdapter dataAdapter;

    // shared prefs for the settings
    SharedPreferences prefs;

    // reference for the toolbar
    Toolbar toolbar;
    ActionBar actionBar;

    // variables for the connect book
    int roleConnectBook; // the role 0=mother; 1=father; 2=third
    String userNameConnectBook; // the users name


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_connect_book);

        // init the connect book
        initConnectBook();

        final EditText txtInputMsg = (EditText) findViewById(R.id.inputMsg);


        // send button init
        Button buttonSendConnectBook = (Button) findViewById(R.id.buttonSendMessage);

        // onClick send button
        buttonSendConnectBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long newID = myDb.insertRowChatMessage(userNameConnectBook, System.currentTimeMillis(), txtInputMsg.getText().toString(), roleConnectBook, 2, false);

                txtInputMsg.setText("");

                displayMessageSet();

                displayToast();

            }
        });

        displayMessageSet();

    }



    private void initConnectBook() {

        // init the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarConnectBook);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setSubtitle("Untertitel");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // init the DB
        myDb = new DBAdapter(getApplicationContext());

        // init the prefs
        prefs = this.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, MODE_PRIVATE);

        // init the connect book variables
        roleConnectBook = prefs.getInt(ConstansClassConnectBook.namePrefsConnectBookRole, 0);
        userNameConnectBook = prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Jon Dow");


    }



    private void displayToast() {
        Toast.makeText(this," Nachricht eingetragen ", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void displayMessageSet () {

        Cursor cursor = myDb.getAllRowsChatMessage();

        // find the listview
        ListView listView = (ListView) findViewById(R.id.list_view_messages);

        // new dataadapter
        dataAdapter = new ConnectBookCursorAdapter(
                ActivityConnectBook.this,
                cursor,
                0);

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


    }


}
