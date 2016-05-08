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

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.menueButton_1:
                if (checked) {
                    buttonBooleanValue=true;
                    buttonNumber =0;
                }
                else {
                    buttonBooleanValue=false;
                    buttonNumber =0;
                }
                break;
            case R.id.menueButton_2:
                if (checked) {
                    buttonBooleanValue=true;
                    buttonNumber =1;
                }
                else {
                    buttonBooleanValue=false;
                    buttonNumber =1;
                }
                break;
            case R.id.menueButton_3:
                if (checked) {
                    buttonBooleanValue=true;
                    buttonNumber =2;
                }
                else {
                    buttonBooleanValue=false;
                    buttonNumber =2;
                }
                break;
            case R.id.menueButton_4:
                if (checked) {
                    buttonBooleanValue=true;
                    buttonNumber =3;
                }
                else {
                    buttonBooleanValue=false;
                    buttonNumber =3;
                }
                break;
            case R.id.menueButton_5:
                if (checked) {
                    buttonBooleanValue=true;
                    buttonNumber =4;
                }
                else {
                    buttonBooleanValue=false;
                    buttonNumber =4;
                }
                break;
            case R.id.menueButton_6:
                if (checked) {
                    buttonBooleanValue=true;
                    buttonNumber =5;
                }
                else {
                    buttonBooleanValue=false;
                    buttonNumber =5;
                }
                break;
        }



        prefsEditor.putBoolean(tmpDynamicButton.replaceMenueButtonTitle(buttonNumber), buttonBooleanValue);
        prefsEditor.commit();


    }




}
