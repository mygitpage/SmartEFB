package de.smart_efb.efbapp.smartefb;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by ich on 09.04.16.
 */
public class ActivityEfbSettings extends AppCompatActivity {


    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    MainActivity tmpDynamicButton;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_settings);


        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //New Object of MainMenue (Activity Main)
        tmpDynamicButton = new MainActivity();

        preSelectElements();

    }

    private void preSelectElements() {

        RadioButton tmpRadioButton;
        CheckBox tmpCheckBox;


        int roleNew = prefs.getInt("connectBookRole", 0);
        switch (roleNew) {
            case 0:
                tmpRadioButton = (RadioButton) findViewById(R.id.radio_role_mother);

                break;
            case 1:
                tmpRadioButton = (RadioButton) findViewById(R.id.radio_role_father);
                break;
            case 2:
                tmpRadioButton = (RadioButton) findViewById(R.id.radio_role_third);
                break;
            default:
                tmpRadioButton = (RadioButton) findViewById(R.id.radio_role_mother);
                break;
        }

        tmpRadioButton.setChecked(true);


        EditText txtChatName = (EditText) findViewById(R.id.txtChatName);
        String chatName = prefs.getString("connectBookName", "Jon Down");
        txtChatName.setText(chatName);

        String tmpRessourceName ="";
        String tmpMainMenueElementName ="";

        // set menueButtonsChecked
        for (int numberOfButtons=0; numberOfButtons < tmpDynamicButton.getNumberOfButtons(); numberOfButtons++) {


            tmpRessourceName ="menueButton_" + (numberOfButtons+1);

            tmpMainMenueElementName ="mainMenueElementId_" + numberOfButtons;


            try {

                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", this.getPackageName());
                tmpCheckBox = (CheckBox) findViewById(resourceId);
                tmpCheckBox.setText(tmpDynamicButton.menueButtonTitle(numberOfButtons));

                if (prefs.getBoolean(tmpMainMenueElementName, false)) {
                    tmpCheckBox.setChecked(true);
                }
                else {
                    tmpCheckBox.setChecked(false);
                }


            } catch (Exception e) {

                e.printStackTrace();
            }

        }


        // Preselect the arrangement settings (History Comment)
        tmpCheckBox = (CheckBox) findViewById(R.id.showCommentLinkArrangement);
        if (prefs.getBoolean("showArrangementComment", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox = (CheckBox) findViewById(R.id.showArrangementHistory);
        if (prefs.getBoolean("showArrangementHistory", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }










    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // The new role in the connectBook
        int roleNew = 0;

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_role_mother:

                    roleNew = 0;
                    break;
            case R.id.radio_role_father:
                    roleNew = 1;
                    break;

            case R.id.radio_role_third:
                    roleNew = 2;
                    break;
        }

        prefsEditor.putInt("connectBookRole", roleNew);
        prefsEditor.commit();

        Toast.makeText(this, "Neue Rolle gespeichert ", Toast.LENGTH_SHORT).show();


    }



    public void onClick_saveChatName (View v) {


        EditText txtChatName = (EditText) findViewById(R.id.txtChatName);

        String stringChatName = txtChatName.getText().toString();

        prefsEditor.putString("connectBookName", stringChatName);
        prefsEditor.commit();

        Toast.makeText(this, "Neuer Chat Name gespeichert ", Toast.LENGTH_SHORT).show();


    }



     public void onCheckboxMenueButtonClicked(View view) {

        int buttonNumber =0;

        boolean buttonBooleanValue=false;


        String prefsMenueButtonName ="";

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.menueButton_1:
                prefsMenueButtonName = "mainMenueElementId_0";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_2:
                prefsMenueButtonName = "mainMenueElementId_1";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_3:
                prefsMenueButtonName = "mainMenueElementId_2";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_4:
                prefsMenueButtonName = "mainMenueElementId_3";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_5:
                prefsMenueButtonName = "mainMenueElementId_4";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_6:
                prefsMenueButtonName = "mainMenueElementId_5";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;

            case R.id.menueButton_7:
                prefsMenueButtonName = "mainMenueElementId_6";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_8:
                prefsMenueButtonName = "mainMenueElementId_7";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_9:
                prefsMenueButtonName = "mainMenueElementId_8";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_10:
                prefsMenueButtonName = "mainMenueElementId_9";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_11:
                prefsMenueButtonName = "mainMenueElementId_10";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;

            case R.id.menueButton_12:
                prefsMenueButtonName = "mainMenueElementId_11";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_13:
                prefsMenueButtonName = "mainMenueElementId_12";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;


        }




        prefsEditor.putBoolean(prefsMenueButtonName, buttonBooleanValue);
        prefsEditor.commit();



    }




    public void onClick_showDateChooserForCurrentArrangement (View v) {


        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(ActivityEfbSettings.this, new saveDateForCurrentArrangement(), mYear, mMonth, mDay);
        dialog.show();


    }



    private class saveDateForCurrentArrangement implements DatePickerDialog.OnDateSetListener {


        SharedPreferences prefs;
        SharedPreferences.Editor prefsEditor;


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            int mYear = year;
            int mMonth = monthOfYear+1;
            int mDay = dayOfMonth;
            Date date = null;

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

            try {
                date = formatter.parse(mDay+"-"+mMonth+"-"+year);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            prefs = ActivityEfbSettings.this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
            prefsEditor = prefs.edit();

            prefsEditor.putLong("currentDateOfArrangement", date.getTime());
            prefsEditor.commit();

            Toast.makeText(ActivityEfbSettings.this, "Stamp:" + date.getTime(), Toast.LENGTH_SHORT).show();

        }

    }





    public void onCheckboxShowArrangementElements(View view) {


        boolean showBooleanValue = false;
        String prefsArrangementName = "";

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.showCommentLinkArrangement:
                prefsArrangementName = "showArrangementComment";
                if (checked) {
                    showBooleanValue = true;
                } else {
                    showBooleanValue = false;
                }
                break;
            case R.id.showArrangementHistory:
                prefsArrangementName = "showArrangementHistory";
                if (checked) {
                    showBooleanValue = true;
                } else {
                    showBooleanValue = false;
                }
                break;
        }


        prefsEditor.putBoolean(prefsArrangementName, showBooleanValue);
        prefsEditor.commit();



    }




}




/*
        // Pre select the elemtentary buttons
        String [] tmpElemtentaryButtonsArray = {"ButtonMenueFaq","ButtonMakeMeeting","ButtonEmergencyHelp","ShowTextNextMeeting"};

        for (int tmpElentaryButtons=7; tmpElentaryButtons<11; tmpElentaryButtons++) {


            String tmpRessourceName ="menueButton_" + tmpElentaryButtons;

            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", this.getPackageName());
                tmpCheckBox = (CheckBox) findViewById(resourceId);

                if (prefs.getBoolean(tmpElemtentaryButtonsArray[tmpElentaryButtons-7], false)) {
                    tmpCheckBox.setChecked(true);
                }
                else {
                    tmpCheckBox.setChecked(false);
                }

            } catch (Exception e) {

                e.printStackTrace();
                //return -1;

            }

        }
        */