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
import android.widget.TextView;

/**
 * Created by ich on 30.11.2016.
 */
public class OurGoalsFragmentJointlyOld extends Fragment {

    // fragment view
    View viewFragmentJointlyOld;

    // fragment context
    Context fragmentJointlyOldContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of jointly goals -> the other are old
    long currentDateOfJointlyGoal;

    // block id of current jointly goals
    String currentBlockIdOfJointlyGoals = "";

    // reference cursorAdapter for the listview
    OurGoalsJointlyOldCursorAdapter dataAdapter;

    
    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentJointlyOld = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_old, null);

        return viewFragmentJointlyOld;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentJointlyOldContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentJointlyOld();

        // show actual old jointly goal set
        displayOldJointlyGoalSet();


    }


    // inits the fragment for use
    private void initFragmentJointlyOld() {

        // init the DB
        myDb = new DBAdapter(fragmentJointlyOldContext);

        // init the prefs
        prefs = fragmentJointlyOldContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentJointlyOldContext.MODE_PRIVATE);

        //get current date of jointly goals
        currentDateOfJointlyGoal = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        //get current block id of jointly goals
        currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

    }


    public void displayOldJointlyGoalSet () {

        String tmpSubtitle;

        // find the listview
        ListView listView = (ListView) viewFragmentJointlyOld.findViewById(R.id.listOurGoalsJointlyOld);

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, false) && listView != null) { // Function showOldJointlyGoals is available!!!!


            // get all old jointly goals from DB
            Cursor cursor = myDb.getAllJointlyRowsOurGoals(currentBlockIdOfJointlyGoals,"notEqualBlockId");

            if (cursor.getCount() > 0) {

                // show the list view for the old jointly goals, hide textview "Function not available" and "nothing there"
                setVisibilityListViewOldJointlyGoals ("show");
                setVisibilityTextViewNothingThere ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");

                // Set correct subtitle in Activity -> "aelter als..."
                tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleOldJointlyGoals", "string", fragmentJointlyOldContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfJointlyGoal, "dd.MM.yyyy");
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");

                // new dataadapter
                dataAdapter = new OurGoalsJointlyOldCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);

            }
            else {

                // hide the list view for the old jointly goals, hide textview "Function not available", show textview "nothing there"
                setVisibilityListViewOldJointlyGoals ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");
                setVisibilityTextViewNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Ziele vorhanden"
                tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleGoalsNothingThere", "string", fragmentJointlyOldContext.getPackageName()));
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");
            }

        }
        else { // Function showOldJointlyGoals is not available

            // show the list view for the old jointly goals, show textview "Function not available" and hide "nothing there"
            setVisibilityListViewOldJointlyGoals ("hide");
            setVisibilityTextViewNothingThere ("hide");
            setVisibilityTextViewFunctionNotAvailable ("show");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentJointlyOldContext.getPackageName()));
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");

        }

    }



    private void setVisibilityListViewOldJointlyGoals (String visibility) {

        ListView tmplistView = (ListView) viewFragmentJointlyOld.findViewById(R.id.listOurGoalsJointlyOld);

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

        TextView tmpNotAvailable = (TextView) viewFragmentJointlyOld.findViewById(R.id.textViewOldJointlyGoalsFunctionNotAvailable);

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

        TextView tmpNothingThere = (TextView) viewFragmentJointlyOld.findViewById(R.id.textViewOldJointlyGoalsNothingThere);

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
