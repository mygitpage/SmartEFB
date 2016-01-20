package de.smart_efb.efbapp.smartefb;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_COLS = 3;
    private static final int NUM_ROWS = 6;

    private static  final int REQUEST_CODE_FROM_ACTIVITY_TWO = 100;
    private static  final int REQUEST_CODE_FROM_ACTIVITY_THREE = 200;


    private static int ranCol, ranRow;

    Button buttons[][] = new Button [NUM_ROWS][NUM_COLS];
    RippleDrawable buttonsBackgroundColors [][] = new RippleDrawable [NUM_ROWS][NUM_COLS];


    TextView txtResultActivityTwoView;



    private String[] rowClicked = new String[NUM_COLS*NUM_ROWS];
    private String[] colClicked = new String[NUM_COLS*NUM_ROWS];
    private int countClicked = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button buttonActivityTwo = (Button) findViewById(R.id.buttonActTwo);


        txtResultActivityTwoView = (TextView) findViewById(R.id.textViewActivityOne);


        buttonActivityTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);

                intent.putExtra("clickedRow", rowClicked);
                intent.putExtra("clickedCol", colClicked);
                intent.putExtra("countClicked", countClicked);


//              startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_FROM_ACTIVITY_TWO);

            }
        });



        Button buttonActivityThree = (Button) findViewById(R.id.buttonActThree);

        buttonActivityThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ActivityThree.class);

                startActivityForResult(intent, REQUEST_CODE_FROM_ACTIVITY_THREE);

            }
        });



        createRandomNumber();

        populateButtons();
        
        
    }

    private void createRandomNumber() {

        Random r = new Random();
        ranCol = r.nextInt(NUM_COLS);

        ranRow = r.nextInt(NUM_ROWS);

    }

    private void populateButtons() {

        TableLayout table = (TableLayout) findViewById(R.id.tableForButtons);


        for (int row=0; row < NUM_ROWS; row++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));

            table.addView(tableRow);

            for (int col=0; col < NUM_COLS; col++) {

                final int FINAL_COL = col;
                final int FINAL_ROW = row;

                Button button = new Button(this);

                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));

                button.setText("" + col + "," + row);
                // Sorgt dafuer, dass bei kleinen Buttons der Text reinpasst
                button.setPadding(0, 0, 0, 0);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridButtonClicked(FINAL_COL, FINAL_ROW);
                    }
                });

                tableRow.addView(button);
                buttons[row][col] = button;

                buttonsBackgroundColors[row][col] = (RippleDrawable) button.getBackground();
            }

        }
    }






    private void gridButtonClicked(int col, int row) {
        //Dies ist eine zusaetzliche Zeile
        Toast.makeText(this,"Button clicked: " + col + "," + row , Toast.LENGTH_SHORT).show();

        if (col == ranCol && row == ranRow) {
            Toast.makeText(this," LOESCHEN!!!!!!!! ", Toast.LENGTH_SHORT).show();

            deleteButtonBackground();
            createRandomNumber();

            countClicked = 0;

            return;
        }


        Button button = buttons [row][col];

        //Lock Button size
        lockButtonSizes();


        // ButtonImage wird nicht skaliert
        //button.setBackgroundResource(R.drawable.action_lock_pink);

        // ButtonImage wir skaliert
        int newWidth = button.getWidth();
        int newHeight = button.getHeight();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.action_lock_pink);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
        Resources resource = getResources();
        button.setBackground(new BitmapDrawable(resource, scaledBitmap));

        // Change Button Text
        //button.setTextSize(newWidth);


        rowClicked[countClicked] = String.valueOf(row);
        colClicked[countClicked] = String.valueOf(col);
        countClicked++;


        button.setText("" + col);




    }

    private void deleteButtonBackground() {

        for (int row=0; row < NUM_ROWS; row++) {


            for (int col = 0; col < NUM_COLS; col++) {

                buttons[row][col].setBackgroundResource(0);

                buttons[row][col].setText("" + col + "," + row);

                buttons[row][col].setBackground(buttonsBackgroundColors[row][col]);




            }
        }

    }





    private void lockButtonSizes() {
        for (int row=0; row < NUM_ROWS; row++) {
            for (int col=0; col < NUM_COLS; col++) {

                Button button = buttons[row][col];


                int width = button.getWidth();
                button.setMinWidth(width);
                button.setMaxWidth(width);

                int height = button.getHeight();
                button.setMinHeight(height);
                button.setMaxHeight(height);


            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_FROM_ACTIVITY_TWO) {

            if (resultCode == RESULT_OK) {

                String resultStringFromActivityTwo = data.getStringExtra("resultString");

                txtResultActivityTwoView.setText(resultStringFromActivityTwo);

            }

        } else if (requestCode == REQUEST_CODE_FROM_ACTIVITY_THREE) {

            if (resultCode == RESULT_OK) {

                Toast.makeText(this," Zurück aus der Datenbank\n" + data.getStringExtra("resultString"), Toast.LENGTH_SHORT).show();

            }




        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
