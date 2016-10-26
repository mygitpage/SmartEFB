package de.smart_efb.efbapp.smartefb;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentD extends Fragment {

    // shared prefs for the app
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // fragment context
    Context fragmentContextD = null;

    // fragment view
    View viewFragmentD;


    // reference to the DB
    DBAdapter myDb;


    // total number of elements (in test-mode please edit variable in class MainActivity please too!!!!!!!!!!!!!)
    private static int mainMenueNumberOfElements=9;
    // title of main Menue Elements
    private String[] mainMenueElementTitle = new String [mainMenueNumberOfElements];


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentD = layoutInflater.inflate(R.layout.fragment_settings_efb_d, null);

        return viewFragmentD;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentContextD = getActivity().getApplicationContext();

        prefs = fragmentContextD.getSharedPreferences("smartEfbSettings", fragmentContextD.MODE_PRIVATE);
        prefsEditor = prefs.edit();


        // init the DB
        myDb = new DBAdapter(fragmentContextD);


        preSelectElements();


    }



    //
    // pre select elements of fragment D (like ragioButtons, checkBoxes, Buttons, EditText, etc.)
    //
    private void preSelectElements() {


        // put pause time and active time for evaluation in OurArrangement in prefs
        // put start data and end data of evaluation in prefs
        // in future comes from coach over internet
        prefsEditor.putInt("evaluatePauseTimeInSeconds", 30);
        prefsEditor.putInt("evaluateActivTimeInSeconds", 30);

        prefsEditor.putLong("startDataEvaluationInMills", System.currentTimeMillis());
        prefsEditor.putLong("endDataEvaluationInMills", System.currentTimeMillis()+150000); // for testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        // for current comment
        // put max count of comments and current count of comments in prefs
        // in future comes from coach over internet
        prefsEditor.putInt("commentOurArrangementMaxComment", 1001); // > 1000 -> no limitation with comments
        prefsEditor.putInt("commentOurArrangementCountComment", 0);
        // since this time count comments
        prefsEditor.putLong("commentOurArrangementTimeSinceInMills", System.currentTimeMillis()); // for testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        // for sketch comment!!!!
        // put max count of sketch comments and current count of sketch comments in prefs
        // in future comes from coach over internet
        prefsEditor.putInt("commentSketchOurArrangementMaxComment", 10); // > 1000 -> no limitation with comments
        prefsEditor.putInt("commentSketchOurArrangementCountComment", 0);
        // since this time count sketch comments
        prefsEditor.putLong("sketchCommentOurArrangementTimeSinceInMills", System.currentTimeMillis()); // for testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!



        // put name and date of sketch arrangements in Prefs
        prefsEditor.putString("authorOfSketchArrangement", "Herr Testmann");
        //prefsEditor.putLong("currentDateOfSketchArrangement", System.currentTimeMillis());



        //Our Goals

        // for jointly goals comment!!!!
        // put max count of jointly comments and current count of jointly comments in prefs
        // in future comes from coach over internet
        prefsEditor.putInt("commentJointlyGoalMaxCountComment", 10); // > 1000 -> no limitation with comments
        prefsEditor.putInt("commentJointlyGoalCountComment", 0);
        // since this time count sketch comments
        prefsEditor.putLong("jointlyGoalsCommentOurGoalsTimeSinceInMills", System.currentTimeMillis()); // for testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!







        prefsEditor.commit();



        //
        // set onclickListener for chat role checkboxes
        //
        RadioButton tmpRoleChatRadioButton;
        tmpRoleChatRadioButton = (RadioButton) viewFragmentD.findViewById(R.id.radio_role_mother);
        tmpRoleChatRadioButton.setOnClickListener(new chatRadioButtonListener(0));
        tmpRoleChatRadioButton = (RadioButton) viewFragmentD.findViewById(R.id.radio_role_father);
        tmpRoleChatRadioButton.setOnClickListener(new chatRadioButtonListener(1));
        tmpRoleChatRadioButton = (RadioButton) viewFragmentD.findViewById(R.id.radio_role_third);
        tmpRoleChatRadioButton.setOnClickListener(new chatRadioButtonListener(2));

        // pre select chat role
        RadioButton tmpRadioButton;
        int roleNew = prefs.getInt("connectBookRole", 0);
        switch (roleNew) {
            case 0:
                tmpRadioButton = (RadioButton) viewFragmentD.findViewById(R.id.radio_role_mother);

                break;
            case 1:
                tmpRadioButton = (RadioButton) viewFragmentD.findViewById(R.id.radio_role_father);
                break;
            case 2:
                tmpRadioButton = (RadioButton) viewFragmentD.findViewById(R.id.radio_role_third);
                break;
            default:
                tmpRadioButton = (RadioButton) viewFragmentD.findViewById(R.id.radio_role_mother);
                break;
        }
        tmpRadioButton.setChecked(true); //and set it!

        //
        // show chat name in textfield
        //
        EditText txtChatName = (EditText) viewFragmentD.findViewById(R.id.txtChatName);
        String chatName = prefs.getString("userName", "Jon Down");
        txtChatName.setText(chatName);
        // set onClickListener button save new chatName
        Button tmpSaveChatNameButton = (Button) viewFragmentD.findViewById(R.id.saveUserName);
        tmpSaveChatNameButton.setOnClickListener(new View.OnClickListener() { // OnclickListener for button save chat name

            @Override
            public void onClick(View v) {

                EditText txtChatName = (EditText) viewFragmentD.findViewById(R.id.txtChatName);
                String stringChatName = txtChatName.getText().toString();

                prefsEditor.putString("userName", stringChatName);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Neuer Username gespeichert", Toast.LENGTH_SHORT).show();

            }
        });


        //
        // pre select checkBoxes mainMenueItems
        //
        String tmpRessourceName ="";
        String tmpMainMenueElementName ="";
        CheckBox tmpCheckBox;
        mainMenueElementTitle = getResources().getStringArray(R.array.mainMenueElementTitle);

        // set menueButtonsChecked
        for (int numberOfButtons=0; numberOfButtons < mainMenueNumberOfElements; numberOfButtons++) {
            tmpRessourceName ="menueButton_" + (numberOfButtons+1);
            tmpMainMenueElementName ="mainMenueElementId_" + numberOfButtons;
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentContextD.getPackageName());
                tmpCheckBox = (CheckBox) viewFragmentD.findViewById(resourceId);
                tmpCheckBox.setOnClickListener(new mainMenueItemCheckBoxListener(numberOfButtons));
                tmpCheckBox.setText(mainMenueElementTitle[numberOfButtons]);

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

        //
        // pre select the arrangement comment settings
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showCommentLinkArrangement);

        if (prefs.getBoolean("showArrangementComment", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showArrangementComment", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Vereinbarungen Kommentare " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });


        //
        // pre select the arrangement evaluate settings
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showEvaluateLinkArrangement);

        if (prefs.getBoolean("showArrangementEvaluate", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showArrangementEvaluate", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Vereinbarungen Bewertungen " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });




        //
        // pre select show old arrangements
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showOldArrangements);

        if (prefs.getBoolean("showOldArrangements", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showOldArrangements", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Ältere Vereinbarungen zeigen " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });


        //
        // pre select show sketch arrangements
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showSketchArrangements);

        if (prefs.getBoolean("showSketchArrangements", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showSketchArrangements", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Vereinbarungen Entwürfe sind " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });


        //
        // pre select show sketch comment link in sketch arrangements
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showCommentLinkSketchArrangements);

        if (prefs.getBoolean("showCommentLinkSketchArrangements", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showCommentLinkSketchArrangements", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Vereinbarungen Entwürfe Kommentare Link sind " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });


        // textfield and button -> insert new test arrangement
        final EditText txtInputArrangement = (EditText) viewFragmentD.findViewById(R.id.arrangementText);
        Button buttonSendArrangement = (Button) viewFragmentD.findViewById(R.id.arrangementTextSend);
        // onClick send button
        buttonSendArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long newID = myDb.insertRowOurArrangement(txtInputArrangement.getText().toString(), "testuser", prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()), true, false, 0);

                txtInputArrangement.setText("");


                Toast.makeText(fragmentContextD, "Neue aktuelle Testabsprache eingetragen", Toast.LENGTH_SHORT).show();


            }
        });
        // end insert new test arrangement



        // textfield and button -> insert new test sketch arrangement
        final EditText txtInputSketchArrangement = (EditText) viewFragmentD.findViewById(R.id.arrangementSketchText);
        Button buttonSendSketchArrangement = (Button) viewFragmentD.findViewById(R.id.arrangementSketchTextSend);
        // onClick send button
        buttonSendSketchArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long newID = myDb.insertRowOurArrangement(txtInputSketchArrangement.getText().toString(), "testuser", 0, true, true, prefs.getLong("currentDateOfSketchArrangement", System.currentTimeMillis()));

                txtInputSketchArrangement.setText("");


                Toast.makeText(fragmentContextD, "Neuen Entwurf für Testabsprache eingetragen", Toast.LENGTH_SHORT).show();




            }
        });
        // end insert new test sketch arrangement



        /* Beginn PRE SELECT, Click-Listener and TextView-Listener OUR GOALS */

        //
        // pre select show comment link in jointly goals
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showCommentLinkJointlyGoals);

        if (prefs.getBoolean("showCommentLinkJointlyGoals", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showCommentLinkJointlyGoals", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Gemeinsame Ziele Kommentare Link sind " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });

        //
        // pre select show evaluate link in jointly goals
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showEvaluateLinkJointlyGoals);

        if (prefs.getBoolean("showEvaluateLinkJointlyGoals", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showEvaluateLinkJointlyGoals", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Evaluierungslink Gemeinsame Ziele sind " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });


        //
        // pre select show old goals
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showOldGoals);

        if (prefs.getBoolean("showOldGoals", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showOldGoals", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Zeige alte Ziele sind " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });


        //
        // pre select show debetable goals
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showDebetableGoals);

        if (prefs.getBoolean("showDebetableGoals", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showDebetableGoals", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Zeige strittige Ziele sind " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });



        //
        // pre select show debetable goals
        //
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showCommentLinkDebetableGoals);

        if (prefs.getBoolean("showCommentLinkDebetableGoals", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox.setOnClickListener(new View.OnClickListener() { // OnclickListener for arrangement comment

            @Override
            public void onClick(View v) {

                boolean checkBoxBooleanValue=false;
                String aktiv_passivText ="";

                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();

                if (checked) {
                    checkBoxBooleanValue=true;
                    aktiv_passivText = "aktiviert";
                }
                else {
                    checkBoxBooleanValue=false;
                    aktiv_passivText = "deaktiviert";
                }

                prefsEditor.putBoolean("showCommentLinkDebetableGoals", checkBoxBooleanValue);
                prefsEditor.commit();

                Toast.makeText(fragmentContextD, "Zeige Kommentarlink strittige Ziele sind " + aktiv_passivText, Toast.LENGTH_SHORT).show();

            }
        });



        // textfield and button -> insert new test jointly goal
        final EditText txtInputJointlyGoal = (EditText) viewFragmentD.findViewById(R.id.jointlyGoalText);
        Button buttonSendJointlyGoal = (Button) viewFragmentD.findViewById(R.id.jointlyGoalTextSend);
        // onClick send button
        buttonSendJointlyGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long newID = myDb.insertRowOurGoals(txtInputJointlyGoal.getText().toString(), "testuser", prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis()), true, false);

                txtInputJointlyGoal.setText("");


                Toast.makeText(fragmentContextD, "Neues gemeinsames Ziel eingetragen", Toast.LENGTH_SHORT).show();




            }
        });
        // end insert new test jointly goal

        // textfield and button -> insert new test debetable goal
        final EditText txtInputDebetableGoal = (EditText) viewFragmentD.findViewById(R.id.debetableGoalText);
        Button buttonSendDebetableGoal = (Button) viewFragmentD.findViewById(R.id.debetableGoalTextSend);
        // onClick send button
        buttonSendDebetableGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long newID = myDb.insertRowOurGoals(txtInputJointlyGoal.getText().toString(), "testuser", prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis()), true, true);

                txtInputDebetableGoal.setText("");


                Toast.makeText(fragmentContextD, "Neues strittiges Ziel eingetragen", Toast.LENGTH_SHORT).show();




            }
        });
        // end insert new test debetable goal




    }


    //
    // onClickListener for radioButtons chat role
    //
    public class chatRadioButtonListener implements View.OnClickListener {

        int radioButtonNumber;

        public chatRadioButtonListener (int number) {
            this.radioButtonNumber = number;
        }

        @Override
        public void onClick(View v) {

            int roleNew = 0;

            switch (radioButtonNumber) {

                case 0: // radioButton mother
                    roleNew = 0;
                    break;
                case 1: // radioButton father
                    roleNew = 1;
                    break;
                case 2: // radioButton third
                    roleNew = 2;
                    break;

                default:
                    roleNew = 0;
                    break;
            }

            prefsEditor.putInt("connectBookRole", roleNew);
            prefsEditor.commit();

            Toast.makeText(fragmentContextD, "Neue Rolle gespeichert", Toast.LENGTH_SHORT).show();

        }
    }


    //
    // onClickListener for mainMenueItemCheckboxes
    //
    public class mainMenueItemCheckBoxListener implements View.OnClickListener {

        int checkBoxButtonNumber;

        public mainMenueItemCheckBoxListener (int number) {

            this.checkBoxButtonNumber = number;
        }

        @Override
        public void onClick(View v) {

            // Is the view now checked?
            boolean checked = ((CheckBox) v).isChecked();

            boolean buttonBooleanValue=false;
            String prefsMenueButtonName ="";
            String aktiv_passivText ="";

            switch (checkBoxButtonNumber) {

                case 0:
                    prefsMenueButtonName = "mainMenueElementId_0";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;
                case 1:
                    prefsMenueButtonName = "mainMenueElementId_1";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;
                case 2:
                    prefsMenueButtonName = "mainMenueElementId_2";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;
                case 3:
                    prefsMenueButtonName = "mainMenueElementId_3";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;
                case 4:
                    prefsMenueButtonName = "mainMenueElementId_4";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;
                case 5:
                    prefsMenueButtonName = "mainMenueElementId_5";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;

                case 6:
                    prefsMenueButtonName = "mainMenueElementId_6";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;
                case 7:
                    prefsMenueButtonName = "mainMenueElementId_7";
                    if (checked) {
                        buttonBooleanValue=true;
                        aktiv_passivText = "aktiviert";
                    }
                    else {
                        buttonBooleanValue=false;
                        aktiv_passivText = "deaktiviert";
                    }
                    break;
           }

            prefsEditor.putBoolean(prefsMenueButtonName, buttonBooleanValue);
            prefsEditor.commit();

            Toast.makeText(fragmentContextD, "Menue Button " + aktiv_passivText, Toast.LENGTH_SHORT).show();

        }
    }

}
