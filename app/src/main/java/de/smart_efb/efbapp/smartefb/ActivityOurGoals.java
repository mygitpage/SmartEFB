package de.smart_efb.efbapp.smartefb;

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
public class ActivityOurGoals extends AppCompatActivity {




    Toolbar toolbarOurGoals;
    ActionBar actionBar;

    ViewPager viewPagerOurGoals;
    TabLayout tabLayoutOurGoals;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_our_goals);





        toolbarOurGoals = (Toolbar) findViewById(R.id.toolbarOurGoals);
        setSupportActionBar(toolbarOurGoals);
        toolbarOurGoals.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);






        viewPagerOurGoals = (ViewPager) findViewById(R.id.viewPagerOurGoals);


        tabLayoutOurGoals = (TabLayout) findViewById(R.id.tabLayoutOurGoals);
        tabLayoutOurGoals.setTabGravity(TabLayout.GRAVITY_FILL);

        /*
        tabLayoutOurGoals.setupWithViewPager(viewPagerOurGoals);

        tabLayoutOurGoals.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

        });
        */





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
