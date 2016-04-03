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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Timestamp;

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





        long newID = myDb.insertRow("ich","Hier steht die Nachricht",1,1);

        displayText ("Record added!");

        Cursor cursor = myDb.getRow(newID);
        displayRecordSet(cursor);

    }

    public void onClick_ClearAll (View v) {
        displayText("Clear all data");
        myDb.deleteAll();


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
        String showMessageString ="";



        if (cursor.moveToFirst()) {



            Toast.makeText(this, "Rows:" + cursor.getCount() + "Col:" + cursor.getColumnCount(), Toast.LENGTH_SHORT).show();



            /*
            public static final int COL_WRITE_TIME = 1;
    public static final int COL_AUTHOR_NAME = 2;
    public static final int COL_MESSAGE = 3;
    public static final int COL_ROLE = 4;
    public static final int COL_TX_TIME = 5;
    public static final int COL_STATUS = 6;
             */



            do {
                int id = cursor.getInt(DBAdapter.COL_ROWID);

                String write_time = cursor.getString(DBAdapter.COL_WRITE_TIME);
                String author_name = cursor.getString(DBAdapter.COL_AUTHOR_NAME);
                String message = cursor.getString(DBAdapter.COL_MESSAGE);
                int role = cursor.getInt(DBAdapter.COL_ROLE);
                int status = cursor.getInt(DBAdapter.COL_STATUS);




                showMessageString += "id= " + id
                        + ", W_Time= " + write_time
                        + ", AName " + author_name
                        + ", Message= " + message
                        + ", Rolle= " + role
                        + ", Status= " + status
                        + "\n";
            } while (cursor.moveToNext());

        }

        cursor.close();




        displayText(showMessageString);



    }


}
