package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentD extends Fragment {

    // shared prefs for the app
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // fragment context
    Context fragmentContextD = null;

    // the fragment
    Fragment fragmentSettingsEfbDFragmentContext;

    // fragment view
    View viewFragmentD;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentD = layoutInflater.inflate(R.layout.fragment_settings_efb_d, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(settingsFragmentDBrodcastReceiver, filter);

        return viewFragmentD;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentContextD = getActivity().getApplicationContext();

        fragmentSettingsEfbDFragmentContext = this;

        prefs = fragmentContextD.getSharedPreferences("smartEfbSettings", fragmentContextD.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // show actual view
        displayActualView();
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(settingsFragmentDBrodcastReceiver);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeServiceEfb
    private BroadcastReceiver settingsFragmentDBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentContextD.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
            }
        }
    };


    void displayActualView() {

        CheckBox checkBox;
        Boolean showAcousticChekcBox = false;
        Boolean showVisualChekcBox = false;

        // show hints for visual signals (when visual signal is off the acoustic signal is off!!!!) +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        // check connect book on? -> show notification signal check box for new message
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderConnectBookVisual = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerConnectBookVisual);
            placeholderConnectBookVisual.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxConnectBookVisualSignal);
            checkBox.setOnClickListener(new checkBoxSettingVisualListener("connect_book", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showVisualChekcBox = true;
        }

        // check our arrangement on? -> show  notification signal check box for new event in our arrangement
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderOurArrangementVisual = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerOurArrangementVisual);
            placeholderOurArrangementVisual.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxOurArrangementVisualSignal);
            checkBox.setOnClickListener(new checkBoxSettingVisualListener("our_arrangement", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showVisualChekcBox = true;
        }

        // check Our Arrangement evaluation on? -> show notification signal check box for new arrangement, sketch, comment, etc.
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false) && prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderArrangementEvaluationVisual = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerArrangementEvaluationVisual);
            placeholderArrangementEvaluationVisual.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxArrangementEvaluationVisualSignal);
            checkBox.setOnClickListener(new checkBoxSettingVisualListener("our_arrangement_evaluation", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showVisualChekcBox = true;
        }


        // check our goals on? -> show  notification signal check box for new event in our goals
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderOurGoalVisual = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerOurGoalVisual);
            placeholderOurGoalVisual.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxOurGoalVisualSignal);
            checkBox.setOnClickListener(new checkBoxSettingVisualListener("our_goal", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showVisualChekcBox = true;
        }

        // check our goals evaluation on? -> show  notification signal check box for goal evaluation change
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false) && prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderOurGoalVisual = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerOurGoalEvaluationVisual);
            placeholderOurGoalVisual.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxOurGoalEvaluationVisualSignal);
            checkBox.setOnClickListener(new checkBoxSettingVisualListener("our_goal_evaluation", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showVisualChekcBox = true;
        }

        // check messages on? -> show  notification signal check box for message
        if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderMessageVisual = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerMessageVisual);
            placeholderMessageVisual.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxMessageVisualSignal);
            checkBox.setOnClickListener(new checkBoxSettingVisualListener("message", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_Message, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_Message, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showVisualChekcBox = true;
        }


        if (!showVisualChekcBox) {
            TextView tmpHintTextNoVisualCheckBox = (TextView) viewFragmentD.findViewById(R.id.textViewInfoNoVisualChekcBoxPossible);
            tmpHintTextNoVisualCheckBox.setVisibility(View.VISIBLE);
        }


        // show hints for acoustics signals +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        
        // show acoustic signal check box for new message only when visual notification on!
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, false) && prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_ConnectBook, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderConnectBookAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerConnectBookAcoustics);
            placeholderConnectBookAcoustics.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxConnectBookAcousticsSignal);
            checkBox.setOnClickListener(new checkBoxSettingAcousticsListener("connect_book", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showAcousticChekcBox = true;
        }

        // show acoustic signal check box for new arrangement, sketch, comment, etc. only when visual notification on!
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, false) && prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderArrangementAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerArrangementAcoustics);
            placeholderArrangementAcoustics.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxArrangementAcousticsSignal);
            checkBox.setOnClickListener(new checkBoxSettingAcousticsListener("our_arrangement", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showAcousticChekcBox = true;
        }

        // show acoustic signal check box for arrangement evaluation only when visual notification on!
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, false) && prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false) && prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderArrangementEvaluationAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerArrangementEvaluationAcoustics);
            placeholderArrangementEvaluationAcoustics.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxArrangementEvaluationAcousticsSignal);
            checkBox.setOnClickListener(new checkBoxSettingAcousticsListener("our_arrangement_evaluation", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showAcousticChekcBox = true;
        }

        // show acoustic signal check box for new Goal, debetable, comment, etc.
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, false) && prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderGoalAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerGoalAcoustics);
            placeholderGoalAcoustics.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxGoalAcousticsSignal);
            checkBox.setOnClickListener(new checkBoxSettingAcousticsListener("our_goal", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showAcousticChekcBox = true;
        }

        // show acoustic signal check box for goal evaluation change
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, false) && prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false) && prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderGoalEvaluationAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerGoalEvaluationAcoustics);
            placeholderGoalEvaluationAcoustics.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxGoalEvaluationAcousticsSignal);
            checkBox.setOnClickListener(new checkBoxSettingAcousticsListener("our_goal_evaluation", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showAcousticChekcBox = true;
        }

        // show acoustic signal check box for new message in message
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_Message, true) && prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_Message, false)) {
            // get linaer layout and set visible
            LinearLayout placeholderMessageAcoustics = (LinearLayout) viewFragmentD.findViewById(R.id.checkBoxContainerMessageAcoustics);
            placeholderMessageAcoustics.setVisibility(View.VISIBLE);
            // get check box and set on click listener
            checkBox = (CheckBox) viewFragmentD.findViewById(R.id.checkBoxMessageAcousticsSignal);
            checkBox.setOnClickListener(new checkBoxSettingAcousticsListener("message", prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_Message, true)));
            if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_Message, true)) {checkBox.setChecked(true);}
            else {checkBox.setChecked(false);}
            showAcousticChekcBox = true;
        }
        

        
        if (!showAcousticChekcBox) {
            TextView tmpHintTextNoAcousitcCheckBox = (TextView) viewFragmentD.findViewById(R.id.textViewInfoNoAcousticsChekcBoxPossible);
            tmpHintTextNoAcousitcCheckBox.setVisibility(View.VISIBLE);
        }



    }



    
    // onClickListener for check box visual signals
    public class checkBoxSettingVisualListener implements View.OnClickListener {

        String checkBoxName;
        Boolean checkBoxValue;

        public checkBoxSettingVisualListener (String tmpCheckBoxName, Boolean tmpCheckBoxValue) {

            this.checkBoxName = tmpCheckBoxName;
            this.checkBoxValue = tmpCheckBoxValue;
        }

        @Override
        public void onClick(View v) {

            switch (checkBoxName) {

                case "connect_book":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, false);
                   }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_ConnectBook, true);
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, true); // acoustic signal to true
                    }
                    prefsEditor.commit();
                    break;

                case "our_arrangement":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, false);
                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangement, true);
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, true); // acoustic signal to true
                    }
                    prefsEditor.commit();
                    break;

                case "our_arrangement_evaluation":
                    if (checkBoxValue) {

                        Log.d("Vis Arr Evaluation -->", "CASE Arr!!! TRUE");

                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, false);
                    }
                    else {
                        checkBoxValue = true;

                        Log.d("Vis Arr Evaluation -->", "CASE Arr!!! FLASE");

                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurArrangementEvaluation, true);
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, true);
                    }
                    prefsEditor.commit();
                    break;

                case "our_goal":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, false);
                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoal, true);
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, true);
                    }
                    prefsEditor.commit();
                    break;

                case "our_goal_evaluation":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, false);
                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_OurGoalEvaluation, true);
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true);
                    }
                    prefsEditor.commit();
                    break;
            }


            // show view
            displayActualView();

            // refresh fragments view
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(fragmentSettingsEfbDFragmentContext).attach(fragmentSettingsEfbDFragmentContext).commit();


        }
    }
    
    
    
    


    // onClickListener for check box acoustics signals
    public class checkBoxSettingAcousticsListener implements View.OnClickListener {

        String checkBoxName;
        Boolean checkBoxValue;

        public checkBoxSettingAcousticsListener (String tmpCheckBoxName, Boolean tmpCheckBoxValue) {

            this.checkBoxName = tmpCheckBoxName;
            this.checkBoxValue = tmpCheckBoxValue;
        }

        @Override
        public void onClick(View v) {

            Log.d("Setting Onclick -->", "In OnClick!");

            switch (checkBoxName) {

                case "connect_book":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, false);

                        Log.d("Setting Onclick -->", "Aktiviere Connect Book");

                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_ConnectBook, true);
                        Log.d("Setting Onclick -->", "Deaktiviere Connect Book");
                    }
                    prefsEditor.commit();
                    break;

                case "our_arrangement":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, false);

                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangement, true);
                    }
                    prefsEditor.commit();
                    break;


                case "our_arrangement_evaluation":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, false);

                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurArrangementEvaluation, true);
                    }
                    prefsEditor.commit();
                    break;



                case "our_goal":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, false);

                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoal, true);
                    }
                    prefsEditor.commit();
                    break;


                case "our_goal_evaluation":
                    if (checkBoxValue) {
                        checkBoxValue = false;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, false);

                    }
                    else {
                        checkBoxValue = true;
                        prefsEditor.putBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_OurGoalEvaluation, true);
                    }
                    prefsEditor.commit();
                    break;







            }


        }
    }







}
