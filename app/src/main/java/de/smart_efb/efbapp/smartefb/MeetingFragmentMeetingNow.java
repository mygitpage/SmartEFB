package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ich on 12.12.2016.
 */
public class MeetingFragmentMeetingNow extends Fragment {


    // fragment view
    View viewFragmentMeetingNow;

    // fragment context
    Context fragmentMeetingNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;



    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingNow = layoutInflater.inflate(R.layout.fragment_meeting_meeting_now, null);

        return viewFragmentMeetingNow;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingNowContext = getActivity().getApplicationContext();

        // init the fragment jointly goals now
        //initFragmentJointlyGoalsNow();

        // show actual jointly goals set
        //displayActualJointlyGoalsSet();

    }

}
