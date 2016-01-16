package de.smart_efb.efbapp.smartefb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        final EditText txtInputView = (EditText) findViewById(R.id.TextActivityTwo);

        Button resultButton = (Button) findViewById(R.id.resultButton);

        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();


                String resultString = txtInputView.getText().toString();
                intent.putExtra("resultString", resultString);
                setResult(RESULT_OK, intent);
                finish();

            }
        });


        if (countClicked > -1) {


            Toast.makeText(this, "Number of Clicked: " + countClicked, Toast.LENGTH_SHORT).show();

            for (int t=0; t< countClicked; t++) {

                txtClickedString += colClicked[t] + "," + rowClicked[t] + "-";

            }

            txtClickedView.setText(txtClickedString);

        }





    }
}
