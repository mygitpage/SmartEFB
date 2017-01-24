package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ich on 12.08.16.
 */
public class FaqFragmentSection2 extends Fragment {

    // fragment view
    View viewFragmentSection2;


    // fragment context
    Context fragmentFaqSectionTwoContext = null;




    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSection2 = layoutInflater.inflate(R.layout.fragment_faq_section_2, null);

        return viewFragmentSection2;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentFaqSectionTwoContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentFaqSectionTwo();

        // show actual faq section one
        displayActualFaqSectionTwo();




    }



    // init fragment
    private void initFragmentFaqSectionTwo () {

    }




    // show fragment ressources
    private void displayActualFaqSectionTwo () {

    }


}
