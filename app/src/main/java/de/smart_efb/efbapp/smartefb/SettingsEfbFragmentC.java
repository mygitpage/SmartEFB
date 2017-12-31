package de.smart_efb.efbapp.smartefb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ich on 20.06.16.
 */

public class SettingsEfbFragmentC extends Fragment {

    View contentFragmentC;

    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        contentFragmentC = layoutInflater.inflate(R.layout.fragment_settings_efb_c, null);

        // link item link to faq question for app
        TextView textViewConnectedWithServerIntroText = (TextView) contentFragmentC.findViewById(R.id.settingsConnectToServerSucsessfullIntro);
        textViewConnectedWithServerIntroText.setMovementMethod(LinkMovementMethod.getInstance());

        return contentFragmentC;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

    }
}
