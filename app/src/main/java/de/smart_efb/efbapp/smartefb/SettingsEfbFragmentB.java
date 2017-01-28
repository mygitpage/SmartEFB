package de.smart_efb.efbapp.smartefb;

import android.content.Context;
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




    // fragment view
    View viewFragmentContactdetails;

    // fragment context
    Context fragmentContactdetailsContext = null;




    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentContactdetails = layoutInflater.inflate(R.layout.fragment_settings_efb_b, null);

        return viewFragmentContactdetails;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);


        fragmentContactdetailsContext = getActivity().getApplicationContext();

        // init the fragment contactdetails
        initFragmentContactdetails();

    }

    private void initFragmentContactdetails () {



    }







}