package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ich on 26.06.16.
 */
public class OurArrangementViewPagerAdapter extends FragmentPagerAdapter {


    // number of tabs
    final static int ourArrangementTabCount = 2;

    // array of tab title
    String ourArrangementTabTitleNames[] = new String[ourArrangementTabCount];


    // calling context
    Context pagerAdapterContext = null;

    //{"Aktuell", "Vergangene"}



    public OurArrangementViewPagerAdapter (FragmentManager ourArrangementFragmentManager, Context context) {

        super (ourArrangementFragmentManager);

        this.pagerAdapterContext = context;

        ourArrangementTabTitleNames = pagerAdapterContext.getResources().getStringArray(R.array.ourArrangementTabTitle);

    }


    @Override
    public Fragment getItem(int position) {


        switch (position) {

            case 0:
                return new SettingsEfbFragmentA();

            case 1:
                return new SettingsEfbFragmentB();

            case 2:
                return new SettingsEfbFragmentC();

            case 3:
                return new SettingsEfbFragmentD();

            default:
                return new SettingsEfbFragmentA();


        }

    }


    @Override
    public int getCount() {
        return ourArrangementTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return ourArrangementTabTitleNames[position];

    }



}















