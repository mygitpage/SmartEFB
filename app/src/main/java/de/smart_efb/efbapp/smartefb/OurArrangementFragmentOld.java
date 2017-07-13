package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    // block id of cuurrent arrangements
    String currentBlockIdOfArrangement = "";

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


    }


    // inits the fragment for use
    private void initFragmentOld() {

        // init the DB
        myDb = new DBAdapter(fragmentOldContext);

        // init the prefs
        prefs = fragmentOldContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentOldContext.MODE_PRIVATE);

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        //get current block id of arrangements
        currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");


    }


    public void displayOldArrangementSet () {

        String tmpSubtitle;

        // find the listview
        ListView listView = (ListView) viewFragmentOld.findViewById(R.id.listOurArrangementOld);

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false) && listView != null) { // Function showOldArrangement is available!!!!


            Log.d("Old Arr","Hier dirn!!!!!!!!!!!!");

            // get all old arrangement from DB
            Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentBlockIdOfArrangement,"notEqualBlockId");

            if (cursor.getCount() > 0) {

                // show the list view for the old arrangements, hide textview "Function not available" and "nothing there"
                setVisibilityListViewOldArrangements ("show");
                setVisibilityTextViewNothingThere ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");

                // Set correct subtitle in Activity -> "Absprachen aelter als..."
                tmpSubtitle = getResources().getString(getResources().getIdentifier("olderArrangementDateFrom", "string", fragmentOldContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");

                // new dataadapter
                dataAdapter = new OurArrangementOldCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);

            }
            else {

                // hide the list view for the old arrangements, hide textview "Function not available", show textview "nothing there"
                setVisibilityListViewOldArrangements ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");
                setVisibilityTextViewNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
                tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNothingThere", "string", fragmentOldContext.getPackageName()));
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");
            }

        }
        else { // Function showOldArrangement is not available

            // show the list view for the old arrangements, show textview "Function not available" and hide "nothing there"
            setVisibilityListViewOldArrangements ("hide");
            setVisibilityTextViewNothingThere ("hide");
            setVisibilityTextViewFunctionNotAvailable ("show");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentOldContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");

        }

    }



    private void setVisibilityListViewOldArrangements (String visibility) {

        ListView tmplistView = (ListView) viewFragmentOld.findViewById(R.id.listOurArrangementOld);

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



    private void setVisibilityTextViewFunctionNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentOld.findViewById(R.id.textViewOldArrangementFunctionNotAvailable);

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


    private void setVisibilityTextViewNothingThere (String visibility) {

        TextView tmpNothingThere = (TextView) viewFragmentOld.findViewById(R.id.textViewOldArrangementNothingThere);

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
