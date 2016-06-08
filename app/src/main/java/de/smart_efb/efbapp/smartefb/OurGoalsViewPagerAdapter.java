package de.smart_efb.efbapp.smartefb;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ich on 08.06.16.
 */
public class OurGoalsViewPagerAdapter extends FragmentPagerAdapter {


    // array of tab title
    String ourGoalsTabTitleNames[] = {"Klaus", "Dagmar", "Dennis", "Gerda"};

    // number of tabs
    final static int ourGoalsTabCount = 2;





    public OurGoalsViewPagerAdapter (FragmentManager ourGoalsFragmentManager) {

        super (ourGoalsFragmentManager);

    }


    @Override
    public Fragment getItem(int position) {


        switch (position) {

            case 0:
                return new OurGoalsFragmentA();

            case 1:
                return new OurGoalsFragmentB();

            default:
                return new OurGoalsFragmentA();


        }

    }


    @Override
    public int getCount() {
        return ourGoalsTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return ourGoalsTabTitleNames[position];

    }
}
