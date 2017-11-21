package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ich on 21.11.2017.
 */

public class MeetingFragmentMeetingSuggestionOld extends Fragment {


    // fragment view
    View viewFragmentMeetingSuggestionOld;

    // fragment context
    Context fragmentMeetingSuggestionOldContext = null;

    // the fragment
    Fragment fragmentThisFragmentContext;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentMeetingSuggestionOld = layoutInflater.inflate(R.layout.fragment_our_arrangement_now_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        //IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        //getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentNowCommentBrodcastReceiver, filter);

        return viewFragmentMeetingSuggestionOld;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingSuggestionOldContext = getActivity().getApplicationContext();

        fragmentThisFragmentContext = this;



    }
}
