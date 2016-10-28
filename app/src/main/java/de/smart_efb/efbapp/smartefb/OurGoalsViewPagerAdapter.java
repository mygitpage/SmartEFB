package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by ich on 08.06.16.
 */
public class OurGoalsViewPagerAdapter extends FragmentStatePagerAdapter {

    // number of tabs
    final static int ourGoalsTabCount = 3;

    // array of tab title
    String ourGoalsTabTitleNames[] = new String[ourGoalsTabCount];

    // calling context
    Context pagerAdapterContext = null;

    // command to choose a fragment for zab zero in getItem
    static int fragmentChooserTabZero;

    // command to choose a fragment for zab one in getItem
    static int fragmentChooserTabOne;

    // the fragments for all tabs
    Fragment fragJointlyGoalsNow, fragJointlyGoalsComment, fragJointlyGoalsOld, fragJointlyGoalsShowComment, fragJointlyGoalsEvaluate, fragDebetableGoalsNow, fragDebetableGoalsComment, fragDebetableGoalsShowComment;

    // Reference to fragment manager
    FragmentManager ourGoalsFragmentManager;


    public OurGoalsViewPagerAdapter(FragmentManager ourGoalsFragmentMana, Context context) {

        super (ourGoalsFragmentMana);

        this.ourGoalsFragmentManager = ourGoalsFragmentMana;

        this.pagerAdapterContext = context;

        ourGoalsTabTitleNames = pagerAdapterContext.getResources().getStringArray(R.array.ourGoalsTabTitle);

        fragmentChooserTabZero = 0;
        fragmentChooserTabOne = 0;

        fragJointlyGoalsNow = new OurGoalsFragmentJointlyGoalsNow();
        fragJointlyGoalsComment = new OurGoalsFragmentCommentJointlyGoals();
        fragJointlyGoalsOld = new OurGoalsFragmentJointlyGoalsNow();
        fragJointlyGoalsShowComment = new OurGoalsFragmentShowCommentJointlyGoals();
        fragJointlyGoalsEvaluate = new OurGoalsFragmentJointlyGoalsNow();
        fragDebetableGoalsNow = new OurGoalsFragmentJointlyGoalsNow();
        fragDebetableGoalsComment = new OurGoalsFragmentJointlyGoalsNow();
        fragDebetableGoalsShowComment = new OurGoalsFragmentJointlyGoalsNow();

    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                switch (fragmentChooserTabZero) {
                    case 0:
                        return fragJointlyGoalsNow;
                    case 1:
                        return fragJointlyGoalsComment;
                    case 2:
                        return fragJointlyGoalsShowComment;
                    case 3:
                        return fragJointlyGoalsEvaluate;
                }
            case 1:
                switch (fragmentChooserTabOne) {
                    case 0:
                        return fragDebetableGoalsNow;
                    case 1:
                        return fragDebetableGoalsComment;
                    case 2:
                        return fragDebetableGoalsShowComment;

                }
            case 2:
                return fragJointlyGoalsOld;
            default:
                return new OurArrangementFragmentNow();

        }

    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }


    @Override
    public int getCount() {

        return ourGoalsTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return ourGoalsTabTitleNames[position];

    }


    // set variable for switching between fragments for tab 0
    public static void setFragmentTabZero(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "show_jointly_goals_now": // set fragment on tab zero to OurGoalsFragmentJointlyGoalsNow
                fragmentChooserTabZero = 0;
                break;

            case "comment_an_jointly_goal": // set fragment on tab zero to OurGoalsFragmentJointlyGoalsComment
                fragmentChooserTabZero = 1;
                break;

            case "show_comment_for_jointly_goal": // set fragment on tab zero to OurGoalsFragmentJointlyGoalsShowComment
                fragmentChooserTabZero = 2;
                break;

            case "evaluate_an_jointly_goal": // set fragment on tab zero to OurGoalsFragmentJointlyGoalsEvaluate
                fragmentChooserTabZero = 3;
                break;
        }

    }


    // set variable for switching between fragments for tab 1
    public static void setFragmentTabOne(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "show_debetable_goals_now": // set fragment on tab one to OurGoalsFragmentDebetableGoalsNow
                fragmentChooserTabOne = 0;
                break;

            case "comment_an_debetable_goal": // set fragment on tab one to OurGoalsFragmentDebetableGoalsComment
                fragmentChooserTabOne = 1;
                break;

            case "show_comment_for_debetable_goal": // set fragment on tab one to OurGoalsFragmentShowDebetableGoalsComment
                fragmentChooserTabOne = 2;
                break;

        }

    }


}
