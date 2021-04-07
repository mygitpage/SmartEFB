package de.smart_efb.efbapp.smartefb;



/**
 * Created by ich on 20.06.16.
 */

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by ich on 08.06.16.
 */
public class SettingsEfbViewPagerAdapter extends FragmentPagerAdapter {



    // number of tabs
    final static int settingsEfbTabCount = 4;

    // array of tab title
    String settingsEfbTabTitleNames[] = new String[settingsEfbTabCount];

    // calling context
    Context pagerAdapterContext = null;


    public SettingsEfbViewPagerAdapter (FragmentManager settingsEfbFragmentManager, Context context) {

        super (settingsEfbFragmentManager);

        this.pagerAdapterContext = context;

        settingsEfbTabTitleNames = pagerAdapterContext.getResources().getStringArray(R.array.settingsEfbTabTitle);
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

        return settingsEfbTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return settingsEfbTabTitleNames[position];
    }


    @Override
    public int getItemPosition(Object object)
    {

        return POSITION_NONE;
    }

}