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
import android.widget.TextView;

public class ActivityThree extends AppCompatActivity {



    DBAdapter myDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_three);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        openDB();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }


    private void openDB() {

        myDb = new DBAdapter(this);
        myDb.open();

    }
    private void closeDB() {
        myDb.close();

    }

    private void displayText (String message) {

        TextView txtDbView = (TextView) findViewById(R.id.textDbDisplay);
        txtDbView.setText(message);

    }


    public void onClick_AddRecord (View v) {
        //displayText("Add a record");

        long newID = myDb.insertRow("Ich",8789,"Blue");

    }

    public void onClick_ClearAll (View v) {
        displayText("Clear all data");
    }

    public void onClick_DisplayRecords (View v) {
        //displayText("Dispaly all resords");

        Cursor cursor = myDb.getAllRows();
        displayRecordSet(cursor);


    }




    public void onClick_DBResultBack (View v) {

        Intent intent = new Intent();

        intent.putExtra("resultString", "Database Result String");
        setResult(RESULT_OK, intent);
        finish();


    }

    private void displayRecordSet(Cursor cursor) {
        String message ="Hi!";


        displayText(message);



    }


}
