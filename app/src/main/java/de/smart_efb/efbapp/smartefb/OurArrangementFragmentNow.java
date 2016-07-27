package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by ich on 26.06.16.
 */
public class OurArrangementFragmentNow extends Fragment {

    // fragment view
    View viewFragmentNow;

    // fragment context
    Context fragmentNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // reference cursorAdapter for the listview
    OurArrangementNowCursorAdapter dataAdapter;



    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentNow = layoutInflater.inflate(R.layout.fragment_our_arrangement_now, null);

        return viewFragmentNow;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentNowContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentNow();

        // show actual arrangement set
        displayActualArrangementSet();


    }



    // inits the fragment for use
    private void initFragmentNow() {

        // init the DB
        myDb = new DBAdapter(fragmentNowContext);

        // init the prefs
        prefs = fragmentNowContext.getSharedPreferences("smartEfbSettings", fragmentNowContext.MODE_PRIVATE);
        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong("currentDateOfArrangement", System.currentTimeMillis());


    }



    public void displayActualArrangementSet () {

        Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentDateOfArrangement);

        // find the listview
        ListView listView = (ListView) viewFragmentNow.findViewById(R.id.listOurArrangementNow);


        // new dataadapter
        dataAdapter = new OurArrangementNowCursorAdapter(
                getActivity(),
                cursor,
                0);

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


    }




}
