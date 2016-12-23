package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ich on 20.12.2016.
 */
public class MeetingFragmentMeetingMake extends Fragment {


    // fragment view
    View viewFragmentMeetingMake;

    // fragment context
    Context fragmentMeetingMakeContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current meeting date and time
    long currentMeetingDateAndTime;

    // meeting status
    int meetingStatus;



    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingMake = layoutInflater.inflate(R.layout.fragment_meeting_meeting_make, null);

        Log.d("Meeting","onCreateView");

        return viewFragmentMeetingMake;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingMakeContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentMeetingNow();

        // show actual meeting informations
        displayActualMeetingInformation();

    }



    private void initFragmentMeetingNow () {

        // init the prefs
        prefs = fragmentMeetingMakeContext.getSharedPreferences("smartEfbSettings", fragmentMeetingMakeContext.MODE_PRIVATE);

        // get the current meeting date and time
        currentMeetingDateAndTime = prefs.getLong("meetingDateAndTime", System.currentTimeMillis());

        // get meeting status
        meetingStatus = prefs.getInt("meetingStatus", 0);

        // get meeting place
        meetingStatus = prefs.getInt("meetingPlace", 0);



    }




    private void displayActualMeetingInformation () {

        String txtNextMeetingIntro = "";
        String tmpSubtitle = "";
        String tmpSubtitleOrder = "";

        Button tmpButton;

        Boolean btnVisibilitySendMakeFirstMeeting = false;
        Boolean btnVisibilitySendFindMeetingDate = false;
        Boolean btnVisibilitySendChangeMeetingDate = false;


        switch (meetingStatus) {


            case 0: // no time and date for meeting -> first meeting
                txtNextMeetingIntro  = fragmentMeetingMakeContext.getResources().getString(R.string.nextMeetingIntroTextNoMeeting);
                btnVisibilitySendMakeFirstMeeting = true;

                tmpSubtitle = getResources().getString(getResources().getIdentifier("meetingSubtitleMakeFirstMeeting", "string", fragmentMeetingMakeContext.getPackageName()));
                tmpSubtitleOrder = "makeFirstMeeting";
                //tmpSubtitle = String.format(tmpSubtitle, jointlyGoalNumberInListView);

                // zu testzwecken
                btnVisibilitySendFindMeetingDate = true;
                btnVisibilitySendChangeMeetingDate = true;


                break;

        }

        // Set correct subtitle in Activity Meeting
        ((ActivityMeeting) getActivity()).setMeetingToolbarSubtitle (tmpSubtitle, tmpSubtitleOrder);



        // show actual comment
        //TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingMake.findViewById(R.id.nextMeetingIntroText);
        //textViewNextMeetingIntroText.setText(txtNextMeetingIntro);

        // set visibility of SendMakeFirstMeeting button to visible
        if (btnVisibilitySendMakeFirstMeeting) {
            tmpButton = (Button) viewFragmentMeetingMake.findViewById(R.id.buttonSendSuggestionFirstMeeting);
            tmpButton.setVisibility(View.VISIBLE);


            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(fragmentMeetingMakeContext, ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","now_meeting");
                    fragmentMeetingMakeContext.startActivity(intent);

                }
            });





        }

        // set visibility of SendFindMeetingDate button to visible
        if (btnVisibilitySendFindMeetingDate) {
            tmpButton = (Button) viewFragmentMeetingMake.findViewById(R.id.buttonAbbortSuggestionFirstMeeting);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(fragmentMeetingMakeContext, ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","now_meeting");
                    fragmentMeetingMakeContext.startActivity(intent);

                }
            });
        }




    }




}