package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 20.06.16.
 */

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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
 * Created by ich on 07.06.16.
 */
public class ActivitySettingsEfb extends AppCompatActivity {


    Toolbar toolbarSettingsEfb;
    ActionBar actionBar;

    ViewPager viewPagerSettingsEfb;
    TabLayout tabLayoutSettingsEfb;


    // shared prefs for the app
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_efb);

        toolbarSettingsEfb = (Toolbar) findViewById(R.id.toolbarSettingsEfb);
        setSupportActionBar(toolbarSettingsEfb);
        toolbarSettingsEfb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        viewPagerSettingsEfb = (ViewPager) findViewById(R.id.viewPagerSettingsEfb);
        SettingsEfbViewPagerAdapter settingsEfbViewPagerAdapter = new SettingsEfbViewPagerAdapter(getSupportFragmentManager(), this);
        viewPagerSettingsEfb.setAdapter(settingsEfbViewPagerAdapter);

        tabLayoutSettingsEfb = (TabLayout) findViewById(R.id.tabLayoutSettingsEfb);
        tabLayoutSettingsEfb.setTabGravity(TabLayout.GRAVITY_FILL);


        tabLayoutSettingsEfb.setupWithViewPager(viewPagerSettingsEfb);

        tabLayoutSettingsEfb.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                viewPagerSettingsEfb.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }





    public void onClick_showDateChooserForCurrentArrangement (View v) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new saveDateForCurrentArrangement(), mYear, mMonth, mDay);
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

            prefs = getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
            prefsEditor = prefs.edit();

            prefsEditor.putLong("currentDateOfArrangement", date.getTime());
            prefsEditor.commit();

            Toast.makeText(ActivitySettingsEfb.this, "Absprachen Stamp:" + date.getTime(), Toast.LENGTH_SHORT).show();

        }

    }





    /* Datepicker for Jointly Goals */
    public void onClick_showDateChooserForJointlyGoals (View v) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new saveDateForJointlyGoals(), mYear, mMonth, mDay);
        dialog.show();

    }



    private class saveDateForJointlyGoals implements DatePickerDialog.OnDateSetListener {


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

            prefs = getSharedPreferences("smartEfbSettings", MODE_PRIVATE);
            prefsEditor = prefs.edit();

            prefsEditor.putLong("currentDateOfJointlyGoals", date.getTime());
            prefsEditor.commit();

            Toast.makeText(ActivitySettingsEfb.this, "Gemeinsame Ziele Stamp:" + date.getTime(), Toast.LENGTH_SHORT).show();

        }

    }





}







