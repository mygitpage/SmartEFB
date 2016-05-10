package de.smart_efb.efbapp.smartefb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 18.04.16.
 */
public class ActivityDynamicButtons extends AppCompatActivity {


    // Maximium number of Buttons of the first activity
    final int numberOfButtons = 6;

    // The grid row and col
    final int gridColumnCount = 2;
    final int gridRowCount = 7;

    // array of buttons
    Button menueButtons[] = new Button [numberOfButtons];

    // margin between the buttons
    final int btnMargin = 10;

    // The Buttons title
    final String[] menueButtonsTitle = {"Mein Übergabebuch", "Unsere Absprachen", "Meine + Deine Ziele", "Prävention", "Na.N.", "Nb.N."};
    // Show the button when true
    boolean showMenueButton[] = {false,false,false,false,false,false};

    // Show elementary Buttons
    boolean showMenueElementaryButton[] = {false,false,false};

    // Show next meeting text
    boolean showMenueNextMeetingText = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_dynamic_buttons);



        initShowMenueButton();

        addMenueElements();



    }

    private void initShowMenueButton() {

        SharedPreferences prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor;

        String concatMenueButtonsTitle = "";

        for (int countBtn = 0; countBtn < numberOfButtons; countBtn++) {


            showMenueButton[countBtn] = prefs.getBoolean(replaceMenueButtonTitle(countBtn), false);


        }




    }


    void addMenueElements () {



        /*
        Look at:
        http: stackoverflow.com/questions/15082432/how-to-create-button-dynamically-in-android
        */


        // http://stackoverflow.com/questions/21455495/gridlayoutnot-gridview-spaces-between-the-cells
        // http://stackoverflow.com/questions/20871690/dont-understand-how-to-use-gridlayout-spec?lq=1
        // http://stackoverflow.com/questions/10347846/how-to-make-a-gridlayout-fit-screen-size



        int btnIndexNumber = 0;









        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int halfScreenWidth = (int)((screenWidth *0.5) - 2*btnMargin);
        int quarterScreenWidth = (int)(halfScreenWidth * 0.5);




    /*
        GridLayout.Spec row1 = GridLayout.spec(0);
        GridLayout.Spec row2 = GridLayout.spec(1);
        GridLayout.Spec row3 = GridLayout.spec(2);
        GridLayout.Spec row4 = GridLayout.spec(3);
        GridLayout.Spec row5 = GridLayout.spec(4);

        GridLayout.Spec col0 = GridLayout.spec(0);
        GridLayout.Spec col1 = GridLayout.spec(1);


        GridLayout.Spec colspan2 = GridLayout.spec(0, 2);
    */


        GridLayout gridLayout = (GridLayout) findViewById(R.id.dynamicLayout);

        gridLayout.setColumnCount(gridColumnCount);
        gridLayout.setRowCount(gridRowCount);




        int countRow = 0, countCol = 0;

        GridLayout.Spec col,row;


        for (int countBtn = 0; countBtn < numberOfButtons; countBtn++) {


            if (showMenueButton[countBtn]) {


                Button btnButton = new Button(this);



                if (countCol >= gridColumnCount) {
                    countCol = 0;
                    countRow++;
                }




                col = GridLayout.spec(countCol);
                row = GridLayout.spec(countRow);



                GridLayout.LayoutParams first = new GridLayout.LayoutParams(row, col);
                first.width = halfScreenWidth;
                first.height = quarterScreenWidth;
                first.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
                btnButton.setLayoutParams(first);
                btnButton.setBackgroundColor(Color.BLUE);
                btnButton.setText(menueButtonsTitle[countBtn]);
                gridLayout.addView(btnButton, first);


                menueButtons[btnIndexNumber]= btnButton;

                countCol++;
                btnIndexNumber++;

            }

        }


        GridLayout.Spec colspan2 = GridLayout.spec(0, 2);



        countRow++;



        Button btnButtonMenueFaq = new Button(this);
        GridLayout.LayoutParams paramsButtonMenueFaq = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
        paramsButtonMenueFaq.width = screenWidth - 2 * btnMargin;
        paramsButtonMenueFaq.height = quarterScreenWidth;
        paramsButtonMenueFaq.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButtonMenueFaq.setLayoutParams(paramsButtonMenueFaq);
        btnButtonMenueFaq.setBackgroundColor(Color.BLUE);
        btnButtonMenueFaq.setText(getResources().getString(R.string.ButtonMenueFaq));
        gridLayout.addView(btnButtonMenueFaq, paramsButtonMenueFaq);


        countRow++;

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



        countRow++;

        Button btnButtonMakeMeeting = new Button(this);
        GridLayout.LayoutParams paramsButtonMakeMeeting = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
        paramsButtonMakeMeeting.width = screenWidth - 2 * btnMargin;
        paramsButtonMakeMeeting.height = quarterScreenWidth;
        paramsButtonMakeMeeting.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButtonMakeMeeting.setLayoutParams(paramsButtonMakeMeeting);
        btnButtonMakeMeeting.setBackgroundColor(Color.BLUE);
        btnButtonMakeMeeting.setText(getResources().getString(R.string.ButtonMenueMakeMeeting));
        gridLayout.addView(btnButtonMakeMeeting, paramsButtonMakeMeeting );



        countRow++;

        Button btnButtonEmergencyHelp = new Button(this);
        GridLayout.LayoutParams paramsButtonEmergencyHelp = new GridLayout.LayoutParams(GridLayout.spec(countRow), colspan2);
        paramsButtonEmergencyHelp.width = screenWidth - 2 * btnMargin;
        paramsButtonEmergencyHelp.height = quarterScreenWidth;
        paramsButtonEmergencyHelp.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButtonEmergencyHelp.setLayoutParams(paramsButtonMakeMeeting);
        btnButtonEmergencyHelp.setBackgroundColor(Color.BLUE);
        btnButtonEmergencyHelp.setText(getResources().getString(R.string.ButtonMenueEmergencyHelp));
        gridLayout.addView(btnButtonEmergencyHelp, paramsButtonEmergencyHelp );








        /*

        Button btnButton1 = new Button(this);
        GridLayout.LayoutParams first = new GridLayout.LayoutParams(row1, col0);
        first.width = halfScreenWidth;
        first.height = quarterScreenWidth;
        first.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButton1.setLayoutParams(first);
        btnButton1.setBackgroundColor(Color.BLUE);
        btnButton1.setText("Mein Übergabebuch");
        gridLayout.addView(btnButton1, first);



        Button btnButton2 = new Button(this);
        GridLayout.LayoutParams second = new GridLayout.LayoutParams(row1, col1);
        second.width = halfScreenWidth;
        second.height = quarterScreenWidth;
        second.setMargins(btnMargin,btnMargin,btnMargin,btnMargin);
        btnButton2.setLayoutParams(second);
        btnButton2.setBackgroundColor(Color.BLUE);
        btnButton2.setText("Unsere Absprachen");
        gridLayout.addView(btnButton2, second);




        Button btnButton3 = new Button(this);
        GridLayout.LayoutParams third = new GridLayout.LayoutParams(row2, col0);
        third.width = halfScreenWidth;
        third.height = quarterScreenWidth;
        third.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButton3.setLayoutParams(third);
        btnButton3.setBackgroundColor(Color.BLUE);
        btnButton3.setText("Meine+Deine Ziele");
        gridLayout.addView(btnButton3, third);


        Button btnButton4 = new Button(this);
        GridLayout.LayoutParams fourth = new GridLayout.LayoutParams(row2, col1);
        fourth.width = halfScreenWidth;
        fourth.height = quarterScreenWidth;
        fourth.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButton4.setLayoutParams(fourth);
        btnButton4.setBackgroundColor(Color.BLUE);
        btnButton4.setText("Prävention");
        gridLayout.addView(btnButton4, fourth);



        Button btnButton5 = new Button(this);
        GridLayout.LayoutParams fifth = new GridLayout.LayoutParams(row3, colspan2);
        fifth.width = screenWidth - 2 * btnMargin;
        fifth.height = quarterScreenWidth;
        fifth.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButton5.setLayoutParams(fourth);
        btnButton5.setBackgroundColor(Color.BLUE);
        btnButton5.setText("Erziehungsberatung Fragen + Antworten");
        gridLayout.addView(btnButton5, fifth);



        TextView txtViewNewDate = new TextView(this);
        GridLayout.LayoutParams sixth = new GridLayout.LayoutParams(row4, colspan2);
        sixth.width = screenWidth - 2 * btnMargin;
        sixth.height = quarterScreenWidth;
        sixth.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        txtViewNewDate.setLayoutParams(fourth);
        txtViewNewDate.setBackgroundColor(Color.WHITE);

        txtViewNewDate.setTextColor(Color.DKGRAY);
        txtViewNewDate.setGravity(Gravity.CENTER);
        txtViewNewDate.setText("Hier steht dann der neue Termin");
        gridLayout.addView(txtViewNewDate, sixth);




        Button btnButton6 = new Button(this);
        GridLayout.LayoutParams seventh = new GridLayout.LayoutParams(row5, colspan2);
        seventh.width = screenWidth - 2 * btnMargin;
        seventh.height = quarterScreenWidth;
        seventh.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
        btnButton6.setLayoutParams(fourth);
        btnButton6.setBackgroundColor(Color.BLUE);
        btnButton6.setText("Termin vereinbaren");
        gridLayout.addView(btnButton6, seventh);

        */


    }



    // Return the number of Buttons
    public int getNumberOfButtons () {
        return numberOfButtons;
    }

    //Return original title of menue button
    public String menueButtonTitle (int position) {
        return menueButtonsTitle[position];
    }

    //Return replaced title of menue button
    public String replaceMenueButtonTitle (int position) {
        return menueButtonsTitle[position].replaceAll("[^a-zA-Z]", "_");
    }


}
