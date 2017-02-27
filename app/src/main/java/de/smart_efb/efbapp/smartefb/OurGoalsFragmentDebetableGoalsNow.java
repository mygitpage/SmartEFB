package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ich on 12.11.2016.
 */
public class OurGoalsFragmentDebetableGoalsNow extends Fragment {

    // fragment view
    View viewFragmentDebetablGoalNow;

    // fragment context
    Context fragmentDebetableGoalNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the date of sketch arrangement
    long currentDateOfDebetableGoal;

    // reference cursorAdapter for the listview
    OurGoalsDebetableGoalsNowCursorAdapter dataAdapterListViewOurGoalsDebetableGoalsNow = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentDebetablGoalNow = layoutInflater.inflate(R.layout.fragment_our_goals_debetable_goals_now, null);

        return viewFragmentDebetablGoalNow;


    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentDebetableGoalNowContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentDebetableGoalNow();

        // show actual debetable goal set
        displayDebetableGoalsSet();


    }


    // inits the fragment for use
    private void initFragmentDebetableGoalNow() {

        // init the DB
        myDb = new DBAdapter(fragmentDebetableGoalNowContext);

        // init the prefs
        prefs = fragmentDebetableGoalNowContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentDebetableGoalNowContext.MODE_PRIVATE);

        //get date of debetable goals
        currentDateOfDebetableGoal = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis());

        Log.d("OurGoalsDebet","Date Time: "+currentDateOfDebetableGoal+" +++++++++");


    }


    // show listView with debetable goals or info: nothing there
    public void displayDebetableGoalsSet () {

        // find the listview
        ListView listView = (ListView) viewFragmentDebetablGoalNow.findViewById(R.id.listOurGoalsDebetableGoalsNow);

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false) && listView != null) { // Function showDebetableGoals is available!!!!

            // get the data from db -> all debetable goals
            Cursor cursor = myDb.getAllDebetableRowsOurGoals(currentDateOfDebetableGoal);

            Log.d("DebetableNOW","CurLae:"+cursor.getCount());


            if (cursor.getCount() > 0 && listView != null) {

                Log.d("FragmentDebetable >0","Date:"+currentDateOfDebetableGoal);

                // set listView visible, textView nothing there and not available gone
                setVisibilityListViewDebetableGoalsNow("show");
                setVisibilityTextViewDebetableGoalsNowNotAvailable("hide");
                setVisibilityTextViewDebetableGoalsNowNothingThere ("hide");

                // Set correct subtitle in Activity -> "Strittige Ziele vom ..."
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleDebetableGoalsNow", "string", fragmentDebetableGoalNowContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfDebetableGoal, "dd.MM.yyyy");
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableNow");

                // new dataadapter
                dataAdapterListViewOurGoalsDebetableGoalsNow = new OurGoalsDebetableGoalsNowCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapterListViewOurGoalsDebetableGoalsNow);

            } else {

                Log.d("FragmentDebetable--","Date:"+currentDateOfDebetableGoal);

                // set listView and textView not available gone, set textView nothing there visible
                setVisibilityListViewDebetableGoalsNow("hide");
                setVisibilityTextViewDebetableGoalsNowNotAvailable("hide");
                setVisibilityTextViewDebetableGoalsNowNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Ziele vorhanden"
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleGoalsNothingThere", "string", fragmentDebetableGoalNowContext.getPackageName()));
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableNow");
            }

        }
        else {

            Log.d("FragmentDebetable","Date:"+currentDateOfDebetableGoal);

            // set listView and textView nothing there gone, set textView not available visible
            setVisibilityListViewDebetableGoalsNow("hide");
            setVisibilityTextViewDebetableGoalsNowNotAvailable("show");
            setVisibilityTextViewDebetableGoalsNowNothingThere ("hide");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleFunctionNotAvailable", "string", fragmentDebetableGoalNowContext.getPackageName()));
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableNow");

        }
    }


    // set visibility of listViewOurArrangement
    private void setVisibilityListViewDebetableGoalsNow (String visibility) {

        ListView tmplistView = (ListView) viewFragmentDebetablGoalNow.findViewById(R.id.listOurGoalsDebetableGoalsNow);

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
    private void setVisibilityTextViewDebetableGoalsNowNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentDebetablGoalNow.findViewById(R.id.textViewOurGoalsFunctionNotAvailable);

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


    private void setVisibilityTextViewDebetableGoalsNowNothingThere (String visibility) {

        TextView tmpNothingThere = (TextView) viewFragmentDebetablGoalNow.findViewById(R.id.textViewOurGoalsNothingThere);

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
