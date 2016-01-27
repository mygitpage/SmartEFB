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
        String message ="";



        if (cursor.moveToFirst()) {



            Toast.makeText(this, "Rows:" + cursor.getCount() + "Col:" + cursor.getColumnCount(), Toast.LENGTH_SHORT).show();



            do {
                int id = cursor.getInt(DBAdapter.COL_ROWID);
                String name = cursor.getString(DBAdapter.COL_NAME);
                int studentNumber = cursor.getInt(DBAdapter.COL_STUDENTNUM);
                String favColor = cursor.getString(DBAdapter.COL_FAVCOLOUR);


                message += "id= " + id
                        + ", Name= " + name
                        + ", #= " + studentNumber
                        + ", Color= " + favColor
                        + "\n";
            } while (cursor.moveToNext());

        }

        cursor.close();




        displayText(message);



    }


}
