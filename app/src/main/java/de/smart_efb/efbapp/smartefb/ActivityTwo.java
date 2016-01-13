package de.smart_efb.efbapp.smartefb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_two);


        String[] colClicked = getIntent().getStringArrayExtra("clickedCol");
        String[] rowClicked = getIntent().getStringArrayExtra("clickedRow");
        int countClicked = getIntent().getIntExtra("countClicked",-1);

        if (countClicked > -1) {


            Toast.makeText(this, "Number of Clicked: " + countClicked, Toast.LENGTH_SHORT).show();


        }


    }
}
