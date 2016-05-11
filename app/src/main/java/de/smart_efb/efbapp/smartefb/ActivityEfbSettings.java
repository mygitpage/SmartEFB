package de.smart_efb.efbapp.smartefb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by ich on 09.04.16.
 */
public class ActivityEfbSettings extends AppCompatActivity {


    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    ActivityDynamicButtons tmpDynamicButton;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_settings);


        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //New Object of DynamicButton
        tmpDynamicButton = new ActivityDynamicButtons();

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


        // set menueButtonsChecked
        for (int numberOfButtons=0; numberOfButtons < tmpDynamicButton.getNumberOfButtons(); numberOfButtons++) {


            String tmpRessourceName ="menueButton_" + (numberOfButtons+1);



            try {

                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", this.getPackageName());
                tmpCheckBox = (CheckBox) findViewById(resourceId);
                tmpCheckBox.setText(tmpDynamicButton.menueButtonTitle(numberOfButtons));

                if (prefs.getBoolean(tmpDynamicButton.replaceMenueButtonTitle(numberOfButtons), false)) {
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
                prefsMenueButtonName = tmpDynamicButton.replaceMenueButtonTitle(0);
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_2:
                prefsMenueButtonName = tmpDynamicButton.replaceMenueButtonTitle(1);
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_3:
                prefsMenueButtonName = tmpDynamicButton.replaceMenueButtonTitle(2);
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_4:
                prefsMenueButtonName = tmpDynamicButton.replaceMenueButtonTitle(3);
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_5:
                prefsMenueButtonName = tmpDynamicButton.replaceMenueButtonTitle(4);
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_6:
                prefsMenueButtonName = tmpDynamicButton.replaceMenueButtonTitle(5);
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;

            case R.id.menueButton_7:
                prefsMenueButtonName = "ButtonMenueFaq";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_8:
                prefsMenueButtonName = "ButtonMakeMeeting";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_9:
                prefsMenueButtonName = "ButtonEmergencyHelp";
                if (checked) {
                    buttonBooleanValue=true;
                }
                else {
                    buttonBooleanValue=false;
                }
                break;
            case R.id.menueButton_10:
                prefsMenueButtonName = "ShowTextNextMeeting";
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




}
