package de.smart_efb.efbapp.smartefb;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentA extends Fragment {

    // fragment view
    View viewFragmentConnectToServer;

     // fragment context
    Context fragmentConnectToServerContext = null;

    // minimum and maximum for random number to connect to server
    static int randomNumverForConnectionMin = 10000;
    static int randomNumverForConnectionMax = 99999;

    // the connecting status (0=not connected, 1=try to connect, 2=connected)
    int connectingStatus = 0;

    // actual random number for connetion to server
    int randomNumverForConnection = 0;





    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentConnectToServer = layoutInflater.inflate(R.layout.fragment_settings_efb_a, null);

        return viewFragmentConnectToServer;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentConnectToServerContext = getActivity().getApplicationContext();

        // init the fragment connect to server
        initFragmentConnectToServer();

        // show actual connecting informations
        displayActualConnectingInformation();


    }


    private void initFragmentConnectToServer () {

        // call getter-methode getMeetingTimeAndDate in ActivityMeeting to get meeting status
        connectingStatus = ((ActivitySettingsEfb)getActivity()).getConnectingStatus();

    }


    // show actual process data of connecting to server
    private void displayActualConnectingInformation () {

        // connecting status 0 -> not connected to server
        if (connectingStatus == 0) {



            // show connecting intro text
            TextView textViewConnectToServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerIntro);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);

            // generate and show random number for connecting
            randomNumverForConnection = EfbHelperClass.randomNumber(randomNumverForConnectionMin, randomNumverForConnectionMax);

            TextView textViewRandomNumberText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingConnectToServerKeyNumber);
            textViewRandomNumberText.setVisibility(View.VISIBLE);
            textViewRandomNumberText.setText(""+randomNumverForConnection);

            // show remark for clicking button
            TextView textViewConnectToServerRemarkClickButtonText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsConnectToServerRemarkClickButton);
            textViewConnectToServerRemarkClickButtonText.setVisibility(View.VISIBLE);

            Button tmpButton = (Button) viewFragmentConnectToServer.findViewById(R.id.buttonSendConnectToServerKeyNumber);
            tmpButton.setVisibility(View.VISIBLE);

            // onClick listener make meeting
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // call setter-methode setRandomNumberForConnection in ActivitySettingsEfb to set random number
                    ((ActivitySettingsEfb)getActivity()).setRandomNumberForConnection(randomNumverForConnection);

                    // call setter-methode setConnectionStatus in ActivitySettingsEfb to set connection status waiting for response
                    ((ActivitySettingsEfb)getActivity()).setConnectionStatus(1);
                    connectingStatus = 1;

                    // show toast number succsessfull send
                    Toast.makeText(fragmentConnectToServerContext, fragmentConnectToServerContext.getResources().getString(R.string.sendConnectToServerKeyNumberSuccsesfulyText), Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(fragmentConnectToServerContext, ActivitySettingsEfb.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com","show_waiting_response");
                    fragmentConnectToServerContext.startActivity(intent);

                }
            });






        }


        // connecting status 1 -> waiting for response from server
        if (connectingStatus == 1) {

            // show waiting for response intro text
            TextView textViewConnectToServerIntroText = (TextView) viewFragmentConnectToServer.findViewById(R.id.settingsWaitingForResponseIntro);
            textViewConnectToServerIntroText.setVisibility(View.VISIBLE);

        }





    }




}




