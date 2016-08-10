package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ich on 10.08.16.
 */
public class OurArrangementFragmentEvaluate extends Fragment {


    // fragment view
    View viewFragmentEvaluate;

    // fragment context
    Context fragmentEvaluateContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentEvaluate = layoutInflater.inflate(R.layout.fragment_our_arrangement_evaluate, null);

        return viewFragmentEvaluate;

    }



    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentEvaluateContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentEvaluate();


        // close database-connection
        myDb.close();


    }



    // inits the fragment for use
    private void initFragmentEvaluate() {

        // init the DB
        myDb = new DBAdapter(fragmentEvaluateContext);

        // init the prefs
        prefs = fragmentEvaluateContext.getSharedPreferences("smartEfbSettings", fragmentEvaluateContext.MODE_PRIVATE);



    }




}
