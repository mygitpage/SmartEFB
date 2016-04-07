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

    }


    private void openDB() {

        myDb = new DBAdapter(this);
    }


    private void displayText (String message) {

        TextView txtDbView = (TextView) findViewById(R.id.textDbDisplay);
        txtDbView.setText(message);

    }


    public void onClick_AddRecord (View v) {
        //displayText("Add a record");





        long newID = myDb.insertRowChatMessage("ich", "Hier steht die Nachricht", 1, 1);

        displayText ("Record added!");

        Cursor cursor = myDb.getRowChatMessage(newID);
        displayRecordSet(cursor);

    }

    public void onClick_ClearAll (View v) {
        displayText("Clear all data");
        myDb.deleteAllChatMessage();


    }

    public void onClick_DisplayRecords (View v) {
        //displayText("Dispaly all resords");

        Cursor cursor = myDb.getAllRowsChatMessage();
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

            do {
                int id = cursor.getInt(DBAdapter.COL_ROWID);

                String write_time = cursor.getString(DBAdapter.CHAT_MESSAGE_COL_WRITE_TIME);
                String author_name = cursor.getString(DBAdapter.CHAT_MESSAGE_COL_AUTHOR_NAME);
                String message = cursor.getString(DBAdapter.CHAT_MESSAGE_COL_MESSAGE);
                int role = cursor.getInt(DBAdapter.CHAT_MESSAGE_COL_ROLE);
                int status = cursor.getInt(DBAdapter.CHAT_MESSAGE_COL_STATUS);




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
