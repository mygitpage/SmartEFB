package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

        // close database-connection
        myDb.close();


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

        Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentDateOfArrangement, "equal");

        // find the listview
        ListView listView = (ListView) viewFragmentNow.findViewById(R.id.listOurArrangementNow);


        if (cursor.getCount() > 0) {

            // new dataadapter
            dataAdapter = new OurArrangementNowCursorAdapter(
                    getActivity(),
                    cursor,
                    0);

            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);



        }
        else {

            LinearLayout nowArrangementHolderLayout = (LinearLayout) viewFragmentNow.findViewById(R.id.listOurArrangementNowHolder);

            String tmpAlternativText = fragmentNowContext.getResources().getString(fragmentNowContext.getResources().getIdentifier("ourArrangementNowArrangementNothingThere", "string", fragmentNowContext.getPackageName()));

            // remove listView in Layout
            nowArrangementHolderLayout.removeViewInLayout(listView);

            //add textView for intro text
            TextView txtViewFunctionNotAvailable = new TextView (fragmentNowContext);
            txtViewFunctionNotAvailable.setText(tmpAlternativText);
            txtViewFunctionNotAvailable.setTextColor(ContextCompat.getColor(fragmentNowContext, R.color.text_color));
            txtViewFunctionNotAvailable.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewFunctionNotAvailable.setTextSize(16);
            txtViewFunctionNotAvailable.setGravity(Gravity.LEFT);
            txtViewFunctionNotAvailable.setPadding(15,15,0,0);
            txtViewFunctionNotAvailable.setTypeface(null, Typeface.BOLD);

            // add text view to arrangement holder holder (linear layout in xml-file)
            nowArrangementHolderLayout.addView(txtViewFunctionNotAvailable);

        }


    }


}
