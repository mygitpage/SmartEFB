package de.smart_efb.efbapp.smartefb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ich on 09.04.16.
 */
public class ActivityEfbSettings extends AppCompatActivity {


    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_settings);


        prefs = this.getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
        prefsEditor = prefs.edit();


        preSelectElements();

    }

    private void preSelectElements() {

        RadioButton tmpRadioButton;

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



}
