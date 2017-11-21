package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ich on 21.11.2017.
 */

public class MeetingViewPagerAdapter extends FragmentStatePagerAdapter {





    // number of tabs
    final static int metingTabCount = 3;

    // array of tab title
    String meetingTabTitleNames[] = new String[metingTabCount];

    // calling context
    Context pagerAdapterContext = null;

    // command to choose a fragment for tab zero in getItem
    static int fragmentChooserTabZero;
    static int fragmentChooserTabOne;

    // the fragments for all tabs
    Fragment fragMeeting, fragSuggestion, fragSuggestionFromClient, fragMeetingCommentFromClient, fragMeetingSuggestionOld;

    // Reference to fragment manager
    FragmentManager meetingFragmentManager;


    // default constructor
    public MeetingViewPagerAdapter (FragmentManager meetingFragmentManager, Context context) {

        super (meetingFragmentManager);

        this.meetingFragmentManager = meetingFragmentManager;

        this.pagerAdapterContext = context;

        meetingTabTitleNames = pagerAdapterContext.getResources().getStringArray(R.array.meetingTabTitle);

        fragmentChooserTabZero = 0;
        fragmentChooserTabOne = 0;

        fragMeeting = new MeetingFragmentMeetingOverview();
        fragSuggestion = new MeetingFragmentSuggestionOverview();
        fragSuggestionFromClient = new MeetingFragmentSuggestionFromClient();
        fragMeetingCommentFromClient = new MeetingFragmentMeetingCommentFromClient();
        fragMeetingSuggestionOld = new MeetingFragmentMeetingSuggestionOld();

    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:

                switch (fragmentChooserTabZero) {
                    case 0:
                        return fragMeeting;

                    case 1:
                        return fragMeetingCommentFromClient;

                }

            case 1:

                switch (fragmentChooserTabOne) {
                    case 0:
                        return fragSuggestion;

                    case 1:
                        return fragSuggestionFromClient;

                }

            case 2:

                return fragMeetingSuggestionOld;


            default:
                return fragMeeting;

        }
    }



    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return metingTabCount;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return meetingTabTitleNames[position];
    }


    // set variable for switching between fragments for tab 0
    public static void setFragmentTabZero(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "meeting_overview": // set fragment on tab zero to MeetingOverview
                fragmentChooserTabZero = 0;
                break;

            case "meeting_comment_from_client": // set fragment on tab zero to MeetingCommentFromClient
                fragmentChooserTabZero = 1;
                break;
        }

    }



    // set variable for switching between fragments for tab 1
    public static void setFragmentTabOne(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "suggestion_overview": // set fragment on tab one to SuggestionOverview
                fragmentChooserTabZero = 0;
                break;

            case "suggestion_from_client": // set fragment on tab zero to SuggestionFromClient
                fragmentChooserTabZero = 1;
                break;
        }

    }



}
