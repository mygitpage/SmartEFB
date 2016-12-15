package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    // the current meeting date and time
    long currentMeetingDateAndTime;

    // meeting status
    int meetingStatus;



    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentMeetingNow = layoutInflater.inflate(R.layout.fragment_meeting_meeting_now, null);

        Log.d("Meeting","onCreateView");

        return viewFragmentMeetingNow;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentMeetingNowContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentMeetingNow();

        Log.d("Meeting","onViewCreated");

        // show actual meeting informations
        displayActualMeetingInformation();

    }



    private void initFragmentMeetingNow () {

        // init the prefs
        prefs = fragmentMeetingNowContext.getSharedPreferences("smartEfbSettings", fragmentMeetingNowContext.MODE_PRIVATE);

        // get the current meeting date and time
        currentMeetingDateAndTime = prefs.getLong("meetingDateAndTime", System.currentTimeMillis());

        // get meeting status
        meetingStatus = prefs.getInt("meetingStatus", 0);

        // get meeting place
        meetingStatus = prefs.getInt("meetingPlace", 0);



    }




    private void displayActualMeetingInformation () {

        String txtNextMeetingIntro = "";

        Button tmpButton;

        Boolean btnVisibilitySendMakeFirstMeeting = false;
        Boolean btnVisibilitySendFindMeetingDate = false;
        Boolean btnVisibilitySendChangeMeetingDate = false;


        switch (meetingStatus) {


            case 0: // no time and date for meeting -> first meeting
                    txtNextMeetingIntro  = fragmentMeetingNowContext.getResources().getString(R.string.nextMeetingIntroTextNoMeeting);
                    btnVisibilitySendMakeFirstMeeting = true;
                    break;

        }





        // show actual comment
        TextView textViewNextMeetingIntroText = (TextView) viewFragmentMeetingNow.findViewById(R.id.nextMeetingIntroText);
        textViewNextMeetingIntroText.setText(txtNextMeetingIntro);

        // set visibility of SendMakeFirstMeeting button to visible
        if (btnVisibilitySendMakeFirstMeeting) {
            tmpButton = (Button) viewFragmentMeetingNow.findViewById(R.id.buttonSendMakeFirstMeeting);
            tmpButton.setVisibility(View.VISIBLE);


            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(fragmentMeetingNowContext, ActivityMeeting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","make_meeting");
                    fragmentMeetingNowContext.startActivity(intent);

                }
            });





        }

        // set visibility of SendFindMeetingDate button to visible
        if (btnVisibilitySendFindMeetingDate) {
            tmpButton = (Button) viewFragmentMeetingNow.findViewById(R.id.buttonSendFindMeetingDate);
            tmpButton.setVisibility(View.VISIBLE);
        }

        // set visibility of SendFindMeetingDate button to visible
        if (btnVisibilitySendChangeMeetingDate) {
            tmpButton = (Button) viewFragmentMeetingNow.findViewById(R.id.buttonSendChangeMeetingDate);
            tmpButton.setVisibility(View.VISIBLE);
        }



    }




}
