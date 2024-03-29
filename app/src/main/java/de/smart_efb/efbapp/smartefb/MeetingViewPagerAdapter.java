package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by ich on 21.11.2017.
 */

public class MeetingViewPagerAdapter extends FragmentStatePagerAdapter {

    // number of tabs
    final static int metingTabCount = 4;

    // array of tab title
    String[] meetingTabTitleNames = new String[metingTabCount];

    // calling context
    Context pagerAdapterContext = null;

    // command to choose a fragment for tab zero in getItem
    static int fragmentChooserTabZero;
    static int fragmentChooserTabOne;
    static int fragmentChooserTabTwo;

    // the fragments for all tabs
    Fragment fragMeeting, fragSuggestion, fragSuggestionFromClient, fragMeetingSuggestionOld, fragmentMeetingCanceledFromClient;

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
        fragmentChooserTabTwo = 0;

        fragMeeting = new MeetingFragmentMeetingOverview();
        fragSuggestion = new MeetingFragmentSuggestionOverview();
        fragSuggestionFromClient = new MeetingFragmentSuggestionFromClient();
        fragMeetingSuggestionOld = new MeetingFragmentMeetingSuggestionOld();
        fragmentMeetingCanceledFromClient = new MeetingFragmentMeetingClientCanceled();
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:

                switch (fragmentChooserTabZero) {
                    case 0:
                        return fragMeeting;

                    case 1:
                        return fragmentMeetingCanceledFromClient;

                }

            case 1:

                return fragSuggestionFromClient;

            case 2:

                return fragSuggestion;

            case 3:

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

            case "meeting_client_canceled": // set fragment on tab zero to MeetingClientCanceled
                fragmentChooserTabZero = 1;
                break;
        }

    }



    // set variable for switching between fragments for tab 1 -> function is simple -> can be delete
    public static void setFragmentTabOne(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "suggestion_from_client": // set fragment on tab one to SuggestionFromClient
                fragmentChooserTabOne = 0;
                break;
        }

    }


    // set variable for switching between fragments for tab 2 -> function is simple -> can be delete
    public static void setFragmentTabTwo(String subFragmentCommand) {

        switch (subFragmentCommand) {

            case "suggestion_overview": // set fragment on tab one to SuggestionOverview
                fragmentChooserTabTwo = 0;
                break;
        }

    }



}
