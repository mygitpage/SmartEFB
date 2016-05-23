package de.smart_efb.efbapp.smartefb;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by ich on 18.04.16.
 */
public class ActivityDynamicButtons extends AppCompatActivity {


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_dynamic_buttons);

        initShowMainMenueElement();

        addMainMenueElements();

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

       GridLayout gridLayout = (GridLayout) findViewById(R.id.dynamicLayout);

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
                        elemParams.setMargins(elemMargin, elemMargin, elemMargin, elemMargin);
                        btnButton.setLayoutParams(elemParams);


                        //btnButton.setBackgroundColor(bgColorButtonMainMenue);


                        btnButton.setTextColor(txtColorButtonMainMenue);

                        btnButton.setTextSize(10);





                        btnButton.setBackgroundResource(R.drawable.main_menue_drawable_background);



                        btnButton.setText(mainMenueElementTitle[countElem]);
                        btnButton.setId(this.getResources().getIdentifier(tmpMainMenueButtonName, "id", this.getPackageName()));
                        btnButton.setOnClickListener(new DynamicButtonOnClickListener(ActivityDynamicButtons.this, countElem));
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
                        elemParams.setMargins(elemMargin, elemMargin, elemMargin, elemMargin);
                        btnButton.setLayoutParams(elemParams);
                        //btnButton.setBackgroundColor(bgColorButtonMainMenue);
                        btnButton.setTextColor(txtColorButtonMainMenue);


                        btnButton.setTextSize(10);





                        btnButton.setBackgroundResource(R.drawable.main_menue_drawable_background);




                        btnButton.setText(mainMenueElementTitle[countElem]);
                        btnButton.setId(this.getResources().getIdentifier(tmpMainMenueButtonName, "id", this.getPackageName()));
                        btnButton.setOnClickListener(new DynamicButtonOnClickListener(ActivityDynamicButtons.this,countElem));
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


}



/*
                Button btnButton = new Button(this);

                if (countCol >= gridColumnCount) {
                    countCol = 0;
                    countRow++;
                }

                col = GridLayout.spec(countCol);
                row = GridLayout.spec(countRow);

                String tmpMainMenueButtonName ="mainMenueButtonId_" + (numberOfButtons+1);

                GridLayout.LayoutParams first = new GridLayout.LayoutParams(row, col);
                first.width = halfScreenWidth;
                first.height = quarterScreenWidth;
                first.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
                btnButton.setLayoutParams(first);
                btnButton.setBackgroundColor(bgColorButtonMainMenue);
                btnButton.setTextColor(txtColorButtonMainMenue);
                btnButton.setText(menueButtonsTitle[countBtn]);
                btnButton.setId(this.getResources().getIdentifier(tmpMainMenueButtonName, "id", this.getPackageName()));
                btnButton.setOnClickListener(mainMenueButtonClicked);
                gridLayout.addView(btnButton, first);

                countCol++;
               */


/*
        if (showMenueElementaryButton[0]) { // Show the FAQ-Button?



            // Define the Button FAQ and add him to the grid
            Button btnButtonMenueFaq = new Button(this);
            GridLayout.LayoutParams paramsButtonMenueFaq = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
            paramsButtonMenueFaq.width = screenWidth - 2 * btnMargin;
            paramsButtonMenueFaq.height = quarterScreenWidth;
            paramsButtonMenueFaq.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
            btnButtonMenueFaq.setLayoutParams(paramsButtonMenueFaq);
            btnButtonMenueFaq.setBackgroundColor(bgColorButtonMainMenue);
            btnButtonMenueFaq.setTextColor(txtColorButtonMainMenue);
            btnButtonMenueFaq.setText(getResources().getString(R.string.ButtonMenueFaq));
            btnButtonMenueFaq.setOnClickListener(mainMenueButtonClicked);
            gridLayout.addView(btnButtonMenueFaq, paramsButtonMenueFaq);

        }

        if (showMenueNextMeetingText) { // Show the next meeting text?

            // Next row in the grid layout
            countRow++;

            // Define the text for the next meeting and add him to the grid
            TextView txtViewNewDate = new TextView(this);
            GridLayout.LayoutParams paramsTxtViewNewDate = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
            paramsTxtViewNewDate.width = screenWidth - 2 * btnMargin;
            paramsTxtViewNewDate.height = quarterScreenWidth;
            paramsTxtViewNewDate.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
            txtViewNewDate.setLayoutParams(paramsTxtViewNewDate);
            txtViewNewDate.setBackgroundColor(Color.WHITE);
            txtViewNewDate.setTextColor(Color.DKGRAY);
            txtViewNewDate.setGravity(Gravity.CENTER);
            txtViewNewDate.setText("Hier steht dann der neue Termin");
            gridLayout.addView(txtViewNewDate, paramsTxtViewNewDate);

        }



        if (showMenueElementaryButton[1]) { // Show the CreateNextMeeting-Button?

            // Next row in the grid layout
            countRow++;

            // Define the Button CreateNextMeeting and add him to the grid
            Button btnButtonMakeMeeting = new Button(this);
            GridLayout.LayoutParams paramsButtonMakeMeeting = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
            paramsButtonMakeMeeting.width = screenWidth - 2 * btnMargin;
            paramsButtonMakeMeeting.height = quarterScreenWidth;
            paramsButtonMakeMeeting.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
            btnButtonMakeMeeting.setLayoutParams(paramsButtonMakeMeeting);
            btnButtonMakeMeeting.setBackgroundColor(bgColorButtonMainMenue);
            btnButtonMakeMeeting.setTextColor(txtColorButtonMainMenue);
            btnButtonMakeMeeting.setText(getResources().getString(R.string.ButtonMenueMakeMeeting));
            btnButtonMakeMeeting.setOnClickListener(mainMenueButtonClicked);
            gridLayout.addView(btnButtonMakeMeeting, paramsButtonMakeMeeting);

        }

        if (showMenueElementaryButton[2]) { // Show the EmergencyHelp-Button?

            // Next row in the grid layout
            countRow++;

            // Define the Button EmergencyHelp and add him to the grid
            Button btnButtonEmergencyHelp = new Button(this);
            GridLayout.LayoutParams paramsButtonEmergencyHelp = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
            paramsButtonEmergencyHelp.width = screenWidth - 2 * btnMargin;
            paramsButtonEmergencyHelp.height = quarterScreenWidth;
            paramsButtonEmergencyHelp.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
            btnButtonEmergencyHelp.setLayoutParams(paramsButtonEmergencyHelp);
            btnButtonEmergencyHelp.setBackgroundColor(bgColorButtonMainMenue);
            btnButtonEmergencyHelp.setTextColor(txtColorButtonMainMenue);
            btnButtonEmergencyHelp.setText(getResources().getString(R.string.ButtonMenueEmergencyHelp));
            btnButtonEmergencyHelp.setOnClickListener(mainMenueButtonClicked);
            gridLayout.addView(btnButtonEmergencyHelp, paramsButtonEmergencyHelp);

        }
        */
