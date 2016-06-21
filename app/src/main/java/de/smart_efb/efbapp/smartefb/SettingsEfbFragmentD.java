package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

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


    // total number of elements (in test-mode please edit variable in class MainActivity please too!!!!!!!!!!!!!)
    private static int mainMenueNumberOfElements=8;
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

        preSelectElements();


    }




    private void preSelectElements() {

        RadioButton tmpRadioButton;
        CheckBox tmpCheckBox;


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

        tmpRadioButton.setChecked(true);


        EditText txtChatName = (EditText) viewFragmentD.findViewById(R.id.txtChatName);
        String chatName = prefs.getString("connectBookName", "Jon Down");
        txtChatName.setText(chatName);


        String tmpRessourceName ="";
        String tmpMainMenueElementName ="";

        mainMenueElementTitle = getResources().getStringArray(R.array.mainMenueElementTitle);

        // set menueButtonsChecked
        for (int numberOfButtons=0; numberOfButtons < mainMenueNumberOfElements; numberOfButtons++) {


            tmpRessourceName ="menueButton_" + (numberOfButtons+1);

            tmpMainMenueElementName ="mainMenueElementId_" + numberOfButtons;


            try {

                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentContextD.getPackageName());
                tmpCheckBox = (CheckBox) viewFragmentD.findViewById(resourceId);
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


        // Preselect the arrangement settings (History Comment)
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showCommentLinkArrangement);
        if (prefs.getBoolean("showArrangementComment", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }
        tmpCheckBox = (CheckBox) viewFragmentD.findViewById(R.id.showArrangementHistory);
        if (prefs.getBoolean("showArrangementHistory", false)) {
            tmpCheckBox.setChecked(true);
        }
        else {
            tmpCheckBox.setChecked(false);
        }

    }





}
