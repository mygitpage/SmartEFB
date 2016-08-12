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
 * Created by ich on 12.08.16.
 */
public class ActivityFaq extends AppCompatActivity {


    Toolbar toolbarFaq;
    ActionBar actionBar;

    ViewPager viewPagerFaq;
    TabLayout tabLayoutFaq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efb_faq);

        toolbarFaq = (Toolbar) findViewById(R.id.toolbarFaq);
        setSupportActionBar(toolbarFaq);
        toolbarFaq.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        viewPagerFaq = (ViewPager) findViewById(R.id.viewPagerFaq);
        FaqViewPagerAdapter faqViewPagerAdapter = new FaqViewPagerAdapter(getSupportFragmentManager(), this);
        viewPagerFaq.setAdapter(faqViewPagerAdapter);

        tabLayoutFaq = (TabLayout) findViewById(R.id.tabLayoutFaq);
        tabLayoutFaq.setTabGravity(TabLayout.GRAVITY_FILL);


        tabLayoutFaq.setupWithViewPager(viewPagerFaq);

        tabLayoutFaq.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                viewPagerFaq.setCurrentItem(tab.getPosition());

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
