package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ich on 12.08.16.
 */
public class FaqFragmentSection1 extends Fragment {

    // fragment view
    View viewFragmentSection1;


    // fragment context
    Context fragmentFaqSectionOneContext = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSection1 = layoutInflater.inflate(R.layout.fragment_faq_section_1, null);

        return viewFragmentSection1;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentFaqSectionOneContext = getActivity().getApplicationContext();

        // init the fragment meeting now
        initFragmentFaqSectionOne();

        // show actual faq section one
        displayActualFaqSectionOne();



    }


    // init fragment
    private void initFragmentFaqSectionOne () {

    }




    // show fragment ressources
    private void displayActualFaqSectionOne () {

        // set movement methode info section two
        TextView tmpShowOverviewSectionTwoTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionTwoTitle);
        tmpShowOverviewSectionTwoTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info section three
        TextView tmpShowOverviewSectionThreeTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionThreeTitle);
        tmpShowOverviewSectionThreeTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info section four
        TextView tmpShowOverviewSectionFourTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionFourTitle);
        tmpShowOverviewSectionFourTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info section five
        TextView tmpShowOverviewSectionFiveTitle = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionFiveTitle);
        tmpShowOverviewSectionFiveTitle.setMovementMethod(LinkMovementMethod.getInstance());

        // set movement methode info text missing faq
        TextView tmpShowMissingFaq = (TextView) viewFragmentSection1.findViewById(R.id.faqOverviewSectionOneEnd);
        tmpShowMissingFaq.setMovementMethod(LinkMovementMethod.getInstance());



    }






    }
