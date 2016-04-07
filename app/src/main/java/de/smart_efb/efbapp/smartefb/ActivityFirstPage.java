package de.smart_efb.efbapp.smartefb;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityFirstPage extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_first_page);


        Button buttonConnectBook = (Button) findViewById(R.id.btnConnectBook);

        buttonConnectBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ActivityConnectBook.class);

                startActivity(intent);

            }
        });



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }







    /*protected void onStart() {

        super.onStart();

        ActionBar actionBar = getActionBar();

        actionBar.setSubtitle("First Page");
        actionBar.setTitle("Smart EFB");
    }
    */

}
