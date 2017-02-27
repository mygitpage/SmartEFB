package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 09.09.16.
 */
public class OurArrangementFragmentSketch  extends Fragment {

    // fragment view
    View viewFragmentSketch;

    // fragment context
    Context fragmentSketchContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the date of sketch arrangement
    long currentDateOfSketchArrangement;

    // reference cursorAdapter for the listview
    OurArrangementSketchCursorAdapter dataAdapterListViewOurArrangementSketch = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSketch = layoutInflater.inflate(R.layout.fragment_our_arrangement_sketch, null);

        return viewFragmentSketch;


    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentSketchContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentSketch();

        // show actual arrangement set
        displaySketchArrangementSet();


    }


    // inits the fragment for use
    private void initFragmentSketch() {

        // init the DB
        myDb = new DBAdapter(fragmentSketchContext);

        // init the prefs
        prefs = fragmentSketchContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentSketchContext.MODE_PRIVATE);

        //get date of sketch arrangement
        currentDateOfSketchArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis());

    }


    // show listView with sketch arrangements or info: nothing there
    public void displaySketchArrangementSet () {

        // find the listview
        ListView listView = (ListView) viewFragmentSketch.findViewById(R.id.listOurArrangementSketch);

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false) && listView != null) { // Function showSketchArrangement is available!!!!

            // get the data from db -> all sketch arrangements
            Cursor cursor = myDb.getAllRowsSketchOurArrangement(currentDateOfSketchArrangement);

            if (cursor.getCount() > 0 && listView != null) {

                // set listView visible, textView nothing there and not available gone
                setVisibilityListViewSketchArrangements("show");
                setVisibilityTextViewSketchNotAvailable("hide");
                setVisibilityTextViewSketchNothingThere ("hide");

                // Set correct subtitle in Activity -> "Entwuerfe vom ..."
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("sketchArrangementsubtitle", "string", fragmentSketchContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfSketchArrangement, "dd.MM.yyyy");
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");

                // new dataadapter
                dataAdapterListViewOurArrangementSketch = new OurArrangementSketchCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapterListViewOurArrangementSketch);

            } else {

                // set listView and textView not available gone, set textView nothing there visible
                setVisibilityListViewSketchArrangements("hide");
                setVisibilityTextViewSketchNotAvailable("hide");
                setVisibilityTextViewSketchNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleSketchNothingThere", "string", fragmentSketchContext.getPackageName()));
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");
            }

        }
        else {

            // set listView and textView nothing there gone, set textView not available visible
            setVisibilityListViewSketchArrangements("hide");
            setVisibilityTextViewSketchNotAvailable("show");
            setVisibilityTextViewSketchNothingThere ("hide");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentSketchContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");

        }
    }


    // set visibility of listViewOurArrangement
    private void setVisibilityListViewSketchArrangements (String visibility) {

        ListView tmplistView = (ListView) viewFragmentSketch.findViewById(R.id.listOurArrangementSketch);

        if (tmplistView != null) {

            switch (visibility) {

                case "show":
                    tmplistView.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmplistView.setVisibility(View.GONE);
                    break;

            }

        }

    }


    // set visibility of textView "nothing there"
    private void setVisibilityTextViewSketchNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentSketch.findViewById(R.id.textViewArrangementSketchFunctionNotAvailable);

        if (tmpNotAvailable != null) {

            switch (visibility) {

                case "show":
                    tmpNotAvailable.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmpNotAvailable.setVisibility(View.GONE);
                    break;

            }

        }

    }


    private void setVisibilityTextViewSketchNothingThere (String visibility) {

        TextView tmpNothingThere = (TextView) viewFragmentSketch.findViewById(R.id.textViewArrangementSketchNothingThere);

        if (tmpNothingThere != null) {

            switch (visibility) {

                case "show":
                    tmpNothingThere.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmpNothingThere.setVisibility(View.GONE);
                    break;

            }

        }

    }



}
