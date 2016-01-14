package de.smart_efb.efbapp.smartefb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_two);


        String[] colClicked = getIntent().getStringArrayExtra("clickedCol");
        String[] rowClicked = getIntent().getStringArrayExtra("clickedRow");
        int countClicked = getIntent().getIntExtra("countClicked", -1);


        String txtClickedString = "Sie haben bisher folgende Buttons angeklicket: ";

        TextView txtClickedView = (TextView) findViewById(R.id.txtClickedView);

        EditText txtInputView = (EditText) findViewById(R.id.TextActivityTwo);

        if (countClicked > -1) {


            Toast.makeText(this, "Number of Clicked: " + countClicked, Toast.LENGTH_SHORT).show();

            for (int t=0; t< countClicked; t++) {

                txtClickedString += colClicked[t] + "," + rowClicked[t] + "-";

            }

            txtClickedView.setText(txtClickedString);

        }


    }
}
