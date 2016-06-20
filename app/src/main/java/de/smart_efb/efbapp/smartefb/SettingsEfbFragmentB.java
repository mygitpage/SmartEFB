package de.smart_efb.efbapp.smartefb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ich on 20.06.16.
 */


public class SettingsEfbFragmentB extends Fragment {


    View contentFragmentB;

    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        contentFragmentB = layoutInflater.inflate(R.layout.fragment_settings_efb_b, null);

        return contentFragmentB;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);



    }



}