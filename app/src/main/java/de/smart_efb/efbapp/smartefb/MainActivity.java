package de.smart_efb.efbapp.smartefb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    // Number of MainMenue elements of the first activity
    final int numberOfElements = 13;

    // The grid row and col
    final int gridColumnCount = 2;
    final int gridRowCount = 10;


    // margin between the buttons
    final int elemMargin = 10;

    // id-name of main menue buttons + number
    final String MAIN_MENUE_ID_BUTTON_NAME = "mainMenueElementId_";

    // The Elementtitle
    String[] mainMenueElementTitle = {"textDummy1", "uebergabebuch", "absprachen", "ziele", "praevention", "n.n.", "n.n.","textDummy2", "faq","textDummy3", "vereinbaren", "hilfe", "textDummy4"};
    // Show the element when true
    boolean showMainMenueElement[] = {false,false,false,false,false,false,false,false,false,false,false,false,false};
    // The kind of element (=0: half Button, =1: full button, =2: Textfield, )
    int mainMenueElementKind[] = {2,0,0,0,0,0,0,2,1,2,1,1,2};



    /*
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
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_main);

        initShowMainMenueElement();

        //addMainMenueElements();





        /*

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
        */
        
    }




    private void initShowMainMenueElement() {

        SharedPreferences prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);

        int resIdMainMenueElements;
        String tmpMainMenuePrefseName;

        // Init ShowMenueButtons
        for (int countElem = 0; countElem < numberOfElements; countElem++) {

            tmpMainMenuePrefseName = MAIN_MENUE_ID_BUTTON_NAME + countElem;
            showMainMenueElement[countElem] = prefs.getBoolean(tmpMainMenuePrefseName, false);



            resIdMainMenueElements = getResources().getIdentifier(tmpMainMenuePrefseName, "string", getPackageName());
            if (resIdMainMenueElements != 0) {

                mainMenueElementTitle[countElem] = getResources().getString(resIdMainMenueElements);
            }



            /*
            Beispiel zur Anzeige von Zahlen. Beispielsweise: Sie habe 20 neue Nachrichten
            <string name="welcome_messages">Hello, %1$s! You have %2$d new messages.</string>

            Resources res = getResources();
            String text = String.format(res.getString(R.string.welcome_messages), username, mailCount);


            */

        }


    }



    void addMainMenueElements () {


        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int halfScreenWidth = (int)((screenWidth *0.5) - 2*elemMargin);
        int quarterScreenWidth = (int)(halfScreenWidth * 0.5);

        GridLayout gridLayout = (GridLayout) findViewById(R.id.dynamicMainMenueLayout);

        gridLayout.setColumnCount(gridColumnCount);
        gridLayout.setRowCount(gridRowCount);


        // Define colspan over two cols
        GridLayout.Spec colspan2 = GridLayout.spec(0, 2);


        int countRow = 0, countCol = 0;


        // Get the BG and txt-color from ressource
        int bgColorButtonMainMenue = ContextCompat.getColor(this, R.color.bg_btn_main_menue);
        int txtColorButtonMainMenue = ContextCompat.getColor(this, R.color.txt_btn_main_menue);

        Button btnButton;
        TextView txtViewElement;

        String tmpMainMenueButtonName="";

        GridLayout.LayoutParams elemParams;

        for (int countElem = 0; countElem < numberOfElements; countElem++) {


            if (showMainMenueElement[countElem]) { // Show the element in the main menue?

                switch (mainMenueElementKind[countElem]) { // what kind of element?

                    case 0: // its a half button!
                        btnButton = new Button(this);

                        if (countCol >= gridColumnCount) {
                            countCol = 0;
                            countRow++;
                        }
                        // generate the buttons id
                        tmpMainMenueButtonName = MAIN_MENUE_ID_BUTTON_NAME + countElem;

                        elemParams = new GridLayout.LayoutParams(GridLayout.spec(countRow), GridLayout.spec(countCol));
                        elemParams.width = halfScreenWidth;
                        elemParams.height = quarterScreenWidth;
                        //elemParams.setMargins(elemMargin, elemMargin, elemMargin, elemMargin);
                        btnButton.setLayoutParams(elemParams);


                        //btnButton.setBackgroundColor(bgColorButtonMainMenue);


                        btnButton.setTextColor(txtColorButtonMainMenue);
                        //btnButton.setTransformationMethod(null);
                        btnButton.setTextSize(10);





                        //btnButton.setBackgroundResource(R.drawable.main_menue_drawable_background);



                        btnButton.setText(mainMenueElementTitle[countElem]+"\n(Hinweistext Anzahl)");
                        btnButton.setId(this.getResources().getIdentifier(tmpMainMenueButtonName, "id", this.getPackageName()));
                        btnButton.setOnClickListener(new MainMenueOnClickListener(MainActivity.this, countElem));
                        gridLayout.addView(btnButton, elemParams);

                        countCol++;
                        break;

                    case 1: // its a full button!
                        // new Button
                        btnButton = new Button(this);
                        // Next row in the grid layout?
                        if (countCol != 0) {
                            countRow++;
                        }
                        // generate the buttons id
                        tmpMainMenueButtonName = MAIN_MENUE_ID_BUTTON_NAME + countElem;

                        // Define the full button
                        elemParams = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
                        elemParams.width = screenWidth - 2 * elemMargin;
                        elemParams.height = quarterScreenWidth;
                        //elemParams.setMargins(elemMargin, elemMargin, elemMargin, elemMargin);
                        btnButton.setLayoutParams(elemParams);
                        //btnButton.setBackgroundColor(bgColorButtonMainMenue);
                        btnButton.setTextColor(txtColorButtonMainMenue);
                        //btnButton.setTransformationMethod(null);

                        btnButton.setTextSize(10);





                        //btnButton.setBackgroundResource(R.drawable.main_menue_drawable_background);




                        btnButton.setText(mainMenueElementTitle[countElem]);
                        btnButton.setId(this.getResources().getIdentifier(tmpMainMenueButtonName, "id", this.getPackageName()));
                        btnButton.setOnClickListener(new MainMenueOnClickListener(MainActivity.this,countElem));
                        gridLayout.addView(btnButton, elemParams);

                        if (countCol < gridColumnCount) {
                            countRow++;
                        }
                        break;

                    case 2: // its a full textfield

                        // new textfield
                        txtViewElement = new TextView(this);
                        if (countCol != 0) {
                            countRow++;
                        }

                        // generate the buttons id
                        tmpMainMenueButtonName = MAIN_MENUE_ID_BUTTON_NAME + countElem;

                        // Define the textfield
                        elemParams = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
                        elemParams.width = screenWidth - 2 * elemMargin;
                        elemParams.height = quarterScreenWidth;
                        elemParams.setMargins(elemMargin, elemMargin, elemMargin, elemMargin);
                        txtViewElement.setLayoutParams(elemParams);
                        txtViewElement.setBackgroundColor(Color.WHITE);
                        txtViewElement.setTextColor(Color.DKGRAY);
                        txtViewElement.setGravity(Gravity.CENTER);
                        txtViewElement.setId(this.getResources().getIdentifier(tmpMainMenueButtonName, "id", this.getPackageName()));
                        txtViewElement.setText("Hier steht dann der neue Termin"); // TODO: texte aus den Prefs auslesen und in ein Array schreiben und hier eintragen
                        gridLayout.addView(txtViewElement, elemParams);

                        if (countCol < gridColumnCount) {
                            countRow++;
                        }
                        break;
                    default:
                        break;

                }


            }

        }

    }


    // Return the number of Buttons
    public int getNumberOfButtons () {
        return numberOfElements;
    }

    //Return original title of menue button
    public String menueButtonTitle (int position) {
        return mainMenueElementTitle[position];
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_firstpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {


            case R.id.efb_settings:
                intent = new Intent(getApplicationContext(), ActivityEfbSettings.class);
                startActivity(intent);
                return true;

            case R.id.efb_help:
                intent = new Intent(getApplicationContext(), ActivityEfbHelp.class);
                startActivity(intent);
                return true;


            case R.id.efb_about:
                intent = new Intent(getApplicationContext(), ActivityEfbAbout.class);
                startActivity(intent);
                return true;

            case R.id.efb_paaring:
                intent = new Intent(getApplicationContext(), ActivityEfbPaaring.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }






    /*
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



    public void onClick_CallFirstPage (View v) {
        //Call the "First Page"

        Intent intent = new Intent(getApplicationContext(), ActivityFirstPage.class);

        startActivity(intent);
    }

    */


}
