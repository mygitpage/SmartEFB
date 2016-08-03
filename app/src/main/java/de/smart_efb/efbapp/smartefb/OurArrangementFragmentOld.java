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
public class OurArrangementFragmentOld extends Fragment {

    // fragment view
    View viewFragmentOld;

    // fragment context
    Context fragmentOldContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of arrangement -> the other are old
    long currentDateOfArrangement;

    // reference cursorAdapter for the listview
    OurArrangementOldCursorAdapter dataAdapter;





    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentOld = layoutInflater.inflate(R.layout.fragment_our_arrangement_old, null);

        return viewFragmentOld;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentOldContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentOld();

        // show actual arrangement set
        displayOldArrangementSet();

        // close database-connection
        myDb.close();

    }


    // inits the fragment for use
    private void initFragmentOld() {

        // init the DB
        myDb = new DBAdapter(fragmentOldContext);

        // init the prefs
        prefs = fragmentOldContext.getSharedPreferences("smartEfbSettings", fragmentOldContext.MODE_PRIVATE);

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong("currentDateOfArrangement", System.currentTimeMillis());

    }


    public void displayOldArrangementSet () {


        // find the listview
        ListView listView = (ListView) viewFragmentOld.findViewById(R.id.listOurArrangementOld);

        // text to show, when arrangement old function not available or count old arrangement is zero
        String tmpAlternativText = "";

        // show alternativ text? true -> yes, false -> no
        Boolean tmpShowAlternativText = false;


        if (prefs.getBoolean("showOldArrangements", false)) { // Function showOldArrangement is available!!!!

            // get all actual arrangement from DB
            Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentDateOfArrangement,"smaller");

            if (cursor.getCount() > 0) {

                // new dataadapter
                dataAdapter = new OurArrangementOldCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);


            }
            else {
                tmpShowAlternativText = true;
                tmpAlternativText = fragmentOldContext.getResources().getString(fragmentOldContext.getResources().getIdentifier("ourArrangementOldArrangementNothingThere", "string", fragmentOldContext.getPackageName()));
            }

        }
        else { // Function showOldArrangement is not available

            tmpShowAlternativText = true;
            tmpAlternativText = fragmentOldContext.getResources().getString(fragmentOldContext.getResources().getIdentifier("ourArrangementOldArrangementFunctionNotAvailable", "string", fragmentOldContext.getPackageName()));
        }

        // when tmpShowAlternativText -> TRUE show alternativ text
        if (tmpShowAlternativText) {

            LinearLayout oldArrangementHolderLayout = (LinearLayout) viewFragmentOld.findViewById(R.id.listOurArrangementOldHolder);

            // remove listView in Layout
            oldArrangementHolderLayout.removeViewInLayout(listView);

            //add textView for intro text
            TextView txtViewFunctionNotAvailable = new TextView (fragmentOldContext);
            txtViewFunctionNotAvailable.setText(tmpAlternativText);
            txtViewFunctionNotAvailable.setTextColor(ContextCompat.getColor(fragmentOldContext, R.color.text_color));
            txtViewFunctionNotAvailable.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewFunctionNotAvailable.setTextSize(16);
            txtViewFunctionNotAvailable.setGravity(Gravity.LEFT);
            txtViewFunctionNotAvailable.setPadding(15,15,0,0);
            txtViewFunctionNotAvailable.setTypeface(null, Typeface.BOLD);

            // add text view to arrangement holder holder (linear layout in xml-file)
            oldArrangementHolderLayout.addView(txtViewFunctionNotAvailable);

        }

    }

}
