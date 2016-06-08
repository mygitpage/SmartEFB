package de.smart_efb.efbapp.smartefb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ich on 08.06.16.
 */

public class OurGoalsFragmentA extends Fragment {


    View contentFragmentA;

    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        contentFragmentA = layoutInflater.inflate(R.layout.fragment_our_goals_a, null);

        return contentFragmentA;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);



    }



}
