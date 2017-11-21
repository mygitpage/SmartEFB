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

public class MeetingFragmentMeetingCommentFromClient extends Fragment {


    // fragment view
    View viewFragmentCommentFromCLient;

    // fragment context
    Context fragmentCommentFromClientContext = null;

    // the fragment
    Fragment fragmentThisFragmentContext;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentCommentFromCLient = layoutInflater.inflate(R.layout.fragment_our_arrangement_now_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        //IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        //getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentNowCommentBrodcastReceiver, filter);

        return viewFragmentCommentFromCLient;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentCommentFromClientContext = getActivity().getApplicationContext();

        fragmentThisFragmentContext = this;



    }





}
