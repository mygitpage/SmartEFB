package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ich on 12.08.16.
 */
public class FaqViewPagerAdapter extends FragmentStatePagerAdapter {


    // number of tabs
    final static int faqTabCount = 5;

    // array of tab title
    String faqTabTitleNames[] = new String[faqTabCount];

    // calling context
    Context pagerAdapterContext = null;





    // default constructor
    public FaqViewPagerAdapter (FragmentManager faqFragmentManager, Context context) {

        super (faqFragmentManager);

        this.pagerAdapterContext = context;

        faqTabTitleNames = pagerAdapterContext.getResources().getStringArray(R.array.faqTabTitle);


    }



    @Override
    public Fragment getItem(int position) {


        switch (position) {

            case 0:
                return new FaqFragmentSection1();

            case 1:
                return new FaqFragmentSection2();

            case 2:
                return new FaqFragmentSection3();

            case 3:
                return new FaqFragmentSection4();

            case 4:
                return new FaqFragmentSection5();

            default:
                return new FaqFragmentSection1();


        }

    }


    @Override
    public int getCount() {

        return faqTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return faqTabTitleNames[position];

    }





}
