package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


/**
 * Created by ich on 26.06.16.
 */
public class OurArrangementViewPagerAdapter extends FragmentStatePagerAdapter {


    // number of tabs
    final static int ourArrangementTabCount = 3;

    // array of tab title
    String ourArrangementTabTitleNames[] = new String[ourArrangementTabCount];

    // calling context
    Context pagerAdapterContext = null;

    // command to choose a fragment for tab zero in getItem
    static int fragmentChooserTabZero;

    // command to choose a fragment for tab one in getItem
    static int fragmentChooserTabOne;


    // the fragments for all tabs
    Fragment fragArraNow, fragArraNowComment, fragArraOld, fragArraShowComment, fragArraEvaluate, fragArraSketch, fragArraSketchComment, fragArraShowSketchComment;

    // Reference to fragment manager
    FragmentManager ourArrangementFragmentManager;


    // default constructor
    public OurArrangementViewPagerAdapter (FragmentManager ourArrangementFragmentMana, Context context) {

        super (ourArrangementFragmentMana);

        this.ourArrangementFragmentManager = ourArrangementFragmentMana;

        this.pagerAdapterContext = context;

        ourArrangementTabTitleNames = pagerAdapterContext.getResources().getStringArray(R.array.ourArrangementTabTitle);

        fragmentChooserTabZero = 0;
        fragmentChooserTabOne = 0;

        fragArraNow = new OurArrangementFragmentNow();
        fragArraNowComment = new OurArrangementFragmentNowComment();
        fragArraOld = new OurArrangementFragmentOld();
        fragArraShowComment = new OurArrangementFragmentShowComment();
        fragArraEvaluate = new OurArrangementFragmentEvaluate();
        fragArraSketch = new OurArrangementFragmentSketch();
        fragArraSketchComment = new OurArrangementFragmentSketchComment();
        fragArraShowSketchComment = new OurArrangementFragmentShowSketchComment();
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:

                switch (fragmentChooserTabZero) {
                    case 0:
                        return fragArraNow;

                    case 1:
                        return fragArraNowComment;

                    case 2:
                        return fragArraShowComment;

                    case 3:
                        return fragArraEvaluate;
                }

            case 1:

                switch (fragmentChooserTabOne) {
                    case 0:
                        return fragArraSketch;

                    case 1:
                        return fragArraSketchComment;

                    case 2:
                        return fragArraShowSketchComment;

                }

            case 2:
                return fragArraOld;

            default:
                return new OurArrangementFragmentNow();

        }
    }


    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;

    }


    @Override
    public int getCount() {
        return ourArrangementTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return ourArrangementTabTitleNames[position];
        }

        return ourArrangementTabTitleNames[position];
    }


    // set variable for switching between fragments for tab 0
    public static void setFragmentTabZero(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "show_arrangement_now": // set fragment on tab zero to OurArrangementFragmentNow
                fragmentChooserTabZero = 0;
                break;

            case "comment_an_arrangement": // set fragment on tab zero to OurArrangementFragmentNowComment
                fragmentChooserTabZero = 1;
                break;

            case "show_comment_for_arrangement": // set fragment on tab zero to OurArrangementFragmentShowComment
                fragmentChooserTabZero = 2;
                break;

            case "evaluate_an_arrangement": // set fragment on tab zero to OurArrangementFragmentEvaluate
                fragmentChooserTabZero = 3;
                break;

        }
    }


    // set variable for switching between fragments for tab 1
    public static void setFragmentTabOne(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "show_sketch_arrangement": // set fragment on tab one to OurArrangementFragmentSketch
                fragmentChooserTabOne = 0;
                break;

            case "comment_an_sketch_arrangement": // set fragment on tab one to OurArrangementFragmentSketchComment
                fragmentChooserTabOne = 1;
                break;

            case "show_comment_for_sketch_arrangement": // set fragment on tab one to OurArrangementFragmentShowSketchComment
                fragmentChooserTabOne = 2;
                break;
        }
    }


}