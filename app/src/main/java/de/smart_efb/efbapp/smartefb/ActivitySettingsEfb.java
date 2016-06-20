package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 20.06.16.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by ich on 07.06.16.
 */
public class ActivitySettingsEfb extends AppCompatActivity {


    Toolbar toolbarSettingsEfb;
    ActionBar actionBar;

    ViewPager viewPagerSettingsEfb;
    TabLayout tabLayoutSettingsEfb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_efb_a);

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




}







