package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

/**
 * Created by ich on 26.06.16.
 */
public class OurArrangementViewPagerAdapter extends FragmentStatePagerAdapter {


    // number of tabs
    final static int ourArrangementTabCount = 2;

    // array of tab title
    String ourArrangementTabTitleNames[] = new String[ourArrangementTabCount];


    // calling context
    Context pagerAdapterContext = null;


    // command to choose a fragment in getItem
    static int fragmentChooser;

    // the fragment
    Fragment fragArraNow, fragArraNowComment, fragArraOld, fragArraShowComment;







    public OurArrangementViewPagerAdapter (FragmentManager ourArrangementFragmentManager, Context context) {

        super (ourArrangementFragmentManager);

        this.pagerAdapterContext = context;

        ourArrangementTabTitleNames = pagerAdapterContext.getResources().getStringArray(R.array.ourArrangementTabTitle);

        fragmentChooser = 0;

        fragArraNow = new OurArrangementFragmentNow();
        fragArraNowComment = new OurArrangementFragmentNowComment();
        fragArraOld = new OurArrangementFragmentOld();

    }


    @Override
    public Fragment getItem(int position) {


        switch (position) {

            case 0:


                switch (fragmentChooser) {


                    case 0:
                        return fragArraNow;

                    case 1:
                        return fragArraNowComment;

                    case 2:
                        //return fragArraShowComment;
                        break;



                }



                /*
                if (fragmentChooser == 0) {
                    fragmentChooser = false;
                    Toast.makeText(pagerAdapterContext, "Fragment Chooser TRUE", Toast.LENGTH_SHORT).show();
                    return fragArraNow;
                } else {
                    fragmentChooser = true;
                    Toast.makeText(pagerAdapterContext, "!!!!!!!Fragment Chooser FASLE", Toast.LENGTH_SHORT).show();
                    return fragArraNowComment;
                }

                //return new OurArrangementFragmentNow();

             */

            case 1:
                return fragArraOld;

            default:
                return new OurArrangementFragmentNow();


        }

    }



    @Override
    public int getItemPosition(Object object)
    {
        /*if (object instanceof FacturasFragment &&
                mFragmentAtPos0 instanceof DetallesFacturaFragment) {
            return POSITION_NONE;
        }
        if (object instanceof DetallesFacturaFragment &&
                mFragmentAtPos0 instanceof FacturasFragment) {
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;*/

        //Toast.makeText(pagerAdapterContext, "POSITION NONE!!!!", Toast.LENGTH_SHORT).show();

        return POSITION_NONE;




    }




    @Override
    public int getCount() {
        return ourArrangementTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return ourArrangementTabTitleNames[position];

    }



    // set variable for switching between fragments for tab 0
    public static void setFragmentTabZero(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "show_arrangement_now": // set fragment on tab zero to OurArrangementFragmentNow
                fragmentChooser = 0;
                break;

            case "comment_an_arrangement": // set fragment on tab zero to OurArrangementFragmentNowComment
                fragmentChooser = 1;
                break;

            case "show_comment_for_arrangement": // set fragment on tab zero to OurArrangementFragmentShowComment
                fragmentChooser = 2;
                break;


        }





    }





}















